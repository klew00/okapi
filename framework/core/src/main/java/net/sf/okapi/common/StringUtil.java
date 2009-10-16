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

package net.sf.okapi.common;

import net.sf.okapi.common.Util;

/**
 * Helper methods to manupulate strings.
 */
public class StringUtil {

	// String formatting
	//TODO: javadoc
	public static String titleCase(String st) {
		if (Util.isEmpty(st)) 
			return st;
		
		return st.substring(0,1).toUpperCase() + st.substring(1);
	}
	
	//TODO: javadoc
	public static String removeQualifiers(String st, String qualifier) {
	
		if (Util.isEmpty(st) || Util.isEmpty(qualifier))
			return st;
		
		int qualifierLen = qualifier.length();
		
		if (st.startsWith(qualifier) && st.endsWith(qualifier))
			return st.substring(qualifierLen, Util.getLength(st) - qualifierLen);
			
		return st;
	}
	
	//TODO: javadoc
	public static String removeQualifiers(String st) {
	
		return removeQualifiers(st, "\""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	//TODO: javadoc
	public static String normalizeLineBreaks(String string) {
		
		String res = string;
		
		if (!Util.isEmpty(res)) {
		
			res = res.replaceAll("\r\r\n", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
			res = res.replace("\r\n", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
			res = res.replace("\r", "\n");  //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		return res;
	}

}
