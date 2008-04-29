/*===========================================================================*/
/* Copyright (C) 2008 ENLASO Corporation, Okapi Development Team             */
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

package net.sf.okapi.Format.Properties;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.regex.Pattern;

import net.sf.okapi.Filter.FilterItem;
import net.sf.okapi.Filter.FilterItemText;
import net.sf.okapi.Filter.FilterItemType;
import net.sf.okapi.Filter.FilterOutputLayer;
import net.sf.okapi.Filter.FilterProperty;
import net.sf.okapi.Filter.IFilter;
import net.sf.okapi.Filter.IFilterItem;
import net.sf.okapi.Filter.ILocalizationDirectives;
import net.sf.okapi.Library.Base.FieldsString;
import net.sf.okapi.Library.Base.ILog;
import net.sf.okapi.Library.Base.IParameters;
import net.sf.okapi.Library.Base.LineBreakType;
import net.sf.okapi.Library.Base.LogProgressMode;
import net.sf.okapi.Library.Base.LogType;
import net.sf.okapi.Library.Base.RTFStyle;
import net.sf.okapi.Library.Base.Utils;

public class Filter implements IFilter {

	private ILog                     m_Log;
	private Parameters               m_Opt;
	private FilterItem               m_CurrentFI;
	private String                   m_sPath;
	private String                   m_sInputEncoding;
	private String                   m_sInputLanguage;
	private String                   m_sOutputLanguage;
	private String                   m_sOutputEncoding;
	private int                      m_nOutputLayer;
	private boolean                  m_bInputStringMode;
	private boolean                  m_bOutputStringMode;
	private String                   m_sLineBreak;
	private int                      m_nID;
	private boolean                  m_bEndingLB;
	private boolean                  m_bReadLine;
	private CharsetEncoder           m_OutputEncoder;
	private Pattern                  m_RE;
	
	// Input file specific
	private BufferedReader           m_SR;
	private Writer                   m_SW;
	private int                      m_nLine;
	private int                      m_nLineSince;
	private long                     m_lPosition;
	private long                     m_lBytePos;
	private StringBuilder            m_sbBuffer;
	private StringBuilder            m_sbKeyBuffer;
	private StringBuilder            m_sbTextBuffer;
	private StringBuilder            m_sbEscape;
	private String                   m_sLine;
	
	// Input String specific
	private String                   m_sInput;
	private int                      m_nStringIndex;
	private StringBuilder            m_sbOutput;
	
	// Layer variables
	private String                   m_sStartDocument;
	private String                   m_sEndDocument;
	private String                   m_sStartCode;
	private String                   m_sEndCode;
	private String                   m_sStartInline;
	private String                   m_sEndInline;
	private String                   m_sStartText;
	private String                   m_sEndText;

	public Filter () {
		m_sbBuffer = new StringBuilder(255);
		m_sbKeyBuffer = new StringBuilder(255);
		m_sbTextBuffer = new StringBuilder(255);
		m_sbEscape = new StringBuilder(255);
		m_sbOutput = null;
		m_SR = null;
		m_SW = null;
		m_bInputStringMode = false;
		m_bOutputStringMode = false;
		m_sLineBreak = LineBreakType.DOS;
		m_Opt = new Parameters();
		m_Opt.m_LD.setLog(m_Log);
		m_nOutputLayer = FilterOutputLayer.NONE;
		m_CurrentFI = new FilterItem();
	}
	
	public IParameters getParameters () {
		return m_Opt;
	}
	
	public void closeInput () {
		try {
			m_sInput = null;
			if ( m_SR != null ) {
				m_SR.close();
				m_SR = null;
			}
		}
		catch ( IOException E ) {
			m_Log.error(E.getLocalizedMessage());
		}
	}

	public String closeOutput () {
		try {
			// Close String
			if ( m_bOutputStringMode )
			{
				m_bOutputStringMode = false;
				if ( m_nOutputLayer == FilterOutputLayer.RTF )
					m_sbOutput.append(m_sEndDocument);
				m_nOutputLayer = FilterOutputLayer.NONE;
				if ( m_sbOutput == null ) return "";
				return m_sbOutput.toString();
			}
			
			// Else: close file
			if ( m_SW != null )
			{
				if ( m_nOutputLayer == FilterOutputLayer.RTF ) {
					m_SW.write(m_sEndDocument);
					m_SW.write("\r\n");
				}
				m_SW.close();
				m_SW = null;
			}
	
			m_nOutputLayer = FilterOutputLayer.NONE;
		}
		catch ( IOException E ) {
			m_Log.error(E.getLocalizedMessage());
		}
		
		return null; // For output file
	}

