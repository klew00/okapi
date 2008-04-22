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
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;

import net.sf.okapi.Library.Base.ILog;

public class HSQLDbBackend extends TMBase
{
	public HSQLDbBackend (ILog p_Log) {
		super(p_Log);
	}
	
	public void login ()
		throws Exception
	{
		// Do nothing
	}

	public void createTM (String p_sName,
		String p_sSrcLang,
		String p_sTrgLang)
		throws Exception
	{
		Statement Stm = null;
		try
		{
			String sTmp = "jdbc:hsqldb:file:" + p_sName;
			m_Conn = DriverManager.getConnection(sTmp, "sa", "");

			m_Conn.setAutoCommit(false);
			m_sName = p_sName;
			m_sPrefix = makePrefix(p_sName);
		
			// Create the Info table
			Stm = m_Conn.createStatement();
			sTmp = "CREATE TABLE " + getTableName(TBLNAME_INF) + " ("
				+ INFCOLN_DATA1 + " VARCHAR,"
				+ INFCOLN_VERSION + " INTEGER,"
				+ INFCOLN_NAME + " VARCHAR,"
				+ INFCOLN_INVARIANT + " INTEGER"
				+ ")";
			Stm.execute(sTmp);
		
			// Insert seed info for the project
			sTmp = String.format("INSERT INTO %s (%s,%s,%s,%s) VALUES('',1,'%s',1)",
				getTableName(TBLNAME_INF), INFCOLN_DATA1, INFCOLN_VERSION,
				INFCOLN_NAME, INFCOLN_INVARIANT, escapeText(p_sName));
			Stm.execute(sTmp);
			
			// Create the Items table
			sTmp = "CREATE TABLE " + getTableName(TBLNAME_ITM) + " ("
				+ ITMCOLN_KEY + " INTEGER IDENTITY PRIMARY KEY,"
				+ ITMCOLN_STEXT + " VARCHAR,"
				+ ITMCOLN_IDXKEY + " VARCHAR,"
				+ ITMCOLN_WORDS + " INTEGER,"
				+ ITMCOLN_TTEXT + " VARCHAR"
				+ ")";
			Stm.execute(sTmp);

			// Commit everything
			m_Conn.commit();
			m_bTMOpened = true;
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
	
	public void openTM (String p_sName)
		throws Exception
	{
		try
		{
			closeTM();
			//TODO: Check if the project really exits (created by default otherwise)
			String sTmp = "jdbc:hsqldb:file:" + p_sName;
			m_Conn = DriverManager.getConnection(sTmp);
			m_sName = p_sName;
			m_sPrefix = makePrefix(p_sName);
			m_bTMOpened = true;
		}
		catch ( Exception E ) {
			throw E;
		}
	}
	
	public void closeTM ()
		throws SQLException
	{
		if ( m_Conn != null ) {
			Statement Stm = m_Conn.createStatement();
			Stm.execute("SHUTDOWN");
			m_Conn.close();
			m_Conn = null;
			m_sName = null;
			m_sPrefix = null;
		}
		m_bTMOpened = false;
	}
	
	public void deleteTM (String p_sName)
		throws SQLException, Exception
	{
		if (( m_sName != null ) && ( m_sName.equalsIgnoreCase(p_sName) )) {
			throw new Exception(Res.getString("CANTDELETE_PRJOPENED"));
		}
		
		// Delete all the files associated with the TM:
		// .properties, .script, .data, .lck, .log
		File F = new File(p_sName+".lck");
		F.delete();
		F = new File(p_sName+".data");
		F.delete();
		F = new File(p_sName+".script");
		F.delete();
		F = new File(p_sName+".log");
		F.delete();
		F = new File(p_sName+".properties");
		F.delete();
	}
	
}
