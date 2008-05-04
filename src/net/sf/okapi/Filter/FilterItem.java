/*===========================================================================*/
/* Copyright (C) 2007 ENLASO Corporation, Okapi Development Team             */
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
/* Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA              */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.Filter;

import java.util.Hashtable;
import java.util.ArrayList;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import net.sf.okapi.Library.Base.*;

/**
 * Implements the IFilterItem interface.
 */
public class FilterItem implements IFilterItem {
	
	public static final String    STARTMARKER    = "(s`M)";
	public static final String    ENDMARKER      = "(e`M)";

	private static final int      INDEXBASE      = 0xE200;
	//Not used yet: private static final int      INDEXWARNING   = 0xEFFF;

	private int                        m_nItemType;
	private int                        m_nItemID;
	private int                        m_nGroupID;
	private int                        m_nLevel;
	private String                     m_sResType;
	private String                     m_sResName;
	private String                     m_sMimeType;
	private String                     m_sNote;
	private String                     m_sFrom;
	private float                      m_fX;
	private float                      m_fY;
	private float                      m_fCX;
	private float                      m_fCY;
	private boolean                    m_bPreFormatted;
	private boolean                    m_bTranslatable;
	private boolean                    m_bTranslated;
	private String                     m_sFontName;
	private String                     m_sFontSize;
	private String                     m_sFontWeight;
	private String                     m_sFontStyle;
	private String                     m_sFontCharset;
	private StringBuffer               m_sbText;
	private ArrayList<CodeInfo>        m_aCodes;
	private boolean                    m_bSubFlow;
	private long                       m_lItemStart;
	private int                        m_nItemLength;
	private Hashtable<String,String>   m_htProperties;

	// Not to be reset in Reset()
	private String                     m_sLineBreak;
	private Charset                    m_Encoding;
	private String                     m_sRTFStartInLine;
	private String                     m_sRTFStartProtected;
	private boolean                    m_bRTFEscapeEC;
	private boolean                    m_bXMLStyle;

	// Unexposed variables
	private boolean                    m_bNormalized;
	private int                        m_nLastCodeID;
	

	static public int SToI (char p_chIndex) {
		return ((int)p_chIndex)-FilterItem.INDEXBASE;
	}

	static public char IToS (int p_nIndex) {
		return (char)(FilterItem.INDEXBASE+p_nIndex);
	}

	public FilterItem ()
	{
		m_sbText = new StringBuffer();
		m_aCodes = new ArrayList<CodeInfo>(2);
		m_htProperties = new Hashtable<String,String>();
		reset();
		m_sLineBreak = LineBreakType.DOS;
		m_Encoding = Charset.defaultCharset();
		m_sRTFStartInLine = "{\\cs6\\f1\\cf6\\lang1024 ";
		m_sRTFStartProtected = "{\\cs8\\f1\\cf13\\lang1024 ";
		m_bRTFEscapeEC = true;
		m_bXMLStyle = false;
	}

	public void appendChar (char p_chValue) {
		m_sbText.append(p_chValue);
	}

	public void appendCode (int p_nType,
		String p_sLabel,
		String p_sData)
	{
		CodeInfo CI = new CodeInfo();
		switch ( p_nType ) {
			case InlineCode.OPENING:
			case InlineCode.CLOSING:
				m_bNormalized = false;
				CI.m_nID = -1;
				break;
			case InlineCode.ISOLATED:
				CI.m_nID = ++m_nLastCodeID;
				break;
		}

		// In all cases:
		CI.m_sData = p_sData;
		CI.m_sLabel = p_sLabel;
		CI.m_nType = p_nType;
		m_aCodes.add(CI);
		m_sbText.append((char)p_nType);
		m_sbText.append(IToS(m_aCodes.size()-1));
	}
	
	public void appendText (String p_sValue) {
		m_sbText.append(p_sValue);
	}
	
	public int changeToCode (int p_nStart,
		int p_nCodeIndex,
		int p_nCodeLength,
		int p_nTextIndex,
		int p_nTextLength)
	{
		// Sanity check
		if (( p_nStart == -1) || ( p_nCodeIndex == -1 )
				|| ( p_nCodeLength == 0 )) return 0;

		int nPrevCorrection = 0;
		int nCorrection = 0;
		CodeInfo CI = new CodeInfo();
		CI.m_nID = ++m_nLastCodeID;
		CI.m_nType = InlineCode.ISOLATED;

		// Check for text group
		if (( p_nTextIndex > -1 ) && ( p_nTextLength > 0 ))
		{
			// Split and set the code into two codes
			nPrevCorrection = (p_nStart-p_nCodeIndex);
			int nTextStart = p_nTextIndex+nPrevCorrection;
			CI.m_sData = m_sbText.toString().substring(p_nStart, nTextStart); //LEN=nTextStart-p_nStart);
			CI.m_sData = expandCodes(CI.m_sData);
			m_aCodes.add(CI);
			// Adjust the text
			m_sbText.delete(p_nStart, nTextStart); //LEN=nTextStart-p_nStart);
			m_sbText.insert(p_nStart, String.format("%c%c", CI.m_nType, IToS(m_aCodes.size()-1)));

			// Second part, after text
			CI = new CodeInfo();
			CI.m_nID = ++m_nLastCodeID;
			CI.m_nType = InlineCode.ISOLATED;
			nCorrection = 2-(nTextStart-p_nStart);
			int nC2Start = (nTextStart+nCorrection)+p_nTextLength;
			int nC2End = ((p_nCodeLength+nCorrection)+p_nStart);
			int nC2Len = nC2End-nC2Start;
			CI.m_sData = m_sbText.toString().substring(nC2Start, nC2End); //LEN=nC2Len
			CI.m_sData = expandCodes(CI.m_sData);
			m_aCodes.add(CI);
			// Adjust the text
			m_sbText.delete(nC2Start, nC2End); //LEN=nC2Len
			m_sbText.insert(nC2Start, String.format("%c%c", CI.m_nType, IToS(m_aCodes.size()-1)));
			nCorrection += (2-nC2Len);
		}
		else
		{
			// Set the code
			CI.m_sData = m_sbText.toString().substring(p_nStart, p_nStart+p_nCodeLength); //LEN=p_nCodeLength
			CI.m_sData = expandCodes(CI.m_sData);
			m_aCodes.add(CI);
			// Adjust the text
			m_sbText.delete(p_nStart, p_nStart+p_nCodeLength); //LEN=p_nCodeLength
			m_sbText.insert(p_nStart, String.format("%c%c", CI.m_nType, IToS(m_aCodes.size()-1)));
			nCorrection = 2-p_nCodeLength;
		}

		return nCorrection;
	}
	
