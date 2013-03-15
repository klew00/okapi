/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
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
============================================================================*/

package net.sf.okapi.steps.encodingconversion;

import net.sf.okapi.common.BaseParameters;

public class Parameters extends BaseParameters {
	
	public static final int ESCAPE_NCRHEXAU      = 0;
	public static final int ESCAPE_NCRHEXAL      = 1;
	public static final int ESCAPE_NCRDECI       = 2;
	public static final int ESCAPE_CER           = 3;
	public static final int ESCAPE_JAVAU         = 4;
	public static final int ESCAPE_JAVAL         = 5;
	public static final int ESCAPE_USERFORMAT    = 6;

	public boolean unescapeNCR;
	public boolean unescapeCER;
	public boolean unescapeJava;
	public boolean escapeAll;
	public int escapeNotation;
	public String userFormat;
	public boolean useBytes;
	public boolean BOMonUTF8;
	public boolean reportUnsupported;

	public Parameters () {
		reset();
	}
	
	public void reset() {
		unescapeNCR = true;
		unescapeCER = true;
		unescapeJava = true;
		escapeAll = false;
		escapeNotation = 0;
		userFormat = "%d";
		useBytes = false;
		BOMonUTF8 = true;
		reportUnsupported = true;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		unescapeNCR = buffer.getBoolean("unescapeNCR", unescapeNCR);
		unescapeCER = buffer.getBoolean("unescapeCER", unescapeCER);
		unescapeJava = buffer.getBoolean("unescapeJava", unescapeJava);
		escapeAll = buffer.getBoolean("escapeAll", escapeAll);
		escapeNotation = buffer.getInteger("escapeNotation", escapeNotation);
		userFormat = buffer.getString("userFormat", userFormat);
		useBytes = buffer.getBoolean("useBytes", useBytes);
		BOMonUTF8 = buffer.getBoolean("BOMonUTF8", BOMonUTF8);
		reportUnsupported = buffer.getBoolean("reportUnsupported", reportUnsupported);
	}

	public String toString() {
		buffer.reset();
		buffer.setBoolean("unescapeNCR", unescapeNCR);
		buffer.setBoolean("unescapeCER", unescapeCER);
		buffer.setBoolean("unescapeJava", unescapeJava);
		buffer.setBoolean("escapeAll", escapeAll);
		buffer.setInteger("escapeNotation", escapeNotation);
		buffer.setString("userFormat", userFormat);
		buffer.setBoolean("useBytes", useBytes);
		buffer.setBoolean("BOMonUTF8", BOMonUTF8);
		buffer.setBoolean("reportUnsupported", reportUnsupported);
		return buffer.toString();
	}
	
}
