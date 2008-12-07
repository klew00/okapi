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

package net.sf.okapi.applications.rainbow.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Locale;

public class Utils {

	static public String escapeToRTF (String p_sText,
		boolean p_bConvertLineBreaks,
		int p_nLineBreakStyle,
		Charset p_Enc)
	{
		if ( p_sText == null ) return "";
		StringBuffer sbTmp = new StringBuffer(p_sText.length());
		for ( int i=0; i<p_sText.length(); i++ ) {
			switch ( p_sText.charAt(i) ) {
				case '{':
				case '}':
				case '\\':
					sbTmp.append('\\');
					sbTmp.append(p_sText.charAt(i));
					continue;

				case '\r':
					//TODO: Fix case when input is Mac
					break;

				case '\n':
					if ( p_bConvertLineBreaks )
					{
						switch ( p_nLineBreakStyle )
						{
							case 1: // Outside external
								sbTmp.append(RTFStyle.ENDCODE);
								sbTmp.append("\r\n\\par ");
								sbTmp.append(RTFStyle.STARTCODE);
								continue;
							case 2:
								sbTmp.append(RTFStyle.ENDINLINE);
								sbTmp.append("\r\n\\par ");
								sbTmp.append(RTFStyle.STARTINLINE);
								continue;
							case 0: // Just convert
							default:
								sbTmp.append("\r\n\\par ");
								continue;
						}
					}
					else sbTmp.append(p_sText.charAt(i));
					continue;

				case '\u00a0': // Non-breaking space
					sbTmp.append("\\~"); // No extra space (it's a control word)
					break;

				case '\t':
					sbTmp.append("\\tab ");
					break;
				case '\u2022':
					sbTmp.append("\\bullet ");
					break;
				case '\u2018':
					sbTmp.append("\\lquote ");
					break;
				case '\u2019':
					sbTmp.append("\\rquote ");
					break;
				case '\u201c':
					sbTmp.append("\\ldblquote ");
					break;
				case '\u201d':
					sbTmp.append("\\rdblquote ");
					break;
				case '\u2013':
					sbTmp.append("\\endash ");
					break;
				case '\u2014':
					sbTmp.append("\\emdash ");
					break;
				case '\u200d':
					sbTmp.append("\\zwj ");
					break;
				case '\u200c':
					sbTmp.append("\\zwnj ");
					break;
				case '\u200e':
					sbTmp.append("\\ltrmark ");
					break;
				case '\u200f':
					sbTmp.append("\\rtlmark ");
					break;

				default:
					if ( p_sText.codePointAt(i) > 127 )
					{
						//TODO: fix this. limit() is not the length!
						ByteBuffer bBuf = p_Enc.encode(Integer.toString(p_sText.codePointAt(i)));
						if ( bBuf.limit() > 1 )
						{
							sbTmp.append(String.format("{{\\uc%1$d", bBuf.limit()));
							sbTmp.append(String.format("\\u%1$d", p_sText.codePointAt(i)));
							for ( int b=0; b<bBuf.limit(); b++ )
								sbTmp.append(String.format("\\'%1$x", bBuf.getChar(b)));
							sbTmp.append("}");
						}
						else
						{
							sbTmp.append(String.format("\\u%1$d", p_sText.codePointAt(i)));
							sbTmp.append(String.format("\\'%1$x", bBuf.getChar(0)));
						}
					}
					else sbTmp.append(p_sText.charAt(i));
					continue;
			}
		}
		return sbTmp.toString();
	}
	
	static public int getPercentage (long p_nPart,
		long p_nTotal)
	{
		return (int)((float)p_nPart/(float)((p_nTotal==0)?1:p_nTotal)*100);
	}
	
	static public String getANSIEncoding (String p_sLanguage)
	{
		String sEncoding = "windows-1252";
/*TODO: getANSIEncoding
		// Fall back to an RTF-friendly encoding
		try
		{
			CultureInfo CI = new CultureInfo(p_sLanguage);
			int nCP = CI.TextInfo.ANSICodePage;
			sEncoding = Encoding.GetEncoding(nCP).WebName;
		}
		catch
		{
			// Use the user choice
			//TODO: handle error: give warning
		}
*/
		return sEncoding;
	}

	static public String getDefaultSourceLanguage ()
	{
		// In most case the 'source' language is English
		// Even when we are on non-English machines
		return "en-US";
	}
	
	static public String getDefaultTargetLanguage ()
	{
		// Use the local language by default
		Locale Loc = Locale.getDefault();
		String sCode = Loc.getLanguage();
		if ( Loc.getCountry().length() > 0 ) {
			sCode = sCode + "-" + Loc.getCountry();
		}
		// If it's the same as the source, use an arbitrary value.
		if ( areSameLanguages(sCode, getDefaultSourceLanguage(), true) ) return "fr-FR";
		else return sCode;
	}

	static public String getCurrentLanguage () {
		String tmp1 = Locale.getDefault().getLanguage();
		String tmp2 = Locale.getDefault().getCountry();
		return (tmp1 + (tmp2.length()==0 ? "" : ("-"+tmp2))); 
	}
	
	static public boolean areSameLanguages (String p_sLanguage1,
		String p_sLanguage2,
		boolean p_bIgnoreSubLanguage)
	{
		String sL1;
		String sL2;
		if ( p_sLanguage1.length() == 0 ) return false;
		if ( p_sLanguage2.length() == 0 ) return false;

		p_sLanguage1 = p_sLanguage1.replace('_', '-');
		p_sLanguage2 = p_sLanguage2.replace('_', '-');

		if ( !p_bIgnoreSubLanguage ) {
			sL1 = p_sLanguage1;
			sL2 = p_sLanguage2;
		}
		else { // Do not take the sub-language is account
			if ( p_sLanguage1.length() > 2 ) {
				sL1 = p_sLanguage1.substring(0, 3);
				if ( sL1.charAt(2) == '-' ) sL1 = sL1.substring(0, 2);
			}
			else sL1 = p_sLanguage1.substring(0, 2);

			if ( p_sLanguage2.length() > 2 ) {
				sL2 = p_sLanguage2.substring(0, 3);
				if ( sL2.charAt(2) == '-' ) sL2 = sL2.substring(0, 2);
			}
			else sL2 = p_sLanguage2.substring(0, 2);
		}

		return sL1.equalsIgnoreCase(sL2);
	}

