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

package net.sf.okapi.lib.terminology;

import java.util.List;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.terminology.dummy.SimpleTBConnector;

import org.junit.Test;
import static org.junit.Assert.*;

public class GlossaryTest {

	private LocaleId locEN = LocaleId.ENGLISH;
	private LocaleId locFR = LocaleId.FRENCH;
	
	@Test
	public void testTermAccess () {
		GlossaryEntry gent = new GlossaryEntry();
		assertFalse(gent.hasLocale(locEN));
		gent.addTerm(locEN, "test-en");
		assertTrue(gent.hasLocale(locEN));
		assertEquals("test-en", gent.getEntries(locEN).getTerm(0).getText());
	}

	@Test
	public void testTBAccess () {
		SimpleTBConnector ta = new SimpleTBConnector();
		ta.open();
		ta.addEntry(locEN, "watch").addTerm(locFR, "montre");
		ta.addEntry(locEN, "time").addTerm(locFR, "temps");
		
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

//	private List<TermHit> getMissingTerms (LocaleId sourceLoc,
//		List<TermHit> sourceTerms,
//		LocaleId targetLoc,
//		List<TermHit> targetTerms)
//	{
//		for ( int s=0; s<sourceTerms.size(); s++ ) {
//			TermHit srcHit = sourceTerms.get(s);
//			
//		}
//		return null;
//	}
}