	public void generateAncillaryData(String p_sId,
		String p_sType,
		String p_sPath)
	{
		// No ancillary data for this filter
		p_sId = p_sType = p_sPath = null;
	}

	public String getCurrentEncoding() {
		return m_sInputEncoding;
	}

	public String getCurrentLanguage() {
		return m_sInputLanguage;
	}

	public String getDefaultDatatype() {
		return "properties";
	}

	public String getIdentifier() {
		return "okf_properties";
	}

	public String getInputLanguage() {
		return m_sInputLanguage;
	}

	public IFilterItem getItem() {
		return m_CurrentFI;
	}

	public int getLastItemID() {
		return m_nID;
	}

	public ILocalizationDirectives getLocalizationDirectives() {
		return m_Opt.m_LD;
	}

	public String getName () {
		return Res.getString("FILTER_NAME");
	}
	
	public String getDescription () {
		return Res.getString("FILTER_DESCRIPTION");
	}

	public String getOutputLanguage() {
		return m_sOutputLanguage;
	}

	public String[] getOutputLayer() {
		String[] aInfo = new String[9];
		aInfo[0] = Integer.valueOf(m_nOutputLayer).toString();
		aInfo[1] = m_sStartDocument;
		aInfo[2] = m_sEndDocument;
		aInfo[3] = m_sStartCode;
		aInfo[4] = m_sEndCode;
		aInfo[5] = m_sStartInline;
		aInfo[6] = m_sEndInline;
		aInfo[7] = m_sStartText;
		aInfo[8] = m_sEndText;
		return aInfo;
	}

	public IFilterItem getTranslatedItem () {
		// Not supported
		return null;
	}

	public void initialize (ILog p_Log) {
		m_Log = p_Log;
		resetInput();
	}

	public boolean loadParameters (String path,
		boolean ignoreErrors)
	{
		try {
			m_Opt.reset();
			m_Opt.load(path, ignoreErrors);
			return true;
		}
		catch ( Exception E ) {
			m_Log.error(E.getLocalizedMessage());
			return false;
		}
	}

	public boolean openInputFile (String p_sPath,
		String p_sLanguage,
		String p_sEncoding)
	{
		try {
			closeOutput();
			closeInput();

			// Must be set first
			m_bInputStringMode = false;
			
			// Default values from the user
			m_sInputLanguage = p_sLanguage;
			m_sInputEncoding = p_sEncoding;

			// Auto-detect encoding and line-breaks info before parsing
			String[] aInfo = Utils.detectFileInformation(p_sPath, true);
			if ( aInfo[0] != null ) m_sInputEncoding = aInfo[0];
			if ( aInfo[1] != null ) m_sLineBreak = aInfo[1];
			m_CurrentFI.setLineBreak(m_sLineBreak);

			// By default output language is the same as input
			m_sOutputLanguage = p_sLanguage;
			// By default output encoding is the same as input
			m_sOutputEncoding = m_sInputEncoding;

			// Load the DNL list if needed
			if ( m_Opt.m_LD.useDNLFile() )
			{
				m_Opt.m_LD.loadDNLFile(p_sPath);
			}

			resetInput();
			
			// Open the input stream
			// Doing it after resetInput, to avoid open to twice
			m_sPath = p_sPath;
			InputStream IS = new BufferedInputStream(new FileInputStream(m_sPath));
			m_SR = new BufferedReader(new InputStreamReader(IS, m_sInputEncoding));

			return true;
		}
		catch ( IOException E ) {
			m_Log.error(E.getLocalizedMessage());
			return false;
		}
	}

	public boolean openInputString (String p_sInput,
		String p_sLanguage,
		String p_sEncoding,
		long p_lOffsetInFile)
	{
		closeOutput();
		closeInput();

		m_sInputLanguage = p_sLanguage;
		m_sInputEncoding = p_sEncoding;
		// By default output language is the same as input
		m_sOutputLanguage = p_sLanguage;
		// By default output encoding is the same as input
		m_sOutputEncoding = p_sEncoding;

		m_bInputStringMode = true;
		m_sInput = p_sInput;
		if ( m_sInput == null ) m_sInput = "";

		// Set encoding, line-breaks info before parsing
		m_sInputEncoding = "UTF-16";
		m_sLineBreak = LineBreakType.UNIX;
		m_CurrentFI.setLineBreak(m_sLineBreak);
		resetInput();
		return true;
	}

