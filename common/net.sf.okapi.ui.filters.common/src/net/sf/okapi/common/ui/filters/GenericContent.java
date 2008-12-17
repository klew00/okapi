/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel                                          */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.common.ui.filters;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.graphics.Point;

import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.InvalidContentException;
import net.sf.okapi.common.resource.InvalidPositionException;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;

/**
 * Handles the conversion between a coded text object and a generic markup string.
 */
public class GenericContent {

	private String      codedText;
	private List<Code>  codes;
	private Pattern     patternOpening = Pattern.compile("\\<(\\d*?)\\>");
	private Pattern     patternClosing = Pattern.compile("\\</(\\d*?)\\>");
	private Pattern     patternIsolated = Pattern.compile("\\<(\\d*?)/\\>");
	

	public GenericContent () {
		codedText = "";
	}
	
	public GenericContent (TextFragment content) {
		setContent(content);
	}
	
	public GenericContent setContent (TextFragment content) {
		codedText = content.getCodedText();
		codes = content.getCodes();
		return this;
	}

	/**
	 * Prints a generic string representation of a given segmented text, with optional
	 * markers to indicate the segments boundaries.
	 * @param container The container to output.
	 * @param showSegments True if segment boundaries should be shown.
	 * @return A string with the segmented text output.
	 */
	public String printSegmentedContent (TextContainer container,
		boolean showSegments)
	{
		return printSegmentedContent(container, showSegments, false);
	}
	
	/**
	 * Prints a string representation of a given segmented text, with optional
	 * markers to indicate the segments boundaries.
	 * @param container The container to output.
	 * @param showSegments True if segment boundaries should be shown.
	 * @param normalText True to show in-line real data instead of generic codes.
	 * @return A string with the segmented text output.
	 */
	public String printSegmentedContent (TextContainer container,
		boolean showSegments,
		boolean normalText)
	{
		if ( !container.isSegmented() ) {
			return setContent(container).toString();
		}
		//TODO: the getCodedText get the codes from the wrong textFragment.
		Code code;
		String text = container.getCodedText();
		StringBuilder tmp = new StringBuilder();
		for ( int i=0; i<text.length(); i++ ) {
			switch ( text.charAt(i) ) {
			case TextFragment.MARKER_OPENING:
				code = container.getCode(text.charAt(++i));
				if ( normalText ) tmp.append(code.toString());
				else tmp.append(String.format("<%d>", code.getId()));
				break;
			case TextFragment.MARKER_CLOSING:
				code = container.getCode(text.charAt(++i));
				if ( normalText ) tmp.append(code.toString());
				else tmp.append(String.format("</%d>", code.getId()));
				break;
			case TextFragment.MARKER_ISOLATED:
				code = container.getCode(text.charAt(++i));
				if ( normalText ) tmp.append(code.toString());
				else tmp.append(String.format("<%d/>", code.getId()));
				break;
			case TextFragment.MARKER_SEGMENT:
				code = container.getCode(text.charAt(++i));
				int index = Integer.parseInt(code.getData());
				if ( showSegments ) tmp.append("[");
				tmp.append(setContent(container.getSegments().get(index)).toString(normalText));
				if ( showSegments ) tmp.append("]");
				break;
			default:
				tmp.append(text.charAt(i));
				break;
			}
		}
		return tmp.toString();
	}
	
	/**
	 * Generates an generic coded string from the content.
	 * @return The generic string.
	 */
	@Override
	public String toString () {
		return toString(false);
	}
	
	/**
	 * Generates a generic coded string or an normal output from the content.
	 * @param normalText True to show in-line real data instead of generic codes.
	 * @return The output string.
	 */
	public String toString (boolean normalText)
	{
		StringBuilder tmp = new StringBuilder();
		int index;
		for ( int i=0; i<codedText.length(); i++ ) {
			switch ( codedText.codePointAt(i) ) {
			case TextFragment.MARKER_OPENING:
				index = TextFragment.toIndex(codedText.charAt(++i));
				if ( normalText ) tmp.append(codes.get(index).toString());
				else tmp.append(String.format("<%d>", codes.get(index).getId()));
				break;
			case TextFragment.MARKER_CLOSING:
				index = TextFragment.toIndex(codedText.charAt(++i));
				if ( normalText ) tmp.append(codes.get(index).toString());
				else tmp.append(String.format("</%d>", codes.get(index).getId()));
				break;
			case TextFragment.MARKER_ISOLATED:
			case TextFragment.MARKER_SEGMENT:
				index = TextFragment.toIndex(codedText.charAt(++i));
				if ( normalText ) tmp.append(codes.get(index).toString());
				else tmp.append(String.format("<%d/>", codes.get(index).getId()));
				break;
			default:
				tmp.append(codedText.charAt(i));
				break;
			}
		}
		return tmp.toString();
	}

