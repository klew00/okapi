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

public class Entry implements Comparable<Entry> {

	String srcTerm;
	String trgTerm;
	String definition;

	public Entry (String srcTerm) {
		this.srcTerm = srcTerm;
	}

	@Override
	public String toString() {
		return srcTerm + ":" + trgTerm;
	}
	public void setSourceTerm (String term) {
		srcTerm = term;
	}

	public String getSourceTerm () {
		return srcTerm;
	}
	
	public void setTargetTerm (String term) {
		trgTerm = term;
	}

	public String getTargetTerm () {
		return trgTerm;
	}
	
	public void setdefinition (String definition) {
		this.definition = definition;
	}

	public String getDefinition () {
		return definition;
	}

	@Override
	/**
	 * This method compare by length then by character, always in reverse order.
	 */
	public int compareTo (Entry other) {
		if ( srcTerm.length() > other.srcTerm.length() ) return -1;
		if ( srcTerm.length() == other.srcTerm.length() ) {
			return other.srcTerm.compareTo(srcTerm);
		}
		return 1;
	}
	
}