	public boolean openOutputFile (String p_sPath) {
		try {
			m_bOutputStringMode = false;

			// Create the folder for the output if needed
			Utils.createDirectories(p_sPath);

			// Make sure we use an RTF-friendly encoding
			if ( m_nOutputLayer == FilterOutputLayer.RTF )
			{
				String sRTF = Utils.getANSIEncoding(m_sOutputLanguage);
				if ( sRTF.length() > 0 )
				{
					if ( m_sOutputEncoding.toLowerCase() != sRTF.toLowerCase() ) {
						m_Log.warning(String.format(Res.getString("CHANGING_OUTENCODING"),
							m_sOutputEncoding, sRTF));
					}
					m_sOutputEncoding = sRTF;
				}
			}

			OutputStream OS = new BufferedOutputStream(new FileOutputStream(p_sPath));
			m_SW = new OutputStreamWriter(OS, m_sOutputEncoding);
			Charset Chs = Charset.forName(m_sOutputEncoding);
			m_OutputEncoder = Chs.newEncoder(); 
			
			// Write header in case of RTF output
			if ( m_nOutputLayer == FilterOutputLayer.RTF )
			{
				m_SW.write(m_sStartDocument);
				m_SW.write("\r\n");
			}
			return true;
		}
		catch ( Exception E ) {
			m_Log.error(E.getLocalizedMessage());
			return false;
		}
	}

	public boolean openOutputString () {
		m_bOutputStringMode = false;
		m_SW = new StringWriter();
		m_OutputEncoder = Charset.forName("UTF-16").newEncoder();
		return true;
	}

	public boolean queryProperty (int p_nProperty) {
		switch ( p_nProperty )
		{
			case FilterProperty.INPUTFILE:
			case FilterProperty.INPUTSTRING:
			case FilterProperty.TEXTBASED:
			case FilterProperty.OUTPUTFILE:
			case FilterProperty.OUTPUTSTRING:
			case FilterProperty.RTFOUTPUT:
				return true;

			case FilterProperty.BILINGUALINPUT:
			case FilterProperty.XMLOUTPUT:
			case FilterProperty.ANCILLARYOUTPUT:
			case FilterProperty.ISINDEMOMODE:
			default:
				return false;
		}
	}

