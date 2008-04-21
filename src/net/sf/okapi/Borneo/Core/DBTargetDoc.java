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

package net.sf.okapi.Borneo.Core;

import java.util.Enumeration;
import java.util.Hashtable;

public class DBTargetDoc {

	private boolean          m_bExcluded;
	private String           m_sEncoding;
	private boolean          m_bUseCustomPB;
	private PathBuilder      m_CustomPB;
	private String           m_sStatus;


	static public String convertToString (Hashtable<String, DBTargetDoc> p_Targets)
	{
		StringBuilder sbTmp = new StringBuilder();
		sbTmp.append(String.format("%d", p_Targets.size())+"\t"); // Number of targets
		
		Enumeration<String> E = p_Targets.keys();
		String sLang;
		while ( E.hasMoreElements() ) {
			sLang = E.nextElement();
			DBTargetDoc TFL = p_Targets.get(sLang);
			if ( TFL == null ) TFL = new DBTargetDoc();
			sbTmp.append(sLang+"\t"); // Target code
			sbTmp.append((TFL.isExcluded() ? "1" : "0")+"\t"); // Excluded?
			if ( TFL.getCustomPB() != null ) sbTmp.append(TFL.getCustomPB().toString()+"\t");
			sbTmp.append("\t");
			if ( TFL.m_sEncoding != null ) sbTmp.append(TFL.getEncoding());
			sbTmp.append("\t");
			sbTmp.append(TFL.getStatus()+"\t");
		}
		return sbTmp.toString();
	}

	static public Hashtable<String, DBTargetDoc> convertFromString (String p_sData)
	{
		Hashtable<String, DBTargetDoc> TrgCol
			= new Hashtable<String,DBTargetDoc>();

		if (( p_sData == null ) || ( p_sData.length() == 0 )) return TrgCol;

		String[] aFld = p_sData.split("\t", -2);
		int nCount = Integer.valueOf(aFld[0]);
		int n = 0;
		String sLang;
		DBTargetDoc TFL;
		for ( int i=0; i<nCount; i++ ) {
			sLang = aFld[++n];
			TFL = new DBTargetDoc();
			TFL.setIsExcluded(aFld[++n]=="1");
			if ( aFld[++n].length() == 0 ) TFL.setCustomPB(null);
			else TFL.getCustomPB().fromString(aFld[n]);
			TFL.setEncoding(aFld[++n]);
			TFL.setStatus(aFld[++n]);
			TrgCol.put(sLang, TFL);
		}

		return TrgCol;
	}

	public DBTargetDoc () {
		m_bExcluded = false;
		m_sEncoding = null;
		m_bUseCustomPB = false;
		m_CustomPB = null;
		m_sStatus = "";
	}

	public boolean isExcluded () {
		return m_bExcluded;
	}

	public void setIsExcluded (boolean p_bValue) {
		m_bExcluded = p_bValue;
	}

	public String getEncoding () {
		return m_sEncoding;
	}
	
	public void setEncoding (String p_sValue) {
		if (( p_sValue != null ) && ( p_sValue.length() == 0 )) 
			m_sEncoding = null;
		else
			m_sEncoding = p_sValue;
	}

	public boolean useCustomPB () {
		return m_bUseCustomPB;
	}

	public String getStatus () {
		return m_sStatus;
	}

	public void setStatus (String p_sValue) {
		m_sStatus = p_sValue;
	}

	public PathBuilder getCustomPB () {
		return m_CustomPB;
	}

	public void setCustomPB (PathBuilder p_Value) {
		m_CustomPB = p_Value;
	}

	public String getFullPath (DBDoc p_Doc,
		DBTarget p_Target,
		String p_sSourceRoot,
		String p_sTarget)
	{
		if ( useCustomPB() )
			return m_CustomPB.getPath(p_Doc.getFullPath(), p_sSourceRoot, p_Target.getRoot(), p_sTarget);
		else
			return p_Target.getDefaultPB().getPath(p_Doc.getFullPath(), p_sSourceRoot,
				p_Target.getRoot(), p_sTarget);
	}

}
