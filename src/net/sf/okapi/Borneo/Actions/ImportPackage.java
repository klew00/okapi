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

package net.sf.okapi.Borneo.Actions;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;

import net.sf.okapi.Borneo.Core.DBBase;
import net.sf.okapi.Borneo.Core.DBDoc;
import net.sf.okapi.Filter.FilterAccess;
import net.sf.okapi.Filter.FilterItemText;
import net.sf.okapi.Filter.IFilterItem;
import net.sf.okapi.Library.Base.LogType;
import net.sf.okapi.Library.Base.Utils;
import net.sf.okapi.Package.Manifest;
import net.sf.okapi.Package.ManifestItem;
import net.sf.okapi.Package.XLIFF.Reader;

public class ImportPackage extends BaseAction {

	private FilterAccess          m_FA;
	private DBBase                m_DB;
	private DBDoc                 m_Doc;
	private String                m_sTarget;
	private Manifest              m_Mnf;
	private Reader                m_PkgR;
	
	public ImportPackage (FilterAccess p_FA,
		DBBase p_DB)
	{
		m_FA = p_FA;
		m_DB = p_DB;
		m_Doc = null;
	}

	public void setManifest (Manifest p_Manifest) {
		m_Mnf = p_Manifest;
	}
	
	@Override
	/**
	 * This action does not use the normal parameters passed to this method.
	 * They can be set to null. All is driven from the manifest document.
	 */
	public boolean execute (int[] p_aDKeys,
		String[] p_aTargets)
	{
		try {
			// Instantiate a package read of the proper type
			//TODO: select from package type
			m_PkgR = new Reader(m_FA.getLog());
			
			// One target language only, and take it from the manifest
			m_sTarget = m_Mnf.getTargetLanguage();
			m_FA.getLog().message(Res.getString("TRG_LANGUAGE") + m_sTarget);
			
			// For each documents that is selected
			// Use the manifest to get the documents, not the parameters
			m_nDocTotal = m_Mnf.getItems().size();
			m_nCurrentDoc = 0;
			Enumeration<Integer> E = m_Mnf.getItems().keys();
			int nDKey;
			ManifestItem MI;
			
			while ( E.hasMoreElements() ) {
				nDKey = E.nextElement();
				MI = m_Mnf.getItems().get(nDKey);
				if ( MI.isSelected() ) {
					if ( !processDocument(nDKey, MI) ) return false;
				}
				m_FA.getLog().setLog(LogType.MAINPROGRESS,
					Utils.getPercentage(++m_nCurrentDoc, m_nDocTotal), null);
			}
		}
		catch ( Exception E ) {
			m_FA.getLog().error(E.getLocalizedMessage());
			return false;
		}
		return true;
	}

	@Override
	public String getID () {
		return ID_IMPORTPACKAGE;
	}

	@Override
	public String getName () {
		return "Import Translation Package";
	}

	@Override
	public boolean needsTarget () {
		return true;
	}

	@Override
	public boolean start () {
		if ( !m_FA.getLog().beginTask(getName()) ) return false;
		return true;
	}
	
	@Override
	public void stop () {
		m_FA.getLog().endTask(null);
	}
	
