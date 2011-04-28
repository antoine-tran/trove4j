// ////////////////////////////////////////////////////////////////////////////
// Copyright (c) 2009, Rob Eden All Rights Reserved.
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
// ////////////////////////////////////////////////////////////////////////////
package gnu.trove.benchmark;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 *
 */
public class BenchmarkRunner {
	private static final BenchmarkSet MAP_PUT_SET =
		new BenchmarkSet( "Map Puts", new JavaHashMapPut(), new TroveHashMapPut() );
	private static final BenchmarkSet MAP_GET_SET =
		new BenchmarkSet( "Map Gets", new JavaHashMapGet(), new TroveHashMapGet() );
	private static final BenchmarkSet MAP_ITERATION =
		new BenchmarkSet( "Map Iteration", new JavaHashMapIteration(),
			new TroveHashMapIteration(), new TroveHashMapForEach() );

	private static final Benchmark[] CASES = { MAP_PUT_SET, MAP_GET_SET, MAP_ITERATION };

	private static final Runtime RUNTIME = Runtime.getRuntime();

	public static final Integer INTEGERS[] = new Integer[ 10000 ];

	private static int warmup_times = 10;
	private static long warmup_settle_time = 3000;
	private static long run_times = 100;
	private static long case_settle_time = 20;

	public static void main( String[] args ) throws Exception {
		List<GarbageCollectorMXBean> collectors =
			ManagementFactory.getGarbageCollectorMXBeans();
		if ( collectors != null && collectors.isEmpty() ) collectors = null;

		if ( collectors == null ) {
			System.out.println( "No garbage collector MX beans found. " +
				"Garbage collection statistics will be disabled." );
		}

		System.gc();

		for( int i = 0; i < INTEGERS.length; i++ ) {
			INTEGERS[ i ] = Integer.valueOf( i );
		}


		// Setup
		System.out.println( "Checking for external libraries..." );

		System.out.print( "   Colt: " );
		if ( classIsAvailable( "cern.colt.map.OpenIntObjectHashMap" ) ) {
			System.out.println( "   found" );
			MAP_PUT_SET.addDynamic(
				createBenchmark( "gnu.trove.benchmark.colt.ColtHashMapPut" ) );
			MAP_GET_SET.addDynamic(
				createBenchmark( "gnu.trove.benchmark.colt.ColtHashMapGet" ) );
		}
		else System.out.println( "   not found" );

		System.out.print( "   Trove 2: " );
		if ( classIsAvailable( "gnu.trove.TIntObjectHashMap" ) ) {
			System.out.println( "found" );
			MAP_PUT_SET.addDynamic(
				createBenchmark( "gnu.trove.benchmark.trove2.Trove2HashMapPut" ) );
			MAP_GET_SET.addDynamic(
				createBenchmark( "gnu.trove.benchmark.trove2.Trove2HashMapGet" ) );
			MAP_ITERATION.addDynamic(
				createBenchmark( "gnu.trove.benchmark.trove2.Trove2HashMapIteration" ) );
			MAP_ITERATION.addDynamic(
				createBenchmark( "gnu.trove.benchmark.trove2.Trove2HashMapForEach" ) );
		}
		else System.out.println( "not found" );


		// Run
		System.out.println();
		System.out.println( "Running tests..." );
		System.out.println();

		for( Benchmark benchmark : CASES ) {
			String indent_level = "";

			runBenchmark( benchmark, indent_level, collectors );
		}

//		Object shutdown_lock = new Object();
//		synchronized ( shutdown_lock ) {
//			try {
//				shutdown_lock.wait();
//			}
//			catch ( InterruptedException e ) {
//				// ignore
//			}
//		}
	}

	private static void runBenchmark( Benchmark mark, String indent_level,
		List<GarbageCollectorMXBean> collectors ) {

		System.out.print( indent_level );
		System.out.print( mark.getName() );
		System.out.print( "..." );

		CollectionInfo collection_info = new CollectionInfo();
		getGCTime( collectors, collection_info, null );

		long total_time = 0;

		if ( mark instanceof BenchmarkSet) {
			System.out.println();

			String child_indent = indent_level + "   ";
			List<Benchmark> child_cases = ( (BenchmarkSet) mark ).getCases();
			for( int i = 0; i < child_cases.size(); i++ ) {
				Benchmark child = child_cases.get( i );
				runBenchmark( child, child_indent, collectors );
			}

			System.out.print( indent_level );
			System.out.print( mark.getName() );
			System.out.print( ": " );
		}
		else {
			// Run a few times to warm up
			for( int i = 0; i < warmup_times; i++ ) {
				mark.setUp();
				mark.run();
				mark.tearDown();
			}

			// Wait for hotspot to have time to compile
			sleep( warmup_settle_time );

			// Run the tests
			for( int i = 0; i < run_times; i++ ) {
				mark.setUp();

				long start_time = System.nanoTime();
				mark.run();
				total_time += System.nanoTime() - start_time;

				mark.tearDown();
			}
		}

//		gc_info = getGCTime( collectors, gc_info );

		System.out.print( "done" );
		if ( total_time == 0 ) System.out.println( "." );
		else {
			System.out.print( ": " );
			System.out.println(
				TimeUnit.MILLISECONDS.convert( total_time, TimeUnit.NANOSECONDS ) );
		}
	}

	private static void sleep( long time ) {
		try {
			Thread.sleep( time );
		}
		catch( InterruptedException ex ) {
			// ignore
		}
	}


	private static boolean classIsAvailable( String class_name ) {
		try {
			Class.forName( "cern.colt.map.OpenIntObjectHashMap" );
			return true;
		}
		catch( Throwable t ) {
			return false;
		}
	}


	private static Benchmark createBenchmark( String class_name )
		throws Exception {

		return (Benchmark) Class.forName( class_name ).newInstance();
	}
	


	private static CollectionInfo getGCTime( List<GarbageCollectorMXBean> collectors,
		CollectionInfo fill_into, CollectionInfo since_info ) {

		long count = 0;
		long time = 0;
		//noinspection ForLoopReplaceableByForEach
		for( int i = 0; i < collectors.size(); i++ ) {
			GarbageCollectorMXBean bean = collectors.get( i );

			count += bean.getCollectionCount();
			time += bean.getCollectionTime();
		}

		long free_mem = RUNTIME.freeMemory();

		if ( since_info == null ) fill_into.fill( free_mem, count, time );
		else fill_into.fill( free_mem, since_info, count, time );
		return fill_into;
	}


	private static class CollectionInfo {
		private long free_mem;
		private long count;
		private long time;

		CollectionInfo() {}

		void fill( long free_mem, long count, long time ) {
			this.count = count;
			this.time = time;
		}

		void fill( long free_mem, CollectionInfo start, long end_count, long end_time ) {
			this.count = end_count - start.count;
			this.time = end_time - start.time;
		}
	}
}
