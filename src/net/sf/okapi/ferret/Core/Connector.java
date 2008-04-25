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

import java.io.File;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import net.sf.okapi.Filter.IFilterItem;
import net.sf.okapi.Library.Base.ILog;
import net.sf.okapi.Library.Base.Utils;
import net.sf.okapi.Translation.IMatch;
import net.sf.okapi.Translation.ITMQuery;
import net.sf.okapi.Translation.Match;

public class Connector implements ITMQuery {
	
	private ILog             m_Log;
	private TMBase           m_TM = null;
	private int              m_nMax = 5;
	private int              m_nCurrent = -1;
	private Vector<Match>    m_aHits = new Vector<Match>();
	
	public Connector (ILog p_Log) {
		m_Log = p_Log;
	}
	
	public boolean isTMOpened () {
		if ( m_TM == null ) return false;
		return m_TM.isTMOpened();
	}
	
	public void createTM (String p_sPath) {
		try
		{ //TODO: language handling
			String sBasePath = Utils.removeExtension(p_sPath);
			File F = new File(sBasePath+".properties");
			if ( F.exists() ) {
				m_TM.deleteTM(sBasePath);
			}
			m_TM.createTM(sBasePath, "en", "fr");
		}
		catch ( Exception E ) {
			Utils.showError(E.getMessage(), null);
			m_Log.error(E.getMessage());
		}
	}

	public void importTMX (String p_sPath) {
		try {
			m_TM.importTMX(p_sPath);
		}
		catch ( Exception E ) {
			Utils.showError(E.getMessage(), null);
			m_Log.error(E.getMessage());
		}
	}

	public void close () {
		try
		{
			m_aHits.clear();
			m_nCurrent = -1;
			if ( m_TM != null ) m_TM.closeTM();
		}
		catch ( Exception E ) {
			Utils.showError(E.getMessage(), null);
			m_Log.error(E.getMessage());
		}
	}

	public int getCount() {
		return m_aHits.size();
	}

	public IMatch getNextMatch() {
		if ( m_nCurrent+1 >= m_aHits.size() ) return null;
		return m_aHits.get(++m_nCurrent);
	}

	public void login (String p_sConnection,
		String p_sUsername,
		String p_sPassword)
	{
		try {
			logout();
			Class.forName("org.hsqldb.jdbcDriver");
			m_TM = new HSQLDbBackend(m_Log);
			m_TM.login();
		}
		catch ( Exception E ) {
			Utils.showError(E.getMessage(), null);
			m_Log.error(E.getMessage());
		}
	}

	public void logout () {
		close();
	}

	public void open (String p_sName) {
		try
		{
			String sBasePath = Utils.removeExtension(p_sName);
			m_TM.openTM(sBasePath);
		}
		catch ( Exception E ) {
			Utils.showError(E.getMessage(), null);
			m_Log.error(E.getMessage());
		}
	}

	public int query (String p_sText)
	{
		int nCount = 0;
		Statement Stm = null;
		m_aHits.clear();
		m_nCurrent = -1;
		try {
			Stm = m_TM.searchItem(p_sText);
			ResultSet rsHits = Stm.getResultSet();
			if ( rsHits.last() ) {
				nCount = rsHits.getRow();
				if ( nCount > m_nMax ) nCount = m_nMax;
			}
			if ( nCount == 0 ) return 0;
			
			// Fill the matches
			rsHits.beforeFirst();
			for ( int i=0; i<nCount; i++ ) {
				if ( !rsHits.next() ) break;
				Match M = new Match();
				String sSrc = rsHits.getString(TMBase.ITMCOLI_STEXT);
				if ( sSrc.equals(p_sText) ) M.setScore(100);
				else M.setScore(88);
				M.setSourceText(sSrc);
				M.setTargetText(rsHits.getString(TMBase.ITMCOLI_TTEXT));
				m_aHits.add(M);
			}
		}
		catch ( Exception E ) {
			Utils.showError(E.getMessage(), null);
			m_Log.error(E.getMessage());
			m_aHits.clear();
			m_nCurrent = -1;
		}
		finally {
			if ( Stm != null ) {
				try { Stm.close(); } catch ( Exception E ) {};
				Stm = null;
			}
		}
		return nCount;
	}

	public int query (IFilterItem p_FI) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void resetList () {
		m_nCurrent = -1;
	}

	public int getMaximum () {
		return m_nMax;
	}

	public void setMaximum (int p_nValue) {
		if ( p_nValue < 1 ) p_nValue = 1;
		m_nMax = p_nValue;
	}


}
