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
	public void fromString (String data) {
		reset();
		super.fromString(data);
		unescapeNCR = getBoolean("unescapeNCR", unescapeNCR);
		unescapeCER = getBoolean("unescapeCER", unescapeCER);
		unescapeJava = getBoolean("unescapeJava", unescapeJava);
		escapeAll = getBoolean("escapeAll", escapeAll);
		escapeNotation = getInteger("escapeNotation", escapeNotation);
		userFormat = getString("userFormat", userFormat);
		useBytes = getBoolean("useBytes", useBytes);
		BOMonUTF8 = getBoolean("BOMonUTF8", BOMonUTF8);
		reportUnsupported = getBoolean("reportUnsupported", reportUnsupported);
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
		setBoolean("unescapeNCR", unescapeNCR);
		setBoolean("unescapeCER", unescapeCER);
		setBoolean("unescapeJava", unescapeJava);
		setBoolean("escapeAll", escapeAll);
		setInteger("escapeNotation", escapeNotation);
		setString("userFormat", userFormat);
		setBoolean("useBytes", useBytes);
		setBoolean("BOMonUTF8", BOMonUTF8);
		setBoolean("reportUnsupported", reportUnsupported);
		return super.toString();
	}
	
}
