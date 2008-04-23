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

import java.sql.PreparedStatement;

import net.sf.okapi.Borneo.Core.DBBase;
import net.sf.okapi.Borneo.Core.DBDoc;
import net.sf.okapi.Filter.FilterAccess;
import net.sf.okapi.Filter.FilterItemText;
import net.sf.okapi.Filter.FilterItemType;
import net.sf.okapi.Filter.IFilter;
import net.sf.okapi.Filter.IFilterItem;
import net.sf.okapi.Library.Base.LogType;
import net.sf.okapi.Library.Base.Utils;

public class ExtractSource extends BaseAction {

	private FilterAccess          m_FA;
	private DBBase                m_DB;
	private DBDoc                 m_Doc;
	private PreparedStatement     m_PStm;
	private String                rootFolder;
	
	public ExtractSource (String rootFolder,
		FilterAccess p_FA,
		DBBase p_DB)
	{
		this.rootFolder = rootFolder;
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

		for ( int i=0; i<m_nDocTotal; i++ ) {
			if ( !processDocument(p_aDKeys[i]) ) return false;
			m_FA.getLog().setLog(LogType.MAINPROGRESS,
				Utils.getPercentage(++m_nCurrentDoc, m_nDocTotal), null);
		}
		return true;
	}

	@Override
	public String getID () {
		return ID_EXTRACTSOURCE;
	}

	@Override
	public String getName () {
		return "Extract Source Document";
	}

	@Override
	public boolean start () {
		return m_FA.getLog().beginTask(getName());
	}
	
	@Override
	public void stop () {
		try {
			if ( m_PStm != null ) m_PStm.close();
		}
		catch ( Exception E ) {
			m_FA.getLog().error(E.getLocalizedMessage());
		}
		m_FA.getLog().endTask(null);
	}
	
