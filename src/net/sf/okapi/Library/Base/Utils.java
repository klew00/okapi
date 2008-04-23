/*===========================================================================*/
/* Copyright (C) 2007-2008 ENLASO Corporation, Okapi Development Team        */
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

package net.sf.okapi.Library.Base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Locale;
import javax.swing.JOptionPane;

import org.eclipse.swt.SWT;

public class Utils {

	public static final String OKAPIHOME         = "OKAPIHOME";
	public static final String PARAMETERS_PRJDIR = "OKAPIPROJECTPARAMETERS";
	
	public static final int    PFTYPE_WIN        = 0;
	public static final int    PFTYPE_MAC        = 1;
	public static final int    PFTYPE_UNIX       = 2;

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
	
	/**
	 * Removes from the from of a string any of the specified characters. 
	 * @param p_sText String to trim.
	 * @param p_sChars List of the characters to trim.
	 * @return The trimmed string.
	 */
	static public String trimStart(String p_sText,
		String p_sChars)
	{
		if ( p_sText == null ) return p_sText;
		int n = 0;
		while ( n < p_sText.length() ) {
			if ( p_sChars.indexOf(p_sText.charAt(n)) == -1 ) break;
			n++;
		}
		if ( n >= p_sText.length() ) return "";
		if ( n > 0 ) return p_sText.substring(n);
		return p_sText;
	}

	/**
	 * Escapes a string for XML.
	 * @param p_sText String to escape.
	 * @param p_nEscapeQuotes 0=no quote escaped, 1=apos and quot,
	 * 2=#39 and quot, and * 3=quot only.
	 * @param p_bAlwaysEscapeGT True to always escape > to gt
	 * @return The escaped string.
	 */
	static public String escapeToXML (String p_sText,
		int p_nEscapeQuotes,
		boolean p_bAlwaysEscapeGT)
	{
		if ( p_sText == null ) return "";
		StringBuffer sbTmp = new StringBuffer(p_sText.length());
		for ( int i=0; i<p_sText.length(); i++ ) {
			switch ( p_sText.charAt(i) )
			{
				case '<':
					sbTmp.append("&lt;");
					continue;

				case '>':
					if ( p_bAlwaysEscapeGT ) sbTmp.append("&gt;");
					else {
						if (( i > 0 ) && ( p_sText.charAt(i-1) == ']' )) sbTmp.append("&gt;");
						else sbTmp.append('>');
					}
					continue;

				case '&':
					sbTmp.append("&amp;");
					continue;

				case '"':
					if ( p_nEscapeQuotes > 0 ) sbTmp.append("&quot;");
					else sbTmp.append('"');
					continue;

				case '\'':
					switch ( p_nEscapeQuotes ) {
						case 1:
							sbTmp.append("&apos;");
							break;
						case 2:
							sbTmp.append("&#39;");
							break;
						default:
							sbTmp.append(p_sText.charAt(i));
							break;
					}
					continue;

				default:
					sbTmp.append(p_sText.charAt(i));
					continue;
			}
		}
		return sbTmp.toString();
	}
	
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

