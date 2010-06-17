/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

package net.sf.okapi.lib.ui.verification;

import java.util.List;

import net.sf.okapi.lib.verification.Issue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

class IssuesTableModel {
	
	Table table;
	List<Issue> list;

	void linkTable (Table newTable) {
		table = newTable;
		TableColumn col = new TableColumn(table, SWT.NONE);
		col = new TableColumn(table, SWT.NONE);
		col.setText("TU");
		col = new TableColumn(table, SWT.NONE);
		col.setText("Seg");
		col = new TableColumn(table, SWT.NONE);
		col.setText("Description");
	}
	
	void setIssues (List<Issue> list) {
		this.list = list;
	}

	void updateTable (int selection) {
		table.removeAll();
		if ( list == null ) return;
		for ( Issue issue : list ) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setChecked(true);
			item.setText(1, issue.tuId);
			item.setText(2, (issue.segId == null ? "" : issue.segId));
			item.setText(3, issue.message);
		}
		
		if (( selection < 0 ) || ( selection > table.getItemCount()-1 )) {
			selection = table.getItemCount()-1;
		}
		if ( table.getItemCount() > 0 ) {
			table.setSelection(selection);
		}
	}

}
