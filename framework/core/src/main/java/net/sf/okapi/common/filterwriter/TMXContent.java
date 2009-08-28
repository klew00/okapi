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

package net.sf.okapi.common.filterwriter;

import java.util.List;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextFragment;

/**
 * Handles the conversion between a coded text object and TMX.
 */
public class TMXContent {

	private String codedText;
	private List<Code> codes;
	private boolean withTradosWorkarounds = false;
	private int defaultQuoteMode = 1;

	/**
	 * Creates a new TMXContent object without any content.
	 */
	public TMXContent () {
		codedText = "";
	}
	
	/**
	 * Creates a new TMXContent object and set its content to the given fragment.
	 * @param content The TextFragment object to format.
	 */
	public TMXContent (TextFragment content) {
		setContent(content);
	}

	/**
	 * Sets the flag that indicates if the TMX generated should use workarounds so the
	 * output can be read in some versions of Trados Translators' Workbench that have
	 * bugs leading to the lose of data.
	 * @param value True to use workarounds, false to not use workarounds.
	 */
	public void setTradosWorkarounds (boolean value) {
		withTradosWorkarounds = value;
	}
	
	/**
	 * Sets the default quote mode. This value is used when using {@link #toString()}
	 * instead of {@link #toString(int, boolean)}.
	 * @param quoteMode 0=no quote escaped, 1=apos and quot, 2=#39 and quot,
	 * and 3=quot only.
	 */
	public void setQuoteMode (int quoteMode) {
		defaultQuoteMode = quoteMode;
	}
	
	/**
	 * Sets the fragment to format.
	 * @param content The TextFragment object to format.
	 * @return Itself
	 */
	public TMXContent setContent (TextFragment content) {
		codedText = content.getCodedText();
		codes = content.getCodes();
		return this;
	}
	
	/**
	 * Generates a TMX string from the content.
	 * This is the same as calling this.toString(quoteMode, true),
	 * where quoteMode is the value set by {@link #setQuoteMode(int)} or 1 by default.
	 * @return The string formatted in TMX.
	 */
	@Override
	public String toString () {
		return toString(defaultQuoteMode, true);
	}

	/**
	 * Generates a TMX string from the content.
	 * @param quoteMode 0=no quote escaped, 1=apos and quot, 2=#39 and quot,
	 * and 3=quot only.
	 * @param escapeGT True to always escape '>' to gt.
	 * @return The string formatted in TMX.
	 */
	public String toString (int quoteMode,
		boolean escapeGT)
	{
		StringBuilder tmp = new StringBuilder();
		int index;
		int id;
		Code code;
		for ( int i=0; i<codedText.length(); i++ ) {
			//TODO: output attribute 'type' whenever possible
			switch ( codedText.codePointAt(i) ) {
			case TextFragment.MARKER_OPENING:
				index = TextFragment.toIndex(codedText.charAt(++i));
				code = codes.get(index);
				if ( code.hasAnnotation("protected") ) {
					tmp.append("<hi type=\"protected\">");
				}
				//if ( code.hasData() ) {
					tmp.append(String.format("<bpt i=\"%d\">", code.getId()));
					tmp.append(Util.escapeToXML(codes.get(index).toString(), quoteMode, escapeGT, null));
					tmp.append("</bpt>");
				//}
				break;
			case TextFragment.MARKER_CLOSING:
				index = TextFragment.toIndex(codedText.charAt(++i));
				code = codes.get(index);
				//if ( code.hasData() ) {
					tmp.append(String.format("<ept i=\"%d\">", code.getId()));
					tmp.append(Util.escapeToXML(codes.get(index).toString(), quoteMode, escapeGT, null));
					tmp.append("</ept>");
				//}
				if ( code.hasAnnotation("protected") ) {
					tmp.append("</hi>");
				}
				break;
			case TextFragment.MARKER_ISOLATED:
			case TextFragment.MARKER_SEGMENT:
				index = TextFragment.toIndex(codedText.charAt(++i));
				code = codes.get(index);
				id = code.getId();
				// Use <ph> or <it> depending on underlying tagType
				switch ( code.getTagType() ) {
				case PLACEHOLDER:
					if ( withTradosWorkarounds
						&& ((code.getData().indexOf('{') != -1 )
							|| (code.getData().indexOf('}') != -1 )
							|| (code.getData().indexOf('\\') != -1 )))
					{
						tmp.append("<ut>{\\cs6\\f1\\cf6\\lang1024 </ut>");
						tmp.append(Util.escapeToXML(code.toString(), quoteMode, escapeGT, null));
						tmp.append("<ut>}</ut>");
					}
					else {
						tmp.append(String.format("<ph x=\"%d\">", id));
						tmp.append(Util.escapeToXML(code.toString(), quoteMode, escapeGT, null));
						tmp.append("</ph>");
					}
					break;
				case OPENING:
					tmp.append(String.format("<it x=\"%d\" pos=\"begin\">", id));
					tmp.append(Util.escapeToXML(code.toString(), quoteMode, escapeGT, null));
					tmp.append("</it>");
					break;
				case CLOSING:
					tmp.append(String.format("<it x=\"%d\" pos=\"end\">", id));
					tmp.append(Util.escapeToXML(code.toString(), quoteMode, escapeGT, null));
					tmp.append("</it>");
					break;
				case SEGMENTHOLDER: // Should not really be used
					tmp.append(String.format("<ph x=\"%d\">", id));
					tmp.append(Util.escapeToXML(code.toString(), quoteMode, escapeGT, null));
					tmp.append("</ph>");
					break;
				}
				
				break;
			case '>':
				if ( escapeGT ) tmp.append("&gt;");
				else {
					if (( i > 0 ) && ( codedText.charAt(i-1) == ']' )) 
						tmp.append("&gt;");
					else
						tmp.append('>');
				}
				break;
			case '<':
				tmp.append("&lt;");
				break;
			case '&':
				tmp.append("&amp;");
				break;
			case '"':
				if ( quoteMode > 0 ) tmp.append("&quot;");
				else tmp.append('"');
				break;
			case '\'':
				switch ( quoteMode ) {
				case 1:
					tmp.append("&apos;");
					break;
				case 2:
					tmp.append("&#39;");
					break;
				default:
					tmp.append(codedText.charAt(i));
					break;
				}
				break;
			default:
				tmp.append(codedText.charAt(i));
				break;
			}
		}
		return tmp.toString();
	}
}
