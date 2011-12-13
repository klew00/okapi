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

package net.sf.okapi.lib.terminology.csv;

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

public class CSVReaderTest {

	private LocaleId locEN = LocaleId.ENGLISH;
	private LocaleId locFR = LocaleId.FRENCH;
	private String root;
	
	public CSVReaderTest () throws URISyntaxException {
		URL url = CSVReaderTest.class.getResource("/test01.csv");
		root = Util.getDirectoryName(url.toURI().getPath()) + File.separator;
	}
	
	@Test
	public void testSimpleCSV () {
		String snippet = "source 1,target 1\n"
			+ ",target 2\n"
			+ "target1,\n"
			+ ",\n"
			+ "  source 2  ,  target 2  \n"
			+ "\"source 3\",\"target 3\"\n"
			+ "\" source 4 \",\" target 4 \"\n"
			+ "\"source 5\",\"target 5,target 6\"\n"
			+ "\"source 5\",\"target 5,\"\"target 6\"\",target 7\"\n"
			+ "line without delimiter\n"
			+ "   \n";

		List<ConceptEntry> list = getConcepts(snippet, null, locEN, locFR);
		assertNotNull(list);
		assertEquals(6, list.size());
		
		ConceptEntry cent = list.get(0);
		assertEquals("source 1", cent.getEntries(locEN).getTerm(0).getText());
		assertEquals("target 1", cent.getEntries(locFR).getTerm(0).getText());
		cent = list.get(1);
		assertEquals("source 2", cent.getEntries(locEN).getTerm(0).getText());
		assertEquals("target 2", cent.getEntries(locFR).getTerm(0).getText());
		cent = list.get(2);
		assertEquals("source 3", cent.getEntries(locEN).getTerm(0).getText());
		assertEquals("target 3", cent.getEntries(locFR).getTerm(0).getText());
		cent = list.get(3);
		assertEquals(" source 4 ", cent.getEntries(locEN).getTerm(0).getText());
		assertEquals(" target 4 ", cent.getEntries(locFR).getTerm(0).getText());
		cent = list.get(4);
		assertEquals("source 5", cent.getEntries(locEN).getTerm(0).getText());
		assertEquals("target 5,target 6", cent.getEntries(locFR).getTerm(0).getText());
		cent = list.get(5);
		assertEquals("source 5", cent.getEntries(locEN).getTerm(0).getText());
		assertEquals("target 5,\"target 6\",target 7", cent.getEntries(locFR).getTerm(0).getText());
	}

	 @Test(expected=RuntimeException.class)
  	 public void testInvalidCSV () {
		String snippet = "source 1,target 1\n"
			+ ",target 2\n"
			+ "target1,\n"
			+ ",\n"
			+ "  source 2  ,  target 2  \n"
			+ "\"source 3\",\"target 3\"\n"
			+ "\" source 4 \",\" target 4 \"\n"
			+ "\"source 5\",\"target 5,target 6\"\n"
			+ "\"source 5\",\"target 5,\"\"target 6\"\",target 7\"\n"
			+ "sou\"rce 6,target 6\n"
			+ "line without delimiter\n"
			+ "   \n";

		List<ConceptEntry> list = getConcepts(snippet, null, locEN, locFR);
		assertNotNull(list);
		assertEquals(6, list.size());
		
		ConceptEntry cent = list.get(6);
		assertEquals("sou\"rce 6", cent.getEntries(locEN).getTerm(0).getText());
		assertEquals("target 6", cent.getEntries(locFR).getTerm(0).getText());
	}
	

	@Test
	public void testFromFiles () {
		File file = new File(root+"test01.csv");
		List<ConceptEntry> list = getConcepts(null, file, locEN, locFR);
		assertEquals(4, list.size());
		assertEquals("target 3", list.get(2).getEntries(locFR).getTerm(0).getText());
	}

	// Use either snippet or file
	List<ConceptEntry> getConcepts (String snippet,
		File file,
		LocaleId srcLoc,
		LocaleId trgLoc)
	{
		try {
			ArrayList<ConceptEntry> list = new ArrayList<ConceptEntry>();
			
			IGlossaryReader csv = new CSVReader(srcLoc, trgLoc);
			
			if ( file == null ) {
				InputStream is = new ByteArrayInputStream(snippet.getBytes("UTF-8"));
				csv.open(is);
			}
			else {
				csv.open(file);
			}
			while ( csv.hasNext() ) {
				list.add(csv.next());
			}
			csv.close();
			return list;
		}
		catch ( Throwable e ) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
}
