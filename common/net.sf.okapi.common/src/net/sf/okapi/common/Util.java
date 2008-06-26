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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;

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
	 * @param p_sPath Directory and filename. If you want to pass only a directory
	 * name make sure it has a trailing separator (e.g. "c:\project\tmp\").
	 */
	static public void createDirectories (String p_sPath) {
		int n = p_sPath.lastIndexOf(File.separatorChar);
		if ( n == -1 ) return; // Nothing to do
		// Else, use the directory part and create the tree	
		String sDir = p_sPath.substring(0, n);
		File F = new File(sDir);
		F.mkdirs();
	}

	/**
	 * Escapes a string for XML.
	 * @param text String to escape.
	 * @param quoteMode 0=no quote escaped, 1=apos and quot, 2=#39 and quot,
	 * and 3=quot only.
	 * @param escapeGT True to always escape '>' to gt
	 * @return The escaped string.
	 */
	static public String escapeToXML (String text,
		int quoteMode,
		boolean escapeGT)
	{
		if ( text == null ) return "";
		StringBuffer sbTmp = new StringBuffer(text.length());
		for ( int i=0; i<text.length(); i++ ) {
			switch ( text.charAt(i) ) {
			case '<':
				sbTmp.append("&lt;");
				continue;
			case '>':
				if ( escapeGT ) sbTmp.append("&gt;");
				else {
					if (( i > 0 ) && ( text.charAt(i-1) == ']' )) sbTmp.append("&gt;");
					else sbTmp.append('>');
				}
				continue;
			case '&':
				sbTmp.append("&amp;");
				continue;
			case '"':
				if ( quoteMode > 0 ) sbTmp.append("&quot;");
				else sbTmp.append('"');
				continue;
			case '\'':
				switch ( quoteMode ) {
				case 1:
					sbTmp.append("&apos;");
					break;
				case 2:
					sbTmp.append("&#39;");
					break;
				default:
					sbTmp.append(text.charAt(i));
					break;
				}
				continue;
			default:
				sbTmp.append(text.charAt(i));
				continue;
			}
		}
		return sbTmp.toString();
	}

	public static void copyFile (String fromPath,
		String toPath,
		boolean move)
	{
		FileChannel ic = null;
		FileChannel oc = null;
		try {
			createDirectories(toPath);
			ic = new FileInputStream(fromPath).getChannel();
			oc = new FileOutputStream(toPath).getChannel();
			ic.transferTo(0, ic.size(), oc);
			if ( move ) {
				ic.close(); ic = null;
				File file = new File(fromPath);
				file.delete();
			}
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
		finally {
			try {
				if ( ic != null ) ic.close();
				if ( oc != null ) oc.close();
			}
			catch ( IOException e ) {};
		}
	}
	
	/**
	 * Gets the filename of a path.
	 * @param path The path from where to get the filename.
	 * @param keepExtension True to keep the existing extension, false to remove it.
	 * @return The filename with or without extension.
	 */
	static public String getFilename (String path,
		boolean keepExtension) {
		// Get the filename
		int n = path.lastIndexOf(File.separator);
		if ( n > -1 ) path = path.substring(n+1);

		if ( keepExtension ) return path;
		
		// Remove the extension if there is one
	    n = path.lastIndexOf('.');
        if ( n > -1 ) return path.substring(0, n);
        else return path;
	}

	/**
	 * Makes a URI string from a path. If the path itself can be recognized as a string
	 * URI already, it is passed unchanged. For example "C:\test" and "file:///C:/test"
	 * will both return "file:///C:/test" encoded as URI.
	 * @param path The path to change to URI string.
	 * @return The URI string.
	 */
	static public String makeURIFromPath (String path) {
		if ( path.startsWith("file:///") ) return path;
		if ( path.startsWith("http://") ) return path;
		if ( path.startsWith("https://") ) return path;
		if ( path.startsWith("ftp://") ) return path;
		try {
			return "file:///"+URLEncoder.encode(path.replace('\\', '/'), "UTF-8");
		}
		catch ( UnsupportedEncodingException e ) {
			throw new RuntimeException(e); // UTF-8 should be always supported anyway
		}
	}
}
