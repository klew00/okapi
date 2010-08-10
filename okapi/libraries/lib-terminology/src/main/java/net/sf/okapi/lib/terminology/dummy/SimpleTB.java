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

package net.sf.okapi.lib.terminology.dummy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.terminology.GlossaryEntry;
import net.sf.okapi.lib.terminology.LangEntry;
import net.sf.okapi.lib.terminology.TermHit;

/**
 * Very basic memory-only simple termbase.
 * This is used for prototyping the terminology interface.
 */
public class SimpleTB {
	
	private List<GlossaryEntry> entries;
	
	
	public SimpleTB () {
		entries = new ArrayList<GlossaryEntry>();
		createTerms();
	}
	
	private void createTerms () {
		LocaleId srcLoc = LocaleId.ENGLISH;
		LocaleId trgLoc = LocaleId.FRENCH;
		
		createEntry(srcLoc, "watch").addTerm(trgLoc, "montre");
		createEntry(srcLoc, "scale").addTerm(trgLoc, "balance");
		createEntry(srcLoc, "weather").addTerm(trgLoc, "temps");
		createEntry(srcLoc, "time").addTerm(trgLoc, "temps");
		createEntry(srcLoc, "channel").addTerm(trgLoc, "chaine");
	}
	
	public GlossaryEntry createEntry (LocaleId locId,
		String term)
	{
		GlossaryEntry gent = new GlossaryEntry();
		gent.addTerm(locId, term);
		entries.add(gent);
		return gent;
	}

	/*
	 * Very crude implementation of the search terms function.
	 */
	public List<TermHit> getExistingTerms (TextFragment frag,
		LocaleId srcLoc,
		LocaleId trgLoc)
	{
		String text = frag.getCodedText();
		List<String> parts = Arrays.asList(text.split("\\s"));
		
		List<TermHit> res = new ArrayList<TermHit>();
	
		for ( GlossaryEntry gent : entries ) {
			LangEntry srcLent = gent.getEntries(srcLoc);
			if ( srcLent == null ) continue;
			LangEntry trgLent = gent.getEntries(trgLoc);
			if ( trgLent == null ) continue;
			if ( !srcLent.hasTerm() || !trgLent.hasTerm() ) continue;
			
			String term = srcLent.getTerm(0).getText();
			if ( parts.contains(term) ) {
				TermHit th = new TermHit();
				th.sourceTerm = srcLent.getTerm(0);
				th.targetTerm = trgLent.getTerm(0);
				res.add(th);
			}
		}
		
		return res;
	}

	
}
