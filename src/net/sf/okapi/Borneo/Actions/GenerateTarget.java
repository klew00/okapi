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
import java.sql.SQLException;
import java.sql.Statement;

import net.sf.okapi.Borneo.Core.DBBase;
import net.sf.okapi.Borneo.Core.DBDoc;
import net.sf.okapi.Borneo.Core.DBTarget;
import net.sf.okapi.Filter.FilterAccess;
import net.sf.okapi.Filter.FilterItem;
import net.sf.okapi.Filter.FilterItemText;
import net.sf.okapi.Filter.FilterItemType;
import net.sf.okapi.Filter.IFilter;
import net.sf.okapi.Filter.IFilterItem;
import net.sf.okapi.Library.Base.LogType;
import net.sf.okapi.Library.Base.Utils;

public class GenerateTarget extends BaseAction {

	private FilterAccess          m_FA;
	private DBBase                m_DB;
	private DBDoc                 m_Doc;
	private String                m_sTarget;
	private IFilterItem           m_SourceFI;
	private ResultSet             m_rsTrg;
	
	public GenerateTarget (FilterAccess p_FA,
		DBBase p_DB)
	{
		m_FA = p_FA;
		m_DB = p_DB;
		m_Doc = null;
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
		return ID_GENERATETARGET;
	}

	@Override
	public String getName () {
		return "Generate Target Documents";
	}

	@Override
	public boolean needsTarget () {
		return true;
	}
	@Override
	public boolean start () {
		return m_FA.getLog().beginTask(getName());
	}
	
	@Override
	public void stop () {
		m_FA.getLog().endTask(null);
	}
	
	private boolean processDocument (int p_nDKey)
	{
		boolean         bContinue = true;
		boolean         bDoEnd = false;
		IFilter         Flt = null;
		Statement       stmTrg = null;
		int             nRes;

		try {
			m_Doc = m_DB.getSourceDocumentData(p_nDKey, m_sTarget);
			m_FA.getLog().message(Res.getString("INPUT_DOCUMENT") + m_Doc.getRelativePath());
			
			if ( m_Doc.getTargetDoc().isExcluded() ) {
				m_FA.getLog().message(String.format(Res.getString("TARGETEXCLUDEDFORTHISDOC"), m_sTarget));
				return bContinue;
			}

			// Get the path, settings, and encoding from the storage
			String[] aRes = m_DB.getOriginalData(p_nDKey, DBBase.DIR_TRGDOCS, m_sTarget);

			// Compose the target path
			DBTarget Trg = m_DB.getTargetData(m_sTarget);
			String sTrgPath = m_Doc.getTargetDoc().getFullPath(m_Doc, Trg, m_DB.getSourceRoot(), m_sTarget);
			m_FA.getLog().message(Res.getString("OUTPUT_DOCUMENT") + sTrgPath);
			
			// Do we have a filter associated with the document?
			if (( aRes[1] == null ) || (aRes[1].length() == 0 )) {
				// If not: just copy the file to the destination
				m_DB.copyDocument(aRes[0], sTrgPath);
				return true;
			}
			
			m_FA.loadFilterFromFilterSettingsType1(m_DB.getParametersFolder(), aRes[1]);
			if ( m_FA.getLog().getErrorCount() > 0 ) return false;
			Flt = m_FA.getFilter();

			// Open the document to extract
			Flt.openInputFile(aRes[0], m_DB.getSourceLanguage(), aRes[2]);

			// Open the target document
			String sOutEnc = m_Doc.getTargetDoc().getEncoding();
			if ( sOutEnc == null ) sOutEnc = Trg.getEncoding();
			Flt.setOutputOptions(m_sTarget, sOutEnc);
			Flt.openOutputFile(sTrgPath);
			bDoEnd = true;

			// Get all the target entries, sorted by SKey
			String sTmp = String.format("SELECT * FROM %s WHERE (%s='%s' AND %s=%d) ORDER BY %s",
				m_DB.getTableName(DBBase.TBLNAME_TRG), DBBase.TRGCOLN_LANG, m_sTarget,
				DBBase.TRGCOLN_DKEY, p_nDKey, DBBase.TRGCOLN_XKEY);
			stmTrg = m_DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			m_rsTrg = stmTrg.executeQuery(sTmp);

			// Parse the file
			do {
				nRes = Flt.readItem();
				m_SourceFI = Flt.getItem();
				switch ( nRes ) {
					case FilterItemType.TEXT:
						getTranslation();
						break;
					case FilterItemType.STARTGROUP:
						//m_Prj.DB.PushGroup(SourceFI);
						break;
					case FilterItemType.ENDGROUP:
						//m_Prj.DB.PopGroup();
						break;
					case FilterItemType.USERCANCEL:
					case FilterItemType.ERROR:
						break;
					case FilterItemType.ENDINPUT:
					case FilterItemType.BINARY:
					case FilterItemType.STARTBINARY:
					case FilterItemType.ENDBINARY:
					case FilterItemType.BLOCK:
					default:
						// Do nothing
						break;
				}
				Flt.writeItem();
			}
			while ( nRes > FilterItemType.ENDINPUT );

			if ( nRes == FilterItemType.ERROR ) {
				m_FA.getLog().error("Properties.Resources.READING_ERROR");
				bContinue = false;
			}

			// Check if user stopped the task
			if ( !m_FA.getLog().canContinue() ) bContinue = false;
		}
		catch ( Exception E )
		{
			m_FA.getLog().error(E.getLocalizedMessage());
			bContinue = false;
		}
		finally {
			if ( Flt != null ) {
				if ( bDoEnd ) Flt.closeOutput();
				Flt.closeInput();
			}
			if ( bDoEnd ) {
				try {
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

	private void getTranslation ()
		throws SQLException
	{
		//TODO: get a fastest way to look up the resultset
		boolean bFound = false;
		m_rsTrg.beforeFirst();
		int nID = m_SourceFI.getItemID();
		while ( m_rsTrg.next() ) {
			if ( m_rsTrg.getInt(DBBase.TRGCOLI_XKEY) == nID ) {
				bFound = true;
				break;
			}
		}
		
		if ( !bFound ) {
			m_FA.getLog().warning(String.format("Item #%d: Not found in the table.",
				m_SourceFI.getItemID()));
			return;
		}

		// Leave empty entries empty
		if ( m_SourceFI.isEmpty() ) return;

		// Non-empty entries should have some translation
		String sTrg = m_rsTrg.getString(DBBase.TRGCOLN_TTEXT);
		if (( sTrg == null ) || ( sTrg.length() == 0 ))
		{
			// Give a warning and keep the original
			//TODO: Externalize
			m_FA.getLog().warning(String.format("Item #%d: No translation in the table.", nID));
			return;
		}

		// Check if the source text is the same
		if ( !m_SourceFI.getText(FilterItemText.GENERIC).equals(
			m_rsTrg.getString(DBBase.TRGCOLN_STEXT)) ) {
			// Give a warning and keep the original
			//TODO: Externalize
			m_FA.getLog().warning(String.format("Item #%d: Different source text in the table.", nID));
		}

		// Else: Use the target of the table
		// The codes should be the same
		if ( m_SourceFI.hasCode() )
			m_SourceFI.modifyText(FilterItem.genericToCoded(sTrg, m_SourceFI));
		else
			m_SourceFI.modifyText(sTrg);
	}

}
