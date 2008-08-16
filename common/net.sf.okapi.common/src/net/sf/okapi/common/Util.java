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

package net.sf.okapi.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetEncoder;

/**
 * Collection of various all-purpose helper functions.
 */
public class Util {
	
	public static final String   LINEBREAK_DOS   = "\r\n";
	public static final String   LINEBREAK_UNIX  = "\n";
	public static final String   LINEBREAK_MAC   = "\r";

	public static final String   RTF_STARTCODE   = "{\\cs5\\f1\\cf15\\lang1024 ";
	public static final String   RTF_ENDCODE     = "}";
	public static final String   RTF_STARTINLINE = "{\\cs6\\f1\\cf6\\lang1024 ";
	public static final String   RTF_ENDINLINE   = "}";

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
	 * @param path Full path from where to extract the directory name.
	 * @return The directory name (without the final separator), or an empty
	 * string if p_sPath is a filename.
	 */
	static public String getDirectoryName (String path) {
		int n = path.lastIndexOf(File.separator);
		if ( n > 0 ) return path.substring(0, n);
		else return "";
	}
	
	/**
	 * Creates the directory tree for the give full path (dir+filename)
	 * @param path Directory and filename. If you want to pass only a directory
	 * name make sure it has a trailing separator (e.g. "c:\project\tmp\").
	 */
	static public void createDirectories (String path) {
		int n = path.lastIndexOf(File.separatorChar);
		if ( n == -1 ) return; // Nothing to do
		// Else, use the directory part and create the tree	
		String dir = path.substring(0, n);
		File F = new File(dir);
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

	/**
	 * Escapes a given string into RTF format.
	 * @param text the string to convert.
	 * @param convertLineBreaks Indicates if the line-breaks should be converted.
	 * @param lineBreakStyle Type of line-break conversion. 0=do nothing special,
	 * 1=close then re-open as external, 2=close then re-open as internal.
	 * @param encoder Encoder to use for the extended characters.
	 * @return The input string escaped to RTF.
	 */
	static public String escapeToRTF (String text,
		boolean convertLineBreaks,
		int lineBreakStyle,
		CharsetEncoder encoder)
	{
		try {
			if ( text == null ) return "";
			StringBuffer tmp = new StringBuffer(text.length());
			CharBuffer tmpBuf = CharBuffer.allocate(1);
			ByteBuffer encBuf;
			
			for ( int i=0; i<text.length(); i++ ) {
				switch ( text.charAt(i) ) {
				case '{':
				case '}':
				case '\\':
					tmp.append("\\"+text.charAt(i));
					break;
				case '\r': // to skip
					break;
				case '\n':
					if ( convertLineBreaks ) {
						switch ( lineBreakStyle ) {
						case 1: // Outside external
							tmp.append(RTF_ENDCODE);
							tmp.append("\r\n\\par ");
							tmp.append(RTF_STARTCODE);
							continue;
						case 2:
							tmp.append(RTF_ENDINLINE);
							tmp.append("\r\n\\par ");
							tmp.append(RTF_STARTINLINE);
							continue;
						case 0: // Just convert
						default:
							tmp.append("\r\n\\par ");
							continue;
						}
					}
					else tmp.append("\n");
					break;
				case '\u00a0': // Non-breaking space
					tmp.append("\\~"); // No extra space (it's a control word)
					break;
				case '\t':
					tmp.append("\\tab ");
					break;
				case '\u2022':
					tmp.append("\\bullet ");
					break;
				case '\u2018':
					tmp.append("\\lquote ");
					break;
				case '\u2019':
					tmp.append("\\rquote ");
					break;
				case '\u201c':
					tmp.append("\\ldblquote ");
					break;
				case '\u201d':
					tmp.append("\\rdblquote ");
					break;
				case '\u2013':
					tmp.append("\\endash ");
					break;
				case '\u2014':
					tmp.append("\\emdash ");
					break;
				case '\u200d':
					tmp.append("\\zwj ");
					break;
				case '\u200c':
					tmp.append("\\zwnj ");
					break;
				case '\u200e':
					tmp.append("\\ltrmark ");
					break;
				case '\u200f':
					tmp.append("\\rtlmark ");
					break;
					
				default:
					if ( text.charAt(i) > 127 ) {
						tmpBuf.put(0, text.charAt(i));
						tmpBuf.position(0);
						encBuf = encoder.encode(tmpBuf);
						if ( encBuf.limit() > 1 ) {
							tmp.append(String.format("{{\\uc{0:d}",
								encBuf.limit()));
							tmp.append(String.format("\\u{0:d}",
								text.charAt(i)));
							for ( int j=0; j<encBuf.limit(); j++ ) {
								tmp.append(String.format("\\'{0:x}",
									(encBuf.get(j)<0 ? (0xFF^~encBuf.get(j)) : encBuf.get(j)) ));
							}
							tmp.append("}");
						}
						else {
							tmp.append(String.format("\\u{0:d}",
								text.charAt(i)));
							tmp.append(String.format("\\'{0:x}",
								(encBuf.get(0)<0 ? (0xFF^~encBuf.get(0)) : encBuf.get(0))));
						}
					}
					else tmp.append(text.charAt(i));
					break;
				}
			}
			return tmp.toString();
		}
		catch (  CharacterCodingException e ) {
			throw new RuntimeException(e);
		}
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
	
	/**
	 * Gets the longest common path between an existing current directory
	 * and a new one.
	 * @param currentDir The current longest common path.
	 * @param newDir The new directory to compare with.
	 * @param ignoreCase True if the method should ignore cases differences.
	 * @return The longest sub-directory that is common to both directories.
	 * This can be a null or empty string.
	 */
	static public String longestCommonDir (String currentDir,
		String newDir,
		boolean ignoreCase)
	{
		if ( currentDir == null ) return newDir;
		if ( currentDir.length() == 0 ) return currentDir;

		// Get temporary copies
		String currentLow = currentDir;
		String newLow = newDir;
		if ( ignoreCase ) {
			currentLow = currentDir.toLowerCase();
			newLow = newDir.toLowerCase();
		}
		
		// The new path equals, or include the existing root: no change
		if ( newLow.indexOf(currentLow) == 0 ) return currentDir;

		// Search the common path
		String tmp = currentLow;
		int i = 0;
		while ( newLow.indexOf(tmp) != 0 ) {
			tmp = Util.getDirectoryName(tmp);
			i++;
			if ( tmp.length() == 0 ) return ""; // No common path at all
		}

		// Do not return currentDir.substring(0, tmp.length());
		// because the lower-case string maybe of a different length than cased one
		// (e.g. German Sz). Instead re-do the splitting as many time as needed.
		tmp = currentDir;
		for ( int j=0; j<i; j++ ) {
			tmp = Util.getDirectoryName(tmp);
		}
		return tmp;
	}
	
	/**
	 * Indicates if the current OS is case-sensitive.
	 * @return True if the current OS is case-sensitive, false if otherwise.
	 */
	static public boolean isOSCaseSensitive () {
		// May not work on all platforms,
		// But should on basic Windows, Mac and Linux
		return File.separator.equals("\\"); // Windows line-types
	}
	
	/**
	 * Writes a Byte-Order-Mark if the encoding indicates it is needed.
	 * This methods must be the first call after opening the writer.
	 * @param writer Writer where to output the BOM.
	 * @param bomOnUTF8 Indicates if we should use a BOM on UTF-8 files.
	 * @param encoding Encoding of the output.
	 */
	static public void writeBOMIfNeeded (Writer writer,
		boolean bomOnUTF8,
		String encoding)
	{
		try {
			String tmp = encoding.toLowerCase();
			
			// Check UTF-8 first (most cases)
			if (( bomOnUTF8 ) && ( tmp.equals("utf-8") )) { 
				writer.write("\ufeff");
				return;
			}
			
			if ( tmp.equals("utf-16be")
				|| tmp.equals("utf-16le")
				|| tmp.equals("utf-16") )
			{
				writer.write("\ufeff");
				return;
			}
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}
}
