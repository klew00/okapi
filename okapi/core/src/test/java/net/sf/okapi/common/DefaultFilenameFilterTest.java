/*===========================================================================
  Copyright (C) 2012 by the Okapi Framework contributors
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

package net.sf.okapi.common;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DefaultFilenameFilterTest {

	private File root;
	
	@Before
	public void setUp() throws URISyntaxException {
		URL url = DefaultFilenameFilterTest.class.getResource("/test.txt");
		root = new File(Util.getDirectoryName(url.getPath()));
	}

	@Test
	public void testPattern1 () {
		File[] files = root.listFiles(new DefaultFilenameFilter("t*.txt", false));
		assertEquals(6, files.length);
		for ( File file : files ) {
			assertTrue(file.getName().startsWith("t") || file.getName().startsWith("T"));
			assertTrue(file.getName().endsWith(".txt"));
		}
	}
	
	@Test
	public void testPattern2 () {
		File[] files = root.listFiles(new DefaultFilenameFilter("test?.t?t", false));
		assertEquals(2, files.length);
		for ( File file : files ) {
			assertTrue(file.getName().startsWith("test") || file.getName().startsWith("Test"));
			assertTrue(file.getName().endsWith(".txt") || file.getName().endsWith(".tzt"));
		}
	}
	
	@Test
	public void testPattern3 () {
		File[] files = root.listFiles(new DefaultFilenameFilter("testE*.t?t", false));
		assertEquals(2, files.length);
		for ( File file : files ) {
			assertTrue(file.getName().startsWith("TestE"));
			assertTrue(file.getName().endsWith(".txt") || file.getName().endsWith(".tzt"));
		}
	}
	
	@Test
	public void testPattern4 () {
		File[] files = root.listFiles(new DefaultFilenameFilter("test.txt", false));
		assertEquals(1, files.length);
		assertEquals("test.txt", files[0].getName());
	}
	
	@Test
	public void testPattern5 () {
		File[] files = root.listFiles(new DefaultFilenameFilter("TestEtc.*", false));
		assertEquals(2, files.length);
		for ( File file : files ) {
			assertTrue(file.getName().startsWith("TestEt"));
		}
	}
	
	@Test
	public void testPattern6 () {
		File[] files = root.listFiles(new DefaultFilenameFilter("*.tzt", false));
		assertEquals(1, files.length);
		assertEquals("testB.tzt", files[0].getName());
		// Backward compatible constructor
		files = root.listFiles(new DefaultFilenameFilter(".tzt"));
		assertEquals(1, files.length);
		assertEquals("testB.tzt", files[0].getName());
	}
	
	@Test
	public void testPattern7 () {
		File[] files = root.listFiles(new DefaultFilenameFilter("*.htm", false));
		assertEquals(0, files.length); // We have test.html not test.htm
		// Backward compatible constructor
		files = root.listFiles(new DefaultFilenameFilter(".htm"));
		assertEquals(0, files.length); // We have test.html not test.htm
	}
	
	@Test
	public void testPattern8 () {
		// Case-sensitive call
		File[] files = root.listFiles(new DefaultFilenameFilter("t*.txt", true));
		assertEquals(2, files.length);
		for ( File file : files ) {
			assertTrue(file.getName().startsWith("t"));
			assertTrue(file.getName().endsWith(".txt"));
		}
	}
	
}
