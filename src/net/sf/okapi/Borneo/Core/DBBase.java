/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel (at ENLASO Corporation)                  */
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.eclipse.swt.widgets.TableItem;

import net.sf.okapi.Filter.FilterAccess;
import net.sf.okapi.Filter.IFilter;
import net.sf.okapi.Library.Base.DefaultFilenameFilter;
import net.sf.okapi.Library.Base.FieldsString;
import net.sf.okapi.Library.Base.FilterSettingsMarkers;
import net.sf.okapi.Library.Base.Utils;

public abstract class DBBase {

	public static final String    TBLNAME_INF         = "Info";
	public static final String    INFCOLN_DATA1       = "Data1";
	public static final int       INFCOLI_DATA1       = 1;
	public static final String    INFCOLN_VERSION     = "Version";
	public static final int       INFCOLI_VERSION     = 2;
	public static final String    INFCOLN_NAME        = "Name";
	public static final int       INFCOLI_NAME        = 3;
	public static final String    INFCOLN_STORAGE     = "Storage";
	public static final int       INFCOLI_STORAGE     = 4;
	public static final String    INFCOLN_INVARIANT   = "Invariant";
	public static final int       INFCOLI_INVARIANT   = 5;

	public static final String    TBLNAME_LNG         = "Langs";
	public static final String    LNGCOLN_CODE        = "Code";
	public static final int       LNGCOLI_CODE        = 1;
	public static final String    LNGCOLN_ROOT        = "Root";
	public static final int       LNGCOLI_ROOT        = 2;
	public static final String    LNGCOLN_ENCODING    = "Encoding";
	public static final int       LNGCOLI_ENCODING    = 3;
	public static final String    LNGCOLN_PATHBLD     = "PathBld";
	public static final int       LNGCOLI_PATHBLD     = 4;

	public static final String    TBLNAME_DOC         = "Docs";
	public static final String    DOCCOLN_KEY         = "PK";
	public static final int       DOCCOLI_KEY         = 1;
	public static final String    DOCCOLN_SRCTYPE     = "SrcType";
	public static final int       DOCCOLI_SRCTYPE     = 2;
	public static final String    DOCCOLN_STATUS      = "Status";
	public static final int       DOCCOLI_STATUS      = 3;
	public static final String    DOCCOLN_SUBDIR      = "Subdir";
	public static final int       DOCCOLI_SUBDIR      = 4;
	public static final String    DOCCOLN_FILENAME    = "Filename";
	public static final int       DOCCOLI_FILENAME    = 5;
	public static final String    DOCCOLN_FSETTINGS   = "FSettings";
	public static final int       DOCCOLI_FSETTINGS   = 6;
	public static final String    DOCCOLN_ENCODING    = "Encoding";
	public static final int       DOCCOLI_ENCODING    = 7;
	public static final String    DOCCOLN_FILESET     = "FileSet";
	public static final int       DOCCOLI_FILESET     = 8;
	public static final String    DOCCOLN_TARGETS     = "Targets";
	public static final int       DOCCOLI_TARGETS     = 9;

	public static final String    TBLNAME_SRC         = "Source";
	public static final String    SRCCOLN_KEY         = "PK";
	public static final int       SRCCOLI_KEY         = 1;
	public static final String    SRCCOLN_DKEY        = "DKey";
	public static final int       SRCCOLI_DKEY        = 2;
	public static final String    SRCCOLN_GKEY        = "GKey";
	public static final int       SRCCOLI_GKEY        = 3;
	public static final String    SRCCOLN_XKEY        = "XKey";
	public static final int       SRCCOLI_XKEY        = 4;
	public static final String    SRCCOLN_FLAG        = "Flag";
	public static final int       SRCCOLI_FLAG        = 5;
	public static final String    SRCCOLN_RESNAME     = "Resname";
	public static final int       SRCCOLI_RESNAME     = 6;
	public static final String    SRCCOLN_RESTYPE     = "Restype";
	public static final int       SRCCOLI_RESTYPE     = 7;
	public static final String    SRCCOLN_NOTRANS     = "NT";
	public static final int       SRCCOLI_NOTRANS     = 8;
	public static final String    SRCCOLN_STATUS      = "Status";
	public static final int       SRCCOLI_STATUS      = 9;
	public static final String    SRCCOLN_TEXT        = "Text";
	public static final int       SRCCOLI_TEXT        = 10;
	public static final String    SRCCOLN_CODES       = "Codes";
	public static final int       SRCCOLI_CODES       = 11;
	public static final String    SRCCOLN_COMMENT     = "Comment";
	public static final int       SRCCOLI_COMMENT     = 12;
	public static final String    SRCCOLN_START       = "Start";
	public static final int       SRCCOLI_START       = 13;
	public static final String    SRCCOLN_LENGTH      = "Length";
	public static final int       SRCCOLI_LENGTH      = 14;
	public static final String    SRCCOLN_MAXWIDTH    = "MaxWidth";
	public static final int       SRCCOLI_MAXWIDTH    = 15;
	public static final String    SRCCOLN_PREVTEXT    = "PrevText";
	public static final int       SRCCOLI_PREVTEXT    = 16;

