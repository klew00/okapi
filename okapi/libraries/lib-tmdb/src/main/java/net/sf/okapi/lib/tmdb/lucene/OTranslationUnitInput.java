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

import java.util.ArrayList;
import java.util.List;

/**
 * All files in this package are based on the files by @author HaslamJD and @author HARGRAVEJE in the okapi-tm-pensieve project amd in most cases there are only minor changes.
 */

/**
 * Represents a Unit of Translation.
 */
public class OTranslationUnitInput extends OTranslationUnitBase {
	
	private List<OTranslationUnitVariant> variants = new ArrayList<OTranslationUnitVariant>();
	
	public OTranslationUnitInput(OField id) {
		super(id);
	}
	
	public OTranslationUnitInput(String idValue) {
		super(idValue);
	}
	
	public OTranslationUnitInput(OField id, OFields fields) {
		super(id, fields);
	}
	
	public OTranslationUnitInput(String idValue, OFields fields) {
		super(idValue, fields);
	}

	public List<OTranslationUnitVariant> getVariants(){
		return variants;
	}

	public void add(OTranslationUnitVariant tuv){
		variants.add(tuv);
	}
	
}
