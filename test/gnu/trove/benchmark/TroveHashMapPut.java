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

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;


/**
 *
 */
class TroveHashMapPut implements Benchmark {
	private TIntObjectMap<Integer> map;
	
	public void setUp() {
		if ( map != null ) return;

		map = new TIntObjectHashMap<Integer>();
		( ( TIntObjectHashMap<Integer> ) map ).ensureCapacity(
			BenchmarkRunner.INTEGERS.length );
	}

	public void tearDown() {
		map.clear();
	}

	public String getName() {
		return "Trove HashMap Put";
	}

	public void run() {
		for( Integer i : BenchmarkRunner.INTEGERS ) {
			map.put( i.intValue(), i );
		}
	}
}
