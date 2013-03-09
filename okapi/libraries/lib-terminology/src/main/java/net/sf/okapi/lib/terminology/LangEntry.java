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

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.LocaleId;

public class LangEntry extends BaseEntry {
	
	private LocaleId locId;
	private List<TermEntry> terms;

	public LangEntry (LocaleId locId) {
		this.locId = locId;
		terms = new ArrayList<TermEntry>();
	}
	
	public LocaleId getLocale () {
		return locId;
	}

	public LangEntry (TermEntry term) {
		terms = new ArrayList<TermEntry>();
		terms.add(term);
	}
	
	public void addTerm (String term) {
		terms.add(new TermEntry(term));
	}
	
	public void addTerm (TermEntry term) {
		terms.add(term);
	}
	
	public boolean hasTerm () {
		return (terms.size() > 0);
	}

	public TermEntry getTerm (int index) {
		return terms.get(index);
	}

}
