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

package net.sf.okapi.common.ui.filters;

import java.util.Iterator;

import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

class FilterConfigurationsTableModel {

	static final int ID_COLINDEX = 0;
	
	private Table table;
	private IFilterConfigurationMapper mapper;

	void linkTable (Table newTable) {
		table = newTable;
		TableColumn col = new TableColumn(table, SWT.NONE);
		col.setText("ID");
		col = new TableColumn(table, SWT.NONE);
		col.setText("Name");
		col = new TableColumn(table, SWT.NONE);
		col.setText("MIME Type");
		col = new TableColumn(table, SWT.NONE);
		col.setText("Custom");
	}
	
	void setMapper (IFilterConfigurationMapper mapper) {
		this.mapper = mapper;
	}

	void updateTable (int selection) {
		table.removeAll();
		if ( mapper == null ) return;
		Iterator<FilterConfiguration> iter = mapper.getAllConfigurations();
		FilterConfiguration config;
		while ( iter.hasNext() ) {
			config = iter.next();
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(ID_COLINDEX, config.configId);
			item.setText(1, config.name);
			item.setText(2, config.mimeType);
			item.setText(3, config.custom ? "Custom" : "Pre-defined");
		}
		if (( selection < 0 ) || ( selection > table.getItemCount()-1 )) {
			selection = table.getItemCount()-1;
		}
		if ( table.getItemCount() > 0 ) {
			table.setSelection(selection);
		}
	}

}
