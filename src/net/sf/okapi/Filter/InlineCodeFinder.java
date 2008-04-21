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

package net.sf.okapi.Filter;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InlineCodeFinder {
	
	private Vector<String>   m_aRules;
	private int              m_nOptions;
	private String           m_sExpression;

	public void reset ()
	{
		m_sExpression = "";
		m_nOptions = Pattern.CASE_INSENSITIVE;
		m_aRules = new Vector<String>();
	}
	
	public Vector<String> getRules () {
		return m_aRules;
	}

	public int processFilterItem (IFilterItem p_Item)
	{
		int            nCount = 0;
		String         sText;
		Pattern        RE;
		int            nCorrection = 0;
		int            nStart;
		int            nTextIndex;
		int            nTextLength;


		// No need to process those
		if (( m_sExpression.length() == 0) || ( m_sExpression == "()" )) return 0;

		RE = Pattern.compile(m_sExpression, m_nOptions);
		sText = p_Item.getText(FilterItemText.CODED);
		Matcher M = RE.matcher(sText);
		if ( M == null ) return 0;
		
		while ( M.find() ) {
			nCount++;
			nStart = M.start() + nCorrection;
			//TODO: group naming needed for text insert handling
			//String sGrp = M.group(1); // M.Groups["text"];
			//if (( sGrp != null ) && ! sGrp.isEmpty() ) {
			//	nTextIndex = M.start(1);
			//	nTextLength = sGrp.length();
			//}
			//else {
				nTextIndex = -1;
				nTextLength = 0;
			//}
			nCorrection += p_Item.changeToCode(nStart, M.start(), M.group(0).length(),
				nTextIndex, nTextLength);
		}
		return nCount;
	}

	public void setOptions (String p_sValue)
	{
		/*
		FieldsString Tmp = new FieldsString(p_sValue);

		Reset();
		m_sSample = Tmp.Get("sample", m_sSample);
		m_Options = (RegexOptions)Tmp.Get("options", (int)m_Options);

		int nCount = Tmp.Get("nbrules", 0);
		if ( nCount > 0 )
		{
			m_aRules.Clear();
			for ( int i=0; i<nCount; i++ )
			{
				m_aRules.Add(Tmp.Get("rule"+i.ToString(), ""));
			}
		} // Else keep defaults
*/
		createExpression();
	}

	public String getOptions ()
	{
		//TODO
		return null;
	}

	public void addRule (String p_sRule) {
		m_aRules.add(p_sRule);
	}

	/**
	 * Build the complete expression from the different rules of the object.
	 * Each rule is ORed with the next one.
	 */
	public void createExpression ()
	{
		StringBuilder sbTmp = new StringBuilder(255);
		if ( m_aRules.size() > 1 ) sbTmp.append("(");
		for ( int i=0; i<m_aRules.size(); i++ ) {
			if ( sbTmp.length() > 1 ) sbTmp.append("|(" + m_aRules.get(i) + ")");
			else sbTmp.append("(" + m_aRules.get(i) + ")");
		}
		if ( m_aRules.size() > 1 ) sbTmp.append(")");
		m_sExpression = sbTmp.toString();
	}
	
	public String getExpression () {
		return m_sExpression;
	}
}
