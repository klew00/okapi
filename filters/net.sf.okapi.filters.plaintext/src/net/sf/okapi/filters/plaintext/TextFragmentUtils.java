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

package net.sf.okapi.filters.plaintext;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.skeleton.GenericSkeleton;

public class TextFragmentUtils {
	
	/**
	 * 
	 * @param textFragment
	 */
	public static void trimLeft(TextFragment textFragment) {
		trimLeft(textFragment, null);
	}
	
	/**
	 * 
	 * @param textFragment
	 * @param skel
	 */
	public static void trimLeft(TextFragment textFragment, GenericSkeleton skel) {
		
		if (Util.isEmpty(textFragment)) return;
		String st = textFragment.getCodedText();
		
		int pos = TextFragment.getFirstNonWhitespacePosition(st, 0, -1, true, true, true, true);
		if (pos == -1) return;
		
		TextFragment skelTF = textFragment.subSequence(0, pos);
		textFragment.setCodedText(st.substring(pos));
		
		if (Util.isEmpty(skel)) return;
		if (Util.isEmpty(skelTF)) return;
		
		skel.append(skelTF.toString());  // Codes get removed
	}
	
	/**
	 * 
	 * @param textFragment
	 */
	public static void trimRight(TextFragment textFragment) {
		trimRight(textFragment, null);
	}
	
	/**
	 * 
	 * @param textFragment
	 * @param skel
	 */
	public static void trimRight(TextFragment textFragment, GenericSkeleton skel) {
		
		if (Util.isEmpty(textFragment)) return;
		
		String st = textFragment.getCodedText();
		
		int pos = TextFragment.getLastNonWhitespacePosition(st, -1, 0, true, true, true, true);
		if (pos == -1) return;
		
		TextFragment skelTF = textFragment.subSequence(pos + 1, st.length());
		textFragment.setCodedText(st.substring(0, pos + 1));
		
		if (Util.isEmpty(skel)) return;
		if (Util.isEmpty(skelTF)) return;
		
		skel.append(skelTF.toString());  // Codes get removed);
	}

	/**
	 * 
	 * @param textFragment
	 * @return
	 */
	public static char getLastChar(TextFragment textFragment) {
		
		if (Util.isEmpty(textFragment)) return '\0';
		
		String st = textFragment.getCodedText();
		
		int pos = TextFragment.getLastNonWhitespacePosition(st, -1, 0, true, true, true, true);
		if (pos == -1) return '\0';
		
		return st.charAt(pos);
	}

	/**
	 * 
	 * @param textFragment
	 */
	public static void deleteLastChar(TextFragment textFragment) {
		
		if (Util.isEmpty(textFragment)) return;
		String st = textFragment.getCodedText();
		
		int pos = TextFragment.getLastNonWhitespacePosition(st, -1, 0, true, true, true, true);
		if (pos == -1) return;
		
		textFragment.remove(pos, pos + 1);
	}
		
	/**
	 * 
	 * @param textFragment
	 * @param findWhat
	 * @return
	 */
	public static int lastIndexOf(TextFragment textFragment, String findWhat) {
		
		if (Util.isEmpty(textFragment)) return -1;
		if (Util.isEmpty(findWhat)) return -1;
		if (Util.isEmpty(textFragment.getCodedText())) return -1;
		
		return (textFragment.getCodedText()).lastIndexOf(findWhat);
	}
		
		
}