	public static final String    TBLNAME_TRG         = "Target";
	public static final String    TRGCOLN_KEY         = "PK";
	public static final int       TRGCOLI_KEY         = 1;
	public static final String    TRGCOLN_LANG        = "Lang";
	public static final int       TRGCOLI_LANG        = 2;
	public static final String    TRGCOLN_DKEY        = "DKey";
	public static final int       TRGCOLI_DKEY        = 3;
	public static final String    TRGCOLN_SKEY        = "SKey";
	public static final int       TRGCOLI_SKEY        = 4;
	public static final String    TRGCOLN_GKEY        = "GKey";
	public static final int       TRGCOLI_GKEY        = 5;
	public static final String    TRGCOLN_XKEY        = "XKey";
	public static final int       TRGCOLI_XKEY        = 6;
	public static final String    TRGCOLN_FLAG        = "Flag";
	public static final int       TRGCOLI_FLAG        = 7;
	public static final String    TRGCOLN_STATUS      = "Status";
	public static final int       TRGCOLI_STATUS      = 8;
	public static final String    TRGCOLN_RESNAME     = "Resname";
	public static final int       TRGCOLI_RESNAME     = 9;
	public static final String    TRGCOLN_RESTYPE     = "Restype";
	public static final int       TRGCOLI_RESTYPE     = 10;
	public static final String    TRGCOLN_STEXT       = "SText";
	public static final int       TRGCOLI_STEXT       = 11;
	public static final String    TRGCOLN_TTEXT       = "TText";
	public static final int       TRGCOLI_TTEXT       = 12;
	public static final String    TRGCOLN_SCODES      = "SCodes";
	public static final int       TRGCOLI_SCODES      = 13;
	public static final String    TRGCOLN_TMP         = "Tmp";
	public static final int       TRGCOLI_TMP         = 14;

	public static final String    ORI_EXTENSION       = ".ori";
	public static final String    DIR_EXTDOCS         = "D1";
	public static final String    DIR_SRCDOCS         = "D2";
	public static final String    DIR_TRGDOCS         = "D3";
	public static final String    DIR_PARAMETERS      = "PF";
	public static final String    DIR_EXCHANGE        = "XF";

	public static final int       SRCTYPE_EXTERNAL    = 0;
	public static final int       SRCTYPE_INTERNAL    = 1;

	public static final int       SSTATUS_PENDING          = 0;
	public static final int       SSTATUS_NEW              = 1;
	public static final int       SSTATUS_GUESSED_MOD      = 2;
	public static final int       SSTATUS_SAME_MOD         = 3;
	public static final int       SSTATUS_GUESSED_SAME     = 4;
	public static final int       SSTATUS_SAME_SAME        = 5;
	public static final int       SSTATUS_DELETED          = 6;
	public static final int       SSTATUS_TOREMOVE         = 999;

	public static final int       TSTATUS_NOTRANS          = 0;
	public static final int       TSTATUS_UNUSED           = 1;
	public static final int       TSTATUS_TOTRANS          = 2;
	public static final int       TSTATUS_TOEDIT           = 3;
	public static final int       TSTATUS_TOREVIEW         = 4;
	public static final int       TSTATUS_OK               = 5;
	
	public static final int       LANGCODE_MAX             = 20;

	protected Connection     m_Conn = null;
	protected String         m_sDBTypeInfo = "";
	protected String         m_sStorage = "";
	protected boolean        m_bProjectOpened = false;
	protected String         m_sName = null;
	protected String         m_sPrefix = null;
	
	protected String         m_sSrcRoot;
	protected String         m_sSrcRootL;
	protected String         m_sSrcLang;
	protected String         m_sSrcEnc;
	protected boolean        m_bAutoSrcEnc;
	protected String         m_sParamFolder;
	protected String         m_sProjectID;
	private boolean          m_bBatchCanceled = false;
	
	protected void finalize ()
		throws Throwable
	{
	    try {
	    	logout();
	    } finally {
	        super.finalize();
	    }
	}
	
	public String escapeText (String p_sText) {
		if ( p_sText == null ) return "";
		StringBuilder sbTmp = new StringBuilder(p_sText.length());

		for ( int i=0; i<p_sText.length(); i++ ) {
			switch ( p_sText.charAt(i) ) {
				case '%':
				case '*':
				case '[':
				case ']':
					sbTmp.append("["+p_sText.charAt(i)+"]");
					break;
				case '\'':
					sbTmp.append(p_sText.charAt(i)+p_sText.charAt(i));
					break;
				default:
					sbTmp.append(p_sText.charAt(i));
					break;
			}
		}
		return sbTmp.toString();
	}
	
	public abstract void login (DBOptions p_Options)
		throws Exception;

	public void logout ()
		throws SQLException
	{
		if ( m_Conn != null )
		{
			if ( isProjectOpened() ) {
				closeProject();
			}
			if ( m_Conn != null ) { // Need to test again
				m_Conn.close();
				m_Conn = null;
			}
		}
		m_sDBTypeInfo = "";
		m_sStorage = "";
	}

	/**
	 * Gets the list of projects in the current database.
	 * @return An array of strings, where each string is a project name.
	 * Or null of list of projects are not supported.
	 */
	public abstract String[] getProjectList ();
	
	public abstract String[] getDBInfo ();
	
	public String getStorage () {
		return m_sStorage;
	}
	
	public String getProjectID () {
		return m_sProjectID;
	}
	
	public String getTableName (String p_sName) {
		return m_sPrefix + p_sName;
	}
	
	public boolean isProjectOpened () {
		return m_bProjectOpened;
	}
	
	public String getSourceRoot () {
		return m_sSrcRoot;
	}
	
