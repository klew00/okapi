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

public interface IFilterItem {

	/**
	 * Appends a character to the text text.
	 * @param p_chValue The character to append.
	 */
	void appendChar (char p_chValue);

	void appendCode (int p_nType,
		String p_sLabel,
		String p_sData);

	/**
	 * Appends a string to the item text.
	 * @param p_sValue The string to append.
	 */
	void appendText (String p_sValue);

	int changeToCode (int p_nStart,
		int p_nCodeIndex,
		int p_nCodeLength,
		int p_nTextIndex,
		int p_nTextLength);

	void copyFrom (IFilterItem p_FilterItem);

	IFilterItem extract (int p_nStart,
		int p_nLength,
		boolean p_bAddMissingCodes);

	String getCode (int p_nIndex,
		boolean p_bStandardLineBreaks);

	int getCodeCount ();

	int getCodeID (int p_nIndex);

	int getCodeIndex (int p_nID,
		int p_nType);

	String getCodeLabel (int p_nIndex);

	String getCodeMapping ();

	String getCoord ();

	float getCX ();

	float getCY ();

	String getEncoding ();

	String getFont ();

	int getGroupID ();

	int getInterfaceVersion ();

	int getItemID ();

	int getItemType ();

	int getLength ();

	int getLevel ();

	String getLineBreak ();

	String getMimeType ();

	String getNote ();

	String getNoteAttributeFrom ();

	String getProperty (String p_sName);

	String getResName ();

	String getResType ();

	long getStart ();

	String getText (int p_nFormat);

	int getTextLength (int p_nFormat);

	float getX ();

	boolean getXMLStyle ();

	float getY ();

	boolean hasCode ();

	boolean hasCoord ();

	boolean hasFont ();

	boolean hasNote ();

	/**
	 * Indicates wether the item has some text. If the item has no text
	 * but has codes, this method will return false.
	 * @param p_bWhiteSpaceIsText True if the white spaces should count
	 * as text.
	 * @return True if the item has text, false otherwise.
	 */
	boolean hasText (boolean p_bWhiteSpaceIsText);

	boolean isEmpty ();

	boolean isPreFormatted ();

	/**
	 * Indicates if the item is a sub-flow.
	 * @return True if the item is a sub-flow, false otherwise.
	 */
	boolean isSubFlow ();

	boolean isTranslatable ();

	boolean isTranslated ();

	String listProperties ();

	void modifyText (String p_sValue);

	void normalizeLineBreaks ();

	void normalizeWhiteSpaces ();

	void removeEnd (int p_nCount);

	void reset ();

	void setCodeMapping (String p_sValue);

	void setCoord (String p_sValue);

	void setCX (float p_fValue);

	void setCY (float p_fValue);

	void setEncoding (String p_sValue);

	void setFont (String p_sValue);

	void setGroupID (int p_nValue);

	void setItemID (int p_nValue);

	void setItemType (int p_nValue);

	void setLength (int p_nValue);

	void setLevel (int p_nValue);

	void setLineBreak (String p_sValue);

	void setMimeType (String p_sValue);

	void setNote (String p_sNote,
		String p_sFrom);

	void setPreFormatted (boolean p_bValue);

	void setProperty (String p_sName,
		String p_sValue);

	void setResName (String p_sValue);

	/**
	 * Sets the restype value for the item.
	 * @param p_sValue Value to set.
	 */
	void setResType (String p_sValue);

	void setRTFOptions (String p_sRTFStartInLine,
		String p_sRTFStartProtected);

	void setStart (long p_lValue);

	/**
	 * Sets the flag indicating whether the item is a sub-flow or not.
	 * @param p_bValue True to set the item as a sub-flow, false to set
	 * it as a normal item.
	 */
	void setSubFlow (boolean p_bValue);

	/**
	 * Sets the text of the item, overriding any existing text and codes.
	 * @param p_sValue The new text of the item.
	 */
	void setText (String p_sValue);

	void setTranslatable (boolean p_bValue);

	void setTranslated (boolean p_bValue);

	void setX (float p_fValue);

	void setXMLStyle (boolean p_bValue);

	void setY (float p_fValue);

}
