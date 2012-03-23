/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.lib.tmdb.lucene;

/**
 * All files in this package are based on the files by @author HaslamJD and @author HARGRAVEJE in the okapi-tm-pensieve project amd in most cases there are only minor changes.
 */

/**
 * Represents a Unit of Translation.
 */
public class OTranslationUnitResult extends OTranslationUnitBase{

	private OTranslationUnitVariant result;
	
	public OTranslationUnitResult(OField id, OTranslationUnitVariant result) {
		super(id);
		this.result = result;
	}
	
	public OTranslationUnitResult(String idValue, OTranslationUnitVariant result) {
		super(idValue);
		this.result = result;
	}
	
	public OTranslationUnitResult(OField id, OFields fields, OTranslationUnitVariant result) {
		super(id, fields);
		this.result = result;
	}
	
	public OTranslationUnitResult(String idValue, OFields fields, OTranslationUnitVariant result) {
		super(idValue, fields);
		this.result = result;
	}

	public OTranslationUnitVariant getResult() {
		return result;
	}
	
	public void setResult(OTranslationUnitVariant result) {
		this.result = result;
	}

	/**
	 * Checks to see if the the result is empty
	 * 
	 * @return true if the result is empty
	 */
	public boolean isResultEmpty() {
		return isFragmentEmpty(result);
	}

	private static boolean isFragmentEmpty(OTranslationUnitVariant frag) {
		return (frag == null || frag.getContent() == null || frag.getContent().isEmpty());
	}

	@Override
	public String toString() {
		return getResult().getContent().toText();

	}
}
