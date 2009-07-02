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

package net.sf.okapi.common.utils;

import net.sf.okapi.common.Util;

public class Util2 {

// Conversion
	public static String intToStr(int intValue) {
		
		return String.valueOf(intValue);
	}
	
	public static int strToInt(String stringValue, int intDefault) {
	
		if (Util.isEmpty(stringValue))
			return intDefault;
		
		try {
			return Integer.valueOf(stringValue);
		}
		catch (NumberFormatException e) {
			
			return intDefault; 
		}
		
	}

// Arrays
	
	public static <T> T get(T [] array, int index) {
		
		if (index >= 0 && index < array.length)
			return array[index];
		else
			return null;
	}
	
// Flags	
	public static boolean checkFlag(int intValue, int flag) {
		
		return (intValue & flag) == flag;
	}
	
}
