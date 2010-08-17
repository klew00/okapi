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

package net.sf.okapi.lib.terminology.simpletb;

import java.util.List;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.terminology.ConceptEntry;
import net.sf.okapi.lib.terminology.ITermAccess;
import net.sf.okapi.lib.terminology.TermHit;

public class SimpleTBConnector implements ITermAccess {

	private SimpleTB tb;
	
	@Override
	public IParameters getParameters () {
		// Nothing to do
		return null;
	}

	@Override
	public void setParameters (IParameters params) {
		// Nothing to do
	}

	@Override
	public void open () {
		tb = new SimpleTB(null);
	}

	@Override
	public void close() {
		// Nothing to do
	}

	@Override
	public List<TermHit> getExistingTerms (TextFragment fragment,
		LocaleId sourceLocId,
		LocaleId targetLocId)
	{
		return tb.getExistingTerms(fragment, sourceLocId, targetLocId);
	}
	
	public ConceptEntry addEntry (LocaleId locId,
		String term)
	{
		return tb.addEntry(locId, term);
	}

//	public List<TermHit> getMissingTerms (TextFragment fragment,
//		List<TermHit> termsToCheck)
//	{
//
//		String text = fragment.getCodedText();
//		List<String> parts = Arrays.asList(text.split("\\s"));
//		List<TermHit> res = new ArrayList<TermHit>();
//	
//		for ( TermHit th : termsToCheck ) {
//			String term = th.targetTerm.getText();
//			if ( !parts.contains(term) ) {
//				res.add(th);
//			}
//		}
//		return res;
//	}

}
