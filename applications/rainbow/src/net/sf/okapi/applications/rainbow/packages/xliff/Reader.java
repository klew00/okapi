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

package net.sf.okapi.applications.rainbow.packages.xliff;

import java.io.FileInputStream;

import net.sf.okapi.applications.rainbow.packages.IReader;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.xliff.XLIFFReader;

/**
 * Implements IReader for generic XLIFF translation packages.
 */
public class Reader implements IReader {
	
	XLIFFReader reader;
	
	public Reader () {
		reader = new XLIFFReader();
	}

	public void closeDocument () {
	}

	public TextUnit getItem () {
		return reader.getItem();
	}

	public void openDocument (String path) {
		try {
			reader.open(new FileInputStream(path));
		}
		catch ( Exception e ) {
			throw new RuntimeException(e);
		}
	}

	public boolean readItem () {
		int n;
		do {
			switch ( (n = reader.readItem()) ) {
			case XLIFFReader.RESULT_ENDTRANSUNIT:
				return true;
			}
		} while ( n > XLIFFReader.RESULT_ENDINPUT );
		return false;
	}

}