	public void copyFrom (IFilterItem p_FilterItem)
	{
		setItemID(p_FilterItem.getItemID());
		setItemType(p_FilterItem.getItemType());
		setGroupID(p_FilterItem.getGroupID());
		setLevel(p_FilterItem.getLevel());
	
		setResType(p_FilterItem.getResType());
		setResName(p_FilterItem.getResName());
		setMimeType(p_FilterItem.getMimeType());
		
		setNote(p_FilterItem.getNote(), p_FilterItem.getNoteAttributeFrom());
		
		setCoord(p_FilterItem.getCoord());
		
		setTranslatable(p_FilterItem.isTranslatable());
		setTranslated(p_FilterItem.isTranslated());
		setPreFormatted(p_FilterItem.isPreFormatted());
		setSubFlow(p_FilterItem.isSubFlow());
		
		setStart(p_FilterItem.getStart());
		setLength(p_FilterItem.getLength());
		
		setFont(p_FilterItem.getFont());
		
		setCodeMapping(p_FilterItem.getCodeMapping());
		modifyText(p_FilterItem.getText(FilterItemText.CODED));

		setEncoding(p_FilterItem.getEncoding());
		setLineBreak(p_FilterItem.getLineBreak());
		setXMLStyle(p_FilterItem.getXMLStyle());

		m_htProperties.clear();
		String[] aKeys = p_FilterItem.listProperties().split(";", -2);
		if ( aKeys[0].length() > 0 ) {
			for ( String sKey : aKeys ) {
				m_htProperties.put(sKey, p_FilterItem.getProperty(sKey));
			}
		}
	}
	
	public IFilterItem extract (int p_nStart,
		int p_nLength,
		boolean p_bAddMissingCodes)
	{
		// TODO: implement extract()
		return null;
	}
	
	public String getCode (int p_nIndex,
		boolean p_bStandardLineBreaks)
	{
		if ( p_bStandardLineBreaks )
			return m_aCodes.get(p_nIndex).m_sData;
		else // Else replace the line breaks
			return m_aCodes.get(p_nIndex).m_sData.replaceAll("\n", m_sLineBreak);
	}
	
	public int getCodeCount () {
		return m_aCodes.size();
	}
	
	public int getCodeID (int p_nIndex) {
		// ID may be changed at normalization:
		// First, make sure the IDs are normalized
		if ( !m_bNormalized ) normalizeCodes();
		return m_aCodes.get(p_nIndex).m_nID;
	}
	
	public int getCodeIndex (int p_nID,
		int p_nType) {
		for ( int i=0; i<m_aCodes.size(); i++ ) {
			if (( m_aCodes.get(i).m_nID == p_nID )
				&& ( m_aCodes.get(i).m_nType == p_nType )) { 
				return i;
			}
		}
		return -1;
	}
	
	public String getCodeLabel (int p_nIndex) {
		return m_aCodes.get(p_nIndex).m_sLabel;
	}
	
	public String getCodeMapping () {
		if ( !hasCode() ) return "";
		if ( !m_bNormalized ) normalizeCodes();
		StringBuffer sbTmp = new StringBuffer(100);
		for ( CodeInfo CI : m_aCodes )
		{
			sbTmp.append(String.format("%1$d\u0086%2$d\u0086%3$s\u0086%4$s\u0087",
				CI.m_nID, CI.m_nType, CI.m_sData, CI.m_sLabel));
		}
		return sbTmp.toString();
	}
	
	public String getCoord () {
		return String.format("%1$.0f;%2$.0f;%3$.0f;%4$.0f", m_fX, m_fY, m_fCX, m_fCY);
	}
	
	public float getCX () {
		return m_fCX;
	}
	
	public float getCY () {
		return m_fCY;
	}
	
	public String getEncoding () {
		return m_Encoding.name();
	}
	
	public String getFont () {
		return m_sFontName + ";"
			+ m_sFontSize + ";"
			+ m_sFontWeight + ";"
			+ m_sFontStyle + ";"
			+ m_sFontCharset;
	}
	
	public int getGroupID () {
		return m_nGroupID;
	}
	
	public int getItemID () {
		return m_nItemID;
	}
	
	public int getItemType () {
		return m_nItemType;
	}
	
	public int getLength () {
		return m_nItemLength;
	}
	
	public int getLevel () {
		return m_nLevel;
	}
	
	public String getLineBreak () {
		return m_sLineBreak;
	}
	
	public String getMimeType () {
		return m_sMimeType;
	}
	
	public String getNote () {
		return m_sNote;
	}
	
	public String getNoteAttributeFrom () {
		return m_sFrom;
	}
	
	public String getProperty (String p_sName) {
		if ( m_htProperties.containsKey(p_sName) )
			return m_htProperties.get(p_sName);
		else
			return null;
	}
	
	public String getResName () {
		return m_sResName;
	}
	
	public String getResType () {
		return m_sResType;
	}

	public long getStart () {
		return m_lItemStart;
	}
	
	public String getText (int p_nFormat) {
		if ( m_sbText.length() == 0 ) return "";
		switch ( p_nFormat )
		{
			case FilterItemText.GENERIC:
				return GetGenericText();
			case FilterItemText.PLAIN:
				return GetPlainText();
			case FilterItemText.ORIGINAL:
				return GetOriginalText(false);
			case FilterItemText.CODESONLY:
				return GetCodesOnlyText();
			case FilterItemText.XLIFF:
				return GetXLIFFText(true);
			case FilterItemText.XLIFFGX:
				return GetXLIFFText(false);
			case FilterItemText.TMX:
				return GetTMXText();
			case FilterItemText.RTF:
				return GetRTFText();
			case FilterItemText.XLIFFRTF:
				return getXLIFFRTFText(true);
			case FilterItemText.XLIFFGXRTF:
				return getXLIFFRTFText(false);
			case FilterItemText.CODED:
			default:
				return m_sbText.toString();
		}
	}
	
	public int getTextLength (int p_nFormat) {
		if ( m_sbText.length() == 0 ) return 0;
		switch ( p_nFormat ) {
			case FilterItemText.GENERIC:
				return GetGenericText().length();
			case FilterItemText.PLAIN:
				return GetPlainText().length();
			case FilterItemText.ORIGINAL:
				return GetOriginalText(false).length();
			case FilterItemText.CODESONLY:
				return GetCodesOnlyText().length();
			case FilterItemText.XLIFF:
				return GetXLIFFText(true).length();
			case FilterItemText.XLIFFGX:
				return GetXLIFFText(false).length();
			case FilterItemText.TMX:
				return GetTMXText().length();
			case FilterItemText.RTF:
				return GetRTFText().length();
			case FilterItemText.XLIFFRTF:
				return getXLIFFRTFText(true).length();
			case FilterItemText.XLIFFGXRTF:
				return getXLIFFRTFText(false).length();
			case FilterItemText.CODED:
			default:
				return m_sbText.length();
		}
	}
	
