/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package net.sf.okapi.filters.transifex.ui;

import java.util.List;

import net.sf.okapi.filters.transifex.Project;
import net.sf.okapi.lib.transifex.ResourceInfo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

class ProjectTableModel {
	
	private Table table;
	private Project project;

	public void linkTable (Table newTable) {
		table = newTable;
		TableColumn col = new TableColumn(table, SWT.NONE);
		col.setText("Resource to Process");
		col = new TableColumn(table, SWT.NONE);
		col.setText("Resource ID");
	}
	
	public void setProject (Project project) {
		this.project = project;
		updateTable(null, 0);
	}

	void updateTable (int[] selection,
		int index)
	{
		table.removeAll();
		for ( ResourceInfo info : project.getResources() ) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(0, info.getName());
			item.setText(1, info.getId());
			item.setChecked(info.getSelected());
		}
		if ( selection == null ) {
			if ( table.getItemCount() > 0 ) {
				if ( index > -1 ) {
					if ( index > table.getItemCount()-1 ) {
						index = table.getItemCount()-1;
					}
				}
				else index = 0;
				table.setSelection(index);
			}
			// Else: nothing to select	
		}
		else table.setSelection(selection);
	}

	public void saveData () {
		ResourceInfo info;
		List<ResourceInfo> list = project.getResources();
		for ( int i=0; i<table.getItemCount(); i++ ) {
			info = list.get(i);
			info.setSelected(table.getItem(i).getChecked());
		}
	}
}
