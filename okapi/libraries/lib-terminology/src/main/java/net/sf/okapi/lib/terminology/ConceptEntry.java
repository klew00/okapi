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

import java.util.HashMap;
import java.util.Map;

import net.sf.okapi.common.LocaleId;

public class ConceptEntry extends BaseEntry {
	
	private String id;
	private Map<LocaleId, LangEntry> langs;

	/**
	 * Creates a new ConceptEntry object.
	 */
	public ConceptEntry () {
		langs = new HashMap<LocaleId, LangEntry>();
	}
	
	/**
	 * Gets the ID for this glossary entry.
	 * @return the ID for this glossary entry.
	 */
	public String getId () {
		return id;
	}
	
	/**
	 * Sets the ID for this glossary entry.
	 * @param id the ID for this glossary entry.
	 */
	public void setId (String id) {
		this.id = id;
	}

	/**
	 * Indicates if there is a set of terms defined for a given locale.
	 * @param locId the locale to query.
	 * @return true if there is a set of terms defined for the given locale.
	 */
	public boolean hasLocale (LocaleId locId) {
		return (langs.get(locId) != null);
	}

	public LangEntry getEntries (LocaleId locId) {
		return langs.get(locId);
	}

	public void addTerm (LocaleId locId,
		String term)
	{
		// Get the existing language entry if possible
		LangEntry lent = getEntries(locId);
		// Create one if there is none yet
		if ( lent == null ) {
			lent = new LangEntry(locId); 
			langs.put(locId, lent);
		}
		// Add the term
		lent.addTerm(term);
	}

	public void addLangEntry (LangEntry lent) {
		langs.put(lent.getLocale(), lent);
	}
	
	public void removeEntries (LocaleId locId) {
		langs.remove(locId);
	}

}
