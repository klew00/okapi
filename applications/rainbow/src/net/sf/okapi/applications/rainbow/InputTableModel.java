/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel (at ENLASO Corporation)                  */
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

package net.sf.okapi.applications.rainbow;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

class InputTableModel {
	
	Table               table;
	Project             prj;

	void linkTable (Table newTable) {
		table = newTable;
		TableColumn col = new TableColumn(table, SWT.NONE);
		col.setText("Path Relative to the Root");
		col = new TableColumn(table, SWT.NONE);
		col.setText("Filter Settings");
	}

	void setProject (Project newProject) {
		prj = newProject;
	}
	
	void updateTable (int[] selection) {
		table.removeAll();
		for ( Input inp : prj.inputList ) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(0, inp.relativePath);
			item.setText(1, inp.filterSettings);
		}
		if ( selection == null ) {
			if ( table.getItemCount() > 0 )
				table.setSelection(0);
		}
		else table.setSelection(selection);
	}

}
