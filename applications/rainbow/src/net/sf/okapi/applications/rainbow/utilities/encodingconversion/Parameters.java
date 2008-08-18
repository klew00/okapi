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

package net.sf.okapi.applications.rainbow.utilities.encodingconversion;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.FieldsString;

public class Parameters extends BaseParameters {
	
	public static final int  ESCAPE_NCRHEXAU     = 0;
	public static final int  ESCAPE_NCRHEXAL     = 1;
	public static final int  ESCAPE_NCRDECI      = 2;
	public static final int  ESCAPE_CER          = 3;
	public static final int  ESCAPE_JAVAU        = 4;
	public static final int  ESCAPE_JAVAL        = 5;
	public static final int  ESCAPE_USERFORMAT   = 6;

	private boolean     unescapeNCR;
	private boolean     unescapeCER;
	private boolean     unescapeJava;
	private boolean     escapeAll;
	private int         escapeNotation;
	private String      userFormat;
	private boolean     useBytes;
	private boolean     BOMonUTF8;
	private boolean     reportUnsupported;


	public Parameters () {
		reset();
	}
	
	@Override
	public void fromString(String data) {
		// Read the file content as a set of fields
		FieldsString tmp = new FieldsString(data);
		// Parse the fields
		unescapeNCR = tmp.get("unescapeNCR", unescapeNCR);
		unescapeCER = tmp.get("unescapeCER", unescapeCER);
		unescapeJava = tmp.get("unescapeJava", unescapeJava);
		escapeAll = tmp.get("escapeAll", escapeAll);
		escapeNotation = tmp.get("escapeNotation", escapeNotation);
		userFormat = tmp.get("userFormat", userFormat);
		useBytes = tmp.get("useBytes", useBytes);
		BOMonUTF8 = tmp.get("BOMonUTF8", BOMonUTF8);
		reportUnsupported = tmp.get("reportUnsupported", reportUnsupported);
	}

	@Override
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

	@Override
	public String toString() {
		// Store the parameters in fields
		FieldsString tmp = new FieldsString();
		tmp.add("unescapeNCR", unescapeNCR);
		tmp.add("unescapeCER", unescapeCER);
		tmp.add("unescapeJava", unescapeJava);
		tmp.add("escapeAll", escapeAll);
		tmp.add("escapeNotation", escapeNotation);
		tmp.add("userFormat", userFormat);
		tmp.add("useBytes", useBytes);
		tmp.add("BOMonUTF8", BOMonUTF8);
		tmp.add("reportUnsupported", reportUnsupported);
		return tmp.toString();
	}
	
}