	private boolean processDocument (int p_nDKey,
		ManifestItem p_MI)
	{
		boolean         bContinue = true;
		boolean         bDoEnd = false;
		Statement       stmTrg = null;

		try {
			m_Doc = m_DB.getSourceDocumentData(p_nDKey, m_sTarget);
			m_FA.getLog().message(Res.getString("INPUT_DOCUMENT") + m_Doc.getRelativePath());
			m_FA.getLog().message(Res.getString("PKG_DOCUMENT") + m_Mnf.getItemRelativeTargetPath(p_nDKey));
			if ( m_Doc.getTargetDoc().isExcluded() ) {
				m_FA.getLog().message(String.format(Res.getString("TARGETEXCLUDEDFORTHISDOC"), m_sTarget));
				return bContinue;
			}
			
			bDoEnd = true;
			// Start the transaction
			m_DB.startBatchMode();
			
			// Get the target items
			// Get all the flagged entries (should the same as above), sorted by SKey
			String sTmp = String.format("SELECT * FROM %s WHERE (%s='%s' AND %s=%d) ORDER BY %s",
				m_DB.getTableName(DBBase.TBLNAME_TRG), DBBase.TRGCOLN_LANG, m_sTarget,
				DBBase.TRGCOLN_DKEY, p_nDKey, DBBase.TRGCOLN_KEY);
			stmTrg = m_DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rsTrg = stmTrg.executeQuery(sTmp);
			
			m_PkgR.openDocument(m_Mnf.getItemFullTargetPath(p_nDKey));

			IFilterItem SrcFI;
			int nTKey;
			while ( m_PkgR.readItem() ) {
				SrcFI = m_PkgR.getSourceItem();
				
				// Skip item that were not to be translated
				if ( !SrcFI.isTranslatable() ) continue;
				
				// Find the corresponding item in the target table
				//TODO: do it in a better way (it's sorted)
				nTKey = SrcFI.getItemID();
				rsTrg.beforeFirst();
				boolean bFound = false;
				while ( rsTrg.next() ) {
					if ( rsTrg.getInt(DBBase.TRGCOLI_KEY) == nTKey ) {
						bFound = true;
						break;
					}
				}
				if ( !bFound ) {
					// Item does not exist anymore
					//TODO: maybe add them as 'un-used'???
					m_FA.getLog().warning(String.format("The item #%d does not exist in the table anymore.", nTKey));
					continue;
				}

				int nDBStatus = rsTrg.getInt(DBBase.TRGCOLI_STATUS);
				switch ( nDBStatus ) {
					case DBBase.TSTATUS_NOTRANS:
					case DBBase.TSTATUS_UNUSED:
						continue;
					default:
						if ( !SrcFI.isTranslated() ) {
							// No translation in the file
							m_FA.getLog().warning("No translation found in file.");
							continue;
						}
						rsTrg.updateString(DBBase.TRGCOLI_TTEXT, m_PkgR.getTargetItem(
							).getText(FilterItemText.GENERIC));
						switch ( nDBStatus ) {
							case DBBase.TSTATUS_TOTRANS:
								rsTrg.updateInt(DBBase.TRGCOLN_STATUS, DBBase.TSTATUS_TOEDIT);
								break;
							case DBBase.TSTATUS_TOEDIT:
								rsTrg.updateInt(DBBase.TRGCOLN_STATUS, DBBase.TSTATUS_TOREVIEW);
								break;
							case DBBase.TSTATUS_TOREVIEW:
								rsTrg.updateInt(DBBase.TRGCOLN_STATUS, DBBase.TSTATUS_OK);
								break;
						}
						rsTrg.updateRow();
						break;
				}
			}
			
			// Check if user stopped the task
			if ( !m_FA.getLog().canContinue() ) bContinue = false;
		}
		catch ( Exception E ) {
			m_FA.getLog().error(E.getLocalizedMessage());
			bContinue = false;
		}
		finally {
			if ( bDoEnd ) {
				m_PkgR.closeDocument();
				try {
					m_DB.stopBatchMode(bContinue);
					m_Doc.getTargetDoc().setStatus(String.format("%s / %s", getName(),
						(bContinue ? Res.getString("ACTION_DONE")
						: Res.getString(Res.getString("ACTION_ERROR")))));
					m_DB.updateTargetDocumentStatus(m_Doc);
				}
				catch ( Exception E ) {
					m_FA.getLog().error(E.getLocalizedMessage());
					bContinue = false;
				}
			}
			try {
				if ( stmTrg != null ) stmTrg.close();
			}
			catch ( Exception E ) {
				m_FA.getLog().error(E.getLocalizedMessage());
			}
		}
		
		return bContinue;
	}

}
