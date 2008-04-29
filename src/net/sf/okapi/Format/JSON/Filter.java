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

package net.sf.okapi.Format.JSON;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import net.sf.okapi.Filter.FilterItem;
import net.sf.okapi.Filter.FilterItemText;
import net.sf.okapi.Filter.FilterItemType;
import net.sf.okapi.Filter.FilterOutputLayer;
import net.sf.okapi.Filter.FilterProperty;
import net.sf.okapi.Filter.IFilter;
import net.sf.okapi.Filter.IFilterItem;
import net.sf.okapi.Filter.ILocalizationDirectives;
import net.sf.okapi.Library.Base.ILog;
import net.sf.okapi.Library.Base.IParameters;
import net.sf.okapi.Library.Base.LineBreakType;
import net.sf.okapi.Library.Base.LogProgressMode;
import net.sf.okapi.Library.Base.LogType;
import net.sf.okapi.Library.Base.RTFStyle;
import net.sf.okapi.Library.Base.Utils;

public class Filter implements IFilter {
	
	private static final int TOKEN_STARTOBJECT   = 0;
	private static final int TOKEN_ENDOBJECT     = 1;
	private static final int TOKEN_STARTARRAY    = 2;
	private static final int TOKEN_ENDARRAY      = 3;
	private static final int TOKEN_ENDINPUT      = 4;
	private static final int TOKEN_STRING        = 5;
	private static final int TOKEN_KEYVALUESEP   = 6;
	private static final int TOKEN_SEPARATOR     = 7;
	private static final int TOKEN_THING         = 8;

	private ILog             m_Log;
	private FilterItem       m_CurrentFI;
	private String           m_sInputLanguage;
	private String           m_sInputEncoding;
	private String           m_sOutputLanguage;
	private String           m_sOutputEncoding;
	private Parameters       m_Opt;

	private String           m_sInput;
	private int              m_nCurrent;
	private String           m_sOriginalLineBreak;
	private String           m_sLineBreak;
	private int              m_nObjectCount;
	private int              m_nArrayCount;
	private StringBuilder    m_sbBuffer;
	private int              m_nID;
	private int              m_nStartRead;
	private int              m_nStartString;
	private int              m_nEndString;
	private int              m_nLastTrigger;

	private Writer           m_SW = null;
	private boolean          m_bOutputStringMode;
	private Charset          m_OutputEncoding;
	private CharsetEncoder   m_OutputEncoder;
	private StringBuilder    m_sbEscape;

	private int              m_nOutputLayer;
	private String           m_sStartDocument;
	private String           m_sEndDocument;
	private String           m_sStartCode;
	private String           m_sEndCode;
	private String           m_sStartInline;
	private String           m_sEndInline;
	private String           m_sStartText;
	private String           m_sEndText;


	public IParameters getParameters () {
		return m_Opt;
	}
	
	public void setParameters (IParameters paramsObject) {
		m_Opt = (Parameters)paramsObject;
	}
	
	public void closeInput () {
		// The file is already closed.
		m_sInput = null; // Allows to dispose of the memory
		if ( m_Opt != null ) m_Opt.m_LD.reset();
	}

	public String closeOutput () {
		try {
			if ( m_SW != null ) {
				if ( m_nOutputLayer == FilterOutputLayer.RTF ) {
					m_SW.write(m_sEndDocument);
					m_SW.write("\r\n");
				}
				m_nOutputLayer = FilterOutputLayer.NONE;

				if ( m_bOutputStringMode ) {
					return m_SW.toString();
				}
				else {
					m_SW.close();
					m_SW = null;
				}
			}
		}
		catch ( Exception E ) {
			m_Log.error(E.getMessage());
		}
		return null; // For file output
	}

	public void generateAncillaryData (String p_sId,
		String p_sType,
		String p_sPath)
	{
		// No ancillary file for this filter
		p_sId = p_sType = p_sPath = null;
	}

	public String getCurrentEncoding () {
		return m_sInputEncoding;
	}

	public String getCurrentLanguage () {
		return m_sInputLanguage;
	}