	public void setSourceRoot (String p_sRoot)
	{
		m_sSrcRoot = p_sRoot;
		m_sSrcRootL = m_sSrcRoot.toLowerCase(); 
	}
	
	public String getSourceLanguage () {
		return m_sSrcLang;
	}
	
	public void setSourceLanguage (String p_sCode) {
		m_sSrcLang = p_sCode.toUpperCase();
	}
	
	public String getSourceEncoding () {
		return m_sSrcEnc;
	}
	
	public void setSourceEncoding (String p_sName) {
		m_sSrcEnc = p_sName;
	}
	
	public boolean getAutoSourceEncoding () {
		return m_bAutoSrcEnc;
	}
	
	public void setAutoSourceEncoding (boolean p_bValue) {
		m_bAutoSrcEnc = p_bValue;
	}
	
	public String getParametersFolder () {
		return m_sParamFolder;
	}
	
	public void setParametersFolder (String p_sFolder) {
		m_sParamFolder = p_sFolder;
	}
	
	public String getName () {
		return m_sName;
	}
	
	protected String makePrefix (String p_sValue) {
		return "";
	}
	
	/**
	 * Creates a new project.
	 * @param p_sName Name of the project.
	 * @param p_sPath Path information (DB-type specific)
	 * @param p_sSrcLang Language code of the source language.
	 * @throws Exception
	 */
	public abstract void createProject (String p_sName,
		String p_sPath,
		String p_sSrcLang,
		String p_sEncoding)
		throws Exception;

	/**
	 * Opens a project.
	 * @param p_sName Name of the project. The value depends on the DB type.
	 * Note that it is not necessarily the same as in createProject. 
	 * @throws Exception
	 */
	public abstract void openProject (String p_sName)
		throws Exception;
	
	public abstract void closeProject ()
		throws SQLException;
	
	public abstract void deleteProject (String p_sName)
		throws SQLException, Exception;
	
	public void startBatchMode ()
		throws SQLException
	{
		// Start the batch only if we are not yet in that mode
		if ( m_Conn.getAutoCommit() ) {
			m_Conn.setAutoCommit(false);
			m_bBatchCanceled = false;
		}
	}
	
	public void stopBatchMode (boolean p_bCommit)
		throws SQLException
	{
		try {
			// Don't do anything if not in batch mode
			if ( m_Conn.getAutoCommit() ) return;
			// Commit or roll-back
			if ( p_bCommit && !m_bBatchCanceled ) m_Conn.commit();
			else m_Conn.rollback();
		}
		finally {
			m_Conn.setAutoCommit(true);
			m_bBatchCanceled = false;
		}
	}
	
	public boolean isBatchCanceled () {
		return m_bBatchCanceled;
	}
	
	public void cancelBatch () {
		m_bBatchCanceled = true;
	}

	public DBTarget getTargetData (String p_sTrgLang)
		throws Exception
	{
		DBTarget DBT = null;
		Statement Stm = null;
		try {
			Stm = m_Conn.createStatement();
			String sTmp = String.format("SELECT * FROM %s", getTableName(TBLNAME_LNG));
			ResultSet rsTmp = Stm.executeQuery(sTmp);
			if ( rsTmp.next() ) {
				DBT = new DBTarget(rsTmp.getString(LNGCOLI_CODE));
				DBT.setEncoding(rsTmp.getString(LNGCOLI_ENCODING));
				DBT.setRoot(rsTmp.getString(LNGCOLI_ROOT));
				DBT.getDefaultPB().fromString(rsTmp.getString(LNGCOLI_PATHBLD));
			}
		}
		catch ( Exception E ) {
			throw E;
		}
		finally {
			if ( Stm != null ) Stm.close();
		}
		return DBT;
	}
	
	public void addTargetLanguage (String p_sTrgLang)
		throws Exception
	{
		boolean bWasAutoCommit = false;
		Statement Stm = null;
		PreparedStatement PStm = null;
		try {
			bWasAutoCommit = m_Conn.getAutoCommit();
			if ( bWasAutoCommit ) m_Conn.setAutoCommit(false);

			// Check if the language is already in the project
			Stm = m_Conn.createStatement();
			ResultSet rsTmp = Stm.executeQuery(String.format("SELECT %s FROM %s WHERE %s='%s'",
				LNGCOLN_CODE, getTableName(TBLNAME_LNG), LNGCOLN_CODE, p_sTrgLang));
			if ( rsTmp.next() ) return; // Exist already
			
			DBTarget DBT = new DBTarget(p_sTrgLang);
			String sTmp = String.format("INSERT INTO %s (%s,%s) VALUES('%s','%s');",
				getTableName(TBLNAME_LNG), LNGCOLN_CODE, LNGCOLN_ENCODING,
				p_sTrgLang, DBT.getEncoding());
			Stm.execute(sTmp);
			
			// Add the target info entry for each input file
			sTmp = String.format("SELECT %s, %s FROM %s", DOCCOLN_KEY,
				DOCCOLN_TARGETS, getTableName(TBLNAME_DOC));
			ResultSet rsDocs = Stm.executeQuery(sTmp);
			Hashtable<String, DBTargetDoc> aTargets;
			Hashtable<Integer, String> aData = new Hashtable<Integer, String>();
			// For each document: get the targets info, add the new target,
			// add an entry into the aData list with the DKey and the new targets data
			while ( rsDocs.next() ) {
				aTargets = DBTargetDoc.convertFromString(rsDocs.getString(2));
				aTargets.put(p_sTrgLang, new DBTargetDoc());
				aData.put(rsDocs.getInt(1), DBTargetDoc.convertToString(aTargets));
			}
			// Now save the aData list back to the database
			sTmp = String.format("UPDATE %s SET %s=? WHERE %s=?",
				getTableName(TBLNAME_DOC), DOCCOLN_TARGETS, DOCCOLN_KEY);
			PStm = m_Conn.prepareStatement(sTmp);
			Enumeration<Integer> E = aData.keys();
			while ( E.hasMoreElements() ) {
				int nDKey = E.nextElement();
				PStm.setString(1, aData.get(nDKey));
				PStm.setInt(2, nDKey);
				PStm.executeUpdate();
			}

			// Commit everything
			if ( bWasAutoCommit ) m_Conn.commit();
		}
		catch ( Exception E ) {
			if ( bWasAutoCommit ) m_Conn.rollback();
			throw E;
		}
		finally {
			if ( bWasAutoCommit ) m_Conn.setAutoCommit(true);
			if ( Stm != null ) Stm.close();
		}
	}
	
