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

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sf.okapi.Borneo.Core.DBBase;
import net.sf.okapi.Borneo.Core.DBDoc;
import net.sf.okapi.Borneo.Core.DBTarget;
import net.sf.okapi.Filter.FilterAccess;
import net.sf.okapi.Filter.FilterItemText;
import net.sf.okapi.Filter.FilterItemType;
import net.sf.okapi.Filter.IFilter;
import net.sf.okapi.Filter.IFilterItem;
import net.sf.okapi.Library.Base.IParameters;
import net.sf.okapi.Library.Base.LogType;
import net.sf.okapi.Library.Base.Utils;

public class ImportTranslation extends BaseAction {

	private FilterAccess               m_FA;
	private DBBase                     m_DB;
	private DBDoc                      m_Doc;
	private String                     m_sTarget;
	private ImportTranslationOptions   m_Opt;
	private IFilterItem                m_SourceFI;
	private IFilterItem                m_TargetFI;
	private ResultSet                  m_rsTrg;
	
	public ImportTranslation (FilterAccess p_FA,
		DBBase p_DB)
	{
		m_FA = p_FA;
		m_DB = p_DB;
		m_Doc = null;
		m_Opt = new ImportTranslationOptions();
	}

	@Override
	public boolean execute (int[] p_aDKeys,
		String[] p_aTargets)
	{
		m_nDocTotal = p_aDKeys.length;
		m_nCurrentDoc = 0;

		for ( int i=0; i<p_aTargets.length; i++ ) {
			// For each target
			m_sTarget = p_aTargets[i];
			m_FA.getLog().message(Res.getString("TRG_LANGUAGE") + m_sTarget);
			
			// For each documents
			for ( int j=0; j<m_nDocTotal; j++ ) {
				if ( !processDocument(p_aDKeys[j]) ) return false;
				m_FA.getLog().setLog(LogType.MAINPROGRESS,
					Utils.getPercentage(++m_nCurrentDoc, m_nDocTotal), null);
			}
		}
		return true;
	}

	@Override
	public String getID () {
		return ID_IMPORTTRANSLATION;
	}

	@Override
	public String getName () {
		return "Import Translation";
	}

	@Override
	public IParameters getOptions () {
		return m_Opt;
	}

	@Override
	public boolean hasOptions () {
		return true;
	}

	@Override
	public boolean needsTarget () {
		return true;
	}

	@Override
	public void setOptions (IParameters p_Value) {
		m_Opt = (ImportTranslationOptions)p_Value;
	}

	@Override
	public boolean start () {
		return m_FA.getLog().beginTask(getName());
	}
	
	@Override
	public void stop () {
		m_FA.getLog().endTask(null);
	}
	
