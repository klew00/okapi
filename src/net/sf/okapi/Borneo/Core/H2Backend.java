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

import java.io.File;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;

import net.sf.okapi.Library.Base.Utils;

public class H2Backend extends DBBase
{
	public void login (DBOptions p_Options)
		throws Exception
	{
		m_sDBTypeInfo = Res.getString("DBTypeH2DB");
		// Nothing else to do
	}

	public String[] getProjectList () {
		return null; // Not supported
	}

	public String[] getDBInfo () {
		String[] aInfo = new String[3];
		aInfo[0] = m_sDBTypeInfo;
		aInfo[1] = "localhost";
		aInfo[2] = ((m_sName == null) ? "<None opened>" : m_sName);
		return aInfo;
	}
	
	public void createProject (String p_sName,
		String p_sPath,
		String p_sSrcLang,
		String p_sEncoding)
		throws Exception
	{
		boolean bWasAutoCommit = true;
		Statement Stm = null;
		try
		{
			m_sStorage = p_sPath + File.separator + p_sName;
			String sPath = m_sStorage + File.separator + p_sName;
			String sTmp = "jdbc:h2:" + sPath;
			m_Conn = DriverManager.getConnection(sTmp, "sa", "");

			bWasAutoCommit = m_Conn.getAutoCommit();
			if ( bWasAutoCommit ) m_Conn.setAutoCommit(false);
			
			m_sName = p_sName;
			m_sPrefix = makePrefix(m_sName);
		
			// Create the Info table
			Stm = m_Conn.createStatement();
			sTmp = "CREATE TABLE " + getTableName(TBLNAME_INF) + " ("
				+ INFCOLN_DATA1 + " VARCHAR,"
				+ INFCOLN_VERSION + " INTEGER,"
				+ INFCOLN_NAME + " VARCHAR,"
				+ INFCOLN_STORAGE + " VARCHAR,"
				+ INFCOLN_INVARIANT + " INTEGER"
				+ ")";
			Stm.execute(sTmp);
		
			// Insert seed info for the project
			// Note that STORAGE is not used with this DB type:
			// It should be set in readSettinsg() to the path of the project
			// so the project files can be moved without breaking anything
			sTmp = String.format("INSERT INTO %s (%s,%s,%s,%s) VALUES('',1,'%s',1)",
				getTableName(TBLNAME_INF), INFCOLN_DATA1, INFCOLN_VERSION,
				INFCOLN_NAME, INFCOLN_INVARIANT, escapeText(m_sName));
			Stm.execute(sTmp);
			
			// Create the Langs table
			sTmp = "CREATE TABLE " + getTableName(TBLNAME_LNG) + " ("
				+ LNGCOLN_CODE + String.format(" VARCHAR(%d) PRIMARY KEY,", LANGCODE_MAX)
				+ LNGCOLN_ROOT + " VARCHAR,"
				+ LNGCOLN_ENCODING + " VARCHAR,"
				+ LNGCOLN_PATHBLD + " VARCHAR"
				+ ")";
			Stm.execute(sTmp);
			
			// Create the documents table
			sTmp = "CREATE TABLE " + getTableName(TBLNAME_DOC) + " ("
				+ DOCCOLN_KEY + " INTEGER IDENTITY PRIMARY KEY,"
				+ DOCCOLN_SRCTYPE + " INTEGER,"
				+ DOCCOLN_STATUS + " VARCHAR,"
				+ DOCCOLN_SUBDIR + " VARCHAR,"
				+ DOCCOLN_FILENAME + " VARCHAR,"
				+ DOCCOLN_FSETTINGS + " VARCHAR,"
				+ DOCCOLN_ENCODING + " VARCHAR,"
				+ DOCCOLN_FILESET + " VARCHAR,"
				+ DOCCOLN_TARGETS + " VARCHAR"
				+ ")";
			Stm.execute(sTmp);
			
			// Create the Source table
			sTmp = "CREATE TABLE " + getTableName(TBLNAME_SRC) + " ("
				+ SRCCOLN_KEY + " INTEGER IDENTITY PRIMARY KEY,"
				+ SRCCOLN_DKEY + " INTEGER,"
				+ SRCCOLN_GKEY + " INTEGER,"
				+ SRCCOLN_XKEY + " INTEGER,"
				+ SRCCOLN_FLAG + " BOOLEAN,"
				+ SRCCOLN_RESNAME + " VARCHAR,"
				+ SRCCOLN_RESTYPE + " VARCHAR,"
				+ SRCCOLN_NOTRANS + " BOOLEAN,"
				+ SRCCOLN_STATUS + " INTEGER,"
				+ SRCCOLN_TEXT + " VARCHAR,"
				+ SRCCOLN_CODES + " VARCHAR,"
				+ SRCCOLN_COMMENT + " VARCHAR,"
				+ SRCCOLN_START + " INTEGER,"
				+ SRCCOLN_LENGTH + " INTEGER,"
				+ SRCCOLN_MAXWIDTH + " INTEGER,"
				+ SRCCOLN_PREVTEXT + " VARCHAR"
				+ ")";
			Stm.execute(sTmp);

			sTmp = "CREATE TABLE " + getTableName(TBLNAME_TRG) + " ("
				+ TRGCOLN_KEY + " INTEGER IDENTITY PRIMARY KEY,"
				+ TRGCOLN_LANG + String.format(" TEXT(%d),", LANGCODE_MAX)
				+ TRGCOLN_DKEY + " INTEGER,"
				+ TRGCOLN_SKEY + " INTEGER,"
				+ TRGCOLN_GKEY + " INTEGER,"
				+ TRGCOLN_XKEY + " INTEGER,"
				+ TRGCOLN_FLAG + " BOOLEAN,"
				+ TRGCOLN_STATUS + " INTEGER,"
				+ TRGCOLN_RESNAME + " VARCHAR,"
				+ TRGCOLN_RESTYPE + " VARCHAR,"
				+ TRGCOLN_STEXT + " VARCHAR,"
				+ TRGCOLN_TTEXT + " VARCHAR,"
				+ TRGCOLN_SCODES + " VARCHAR,"
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
	
	/**
	 * Opens a give project.
	 * @param p_sName Full path of one of the HSQL DB files. If the path has 
	 * no extension the path is assumed to be the folder where the DB is located,
	 * and the last folder name is appended to this path.
	 * For example: C:\Projects\myProject\myProject.script or C:\Projects\myProject
	 * will both open the same DB. 
	 */
	public void openProject (String p_sName)
		throws Exception
	{
		try
		{
			//TODO: Check if the project really exits (created by default otherwise)
			String[] aData = computeNameAndPath(p_sName);

			// Connect
			String sTmp = "jdbc:h2:" + aData[1];
			m_Conn = DriverManager.getConnection(sTmp, "sa", "");
			
			m_sName = aData[0];
			m_sPrefix = makePrefix(m_sName);
			// Storage path is set here, as the project folder
			m_sStorage = Utils.getDirectoryName(aData[1]);
			
			readSettings();
			
			m_bProjectOpened = true;
		}
		catch ( Exception E ) {
			throw E;
		}
	}
	
	public void closeProject ()
		throws SQLException
	{
		if ( m_Conn != null ) {
			Statement Stm = m_Conn.createStatement();
			Stm.execute("SHUTDOWN COMPACT");
			m_Conn.close();
			m_Conn = null;
			m_sName = null;
			m_sPrefix = null;
		}
		m_bProjectOpened = false;
	}
	
	public void deleteProject (String p_sName)
		throws SQLException, Exception
	{
		if (( m_sName != null ) && ( m_sName.equalsIgnoreCase(p_sName) )) {
			throw new Exception("The project is opened.");
		}
		// Else: Nothing to do when the dB is in memory
	}
	
	protected int getLastAutoGeneratedKey (Statement p_Stm)
		throws SQLException
	{
		int nLastValue = -1;
		ResultSet rsTmp = null;
		try
		{
			rsTmp = p_Stm.executeQuery("CALL IDENTITY()");
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
	
	/**
	 * Compute the project name and path from a flexible path/name.
	 * @param p_sName Path to transform.
	 * @return An array of strings. 0=the name, 1=the correct path
	 */
	private String[] computeNameAndPath (String p_sName)
	{
		String[] aResult = new String[2];
		
		// Make sure the end is not a separator
		if ( p_sName.endsWith(File.separator) ) {
			p_sName = p_sName.substring(0, p_sName.length()-1);
		}
		
		// Get the project name
		int n = p_sName.lastIndexOf(File.separator);
		if ( n > -1 ) aResult[0] = p_sName.substring(n+1);
		else aResult[0] = p_sName; 
		
		if ( Utils.getExtension(p_sName).equals("") ) {
			// It it's a path without filename, we need to add the filename
			// (which is the same as the last sub-folder)
			p_sName = p_sName + File.separator + aResult[0];
		}
		else { // If it's a path with filename, just remove the extension
			aResult[0] = Utils.removeExtension(aResult[0]);
			p_sName = Utils.removeExtension(p_sName);
			// H2 file names have double-extensions, remove both of them
			aResult[0] = Utils.removeExtension(aResult[0]);
			p_sName = Utils.removeExtension(p_sName);
		}
		
		aResult[1] = p_sName;
		return aResult;
	}
}
