/*===========================================================================
  Copyright (C) 2012 by the Okapi Framework contributors
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

package net.sf.okapi.filters.mif;

import net.sf.okapi.common.encoder.IEncoder;

/**
 * Implements {@link IEncoder} for Adobe FrameMaker MIF format
 * when the text is inside a marker string (that cannot be using <Char xyz>).
 */
public class MIFStringEncoder extends MIFEncoder {

	/**
	 * Tries to convert an escaped hexadecimal character/byte into a corresponding Unicode value or inline code.
	 * <p>Inline code are bracketed between {@link #ILC_START} and {@link #ILC_END} characters.
	 * @param hexaValue the value to convert.
	 * @return the string with the conversion, or null if the value could not be mapped.
	 */
	static String convertCtrl (int hexaValue) {
		switch ( hexaValue ) {
		case 0x04: return "\u00ad"; // Discretionary hyphen
		case 0x05: return MIFFilter.ILC_START+"\\x05 "+MIFFilter.ILC_END; // Suppress hyphenation
		case 0x06: return MIFFilter.ILC_START+"\\x06 "+MIFFilter.ILC_END; // Automatic hyphen
		case 0x08: return "\u0007"; // Tab
		case 0x09: return MIFFilter.ILC_START+"\\x09 "+MIFFilter.ILC_END; // Forced return/line-break
		case 0x0a: return MIFFilter.ILC_START+"\\x0a "+MIFFilter.ILC_END; // End of paragraph
		case 0x0b: return MIFFilter.ILC_START+"\\x0b "+MIFFilter.ILC_END; // End of flow
		case 0x10: return "\u2007"; // Numeric space
		case 0x11: return "\u00a0"; // Non-breaking space
		case 0x12: return "\u2009"; // Thin space
		case 0x13: return "\u2002"; // En space
		case 0x14: return "\u2003"; // Em space
		case 0x15: return "\u2011"; // Non-breaking/hard hyphen
		}
		return null;
	}

	@Override
	protected String tryCharStatment (int value) {
		switch ( value ) {
		case '\t': return "\\x08 ";
		case '\u00a0': return "\\x11 "; // Non-breaking space
		case '\u2011': return "\\x15 "; // Non-breaking/hard hyphen
		case '\u00ad': return "\\x04 "; // Discretionary hyphen
		case '\u2007': return "\\x10 "; // Numeric space
		case '\u2009': return "\\x12 "; // Thin space
		case '\u2002': return "\\x13 "; // En space
		case '\u2003': return "\\x14 "; // Em space
		}
		return null;
	}

}
