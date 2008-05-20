/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel (at ENLASO Corporation)                  */
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

package net.sf.okapi.common;

import java.io.File;

/**
 * Collection of various all-purpose helper functions.
 */
public class Util {

	/**
	 * Removes from the from of a string any of the specified characters. 
	 * @param text String to trim.
	 * @param chars List of the characters to trim.
	 * @return The trimmed string.
	 */
	static public String trimStart (String text,
		String chars)
	{
		if ( text == null ) return text;
		int n = 0;
		while ( n < text.length() ) {
			if ( chars.indexOf(text.charAt(n)) == -1 ) break;
			n++;
		}
		if ( n >= text.length() ) return "";
		if ( n > 0 ) return text.substring(n);
		return text;
	}

	/**
	 * Gets the application name from an application caption.
	 * This methods extracts the application name from a caption of the form 
	 * "filename - application name". If no "- " is found, the whole caption
	 * is returned as-it. 
	 * @param text The full caption where to take the name from.
	 * @return The name of the application.
	 */
	//TODO: Move to the UI library maybe (when there is one)
	static public String getNameInCaption (String text) {
		int n = text.indexOf("- ");
		if ( n > -1 ) return text.substring(n+1);
		else return text; // Same as caption itself
	}

	/**
	 * Gets the directory name of a full path.
	 * @param p_sPath Full path from where to extract the directory name.
	 * @return The directory name (without the final separator), or an empty
	 * string if p_sPath is a filename.
	 */
	static public String getDirectoryName (String p_sPath) {
		int n = p_sPath.lastIndexOf(File.separator);
		if ( n > 0 ) return p_sPath.substring(0, n);
		else return "";
	}
	
	/**
	 * Creates the directory tree for the give full path (dir+filename)
	 * @param p_sPath Directory and filename.
	 */
	static public void createDirectories (String p_sPath) {
		int n = p_sPath.lastIndexOf(File.separatorChar);
		if ( n == -1 ) return; // Nothing to do
		// Else, use the directory part and create the tree	
		String sDir = p_sPath.substring(0, n);
		File F = new File(sDir);
		F.mkdirs();
	}
	
}