	public float getX () {
		return m_fX;
	}

	public boolean getXMLStyle () {
		return m_bXMLStyle;
	}
	
	public float getY () {
		return m_fY;
	}
	
	public boolean hasCode () {
		return (m_aCodes.size() > 0);
	}
	
	public boolean hasCoord () {
		return ((m_fX!=0) || (m_fY!=0) || (m_fCX!=0) || (m_fCY!=0));
	}
	
	public boolean hasFont () {
		return (( m_sFontName != null ) && ( m_sFontName.length() > 0 ));
	}
	
	public boolean hasNote () {
		return (( m_sNote != null ) && ( m_sNote.length() > 0 ));
	}
	
	public boolean hasText(boolean p_bWhiteSpaceIsText)
	{
		for ( int i=0; i<m_sbText.length(); i++ ) {
			switch ( m_sbText.codePointAt(i) ) {
				case InlineCode.OPENING:
				case InlineCode.CLOSING:
				case InlineCode.ISOLATED:
					if ( i+1 < m_sbText.length() ) i++;
					continue;
				default:
					if ( p_bWhiteSpaceIsText )
						return true; // At least one character is in the text
					// Else: Text only if not whitepsace
					if ( !Character.isWhitespace(m_sbText.charAt(i)) ) return true;
					break;
			}
		}
		return false;
	}
	
	public boolean isEmpty() {
		return (m_sbText.length() == 0);
	}
	
	public boolean isPreFormatted() {
		return m_bPreFormatted;
	}
	
	public boolean isSubFlow() {
		return m_bSubFlow;
	}
	
	public boolean isTranslatable() {
		return m_bTranslatable;
	}
	
	public boolean isTranslated() {
		return m_bTranslated;
	}

	public String listProperties()
	{
		StringBuffer  sbList = new StringBuffer(50);
		for ( String sKey : m_htProperties.keySet() )
		{
			if ( sbList.length() > 0 ) sbList.append(';');
			sbList.append(sKey);
		}
		return sbList.toString();
	}
	
	public void modifyText(String p_sValue)
	{
		// The argument must have the correct inline codes
		m_sbText.setLength(0);
		m_sbText.append(p_sValue);
	}

	public void normalizeLineBreaks()
	{
		for ( int i=0; i<m_sbText.length(); i++ )
		{
			switch ( m_sbText.codePointAt(i) )
			{
				case InlineCode.OPENING:
				case InlineCode.CLOSING:
				case InlineCode.ISOLATED:
					i++; // Skip over the code ID
					continue;

				case '\r':
					if ( i+1 < m_sbText.length() ) // Check next char
					{
						if ( m_sbText.charAt(i+1) == '\n' )
						{
							// It's \r\n, we change to \n only
							m_sbText.delete(i, i+1); //LEN=1
							// No offeset change since \n is now treated
							continue;
						}
					}
					// Else it's \r only, we change it to \n only
					m_sbText.setCharAt(i, '\n');
					break;
			}
		}
	}

	public void normalizeWhiteSpaces()
	{
		boolean         bWasWS = false;

		for ( int i=0; i<m_sbText.length(); i++ )
		{
			switch ( m_sbText.codePointAt(i) )
			{
				case InlineCode.OPENING:
				case InlineCode.CLOSING:
				case InlineCode.ISOLATED:
					i++; // Skip over the code ID
					continue;

				case ' ':
				case '\r':
				case '\n':
				case '\t':
					if ( bWasWS )
					{
						m_sbText.delete(i, i+1); //LEN=1
						i--; // Offset the deletion
					}
					else 
					{
						m_sbText.setCharAt(i, ' ');
						bWasWS = true;
					}
					break;

				default:
					bWasWS = false;
					break;
			}
		}
	}
	
	public void removeEnd(int p_nCount)
	{
		int            nPos = m_sbText.length()-1;
		int            nCodePos = -1;

		if ( hasCode() ) {
			for ( int i=0; i<m_sbText.length(); i++ ) {
				switch ( m_sbText.codePointAt(i) ) {
					case InlineCode.OPENING:
					case InlineCode.CLOSING:
					case InlineCode.ISOLATED:
						if ( i+1 > m_sbText.length() ) continue;
						i++; // Skip over the code ID
						nCodePos = i;
						continue;
				}	
			}
		}

		while ( p_nCount > 0 ) {
			if ( nPos < 0 ) return; // End of string
			if ( nPos <= nCodePos ) { // Check for inline code
				return; // Stop at first inline code
			}
			m_sbText.delete(nPos, nPos+1); //LEN=1
			nPos--;
			p_nCount--;
		}
	}
	
	public void reset ()
	{
		m_nItemType = FilterItemType.ERROR;
		m_nItemID = 0;
		m_nGroupID = 0;
		m_nLevel = 0;
		
		m_sResType = "";
		m_sResName = "";
		m_sMimeType = "";
		
		m_sNote = "";
		m_sFrom = "";

		m_fX = m_fY = m_fCX = m_fCY = 0;

		m_bTranslatable = true;
		m_bTranslated = false;
		m_bPreFormatted = false;
		m_bSubFlow = false;

		m_lItemStart = -1;
		m_nItemLength = -1;
		
		m_sFontName = "";
		m_sFontSize = "";
		m_sFontWeight = "";
		m_sFontStyle = "";
		m_sFontCharset = "";

		m_sbText.setLength(0);
		m_aCodes.clear();
		m_bNormalized = true;
		m_nLastCodeID = 0;
		m_htProperties.clear();

		// m_sEncoding is not reset here.
		// m_sLineBreak is not reset here.
		// m_sRTFStartInLine is not reset here.
		// m_sRTFEndInLine is not reset here.
		// m_bRTFEscapeEC is not reset here.
		// m_bXMLStyle is not reset here
	}
	
	public void setCodeMapping (String p_sValue)
	{
		m_aCodes.clear();
		String[] asTmp1 = p_sValue.split("\u0087");
		for ( String sTmp1 : asTmp1 )
		{
			String[] asTmp2 = sTmp1.split("\u0086");
			if ( asTmp2.length < 4 ) break; // End
			CodeInfo CI = new CodeInfo();
			CI.m_nID = Integer.parseInt(asTmp2[0]);
			CI.m_nType = Integer.parseInt(asTmp2[1]);
			CI.m_sData = asTmp2[2];
			CI.m_sLabel = asTmp2[3];
			m_aCodes.add(CI);
		}
	}
	