	public String getDefaultDatatype () {
		return "x-json";
	}

	public String getIdentifier () {
		return "okf_json";
	}

	public String getInputLanguage () {
		return m_sInputLanguage;
	}

	public IFilterItem getItem () {
		return m_CurrentFI;
	}

	public ILocalizationDirectives getLocalizationDirectives () {
		return m_Opt.m_LD;
	}

	public String getName() {
		return Res.getString("FILTER_NAME");
	}

	public String getDescription () {
		return Res.getString("FILTER_DESCRIPTION");
	}

	public String getOutputLanguage() {
		return m_sOutputLanguage;
	}

	public IFilterItem getTranslatedItem() {
		// Not supported with this filter
		return null;
	}

	public void initialize(ILog p_Log) {
		m_Log = p_Log;
		m_CurrentFI = new FilterItem();
		m_Opt = new Parameters();
		m_Opt.m_LD.setLog(m_Log);
	}

	/*public boolean loadParameters (String path,
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
	}*/

	public boolean openInputFile (String p_sPath,
		String p_sLanguage,
		String p_sEncoding)
	{
		try {
			closeOutput();
			closeInput();
			
			m_sInputLanguage = p_sLanguage;
			m_sInputEncoding = p_sEncoding;
			
			//TODO: detection for encoding!!!

			// Open the file
			InputStream IS = new BufferedInputStream(new FileInputStream(p_sPath));
			Reader SR = new InputStreamReader(IS, m_sInputEncoding);
			// Read the file in one string
			StringBuffer sbTmp = new StringBuffer(1024);
			char[] aBuf = new char[1024];
			int nCount;
			while ((nCount = SR.read(aBuf)) > -1) {
				sbTmp.append(aBuf, 0, nCount);	
			}
			m_sInput = sbTmp.toString();
			SR.close();
			SR = null;
			
			// Detect and normalize line-breaks
			m_sOriginalLineBreak = LineBreakType.DOS; // Default
			if ( m_sInput.indexOf("\r\n") != -1 ) {
				m_sInput = m_sInput.replace("\r\n", "\n");
			}
			else if ( m_sInput.indexOf('\n') != -1 ) {
				m_sOriginalLineBreak = LineBreakType.UNIX;
			}
			else if ( m_sInput.indexOf('\r') != -1 ) {
				m_sOriginalLineBreak = LineBreakType.MAC;
				m_sInput = m_sInput.replace('\r', '\n');
			}
			// Default line-break type
			m_sLineBreak = m_sOriginalLineBreak;
			m_CurrentFI.setLineBreak(m_sLineBreak);

			if ( m_Opt.m_LD.useDNLFile() ) {
				m_Opt.m_LD.loadDNLFile(p_sPath);
			}
			resetInput();
		}
		catch ( Exception E ) {
			m_Log.error(E.getMessage() + "\n" + E.getStackTrace());
			return false;
		}
		return true;
	}

	public boolean openInputString(String p_sInput,
		String p_sLanguage,
		String p_sEncoding,
		long p_lOffsetInFile)
	{
		closeOutput();
		closeInput();
		
		m_sInputLanguage = p_sLanguage;
		m_sInputEncoding = p_sEncoding;

		m_sInput = p_sInput;
		if ( m_sInput == null ) m_sInput = "";

		//TODO: detect line-break
		
		resetInput();
		return true;
	}

