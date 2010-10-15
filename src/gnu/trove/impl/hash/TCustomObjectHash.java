///////////////////////////////////////////////////////////////////////////////
// Copyright (c) 2001, Eric D. Friedman All Rights Reserved.
// Copyright (c) 2009, Rob Eden All Rights Reserved.
// Copyright (c) 2009, Jeff Randall All Rights Reserved.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
///////////////////////////////////////////////////////////////////////////////

package gnu.trove.impl.hash;

import gnu.trove.strategy.HashingStrategy;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;


/**
 * An open addressed hashing implementation for Object types.
 *
 * @author Rob Eden
 * @author Eric D. Friedman
 * @author Jeff Randall
 * @version $Id: TObjectHash.java,v 1.1.2.6 2009/11/07 03:36:44 robeden Exp $
 */
@SuppressWarnings( { "UnusedDeclaration" } )
abstract public class TCustomObjectHash<T> extends TObjectHash<T> {
	protected HashingStrategy<T> strategy;


	/** FOR EXTERNALIZATION ONLY!!! */
	public TCustomObjectHash() {}

	
    /**
     * Creates a new <code>TManualObjectHash</code> instance with the
     * default capacity and load factor.
     */
    public TCustomObjectHash( HashingStrategy<T> strategy ) {
        super();

		this.strategy = strategy;
    }


    /**
     * Creates a new <code>TManualObjectHash</code> instance whose capacity
     * is the next highest prime above <tt>initialCapacity + 1</tt>
     * unless that value is already prime.
     *
     * @param initialCapacity an <code>int</code> value
     */
    public TCustomObjectHash( HashingStrategy<T> strategy, int initialCapacity ) {
        super( initialCapacity );

		this.strategy = strategy;
    }


    /**
     * Creates a new <code>TManualObjectHash</code> instance with a prime
     * value at or near the specified capacity and load factor.
     *
     * @param initialCapacity used to find a prime capacity for the table.
     * @param loadFactor      used to calculate the threshold over which
     *                        rehashing takes place.
     */
    public TCustomObjectHash( HashingStrategy<T> strategy, int initialCapacity,
		float loadFactor ) {

        super( initialCapacity, loadFactor );

		this.strategy = strategy;
    }


    /**
     * Locates the index of <tt>obj</tt>.
     *
     * @param obj an <code>Object</code> value
     * @return the index of <tt>obj</tt> or -1 if it isn't in the set.
     */
    protected int index( Object obj ) {
        final Object[] set = _set;
        final int length = set.length;
        @SuppressWarnings( { "unchecked" } )
        final int hash = strategy.computeHashCode( ( T ) obj ) & 0x7fffffff;
        int index = hash % length;
        Object cur = set[index];

        if ( cur == obj || strategy.equals( ( T ) cur, ( T ) obj ) ) {
            return index;
        }

        if ( cur == FREE ) {
            return -1;
        }

        // NOTE: here it has to be REMOVED or FULL (some user-given value)
	    //noinspection unchecked
	    if ( cur == REMOVED || !strategy.equals( ( T ) cur, ( T ) obj ) ) {
            // see Knuth, p. 529
            final int probe = 1 + ( hash % ( length - 2 ) );

		    //noinspection unchecked
		    do {
                index -= probe;
                if ( index < 0 ) {
                    index += length;
                }
                cur = set[index];
            } while ( cur != FREE
                      && ( cur == REMOVED || !strategy.equals( ( T ) cur, ( T ) obj ) ) );
        }

        return cur == FREE ? -1 : index;
    }


    /**
     * Locates the index at which <tt>obj</tt> can be inserted.  if
     * there is already a value equal()ing <tt>obj</tt> in the set,
     * returns that value's index as <tt>-index - 1</tt>.
     *
     * @param obj an <code>Object</code> value
     * @return the index of a FREE slot at which obj can be inserted
     *         or, if obj is already stored in the hash, the negative value of
     *         that index, minus 1: -index -1.
     */
    protected int insertionIndex( T obj ) {
        final Object[] set = _set;
        final int length = set.length;
        final int hash = strategy.computeHashCode( obj ) & 0x7fffffff;
        int index = hash % length;
        Object cur = set[index];

        if ( cur == FREE ) {
            return index;       // empty, all done
        } else //noinspection unchecked
	        if ( cur == obj ||
			( cur != REMOVED && strategy.equals( ( T ) cur, obj ) ) ) {

            return -index - 1;   // already stored
        } else {                // already FULL or REMOVED, must probe
            // compute the double hash
            final int probe = 1 + ( hash % ( length - 2 ) );

            // if the slot we landed on is FULL (but not removed), probe
            // until we find an empty slot, a REMOVED slot, or an element
            // equal to the one we are trying to insert.
            // finding an empty slot means that the value is not present
            // and that we should use that slot as the insertion point;
            // finding a REMOVED slot means that we need to keep searching,
            // however we want to remember the offset of that REMOVED slot
            // so we can reuse it in case a "new" insertion (i.e. not an update)
            // is possible.
            // finding a matching value means that we've found that our desired
            // key is already in the table
            if ( cur != REMOVED ) {
                // starting at the natural offset, probe until we find an
                // offset that isn't full.
	            //noinspection unchecked
	            do {
                    index -= probe;
                    if ( index < 0 ) {
                        index += length;
                    }
                    cur = set[index];
                } while ( cur != FREE
					&& cur != REMOVED
					&& cur != obj
					&& !strategy.equals( ( T ) cur, obj ) );
            }

            // if the index we found was removed: continue probing until we
            // locate a free location or an element which equal()s the
            // one we have.
            if ( cur == REMOVED ) {
                int firstRemoved = index;
	            //noinspection unchecked
	            while ( cur != FREE
					&& ( cur == REMOVED || cur != obj ||
					!strategy.equals( ( T ) cur, obj ) ) ) {

                    index -= probe;
                    if ( index < 0 ) {
                        index += length;
                    }
                    cur = set[index];
                }
                // NOTE: cur cannot == REMOVED in this block
                return ( cur != FREE ) ? -index - 1 : firstRemoved;
            }
            // if it's full, the key is already stored
            // NOTE: cur cannot equal REMOVE here (would have retuned already (see above)
            return ( cur != FREE ) ? -index - 1 : index;
        }
    }


    @Override
    public void writeExternal( ObjectOutput out ) throws IOException {

        // VERSION
        out.writeByte( 0 );

        // SUPER
        super.writeExternal( out );

	    // STRATEGY
	    out.writeObject( strategy );
    }


    @Override
    public void readExternal( ObjectInput in )
            throws IOException, ClassNotFoundException {

        // VERSION
        in.readByte();

        // SUPER
        super.readExternal( in );

	    // STRATEGY
	    //noinspection unchecked
	    strategy = ( HashingStrategy<T> ) in.readObject();
    }
} // TCustomObjectHash