	private boolean processDocument (int p_nDKey) {
		boolean         bContinue = true;
		boolean         bDoEnd = false;
		Statement       stmTrg = null;
		IFilter         Flt = null;

		try {
			m_Doc = m_DB.getSourceDocumentData(p_nDKey, m_sTarget);
			m_FA.getLog().message(Res.getString("INPUT_DOCUMENT") + m_Doc.getRelativePath());
			if ( m_Doc.getTargetDoc().isExcluded() ) {
				m_FA.getLog().message(String.format(Res.getString("TARGETEXCLUDEDFORTHISDOC"), m_sTarget));
				return bContinue;
			}
			
			// Get the parameters to read the file to import
			String sPath = null;
			String sEncoding = null;
			String sFSettings = null;
			String sLang = m_sTarget;
			String[] aRes = m_DB.getOriginalData(p_nDKey, DBBase.DIR_TRGDOCS, m_sTarget);
			switch ( m_Opt.getImportType() ) {
			case 0: // From source
				sPath = aRes[0];
				sFSettings = aRes[1];
				sEncoding = aRes[2];
				sLang = m_DB.getSourceLanguage();
				break;
			case 1: // From target
				DBTarget Trg = m_DB.getTargetData(m_sTarget);
				sPath = m_Doc.getTargetDoc().getFullPath(m_Doc, Trg, m_DB.getSourceRoot(), m_sTarget);
				sFSettings = aRes[1];
				sEncoding = m_Doc.getTargetDoc().getEncoding();
				if ( sEncoding == null ) sEncoding = Trg.getEncoding();
				break;
			case 2: // From other
				sPath = m_Opt.getPath();
				sFSettings = m_Opt.getFSettings();
				sEncoding = m_Opt.getEncoding();
				break;
			}

			// Checks if the file to import exists.
			// If not it's a warning not an error.
			if ( !(new File(sPath)).exists() ) {
				m_FA.getLog().warning(String.format(Res.getString("FILENOTFOUND_SKIPIT"), sPath));
				return bContinue;
			}
			else m_FA.getLog().message(Res.getString("IMPORT_FROM") + sPath);
			
			// Loads the filter
			m_FA.loadFilter(sFSettings);
			if ( m_FA.getLog().getErrorCount() > 0 ) return false;
			Flt = m_FA.getFilter();

			// Open the document where to import from
			Flt.openInputFile(sPath, sLang, sEncoding);
			
			bDoEnd = true;
			// Start the transaction
			m_DB.startBatchMode();

			// Get all the target entries
			//TODO: find a good way to search on resname
			String sTmp = String.format("SELECT * FROM %s WHERE (%s='%s' AND %s=%d) ORDER BY %s",
				m_DB.getTableName(DBBase.TBLNAME_TRG), DBBase.TRGCOLN_LANG, m_sTarget,
				DBBase.TRGCOLN_DKEY, p_nDKey, DBBase.TRGCOLN_RESNAME);
			stmTrg = m_DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			m_rsTrg = stmTrg.executeQuery(sTmp);

			// Parse the file
			int nRes;
			do {
				nRes = Flt.readItem();
				switch ( nRes ) {
				case FilterItemType.TEXT:
					m_SourceFI = Flt.getItem();
					if ( m_SourceFI.isTranslated() )
						m_TargetFI = Flt.getTranslatedItem();
					else
						m_TargetFI = null;
					leverage();
					break;
				case FilterItemType.USERCANCEL:
				case FilterItemType.ERROR:
					m_DB.cancelBatch();
					break;
				}
			}
			while ( nRes > FilterItemType.ENDINPUT );

			if ( nRes == FilterItemType.ERROR ) {
				m_FA.getLog().error("Properties.Resources.READING_ERROR");
				bContinue = false;
			}

			// Check if user stopped the task
			if ( !m_FA.getLog().canContinue() ) bContinue = false;

			// Make sure we stop if one of the DB call failed
			if ( m_DB.isBatchCanceled() ) bContinue = false;
		}
		catch ( Exception E )
		{
			m_FA.getLog().error(E.getLocalizedMessage());
			bContinue = false;
		}
		finally {
			if ( Flt != null ) Flt.closeInput();
			if ( bDoEnd ) {
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

	/**
	 * Tries to leverages the imported item into the current target table.
	 * If the imported file is from source, use the target item. If it's
	 * from target or other: the source item is the translation (and no
	 * source text is available to confirm the match).
	 */
	private void leverage ()
		throws SQLException
	{
		// Search the table item by its resname
		m_rsTrg.beforeFirst();
		boolean bFound = false;
		while ( m_rsTrg.next() ) {
			if ( m_SourceFI.getResName().equals(m_rsTrg.getString(DBBase.TRGCOLI_RESNAME)) ) {
				bFound = true;
				break;
			}
		}
		if ( !bFound ) return;
		
		// Do we have a translation in the table?
		String sTrg = m_rsTrg.getString(DBBase.TRGCOLN_TTEXT);
		boolean bHasTrans = (( sTrg != null ) && ( sTrg.length() == 0 ));
		if ( bHasTrans ) {
			if ( sTrg.equals(m_rsTrg.getString(DBBase.TRGCOLN_STEXT)) ) {
				// If target == source, we consider it not a translation
				bHasTrans = false;
			}
		}

		// Check if leveraging applies
		if ( !m_Opt.includeItemsWithoutTranslation() && !bHasTrans ) return;
		if ( !m_Opt.includeItemsWithTranslation() && bHasTrans ) return;

		// Get the current status
		int nS = m_rsTrg.getInt(DBBase.TRGCOLN_STATUS);
		// Skip items with lower status than to-translate
		if ( nS < DBBase.TSTATUS_TOTRANS ) return;
		// Adjust the scope index to 0 for to-translate
		nS -= DBBase.TSTATUS_TOTRANS;
		int nNewStatus = DBBase.TSTATUS_TOREVIEW;

		if (( bHasTrans && m_Opt.getScopeWithTranslation()[nS] )
			|| ( !bHasTrans && m_Opt.getScopeWithoutTranslation()[nS] ))
		{
			// Do we have a bilingual entry?
			if ( m_TargetFI != null ) {
				// Never set an empty translation
				if ( m_TargetFI.isEmpty() ) return;
				// Otherwise, check the source text
				//TODO
				// Leverage the target text
				m_rsTrg.updateString(DBBase.TRGCOLN_TTEXT,
					m_TargetFI.getText(FilterItemText.GENERIC));
			}
			else { // One language only
				// Never set an empty translation
				if ( m_SourceFI.isEmpty() ) return;
				// Otherwise, this action assumes the file is a target file and its
				// source item is the corresponding translation.
				// The user is responsible for providing correct files.
				String sNewTrg = m_SourceFI.getText(FilterItemText.GENERIC);
				if ( sNewTrg.equals(m_rsTrg.getString(DBBase.TRGCOLI_STEXT)) ) {
					// If imported target==source and current target is different
					// Then we keep the current target
					if ( bHasTrans ) return;
					// Otherwise we set the status to to-edit
					nNewStatus = DBBase.TSTATUS_TOEDIT;
				}
				m_rsTrg.updateString(DBBase.TRGCOLN_TTEXT, sTrg);
			}
			// Update the status and save the updates
			m_rsTrg.updateInt(DBBase.TRGCOLN_STATUS, nNewStatus);
			m_rsTrg.updateRow();
		}
	}

}
