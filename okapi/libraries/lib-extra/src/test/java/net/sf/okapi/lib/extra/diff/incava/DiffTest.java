/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

package net.sf.okapi.lib.extra.diff.incava;

import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import net.sf.okapi.lib.extra.diff.incava.DiffLists;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DiffTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testStrings1() {
		String[] a = { "a", "b", "c", "e", "h", "j", "l", "m", "n", "p" };
		String[] b = { "b", "c", "d", "e", "f", "j", "k", "l", "m", "r", "s", "t" };
		Difference[] expected = { new Difference(0, 0, 0, -1), new Difference(3, -1, 2, 2),
				new Difference(4, 4, 4, 4), new Difference(6, -1, 6, 6),
				new Difference(8, 9, 9, 11) };

		DiffLists<String> d = new DiffLists<String>(a, b);
		List<Difference> diffs = d.diff();

		int i = 0;
		for (Difference diff : diffs) {
			Assert.assertEquals(expected[i++], diff);
		}
	}

	@Test
	public void testStrings2() {
		String[] a = { "a", "b", "c", "d" };
		String[] b = { "c", "d" };
		Difference[] expected = { new Difference(0, 1, 0, -1) };

		DiffLists<String> d = new DiffLists<String>(a, b);
		List<Difference> diffs = d.diff();

		int i = 0;
		for (Difference diff : diffs) {
			Assert.assertEquals(expected[i++], diff);
		}
	}

	@Test
	public void testStrings3() {
		String[] a = { "a", "b", "c", "d", "x", "y", "z" };
		String[] b = { "c", "d" };
		Difference[] expected = { new Difference(0, 1, 0, -1), new Difference(4, 6, 2, -1) };

		DiffLists<String> d = new DiffLists<String>(a, b);
		List<Difference> diffs = d.diff();

		int i = 0;
		for (Difference diff : diffs) {
			Assert.assertEquals(expected[i++], diff);
		}
	}

	@Test
	public void testStrings4() {
		String[] a = { "a", "b", "c", "d", "e" };
		String[] b = { "a", "x", "y", "b", "c", "j", "e" };
		Difference[] expected = { new Difference(1, -1, 1, 2), new Difference(3, 3, 5, 5) };

		DiffLists<String> d = new DiffLists<String>(a, b);
		List<Difference> diffs = d.diff();

		int i = 0;
		for (Difference diff : diffs) {
			Assert.assertEquals(expected[i++], diff);
		}
	}

	@Test
	public void testInteger() {
		Integer[] a = { new Integer(1), new Integer(2), new Integer(3) };
		Integer[] b = { new Integer(2), new Integer(3) };
		Difference[] expected = { new Difference(0, 0, 0, -1) };

		DiffLists<Integer> d = new DiffLists<Integer>(a, b);
		List<Difference> diffs = d.diff();

		int i = 0;
		for (Difference diff : diffs) {
			Assert.assertEquals(expected[i++], diff);
		}
	}

	@Test
	public void testStringsMatches() {
		String[] a = { "a", "b", "c", "d", "e" };
		String[] b = { "a", "x", "y", "b", "c", "j", "e" };

		DiffLists<String> d = new DiffLists<String>(a, b);
		Map<Integer, Integer> matches = d.getMatches();
		
		Assert.assertEquals(0, (int)matches.get(0));
		Assert.assertEquals(3, (int)matches.get(1));
		Assert.assertEquals(4, (int)matches.get(2));
		Assert.assertEquals(6, (int)matches.get(4));
	}

	@Test
	public void testIntegerMatches() {
		Integer[] a = { new Integer(1), new Integer(2), new Integer(3) };
		Integer[] b = { new Integer(2), new Integer(3) };		

		DiffLists<Integer> d = new DiffLists<Integer>(a, b);		
		Map<Integer, Integer> matches = d.getMatches();
		
		Assert.assertEquals(0, (int)matches.get(1));
		Assert.assertEquals(1, (int)matches.get(2));		
	}
}