				case '\u00a0': // Nbsp
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
/*TODO
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

	static public void showError (String p_sMessage,
		String p_sDetails)
	{
		//TODO: get application name
		showError(p_sMessage, p_sDetails, "Error", false);
	}

	static public void showError (String p_sMessage,
		String p_sDetails,
		String p_sTitle)
	{
		showError(p_sMessage, p_sDetails, p_sTitle, false);
	}

	static public void showError (String p_sMessage,
		String p_sDetails,
		String p_sTitle,
		boolean p_bShowDetails)
	{
		try {
			//TODO: Create custom dialog where details can be hidden/shown 
			JOptionPane.showMessageDialog(null,
			    p_sMessage + ((p_sDetails==null) ? "" : "\n"+p_sDetails),
			    "Error", //TODO: Use application name
			    JOptionPane.ERROR_MESSAGE);
		}
		catch ( Exception E )
		{
			JOptionPane.showMessageDialog(null,
			    E.getMessage() + "\n" + E.getStackTrace(),
			    "TODO: App name", //TODO: Use application name
			    JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public static String getDefaultSourceLanguage ()
	{
		// In most case the 'source' language is English
		// Even when we are on non-English machines
		return "en-US";
	}
	
	public static String getDefaultTargetLanguage ()
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

		if ( !p_bIgnoreSubLanguage )
		{
			sL1 = p_sLanguage1;
			sL2 = p_sLanguage2;
		}
		else // Do not take the sub-language is account
		{
			if ( p_sLanguage1.length() > 2 )
			{
				sL1 = p_sLanguage1.substring(0, 3); //LEN=3
				if ( sL1.charAt(2) == '-' ) sL1 = sL1.substring(0, 1); //LEN=2
			}
			else sL1 = p_sLanguage1.substring(0, 2); //LEN=2

			if ( p_sLanguage2.length() > 2 )
			{
				sL2 = p_sLanguage2.substring(0, 3); //LEN=3
				if ( sL2.charAt(2) == '-' ) sL2 = sL2.substring(0, 2); //LEN=2
			}
			else sL2 = p_sLanguage2.substring(0, 2); //LEN=2
		}
			
		return sL1.equalsIgnoreCase(sL2);
	}

	static public String makeParametersFullPath (String rootFolder,
		String p_sFilterSettings)
	{
		String sTmp;
		int n;
		StringBuilder sbTmp = new StringBuilder(p_sFilterSettings);
		// Check for system parameters markers
		if ( (n = sbTmp.indexOf(
			FilterSettingsMarkers.PARAMETERSSEP+FilterSettingsMarkers.FOLDERTYPE_SYSTEM)) != -1 )
		{
			sbTmp = sbTmp.delete(n+1, n+3); //LEN=2
			sTmp = Utils.getOkapiParametersFolder(rootFolder, 0) + sbTmp;
		}
		else if ( (n = sbTmp.indexOf(
			FilterSettingsMarkers.PARAMETERSSEP+FilterSettingsMarkers.FOLDERTYPE_PROJECT)) != -1 )
		{
			sbTmp = sbTmp.delete(n+1, n+3); //LEN=2
			sTmp = Utils.getOkapiParametersFolder(rootFolder, 2) + sbTmp;
		}
		else if ( (n = sbTmp.indexOf(
			FilterSettingsMarkers.PARAMETERSSEP+FilterSettingsMarkers.FOLDERTYPE_USER)) != -1 )
		{
			sbTmp = sbTmp.delete(n+1, n+3); // LEN=2
			sTmp = Utils.getOkapiParametersFolder(rootFolder, 1) + sbTmp;
		}
		else // Check for real folder
		{
			File F = new File(p_sFilterSettings);
			sTmp = F.getParent(); 
			if (( sTmp == null ) || ( sTmp.length() == 0 ))
			{
				// No folder: use system parameters (backward compatible)
				sTmp = Utils.getOkapiParametersFolder(rootFolder) + File.separatorChar + sbTmp;
			}
			else sTmp = sbTmp.toString(); // Real folder is already there
		}

		// Need the extension?
		if ( !sTmp.endsWith(FilterSettingsMarkers.PARAMETERS_FILEEXT) )
			return sTmp + FilterSettingsMarkers.PARAMETERS_FILEEXT;
		else
			return sTmp;
	}

	static public String getOkapiSharedFolder (String rootFolder) {
		return rootFolder + File.separatorChar + "shared";
	}

	static public String getOkapiParametersFolder (String rootFolder) {
		return getOkapiSharedFolder(rootFolder) + File.separatorChar + "parameters";
	}

	/**
	 * Gets the Okapi Filter Parameters folder for a give type.
	 * @param p_nType Type of the folder to fetch: 0=System, 1=User, 2=Project
	 * @return The Filter Parameters folder for the given type (without a trailing separator).
	 */
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

	/**
	 * Construct a filter settings string.
	 * @param filterID Filter identifier (cannot be null nor empty).
	 * @param paramsName Name of the parameters file (can be null or empty).
	 * @return Filter settings string.
	 */
	static public String buildFilterSettingsType1 (String filterID,
		String paramsName)
	{
		String sTmp = filterID;
		if (( paramsName != null ) && ( paramsName.length() > 0 ))
			sTmp += (FilterSettingsMarkers.PARAMETERSSEP + paramsName);
		return sTmp;
	}

	/**
	 * Splits a filter settings string into its different components.
	 * @param filterSettings The setting string to split.
	 * @return An array of 4 strings: 0=folder, 1=filter id, 2=parameters name
	 * and 3=full parameters file path (folder + parameters name + extension).
	 */
	static public String[] splitFilterSettingsType1 (String filterSettings) {
		String[] aOutput = new String[4];
		for ( int i=0; i<4; i++ ) aOutput[i] = "";

		if (( filterSettings == null ) || ( filterSettings.length() == 0 ))
			return aOutput;

		File F = new File(filterSettings);
		aOutput[0] = F.getParent();
		String sTmp;
//TODO: get real path.
		if ( aOutput[0] == null ) aOutput[0] = "";
		if ( aOutput[0].length() > 0 )
			sTmp = F.getName();
		else
			sTmp = filterSettings;

		// Get the parameters file
		int n;
		if ( (n = sTmp.indexOf(FilterSettingsMarkers.PARAMETERSSEP)) > -1 )
		{
			if ( n < sTmp.length()-1 )
				aOutput[2] = sTmp.substring(n+1);
			sTmp = sTmp.substring(0, n); //LEN=n
		}

		// Get the filter identifier
		aOutput[1] = sTmp;
		
		// Get the full path of the parameters file
		if ( aOutput[0].length() > 0 )
			aOutput[3] = aOutput[0] + File.separator;
		aOutput[3] = aOutput[3] + aOutput[2] + FilterSettingsMarkers.PARAMETERS_FILEEXT;
		
		return aOutput;
	}

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
	
