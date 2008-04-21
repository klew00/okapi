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

import java.util.Enumeration;
import java.util.Hashtable;

import net.sf.okapi.Borneo.Core.DBTargetDoc;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

class DocumentTargetsModel {

	static final int         COL_LANG       = 0;
	static final int         COL_STATUS     = 1;

	private Table            m_Tbl;
	
	void linkTable (Table p_Table) {
		m_Tbl = p_Table;

		TableColumn colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("Lang");
		colTmp.setWidth(140);
	
		colTmp = new TableColumn(p_Table, SWT.NONE);
		colTmp.setText("Status");
		colTmp.setWidth(340);
	}
	
	void updateTable (Hashtable<String, DBTargetDoc> p_Targets) {
		m_Tbl.removeAll();
		if ( p_Targets == null ) return;
		Enumeration<String> E = p_Targets.keys();
		while ( E.hasMoreElements() ) {
			String sLang = E.nextElement();
			TableItem TI = new TableItem(m_Tbl, SWT.NONE);
			TI.setText(0, sLang);
			TI.setText(1, p_Targets.get(sLang).getStatus());
		}
		if ( m_Tbl.getItemCount() > 0 ) {
			m_Tbl.setSelection(0);
		}
	}
	
}
