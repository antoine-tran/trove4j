package gnu.trove.benchmark.compare;

import cern.colt.map.OpenIntObjectHashMap;
import com.google.caliper.Param;
import com.google.caliper.SimpleBenchmark;

import java.util.HashMap;
import java.util.Map;


/**
 *
 */
@SuppressWarnings( { "UnusedDeclaration", "MismatchedQueryAndUpdateOfCollection" } )
public class LibraryCompareBenchmark extends SimpleBenchmark {
	@Param int size;

	// java.util
	private Integer[] int_obj_array = null;
	private Map<Integer,String> java_empty_map;
	private Map<Integer,String> java_populated_map;

	// Colt
	private OpenIntObjectHashMap colt_empty_map;
	private OpenIntObjectHashMap colt_populated_map;

	// Trove 2
	private gnu.trove.TIntObjectHashMap<String> trove2_empty_map;
	private gnu.trove.TIntObjectHashMap<String> trove2_populated_map;

	// Trove 2 - THashMap
	private Map<Integer,String> trove2_empty_obj_map;
	private Map<Integer,String> trove2_populated_obj_map;

	// Trove 3 - TIntObjectHashMap
	private gnu.trove.map.TIntObjectMap<String> trove3_empty_map;
	private gnu.trove.map.TIntObjectMap<String> trove3_populated_map;

	// Trove 3 - THashMap
	private Map<Integer,String> trove3_empty_obj_map;
	private Map<Integer,String> trove3_populated_obj_map;


	@Override
	protected void setUp() throws Exception {
		if ( int_obj_array == null ) {
			int_obj_array = new Integer[ size ];
			for( int i = 0; i < size; i++ ) {
				int_obj_array[ i ] = Integer.valueOf( i );
			}
		}

		java_empty_map = new HashMap<Integer, String>();
		java_populated_map = new HashMap<Integer, String>( size );
		for( int i = 0; i < size; i++ ) {
			java_populated_map.put( Integer.valueOf( i ), "value" );
		}

		colt_empty_map = new OpenIntObjectHashMap();
		colt_populated_map = new OpenIntObjectHashMap( size );
		for( int i = 0; i < size; i++ ) {
			colt_populated_map.put( i, "value" );
		}

		trove2_empty_map = new gnu.trove.TIntObjectHashMap<String>();
		trove2_populated_map = new gnu.trove.TIntObjectHashMap<String>( size );
		for( int i = 0; i < size; i++ ) {
			trove2_populated_map.put( i, "value" );
		}

		trove2_empty_obj_map = new gnu.trove.THashMap<Integer,String>();
		trove2_populated_obj_map = new gnu.trove.THashMap<Integer,String>();
		for( int i = 0; i < size; i++ ) {
			trove2_populated_obj_map.put( Integer.valueOf( i ), "value" );
		}

		trove3_empty_map = new gnu.trove.map.hash.TIntObjectHashMap<String>();
		trove3_populated_map = new gnu.trove.map.hash.TIntObjectHashMap<String>( size );
		for( int i = 0; i < size; i++ ) {
			trove3_populated_map.put( i, "value" );
		}

		trove3_empty_obj_map = new gnu.trove.map.hash.THashMap<Integer,String>();
		trove3_populated_obj_map = new gnu.trove.map.hash.THashMap<Integer,String>( size );
		for( int i = 0; i < size; i++ ) {
			trove3_populated_obj_map.put( Integer.valueOf( i ), "value" );
		}
	}

	@Override
	protected void tearDown() throws Exception {
		java_empty_map.clear();
		java_populated_map.clear();

		colt_empty_map.clear();
		colt_populated_map.clear();

		trove2_empty_map.clear();
		trove2_populated_map.clear();

		trove2_empty_obj_map.clear();
		trove2_populated_obj_map.clear();

		trove3_empty_map.clear();
		trove3_populated_map.clear();

		trove3_empty_obj_map.clear();
		trove3_populated_obj_map.clear();
	}


	/////////////////////////////////////////////////////
	// put

	public void timeJavaPut( int reps ) {
		for( int r = 0; r < reps; r++ ) {
			for( int i = 0; i < size; i ++ ) {
				java_empty_map.put( int_obj_array[ i ], "value" );
			}
		}
	}