	/**
	 * Gets the filename (without extension) of a path.
	 * @param p_sPath The path from where to get the filename.
	 * @param p_bKeepExtension True to keep the existing extension, false to remove it.
	 * @return The filename without extension.
	 */
	static public String getFilename (String p_sPath,
		boolean p_bKeepExtension) {
		// Get the filename
		int n = p_sPath.lastIndexOf(File.separator);
		if ( n > -1 ) p_sPath = p_sPath.substring(n+1);

		if ( p_bKeepExtension ) return p_sPath;
		
		// Remove the extension if there is one
	    n = p_sPath.lastIndexOf('.');
        if ( n > -1 ) return p_sPath.substring(0, n);
        else return p_sPath;
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
	 * Opens a given page in the default browser.
	 * @param p_sURL URL (can be a local file) of the page to open.
	 * @throws IOException
	 */
	static public void startPage (String p_sURL)
	{
		try {
			Runtime RT = Runtime.getRuntime();
			//TODO: No-windows parts
			RT.exec("cmd.exe /C start " + p_sURL);
		}
		catch ( Exception E ) {
			showError(E.getLocalizedMessage(), null);
		}
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
	public static String[] DetectInformation (String p_sPath,
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
		catch ( Exception E ) {
			// Do nothing.
		}
		finally {
			if ( IS != null )
				try { IS.close(); } catch ( Exception E ){};
		}
		return aInfo;
	}
	
	public static void copyFile (String p_sFromPath,
		String p_sToPath,
		boolean p_bMove)
		throws Exception
	{
		FileChannel IC = null;
		FileChannel OC = null;
		try {
			createDirectories(p_sToPath);
			IC = new FileInputStream(p_sFromPath).getChannel();
			OC = new FileOutputStream(p_sToPath).getChannel();
			IC.transferTo(0, IC.size(), OC);
			
			if ( p_bMove ) {
				IC.close(); IC = null;
				File F = new File(p_sFromPath);
				F.delete();
			}
		}
		catch ( Exception E ) {
			throw E;
		}
		finally {
			if ( IC != null ) IC.close();
			if ( OC != null ) OC.close();
		}
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

	/**
	 * Searches the element a classpath that ends a specific way. 
	 * @param p_sClassPath The classpath where to search.
	 * @param p_sEnding The ending to look for.
	 * @return The path where the ending was found (without the ending),
	 * or null if it was not found.
	 */
	public static String searchInClassPath (String p_sClassPath,
    	String p_sEnding)
    {
   		int nPos = p_sClassPath.indexOf(p_sEnding);
   		if ( nPos > -1) {
   			// Semicolon before the path to the Jar
   			int nSC1 = p_sClassPath.lastIndexOf(File.pathSeparatorChar, nPos);
   			// Semicolon after the path to the Jar
   			int nSC2 = p_sClassPath.indexOf(File.pathSeparatorChar, nPos);
   			if ( nSC1 < 0 ) nSC1 = -1;
   			if ( nSC2 < 0 ) nSC2 = p_sClassPath.length();
   			String sPath = p_sClassPath.substring(nSC1+1, nSC2);
   			sPath = sPath.substring(0, sPath.indexOf(p_sEnding));
   			return sPath;
   		}
        return null;
    }

	/**
	 * Gets the type of platform the application is running on.
	 * @return -1 if the type could not be detected. Otherwise one of the PFTYPE_* values.
	 */
	public static int getPlatformType () {
		if ( "win32".equals(SWT.getPlatform()) ) return PFTYPE_WIN;
		if ( "carbon".equals(SWT.getPlatform()) ) return PFTYPE_MAC;
		if ( "gtk".equals(SWT.getPlatform()) ) return PFTYPE_UNIX;
		if ( "motif".equals(SWT.getPlatform()) ) return PFTYPE_UNIX;
		return -1; // Unknown
	}
	
}
