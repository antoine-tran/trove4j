package gnu.trove.map.hash;

import gnu.trove.strategy.HashingStrategy;
import junit.framework.TestCase;

import java.io.*;
import java.util.Map;
import java.util.Set;


/**
 *
 */
public class TCustomHashMapTest extends TestCase {
	// Example from Trove overview doc
	public void testArray() {
		char[] foo = new char[] { 'a', 'b', 'c' };
		char[] bar = new char[] { 'a', 'b', 'c' };

		assertFalse( foo.hashCode() == bar.hashCode() );
		//noinspection ArrayEquals
		assertFalse( foo.equals( bar ) );

		HashingStrategy<char[]> strategy = new ArrayHashingStrategy();
		assertTrue( strategy.computeHashCode( foo ) ==
			strategy.computeHashCode( bar ) );
		assertTrue( strategy.equals( foo, bar ) );

		Map<char[],String> map = new TCustomHashMap<char[],String>( strategy );
		map.put( foo, "yay" );
		assertTrue( map.containsKey( foo ) );
		assertTrue( map.containsKey( bar ) );
		assertEquals( "yay", map.get( foo ) );
		assertEquals( "yay", map.get( bar ) );

		Set<char[]> keys = map.keySet();
		assertTrue( keys.contains( foo ) );
		assertTrue( keys.contains( bar ) );
	}


	public void testSerialization() throws Exception {
		char[] foo = new char[] { 'a', 'b', 'c' };
		char[] bar = new char[] { 'a', 'b', 'c' };

		HashingStrategy<char[]> strategy = new ArrayHashingStrategy();
		Map<char[],String> map = new TCustomHashMap<char[],String>( strategy );

		map.put( foo, "yay" );

		// Make sure it still works after being serialized
		ObjectOutputStream oout = null;
		ByteArrayOutputStream bout = null;
		ObjectInputStream oin = null;
		ByteArrayInputStream bin = null;
		try {
			bout = new ByteArrayOutputStream();
			oout = new ObjectOutputStream( bout );

			oout.writeObject( map );

			bin = new ByteArrayInputStream( bout.toByteArray() );
			oin = new ObjectInputStream( bin );

			map = ( Map<char[],String> ) oin.readObject();
		}
		finally {
			if ( oin != null ) oin.close();
			if ( bin != null ) bin.close();
			if ( oout != null ) oout.close();
			if ( bout != null ) bout.close();
		}

		assertTrue( map.containsKey( foo ) );
		assertTrue( map.containsKey( bar ) );
		assertEquals( "yay", map.get( foo ) );
		assertEquals( "yay", map.get( bar ) );

		Set<char[]> keys = map.keySet();
		assertTrue( keys.contains( foo ) );
		assertTrue( keys.contains( bar ) );
	}
}