	public void removeTargetLanguage (String p_sTrgLang)
		throws SQLException, Exception
	{
		boolean bWasAutoCommit = false;
		Statement Stm = null;
		PreparedStatement PStm = null;
		try {
			bWasAutoCommit = m_Conn.getAutoCommit();
			if ( bWasAutoCommit ) m_Conn.setAutoCommit(false);
			Stm = m_Conn.createStatement();
			
			// Remove the entry in the Langs table
			String sTmp = String.format("DELETE FROM %s WHERE %s='%s'",
				getTableName(TBLNAME_LNG), LNGCOLN_CODE, p_sTrgLang);
			Stm.execute(sTmp);

			// Remove the target entries from the target table
			sTmp = String.format("DELETE FROM %s WHERE %s='%s'",
				getTableName(TBLNAME_TRG), TRGCOLN_LANG, p_sTrgLang);
			Stm.execute(sTmp);
			
			// Remove the entry in each input document
			sTmp = String.format("SELECT %s, %s FROM %s", DOCCOLN_KEY,
				DOCCOLN_TARGETS, getTableName(TBLNAME_DOC));
			ResultSet rsDocs = Stm.executeQuery(sTmp);
			Hashtable<String, DBTargetDoc> aTargets;
			Hashtable<Integer, String> aData = new Hashtable<Integer, String>();
			// For each document: get the targets info, remove the target,
			// add an entry into the aData list with the DKey and the updated targets data
			while ( rsDocs.next() ) {
				aTargets = DBTargetDoc.convertFromString(rsDocs.getString(2));
				aTargets.remove(p_sTrgLang);
				aData.put(rsDocs.getInt(1), DBTargetDoc.convertToString(aTargets));
			}
			// Now save the aData list back to the database
			sTmp = String.format("UPDATE %s SET %s=? WHERE %s=?",
				getTableName(TBLNAME_DOC), DOCCOLN_TARGETS, DOCCOLN_KEY);
			PStm = m_Conn.prepareStatement(sTmp);
			Enumeration<Integer> E = aData.keys();
			while ( E.hasMoreElements() ) {
				int nDKey = E.nextElement();
				PStm.setString(1, aData.get(nDKey));
				PStm.setInt(2, nDKey);
				PStm.executeUpdate();
			}

			// Remove the entries for the language in the Target table
			sTmp = String.format("DELETE FROM %s WHERE %s='%s'",
				getTableName(TBLNAME_TRG), TRGCOLN_LANG, p_sTrgLang);
				Stm.execute(sTmp);

			// Commit everything
			if ( bWasAutoCommit ) m_Conn.commit();
		}
		catch ( Exception E ) {
			if ( bWasAutoCommit ) m_Conn.rollback();
			throw E;
		}
		finally {
			if ( bWasAutoCommit ) m_Conn.setAutoCommit(true);
			if ( Stm != null ) Stm.close();
		}
	}

	protected abstract int getLastAutoGeneratedKey(Statement p_Stm)
		throws SQLException;
	
