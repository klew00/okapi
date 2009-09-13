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

package net.sf.okapi.lib.translation;

import java.util.List;

import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextFragment;

/**
 * Collection of helper method for preparing and querying translation resources.
 */
public class QueryUtil {

	private StringBuilder codesMarkers;
	private List<Code> codes;
	
	public QueryUtil () {
		codesMarkers = new StringBuilder();
	}
	
	/**
	 * Separate and store codes of a given text fragment.
	 * @param frag the fragment to process. Use {@link #appendCodesToText(TextFragment, String)}
	 * to reconstruct the text back with its codes at the end.
	 * @return the fragment content stripped of its codes.
	 */
	public String separateCodesFromText (TextFragment frag) {
		// Reset
		codesMarkers.setLength(0);
		codes = frag.getCodes();
		// Get coded text
		String text = frag.getCodedText();
		if ( !frag.hasCode() ) {
			return text; // No codes
		}
		// If there are codes: store them appart
		StringBuilder tmp = new StringBuilder();
		for ( int i=0; i<text.length(); i++ ) {
			switch ( text.charAt(i) ) {
			case TextFragment.MARKER_OPENING:
			case TextFragment.MARKER_CLOSING:
			case TextFragment.MARKER_ISOLATED:
			case TextFragment.MARKER_SEGMENT:
				codesMarkers.append(text.charAt(i));
				codesMarkers.append(text.charAt(++i));
				break;
			default:
				tmp.append(text.charAt(i));
			}
		}
		// Return text without codes
		return tmp.toString();
	}

	/**
	 * Appends the codes stored apart using {@link #separateCodesFromText(TextFragment)}
	 * at the end of a given plain text. The text fragment provided must be the same and
	 * without code modifications, as the one used for the splitting.
	 * @param plainText new text to use (must be plain)
	 * @return the provided fragment, but with the new text and the original codes
	 * appended at the end.
	 */
	public TextFragment createNewFragmentWithCodes (String plainText) {
		return new TextFragment(plainText + codesMarkers, codes);
	}
	
}