	public int readItem ()
	{
		boolean        bMultiline = false;
		String         sKey = "";
		String         sValue = "";
		int            nStartText = 0;
		long           lS = -1;

		try
		{
			m_CurrentFI.reset();
			if ( !m_Log.setLog(LogType.SUBPROGRESS, getPercentageDone(), null) )
			{
				m_CurrentFI.setItemType(FilterItemType.USERCANCEL);
				return m_CurrentFI.getItemType();
			}

			m_sbBuffer = new StringBuilder();
			m_sbKeyBuffer = new StringBuilder();
			m_sbTextBuffer = new StringBuilder();

			while ( true )
			{
				if ( m_bReadLine )
				{
					int nRes = getNextLine();
					if ( nRes < 0 )
					{
						m_CurrentFI.setItemType(FilterItemType.ERROR);
						return m_CurrentFI.getItemType();
					}
					if ( nRes == 0 )
					{
						if ( m_sbBuffer.length() > 0 )
						{
							// Some data still to pass along
							m_CurrentFI.setItemType(FilterItemType.BLOCK);
							m_bEndingLB = false; // No ending line-break;
						}
						else 	// Else: end of input
						{
							m_CurrentFI.setItemType(FilterItemType.ENDINPUT);
							m_Log.setLog(LogType.SUBPROGRESS, 100, null);
						}
						return m_CurrentFI.getItemType();
					}
				}
				else {
					m_bReadLine = true;
				}

				// Remove any leading white-spaces
				String sTmp = Utils.trimStart(m_sLine, "\t\r\n \f");

				if ( bMultiline ) {
					sValue += sTmp;
				}
				else {
					// Empty lines
					if ( sTmp.length() == 0 ) {
						m_sbBuffer.append(m_sLine);
						m_sbBuffer.append(m_sLineBreak);
						continue;
					}

					// Comments
					boolean bComments = (( sTmp.charAt(0) == '#' ) || ( sTmp.charAt(0) == '!' ));
					if ( !bComments &&  m_Opt.m_bExtraComments ) {
						bComments = (sTmp.charAt(0) == ';'); // .NET style
						if ( sTmp.startsWith("//") ) bComments = true; // C++/Java-style
					}

					if ( bComments ) {
						m_Opt.m_LD.process(sTmp);
						m_sbBuffer.append(m_sLine);
						m_sbBuffer.append(m_sLineBreak);
						continue;
					}

					// Get the key
					boolean bEscape = false;
					int n = 0;
					for ( int i=0; i<sTmp.length(); i++ )
					{
						if ( bEscape ) bEscape = false;
						else
						{
							if ( sTmp.charAt(i) == '\\' )
							{
								bEscape = true;
								continue;
							}
							if (( sTmp.charAt(i) == ':' ) || ( sTmp.charAt(i) == '=' )
								|| ( Character.isWhitespace(sTmp.charAt(i)) ))
							{
								// That the first white-space after the key
								n = i;
								break;
							}
						}
					}

					// Get the key
					if ( n == 0 ) {
						// Line empty after the key
						n = sTmp.length();
					}
					sKey = sTmp.substring(0, n); //Len=n

					// Gets the value
					boolean bEmpty = true;
					boolean bCheckEqual = true;
					for ( int i=n; i<sTmp.length(); i++ ) {
						if ( bCheckEqual && (( sTmp.charAt(i) == ':' ) || ( sTmp.charAt(i) == '=' )))
						{
							bCheckEqual = false;
							continue;
						}
						if ( !Character.isWhitespace(sTmp.charAt(i)) )
						{
							// That the first white-space after the key
							n = i;
							bEmpty = false;
							break;
						}
					}

					if ( bEmpty ) n = sTmp.length();
					sValue = sTmp.substring(n);
					// Real text start point (adjusted for trimmed characters)
					nStartText = n + (m_sLine.length() - sTmp.length());
					// Use m_nLineSince-1 to not count the current one
					lS = (m_lPosition-(m_sLine.length()+(m_nLineSince-1))) + nStartText;
					m_nLineSince = 0; // Reset the line counter for next time
				}

				// Is it a multilines entry?
				if ( sValue.endsWith("\\") ) {
					// Make sure we have an odd number of ending '\'
					int n = 0;
					for ( int i=sValue.length()-1;
						(( i > -1 ) && ( sValue.charAt(i) == '\\' ));
						i-- ) n++;

					if ( (n % 2) != 0 ) // Continue onto the next line
					{
						sValue = sValue.substring(0, sValue.length()-1); //Len=sValue.length()-1
						bMultiline = true;
						// Preserve parsed text in case we do not extract
						if ( m_sbKeyBuffer.length() == 0 ) {
							m_sbKeyBuffer.append(m_sLine.substring(0, nStartText));
							nStartText = 0; // Next time we get the whole line
						}
						m_sbTextBuffer.append(m_sLine.substring(nStartText));
						continue; // Read next line
					}
				}

				// Check for key condition
				// Then for directives (they can overwrite the condition)
				//TODO: Revisit if key should override directives or reverse
				boolean bExtract = true;
				
				if ( m_Opt.m_bUseKeyCondition ) {
					if ( m_Opt.m_bExtractOnlyMatchingKey ) {
						if ( m_RE.matcher(sKey).matches() )
							bExtract = m_Opt.m_LD.isLocalizable(true);
						else
							bExtract = false;
					}
					else // Extract all but items with matching keys
					{
						if ( !m_RE.matcher(sKey).matches() )
							bExtract = m_Opt.m_LD.isLocalizable(true);
						else
							bExtract = false;
					}
				}
				
				if ( bExtract ) bExtract = m_Opt.m_LD.isLocalizable(true);
				else {
					// Make sure we pop/push the directives even if the 
					// outcome is already decided, otherwise it gets out-of-sync
					m_Opt.m_LD.isLocalizable(true);
				}

				if ( bExtract ) {
					m_CurrentFI.setText(unescape(sValue));
					m_CurrentFI.setResName(sKey);

					// Check the DNL list here to have resname, etc.
					bExtract = !m_Opt.m_LD.isInDNLList(m_CurrentFI);
				}

				if ( bExtract ) {
					// Parts before the text
					if ( m_sbKeyBuffer.length() == 0 ) {
						// Single-line case
						m_sbKeyBuffer.append(m_sLine.substring(0, nStartText));
					}
					m_sbBuffer.append(m_sbKeyBuffer);
				}
				else {
					m_sbBuffer.append(m_sbKeyBuffer);
					m_sbBuffer.append(m_sbTextBuffer);
					m_sbBuffer.append(m_sLine);
					m_CurrentFI.setItemType(FilterItemType.BLOCK);
					return m_CurrentFI.getItemType();
				}

				m_CurrentFI.setPreFormatted(true);
//m_CurrentFI.SetText(UnEscape(sValue));
//m_CurrentFI.SetResName(sKey);
				m_CurrentFI.setItemID(++m_nID);
				m_CurrentFI.setItemType(FilterItemType.TEXT);
				m_CurrentFI.setStart(lS);

//TODO: handle {0,choice...} cases
//http://java.sun.com/j2se/1.4.2/docs/api/java/text/MessageFormat.html

				if ( m_Opt.m_bUseCodeFinder )
					m_Opt.m_CodeFinder.processFilterItem(m_CurrentFI);

				return m_CurrentFI.getItemType();
			}
		}
		catch ( Exception E ) {
			logMessage(LogType.ERROR, E.getMessage());
			m_CurrentFI.setItemType(FilterItemType.ERROR);
		}

		return m_CurrentFI.getItemType();
	}