	/**
	 * Adds a document to the source list.
	 * @param p_sFullPath Full path of the document to add.
	 * @param p_nSrcType Type of source.
	 * @param p_sEncoding Default sourceEncoding for the source document (can be null or empty).
	 * @param p_sFSettings Filter settings for the source document (can be null or empty).
	 * @return A long that is the Document key, or -1 if an error occurs.
	 * @throws Exception
	 */
	public int addDocument (String p_sFullPath,
		int p_nSrcType,
		String p_sEncoding,
		String p_sFSettings)
		throws Exception
	{
		int             nDKey = -1;
		Statement       Stm = null;
		boolean         bWasAutoCommit = false; 
		
		try {
			// Check and remove the root
			if ( p_sFullPath.toLowerCase().indexOf(m_sSrcRootL+File.separatorChar) != 0 ) {
				throw new Exception("Current source root and document are not compatible.");
			}
			// Get the relative path
			String sRelPath = p_sFullPath.substring(m_sSrcRootL.length()+1);
		
			//TODO: Check if the document is already in the project
		
			// Split into filename and relative directory
			File F = new File(sRelPath);
			String sFilename = F.getName();
			String sSubdir = F.getParent();
			if (( sSubdir != null ) && ( sSubdir.endsWith(File.separator) ))
				sSubdir = sSubdir.substring(0, sSubdir.length());

			bWasAutoCommit = m_Conn.getAutoCommit();
			if ( bWasAutoCommit ) m_Conn.setAutoCommit(false);
			Stm = m_Conn.createStatement();

			// Add entries for each target locale in the target document list
			Vector<String> aLangs = fetchLanguageList();
			Hashtable<String, DBTargetDoc> aTargets = new Hashtable<String, DBTargetDoc>();
			for ( int i=0; i<aLangs.size(); i++ ) {
				aTargets.put(aLangs.get(i), new DBTargetDoc());
			}
		
			// Create the new entry in the Files table
			String sTmp = String.format("INSERT INTO %s (%s,%s,%s,%s,%s,%s,%s) VALUES(%s,'%s','%s','%s','%s','%s','%s')",
				getTableName(TBLNAME_DOC), DOCCOLN_SRCTYPE, DOCCOLN_STATUS,
				DOCCOLN_SUBDIR, DOCCOLN_FILENAME, DOCCOLN_ENCODING,
				DOCCOLN_FSETTINGS, DOCCOLN_TARGETS, p_nSrcType,
				null, escapeText(sSubdir), escapeText(sFilename),
				(p_sEncoding==null) ? "" : p_sEncoding,
				escapeText(p_sFSettings),
				DBTargetDoc.convertToString(aTargets));
			Stm.executeUpdate(sTmp);
			nDKey = getLastAutoGeneratedKey(Stm);
			
			// Commit everything
			if ( bWasAutoCommit ) m_Conn.commit();
		}
		catch ( Exception E ) {
			nDKey = -1;
			if ( bWasAutoCommit ) m_Conn.rollback();
			throw E;
		}
		finally {
			if ( bWasAutoCommit ) m_Conn.setAutoCommit(true);
			if ( Stm != null ) Stm.close();
		}
		return nDKey;
	}
	
	/**
	 * Removes a give document (or all documents) from a project.
	 * @param p_nDKey The DKey of the document to remove.
	 * Or, use -1 to remove all documents.
	 * @throws Exception
	 */
	public void removeDocument (int p_nDKey)
		throws Exception
	{
		boolean bWasAutoCommit = false;
		Statement Stm = null;
		try {
			bWasAutoCommit = m_Conn.getAutoCommit();
			if ( bWasAutoCommit ) m_Conn.setAutoCommit(false);
			Stm = m_Conn.createStatement();
			
			// Remove the entry in the Files table
			if ( p_nDKey == -1 )
				Stm.executeUpdate("DELETE FROM " + getTableName(TBLNAME_DOC));
			else
				Stm.executeUpdate(String.format("DELETE FROM %s WHERE %s=%d",
					getTableName(TBLNAME_DOC), DOCCOLN_KEY, p_nDKey));

			// Remove the entries in the Source table
			if ( p_nDKey == -1 )
				Stm.executeUpdate("DELETE FROM " + getTableName(TBLNAME_SRC));
			else
				Stm.executeUpdate(String.format("DELETE FROM %s WHERE %s=%d",
					getTableName(TBLNAME_SRC), SRCCOLN_DKEY, p_nDKey));
			
			// Remove the data from the Target tables
			Vector<String> aLangs = fetchLanguageList();
			String sTmp;
			for ( int i=0; i<aLangs.size(); i++ ) {
				if ( p_nDKey == -1 ) {
					sTmp = String.format("DELETE FROM %s WHERE %s='%s'",
						getTableName(TBLNAME_TRG), TRGCOLN_LANG, aLangs.get(i));
				}
				else {
					sTmp = String.format("DELETE FROM %s WHERE (%s='%s' AND %s=%d)",
						getTableName(TBLNAME_TRG), TRGCOLN_LANG, aLangs.get(i),
						TRGCOLN_DKEY, p_nDKey);
				}
				Stm.executeUpdate(sTmp);
			}

			//TODO: Remove the copies of the document from the project work folders
/*			try
			{
				DeleteOriginalData(p_lDKey, DBBase.DIR_EXTDOCS);
				DeleteOriginalData(p_lDKey, DBBase.DIR_SRCDOCS);
				DeleteOriginalData(p_lDKey, DBBase.DIR_TRGDOCS);
			}
			catch ( Exception E )
			{
				// Just log the error as warning
				m_Log.Warning(E.Message);
			}*/

			// Commit everything
			if ( bWasAutoCommit ) m_Conn.commit();
		}
		catch ( Exception E ) {
			if ( bWasAutoCommit ) m_Conn.rollback();
			throw E;
		}
		finally {
			if ( bWasAutoCommit ) m_Conn.setAutoCommit(true);
			if ( Stm != null ) Stm.close();
		}
	}
	
	public Vector<String> fetchLanguageList ()
		throws SQLException
	{
		Vector<String> aRes = new Vector<String>();
		Statement Stm = null;
		try {
			Stm = m_Conn.createStatement();
			ResultSet rsTmp = Stm.executeQuery(String.format("SELECT %s FROM %s",
				LNGCOLN_CODE, getTableName(TBLNAME_LNG)));
			while ( rsTmp.next() ) {
				aRes.add(rsTmp.getString(1));
			}
		}
		finally {
			if ( Stm != null ) {
				Stm.close();
			}
		}
		return aRes;
	}

	public ResultSet fetchDocuments (boolean p_bScrollable)
		throws SQLException
	{
		Statement Stm;
		if ( p_bScrollable )
			Stm = m_Conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		else
			Stm = m_Conn.createStatement();
	
		return Stm.executeQuery(String.format("SELECT * FROM %s ORDER BY %s",
			getTableName(TBLNAME_DOC), DOCCOLN_KEY));
	}

