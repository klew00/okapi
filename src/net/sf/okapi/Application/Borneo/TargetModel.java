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

class TargetModel {

	static final int         COL_KEY        = 0;
	static final int         COL_LANG       = 1;
	static final int         COL_DKEY       = 2;
	static final int         COL_SKEY       = 3;
	static final int         COL_GKEY       = 4;
	static final int         COL_XKEY       = 5;
	static final int         COL_FLAG       = 6;
	static final int         COL_STATUS     = 7;
	static final int         COL_RESNAME    = 8;
	static final int         COL_RESTYPE    = 9;
	static final int         COL_STEXT      = 10;
	static final int         COL_TTEXT      = 11;
	static final int         COL_SCODES     = 12;
	static final int         COL_TMP        = 13;
	
	private TableColumn[]    m_aCols;
	private DBBase           m_DB;
	private ResultSet        m_rsItems;
	private Table            m_Tbl;
	private ResourceManager  m_RM;
	
	void linkTable (Table p_Table,
		ResourceManager p_RM)
	{
		m_Tbl = p_Table;
		m_RM = p_RM;
		TableColumn colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("Key"); //DBBase.TRGCOLN_KEY);
	
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("Lang"); //DBBase.TRGCOLN_LANG);
	
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("DKey"); //DBBase.TRGCOLN_DKEY);
	
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("SKey"); //DBBase.TRGCOLN_SKEY);
	
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("GKey"); //DBBase.TRGCOLN_GKEY);
	
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("XKey"); //DBBase.TRGCOLN_XKEY);
	
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("Flag"); //DBBase.TRGCOLN_FLAG);
	
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("Status"); //DBBase.TRGCOLN_STATUS);
	
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("Resname"); //DBBase.TRGCOLN_RESNAME);
	
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("Restype"); //DBBase.TRGCOLN_RESTYPE);
	
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("SText"); //DBBase.TRGCOLN_STEXT);
	
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("TText"); //DBBase.TRGCOLN_TTEXT);
	
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("SCodes"); //DBBase.TRGCOLN_SCODES);
	
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("Tmp"); //DBBase.TRGCOLN_TMP);

		m_aCols = p_Table.getColumns();
		for ( int i=0; i<m_aCols.length; i++ ) {
			m_aCols[i].pack();
		}
	}
	
	void setDB (DBBase p_DB) {
		m_DB = p_DB;
	}
	
	void updateTable (int p_nDKey,
		String p_sLangCode)
		throws SQLException
	{
		m_rsItems = m_DB.fetchTargetItems(p_nDKey, p_sLangCode);
		m_Tbl.removeAll();
		int n = 1;
		while ( m_rsItems.next() ) {
			TableItem TI = new TableItem(m_Tbl, SWT.NONE);
			if ( ((++n) % 2) == 0 ) TI.setBackground(m_RM.getColor("TrgLightBG"));
			for ( int i=0; i<m_aCols.length; i++ ) {
				switch ( i ) {
					case COL_STATUS:
						TI.setText(i, MainForm.s_aTrgStatus[m_rsItems.getInt(i+1)]);
						TI.setBackground(i, m_RM.getColor(MainForm.TSTATUSBASE+m_rsItems.getInt(i+1)));
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