	public void resetInput () {
		try {
			if ( m_SR != null ) {
				m_SR.close();
				// Open the input stream
				InputStream IS = new BufferedInputStream(new FileInputStream(m_sPath));
				m_SR = new BufferedReader(new InputStreamReader(IS, m_sInputEncoding));
			}
	
			m_bEndingLB = true;
			m_CurrentFI.reset();
			m_nID = 0;
			m_nLine = 0;
			m_nLineSince = 0;
			m_nStringIndex = 0;
			m_lPosition = 0;
			m_lBytePos = 0;
			m_bReadLine = true;
			m_Log.setSubProgressMode(LogProgressMode.PERCENTAGE);

			if ( m_Opt.m_bUseKeyCondition ) {
				m_RE = Pattern.compile(m_Opt.m_sKeyCondition);
			}
		}
		catch ( Exception E ) {
			m_Log.error(E.getLocalizedMessage());
		}
	}

	public boolean saveParameters (String path,
		String prefix)
	{
		try {
			m_Opt.save(path);
			return true;
		}
		catch ( Exception E ) {
			m_Log.error(E.getLocalizedMessage());
			return false;
		}
	}

	public void setAncillaryDirectory (String p_sInputRoot,
		String p_sAncillaryRoot)
	{
		// No ancillary file for this filter
	}

	public void setLastItemID (int p_nId) {
		m_nID = p_nId;
	}

	public boolean setLocalizationDirectives (ILocalizationDirectives p_LD) {
		m_Opt.m_LD = p_LD;
		return true;
	}

	public boolean setOutputOptions (String p_sLanguage,
		String p_sEncoding)
	{
		m_sOutputLanguage = p_sLanguage;
		m_sOutputEncoding = p_sEncoding;
		Charset Chs = Charset.forName(m_sOutputEncoding);
		m_OutputEncoder = Chs.newEncoder(); 
		return true;
	}

	public boolean useOutputLayer(int p_nLayer,
		String p_sStartDocument,
		String p_sEndDocument,
		String p_sStartCode,
		String p_sEndCode,
		String p_sStartInline,
		String p_sEndInline,
		String p_sStartText,
		String p_sEndText)
	{
		m_nOutputLayer = p_nLayer;
		m_sStartDocument = p_sStartDocument;
		m_sEndDocument = p_sEndDocument;
		m_sStartCode = p_sStartCode;
		m_sEndCode = p_sEndCode;
		m_sStartInline = p_sStartInline;
		m_sEndInline = p_sEndInline;
		m_sStartText = p_sStartText;
		m_sEndText = p_sEndText;
		return true;
	}