	public boolean openOutputFile (String p_sPath) {
		try {
			m_bOutputStringMode = false;

			// Create the folder for the output if needed
			Utils.createDirectories(p_sPath);

			// Make sure we use an RTF-friendly encoding
			if ( m_nOutputLayer == FilterOutputLayer.RTF ) {
				String sRTF = Utils.getANSIEncoding(m_sOutputLanguage);
				if ( sRTF.length() > 0 ) {
					if ( m_sOutputEncoding.toLowerCase() != sRTF.toLowerCase() ) {
						m_Log.warning(String.format(Res.getString("CHANGING_OUTENCODING"),
							m_sOutputEncoding, sRTF));
					}
					m_sOutputEncoding = sRTF;
				}
			}

			OutputStream OS = new BufferedOutputStream(new FileOutputStream(p_sPath));
			m_SW = new OutputStreamWriter(OS, m_sOutputEncoding);
			m_OutputEncoding = Charset.forName(m_sOutputEncoding);
			m_OutputEncoder = m_OutputEncoding.newEncoder(); 
			
			// Write header in case of RTF output
			if ( m_nOutputLayer == FilterOutputLayer.RTF ) {
				m_SW.write(m_sStartDocument);
				m_SW.write("\r\n");
			}
			return true;
		}
		catch ( Exception E ) {
			m_Log.error(E.getMessage() + "\n" + E.getStackTrace());
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
		switch ( p_nProperty ) {
			case FilterProperty.INPUTFILE:
			case FilterProperty.OUTPUTFILE:
			case FilterProperty.TEXTBASED:
			case FilterProperty.RTFOUTPUT:
			case FilterProperty.INPUTSTRING:
			case FilterProperty.OUTPUTSTRING:
				return true;

			case FilterProperty.BILINGUALINPUT:
			case FilterProperty.XMLOUTPUT:
			case FilterProperty.ANCILLARYOUTPUT:
			case FilterProperty.ISINDEMOMODE:
			default:
				return false;
		}
	}

	public int readItem() {
		try {
			m_nStartRead = m_nCurrent;
			m_CurrentFI.reset();
			if ( !m_Log.setLog(LogType.SUBPROGRESS,
				Utils.getPercentage(m_nCurrent, m_sInput.length()), null) ) {
				m_CurrentFI.setItemType(FilterItemType.USERCANCEL);
				return m_CurrentFI.getItemType();
			}

			int nToken;
			String sKey = "";
			int nState = 0;
			boolean bPrevWasString = false;

			while ( (nToken = readToken()) != TOKEN_ENDINPUT ) {
				switch ( nState ) {
					case 0: // Start state
						switch ( nToken ) {
							case TOKEN_KEYVALUESEP:
								sKey = m_sbBuffer.toString();
								bPrevWasString = false;
								nState = 1;
								break;

							case TOKEN_SEPARATOR:
							case TOKEN_ENDARRAY:
								if (( bPrevWasString ) && ( m_Opt.m_bExtractStandalone )) {
									if ( doExtract(null) )
										return m_CurrentFI.getItemType();
								}
								bPrevWasString = false;
								break;

							case TOKEN_STRING:
								bPrevWasString = true;
								break;

							default:
								bPrevWasString = false;
								break;
						}
						break;

					case 1: // After a key
						nState = 0;
						bPrevWasString = false;
						if ( nToken == TOKEN_STRING ) {
							if ( doExtract(sKey) )
								return m_CurrentFI.getItemType();
						}
						break;
				}
			}

			if ( nToken == TOKEN_ENDINPUT ) {
				if (( m_nStartRead == m_nCurrent ) && ( m_nLastTrigger != m_nCurrent )) {
					m_CurrentFI.setItemType(FilterItemType.ENDINPUT);
				}
				else {
					m_nLastTrigger = -1;
					m_CurrentFI.setItemType(FilterItemType.BLOCK);
				}
			}
		}
		catch ( Exception E ) {
			errorMessage(E.getLocalizedMessage());
			m_CurrentFI.setItemType(FilterItemType.ERROR);
		}

		return m_CurrentFI.getItemType();
	}

	public void resetInput () {
		m_Log.setSubProgressMode(LogProgressMode.PERCENTAGE);
		m_nObjectCount = 0;
		m_nArrayCount = 0;
		m_sbBuffer = new StringBuilder();
		m_nID = 0;
		m_nCurrent = 0;
		m_nLastTrigger = -1;
		m_sbEscape = new StringBuilder();
	}
	
	/*public boolean saveParameters (String path,
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
	}*/

	public void setAncillaryDirectory(String p_sInputRoot,
		String p_sAncillaryRoot) {
		// No ancillary file for this filter
	}

	public boolean setLocalizationDirectives(ILocalizationDirectives p_LD) {
		m_Opt.m_LD = p_LD;
		return true;
	}

	public boolean setOutputOptions(String p_sLanguage,
		String p_sEncoding)
	{
		m_sOutputLanguage = p_sLanguage;
		m_sOutputEncoding = p_sEncoding;
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
			if (( m_CurrentFI.getItemType() == FilterItemType.TEXT )
				&& m_CurrentFI.isTranslatable() ) {
				if ( m_nOutputLayer == FilterOutputLayer.RTF ) {
					m_SW.write(Utils.escapeToRTF(
						m_sInput.substring(m_nStartRead, m_nStartString+1), //LEN=(m_nStartString+1)-m_nStartRead
						true, 0, m_OutputEncoding));
					m_SW.write(RTFStyle.ENDCODE
						+ m_CurrentFI.getText(FilterItemText.RTF)
						+ RTFStyle.STARTCODE);
					if ( m_nEndString < m_nCurrent ) {
						m_SW.write(Utils.escapeToRTF(
							m_sInput.substring(m_nEndString, m_nCurrent), //LEN=m_nCurrent-m_nEndString
							true, 0, m_OutputEncoding));
					}
				}
				else {
					m_SW.write(escapeChars(m_sInput.substring(m_nStartRead, m_nStartString+1))); //LEN=(m_nStartString+1)-m_nStartRead
					m_SW.write(escapeChars(m_CurrentFI.getText(FilterItemText.ORIGINAL)));
					if ( m_nEndString < m_nCurrent ) {
						m_SW.write(escapeChars(m_sInput.substring(m_nEndString, m_nCurrent))); //LEN=m_nCurrent-m_nEndString
					}
				}
				return;
			}

			if ( m_CurrentFI.getItemType() == FilterItemType.ENDINPUT ) return;

			int n = ((m_CurrentFI.getItemType()==FilterItemType.BLOCK) ? 1 : 0);
			if ( m_nOutputLayer == FilterOutputLayer.RTF )
				m_SW.write(Utils.escapeToRTF(
					m_sInput.substring(m_nStartRead, m_nCurrent+n), //LEN=(m_nCurrent-m_nStartRead)+n
					true, 0, m_OutputEncoding));
			else
				m_SW.write(escapeChars(m_sInput.substring(m_nStartRead, m_nCurrent+n))); //LEN=(m_nCurrent-m_nStartRead)+n
		}
		catch ( Exception E ) {
			m_Log.error(E.getLocalizedMessage());
		}
	}

