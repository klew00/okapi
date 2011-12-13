/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.lib.terminology.tsv;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.lib.terminology.ConceptEntry;
import net.sf.okapi.lib.terminology.IGlossaryReader;

import org.junit.Test;
import static org.junit.Assert.*;

public class TSVReaderTest {

	private LocaleId locEN = LocaleId.ENGLISH;
	private LocaleId locFR = LocaleId.FRENCH;
	private String root;
	
	public TSVReaderTest () throws URISyntaxException {
		URL url = TSVReaderTest.class.getResource("/test01.tsv");
		root = Util.getDirectoryName(url.toURI().getPath()) + File.separator;
	}
	
	@Test
	public void testSimpleTSV () {
		String snippet = "source 1\ttarget 1\n"
			+ "source 2\ttarget 2\tmore\n"
			+ "line without tab\n"
			+ "   \n"
			+ "source 3\ttarget 3\n";

		List<ConceptEntry> list = getConcepts(snippet, null, locEN, locFR);
		assertNotNull(list);
		assertEquals(3, list.size());
		
		ConceptEntry cent = list.get(0);
		assertEquals("source 1", cent.getEntries(locEN).getTerm(0).getText());
		assertEquals("target 1", cent.getEntries(locFR).getTerm(0).getText());
		cent = list.get(2);
		assertEquals("source 3", cent.getEntries(locEN).getTerm(0).getText());
		assertEquals("target 3", cent.getEntries(locFR).getTerm(0).getText());
	}


	@Test
	public void testFromFiles () {
		File file = new File(root+"test01.tsv");
		List<ConceptEntry> list = getConcepts(null, file, locEN, locFR);
		assertEquals(3, list.size());
		assertEquals("target 3", list.get(2).getEntries(locFR).getTerm(0).getText());
	}

	@Test
	public void testEncoding () {
		File file = new File(root+"test02_utf16be.tsv");
		List<ConceptEntry> list = getConcepts(null, file, locEN, locFR);
		assertEquals(2, list.size());
		assertEquals("\u00e9\u00df\u00d1\uffe6 target 2", list.get(1).getEntries(locFR).getTerm(0).getText());
	}

	// Use either snippet or file
	List<ConceptEntry> getConcepts (String snippet,
		File file,
		LocaleId srcLoc,
		LocaleId trgLoc)
	{
		try {
			ArrayList<ConceptEntry> list = new ArrayList<ConceptEntry>();
			
			IGlossaryReader tsv = new TSVReader(srcLoc, trgLoc);
			
			if ( file == null ) {
				InputStream is = new ByteArrayInputStream(snippet.getBytes("UTF-8"));
				tsv.open(is);
			}
			else {
				tsv.open(file);
			}
			while ( tsv.hasNext() ) {
				list.add(tsv.next());
			}
			tsv.close();
			return list;
		}
		catch ( Throwable e ) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
	
}
