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

package net.sf.okapi.Application.Borneo;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.sf.okapi.Borneo.Core.DBBase;
import net.sf.okapi.Library.UI.ResourceManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

class SourceModel {

	static final int         COL_KEY        = 0;
	static final int         COL_DKEY       = 1;
	static final int         COL_GKEY       = 2;
	static final int         COL_XKEY       = 3;
	static final int         COL_FLAG       = 4;
	static final int         COL_RESNAME    = 5;
	static final int         COL_RESTYPE    = 6;
	static final int         COL_NOTRANS    = 7;
	static final int         COL_STATUS     = 8;
	static final int         COL_TEXT       = 9;
	static final int         COL_CODES      = 10;
	static final int         COL_COMMENT    = 11;
	static final int         COL_START      = 12;
	static final int         COL_LENGTH     = 13;
	static final int         COL_MAXWIDTH   = 14;
	static final int         COL_PREVTEXT   = 15;

	private TableColumn[]    m_aCols;
	private DBBase           m_DB;
	private ResultSet        m_rsItems;
	private Table            m_Tbl;
	private ResourceManager  m_RM;
	
	void linkTable (Table p_Table,
		ResourceManager p_RM) {
		m_Tbl = p_Table;
		m_RM = p_RM;
		TableColumn colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("Key"); //DBBase.SRCCOLN_KEY);
	
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("DKey"); //DBBase.SRCCOLN_DKEY);
	
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("GKey"); //DBBase.SRCCOLN_GKEY);
	
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("XKey"); //DBBase.SRCCOLN_XKEY);
	
		colTmp = new TableColumn(p_Table, SWT.CENTER);
		colTmp.setText("Flag"); //DBBase.SRCCOLN_FLAG);
	
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("Resname"); //DBBase.SRCCOLN_RESNAME);
	
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("Restype"); //DBBase.SRCCOLN_RESTYPE);
	
		colTmp = new TableColumn(p_Table, SWT.CENTER);
		colTmp.setText("N/T"); //DBBase.SRCCOLN_NOTRANS);
	
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("Status"); //DBBase.SRCCOLN_STATUS);
	
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("Text"); //DBBase.SRCCOLN_TEXT);
	
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("Codes"); //DBBase.SRCCOLN_CODES);
	
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("Comment"); //DBBase.SRCCOLN_COMMENT);
	
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("Start"); //DBBase.SRCCOLN_STATE);
	
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("Length"); //DBBase.SRCCOLN_LENGTH);
	
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("Max"); //DBBase.SRCCOLN_MAXWIDTH);
	
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("Previous Text"); //DBBase.SRCCOLN_PREVTEXT);

		m_aCols = p_Table.getColumns();
		for ( int i=0; i<m_aCols.length; i++ ) {
			m_aCols[i].pack();
		}
	}
	
	void setDB (DBBase p_DB) {
		m_DB = p_DB;
	}
	
	void updateTable (int p_nDKey,
		boolean p_bPendingItems)
		throws SQLException
	{
		m_rsItems = m_DB.fetchSourceItems(p_nDKey, p_bPendingItems);
		m_Tbl.removeAll();
		int n = 1;
		while ( m_rsItems.next() ) {
			TableItem TI = new TableItem(m_Tbl, SWT.NONE);
			if ( ((++n) % 2) == 0 ) TI.setBackground(m_RM.getColor("SrcLightBG"));
			for ( int i=0; i<m_aCols.length; i++ ) {
				switch ( i ) {
					case COL_FLAG:
						TI.setText(i, m_rsItems.getBoolean(i+1) ? "X" : "");
						break;
					case COL_NOTRANS:
						TI.setText(i, m_rsItems.getBoolean(i+1) ? "NT" : "");
						break;
					case COL_STATUS:
						TI.setText(i, MainForm.s_aSrcStatus[m_rsItems.getInt(i+1)]);
						TI.setBackground(i, m_RM.getColor(m_rsItems.getInt(i+1)));
						break;
					default:
						if ( m_rsItems.getString(i+1) != null )
							TI.setText(i, m_rsItems.getString(i+1));
						break;
				}
			}
		}
	}
	
}