	public void setCoord (String p_sValue) {
		m_fX  = m_fY = m_fCX = m_fCY = 0;
		if (( p_sValue == null ) || ( p_sValue.length() < 1 )) return;
		
		String[] aFields = p_sValue.split(";");
		for ( int i=0; i<aFields.length; i++ )
		{
			if (( aFields[i].length() == 0 ) || ( aFields[i].charAt(0) == '#' ))
				continue;
			switch ( i )
			{
				case 0: // X
					m_fX = Integer.parseInt(aFields[i]);
					break;
				case 1: // Y
					m_fY = Integer.parseInt(aFields[i]);
					break;
				case 2: // CX
					m_fCX = Integer.parseInt(aFields[i]);
					break;
				case 3: // CY
					m_fCY = Integer.parseInt(aFields[i]);
					break;
			}
		}
	}
	
	public void setCX (float p_fValue) {
		m_fCX = p_fValue;
	}

	public void setCY (float p_fValue) {
		m_fCY = p_fValue;
	}
	
	public void setEncoding (String p_sValue) {
		m_Encoding = Charset.forName(p_sValue);
		// For now we don't escape normal extended chars if the output
		// sourceEncoding is UTF-8. This is for TWB v7 output in Text TM
		m_bRTFEscapeEC = !m_Encoding.name().equalsIgnoreCase("utf-8");
	}
	
	public void setFont(String p_sValue) {
		m_sFontName = m_sFontSize = m_sFontWeight = m_sFontStyle
			= m_sFontCharset = "";
		if (( p_sValue == null ) || ( p_sValue.length() < 1 )) return;
		
		String[] aFields = p_sValue.split(";");
		for ( int i=0; i<aFields.length; i++ ) {
			if ( aFields[i].length() == 0 ) continue;
			switch ( i )
			{
				case 0: // FontName
					m_sFontName = aFields[i];
					break;
				case 1: // FontSize
					m_sFontSize = aFields[i];
					break;
				case 2: // FontWeight
					m_sFontWeight = aFields[i];
					break;
				case 3: // FontStyle
					m_sFontStyle = aFields[i];
					break;
				case 4: // FontCharset
					m_sFontCharset = aFields[i];
					break;
			}
		}
	}
	
	public void setGroupID(int p_nValue) {
		m_nGroupID = p_nValue;
	}
	
	public void setItemID(int p_nValue) {
		m_nItemID = p_nValue;
	}
	
	public void setItemType(int p_nValue) {
		m_nItemType = p_nValue;
	}

	public void setLength(int p_nValue) {
		m_nItemLength = p_nValue;
	}
	
	public void setLevel(int p_nValue) {
		m_nLevel = p_nValue;
	}
	
	public void setLineBreak(String p_sValue) {
		m_sLineBreak = p_sValue;
	}
	
	public void setMimeType(String p_sValue) {
		m_sMimeType = p_sValue;
	}
	
	public void setNote(String p_sNote,
		String p_sFrom)
	{
		m_sNote = p_sNote;
		m_sFrom = p_sFrom;
	}
	
	public void setPreFormatted(boolean p_bValue) {
		m_bPreFormatted = p_bValue;
	}
	
	public void setProperty(String p_sName,
		String p_sValue)
	{
		if ( p_sValue == null ) {
			if ( m_htProperties.containsKey(p_sName) )
				m_htProperties.remove(p_sName);
		}
		else m_htProperties.put(p_sName, p_sValue); // Add or change
	}
	
	public void setResName(String p_sValue) {
		m_sResName = p_sValue;
	}
	
	public void setResType(String p_sValue) {
		m_sResType = p_sValue;
	}
	
	public void setRTFOptions(String p_sRTFStartInLine,
		String p_sRTFStartProtected)
	{
		m_sRTFStartInLine = p_sRTFStartInLine;
		m_sRTFStartProtected = p_sRTFStartProtected;
		// m_bRTFEscapeEC may be changed here in the future.
		// For now it depends on the sourceEncoding.
	}
	
	public void setStart(long p_lValue) {
		m_lItemStart = p_lValue;
	}
	
	public void setSubFlow(boolean p_bValue) {
		m_bSubFlow = p_bValue; 
	}

	public void setText(String p_sValue) {
		m_sbText.setLength(0);
		m_sbText.append(p_sValue);
		m_aCodes.clear();
		m_bNormalized = true;
		m_nLastCodeID = 0;
	}

	public void setTranslatable(boolean p_bValue) {
		m_bTranslatable = p_bValue;
	}
	
	public void setTranslated(boolean p_bValue) {
		m_bTranslated = p_bValue;
	}

	public void setX(float p_fValue) {
		m_fX = p_fValue;
	}

	public void setXMLStyle(boolean p_bValue) {
		m_bXMLStyle = p_bValue;		
	}
	
	public void setY(float p_fValue) {
		m_fY = p_fValue;
	}

	private void normalizeCodes ()
	{
		if ( m_bNormalized ) return;

		int i = 0;
		int j;
		boolean bFound;
		CodeInfo CI;
		int nStack;

		while ( i < m_aCodes.size() )
		{
			CI = m_aCodes.get(i);
			if ( CI.m_nID == -1 ) {
				// If it's a closing code: it's isolated
				if ( CI.m_nType == InlineCode.CLOSING ) {
					m_aCodes.get(i).m_nID = ++m_nLastCodeID;
					changeCodeType(i, InlineCode.ISOLATED);
					continue;
				}

				// Else, it's a BPT: search corresponding closing code
				j = i+1; bFound = false;
				nStack = 1;
				while ( j < m_aCodes.size() ) {
					if ( m_aCodes.get(j).m_nType == InlineCode.CLOSING ) {
						if ( m_aCodes.get(j).m_sLabel == CI.m_sLabel ) {
							if ( (--nStack) == 0 ) {
								bFound = true;
								break;
							}
						}
					}
					else if ( m_aCodes.get(j).m_nType == InlineCode.OPENING ) {
						if ( m_aCodes.get(j).m_sLabel == CI.m_sLabel ) {
							nStack++;
						}
					}
					j++;
				}

				if ( bFound ) {
					m_aCodes.get(i).m_nID = ++m_nLastCodeID;
					m_aCodes.get(j).m_nID = m_nLastCodeID; // Same ID
				}
				else {
					// Change to Isolated tag (update the type)
					m_aCodes.get(i).m_nID = ++m_nLastCodeID;
					changeCodeType(i, InlineCode.ISOLATED);
				}
			}
			i++;
		}
		m_bNormalized = true;
	}
	
