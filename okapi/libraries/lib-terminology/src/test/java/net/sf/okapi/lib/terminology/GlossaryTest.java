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

package net.sf.okapi.lib.terminology;

import java.util.List;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.terminology.simpletb.Parameters;
import net.sf.okapi.lib.terminology.simpletb.SimpleTBConnector;

import org.junit.Test;
import static org.junit.Assert.*;

public class GlossaryTest {

	private LocaleId locEN = LocaleId.ENGLISH;
	private LocaleId locFR = LocaleId.FRENCH;
	
	@Test
	public void testTermAccess () {
		ConceptEntry cent = new ConceptEntry();
		assertFalse(cent.hasLocale(locEN));
		cent.addTerm(locEN, "test-en");
		assertTrue(cent.hasLocale(locEN));
		assertEquals("test-en", cent.getEntries(locEN).getTerm(0).getText());
		
		cent.removeEntries(locFR); // No effect;
		assertTrue(cent.hasLocale(locEN));
		cent.removeEntries(locEN); // Remove
		assertFalse(cent.hasLocale(locEN));
	}

	@Test
	public void testTBAccess () {
		SimpleTBConnector ta = new SimpleTBConnector();
		Parameters params = (Parameters)ta.getParameters();
		params.setSourceLocale(locEN);
		params.setTargetLocale(locFR);
		ta.open();
		ta.addEntry("watch", "montre");
		ta.addEntry("time", "temps");
		
		TextFragment srcFrag = new TextFragment("This watch shows a time");
		List<TermHit> found1 = ta.getExistingTerms(srcFrag, locEN, locFR);
		assertEquals(2, found1.size());
		assertEquals("watch", found1.get(0).sourceTerm.getText());
		assertEquals("time", found1.get(1).sourceTerm.getText());
		
		TextFragment trgFrag = new TextFragment("Cette montre marque une heure");
		List<TermHit> found2 = ta.getExistingTerms(trgFrag, locFR, locEN);
		assertEquals(1, found2.size());
		assertEquals("montre", found2.get(0).sourceTerm.getText());

		ta.close();
	}
	
	@Test
	public void testGetExistingStrings () {
		SimpleTBConnector ta = new SimpleTBConnector();
		Parameters params = (Parameters)ta.getParameters();
		params.setSourceLocale(locEN);
		params.setTargetLocale(locFR);
		ta.open();
		ta.addEntry("src", "trg");
		ta.addEntry("src2", "trg2");
		ta.addEntry("Src1 src2", "Trg1 trg2");
		ta.addEntry("src5", "trg5");
		ta.initializeSearch(true, false);
		
		TextFragment srcFrag = new TextFragment(">src2< and Src1 src2. Also: WithiWordsrcWord");
		List<TermHit> found1 = ta.getExistingStrings(srcFrag, locEN, locFR);
		assertEquals(2, found1.size());
		assertEquals("Src1 src2", found1.get(0).sourceTerm.getText());
		assertEquals("src2", found1.get(1).sourceTerm.getText());
		
	}

}
