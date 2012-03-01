/*===========================================================================
  Copyright (C) 2012 by the Okapi Framework contributors
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

package net.sf.okapi.filters.drupal.ui;

import java.util.List;

import net.sf.okapi.filters.drupal.NodeInfo;
import net.sf.okapi.filters.drupal.Project;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

class ProjectTableModel {
	
	private Table table;
	private Project project;

	public void initializeTable (Table newTable) {
		table = newTable;
		TableColumn col = new TableColumn(table, SWT.NONE);
		col.setText("ID");
		col.setWidth(100);
		col = new TableColumn(table, SWT.NONE);
		col.setText("Title");
		col = new TableColumn(table, SWT.NONE);
		col.setText("Type");
		col = new TableColumn(table, SWT.NONE);
		col.setText("Status");
		
		table.addControlListener(new ControlAdapter() {
		    public void controlResized(ControlEvent e) {
		    	int count = table.getColumnCount()-1; // Exclude ID column
		    	try {
		    		table.setRedraw(false);
		    		Rectangle rect = table.getClientArea();
		    		int keyColWidth = table.getColumn(0).getWidth();
		    		int part = (int)((rect.width-keyColWidth) / count);
		    		int remainder = (int)((rect.width-keyColWidth) % count);
		    		for ( int i=1; i<table.getColumnCount(); i++ ) {
		    			table.getColumn(i).setWidth(part);
		    		}
		    		table.getColumn(1).setWidth(table.getColumn(1).getWidth()+remainder);
		    	}
		    	finally {
		    		table.setRedraw(true);
		    	}
		    }
		});
		
		
	}
	
	public void setProject (Project project) {
		this.project = project;
		updateTable(null, 0);
	}

	void updateTable (int[] selection,
		int index)
	{
		table.removeAll();
		for ( NodeInfo info : project.getEntries() ) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(0, info.getNid());
			item.setChecked(info.getSelected());
			item.setText(1, info.getTitle());
			item.setText(2, info.getType());
			String status = info.getStatus();
			item.setText(3, status.isEmpty() ? "" : status.equals("0") ? "unpublished" : "published");
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
		NodeInfo info;
		List<NodeInfo> list = project.getEntries();
		for ( int i=0; i<table.getItemCount(); i++ ) {
			info = list.get(i);
			info.setSelected(table.getItem(i).getChecked());
		}
	}

}
