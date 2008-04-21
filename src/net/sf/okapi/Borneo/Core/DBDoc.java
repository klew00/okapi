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
import java.util.Hashtable;

import net.sf.okapi.Application.Borneo.DocumentsModel;

import org.eclipse.swt.widgets.TableItem;

public class DBDoc {
	private int               m_nKey;
	private int               m_nSrcType;
	private String            m_sFullPath;
	private String            m_sRelativePath;
	private String            m_sFSettings;
	private String            m_sEncoding;
	private String            m_sStatus;
	private String            m_sSet;
	private DBTargetDoc       m_TrgDoc;
	private Hashtable<String, DBTargetDoc> m_Targets;

	public static String makeRelativePath (String p_sSubdir,
		String p_sFilename)
	{
		if ( p_sSubdir.length() == 0 ) return p_sFilename;
		else return p_sSubdir + File.separatorChar + p_sFilename;
	}

	public static String makeFullPath (String p_sRoot,
		String p_sSubdir,
		String p_sFilename)
	{
		String sTmp = p_sRoot + File.separatorChar;
		if ( p_sSubdir.length() == 0 ) return sTmp + p_sFilename;
		return sTmp + p_sSubdir + File.separatorChar + p_sFilename;
	}


	public DBDoc (TableItem p_TI,
		String p_sRoot,
		String p_sDefaultEncoding,
		String p_sTrgLang,
		DBTarget p_Trg)
	{
		set(p_TI, p_sRoot, p_sDefaultEncoding, p_sTrgLang, p_Trg);
	}

	public DBDoc (ResultSet p_RS,
		String p_sRoot,
		String p_sDefaultEncoding,
		String p_sTrgLang,
		DBTarget p_Trg)
	{
		set(p_RS, p_sRoot, p_sDefaultEncoding, p_sTrgLang, p_Trg);
	}

	//TODO: Move this. It should not be defined here, it's a UI-specific method
	public void set (TableItem p_TI,
		String p_sRoot,
		String p_sDefaultEncoding,
		String p_sTrgLang,
		DBTarget p_Trg)
	{
		m_nKey = Integer.valueOf(p_TI.getText(DocumentsModel.COL_KEY));
		m_nSrcType = Integer.valueOf(p_TI.getText(DocumentsModel.COL_SRCTYPE));
		m_sFSettings = p_TI.getText(DocumentsModel.COL_FSETTINGS);
		String sSubdir = p_TI.getText(DocumentsModel.COL_SUBDIR);
		String sFilename = p_TI.getText(DocumentsModel.COL_FILENAME);
		m_sFullPath = makeFullPath(p_sRoot, sSubdir, sFilename);
		m_sRelativePath = makeRelativePath(sSubdir, sFilename);
		m_sStatus = p_TI.getText(DocumentsModel.COL_STATUS);
		m_sSet = p_TI.getText(DocumentsModel.COL_FILESET);

		m_sEncoding = p_TI.getText(DocumentsModel.COL_ENCODING);
		if (( m_sEncoding == null ) || ( m_sEncoding.length() == 0 ))
			m_sEncoding = p_sDefaultEncoding;

		if ( p_sTrgLang == null ) {
			m_TrgDoc = null;
			m_Targets = null;
		}
		else { // Read the target document properties
			m_Targets = DBTargetDoc.convertFromString(p_TI.getText(DocumentsModel.COL_TARGETS));
			m_TrgDoc = m_Targets.get(p_sTrgLang);
			// Adjust the defaults to use the target-level data
			if ( m_TrgDoc.getEncoding() == null )
				m_TrgDoc.setEncoding(p_Trg.getEncoding());
		}
	}

	public void set (ResultSet p_RS,
		String p_sRoot,
		String p_sDefaultEncoding,
		String p_sTrgLang,
		DBTarget p_Trg)
	{
		try {
			m_nKey = p_RS.getInt(DBBase.DOCCOLI_KEY);
			m_nSrcType = p_RS.getInt(DBBase.DOCCOLI_SRCTYPE);
			m_sFSettings = p_RS.getString(DBBase.DOCCOLI_FSETTINGS);
			String sSubdir = p_RS.getString(DBBase.DOCCOLI_SUBDIR);
			String sFilename = p_RS.getString(DBBase.DOCCOLI_FILENAME);
			m_sFullPath = makeFullPath(p_sRoot, sSubdir, sFilename);
			m_sRelativePath = makeRelativePath(sSubdir, sFilename);
			m_sStatus = p_RS.getString(DBBase.DOCCOLI_STATUS);
			m_sSet = p_RS.getString(DBBase.DOCCOLI_FILESET);
	
			m_sEncoding = p_RS.getString(DBBase.DOCCOLI_ENCODING);
			if (( m_sEncoding == null ) || ( m_sEncoding.length() == 0 ))
				m_sEncoding = p_sDefaultEncoding;
	
			if ( p_sTrgLang == null ) {
				m_TrgDoc = null;
				m_Targets = null;
			}
			else { // Read the target document properties
				m_Targets = DBTargetDoc.convertFromString(p_RS.getString(DBBase.DOCCOLI_TARGETS));
				m_TrgDoc = m_Targets.get(p_sTrgLang);
				// Adjust the defaults to use the target-level data
				if ( m_TrgDoc.getEncoding() == null )
					m_TrgDoc.setEncoding(p_Trg.getEncoding());
			}
		}
		catch ( Exception E ) {
			// Swallow that one.
		}
	}

	public int getKey () {
		return m_nKey;
	}

	public Hashtable<String, DBTargetDoc> getTargets () {
		return m_Targets;
	}

	public DBTargetDoc getTargetDoc () {
		return m_TrgDoc;
	}
	
	public void setTargetDoc (DBTargetDoc p_Value) {
		m_TrgDoc = p_Value;
	}

	public String getFullPath () {
		return m_sFullPath;
	}

	public String getRelativePath () {
		return m_sRelativePath;
	}

	public String getStatus () {
		return m_sStatus;
	}
	
	public void setStatus (String p_sValue) {
		m_sStatus = p_sValue;
	}

	public String getEncoding (String p_sDefautEncoding) {
		if (( m_sEncoding == null ) || ( m_sEncoding.length() == 0 ))
			return p_sDefautEncoding;
		return m_sEncoding;
	}

	public String getFSettings () {
		return m_sFSettings;
	}

	public boolean isSourceTypeInternal () {
		return m_nSrcType == DBBase.SRCTYPE_INTERNAL;
	}

	public String getFileSet () {
		return m_sSet;
	}

}
