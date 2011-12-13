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

package net.sf.okapi.lib.terminology.tbx;

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
import net.sf.okapi.lib.terminology.LangEntry;
import net.sf.okapi.lib.terminology.TermEntry;

import org.junit.Test;
import static org.junit.Assert.*;

public class TBXReaderTest {

	private LocaleId locEN = LocaleId.ENGLISH;
	private LocaleId locFR = LocaleId.FRENCH;
	private LocaleId locHU = LocaleId.fromString("hu");
	private LocaleId locTR = LocaleId.fromString("tr");
	private String root;
	
	public TBXReaderTest () throws URISyntaxException {
		URL url = TBXReaderTest.class.getResource("/test01.tbx");
		root = Util.getDirectoryName(url.toURI().getPath()) + File.separator;
	}
	
	@Test
	public void testSimpleTBX () {
		String snippet = "<?xml version='1.0'?>"
			+ "<!DOCTYPE martif SYSTEM \"TBXcoreStructV02.dtd\">"
			+ "<martif type=\"TBX\" xml:lang=\"en\"><martifHeader><fileDesc><sourceDesc>"
			+ "<p>From an Oracle corporation termbase</p>"
			+ "</sourceDesc></fileDesc>"
			+ "<encodingDesc><p type=\"XCSURI\">http://www.lisa.org/fileadmin/standards/tbx/TBXXCSV02.XCS</p></encodingDesc>"
			+ "</martifHeader><text><body>"
			
			+ "<termEntry id=\"eid1\">"
			+ "<descrip type=\"subjectField\">manufacturing</descrip>"
			+ "<descrip type=\"definition\">def text</descrip>"
			+ "<langSet xml:lang=\"en\">"
			+ "<tig>"
			+ "<term id=\"eid1-en1\">en text</term>"
			+ "<termNote type=\"partOfSpeech\">noun-en</termNote>"
			+ "</tig></langSet>"
			+ "<langSet xml:lang=\"hu\">"
			+ "<tig>"
			+ "<term id=\"eid1-hu1\">hu <hi>special</hi> text</term>"
			+ "<termNote type=\"partOfSpeech\">noun-hu</termNote>"
			+ "</tig></langSet>"
			+ "</termEntry>"
			
			+ "<termEntry id=\"ent2\">"
			+ "<langSet xml:lang=\"en\">"
			+ "<ntig><termGrp>"
			+ "<term id=\"ent2-1\">en text2</term>"
			+ "</termGrp></ntig></langSet>"
			+ "<langSet xml:lang=\"fr\">"
			+ "<tig>"
			+ "<term id=\"ent2-2\">fr text2</term>"
			+ "</tig></langSet>"
			+ "</termEntry>"

			+ "</body></text></martif>";

		List<ConceptEntry> list = getConcepts(snippet, null);
		assertNotNull(list);
		assertEquals(2, list.size());
		
		ConceptEntry gent = list.get(0);
		assertEquals("eid1", gent.getId());
		assertTrue(gent.hasLocale(locEN));
		LangEntry lent = gent.getEntries(locEN);
		TermEntry tent = lent.getTerm(0);
		assertEquals("eid1-en1", tent.getId());
		assertEquals("en text", tent.getText());
		
		assertTrue(gent.hasLocale(locHU));
		lent = gent.getEntries(locHU);
		tent = lent.getTerm(0);
		assertEquals("eid1-hu1", tent.getId());
		assertEquals("hu special text", tent.getText());

		gent = list.get(1);
		assertTrue(gent.hasLocale(locFR));
		lent = gent.getEntries(locFR);
		tent = lent.getTerm(0);
		assertEquals("ent2-2", tent.getId());
		assertEquals("fr text2", tent.getText());
	}

	@Test
	public void testNoTerms () {
		String snippet = "<?xml version='1.0'?>"
			+ "<!DOCTYPE martif SYSTEM \"TBXcoreStructV02.dtd\">"
			+ "<martif type=\"TBX\" xml:lang=\"en\"><martifHeader><fileDesc><sourceDesc>"
			+ "<p>From an Oracle corporation termbase</p>"
			+ "</sourceDesc></fileDesc>"
			+ "<encodingDesc><p type=\"XCSURI\">http://www.lisa.org/fileadmin/standards/tbx/TBXXCSV02.XCS</p></encodingDesc>"
			+ "</martifHeader><text><body>"
			+ "</body></text></martif>";

		List<ConceptEntry> list = getConcepts(snippet, null);
		assertNotNull(list);
		assertEquals(0, list.size());
	}

	@Test
	public void testEncoding () {
		File file = new File(root+"test02_win1254.tbx");
		List<ConceptEntry> list = getConcepts(null, file);
		assertEquals(1, list.size());
		ConceptEntry cent = list.get(0);
		assertEquals("id1", cent.getId());
		assertEquals("term with: \u00e9\u00e1 and \u0130\u0131", cent.getEntries(locEN).getTerm(0).getText());
		assertEquals("tr term with: \u00e9\u00e1 and \u0130\u0131", cent.getEntries(locTR).getTerm(0).getText());
	}
	
	@Test
	public void testFromFiles () {
		File file = new File(root+"test01.tbx");
		List<ConceptEntry> list = getConcepts(null, file);
		assertEquals(1, list.size());
		assertEquals("eid-Oracle-67", list.get(0).getId());

		file = new File(root+"sdl_tbx.tbx");
		list = getConcepts(null, file);
		assertEquals(223, list.size());
		assertEquals("c228", list.get(list.size()-1).getId());

		file = new File(root+"ibm_tbx.tbx");
		list = getConcepts(null, file);
		assertEquals(5, list.size());
		assertEquals("c5", list.get(list.size()-1).getId());
		
		file = new File(root+"maryland.tbx");
		list = getConcepts(null, file);
		assertEquals(1, list.size());
		assertEquals("eid-VocCod-211.01", list.get(list.size()-1).getId());
		
		file = new File(root+"medtronic_TBX.tbx");
		list = getConcepts(null, file);
		assertEquals(3, list.size());
		assertEquals("c7333", list.get(list.size()-1).getId());
		
		file = new File(root+"oracle_TBX.tbx");
		list = getConcepts(null, file);
		assertEquals(2, list.size());
		assertEquals("c2", list.get(list.size()-1).getId());
	}

	// Comment out for SVN
//	@Test
//	public void testBigFile () {
//		File file = new File(root+"MicrosoftTermCollection_FR.tbx");
//		List<GlossaryEntry> list = getEntries(null, file);
//		assertNotNull(list);
//		GlossaryEntry gent = list.get(list.size()-1);
//		assertEquals(17133, list.size());
//		assertEquals("27766_1436100", gent.getId());
//	}

	// Use either snippet or file
	List<ConceptEntry> getConcepts (String snippet, File file) {
		try {
			ArrayList<ConceptEntry> list = new ArrayList<ConceptEntry>();
			
			//IGlossaryReader tbx = new TBXJaxbReader();
			IGlossaryReader tbx = new TBXReader();
			
			if ( file == null ) {
				InputStream is = new ByteArrayInputStream(snippet.getBytes("UTF-8"));
				tbx.open(is);
			}
			else {
				tbx.open(file);
			}
			while ( tbx.hasNext() ) {
				list.add(tbx.next());
			}
			tbx.close();
			return list;
		}
		catch ( Throwable e ) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
	
}
