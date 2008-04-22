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

package net.sf.okapi.ferret.Core;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sf.okapi.Filter.FilterItemText;
import net.sf.okapi.Filter.IFilterItem;
import net.sf.okapi.Format.TMX.Item;
import net.sf.okapi.Format.TMX.Reader;
import net.sf.okapi.Library.Base.ILog;

public abstract class TMBase {

	public static final String    TBLNAME_INF         = "Info";
	public static final String    INFCOLN_DATA1       = "Data1";
	public static final int       INFCOLI_DATA1       = 1;
	public static final String    INFCOLN_VERSION     = "Version";
	public static final int       INFCOLI_VERSION     = 2;
	public static final String    INFCOLN_NAME        = "Name";
	public static final int       INFCOLI_NAME        = 3;
	public static final String    INFCOLN_INVARIANT   = "Invariant";
	public static final int       INFCOLI_INVARIANT   = 4;

	public static final String    TBLNAME_ITM         = "Items";
	public static final String    ITMCOLN_KEY         = "PK";
	public static final int       ITMCOLI_KEY         = 1;
	public static final String    ITMCOLN_STEXT       = "SText";
	public static final int       ITMCOLI_STEXT       = 2;
	public static final String    ITMCOLN_IDXKEY      = "IdxKey";
	public static final int       ITMCOLI_IDXKEY      = 3;
	public static final String    ITMCOLN_WORDS       = "Words";
	public static final int       ITMCOLI_WORDS       = 4;
	public static final String    ITMCOLN_TTEXT       = "TText";
	public static final int       ITMCOLI_TTEXT       = 5;

	protected Connection     m_Conn = null;
	protected boolean        m_bTMOpened = false;
	protected String         m_sName = null;
	protected String         m_sPrefix = null;
	protected StopWords      m_SWL;
	protected ILog           m_Log;
	
	static public String escapeText (String p_sText)
	{
		if ( p_sText == null ) return "";
		StringBuilder sbTmp = new StringBuilder(p_sText.length());

		for ( int i=0; i<p_sText.length(); i++ )
		{
			switch ( p_sText.charAt(i) )
			{
//				case '%':
//				case '*':
//				case '[':
//				case ']':
//					sbTmp.append("["+p_sText.charAt(i)+"]");
//					break;
				case '\'':
					sbTmp.append("''");
					break;
				default:
					sbTmp.append(p_sText.charAt(i));
					break;
			}
		}
		return sbTmp.toString();
	}
	
	public TMBase (ILog p_Log) {
		m_SWL = new StopWords();
		m_SWL.loadList();
		m_Log = p_Log;
	}

	protected void finalize ()
		throws Throwable
	{
	    try {
	    	logout();
	    } finally {
	        super.finalize();
	    }
	}
	
	public abstract void login ()
		throws Exception;

	public void logout ()
		throws SQLException
	{
		if ( m_Conn != null )
		{
			if ( isTMOpened() ) {
				closeTM();
			}
			if ( m_Conn != null ) { // Need to test again
				m_Conn.close();
				m_Conn = null;
			}
		}
	}
	
	public String getTableName (String p_sName) {
		return m_sPrefix + p_sName;
	}
	
	public boolean isTMOpened () {
		return m_bTMOpened;
	}
	
	public String makePrefix (String p_sValue) {
		return "";
	}
	
	public abstract void createTM (String p_sName,
		String p_sSrcLang,
		String p_sTrgLang)
		throws Exception;

	public abstract void openTM (String p_sName)
		throws Exception;
	
	public abstract void closeTM ()
		throws SQLException;
	
	public abstract void deleteTM (String p_sName)
		throws SQLException, Exception;

	/**
	 * Construct a search key for a given text.
	 * @param p_sText original text to process.
	 * @param p_sbKey String where to put the search key. You MUST create the object
	 * just before call the method.
	 * @return
	 */
	public int makeKey (String p_sText,
		StringBuilder p_sbKey)
	{
		p_sText = p_sText.trim().toLowerCase();
		if ( p_sText == null ) return 0;

		//TODO: convert numbers into one generic case.
		
		String sWord;
		int nWordCount = 0;
		int nStart = 0;
		for ( int i=0; i<p_sText.length(); i++ ) {
			if (( Character.isWhitespace(p_sText.charAt(i)) )
				|| ( "(){}[].;:?!%+".indexOf(p_sText.charAt(i)) != -1 ))
			{
				if ( i == nStart ) {
					nStart++;
					continue;
				}
				else {
					sWord = p_sText.substring(nStart, i);
					nWordCount++;
					if ( !m_SWL.getList().contains(sWord) ) {
						p_sbKey.append(sWord);
					}
					nStart = i+1;
				}
			}
		}

		sWord = p_sText.substring(nStart);
		if ( sWord.length() > 0 ) {
			nWordCount++;
			if ( !m_SWL.getList().contains(sWord) ) {
				p_sbKey.append(p_sText.substring(nStart));
			}
		}

		return nWordCount;
	}
	
	public void addItem (String p_sSrcText,
			String p_sTrgText)
		throws Exception
	{
		Statement Stm = null;
		try
		{
			if (( p_sSrcText == null ) || ( p_sSrcText.length() == 0 )) return;
			StringBuilder sbKey = new StringBuilder();
			int nWC = makeKey(p_sSrcText, sbKey);
			if ( nWC == 0 ) return;
			
			Stm = m_Conn.createStatement();
			String sTmp = String.format("INSERT INTO %s (%s,%s,%s,%s) VALUES('%s','%s',%d,'%s')",
				getTableName(TBLNAME_ITM), ITMCOLN_STEXT, ITMCOLN_IDXKEY, ITMCOLN_WORDS, ITMCOLN_TTEXT,
				escapeText(p_sSrcText), escapeText(sbKey.toString()), nWC, escapeText(p_sTrgText));
			Stm.execute(sTmp);

			// Commit everything
			m_Conn.commit();
		}
		catch ( Exception E ) {
			m_Conn.rollback();
			throw E;
		}
		finally {
			if ( Stm != null ) {
				Stm.close();
				Stm = null;
			}
		}
	}
	
	public Statement searchItem (String p_sText)
		throws SQLException
	{
		Statement Stm = null;
		if (( p_sText == null ) || ( p_sText.length() == 0 )) return null;
		StringBuilder sbKey = new StringBuilder();
		int nWC = makeKey(p_sText, sbKey);
		if ( nWC == 0 ) return null;

		Stm = m_Conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);			
		String sTmp = String.format("SELECT * FROM %s WHERE %s LIKE '%s'",
			getTableName(TBLNAME_ITM), ITMCOLN_IDXKEY, escapeText(sbKey.toString()));
		Stm.executeQuery(sTmp);
		return Stm;
	}

	public void importTMX (String p_sPath)
		throws Exception
	{
		Reader TR = null;
		try
		{
			TR = new Reader(m_Log);
			TR.open(p_sPath, null, null);
			
			Item ITM;
			IFilterItem FI;
			
			while ( TR.readItem() > Reader.RESULT_ENDOFDOC ) {
				FI = TR.getSource();
				while ( (ITM = TR.getNextTarget()) != null ) {
					addItem(FI.getText(FilterItemText.PLAIN),
						ITM.getFI().getText(FilterItemText.PLAIN));
				}
			}
		}
		finally
		{
			if ( TR != null ) {
				TR.close();
				TR = null;
			}
		}
		
	}
}
