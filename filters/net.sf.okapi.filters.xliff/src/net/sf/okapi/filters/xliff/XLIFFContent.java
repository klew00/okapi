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

package net.sf.okapi.filters.xliff;

import java.util.List;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;

/**
 * Handles the conversion between a coded text object and XLIFF.
 */
public class XLIFFContent {

	private String codedText;
	private List<Code> codes;
	private XLIFFContent innerContent;
	
	public XLIFFContent () {
		codedText = "";
	}
	
	public XLIFFContent (TextFragment content) {
		setContent(content);
	}
	
	public XLIFFContent setContent (TextFragment content) {
		codedText = content.getCodedText();
		codes = content.getCodes();
		return this;
	}
	
	@Override
	public String toString () {
		return toString(1, true, false);
	}

	/**
	 * Generates an XLIFF string from the content.
	 * @param quoteMode 0=no quote escaped, 1=apos and quot, 2=#39 and quot,
	 * and 3=quot only.
	 * @param escapeGT True to always escape '>' to gt
	 * @param codeOnlyMode True when the in-line codes are to be set as raw-values.
	 * This option is to be used when the in-line code is an XLIFF-in-line code itself.
	 * @return The coded string.
	 */
	public String toString (int quoteMode,
		boolean escapeGT,
		boolean codeOnlyMode)
	{
		StringBuilder tmp = new StringBuilder();
		int index;
		Code code;
		for ( int i=0; i<codedText.length(); i++ ) {
			switch ( codedText.codePointAt(i) ) {
			case TextFragment.MARKER_OPENING:
				index = TextFragment.toIndex(codedText.charAt(++i));
				code = codes.get(index);
				if ( codeOnlyMode ) {
					tmp.append(code.toString());
				}
				else {
					if ( code.hasData() ) {
						tmp.append(String.format("<bpt id=\"%d\">", code.getId()));
						tmp.append(Util.escapeToXML(code.toString(), quoteMode, escapeGT));
						tmp.append("</bpt>");
					}
					if ( code.hasAnnotation("protected") ) {
						tmp.append("<mrk mtype=\"protected\">");
					}
				}
				break;
			case TextFragment.MARKER_CLOSING:
				index = TextFragment.toIndex(codedText.charAt(++i));
				code = codes.get(index);
				if ( codeOnlyMode ) {
					tmp.append(code.toString());
				}
				else {
					if ( code.hasData() ) {
						tmp.append(String.format("<ept id=\"%d\">", code.getId()));
						tmp.append(Util.escapeToXML(code.toString(), quoteMode, escapeGT));
						tmp.append("</ept>");
					}
					if ( code.hasAnnotation("protected") ) {
						tmp.append("</mrk>");
					}
				}
				break;
			case TextFragment.MARKER_ISOLATED:
			case TextFragment.MARKER_SEGMENT:
				index = TextFragment.toIndex(codedText.charAt(++i));
				code = codes.get(index);
				if ( codeOnlyMode ) {
					tmp.append(code.toString());
				}
				else {
					if ( code.hasData() ) {
						tmp.append(String.format("<ph id=\"%d\">", code.getId()));
						tmp.append(Util.escapeToXML(code.toString(), quoteMode, escapeGT));
						tmp.append("</ph>");
					}
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
	
	public String toSegmentedString (TextContainer container,
		int quoteMode,
		boolean escapeGT,
		boolean withMarkers)
	{
		codedText = container.getCodedText();
		codes = container.getCodes();
		StringBuilder tmp = new StringBuilder();
		int index;
		Code code;
		for ( int i=0; i<codedText.length(); i++ ) {
			switch ( codedText.codePointAt(i) ) {
			case TextFragment.MARKER_ISOLATED:
			case TextFragment.MARKER_SEGMENT:
				index = TextFragment.toIndex(codedText.charAt(++i));
				code = codes.get(index);
				if ( code.getType().equals(TextContainer.CODETYPE_SEGMENT) ) {
					if ( withMarkers ) tmp.append("<mrk mtype=\"seg\">"); //TODO: mid attribute
					if ( innerContent == null ) {
						innerContent = new XLIFFContent();
					}
					index = Integer.valueOf(code.getData());
					innerContent.setContent(container.getSegments().get(index));
					tmp.append(innerContent.toString(quoteMode, escapeGT, false));
					if ( withMarkers ) tmp.append("</mrk>");
				}
				else {
					tmp.append(String.format("<ph id=\"%d\">", code.getId()));
					tmp.append(Util.escapeToXML(code.toString(),
						quoteMode, escapeGT));
					tmp.append("</ph>");
				}
				break;
			case TextFragment.MARKER_OPENING:
				index = TextFragment.toIndex(codedText.charAt(++i));
				code = codes.get(index);
				if ( code.hasData() ) {
					tmp.append(String.format("<bpt id=\"%d\">", code.getId()));
					tmp.append(Util.escapeToXML(code.toString(),
						quoteMode, escapeGT));
					tmp.append("</bpt>");
				}
				if ( code.hasAnnotation("protected") ) {
					tmp.append("<mrk mtype=\"protected\">");
				}
				break;
			case TextFragment.MARKER_CLOSING:
				index = TextFragment.toIndex(codedText.charAt(++i));
				code = codes.get(index);
				if ( code.hasData() ) {
					tmp.append(String.format("<ept id=\"%d\">", code.getId()));
					tmp.append(Util.escapeToXML(code.toString(),
						quoteMode, escapeGT));
					tmp.append("</ept>");
				}
				if ( code.hasAnnotation("protected") ) {
					tmp.append("</mrk>");
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
