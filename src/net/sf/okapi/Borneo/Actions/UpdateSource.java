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
import net.sf.okapi.Filter.FilterAccess;
import net.sf.okapi.Library.Base.LogType;
import net.sf.okapi.Library.Base.Utils;

public class UpdateSource extends BaseAction {

	private FilterAccess          m_FA;
	private DBBase                m_DB;
	private DBDoc                 m_Doc;
	private ResultSet             m_rsExisting;
	private int                   m_nExistingCount;
	
	public UpdateSource (FilterAccess p_FA,
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

		for ( int i=0; i<m_nDocTotal; i++ ) {
			if ( !processDocument(p_aDKeys[i]) ) return false;
			m_FA.getLog().setLog(LogType.MAINPROGRESS,
				Utils.getPercentage(++m_nCurrentDoc, m_nDocTotal), null);
		}
		return true;
	}

	@Override
	public String getID () {
		return ID_UPDATESOURCE;
	}

	@Override
	public String getName () {
		return "Update Source Document";
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
		String          sTmp;
		Statement       stmPending = null;
		Statement       stmExisting = null;

		try {
			m_Doc = m_DB.getSourceDocumentData(p_nDKey, null);
			m_FA.getLog().message(Res.getString("INPUT_DOCUMENT") + m_Doc.getRelativePath());
			
			if ( m_Doc.isSourceTypeInternal() ) {
				// Internal documents are not extractable
				m_FA.getLog().message("Properties.Resources.INTERNAL_SKIPPED");
				return bContinue; // Move on to next file
			}

			//TODO: check last update

			bDoEnd = true;
			// Start the transaction
			m_DB.startBatchMode();

			// Get the pending items for this document
			sTmp = String.format("SELECT * FROM %s WHERE (%s=%d AND %s=%d)",
				m_DB.getTableName(DBBase.TBLNAME_SRC), DBBase.SRCCOLN_DKEY, p_nDKey,
				DBBase.SRCCOLN_STATUS, DBBase.SSTATUS_PENDING);
			stmPending = m_DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rsPending = stmPending.executeQuery(sTmp);

			if ( !rsPending.next() ) {
				// Nothing to do, abort quietly
				m_DB.cancelBatch();
				return bContinue;
			}
			rsPending.beforeFirst(); // Move back to start

			// Set all entries not pending or deleted to deleted
			sTmp = String.format("UPDATE %s SET %s=%d WHERE (%s=%d AND %s<>%d AND %s<>%d)",
				 m_DB.getTableName(DBBase.TBLNAME_SRC),
				 DBBase.SRCCOLN_STATUS, DBBase.SSTATUS_DELETED,
				 DBBase.SRCCOLN_DKEY, p_nDKey,
				 DBBase.SRCCOLN_STATUS, DBBase.SSTATUS_PENDING,
				 DBBase.SRCCOLN_STATUS, DBBase.SSTATUS_DELETED);
			m_DB.doCommand(sTmp);
			
			// Get the updated list of available items (all the ones set to deleted)
			// And sort then by RESNAME and XKEY
			sTmp = String.format("SELECT * FROM %s WHERE (%s=%d AND %s=%d) ORDER BY %s,%s",
				m_DB.getTableName(DBBase.TBLNAME_SRC), DBBase.SRCCOLN_DKEY, p_nDKey,
				DBBase.SRCCOLN_STATUS, DBBase.SSTATUS_DELETED, DBBase.SRCCOLN_RESNAME,
				DBBase.SRCCOLN_XKEY);
			stmExisting = m_DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			m_rsExisting = stmExisting.executeQuery(sTmp);

			// If it's a first time update: we can just set all items to 'new'
			if ( !m_rsExisting.next() ) {
				sTmp = String.format("UPDATE %s SET %s=%d WHERE %s=%d",
					 m_DB.getTableName(DBBase.TBLNAME_SRC), DBBase.SRCCOLN_STATUS, DBBase.SSTATUS_NEW,
					 DBBase.SRCCOLN_DKEY, p_nDKey);
				m_DB.doCommand(sTmp);
			}
			else {
				// Get the size of the existing set
				m_rsExisting.last();
				m_nExistingCount = m_rsExisting.getRow();

				// First pass: Go through the items with using resname
				int nDone = 0;
				int nToDo = 0;
				while ( rsPending.next() ) {
					nDone += doFirstPass(rsPending);
					nToDo++;
				}

				if ( nDone < nToDo ) {
					// Get the remaining pending items for this document
					sTmp = String.format("SELECT * FROM %s WHERE (%s=%d AND %s=%d)",
						m_DB.getTableName(DBBase.TBLNAME_SRC), DBBase.SRCCOLN_DKEY, p_nDKey,
						DBBase.SRCCOLN_STATUS, DBBase.SSTATUS_PENDING);
					rsPending = stmPending.executeQuery(sTmp);
					
					// Get the updated list of available items (all the ones set to deleted)
					// And sort then by RESNAME and XKEY
					sTmp = String.format("SELECT * FROM %s WHERE (%s=%d AND %s=%d) ORDER BY %s,%s",
						m_DB.getTableName(DBBase.TBLNAME_SRC), DBBase.SRCCOLN_DKEY, p_nDKey,
						DBBase.SRCCOLN_STATUS, DBBase.SSTATUS_DELETED, DBBase.SRCCOLN_RESNAME,
						DBBase.SRCCOLN_XKEY);
					m_rsExisting = stmExisting.executeQuery(sTmp);

					// If there is nothing left to align with, we 
					if ( m_rsExisting.next() ) {
						// Get the size of the existing set
						m_rsExisting.last();
						m_nExistingCount = m_rsExisting.getRow();
						// Second pass
						while ( rsPending.next() ) {
							doSecondPass(rsPending);
						}
					}
				}	
				
				// Set and remaining pending items to 'new'
				sTmp = String.format("UPDATE %s SET %s=%d WHERE (%s=%d AND %s=%d)",
					m_DB.getTableName(DBBase.TBLNAME_SRC),
					DBBase.SRCCOLN_STATUS, DBBase.SSTATUS_NEW,
					DBBase.SRCCOLN_DKEY, p_nDKey,
					DBBase.SRCCOLN_STATUS, DBBase.SSTATUS_PENDING);
				m_DB.doCommand(sTmp);
				
				// Remove all items to discard
				sTmp = String.format("DELETE FROM %s WHERE (%s=%d AND %s=%d)",
					m_DB.getTableName(DBBase.TBLNAME_SRC),
					DBBase.SRCCOLN_DKEY, p_nDKey,
					DBBase.SRCCOLN_STATUS, DBBase.SSTATUS_TOREMOVE);
				m_DB.doCommand(sTmp);
			}

			// Check if user stopped the task
			if ( !m_FA.getLog().canContinue() ) bContinue = false;

			// Make sure we stop if one of the DB call failed
			if ( m_DB.isBatchCanceled() ) bContinue = false;

			if ( bContinue && !m_Doc.isSourceTypeInternal() ) {
				m_DB.copyOriginalData(p_nDKey, DBBase.DIR_EXTDOCS, DBBase.DIR_SRCDOCS, null, true);
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
			try {
				if ( stmPending != null ) stmPending.close();
				if ( stmExisting != null ) stmExisting.close();
			}
			catch ( Exception E ) {
				m_FA.getLog().error(E.getLocalizedMessage());
			}
		}
		return bContinue;
	}

	private int doFirstPass (ResultSet p_RS)
		throws SQLException
	{
		int nNewStatus = -1;
		String sResname = p_RS.getString(DBBase.SRCCOLI_RESNAME);

		// No ID: wait for the second pass
		if (( sResname == null ) || ( sResname.length() == 0 )) return 0;

		// Search for Item(s) with similar resname
		//TODO: improve speed, by sorting both lists (?)
		int nFound = 0;
		int nFoundIndex = -1;
		for ( int i=1; i<=m_nExistingCount; i++ ) {
			m_rsExisting.absolute(i);
			if ( sResname.equals(m_rsExisting.getString(DBBase.SRCCOLI_RESNAME)) ) {
				nFound++; nFoundIndex = i;
				if ( nFound > 1 ) {
					// Second match: we stop here
					break;
				}
			}
			else if ( nFound > 0 ) {
				// A first match was found before, no second match
				break;
			}
		}

		if ( nFound == 0 ) {
			// No match found, new item or ID has changed
			// To process in the second pass
			return 0;
		}

		// Store the text
		String sText = p_RS.getString(DBBase.SRCCOLI_TEXT);

		if ( nFound == 1 ) { // Unique match
			m_rsExisting.absolute(nFoundIndex);
			// Is the text identical?
			if ( sText.equals(m_rsExisting.getString(DBBase.SRCCOLI_TEXT)) ) {
				copyData(p_RS, m_rsExisting, false);
				nNewStatus = DBBase.SSTATUS_SAME_SAME;
			}
			else {
				// Else: text has been changed
				copyData(p_RS, m_rsExisting, true);
				nNewStatus = DBBase.SSTATUS_SAME_MOD;
			}
			m_rsExisting.updateInt(DBBase.SRCCOLI_STATUS, nNewStatus);
			m_rsExisting.updateRow();
			p_RS.updateInt(DBBase.SRCCOLI_STATUS, DBBase.SSTATUS_TOREMOVE);
			p_RS.updateRow();
			return 1;
		}

		// Else, several matches found: Try to find the same text
		for ( int i=nFoundIndex; i<nFoundIndex+nFound; i++ ) {
			// Use the first text match if there is one
			if ( sText.equals(m_rsExisting.getString(DBBase.SRCCOLI_TEXT)) ) {
				// Guessed match based on text
				copyData(p_RS, m_rsExisting, false);
				m_rsExisting.updateInt(DBBase.SRCCOLI_STATUS, DBBase.SSTATUS_GUESSED_SAME);
				m_rsExisting.updateRow();
				p_RS.updateInt(DBBase.SRCCOLI_STATUS, DBBase.SSTATUS_TOREMOVE);
				p_RS.updateRow();
				return 1;
			}
		}

		// If no text matches: Pick the first item, and flag it as modified
		m_rsExisting.absolute(nFoundIndex);
		copyData(p_RS, m_rsExisting, true);
		m_rsExisting.updateInt(DBBase.SRCCOLI_STATUS, DBBase.SSTATUS_GUESSED_MOD);
		m_rsExisting.updateRow();
		p_RS.updateInt(DBBase.SRCCOLI_STATUS, DBBase.SSTATUS_TOREMOVE);
		p_RS.updateRow();
		return 1;
	}
	
	private void doSecondPass (ResultSet p_RS)
		throws SQLException
	{
		String sText = p_RS.getString(DBBase.SRCCOLI_TEXT);

		for ( int i=1; i<=m_nExistingCount; i++ ) {
			m_rsExisting.absolute(i);
			if ( m_rsExisting.getString(DBBase.SRCCOLI_TEXT).equals(sText) ) {
				copyData(p_RS, m_rsExisting, false);
				m_rsExisting.updateInt(DBBase.SRCCOLI_STATUS, DBBase.SSTATUS_GUESSED_SAME);
				m_rsExisting.updateRow();
				p_RS.updateInt(DBBase.SRCCOLI_STATUS, DBBase.SSTATUS_TOREMOVE);
				p_RS.updateRow();
				return;
			}
		}
	}
	
	private void copyData (ResultSet p_From,
		ResultSet p_To,
		boolean p_bCopyText)
		throws SQLException
	{
		p_To.updateInt(DBBase.SRCCOLI_GKEY, p_From.getInt(DBBase.SRCCOLI_GKEY));
		p_To.updateInt(DBBase.SRCCOLI_XKEY, p_From.getInt(DBBase.SRCCOLI_XKEY));
		p_To.updateInt(DBBase.SRCCOLI_START, p_From.getInt(DBBase.SRCCOLI_START));
		p_To.updateInt(DBBase.SRCCOLI_LENGTH, p_From.getInt(DBBase.SRCCOLI_LENGTH));

		String sTmp = p_From.getString(DBBase.SRCCOLI_RESTYPE);
		if ( sTmp != null ) p_To.updateString(DBBase.SRCCOLI_RESTYPE, sTmp);

		sTmp = p_From.getString(DBBase.SRCCOLI_COMMENT); 
		if ( sTmp != null ) p_To.updateString(DBBase.SRCCOLI_COMMENT, sTmp);

		if ( p_From.getInt(DBBase.SRCCOLI_MAXWIDTH) > 0 )
			p_To.updateInt(DBBase.SRCCOLI_MAXWIDTH, p_From.getInt(DBBase.SRCCOLI_MAXWIDTH));

		if ( p_From.getBoolean(DBBase.SRCCOLI_NOTRANS) )
			p_To.updateBoolean(DBBase.SRCCOLI_NOTRANS, true);

		if ( !p_bCopyText ) return;

		// Preserve previous text
		sTmp = p_To.getString(DBBase.SRCCOLI_TEXT);
		if ( sTmp != null ) p_To.updateString(DBBase.SRCCOLI_PREVTEXT, sTmp);

		// Copy text-related data
		sTmp = p_From.getString(DBBase.SRCCOLI_TEXT);
		if ( sTmp != null ) p_To.updateString(DBBase.SRCCOLI_TEXT, sTmp);
		
		sTmp = p_From.getString(DBBase.SRCCOLI_CODES);
		if ( sTmp != null ) p_To.updateString(DBBase.SRCCOLI_CODES, sTmp);
	}
}