	private void changeCodeType (int p_nIndexZeroBased,
		int p_nNewType)
	{
		// Search for the index in the string (i+1) after a code
		for ( int i=0; i<m_sbText.length(); i++ )
		{
			switch ( m_sbText.codePointAt(i) )
			{
				case InlineCode.OPENING:
				case InlineCode.CLOSING:
					if ( i+1 > m_sbText.length() ) continue;
					if ( p_nIndexZeroBased == SToI(m_sbText.charAt(i+1)) )
					{
						m_aCodes.get(p_nIndexZeroBased).m_nType = InlineCode.ISOLATED; 
						m_sbText.setCharAt(i, (char)InlineCode.ISOLATED);
						return;
					}
					i++; // Skip over the code ID
					continue;

				case InlineCode.ISOLATED: // Treat those too to avoid any mistake
					if ( i+1 > m_sbText.length() ) continue;
					i++; // Skip over the code-type
					continue;
			}	
		}
	}

	private String expandCodes (String p_sText) {
		StringBuffer sbTmp = new StringBuffer(p_sText.length());
		
		for ( int i=0; i<p_sText.length(); i++ ) {
			switch ( p_sText.codePointAt(i) ) {
				case InlineCode.OPENING:
				case InlineCode.CLOSING:
				case InlineCode.ISOLATED:
					if ( i+1 >= p_sText.length() )	continue;
					sbTmp.append(getCode(p_sText.codePointAt(++i), false));
					continue;
				default:
					sbTmp.append(p_sText.charAt(i));
					continue;
			}
		}

		//TODO: check if XML conversion is needed
		if ( m_bXMLStyle )
			return Utils.escapeToXML(sbTmp.toString(), 2, false);
		else
			return sbTmp.toString();
	}

