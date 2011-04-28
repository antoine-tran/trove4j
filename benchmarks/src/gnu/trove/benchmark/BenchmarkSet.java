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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 *
 */
class BenchmarkSet implements Benchmark {
	private final String name;

	private final List<Benchmark> cases;

	BenchmarkSet( String name, Benchmark... cases ) {
		this.name = name;
		this.cases = new ArrayList<Benchmark>( Arrays.asList( cases ) );
	}

	public void setUp() {
		for ( Benchmark benchmark : cases ) {
			benchmark.setUp();
		}
	}

	public void tearDown() {
		for ( Benchmark benchmark : cases ) {
			benchmark.tearDown();
		}
	}

	public String getName() {
		return name;
	}

	
	public void run() {
		throw new UnsupportedOperationException();
	}


	public List<Benchmark> getCases() {
		return cases;
	}


	void addDynamic( Benchmark benchmark ) {
		cases.add( benchmark );
	}
}
