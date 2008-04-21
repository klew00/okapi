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

import net.sf.okapi.Borneo.Core.DBBase;
import net.sf.okapi.Borneo.Core.DBDoc;
import net.sf.okapi.Filter.FilterAccess;
import net.sf.okapi.Library.Base.LogType;
import net.sf.okapi.Library.Base.Utils;

public class UpdateTarget extends BaseAction {

	private FilterAccess          m_FA;
	private DBBase                m_DB;
	private DBDoc                 m_Doc;
	private String                m_sTarget;
	
	public UpdateTarget (FilterAccess p_FA,
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
		return ID_UPDATETARGET;
	}

	@Override
	public String getName () {
		return "Update Target Table";
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
		Statement       stmTrg = null;
		Statement       stmSrc = null;

		try {
			m_Doc = m_DB.getSourceDocumentData(p_nDKey, m_sTarget);
			m_FA.getLog().message(Res.getString("INPUT_DOCUMENT") + m_Doc.getRelativePath());
			if ( m_Doc.getTargetDoc().isExcluded() )
			{
				m_FA.getLog().message(String.format(Res.getString("TARGETEXCLUDEDFORTHISDOC"), m_sTarget));
				return bContinue;
			}

			bDoEnd = true;
			// Start the transaction
			m_DB.startBatchMode();
			
			// Set all the relevant target entries with a temporary 999 flag
			String sTmp = String.format("UPDATE %s SET %s=999 WHERE (%s='%s' AND %s=%d)",
				m_DB.getTableName(DBBase.TBLNAME_TRG), DBBase.TRGCOLN_TMP,
				DBBase.TRGCOLN_LANG, m_sTarget,
				DBBase.TRGCOLN_DKEY, p_nDKey);
			m_DB.doCommand(sTmp);

			// Get all the flagged entries (should the same as above), sorted by SKey
			sTmp = String.format("SELECT * FROM %s WHERE (%s='%s' AND %s=%d) ORDER BY %s",
				m_DB.getTableName(DBBase.TBLNAME_TRG),
				DBBase.TRGCOLN_LANG, m_sTarget,
				DBBase.TRGCOLN_DKEY, p_nDKey,
				DBBase.TRGCOLN_SKEY);
			stmTrg = m_DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rsTrg = stmTrg.executeQuery(sTmp);
			
			// Get the source items for the document (minus the deleted or pending)
			sTmp = String.format("SELECT * FROM %s WHERE (%s=%d AND (%s<>%d AND %s<>%d))",
				m_DB.getTableName(DBBase.TBLNAME_SRC),
				DBBase.SRCCOLN_DKEY, p_nDKey,
				DBBase.SRCCOLN_STATUS, DBBase.SSTATUS_DELETED,
				DBBase.SRCCOLN_STATUS, DBBase.SSTATUS_PENDING);
			stmSrc = m_DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rsSrc = stmSrc.executeQuery(sTmp);

			// Go through the source items, trying to find matches
			if ( !rsSrc.last() ) {
				// Nothing to do, abort quietly
				m_DB.cancelBatch();
				return bContinue;
			}
			rsSrc.beforeFirst();

			// Get the count of existing targets
			int nTrgCount = 0;
			if ( rsTrg.last() ) {
				nTrgCount = rsTrg.getRow();
				rsTrg.beforeFirst();
			}
			
			int nSKey;
			while ( rsSrc.next() ) {
				nSKey = rsSrc.getInt(DBBase.TRGCOLI_KEY);
				// Find it in the target set
				boolean bFound = false;
				if ( nTrgCount > 0 ) {
					rsTrg.beforeFirst();
					for ( int i=1; i<=nTrgCount; i++ ) {
						rsTrg.relative(+1);
						if ( rsTrg.getInt(DBBase.TRGCOLI_SKEY) == nSKey ) {
							bFound = true;
							break;
						}
					}
				}
				
				if ( bFound ) { // Entry found: update it
					switch ( rsSrc.getInt(DBBase.SRCCOLI_STATUS) )
					{
						case DBBase.SSTATUS_NEW:
						case DBBase.SSTATUS_GUESSED_MOD:
						case DBBase.SSTATUS_GUESSED_SAME:
						case DBBase.SSTATUS_SAME_MOD:
						case DBBase.SSTATUS_SAME_SAME:
							String sTrgSText = rsTrg.getString(DBBase.TRGCOLI_STEXT); 
							if (( sTrgSText != null ) && sTrgSText.equals(rsSrc.getString(DBBase.SRCCOLI_TEXT)) ) {
								// If source text is the same: keep current target status,
								// except if there is no translation, then force 'to-translate'
								String sTrgTText = rsTrg.getString(DBBase.TRGCOLI_TTEXT);  
								if (( sTrgTText != null ) && ( sTrgTText.length() == 0 ))
									rsTrg.updateInt(DBBase.TRGCOLI_STATUS, DBBase.TSTATUS_TOTRANS);
							}
							else {
								// If source text is different, force 'to-translate'
								rsTrg.updateInt(DBBase.TRGCOLI_STATUS, DBBase.TSTATUS_TOTRANS);
							}
							break;
					}
				}
				else { // No corresponding entry: add a new one
					rsTrg.moveToInsertRow();
					rsTrg.updateString(DBBase.TRGCOLI_LANG, m_sTarget);
					rsTrg.updateInt(DBBase.TRGCOLI_SKEY, nSKey);
					rsTrg.updateInt(DBBase.TRGCOLI_DKEY, rsSrc.getInt(DBBase.SRCCOLI_DKEY));
					rsTrg.updateInt(DBBase.TRGCOLI_STATUS, DBBase.TSTATUS_TOTRANS);
				}
				// Common changes
				rsTrg.updateString(DBBase.TRGCOLI_STEXT, rsSrc.getString(DBBase.SRCCOLI_TEXT));
				rsTrg.updateString(DBBase.TRGCOLI_SCODES, rsSrc.getString(DBBase.SRCCOLI_CODES));
				rsTrg.updateInt(DBBase.TRGCOLI_GKEY, rsSrc.getInt(DBBase.SRCCOLI_GKEY));
				rsTrg.updateInt(DBBase.TRGCOLI_XKEY, rsSrc.getInt(DBBase.SRCCOLI_XKEY));
				rsTrg.updateString(DBBase.TRGCOLI_RESTYPE, rsSrc.getString(DBBase.SRCCOLI_RESTYPE));
				rsTrg.updateString(DBBase.TRGCOLI_RESNAME, rsSrc.getString(DBBase.SRCCOLI_RESNAME));
				rsTrg.updateInt(DBBase.TRGCOLN_TMP, 0);
				rsTrg.updateBoolean(DBBase.TRGCOLI_FLAG, false);
				// Read-only override
				if ( rsSrc.getBoolean(DBBase.SRCCOLI_NOTRANS) ) {
					rsTrg.updateInt(DBBase.TRGCOLI_STATUS, DBBase.TSTATUS_NOTRANS);
					rsTrg.updateString(DBBase.TRGCOLI_TTEXT, rsSrc.getString(DBBase.SRCCOLI_TEXT));
				}
				
				// Update or insert the data
				if ( bFound ) rsTrg.updateRow();
				else rsTrg.insertRow();
			}
			
			// Set the unused items to unused
			// Set all the relevant target entries with a temporary 999 flag
			sTmp = String.format("UPDATE %s SET %s=0, %s=%d WHERE (%s='%s' AND %s=%d AND %s=999)",
				m_DB.getTableName(DBBase.TBLNAME_TRG), DBBase.TRGCOLN_TMP,
				DBBase.TRGCOLN_STATUS, DBBase.TSTATUS_UNUSED, DBBase.TRGCOLN_LANG,
				m_sTarget, DBBase.TRGCOLN_DKEY, p_nDKey, DBBase.TRGCOLN_TMP);
			m_DB.doCommand(sTmp);

			// Check if user stopped the task
			if ( !m_FA.getLog().canContinue() ) bContinue = false;

			// Make sure we stop if one of the DB call failed
			if ( m_DB.isBatchCanceled() ) bContinue = false;

			if ( bContinue && !m_Doc.isSourceTypeInternal() ) {
				m_DB.copyOriginalData(p_nDKey, DBBase.DIR_SRCDOCS,
					DBBase.DIR_TRGDOCS, m_sTarget, false);
			}
		}
		catch ( Exception E )
		{
			m_FA.getLog().error(E.getLocalizedMessage());
			bContinue = false;
		}
		finally {
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
				if ( stmSrc != null ) stmSrc.close();
			}
			catch ( Exception E ) {
				m_FA.getLog().error(E.getLocalizedMessage());
			}
		}
		return bContinue;
	}
}