	private boolean processDocument (int p_nDKey)
	{
		boolean         bContinue = true;
		boolean         bDoEnd = false;
		int             nRes;
		IFilterItem     SourceFI;
		IFilterItem     TargetFI;

		try {
			m_Doc = m_DB.getSourceDocumentData(p_nDKey, null);
			m_FA.getLog().message(Res.getString("INPUT_DOCUMENT") + m_Doc.getRelativePath());

			if ( m_Doc.isSourceTypeInternal() ) {
				// Internal documents are not extractable
				m_FA.getLog().message("Properties.Resources.INTERNAL_SKIPPED");
				return bContinue; // Move on to next file
			}

			String sFSettings = m_Doc.getFSettings(); 
			if (( sFSettings == null ) || ( sFSettings.length() == 0 )) { 
				// No associated filter
				m_FA.getLog().message("Properties.Resources.NOSETTINGS_SKIPPED");
				// Copy the input file to the project extraction storage
				//TODO m_DB.createOriginalDataStorage(m_Doc, null);
				return bContinue; // Move on to next file
			}

			// Check the last update date
			//if ( !m_bForce )
			//{
			//   FileInfo FI = new System.IO.FileInfo(m_Doc.FullPath);
			//   if ( m_Inp.LastUpdate > FI.LastWriteTimeUtc )
			//   {
			//      m_FA.Log.Message(Properties.Resources.NOUPDATENEEDED);
			//      return true; // Move on to next file
			//   }
			//}
			m_FA.loadFilterFromFilterSettingsType1(rootFolder, sFSettings);
			if ( m_FA.getLog().getErrorCount() > 0 ) {
				bContinue = false;
				return bContinue;
			}

			// Open the file to extract
			m_FA.getFilter().openInputFile(m_Doc.getFullPath(), m_DB.getSourceLanguage(),
				m_Doc.getEncoding(m_DB.getSourceEncoding()));
			bDoEnd = true;

			// Start the transaction
			m_DB.startBatchMode();
			
			// Remove all entries for the given file that are still 'pending'
			// (i.e. Delete previous extraction result if it was not updated yet)
			String sTmp = String.format("DELETE FROM %s WHERE (%s=%d AND %s=%d)",
				m_DB.getTableName(DBBase.TBLNAME_SRC), DBBase.SRCCOLN_DKEY, p_nDKey,
				DBBase.SRCCOLN_STATUS, DBBase.SSTATUS_PENDING);
			m_DB.doCommand(sTmp);

			// Get the source items
			//Needed? for what?? m_rsTmp = m_DB.getSourceItems(p_nDKey, true);
			
			// Prepare the update statement for adding rows
			sTmp = String.format("INSERT INTO %s (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)",
				m_DB.getTableName(DBBase.TBLNAME_SRC), DBBase.SRCCOLN_DKEY, DBBase.SRCCOLN_MAXWIDTH,
				DBBase.SRCCOLN_FLAG, DBBase.SRCCOLN_NOTRANS, DBBase.SRCCOLN_STATUS,
				DBBase.SRCCOLN_GKEY, DBBase.SRCCOLN_XKEY, DBBase.SRCCOLN_TEXT,
				DBBase.SRCCOLN_CODES, DBBase.SRCCOLN_RESNAME, DBBase.SRCCOLN_RESTYPE,
				DBBase.SRCCOLN_START, DBBase.SRCCOLN_LENGTH);
			m_PStm = m_DB.prepareStatement(sTmp);

			// Parse the file
			IFilter Flt = m_FA.getFilter();
			do {
				nRes = Flt.readItem();
				SourceFI = Flt.getItem();
				switch ( nRes ) {
					case FilterItemType.TEXT:
						// Extract all, included when IsTranslatable() is set
						// the R/O state is set for such entries
						//nCount++;
						if ( SourceFI.isTranslated() ) {
							TargetFI = Flt.getTranslatedItem();
							//nTranslatedCount++;
						}
						else TargetFI = null;
						// Add the item to the DB
						addItem(p_nDKey, SourceFI, TargetFI);
						break;
					case FilterItemType.STARTGROUP:
						//m_Prj.DB.PushGroup(SourceFI);
						break;
					case FilterItemType.ENDGROUP:
						//m_Prj.DB.PopGroup();
						break;
					case FilterItemType.USERCANCEL:
					case FilterItemType.ERROR:
						m_DB.cancelBatch();
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

			if ( bContinue ) {
				// Copy the input file and filter parameters to the extraction storage
				m_DB.createOriginalDataStorage(m_Doc, Flt);
			}
		}
		catch ( Exception E ) {
			m_FA.getLog().error(E.getLocalizedMessage());
			bContinue = false;
		}
		finally {
			if ( bDoEnd ) {
				try {
					m_DB.stopBatchMode(bContinue);
					m_Doc.setStatus(String.format("%s / %s", getName(),
						(bContinue ? Res.getString("ACTION_DONE")
						: Res.getString(Res.getString("ACTION_ERROR")))));
					m_DB.updateSourceDocumentStatus(m_Doc);
				}
				catch ( Exception E ) {
					m_FA.getLog().error(E.getLocalizedMessage());
					bContinue = false;
				}
			}
			if ( m_FA.getFilter() != null ) m_FA.getFilter().closeInput();
		}
		return bContinue;
	}

	public boolean addItem (int p_nDKey,
		IFilterItem p_SrcFI,
		IFilterItem p_TrgFI)
	{
		try {
			m_PStm.setInt(1, p_nDKey); // DKEY
			m_PStm.setInt(2, 0); // MAXWIDTH
			m_PStm.setBoolean(3, false); // FLAG
			m_PStm.setBoolean(4, (!p_SrcFI.isTranslatable() || !p_SrcFI.hasText(false))); // NOTRANS
			m_PStm.setInt(5, DBBase.SSTATUS_PENDING); // STATUS
			m_PStm.setInt(6, p_SrcFI.getGroupID()); // GKEY
			m_PStm.setInt(7, p_SrcFI.getItemID()); // XKEY
			m_PStm.setString(8, p_SrcFI.getText(FilterItemText.GENERIC)); // TEXT
			m_PStm.setString(9, p_SrcFI.getCodeMapping()); // CODES
			m_PStm.setString(10, p_SrcFI.getResName()); // RESNAME
			m_PStm.setString(11, p_SrcFI.getResType()); // RESTYPE
			m_PStm.setLong(12, p_SrcFI.getStart()); // START
			m_PStm.setInt(13, p_SrcFI.getLength()); // LENGTH
			m_PStm.executeUpdate();
			return true;
		}
		catch ( Exception E ) {
			m_DB.cancelBatch();
			m_FA.getLog().error(E.getLocalizedMessage());
			return false;
		}
	}

}