	private String escapeToRTF (String p_sText) {
		StringBuilder sbTmp = new StringBuilder(p_sText.length());

		for ( int i=0; i<p_sText.length(); i++ ) {
			switch ( p_sText.charAt(i) ) {
				case '{':
				case '}':
				case '\\':
					sbTmp.append('\\');
					sbTmp.append(p_sText.charAt(i));
					continue;

				case '\n':
					sbTmp.append("\r\n\\par ");
					continue;

				case '\u0009':
					sbTmp.append("\\tab ");
					continue;
				case '\u2022':
					sbTmp.append("\\bullet ");
					continue;
				case '\u2018':
					sbTmp.append("\\lquote ");
					continue;
				case '\u2019':
					sbTmp.append("\\rquote ");
					continue;
				case '\u201c':
					sbTmp.append("\\ldblquote ");
					continue;
				case '\u201d':
					sbTmp.append("\\rdblquote ");
					continue;
				case '\u2013':
					sbTmp.append("\\endash ");
					continue;
				case '\u2014':
					sbTmp.append("\\emdash ");
					continue;
				case '\u200d':
					sbTmp.append("\\zwj ");
					continue;
				case '\u200c':
					sbTmp.append("\\zwnj ");
					continue;
				case '\u200e':
					sbTmp.append("\\ltrmark ");
					continue;
				case '\u00a0':
					sbTmp.append("\\~"); // No space after control symbols
					continue;

				default:
					if (( p_sText.codePointAt(i) > 127 ) && m_bRTFEscapeEC )
					{
						ByteBuffer bBuf = m_Encoding.encode(Integer.toString(p_sText.codePointAt(i)));
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

	private String GetGenericText () {
		StringBuffer sbTmp = new StringBuffer(m_sbText.length());

		// Return the plain string if no codes
		if ( !hasCode() ) {
			sbTmp.append(m_sbText);
			return sbTmp.toString().replaceAll("\n", m_sLineBreak);
		}

		// If codes: first, make sure they are normalized
		if ( !m_bNormalized ) normalizeCodes();
		// Then convert them to generic tags
		for ( int i=0; i<m_sbText.length(); i++ ) {
			switch ( m_sbText.codePointAt(i) ) {
				case InlineCode.OPENING:
					if ( i+1 >= m_sbText.length() )	continue;
					sbTmp.append("<"+getCodeID(SToI(m_sbText.charAt(++i)))+">");
					continue;
				case InlineCode.CLOSING:
					if ( i+1 >= m_sbText.length() )	continue;
					sbTmp.append("</"+getCodeID(SToI(m_sbText.charAt(++i)))+">");
					continue;
				case InlineCode.ISOLATED:
					if ( i+1 >= m_sbText.length() )	continue;
					sbTmp.append("<"+getCodeID(SToI(m_sbText.charAt(++i)))+"/>");
					continue;
				default:
					sbTmp.append(m_sbText.charAt(i));
					continue;
			}
		}
		return sbTmp.toString().replaceAll("\n", m_sLineBreak);
	}

	private String GetPlainText ()
	{
		StringBuffer sbTmp = new StringBuffer(m_sbText.length());

		// Return the plain string if no codes
		if ( !hasCode() )
		{
			sbTmp.append(m_sbText);
			return sbTmp.toString().replaceAll("\n", m_sLineBreak);
		}

		// Not need to Normalize for this output
		// If codes: remove them
		for ( int i=0; i<m_sbText.length(); i++ )
		{
			switch ( m_sbText.codePointAt(i) )
			{
				case InlineCode.OPENING:
				case InlineCode.CLOSING:
				case InlineCode.ISOLATED:
					if ( i+1 < m_sbText.length() ) i++;
					continue;
				default:
					sbTmp.append(m_sbText.charAt(i));
					continue;
			}
		}
		return sbTmp.toString().replaceAll("\n", m_sLineBreak);
	}

	// Public but not part of the IFilterItem interface
	public String GetTextWithMarkers ()
	{
		return GetOriginalText(true);
	}


	private String GetOriginalText (boolean p_bWithInlineMarkers)
	{
		StringBuilder sbTmp = new StringBuilder(m_sbText.length());

		// Return the plain string if no codes
		if ( !hasCode() && !m_bXMLStyle )
		{
			sbTmp.append(m_sbText);
			return sbTmp.toString().replaceAll("\n", m_sLineBreak);
		}

		// No need to normalize the inline codes for this output
		// Convert to original
		for ( int i=0; i<m_sbText.length(); i++ )
		{
			switch ( m_sbText.codePointAt(i) )
			{
				case InlineCode.OPENING:
				case InlineCode.CLOSING:
				case InlineCode.ISOLATED:
					if ( i+1 >= m_sbText.length() )	continue;
//TODO: do we need it here???						if ( m_bXMLStyle )
//               sbTmp.Append(Routines.EscapeToXML(GetCode(SToI(m_sbText[++i]), true), 0));
//					else
					if ( p_bWithInlineMarkers ) sbTmp.append(STARTMARKER);
					sbTmp.append(getCode(SToI(m_sbText.charAt(++i)), true));
					if ( p_bWithInlineMarkers ) sbTmp.append(ENDMARKER);
					continue;

				case '\n':
					sbTmp.append(m_sLineBreak);
					break;

				case '<':
					if ( m_bXMLStyle ) sbTmp.append("&lt;");
					else sbTmp.append('<');
					break;

				case '>':
					if ( m_bXMLStyle ) sbTmp.append("&gt;");
					else sbTmp.append('>');
					break;

				case '&':
					if ( m_bXMLStyle ) sbTmp.append("&amp;");
					else sbTmp.append('&');
					break;

				default:
					sbTmp.append(m_sbText.charAt(i));
					continue;
			}
		}
		return sbTmp.toString().replaceAll("\n", m_sLineBreak);
	}

	private String GetCodesOnlyText ()
	{
		StringBuilder sbTmp = new StringBuilder(m_sbText.length());

		// Return the plain string if no codes
		if ( !hasCode() )
		{
			if ( !m_bPreFormatted ) return "";
		}

		// If codes: first, make sure they are normalized
		if ( !m_bNormalized ) normalizeCodes();
		// Then emove text not codes
		for ( int i=0; i<m_sbText.length(); i++ )
		{
			switch ( m_sbText.codePointAt(i) )
			{
				case InlineCode.OPENING:
				case InlineCode.CLOSING:
				case InlineCode.ISOLATED:
					if ( i+1 >= m_sbText.length() ) continue;
					sbTmp.append(m_sbText.charAt(i++)); // Code
					sbTmp.append(m_sbText.charAt(i)); // Index
					break;
				case '\n':
					if ( m_bPreFormatted ) sbTmp.append('\n');
					break;
				default:
					// Nothing for text
					continue;
			}
		}
		return sbTmp.toString().replaceAll("\n", m_sLineBreak);
	}

	private String GetXLIFFText (boolean p_bUseBPT)
	{
		StringBuilder sbTmp = new StringBuilder(m_sbText.length());

		// Return the plain string if no codes
		if ( !hasCode() )
		{
			String sTmp = m_sbText.toString();
			sTmp = sTmp.replaceAll("\n", m_sLineBreak);
			// Order is important: do '&' first
			sTmp = sTmp.replaceAll("&", "&amp;");
			sTmp = sTmp.replaceAll("\\<", "&lt;");
			return sTmp.replace("\\]\\>", "]&gt;");
		}

		// If codes: first, make sure they are normalized
		if ( !m_bNormalized ) normalizeCodes();
		// Then convert to XLIFF
		for ( int i=0; i<m_sbText.length(); i++ )
		{
			switch ( m_sbText.codePointAt(i) )
			{
				case InlineCode.OPENING:
					if ( i+1 >= m_sbText.length() )	continue;
					if ( p_bUseBPT )
					{
						sbTmp.append("<bpt id=\"" + getCodeID(SToI(m_sbText.charAt(++i))) + "\">");
						sbTmp.append(Utils.escapeToXML(getCode(SToI(m_sbText.charAt(i)), false), 0, false));
						sbTmp.append("</bpt>");
					}
					else
					{
						sbTmp.append("<g id=\"" + getCodeID(SToI(m_sbText.charAt(++i))) + "\">");
					}
					continue;
				case InlineCode.CLOSING:
					if ( i+1 >= m_sbText.length() )	continue;
					if ( p_bUseBPT )
					{
						sbTmp.append("<ept id=\"" + getCodeID(SToI(m_sbText.charAt(++i))) + "\">");
						sbTmp.append(Utils.escapeToXML(getCode(SToI(m_sbText.charAt(i)), false), 0, false));
						sbTmp.append("</ept>");
					}
					else
					{
						sbTmp.append("</g>");
						++i; // Make sure we skip the code even if it's not used
					}
					continue;
				case InlineCode.ISOLATED:
					if ( i+1 >= m_sbText.length() )	continue;
					if ( p_bUseBPT )
					{
						if ( getCode(SToI(m_sbText.charAt(i+1)), false).length() > 0 )
						{
							sbTmp.append("<ph id=\"" + getCodeID(SToI(m_sbText.charAt(++i))) + "\">");
							sbTmp.append(Utils.escapeToXML(getCode(SToI(m_sbText.charAt(i)), false), 0, false));
							sbTmp.append("</ph>");
						}
						else sbTmp.append("<ph id=\"" + getCodeID(SToI(m_sbText.charAt(++i))) + "\"/>");
					}
					else
					{
						sbTmp.append("<x id=\"" + getCodeID(SToI(m_sbText.charAt(++i))) + "\"/>");
					}
					continue;
				case '&':
					sbTmp.append("&amp;");
					continue;
				case '<':
					sbTmp.append("&lt;");
					continue;
				case '>':
					if (( i > 0 ) && ( m_sbText.charAt(i-1) == ']' )) sbTmp.append("&gt;");
					else sbTmp.append(m_sbText.charAt(i));
					continue;
				default:
					sbTmp.append(m_sbText.charAt(i));
					continue;
			}
		}

		return sbTmp.toString().replaceAll("\n", m_sLineBreak);
	}

	private String GetTMXText ()
	{
		StringBuffer sbTmp = new StringBuffer(m_sbText.length());

		// Return the plain string if no codes
		if ( !hasCode() )
		{
			String sTmp = m_sbText.toString();
			sTmp = sTmp.replaceAll("\n", m_sLineBreak);
			// Order is important: do '&' first
			sTmp = sTmp.replaceAll("&", "&amp;");
			sTmp = sTmp.replaceAll("\\<", "&lt;");
			return sTmp.replace("\\]\\>", "]&gt;");
		}

		// If codes: first, make sure they are normalized
		if ( !m_bNormalized ) normalizeCodes();

		// Then convert to TMX
		for ( int i=0; i<m_sbText.length(); i++ )
		{
			switch ( m_sbText.codePointAt(i) )
			{
				case InlineCode.OPENING:
					if ( i+1 >= m_sbText.length() )	continue;
					sbTmp.append(String.format("<bpt i=\"%1$s\" x=\"%2$d\">",
						getCodeID(SToI(m_sbText.charAt(++i))), SToI(m_sbText.charAt(i))+1));
					sbTmp.append(Utils.escapeToXML(getCode(SToI(m_sbText.charAt(i)), true), 0, false));
					sbTmp.append("</bpt>");
					continue;
				case InlineCode.CLOSING:
					if ( i+1 >= m_sbText.length() )	continue;
					sbTmp.append(String.format("<ept i=\"%1$d\">", getCodeID(SToI(m_sbText.charAt(++i)))));
					sbTmp.append(Utils.escapeToXML(getCode(SToI(m_sbText.charAt(i)), true), 0, false));
					sbTmp.append("</ept>");
					continue;
				case InlineCode.ISOLATED:
					if ( i+1 >= m_sbText.length() )	continue;
					sbTmp.append(String.format("<ph x=\"%1$d\">", SToI(m_sbText.charAt(++i))+1));
					sbTmp.append(Utils.escapeToXML(getCode(SToI(m_sbText.charAt(i)), true), 0, false));
					sbTmp.append("</ph>");
					continue;
				case '&':
					sbTmp.append("&amp;");
					continue;
				case '<':
					sbTmp.append("&lt;");
					continue;
				case '>':
					if (( i > 0 ) && ( m_sbText.charAt(i-1) == ']' )) sbTmp.append("&gt;");
					else sbTmp.append(m_sbText.charAt(i));
					continue;
				default:
					sbTmp.append(m_sbText.charAt(i));
					continue;
			}
		}

		return sbTmp.toString().replaceAll("\n", m_sLineBreak);
	}

	private String GetRTFText ()
	{
		StringBuffer sbTmp = new StringBuffer(m_sbText.length());

		// Return the plain string if no codes
		if ( !hasCode() && !m_bXMLStyle )
		{
			sbTmp.append(m_sbText);
			return escapeToRTF(sbTmp.toString());
		}

		for ( int i=0; i<m_sbText.length(); i++ )
		{
			switch ( m_sbText.codePointAt(i) )
			{
				case InlineCode.OPENING:
				case InlineCode.CLOSING:
					if ( i+1 >= m_sbText.length() )	continue;
					if ( isTranslatable() )
					{
						sbTmp.append(m_sRTFStartInLine);
						sbTmp.append(escapeToRTF(
								getCode(SToI(m_sbText.charAt(++i)), true)));
						sbTmp.append('}');
					}
					else
					{
						sbTmp.append(escapeToRTF(
								getCode(SToI(m_sbText.charAt(++i)), true)));
					}
					continue;

				case InlineCode.ISOLATED:
					if ( i+1 >= m_sbText.length() )	continue;
					if ( isTranslatable() )
					{
						if ( getCodeLabel(SToI(m_sbText.charAt(++i))).equals("protected") )
							sbTmp.append(m_sRTFStartProtected);
						else
							sbTmp.append(m_sRTFStartInLine);
						sbTmp.append(escapeToRTF(getCode(SToI(m_sbText.charAt(i)), true)));
						sbTmp.append('}');
					}
					else
					{
						sbTmp.append(escapeToRTF(getCode(SToI(m_sbText.charAt(++i)), true)));
					}
					continue;

				case '{':
				case '}':
				case '\\':
					sbTmp.append("\\");
					sbTmp.append(m_sbText.charAt(i));
					continue;

				case '\n':
					sbTmp.append("\r\n\\par ");
					continue;

				case '\u0009':
					sbTmp.append("\\tab ");
					continue;
				case '\u2022':
					sbTmp.append("\\bullet ");
					continue;
				case '\u2018':
					sbTmp.append("\\lquote ");
					continue;
				case '\u2019':
					sbTmp.append("\\rquote ");
					continue;
				case '\u201c':
					sbTmp.append("\\ldblquote ");
					continue;
				case '\u201d':
					sbTmp.append("\\rdblquote ");
					continue;
				case '\u2013':
					sbTmp.append("\\endash ");
					continue;
				case '\u2014':
					sbTmp.append("\\emdash ");
					continue;
				case '\u200d':
					sbTmp.append("\\zwj ");
					continue;
				case '\u200c':
					sbTmp.append("\\zwnj ");
					continue;
				case '\u200e':
					sbTmp.append("\\ltrmark ");
					continue;
				case '\u00a0':
					sbTmp.append("\\~"); // No space after control symbols
					continue;

				case '<':
					if ( m_bXMLStyle ) sbTmp.append("&lt;");
					else sbTmp.append('<');
					break;
				case '>':
					if ( m_bXMLStyle ) sbTmp.append("&gt;");
					else sbTmp.append('>');
					break;
				case '&':
					if ( m_bXMLStyle ) sbTmp.append("&amp;");
					else sbTmp.append('&');
					break;

				default:
					if (( m_sbText.codePointAt(i) > 127 ) && m_bRTFEscapeEC )
					{
						ByteBuffer bBuf = m_Encoding.encode(Integer.toString(m_sbText.codePointAt(i)));
						if ( bBuf.limit() > 1 )
						{
							sbTmp.append(String.format("{{\\uc%1$d", bBuf.limit()));
							sbTmp.append(String.format("\\u%1$d", m_sbText.codePointAt(i))); 
							for ( int b=0; b<bBuf.limit(); b++ )
								sbTmp.append(String.format("\\'%1$x", bBuf.getChar(b)));
							sbTmp.append("}");
						}
						else
						{
							sbTmp.append(String.format("\\u%1$d", m_sbText.codePointAt(i)));
							sbTmp.append(String.format("\\'%1$x", bBuf.getChar(0)));
						}
					}
					else sbTmp.append(m_sbText.charAt(i));
					continue;
			}
		}

		return sbTmp.toString();
	}

	private String getXLIFFRTFText (boolean p_bUseBPT)
	{
		StringBuilder sbTmp = new StringBuilder(m_sbText.length());

		// Return the plain string if no codes
		if ( !hasCode() )
		{
			String sTmp = m_sbText.toString();
			// Order is important: do '&' first
			sTmp = sTmp.replaceAll("&", "&amp;");
			sTmp = sTmp.replaceAll("\\<", "&lt;");
			sTmp = sTmp.replace("\\]\\>", "]&gt;");
			return escapeToRTF(sTmp);
		}

		// If codes: first, make sure they are normalized
		if ( !m_bNormalized ) normalizeCodes();
		// Then encapsulate them
		for ( int i=0; i<m_sbText.length(); i++ )
		{
			switch ( m_sbText.codePointAt(i) )
			{
				case InlineCode.OPENING:
					if ( i+1 >= m_sbText.length() )	continue;
					sbTmp.append(m_sRTFStartInLine);
					if ( p_bUseBPT )
					{
						sbTmp.append(String.format("<bpt id=\"%1$d\">", getCodeID(SToI(m_sbText.charAt(++i)))));
						sbTmp.append(escapeToRTF(
							Utils.escapeToXML(getCode(SToI(m_sbText.charAt(i)), true), 0, false)));
						sbTmp.append("</bpt>");
					}
					else
					{
						sbTmp.append(String.format("<g id=\"%1$d\">", getCodeID(SToI(m_sbText.charAt(++i)))));
					}
					sbTmp.append('}');
					continue;

				case InlineCode.CLOSING:
					if ( i+1 >= m_sbText.length() )	continue;
					sbTmp.append(m_sRTFStartInLine);
					if ( p_bUseBPT )
					{
						sbTmp.append(String.format("<ept id=\"%1$d\">", getCodeID(SToI(m_sbText.charAt(++i)))));
						sbTmp.append(escapeToRTF(
							Utils.escapeToXML(getCode(SToI(m_sbText.charAt(i)), true), 0, false)));
						sbTmp.append("</ept>");
					}
					else
					{
						sbTmp.append("</g>");
						i++; // Skip code even if we don't use it here
					}
					sbTmp.append('}');
					continue;
				
				case InlineCode.ISOLATED:
					if ( i+1 >= m_sbText.length() )	continue;
					if ( getCodeLabel(SToI(m_sbText.charAt(++i))).equals("protected") )
						sbTmp.append(m_sRTFStartProtected);
					else
						sbTmp.append(m_sRTFStartInLine);
					if ( p_bUseBPT )
					{
						if ( getCode(SToI(m_sbText.charAt(i)), true).length() > 0 )
						{
							sbTmp.append(String.format("<ph id=\"%1$d\">", getCodeID(SToI(m_sbText.charAt(i)))));
							sbTmp.append(escapeToRTF(
								Utils.escapeToXML(getCode(SToI(m_sbText.charAt(i)), true), 0, false)));
							sbTmp.append("</ph>");
						}
						else sbTmp.append(String.format("<ph id=\"%1$d\"/>",
								getCodeID(SToI(m_sbText.charAt(i)))));
					}
					else
					{
						sbTmp.append(String.format("<x id=\"%1$d\"/>", getCodeID(SToI(m_sbText.charAt(i)))));
					}
					sbTmp.append('}');
					continue;
				case '&':
					sbTmp.append("&amp;");
					continue;
				case '<':
					sbTmp.append("&lt;");
					continue;
				case '>':
					if (( i > 0 ) && ( m_sbText.charAt(i-1) == ']' )) sbTmp.append("&gt;");
					else sbTmp.append(m_sbText.charAt(i));
					continue;
				case '{':
				case '}':
				case '\\':
					sbTmp.append('\\');
					sbTmp.append(m_sbText.charAt(i));
					continue;

				case '\n':
					sbTmp.append("\r\n\\par ");
					continue;
				
				case '\u0009':
					sbTmp.append("\\tab ");
					continue;
				case '\u2022':
					sbTmp.append("\\bullet ");
					continue;
				case '\u2018':
					sbTmp.append("\\lquote ");
					continue;
				case '\u2019':
					sbTmp.append("\\rquote ");
					continue;
				case '\u201c':
					sbTmp.append("\\ldblquote ");
					continue;
				case '\u201d':
					sbTmp.append("\\rdblquote ");
					continue;
				case '\u2013':
					sbTmp.append("\\endash ");
					continue;
				case '\u2014':
					sbTmp.append("\\emdash ");
					continue;
				case '\u200d':
					sbTmp.append("\\zwj ");
					continue;
				case '\u200c':
					sbTmp.append("\\zwnj ");
					continue;
				case '\u200e':
					sbTmp.append("\\ltrmark ");
					continue;
				case '\u00a0':
					sbTmp.append("\\~"); // No space after control symbols
					continue;

				default:
					if ( m_sbText.codePointAt(i) > 127 )
					{
						ByteBuffer bBuf = m_Encoding.encode(Integer.toString(m_sbText.codePointAt(i)));
						if ( bBuf.limit() > 1 )
						{
							sbTmp.append(String.format("{{\\uc%1$d", bBuf.limit()));
							sbTmp.append(String.format("\\u%1$d", m_sbText.codePointAt(i))); 
							for ( int b=0; b<bBuf.limit(); b++ )
								sbTmp.append(String.format("\\'%1$x", bBuf.getChar(b)));
							sbTmp.append("}");
						}
						else
						{
							sbTmp.append(String.format("\\u%1$d", m_sbText.codePointAt(i)));
							sbTmp.append(String.format("\\'%1$x", bBuf.getChar(0)));
						}
					}
					else sbTmp.append(m_sbText.charAt(i));
					continue;
			}
		}

		return sbTmp.toString();
	}

	static public String genericToCoded (String p_sText,
		IFilterItem p_FI) {
		StringBuilder  sbTmp = new StringBuilder();
		if (( p_sText == null ) || ( p_sText.length() == 0 )) return "";
		int nType = InlineCode.OPENING;
		int nState = 0;
		int nStart = -1;

		for ( int i=0; i<p_sText.length(); i++ ) {
			switch ( nState ) {
				case 0:
					if ( p_sText.codePointAt(i) == '<' ) {
						nStart = i;
						nState = 1;
					}
					else {
						sbTmp.append(p_sText.charAt(i));
					}
					continue;

				case 1: // After <
					if ( p_sText.codePointAt(i) == '/' ) {
						nType = InlineCode.CLOSING; // Closing code
						nState = 2;
						continue;
					}
					if ( !Character.isDigit(p_sText.codePointAt(i)) ) {
						sbTmp.append('<'); // Output '<'
						i--; // Then redo the char after '<'
						nState = 0; // In normal state
						continue;
					}
					// Else: opening/closing code
					nType = InlineCode.OPENING; // Assumes opening code
					nState = 2;
					continue;

				case 2: // Waiting for '/' or '>'
					if ( p_sText.codePointAt(i) == '/' ) {
						i++;
						if (( i > p_sText.length() ) || ( p_sText.codePointAt(i) != '>' )) {
							// Not the end of a placeholder
							sbTmp.append(p_sText.subSequence(nStart, i));
							i -= 2; // Redo the current char
							nState = 0;
							continue;
						}
						// Else: isolated code
						sbTmp.append((char)InlineCode.ISOLATED);
						int n = Integer.valueOf(p_sText.substring(nStart+1, i-1));
						n = p_FI.getCodeIndex(n, InlineCode.ISOLATED);
						sbTmp.append(FilterItem.IToS(n));
						nState = 0;
						continue;
					}
					if ( p_sText.codePointAt(i) == '>' ) { // Opening or closing code
						sbTmp.append((char)nType);
						int n = Integer.valueOf(p_sText.substring(
							nStart+1+((nType==InlineCode.CLOSING) ? 1 : 0), i));
						n = p_FI.getCodeIndex(n, nType);
						sbTmp.append(FilterItem.IToS(n));
						nState = 0;
						continue;
					}
					if ( !Character.isDigit(p_sText.codePointAt(i))
							&& ( p_sText.codePointAt(i) != '|' )) {
						// Not an id
						sbTmp.append('<');
						if ( nState == 1 ) sbTmp.append('/');
						sbTmp.append(p_sText.substring(nStart, i));
						i--; // Redo the current char
						nState = 0;
						continue;
					}
					continue;
			}
		}

		if ( nState > 0 ) {
			// Case of "<123" or "</123"
			sbTmp.append('<');
			//if ( nState == 1 ) sbTmp.append('/');
			sbTmp.append(p_sText.substring(nStart));
		}

		return sbTmp.toString();
	}
}
