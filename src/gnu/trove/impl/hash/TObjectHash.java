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

import gnu.trove.procedure.TObjectProcedure;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;


/**
 * An open addressed hashing implementation for Object types.
 * <p/>
 * Created: Sun Nov  4 08:56:06 2001
 *
 * @author Eric D. Friedman
 * @author Rob Eden
 * @author Jeff Randall
 * @version $Id: TObjectHash.java,v 1.1.2.6 2009/11/07 03:36:44 robeden Exp $
 */
abstract public class TObjectHash<T> extends THash {

    @SuppressWarnings({"UnusedDeclaration"})
    static final long serialVersionUID = -3461112548087185871L;


    /**
     * the set of Objects
     */
    public transient Object[] _set;

    public static final Object REMOVED = new Object(), FREE = new Object();


    /**
     * Creates a new <code>TObjectHash</code> instance with the
     * default capacity and load factor.
     */
    public TObjectHash() {
        super();
    }


    /**
     * Creates a new <code>TObjectHash</code> instance whose capacity
     * is the next highest prime above <tt>initialCapacity + 1</tt>
     * unless that value is already prime.
     *
     * @param initialCapacity an <code>int</code> value
     */
    public TObjectHash(int initialCapacity) {
        super(initialCapacity);
    }


    /**
     * Creates a new <code>TObjectHash</code> instance with a prime
     * value at or near the specified capacity and load factor.
     *
     * @param initialCapacity used to find a prime capacity for the table.
     * @param loadFactor      used to calculate the threshold over which
     *                        rehashing takes place.
     */
    public TObjectHash(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }


    public int capacity() {
        return _set.length;
    }


    protected void removeAt(int index) {
        _set[index] = REMOVED;
        super.removeAt(index);
    }


    /**
     * initializes the Object set of this hash table.
     *
     * @param initialCapacity an <code>int</code> value
     * @return an <code>int</code> value
     */
    public int setUp(int initialCapacity) {
        int capacity;

        capacity = super.setUp(initialCapacity);
        _set = new Object[capacity];
        Arrays.fill(_set, FREE);
        return capacity;
    }


    /**
     * Executes <tt>procedure</tt> for each element in the set.
     *
     * @param procedure a <code>TObjectProcedure</code> value
     * @return false if the loop over the set terminated because
     *         the procedure returned false for some value.
     */
    @SuppressWarnings({"unchecked"})
    public boolean forEach(TObjectProcedure<? super T> procedure) {
        Object[] set = _set;
        for (int i = set.length; i-- > 0;) {
            if (set[i] != FREE
                    && set[i] != REMOVED
                    && !procedure.execute((T) set[i])) {
                return false;
            }
        }
        return true;
    }


    /**
     * Searches the set for <tt>obj</tt>
     *
     * @param obj an <code>Object</code> value
     * @return a <code>boolean</code> value
     */
    @SuppressWarnings({"unchecked"})
    public boolean contains(Object obj) {
        return index(obj) >= 0;
    }


    /**
     * Locates the index of <tt>obj</tt>.
     *
     * @param obj an <code>Object</code> value
     * @return the index of <tt>obj</tt> or -1 if it isn't in the set.
     */
    protected int index(Object obj) {
        if (obj == null)
            return indexForNull();

        // From here on we know obj to be non-null
        final int hash = obj.hashCode() & 0x7fffffff;
        int index = hash % _set.length;
        Object cur = _set[index];


        if (cur == FREE) {
            return -1;
        }

        if (cur == obj || equals(obj, cur)) {
            return index;
        }

        return indexRehashed(obj, index, hash, cur);
    }

    /**
     * Locates the index of non-null <tt>obj</tt>.
     *
     * @param obj   target key, know to be non-null
     * @param index we start from
     * @param hash
     * @param cur
     * @return
     */
    private int indexRehashed(Object obj, int index, int hash, Object cur) {
        final Object[] set = _set;
        final int length = set.length;

        // NOTE: here it has to be REMOVED or FULL (some user-given value)
        // see Knuth, p. 529
        int probe = 1 + (hash % (length - 2));

        do {
            index -= probe;
            if (index < 0) {
                index += length;
            }
            cur = set[index];
            //
            if (cur == FREE)
                return -1;
        } while (!(cur == obj || equals(obj, cur)));

        return index;
    }

    /**
     * Locates the index <tt>null</tt>.
     *
     * null specific loop exploiting several properties to simplify the iteration logic
     * - the null value hashes to 0 we so we can iterate from the beginning.
     * - the probe value is 1 for this case
     * - object identity can be used to match this case
     * <p/>
     * --> this result a simpler loop
     *
     * @return
     */
    private int indexForNull() {
        int index = 0;
        for (Object o : _set) {
            if (o == null)
                return index;

            if (o == FREE)
                return -1;

            index++;
        }

        return -1;
    }


