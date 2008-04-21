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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

//TODO: Class should not be public
public class DocumentsModel {

	public static final int       COL_KEY        = 0;
	public static final int       COL_SRCTYPE    = 1;
	public static final int       COL_STATUS     = 2;
	public static final int       COL_SUBDIR     = 3;
	public static final int       COL_FILENAME   = 4;
	public static final int       COL_FSETTINGS  = 5;
	public static final int       COL_ENCODING   = 6;
	public static final int       COL_FILESET    = 7;
	public static final int       COL_TARGETS    = 8;

	private TableColumn[]    m_aCols;
	private DBBase           m_DB;
	private ResultSet        m_rsDocs;
	private Table            m_Tbl;
	
	public void linkTable (Table p_Table) {
		m_Tbl = p_Table;
		TableColumn colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("DKey"); //DBBase.DOCCOLN_KEY);
	
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("Source Type"); //DBBase.DOCCOLN_SRCTYPE);
		
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("Status"); //DBBase.DOCCOLN_STATUS);
		
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("Sub-directory"); //DBBase.DOCCOLN_SUBDIR);
		
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("Filename"); //DBBase.DOCCOLN_FILENAME);
		
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("Filter Settings"); //DBBase.DOCCOLN_FSETTINGS);
		
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("Encoding"); //DBBase.DOCCOLN_ENCODING);
		
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("File Set"); //DBBase.DOCCOLN_FILESET);
		
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("Targets"); //DBBase.DOCCOLN_TARGETS);

		m_aCols = p_Table.getColumns();
		for ( int i=0; i<m_aCols.length; i++ ) {
			m_aCols[i].pack();
		}
	}
	
	void setDB (DBBase p_DB) {
		m_DB = p_DB;
	}
	
	void updateTable ()
		throws SQLException
	{
		m_rsDocs = m_DB.fetchDocuments(true);
		m_Tbl.removeAll();
		while ( m_rsDocs.next() ) {
			TableItem TI = new TableItem(m_Tbl, SWT.NONE);
			for ( int i=0; i<m_aCols.length; i++ ) {
				if ( m_rsDocs.getString(i+1) != null )
					TI.setText(i, m_rsDocs.getString(i+1));
			}
		}
	}
	
	void updateDB ()
		throws Exception
	{
		m_DB.saveDocuments(m_Tbl.getItems());
	}
}