	/**
	 * Gets the matching position in the coded text string of a given 
	 * position in the generic text output.
	 * @param position Generic text position to convert to coded text position.
	 * @return Calculated coded text position.
	 */
	public Point getCodedTextPosition (Point position) {
		Point result = new Point(0, 0);
		int genericPos = 0;
		int codedPos = 0;
		int index;
		for ( int i=0; i<codedText.length(); i++ ) {
			switch ( codedText.codePointAt(i) ) {
			case TextFragment.MARKER_OPENING:
				index = TextFragment.toIndex(codedText.charAt(++i));
				genericPos += String.format("<%d>", codes.get(index).getId()).length();
				codedPos += 2;
				break;
			case TextFragment.MARKER_CLOSING:
				index = TextFragment.toIndex(codedText.charAt(++i));
				genericPos += String.format("</%d>", codes.get(index).getId()).length();
				codedPos += 2;
				break;
			case TextFragment.MARKER_ISOLATED:
			case TextFragment.MARKER_SEGMENT:
				index = TextFragment.toIndex(codedText.charAt(++i));
				genericPos += String.format("<%d/>", codes.get(index).getId()).length();
				codedPos += 2;
				break;
			default:
				genericPos++;
				codedPos++;
				break;
			}
			if ( genericPos == position.x ) {
				result.x = codedPos;
				if ( position.x == position.y ) {
					result.y = result.x;
					return result;
				}
			}
			if ( genericPos == position.y ) {
				result.y = codedPos;
				return result;
			}
		}
		// Else: out-of-bounds or within an in-line code
		throw new InvalidPositionException (
			String.format("Position %d or %d is invalid.", position.x, position.y));
	}
	
	/**
	 * Updates a text fragment from a generic representation.
	 * @param genericText The generic text to use to update the fragment.
	 * @param fragment The text fragment to update.
	 * @param allowCodeDeletion True when missing in-line codes in the generic text
	 * means the corresponding codes should be deleted from the fragment.
	 * @throws InvalidContentException When the generic text is not valid, or does
	 * not correspond to the existing codes.
	 */
	public void updateFragment (String genericText,
		TextFragment fragment,
		boolean allowCodeDeletion)
	{
		if ( genericText == null )
			throw new NullPointerException("Parameter genericText is null");

		// Case with no in-line codes
		if ( !fragment.hasCode() && ( genericText.indexOf('<') == -1 )) {
			fragment.setCodedText(genericText);
			return;
		}
		
		// Otherwise: we have in-line codes
		StringBuilder tmp = new StringBuilder(genericText);
		
		int n;
		int start = 0;
		int diff = 0;
		int index;
		Matcher m = patternOpening.matcher(genericText);
		while ( m.find(start) ) {
			n = m.start();
			index = fragment.getIndex(Integer.valueOf(m.group(1)));
			if ( index == -1 )
				throw new InvalidContentException(String.format("Invalid code: '%s'", m.group()));
			tmp.replace(n+diff, (n+diff)+m.group().length(), String.format("%c%c",
				(char)TextFragment.MARKER_OPENING, TextFragment.toChar(index)));
			diff += (2-m.group().length());
			start = n+m.group().length();
		}
		start = diff = 0;
		m = patternClosing.matcher(tmp.toString());
		while ( m.find(start) ) {
			n = m.start();
			index = fragment.getIndex(Integer.valueOf(m.group(1)));
			if ( index == -1 )
				throw new InvalidContentException(String.format("Invalid code: '%s'", m.group()));
			tmp.replace(n+diff, (n+diff)+m.group().length(), String.format("%c%c",
				(char)TextFragment.MARKER_ISOLATED, TextFragment.toChar(index)));
			diff += (2-m.group().length());
			start = n+m.group().length();
		}
		start = diff = 0;
		m = patternIsolated.matcher(tmp.toString());
		while ( m.find(start) ) {
			n = m.start();
			index = fragment.getIndex(Integer.valueOf(m.group(1)));
			if ( index == -1 )
				throw new InvalidContentException(String.format("Invalid code: '%s'", m.group()));
			tmp.replace(n+diff, (n+diff)+m.group().length(), String.format("%c%c",
				(char)TextFragment.MARKER_ISOLATED, TextFragment.toChar(index)));
			diff += (2-m.group().length());
			start = n+m.group().length();
		}
		
		// Allow deletion of codes
		fragment.setCodedText(tmp.toString(), allowCodeDeletion);
	}
}