    /**
     * Locates the index at which <tt>obj</tt> can be inserted.  if
     * there is already a value equal()ing <tt>obj</tt> in the set,
     * returns that value's index as <tt>-index - 1</tt>.
     *
     * If a slot is found the value is inserted
     *
     * @param obj an <code>Object</code> value
     * @return the index of a FREE slot at which obj can be inserted
     *         or, if obj is already stored in the hash, the negative value of
     *         that index, minus 1: -index -1.
     */
    protected int insertionIndex(T obj) {
        if (obj == null)
            return insertionIndexForNull();

        final int hash = obj.hashCode() & 0x7fffffff;
        int index = hash % _set.length;
        Object cur = _set[index];

        if (cur == FREE) {
            _set[index] = obj;  // insert value
            return index;       // empty, all done
        }

        if (cur == obj || equals(obj, cur)) {
            return -index - 1;   // already stored
        }

        return insertionIndexRehash(obj, index, hash, cur);
    }

    /**
     * Looks for a slot using double hashing for a non-null key values and inserts the value
     * in the slot
     *
     * @param obj   non-null key value
     * @param index natural index
     * @param hash
     * @param cur   value of first matched slot
     * @return
     */
    private int insertionIndexRehash(T obj, int index, int hash, Object cur) {
        final Object[] set = _set;
        final int length = set.length;
        // already FULL or REMOVED, must probe
        // compute the double hash
        final int probe = 1 + (hash % (length - 2));

        final int loopIndex = index;

        /**
         * Look for any FREE slot until we start to loop
         */
        do {
            index -= probe;
            if (index < 0) {
                index += length;
            }
            cur = set[index];
            //
            if (cur == FREE) {
                _set[index] = obj;  // insert value
                return index;
            }

            if (cur == obj || equals(obj, cur))
                return -index - 1;

            // Detect loop
        } while (index != loopIndex);

        /**
         * if we get here performance will be BAD!!!! We found no FREE slot, now
         * we look for REMOVED slot to reuse it
         */
        do {
            index -= probe;
            if (index < 0) {
                index += length;
            }
            cur = set[index];
            // Break loop by reusing the first REMOVED slot
            if (cur == REMOVED) {
                _set[index] = obj;  // insert value
                return index;
            }
            // Detect loop
        } while (index != loopIndex);


        // Can a resizing strategy be found that resizes the set?
        throw new IllegalStateException("No free or removed slots available. Key set full?!!");
    }

    /**
     * Looks for a slot using double hashing for a null key value and inserts the value.
     *
     * null specific loop exploiting several properties to simplify the iteration logic
     * - the null value hashes to 0 we so we can iterate from the beginning.
     * - the probe value is 1 for this case
     * - object identity can be used to match this case
     *
     * @return
     */
    private int insertionIndexForNull() {
        int index = 0;

        // Look for a slot containing the 'null' value as key
        for (Object o : _set) {
            if (o == FREE) {
                _set[index] = null;  // insert value
                return index;
            }

            if (o == null)
                return -index - 1;

            index++;
        }

        // Look for a REMOVED slot
        for (Object o : _set) {
            if (o == REMOVED) {
                _set[index] = null;  // insert value
                return index;
            }

            if (o == null)
                return -index - 1;

            index++;
        }

        // We scanned the entire key set and found nothing, is set full?
        // Can a resizing strategy be found that resizes the set?
        throw new IllegalStateException("Could not find insertion index for null key. Key set full!?!!");
    }


    /**
     * Convenience methods for subclasses to use in throwing exceptions about
     * badly behaved user objects employed as keys.  We have to throw an
     * IllegalArgumentException with a rather verbose message telling the
     * user that they need to fix their object implementation to conform
     * to the general contract for java.lang.Object.
     *
     * @param o1 the first of the equal elements with unequal hash codes.
     * @param o2 the second of the equal elements with unequal hash codes.
     * @throws IllegalArgumentException the whole point of this method.
     */
    protected final void throwObjectContractViolation(Object o1, Object o2)
            throws IllegalArgumentException {

        throw new IllegalArgumentException("Equal objects must have equal hashcodes. " +
                "During rehashing, Trove discovered that the following two objects claim " +
                "to be equal (as in java.lang.Object.equals()) but their hashCodes (or " +
                "those calculated by your TObjectHashingStrategy) are not equal." +
                "This violates the general contract of java.lang.Object.hashCode().  See " +
                "bullet point two in that method's documentation. object #1 =" + o1 +
                "; object #2 =" + o2);
    }


    protected boolean equals(Object notnull, Object two) {
        return two != null && notnull.equals(two);
    }

    protected int hash(Object notnull) {
        return notnull == null ? 0 : notnull.hashCode();
    }


    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        // VERSION
        out.writeByte(0);

        // SUPER
        super.writeExternal(out);
    }


    @Override
    public void readExternal(ObjectInput in)
            throws IOException, ClassNotFoundException {

        // VERSION
        in.readByte();

        // SUPER
        super.readExternal(in);
    }
} // TObjectHash
