/*===========================================================================
  Copyright (C) 2009-2010 by the Okapi Framework contributors
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.lib.terminology.GlossaryEntry;
import net.sf.okapi.lib.terminology.IGlossaryReader;
import net.sf.okapi.lib.terminology.LangEntry;
import net.sf.okapi.lib.terminology.TermEntry;

import org.junit.Test;
import static org.junit.Assert.*;

public class TBXReaderTest {

	private LocaleId locEN = LocaleId.ENGLISH;
	private LocaleId locFR = LocaleId.FRENCH;
	private LocaleId locHU = LocaleId.fromString("hu");
	
	@Test
	public void testSimpleTBX () {
		String snippet = "<?xml version='1.0'?>"
			+ "<!DOCTYPE martif SYSTEM \"TBXcoreStructV02.dtd\">"
			+ "<martif type=\"TBX\" xml:lang=\"en\"><martifHeader><fileDesc><sourceDesc>"
			+ "<p>From an Oracle corporation termbase</p>"
			+ "</sourceDesc></fileDesc>"
			+ "<encodingDesc><p type=\"XCSURI\">http://www.lisa.org/fileadmin/standards/tbx/TBXXCSV02.XCS</p></encodingDesc>"
			+ "</martifHeader><text><body>"
			
			+ "<termEntry id=\"eid-Oracle-67\">"
			+ "<descrip type=\"subjectField\">manufacturing</descrip>"
			+ "<descrip type=\"definition\">def text</descrip>"
			+ "<langSet xml:lang=\"en\">"
			+ "<tig>"
			+ "<term id=\"tid-Oracle-67-en1\">en text</term>"
			+ "<termNote type=\"partOfSpeech\">noun-en</termNote>"
			+ "</tig></langSet>"
			+ "<langSet xml:lang=\"hu\">"
			+ "<tig>"
			+ "<term id=\"tid-Oracle-67-hu1\">hu text</term>"
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

		List<GlossaryEntry> list = getEntries(snippet);
		assertNotNull(list);
		assertEquals(2, list.size());
		
		GlossaryEntry gent = list.get(0);
		assertTrue(gent.hasLocale(locEN));
		LangEntry lent = gent.getEntries(locEN);
		TermEntry tent = lent.getTerm(0);
		assertEquals("en text", tent.getText());
		
		assertTrue(gent.hasLocale(locHU));
		lent = gent.getEntries(locHU);
		tent = lent.getTerm(0);
		assertEquals("hu text", tent.getText());

		gent = list.get(1);
		assertTrue(gent.hasLocale(locFR));
		lent = gent.getEntries(locFR);
		tent = lent.getTerm(0);
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

		List<GlossaryEntry> list = getEntries(snippet);
		assertNotNull(list);
		assertEquals(0, list.size());
	}
	
	List<GlossaryEntry> getEntries (String snippet) {
		try {
			ArrayList<GlossaryEntry> list = new ArrayList<GlossaryEntry>();
			
			//IGlossaryReader tbx = new TBXJaxbReader();
			IGlossaryReader tbx = new TBXReader();
			
			InputStream is = new ByteArrayInputStream(snippet.getBytes("UTF-8"));
			tbx.open(is);
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
