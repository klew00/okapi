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

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class MySQLBackend extends DBBase {
	
	private static String    PRJLISTTBL = "BnoProjectList";
	
	private String      m_sServerInfo = "";
	private String      m_sDatabaseInfo = "";

	@Override
	public String escapeText (String p_sText)
	{
		if ( p_sText == null ) return "";
		StringBuilder sbTmp = new StringBuilder(p_sText.length());

		for ( int i=0; i<p_sText.length(); i++ )
		{
			switch ( p_sText.charAt(i) )
			{
				case '%':
				case '*':
				case '[':
				case ']':
					sbTmp.append(String.format("[%c]", p_sText.charAt(i)));
					break;
				case '\'':
				case '\\':
					sbTmp.append('\\');
					sbTmp.append(p_sText.charAt(i));
					break;
				default:
					sbTmp.append(p_sText.charAt(i));
					break;
			}
		}
		return sbTmp.toString();
	}
	
	public void login (DBOptions p_Options)
		throws Exception
	{
		try
		{
			String sTmp = String.format("jdbc:mysql://%s:%d/%s",
				p_Options.getServer(), p_Options.getPort(), p_Options.getDatabase());
			m_Conn = DriverManager.getConnection(sTmp,
				p_Options.getUsername(), p_Options.getPassword());
			m_sServerInfo = String.format("%s:%d", p_Options.getServer(), p_Options.getPort());
			m_sDatabaseInfo = p_Options.getDatabase();
			m_sDBTypeInfo = Res.getString("DBTypeMYSQL");
		}
		catch ( Exception E )
		{
			throw E;
		}
	}
	
	public String[] getProjectList ()
	{
		Statement Stm = null;
		String[] aRes = null;
		try {
			Stm = m_Conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rsTmp = Stm.executeQuery("SELECT * FROM " + PRJLISTTBL);
			if ( !rsTmp.last() ) aRes = new String[0];
			else {
				aRes = new String[rsTmp.getRow()];
				rsTmp.beforeFirst();
			}
			int i = 0;
			while ( rsTmp.next() ) {
				aRes[i] = rsTmp.getString(1);
			}
		}
		catch ( Exception E ) {
			// Return null on error (likely the table does not exists)
			aRes = null;
		}
		finally {
			if ( Stm != null ) {
				try { Stm.close(); } catch ( Exception E ) {};
				Stm = null;
			}
		}
		return aRes;
	}

	public String[] getDBInfo () {
		String[] aInfo = new String[3];
		aInfo[0] = m_sDBTypeInfo;
		aInfo[1] = m_sServerInfo;
		aInfo[2] = m_sDatabaseInfo;
		return aInfo;
	}

	protected String makePrefix (String p_sValue) {
		return String.format("P%X_", p_sValue.toLowerCase().hashCode());
	}
	
	public void createProject (String p_sName,
		String p_sPath,
		String p_sSrcLang,
		String p_sEncoding)
		throws Exception
	{
		boolean bWasAutoCommit = false;
		Statement Stm = null;
		try
		{
			bWasAutoCommit = m_Conn.getAutoCommit();
			if ( bWasAutoCommit ) m_Conn.setAutoCommit(false);
			
			m_sName = p_sName;
			m_sPrefix = makePrefix(p_sName);
			m_sStorage = p_sPath;
			
			// Update the project list
		
			// Create the Info table
			Stm = m_Conn.createStatement();
			String sTmp = "CREATE TABLE " + getTableName(TBLNAME_INF) + " ("
				+ INFCOLN_DATA1 + " TEXT,"
				+ INFCOLN_VERSION + " INTEGER,"
				+ INFCOLN_NAME + " TEXT,"
				+ INFCOLN_STORAGE + " TEXT,"
				+ INFCOLN_INVARIANT + " INTEGER"
				+ ")";
			Stm.execute(sTmp);

			// Insert seed info for the project
			sTmp = String.format("INSERT INTO %s (%s,%s,%s,%s,%s) VALUES('',1,'%s','%s',1)",
				getTableName(TBLNAME_INF), INFCOLN_DATA1, INFCOLN_VERSION,
				INFCOLN_NAME, INFCOLN_STORAGE, INFCOLN_INVARIANT, escapeText(p_sName),
				escapeText(m_sStorage));
			Stm.execute(sTmp);
			
			// Create the Langs table
			sTmp = "CREATE TABLE " + getTableName(TBLNAME_LNG) + " ("
				+ LNGCOLN_CODE + " TEXT,"
				+ LNGCOLN_ROOT + " TEXT,"
				+ LNGCOLN_ENCODING + " TEXT,"
				+ LNGCOLN_PATHBLD + " TEXT"
				+ ", PRIMARY KEY(" + LNGCOLN_CODE + String.format("(%d))", LANGCODE_MAX)
				+ ")";
			Stm.execute(sTmp);
			
			// Create the documents table
			sTmp = "CREATE TABLE " + getTableName(TBLNAME_DOC) + " ("
				+ DOCCOLN_KEY + " INTEGER AUTO_INCREMENT PRIMARY KEY,"
				+ DOCCOLN_SRCTYPE + " INTEGER,"
				+ DOCCOLN_STATUS + " TEXT,"
				+ DOCCOLN_SUBDIR + " TEXT,"
				+ DOCCOLN_FILENAME + " TEXT,"
				+ DOCCOLN_FSETTINGS + " TEXT,"
				+ DOCCOLN_ENCODING + " TEXT,"
				+ DOCCOLN_FILESET + " TEXT,"
				+ DOCCOLN_TARGETS + " TEXT"
				+ ")";
			Stm.execute(sTmp);
			
			// Create the Source table
			sTmp = "CREATE TABLE " + getTableName(TBLNAME_SRC) + " ("
				+ SRCCOLN_KEY + " INTEGER AUTO_INCREMENT PRIMARY KEY,"
				+ SRCCOLN_DKEY + " INTEGER,"
				+ SRCCOLN_GKEY + " INTEGER,"
				+ SRCCOLN_XKEY + " INTEGER,"
				+ SRCCOLN_FLAG + " BOOLEAN,"
				+ SRCCOLN_RESNAME + " TEXT,"
				+ SRCCOLN_RESTYPE + " TEXT,"
				+ SRCCOLN_NOTRANS + " BOOLEAN,"
				+ SRCCOLN_STATUS + " INTEGER,"
				+ SRCCOLN_TEXT + " TEXT,"
				+ SRCCOLN_CODES + " TEXT,"
				+ SRCCOLN_COMMENT + " TEXT,"
				+ SRCCOLN_START + " INTEGER,"
				+ SRCCOLN_LENGTH + " INTEGER,"
				+ SRCCOLN_MAXWIDTH + " INTEGER,"
				+ SRCCOLN_PREVTEXT + " TEXT"
				+ ")";
			Stm.execute(sTmp);
			
			sTmp = "CREATE TABLE " + getTableName(TBLNAME_TRG) + " ("
				+ TRGCOLN_KEY + " INTEGER AUTO_INCREMENT PRIMARY KEY,"
				+ TRGCOLN_LANG + String.format(" TEXT(%d),", LANGCODE_MAX)
				+ TRGCOLN_DKEY + " INTEGER,"
				+ TRGCOLN_SKEY + " INTEGER,"
				+ TRGCOLN_GKEY + " INTEGER,"
				+ TRGCOLN_XKEY + " INTEGER,"
				+ TRGCOLN_FLAG + " BOOLEAN,"
				+ TRGCOLN_STATUS + " INTEGER,"
				+ TRGCOLN_RESNAME + " TEXT,"
				+ TRGCOLN_RESTYPE + " TEXT,"
				+ TRGCOLN_STEXT + " TEXT,"
				+ TRGCOLN_TTEXT + " TEXT,"
				+ TRGCOLN_SCODES + " TEXT,"
				+ TRGCOLN_TMP + " INTEGER"
				+ ")";
			Stm.execute(sTmp);

			// Save project defaults settings
			setSourceRoot(System.getProperty("user.home"));
			setSourceLanguage(p_sSrcLang);
			setSourceEncoding(p_sEncoding);
			setAutoSourceEncoding(true);
			m_sProjectID = java.util.UUID.randomUUID().toString();
			saveSettings();
			
			// Commit everything
			if ( bWasAutoCommit ) m_Conn.commit();
			m_bProjectOpened = true;
		}
		catch ( Exception E ) {
			if ( bWasAutoCommit ) m_Conn.rollback();
			throw E;
		}
		finally {
			if ( bWasAutoCommit ) m_Conn.setAutoCommit(true);
			if ( Stm != null ) {
				Stm.close();
				Stm = null;
			}
		}
	}
	
	public void openProject (String p_sName)
		throws Exception
	{
		m_sName = p_sName;
		m_sPrefix = makePrefix(p_sName);
		readSettings();
		//TODO: check if the project exists!!!
		m_bProjectOpened = true;
	}
	
	public void closeProject ()
		throws SQLException
	{
		m_sName = null;
		m_sPrefix = null;
		m_bProjectOpened = false;
	}
	
	public void deleteProject (String p_sName)
		throws SQLException, Exception
	{
		boolean bWasAutoCommit = false;
		Statement Stm = null;
		String sPrevPrefix = m_sPrefix;
		try
		{
			bWasAutoCommit = m_Conn.getAutoCommit();
			if ( bWasAutoCommit ) m_Conn.setAutoCommit(false);

			// Check if the project is opened
			String sTmp = makePrefix(p_sName);
			if ( m_sPrefix != null ) {
				if ( m_sPrefix.equals(sTmp) ) {
					throw new Exception("The project is opened.");
				}
			}

			m_sPrefix = sTmp; // used in fetchLanguages()
			Stm = m_Conn.createStatement();

			// Remove the target tables
			Vector<String> aLangs = fetchLanguageList();
			for ( int i=0; i<aLangs.size(); i++ ) {
				sTmp = "DROP TABLE " + getTableName(aLangs.get(i));
				Stm.execute(sTmp);
			}

			// Delete the other tables
			sTmp = String.format("DROP TABLE %s,%s,%s,%s", getTableName(TBLNAME_INF),
				getTableName(TBLNAME_LNG), getTableName(TBLNAME_SRC),
				getTableName(TBLNAME_DOC));
			Stm.execute(sTmp);
			
			// Remove the storage
			//TODO: Remove the files			

			// Commit everything
			if ( bWasAutoCommit ) m_Conn.commit();
		}
		catch ( Exception E ) {
			if ( bWasAutoCommit ) m_Conn.rollback();
			throw E;
		}
		finally {
			if ( bWasAutoCommit ) m_Conn.setAutoCommit(true);
			m_sPrefix = sPrevPrefix;
			if ( Stm != null ) {
				Stm.close();
				Stm = null;
			}
		}
	}
	
	protected int getLastAutoGeneratedKey (Statement p_Stm)
		throws SQLException
	{
		int nLastValue = -1;
		ResultSet rsTmp = null;
		try
		{
			rsTmp = p_Stm.executeQuery("SELECT LAST_INSERT_ID()");
			if ( rsTmp.next() ) {
				nLastValue = rsTmp.getInt(1);
			}
	    }
		finally {
			if ( rsTmp != null ) {
				rsTmp.close();
				rsTmp = null;
			}
		}
		return nLastValue;
	}
	
}