	static public String getOkapiSharedFolder (String rootFolder) {
		return rootFolder + File.separatorChar + "lib" + File.separator + "shared";
	}

	/*static public String getOkapiParametersFolder (String rootFolder) {
		return getOkapiSharedFolder(rootFolder) + File.separatorChar + "parameters";
	}*/

	/*
	 * Gets the Okapi Filter Parameters folder for a give type.
	 * @param p_nType Type of the folder to fetch: 0=System, 1=User, 2=Project
	 * @return The Filter Parameters folder for the given type (without a trailing separator).
	 *
	static public String getOkapiParametersFolder (String rootFolder,
		int p_nType)
	{
		String sTmp;
		switch ( p_nType ) {
		case 2: // Project folder
			// Check for the environment variable
			sTmp = System.getenv(PARAMETERS_PRJDIR);
			if (( sTmp != null ) && ( sTmp.length() > 0 ))
				return sTmp;
			// Else, fall through: use the User folder
		case 1: // User folder
			sTmp = System.getProperty("user.dir");
			sTmp = sTmp + File.separatorChar + "okapi"
				+ File.separatorChar + "parameters";
			return sTmp;
		case 0: // System folder
		default:
			return getOkapiParametersFolder(rootFolder);
		}
	}
*/
	static public String removeExtension (String p_sPath)
	{
		int n1 = p_sPath.lastIndexOf(File.separator);
        int n2 = p_sPath.lastIndexOf('.');
        if (( n2 > -1 ) && ( n1 < n2 )) {
        	return p_sPath.substring(0, n2);
        }
        return p_sPath;
	}
	
	
	/**
	 * Gets the extension of a path or file name.
	 * @param p_sPath The path or file name.
	 * @return The extension (with the period), or an empty string.
	 */
	static public String getExtension (String p_sPath)
	{
		int n1 = p_sPath.lastIndexOf(File.separator);
        int n2 = p_sPath.lastIndexOf('.');
        if (( n2 > -1 ) && ( n1 < n2 )) {
        	return p_sPath.substring(n2);
        }
        return "";
	}
	
	public static String[] splitLanguageCode (String p_sCode) {
		if (( p_sCode == null ) || ( p_sCode.length() == 0 )) return null;
		String[] aRes = new String[2];
		p_sCode = p_sCode.replace('_', '-');
		int n = p_sCode.indexOf('-');
		if ( n > -1 ) {
			aRes[0] = p_sCode.substring(0, n);
			aRes[1] = p_sCode.substring(n+1);
		}
		else {
			aRes[0] = p_sCode;
			aRes[1] = "";
		}
		return aRes;
	}
	
	public static String makeID (String p_sText) {
		int n = p_sText.hashCode();
		return String.format("%s%X", ((n>0)?'P':'N'), n);
	}

	/**
	 * Tries to detect the encoding and optionally the line-break type of a given file.
	 * @param p_sPath the full path of the file.
	 * @param p_bDetectLB True to try to detect line-break
	 * @return A string array: 0=encoding, 1=Line-break type. If the encoding could not be detected
	 * a value null is return for it. If the line-break could not be detected, a value null is
	 * return for it.
	 */
	public static String[] detectFileInformation (String p_sPath,
		boolean p_bDetectLB)
	{
		// Set defaults
		FileInputStream IS = null;
		String[] aInfo = new String[2];
		try {
			// Opens the file
			IS = new FileInputStream(p_sPath);
			byte Buf[] = new byte[9];
			int nRead = IS.read(Buf, 0, 3);
			// Try to detect the encoding
			//TODO: add detection for UTF-32			
			if ( nRead > 1 ) {
				// Try to get detect the encoding values
				if (( Buf[0]==(byte)0xFE ) && ( Buf[1]==(byte)0xFF )) aInfo[0] = "UTF-16BE";
				if (( Buf[0]==(byte)0xFF ) && ( Buf[1]==(byte)0xFE )) aInfo[0] = "UTF-16LE";
				if ( nRead > 2 ) {
					if (( Buf[0]==(byte)0xEF ) && ( Buf[1]==(byte)0xBB ) && ( Buf[3]==(byte)0xBF ))
						aInfo[0] = "UTF-8";
				}
			}

			//TODO: LB auto-detection
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
		finally {
			if ( IS != null )
				try { IS.close(); } catch ( IOException e ){};
		}
		return aInfo;
	}
	
	/**
	 * Checks if a string contain at least one occurrence of one of the characters
	 * listed in a string.
	 * @param p_sText Text to validate.
	 * @param p_sCharList List of characters to match against.
	 * @return (char)0 if no character is found, else the character found.
	 */
	public static char checksCharList (String p_sText,
		String p_sCharList) {
		if (( p_sCharList == null ) || ( p_sCharList.length() == 0 )) return (char)0;
		if (( p_sText == null ) || ( p_sText.length() == 0 )) return (char)0;
		for ( int i=0; i<p_sCharList.length(); i++ ) {
			if ( p_sText.indexOf(p_sCharList.charAt(i)) != -1 ) {
				return p_sCharList.charAt(i);
			}
		}
		return (char)0; // Does not contain any characters listed in p_sCharList 
	}

}