	public void timeColtPut( int reps ) {
		for( int r = 0; r < reps; r++ ) {
			for( int i = 0; i < size; i ++ ) {
				colt_empty_map.put( i, "value" );
			}
		}
	}

	public void timeTrove2Put( int reps ) {
		for( int r = 0; r < reps; r++ ) {
			for( int i = 0; i < size; i ++ ) {
				trove2_empty_map.put( i, "value" );
			}
		}
	}

	public void timeTrove2ObjectPut( int reps ) {
		for( int r = 0; r < reps; r++ ) {
			for( int i = 0; i < size; i ++ ) {
				trove2_empty_obj_map.put( int_obj_array[ i ], "value" );
			}
		}
	}

	public void timeTrove3Put( int reps ) {
		for( int r = 0; r < reps; r++ ) {
			for( int i = 0; i < size; i ++ ) {
				trove3_empty_map.put( i, "value" );
			}
		}
	}

	public void timeTrove3ObjectPut( int reps ) {
		for( int r = 0; r < reps; r++ ) {
			for( int i = 0; i < size; i ++ ) {
				trove3_empty_obj_map.put( int_obj_array[ i ], "value" );
			}
		}
	}


	/////////////////////////////////////////////////////
	// get

	public void timeJavaGet( int reps ) {
		for( int r = 0; r < reps; r++ ) {
			for( int i = 0; i < size; i++ ) {
				java_populated_map.get( int_obj_array[ i ] );
			}
		}
	}

	public void timeColtGet( int reps ) {
		for( int r = 0; r < reps; r++ ) {
			for( int i = 0; i < size; i++ ) {
				colt_populated_map.get( i );
			}
		}
	}

	public void timeTrove2Get( int reps ) {
		for( int r = 0; r < reps; r++ ) {
			for( int i = 0; i < size; i++ ) {
				trove2_populated_map.get( i );
			}
		}
	}

	public void timeTrove2ObjectGet( int reps ) {
		for( int r = 0; r < reps; r++ ) {
			for( int i = 0; i < size; i++ ) {
				trove2_populated_obj_map.get( int_obj_array[ i ] );
			}
		}
	}

	public void timeTrove3Get( int reps ) {
		for( int r = 0; r < reps; r++ ) {
			for( int i = 0; i < size; i++ ) {
				trove3_populated_map.get( i );
			}
		}
	}

	public void timeTrove3ObjectGet( int reps ) {
		for( int r = 0; r < reps; r++ ) {
			for( int i = 0; i < size; i++ ) {
				trove3_populated_obj_map.get( int_obj_array[ i ] );
			}
		}
	}


	/////////////////////////////////////////////////////
	// containsKey

	public void timeJavaContainsKey( int reps ) {
		for( int r = 0; r < reps; r++ ) {
			for( int i = 0; i < size; i++ ) {
				java_populated_map.containsKey( int_obj_array[ i ] );
			}
		}
	}

	public void timeColtContainsKey( int reps ) {
		for( int r = 0; r < reps; r++ ) {
			for( int i = 0; i < size; i++ ) {
				colt_populated_map.containsKey( i );
			}
		}
	}

	public void timeTrove2ContainsKey( int reps ) {
		for( int r = 0; r < reps; r++ ) {
			for( int i = 0; i < size; i++ ) {
				trove2_populated_map.containsKey( i );
			}
		}
	}

	public void timeTrove2ObjectContainsKey( int reps ) {
		for( int r = 0; r < reps; r++ ) {
			for( int i = 0; i < size; i++ ) {
				trove2_populated_obj_map.containsKey( int_obj_array[ i ] );
			}
		}
	}

	public void timeTrove3ContainsKey( int reps ) {
		for( int r = 0; r < reps; r++ ) {
			for( int i = 0; i < size; i++ ) {
				trove3_populated_map.containsKey( i );
			}
		}
	}

	public void timeTrove3ObjectContainsKey( int reps ) {
		for( int r = 0; r < reps; r++ ) {
			for( int i = 0; i < size; i++ ) {
				trove3_populated_obj_map.containsKey( int_obj_array[ i ] );
			}
		}
	}


	public static void main( String[] args ) throws Exception {
		LibraryCompareBenchmark benchmark = new LibraryCompareBenchmark();
		while( true ) {
			benchmark.setUp();
			benchmark.timeTrove3ObjectGet( 1000 );
			benchmark.tearDown();
			Thread.sleep( 1000 );
		}
	}
}
