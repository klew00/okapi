/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package net.sf.okapi.lib.tmdb;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextFragment;

public class DbUtil {
	
	public static final String TEXT_PREFIX = "Text_";

	private static GenericContent fmt = new GenericContent();
	
	public static String[] fragmentToTmFields (TextFragment frag) {
		String[] res = new String[2];
		res[0] = fmt.setContent(frag).toString();
		res[1] = Code.codesToString(frag.getCodes());
		return res;
	}
	
	public static String toDbLang (LocaleId locId) {
		String tmp = locId.toString();
		return tmp.toUpperCase().replace('-', '_');
	}
}
