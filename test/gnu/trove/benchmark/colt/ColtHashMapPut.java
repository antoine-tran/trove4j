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

package gnu.trove.benchmark.colt;

import cern.colt.map.OpenIntObjectHashMap;
import gnu.trove.benchmark.BenchmarkRunner;
import gnu.trove.benchmark.Benchmark;


/**
 *
 */
public class ColtHashMapPut implements Benchmark {
	private OpenIntObjectHashMap map;


	public void setUp() {
		if ( map != null ) return;

		map = new OpenIntObjectHashMap();
		map.ensureCapacity( BenchmarkRunner.INTEGERS.length );
	}

	public void tearDown() {
		map.clear();
	}

	public String getName() {
		return "Colt HashMap Put";
	}

	public void run() {
		for( Integer i : BenchmarkRunner.INTEGERS ) {
			map.put( i.intValue(), i );
		}
	}
}