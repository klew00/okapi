/*===========================================================================
  Copyright (C) 2012 by the Okapi Framework contributors
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

package net.sf.okapi.common.annotation;

import java.util.ArrayList;

/**
 * Simple annotation for storing terms and their associated information.
 */
public class TermsAnnotation implements IAnnotation {

	private ArrayList<String> terms;
	private ArrayList<String> infos;

	/**
	 * Creates a new empty TermsAnnotation object.
	 */
	public TermsAnnotation () {
		terms = new ArrayList<String>();
		infos = new ArrayList<String>();
	}

	/**
	 * Adds a term to the annotation.
	 * @param term the term to add.
	 * @param info the associated into (can be null).
	 */
	public void add (String term,
		String info)
	{
		terms.add(term);
		infos.add(info==null ? "" : info);
	}

	/**
	 * Gets the number of terms in this annotation.
	 * @return the number of terms in this annotation.
	 */
	public int size () {
		return terms.size();
	}
	
	/**
	 * Gets the term for a given index.
	 * @param index the index of the term to retrieve.
	 * @return the term for a given index.
	 */
	public String getTerm (int index) {
		return terms.get(index);
	}
	
	/**
	 * Gets the term information for a given index,
	 * or an empty string if there is not associated information for the term.
	 * @param index the index of the term information to retrieve.
	 * @return the term information for a given index.
	 */
	public String getInfo (int index) {
		return infos.get(index);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for ( int i=0; i<terms.size(); i++ ) {
			sb.append(terms.get(i));
			if ( !infos.get(i).isEmpty() ) {
				sb.append(" ["+infos.get(i)+"]");
			}
			sb.append(";\n");
		}
		return sb.toString();
	}
}