	public int getLastItemID() {
		return m_nID;
	}

	public void setLastItemID (int p_nID) {
		m_nID = p_nID;
	}
	
	public String[] getOutputLayer () {
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

/*	public String getOption (String p_sName) {
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
*/	
	private String escapeChars (String p_sValue) {
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

	private void errorMessage (String p_sText) {
		m_Log.error(String.format("[0%04x] %s", m_nCurrent, p_sText));
	}

	private int readThing ()
		throws Exception
	{
		m_sbBuffer = new StringBuilder();
		while ( true ) {
			if ( m_nCurrent+1 >= m_sInput.length() ) {
				throw new Exception(Res.getString("ERROR_UNEXCPECTEDENDOINPUT"));
			}
			switch ( m_sInput.charAt(++m_nCurrent) ) {
				case '}':
				case ']':
					m_nCurrent--; // For next ReadToken()
					return TOKEN_THING;

				default:
					if ( Character.isWhitespace(m_sInput.charAt(m_nCurrent)) ) {
						// No need for m_nCurrent--;
						return TOKEN_THING;
					}
					// Else: keep adding to this token
					m_sbBuffer.append(m_sInput.charAt(m_nCurrent));
					break;
			}
		}
	}

	private int readString ()
		throws Exception
	{
		int nState = 0;
		m_nStartString = m_nCurrent;
		m_sbBuffer = new StringBuilder();

		while ( true ) {
			if ( m_nCurrent+1 >= m_sInput.length() ) {
				throw new Exception(Res.getString("ERROR_UNEXCPECTEDENDOFSTRING"));
			}

			switch ( nState ) {
				case 0:
					switch ( m_sInput.charAt(++m_nCurrent) ) {
						case '"': // End of string
							m_nEndString = m_nCurrent;
							return TOKEN_STRING;
						case '\\': // Start escape
							nState = 1;
							break;
						default:
							m_sbBuffer.append(m_sInput.charAt(m_nCurrent));
							break;
					}
					break;

				case 1: // After '\'
					nState = 0;
					switch ( m_sInput.charAt(++m_nCurrent) ) {
						case 'b':
						case 'f':
						case 'n':
						case 'r':
						case 't':
							m_sbBuffer.append('\\');
							m_sbBuffer.append(m_sInput.charAt(m_nCurrent));
							break;
						case 'u':
							nState = 2;
							break;
						case '\\':
						case '/':
						case '"':
							m_sbBuffer.append('\\');
							m_sbBuffer.append(m_sInput.charAt(m_nCurrent));
							break;
						default: // Unexpected escape sequence
							m_Log.warning(String.format("WARN_UNEXPECTEDESCAPE", m_sInput.charAt(m_nCurrent)));
							m_sbBuffer.append('\\');
							m_sbBuffer.append(m_sInput.charAt(m_nCurrent));
							break;
					}
					break;

				case 2: // After 'bslash+u'
					m_nCurrent++;
					String sValue = m_sInput.substring(m_nCurrent, m_nCurrent+4); //LEN=4
					int nValue = Integer.parseInt(sValue, 16);
					m_sbBuffer.append((char)nValue);
					m_nCurrent += 3;
					nState = 0;
					break;
			}
		}
	}

	private int readToken ()
		throws Exception
	{
		while ( true ) {
			if ( m_nCurrent+1 >= m_sInput.length() ) {
				return TOKEN_ENDINPUT;
			}

			switch ( m_sInput.charAt(++m_nCurrent) ) {
				case '{':
					m_nObjectCount++;
					return TOKEN_STARTOBJECT;
				case '}':
					m_nObjectCount--;
					return TOKEN_ENDOBJECT;
				case '[':
					m_nArrayCount++;
					return TOKEN_STARTARRAY;
				case ']':
					m_nArrayCount--;
					return TOKEN_ENDARRAY;
				case ':':
					return TOKEN_KEYVALUESEP;
				case ',':
					return TOKEN_SEPARATOR;
				case '"':
					return readString();
				default:
					if ( !Character.isWhitespace(m_sInput.charAt(m_nCurrent)) )
						return readThing();
					// Else do nothing
					break;
			}
		}
	}


	private boolean doExtract (String p_sResName)
	{
		if ( !m_Opt.m_LD.isLocalizable(true) ) return false;

		// Treat options for key+value pairs
		boolean bExtract = m_Opt.m_bExtractAllPairs;
		if (( p_sResName != null ) && ( p_sResName != "" )) {
			if ( bExtract ) bExtract = (m_Opt.m_sExceptions.indexOf("\t"+p_sResName+"\t") == -1);
			else bExtract = (m_Opt.m_sExceptions.indexOf("\t"+p_sResName+"\t") != -1);
			if ( !bExtract ) return false;
		}
		else if ( !bExtract ) return false;

		m_CurrentFI.setItemType(FilterItemType.TEXT);
		m_CurrentFI.setText(m_sbBuffer.toString());
		m_CurrentFI.setStart(m_nStartString+1);
		m_CurrentFI.setLength(m_nEndString-m_nStartString-1);

		if (( p_sResName != null ) && ( p_sResName != "" ))
			m_CurrentFI.setResName(p_sResName);

		if ( m_Opt.m_bUseCodeFinder )
			m_Opt.m_CodeFinder.processFilterItem(m_CurrentFI);

		// Check the DNL list here to have resname, etc.
		if ( m_Opt.m_LD.isInDNLList(m_CurrentFI) ) return false;
		
		m_CurrentFI.setItemID(++m_nID);
		m_nLastTrigger = m_nCurrent;
		return true;
	}
}
