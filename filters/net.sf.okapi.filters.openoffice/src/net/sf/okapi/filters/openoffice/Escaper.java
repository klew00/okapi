/*===========================================================================*/
/* Copyright (C) 2008 by the Okapi Framework contributors                    */
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

package net.sf.okapi.filters.openoffice;

import java.util.List;

import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextFragment;

public class Escaper {

	String codedText;
	private List<Code> codes;
	
	public String escape (TextFragment fragment,
		boolean escapeDQ)
	{
		codedText = fragment.getCodedText();
		codes = fragment.getCodes();
		StringBuilder tmp = new StringBuilder();
		int index;
		for ( int i=0; i<codedText.length(); i++ ) {
			switch ( codedText.codePointAt(i) ) {
			case TextFragment.MARKER_OPENING:
			case TextFragment.MARKER_CLOSING:
			case TextFragment.MARKER_ISOLATED:
			case TextFragment.MARKER_SEGMENT:
				index = TextFragment.toIndex(codedText.charAt(++i));
				tmp.append(codes.get(index).toString());
				break;
			case '>':
				if (( i > 0 ) && ( codedText.charAt(i-1) == ']' )) 
					tmp.append("&gt;");
				else
					tmp.append('>');
				break;
			case '<':
				tmp.append("&lt;");
				break;
			case '&':
				tmp.append("&amp;");
				break;
			case '"':
				if ( escapeDQ ) tmp.append("&quot;");
				else tmp.append('"');
				break;
			default:
				tmp.append(codedText.charAt(i));
				break;
			}
		}
		return tmp.toString();
	}
}
