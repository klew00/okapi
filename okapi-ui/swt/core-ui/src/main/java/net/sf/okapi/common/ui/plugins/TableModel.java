/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.common.ui.plugins;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

class TableModel {
	
	private Table table;
	private boolean maxMode;

	TableModel (Table newTable,
		boolean maxMode)
	{
		table = newTable;
		this.maxMode = maxMode;
		TableColumn col = new TableColumn(table, SWT.NONE);
		col.setText("Name");
		
		if ( maxMode ) {
			col = new TableColumn(table, SWT.NONE);
			col.setText("Provider");
		}
		else {
			col = new TableColumn(table, SWT.NONE);
			col.setText("Locked?");
		}
		col.pack();
	}

	void updateTable (List<PluginInfo> list,
		List<String> lockedPlugins,
		int index)
	{
		table.removeAll();
		for ( PluginInfo info : list) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(0, info.getName());
			if ( maxMode ) {
				item.setText(1, info.getProvider());
			}
			else {
				if ( lockedPlugins.contains(info.getName()) ) {
					item.setText(1, "Locked");
				}
			}
			item.setData(info);
		}
		if ( table.getItemCount() > 0 ) {
			if ( index > -1 ) {
				if ( index > table.getItemCount()-1 ) {
					index = table.getItemCount()-1;
				}
			}
			else index = 0;
			table.setSelection(index);
		}
	}

}
