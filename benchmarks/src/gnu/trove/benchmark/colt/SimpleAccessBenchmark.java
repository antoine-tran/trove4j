package gnu.trove.benchmark.colt;

import cern.colt.map.OpenIntObjectHashMap;
import com.google.caliper.Param;
import com.google.caliper.SimpleBenchmark;


/**
 *
 */
public class SimpleAccessBenchmark extends SimpleBenchmark {
	@Param int size;

	private OpenIntObjectHashMap empty_map;
	private OpenIntObjectHashMap populated_map;


	@Override
	protected void setUp() throws Exception {
		System.out.println( "setUp" );
		empty_map = new OpenIntObjectHashMap();

		populated_map = new OpenIntObjectHashMap( size );
		for( int i = 0; i < size; i++ ) {
			populated_map.put( i, "value" );
		}
	}

	@Override
	protected void tearDown() throws Exception {
		System.out.println( "tearDown" );
		empty_map.clear();
		populated_map.clear();
	}


	public void timePut( int reps ) {
		System.out.println( "timePut: " + size );
		for( int r = 0; r < reps; r++ ) {
			for( int i = 0; i < size; i ++ ) {
				empty_map.put( i, "value" );
			}
		}
	}


	public void timeGet( int reps ) {
		System.out.println( "timeGet: " + size );
		for( int r = 0; r < reps; r++ ) {
			for( int i = 0; i < size; i++ ) {
				populated_map.get( i );
			}
		}
	}

	public void timeContainsKey( int reps ) {
		System.out.println( "timeContainsKey: " + reps );
		for( int r = 0; r < reps; r++ ) {
			for( int i = 0; i < size; i++ ) {
				populated_map.containsKey( i );
			}
		}
	}
}
