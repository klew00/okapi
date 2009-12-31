/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

	public static String replaceAll(String string, String regex, int group, String replacement) {
	
		Pattern pattern = Pattern.compile(regex);
	    Matcher matcher = pattern.matcher(string);
	    
	    // Replace all occurrences of pattern in input
	    StringBuffer buf = new StringBuffer();
	    
	    int start = 0;
	    int end = 0;
	    
	    while (matcher.find()) {
	    
	        start = matcher.start(group);
	        buf.append(string.substring(end, start));
	        buf.append(replacement);
	        end = matcher.end(group);
	    }
	    
	    buf.append(string.substring(end));
	    return buf.toString();
	}
}