	public void writeItem () {
		try {
			String sTmp;
			switch ( m_CurrentFI.getItemType() )
			{
	//TODO: escape cases for extended char as well as special first chars ( wspaces, : and =)
				case FilterItemType.TEXT:
					if ( m_nOutputLayer == FilterOutputLayer.RTF ) {
						if ( m_CurrentFI.isTranslatable() ) {
							sTmp = RTFStyle.ENDCODE
								+ m_CurrentFI.getText(FilterItemText.RTF)
								+ RTFStyle.STARTCODE;
						}
						else {
							sTmp = Utils.escapeToRTF(
								m_CurrentFI.getText(FilterItemText.ORIGINAL), true,
								0, m_OutputEncoder.charset());
						}
					}
					else {
						sTmp = escape(m_CurrentFI.getText(FilterItemText.ORIGINAL));
					}
	
					if ( m_nOutputLayer == FilterOutputLayer.RTF ) {
						if ( m_bOutputStringMode ) {
							m_sbOutput.append(Utils.escapeToRTF(
								m_sbBuffer.toString(), true, 0, m_OutputEncoder.charset()));
							m_sbOutput.append(sTmp);
						}
						else {
							m_SW.write(Utils.escapeToRTF(
								m_sbBuffer.toString(), true, 0, m_OutputEncoder.charset()));
							m_SW.write(sTmp);
						}
					}
					else {
						if ( m_bOutputStringMode ) {
							m_sbOutput.append(m_sbBuffer);
							m_sbOutput.append(sTmp);
						}
						else {
							m_SW.write(m_sbBuffer.toString());
							m_SW.write(sTmp);
						}
					}
					writeLineBreak();
					break;
	
				case FilterItemType.BLOCK:
				case FilterItemType.STARTGROUP:
				case FilterItemType.ENDGROUP:
					if ( m_nOutputLayer == FilterOutputLayer.RTF ) {
						if ( m_bOutputStringMode )
							m_sbOutput.append(Utils.escapeToRTF(
								m_sbBuffer.toString(), true, 1, m_OutputEncoder.charset()));
						else
							m_SW.write(Utils.escapeToRTF(
								m_sbBuffer.toString(), true, 1, m_OutputEncoder.charset()));
					}
					else {
						if ( m_bOutputStringMode ) m_sbOutput.append(m_sbBuffer);
						else m_SW.write(m_sbBuffer.toString());
					}
					writeLineBreak();
					break;
			}
		}
		catch ( Exception E ) {
			logMessage(LogType.ERROR, E.getMessage());
		}
	}

	public String getOption (String p_sName) {
		//TODO: Implement better handling of GetOption()
		FieldsString FS = new FieldsString(getOptions());
		return FS.get(p_sName, null);
	}

	public String getOptions () {
		return m_Opt.toString();
	}

	public void setOption (String p_sName,
		String p_sValue)
	{
		//TODO: Implement better handling of GetOption()
		FieldsString FS = new FieldsString(getOptions());
		FS.set(p_sName, p_sValue);
		setOptions(FS.toString());
	}

	public void setOptions (String p_sValue) {
		m_Opt.fromString(p_sValue);
	}
	
	private void logMessage (int p_nType,
		String p_sText)
	{
		m_Log.setLog(p_nType, 0, String.format(
			Res.getString("LINE_LOCATION"), m_nLine) + p_sText);
	}

	private void writeLineBreak ()
		throws IOException
	{
		// Output line-break if necessary
		if ( !m_bEndingLB ) return;
		if ( m_nOutputLayer == FilterOutputLayer.RTF ) {
			if ( m_bOutputStringMode )
				m_sbOutput.append(RTFStyle.ENDCODE + "\r\n\\par " + RTFStyle.STARTCODE);
			else 
				m_SW.write(RTFStyle.ENDCODE + "\r\n\\par " + RTFStyle.STARTCODE);
		}
		else {
			if ( m_bOutputStringMode ) m_sbOutput.append(m_sLineBreak);
			else m_SW.write(m_sLineBreak);
		}
	}

	private int getPercentageDone ()
	{
		if ( m_bInputStringMode ) {
			return Utils.getPercentage(m_nStringIndex, m_sInput.length());
		}
		// Else: input file
		return 0; //TODO return Routines.getPercentage(m_lBytePos, m_SR.BaseStream.Length);
	}