	public ResultSet fetchSourceItems (int p_nDKey,
		boolean p_bPendingItems)
		throws SQLException
	{
		Statement Stm = m_Conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			ResultSet.CONCUR_READ_ONLY);
		String sTmp;
		if ( p_nDKey == -2 ) {
			sTmp = String.format("SELECT * FROM %s WHERE %s%s%d",
				getTableName(TBLNAME_SRC), SRCCOLN_STATUS,
				(p_bPendingItems ? "=" : "<>"), SSTATUS_PENDING);
		}
		else {
			sTmp = String.format("SELECT * FROM %s WHERE (%s=%d AND %s%s%d)",
				getTableName(TBLNAME_SRC), SRCCOLN_DKEY, p_nDKey,
				SRCCOLN_STATUS, (p_bPendingItems ? "=" : "<>"), SSTATUS_PENDING);
		}
		return Stm.executeQuery(sTmp + "ORDER BY " + SRCCOLN_KEY);
	}

	public ResultSet fetchTargetItems (int p_nDKey,
		String p_sLangCode)
		throws SQLException
	{
		Statement Stm = m_Conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			ResultSet.CONCUR_READ_ONLY);
		if ( p_nDKey == -2 ) {
			return Stm.executeQuery(String.format("SELECT * FROM %s WHERE %s='%s'",
				getTableName(TBLNAME_TRG), TRGCOLN_LANG, p_sLangCode));
		}
		else {
			return Stm.executeQuery(String.format("SELECT * FROM %s WHERE (%s=%d AND %s='%s')",
				getTableName(TBLNAME_TRG), TRGCOLN_DKEY, p_nDKey,
				TRGCOLN_LANG, p_sLangCode));
		}
	}

	public void saveDocuments (TableItem[] p_aItems)
		throws Exception
	{
		boolean bWasAutoCommit = false;
		PreparedStatement PStm = null;
		try {
			bWasAutoCommit = m_Conn.getAutoCommit();
			if ( bWasAutoCommit ) m_Conn.setAutoCommit(false);
			
			String sTmp = String.format("UPDATE %s SET %s=?,%s=?,%s=?,%s=?,%s=?,%s=?,%s=?,%s=? WHERE %s=?",
				getTableName(TBLNAME_DOC), DOCCOLN_SRCTYPE, DOCCOLN_STATUS,
				DOCCOLN_SUBDIR, DOCCOLN_FILENAME, DOCCOLN_FSETTINGS,
				DOCCOLN_ENCODING, DOCCOLN_FILESET, DOCCOLN_TARGETS,
				DOCCOLN_KEY);
			PStm = m_Conn.prepareStatement(sTmp);
			for ( int i=0; i<p_aItems.length; i++ ) {
				// Note: DB index is 1-based, table index is 0-based
				PStm.setString(1, p_aItems[i].getText(DOCCOLI_SRCTYPE-1));
				PStm.setString(2, p_aItems[i].getText(DOCCOLI_STATUS-1));
				PStm.setString(3, p_aItems[i].getText(DOCCOLI_SUBDIR-1));
				PStm.setString(4, p_aItems[i].getText(DOCCOLI_FILENAME-1));
				PStm.setString(5, p_aItems[i].getText(DOCCOLI_FSETTINGS-1));
				PStm.setString(6, p_aItems[i].getText(DOCCOLI_ENCODING-1));
				PStm.setString(7, p_aItems[i].getText(DOCCOLI_FILESET-1));
				PStm.setString(8, p_aItems[i].getText(DOCCOLI_TARGETS-1));
				PStm.setInt(9, Integer.valueOf(p_aItems[i].getText(DOCCOLI_KEY-1)));
				PStm.executeUpdate();
			}
			// Commit everything if not in batch mode
			if ( bWasAutoCommit ) m_Conn.commit();
		}
		catch ( Exception E ) {
			if ( bWasAutoCommit ) m_Conn.rollback();
			throw E;
		}
		finally {
			if ( bWasAutoCommit ) m_Conn.setAutoCommit(true);
			if ( PStm != null ) {
				PStm.close();
				PStm = null;
			}
		}
	}

	public void readSettings ()
		throws SQLException
	{
		Statement Stm = null;
		try {
			Stm = m_Conn.createStatement();
			ResultSet rsTmp = Stm.executeQuery("SELECT * FROM " + getTableName(TBLNAME_INF));
			if ( !rsTmp.next() ) return;
			
			FieldsString FS = new FieldsString(rsTmp.getString(INFCOLI_DATA1));
			setSourceLanguage(FS.get("srclang", Utils.getDefaultSourceLanguage()));
			setSourceEncoding(FS.get("srcenc", "utf-8"));
			setAutoSourceEncoding(FS.get("autosrcenc", true));
			setParametersFolder(FS.get("paramdir", null));
			setSourceRoot(FS.get("srcroot", System.getProperty("user.home")));
			m_sProjectID = FS.get("prjid", "");
			
			// Get the storage location.
			// If null, the path should have been set when opening/creating.
			String sTmp = rsTmp.getString(INFCOLI_STORAGE);
			
			if ( sTmp != null ) m_sStorage = sTmp; 
		}
		finally {
			if ( Stm != null ) {
				Stm.close();
				Stm = null;
			}
		}
	}

	public void saveSettings ()
		throws Exception
	{
		boolean bWasAutoCommit = false;
		Statement Stm = null;
		try {
			bWasAutoCommit = m_Conn.getAutoCommit();
			if ( bWasAutoCommit ) m_Conn.setAutoCommit(false);

			FieldsString FS = new FieldsString();
			FS.add("srclang", m_sSrcLang);
			FS.add("srcenc", m_sSrcEnc);
			FS.add("autosrcenc", m_bAutoSrcEnc);
			FS.add("paramdir", m_sParamFolder);
			FS.add("srcroot", m_sSrcRoot);
			FS.add("prjid", m_sProjectID);

			Stm = m_Conn.createStatement();
			String sTmp = String.format("UPDATE %s SET %s='%s' WHERE %s=1",
				getTableName(TBLNAME_INF), DBBase.INFCOLN_DATA1, escapeText(FS.toString()),
				DBBase.INFCOLN_INVARIANT);
			Stm.execute(sTmp);

			// Commit everything if not in batch mode
			if ( bWasAutoCommit ) m_Conn.commit();
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

	public void doCommand (String p_sCommand)
		throws Exception
	{
		boolean bWasAutoCommit = false;
		Statement Stm = null;
		try {
			bWasAutoCommit = m_Conn.getAutoCommit();
			if ( bWasAutoCommit ) m_Conn.setAutoCommit(false);
			
			Stm = m_Conn.createStatement();
			Stm.execute(p_sCommand);
			// Commit everything if not in batch mode
			if ( bWasAutoCommit ) m_Conn.commit();
		}
		catch ( Exception E ) {
			if ( bWasAutoCommit ) m_Conn.rollback();
			throw E;
		}
		finally {
			if ( bWasAutoCommit ) m_Conn.setAutoCommit(true);
			if ( Stm != null ) Stm.close();
		}
	}

	public DBDoc getSourceDocumentData (int p_nDKey,
		String p_sTrgLang)
		throws SQLException, Exception
	{
		Statement Stm = null;
		try {
			String sTmp = String.format("SELECT * FROM %s WHERE %s=%d",
				getTableName(TBLNAME_DOC), DOCCOLN_KEY, p_nDKey);
			Stm = m_Conn.createStatement();
			ResultSet rsTmp = Stm.executeQuery(sTmp);
			if ( !rsTmp.next() ) return null;
			
			DBTarget Trg = null;
			if ( p_sTrgLang != null ) Trg = getTargetData(p_sTrgLang);
			
			DBDoc Doc = new DBDoc(rsTmp, getSourceRoot(), getSourceEncoding(),
				p_sTrgLang, Trg);
			return Doc;
		}
		finally {
			if ( Stm != null ) Stm.close();
		}
	}

	public PreparedStatement prepareStatement (String p_sCommand)
		throws SQLException
	{
		return m_Conn.prepareStatement(p_sCommand);
	}

	public Statement createStatement (int p_nType,
		int p_nConcurrency)
		throws SQLException
	{
		return m_Conn.createStatement(p_nType, p_nConcurrency);
	}
	
	public void updateSourceDocumentStatus (DBDoc p_Doc)
		throws Exception
	{
		String sTmp = String.format("UPDATE %s SET %s='%s' WHERE %s=%d",
			getTableName(TBLNAME_DOC), DOCCOLN_STATUS, escapeText(p_Doc.getStatus()),
			DOCCOLN_KEY, p_Doc.getKey());
		doCommand(sTmp);
	}
	
	public void updateTargetDocumentStatus (DBDoc p_Doc)
		throws Exception
	{
		String sData = DBTargetDoc.convertToString(p_Doc.getTargets());
		String sTmp = String.format("UPDATE %s SET %s='%s' WHERE %s=%d",
			getTableName(TBLNAME_DOC), DOCCOLN_TARGETS, escapeText(sData),
			DOCCOLN_KEY, p_Doc.getKey());
		doCommand(sTmp);
	}

	public void updateTargetData (String p_sLanguage,
		DBTarget p_Data)
		throws Exception
	{
		boolean bWasAutoCommit = false;
		PreparedStatement PStm = null;
		try {
			bWasAutoCommit = m_Conn.getAutoCommit();
			if ( bWasAutoCommit ) m_Conn.setAutoCommit(false);
			
			String sTmp = String.format("UPDATE %s SET %s=?,%s=?,%s=? WHERE %s=?",
				getTableName(TBLNAME_LNG), LNGCOLN_ENCODING, LNGCOLN_ROOT,
				LNGCOLN_PATHBLD, LNGCOLN_CODE);
			PStm = m_Conn.prepareStatement(sTmp);
			PStm.setString(1, p_Data.getEncoding());
			PStm.setString(2, p_Data.getRoot());
			PStm.setString(3, p_Data.getDefaultPB().toString());
			PStm.setString(4, p_sLanguage);
			PStm.executeUpdate();
			// Commit everything if not in batch mode
			if ( bWasAutoCommit ) m_Conn.commit();
		}
		catch ( Exception E ) {
			if ( bWasAutoCommit ) m_Conn.rollback();
			throw E;
		}
		finally {
			if ( bWasAutoCommit ) m_Conn.setAutoCommit(true);
			if ( PStm != null ) {
				PStm.close();
				PStm = null;
			}
		}
	}

	public void deleteOriginalData(int p_nDKey,
		String p_sDirectory)
	{
		String sKeyDot = String.format("%d.", p_nDKey);
		String sFrom = getStorage() + File.separator + p_sDirectory;
		File Origin = new File(sFrom);
		String[] aNames = Origin.list(new DefaultFilenameFilter(sKeyDot, null));
		if ( aNames != null ) {
			for ( String sName : aNames ) {
				File F = new File(sFrom + File.separator + sName);
				F.delete();
			}
		}
	}
	
	public void createOriginalDataStorage (DBDoc p_Doc,
		IFilter p_Filter)
		throws Exception
	{
		// Remove any document data from the extraction folder
		deleteOriginalData(p_Doc.getKey(), DBBase.DIR_EXTDOCS);

		// Copy the document itself to the extraction folder
		String sKeyDot = String.format("%d.", p_Doc.getKey()); 
		String sCommon = getStorage() + File.separator + DBBase.DIR_EXTDOCS + File.separator + sKeyDot;
		String sCopyPath = sCommon + p_Doc.getEncoding(getSourceEncoding()) + DBBase.ORI_EXTENSION;
		Utils.copyFile(p_Doc.getFullPath(), sCopyPath, false);

		// Make sure the result file is not read-only
		//File F = new File(sCopyPath);
		//TODO: Fix this for JRE 1.5 if ( !F.canWrite() ) F..setWritable(true);

		// Nothing more to do if no filter is provided
		if ( p_Filter == null ) return;

		// Create a local parameters file (even if it's the default parameters)
		// Make a unique name from the filter Id an dthe parameters path
		String sTmp = p_Filter.getParameters().getPath();
		if ( sTmp == null ) sTmp = p_Filter.getIdentifier() + sTmp; 
		else sTmp = p_Filter.getIdentifier();
		// Reduce it to a short unique string
		sTmp = Utils.makeID(sTmp);
		// Create filter settings from the filter ID and the unique name
		String sFSettings = FilterAccess.buildFilterSettingsType1(p_Filter.getIdentifier(), sTmp);
		sCopyPath = sCommon + sFSettings + FilterSettingsMarkers.PARAMETERS_FILEEXT;
		p_Filter.getParameters().save(sCopyPath, sKeyDot);
	}

	/**
	 * Copies the original file and settings information from one storage
	 * location to another.
	 * @param p_nDKey Key of the document information to copy.
	 * @param p_sFromFolder Storage where to copy from.
	 * @param p_sToFolder Storage where to copy to.
	 * @param p_sToTarget Target language code, or null for source data.
	 * @param p_bMove True if the data need to be moved rather than just copied.
	 * @throws Exception
	 */
	public void copyOriginalData (int p_nDKey,
		String p_sFromFolder,
		String p_sToFolder,
		String p_sToTarget,
		boolean p_bMove)
		throws Exception
	{
		// Adjust storage destination
		if ( p_sToTarget != null )
			p_sToFolder = p_sToFolder + File.separator + p_sToTarget;
		
		// Remove existing data in the destination
		deleteOriginalData(p_nDKey, p_sToFolder);

		String sKeyDot = String.format("%d.", p_nDKey);
		String sFrom = getStorage() + File.separator + p_sFromFolder;
		String sDest = getStorage() + File.separator + p_sToFolder; 
		File Origin = new File(sFrom);
		String[] aNames = Origin.list(new DefaultFilenameFilter(sKeyDot, null));
		if ( aNames != null ) {
			for ( String sName : aNames ) {
				String sFromPath = sFrom + File.separator + sName; 
				String sToPath = sDest + File.separator + sName;
				Utils.copyFile(sFromPath, sToPath, p_bMove);
			}
		}
	}

	/**
	 * Gets stored file and information for a given document.
	 * @param p_nDKey The key of the document to look for.
	 * @param p_sFolder The storage sub-folder where to look.
	 * @param p_sTarget target language code, or null for source data. 
	 * @return An array of three strings: 0=path of the original file,
	 * 1=filter settings, 2=sourceEncoding.
	 */
	public String[] getOriginalData (int p_nDKey,
		String p_sFolder,
		String p_sTarget)
	{
		String[] aRes = new String[3];

		// Adjust for target
		if ( p_sTarget != null )
			p_sFolder = p_sFolder + File.separator + p_sTarget;
		
		// Look for the parameters file in the storage
		String sKeyDot = String.format("%d.", p_nDKey);
		String sFrom = getStorage() + File.separator + p_sFolder;
		File Origin = new File(sFrom);

		String[] aNames = Origin.list(new DefaultFilenameFilter(sKeyDot, null));
		if ( aNames != null ) {
			for ( String sName : aNames ) {
				if ( Utils.getExtension(sName).equals(FilterSettingsMarkers.PARAMETERS_FILEEXT) ) {
					aRes[1] = sFrom + File.separator + sName; // Settings
				}
				else if ( Utils.getExtension(sName).equals(DBBase.ORI_EXTENSION) ) {
					aRes[0] = sFrom + File.separator + sName;
					aRes[2] = Utils.getFilename(aRes[0], false);
					int n = aRes[2].indexOf(".");
					if ( n > -1 ) aRes[2] = aRes[2].substring(n+1);
				}
			}
		}
		return aRes;
	}
	
	public void copyDocument (String p_sFromFullPath,
		String p_sToFullPath)
		throws Exception
	{
		File F = new File(p_sToFullPath);
		if ( F.exists() ) F.delete();
		else Utils.createDirectories(p_sToFullPath);
		Utils.copyFile(p_sFromFullPath, p_sToFullPath, false);
	}

	
}
