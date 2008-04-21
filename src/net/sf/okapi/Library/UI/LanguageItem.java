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

package net.sf.okapi.Library.UI;

import net.sf.okapi.Library.Base.Utils;

public class LanguageItem {

	private String      m_sName;
	private String      m_sCode;
	private String      m_sEncodingW;
	private String      m_sEncodingM;
	private String      m_sEncodingU;
	private int         m_nLCID;
	
	public String toString () {
		return m_sName;
	}
	
	public String getName () {
		return m_sName;
	}
	
	public void setName (String p_sValue) {
		m_sName = p_sValue;
	}
	
	public String getCode () {
		return m_sCode;
	}
	
	public void setCode (String p_sValue) {
		m_sCode = p_sValue;
	}
	
	public String getEncoding (int p_nPFType) {
		String sTmp;
		switch ( p_nPFType ) {
		case Utils.PFTYPE_MAC:
			sTmp = m_sEncodingM;
			break;
		case Utils.PFTYPE_UNIX:
			sTmp = m_sEncodingU;
			break;
		default:
			sTmp = m_sEncodingW;
			break;
		}
		if ( sTmp == null ) return m_sEncodingW;
		else return sTmp;
	}
	
	public void setEncoding (String p_sValue,
		int p_nPFType) {
		switch ( p_nPFType ) {
		case Utils.PFTYPE_MAC:
			m_sEncodingM = p_sValue;
			break;
		case Utils.PFTYPE_UNIX:
			m_sEncodingU = p_sValue;
			break;
		default:
			m_sEncodingW = p_sValue;
			break;
		}
	}
	
	public int getLCID () {
		return m_nLCID;
	}
	
	public void setLCID (int p_nValue) {
		m_nLCID = p_nValue;
	}
	
}