	private String unescape (String p_sValue)
	{
		if ( p_sValue.indexOf('\\') == -1 ) return p_sValue;

		m_sbEscape = new StringBuilder();
		for ( int i=0; i<p_sValue.length(); i++ ) {
			if ( p_sValue.charAt(i) == '\\' ) {
				if ( i+1 < p_sValue.length() ) {
					switch ( p_sValue.charAt(i+1) ) {
					case 'u':
						if ( i+5 < p_sValue.length() ) {
							try {
								int nTmp = Integer.parseInt(p_sValue.substring(i+2, i+6), 16);
								m_sbEscape.append((char)nTmp);
							}
							catch ( Exception E ) {
								logMessage(LogType.WARNING,
									String.format(Res.getString("INVALID_UESCAPE"),
									p_sValue.substring(i+2, i+6)));
							}
							i += 5;
							continue;
						}
						else {
							logMessage(LogType.WARNING,
								String.format(Res.getString("INVALID_UESCAPE"),
								p_sValue.substring(i+2)));
						}
						break;
					case '\\':
						m_sbEscape.append("\\\\");
						i++; // Next '\' will be set below
						continue;
					}
				}
			}
			m_sbEscape.append(p_sValue.charAt(i));
		}
		return m_sbEscape.toString();
	}

	private String escape (String p_sValue)
	{
		m_sbEscape = new StringBuilder();
		for ( int i=0; i<p_sValue.length(); i++ ) {
			if ( p_sValue.codePointAt(i) > 127 ) {
				if ( m_Opt.m_bEscapeExtendedChars ) {
					m_sbEscape.append(String.format("\\u%04x", p_sValue.codePointAt(i))); 
				}
				else {
					if ( m_OutputEncoder.canEncode(p_sValue.charAt(i)) )
						m_sbEscape.append(p_sValue.charAt(i));
					else
						m_sbEscape.append(String.format("\\u%04x", p_sValue.codePointAt(i)));
				}
			}
			else m_sbEscape.append(p_sValue.charAt(i));
		}
		return m_sbEscape.toString();
	}

	/**
	 * Gets the next line of the string or file input.
	 * @return 0=end of input, 1=m_sLine has been set to the new line.
	 */
	private int getNextLine ()
		throws IOException
	{
		while ( true ) {
			if ( m_bInputStringMode ) readLineFromString();
			else {
				m_sLine = m_SR.readLine();
				if ( m_sLine != null )
				{
					m_nLine++;
					m_nLineSince++;
					// We count char instead of byte, while the BaseStream.Length is in byte
					// Not perfect, but better than nothing.
					m_lPosition += m_sLine.length() + 1; // +1 For the line-break
					m_lBytePos += m_sLine.length();
				}
				//TODO else m_lBytePos = m_lPosition = m_SR.BaseStream.Length;
			}
			if ( m_sLine == null ) return 0; // End of input
			return 1;
		}
	}

	/**
	 * reads a line from the string input. The line read is placed in
	 * m_sLine. If there is no more lines, m_sLine is set to null.
	 */
	private void readLineFromString ()
	{
		if ( m_nStringIndex >= m_sInput.length() ) {
			m_sLine = null; // End of string reached
			return;
		}

		int nStart = m_nStringIndex;
		while ( true ) {
			if ( m_nStringIndex == m_sInput.length() ) {
				m_sLine = m_sInput.substring(nStart, m_nStringIndex); //Len=(m_nStringIndex-nStart)
				// No m_nLine++ because not ending on a line-break
				return;
			}

			if ( m_sInput.charAt(m_nStringIndex) == '\r' ) {
				if ( m_nStringIndex < (m_sInput.length()-1) ) {
					if ( m_sInput.charAt(m_nStringIndex+1) == '\n' ) {
						m_sLine = m_sInput.substring(nStart, m_nStringIndex); //Len=(m_nStringIndex-nStart)
						m_nStringIndex += 2;
						m_nLine++;
						m_nLineSince++;
						return;
					}
				}
				// Else: \r only
				m_sLine = m_sInput.substring(nStart, m_nStringIndex); //Len=(m_nStringIndex-nStart)
				m_nStringIndex++;
				m_nLine++;
				m_nLineSince++;
				return;
			}
			else if ( m_sInput.charAt(m_nStringIndex) == '\n' ) {
				m_sLine = m_sInput.substring(nStart, m_nStringIndex); //Len=(m_nStringIndex-nStart)
				m_nStringIndex++;
				m_nLine++;
				m_nLineSince++;
				return;
			}

			m_nStringIndex++;
		}
	}

}
