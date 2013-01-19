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

/**
 * Internal class used by FilterConfigurationPanel.
 */
class FilterConfigurationsTableModel {

	static final int ID_COLINDEX = 1;
	
	private Table table;
	private IFilterConfigurationMapper mapper;

	void linkTable (Table newTable) {
		table = newTable;
		TableColumn col = new TableColumn(table, SWT.NONE);
		col.setText(Res.getString("FilterConfigurationsTableModel.name")); //$NON-NLS-1$
		col = new TableColumn(table, SWT.NONE);
		col.setText(Res.getString("FilterConfigurationsTableModel.id")); //$NON-NLS-1$
		col = new TableColumn(table, SWT.NONE);
		col.setText(Res.getString("FilterConfigurationsTableModel.mimeType")); //$NON-NLS-1$
		col = new TableColumn(table, SWT.NONE);
		col.setText(Res.getString("FilterConfigurationsTableModel.custom")); //$NON-NLS-1$
	}
	
	void setMapper (IFilterConfigurationMapper mapper) {
		this.mapper = mapper;
	}

	/**
	 * Refill the table with the configurations in the mapper.
	 * The method also tries to select the provided configuration identifier.
	 * @param selection index of the configuration to select (if selectedConfigId
	 * is null or not found). If the value is out of range the last configuration
	 * is selected. 
	 * @param selectedConfigId identifier of the configuration to select, or zero
	 * to select by index. If the configuration is not found, the index selection
	 * is used instead.
	 */
	void updateTable (int selection,
		String selectedConfigId)
	{
		table.removeAll();
		if ( mapper == null ) return;
		Iterator<FilterConfiguration> iter = mapper.getAllConfigurations();
		FilterConfiguration config;
		int i = 0;
		while ( iter.hasNext() ) {
			config = iter.next();
			if ( selectedConfigId != null ) {
				if ( selectedConfigId.equals(config.configId) ) {
					selection = i;
				}
			}
			i++;
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(0, config.name);
			item.setText(ID_COLINDEX, config.configId);
			item.setText(2, config.mimeType);
			item.setText(3, config.custom ? Res.getString("FilterConfigurationsTableModel.customFlag") : Res.getString("FilterConfigurationsTableModel.predefinedFlag")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (( selection < 0 ) || ( selection > table.getItemCount()-1 )) {
			selection = table.getItemCount()-1;
		}
		if ( table.getItemCount() > 0 ) {
			table.setSelection(selection);
		}
	}

}
