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

package net.sf.okapi.applications.rainbow;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

class InputTableModel {
	
	Table               table;
	ArrayList<Input>    inputList;

	void linkTable (Table newTable) {
		table = newTable;
		TableColumn col = new TableColumn(table, SWT.NONE);
		col.setText(Res.getString("INPTAB_RELPATH")); //$NON-NLS-1$
		col = new TableColumn(table, SWT.NONE);
		col.setText(Res.getString("INPTAB_FSETTINGS")); //$NON-NLS-1$
	}

	void setProject (ArrayList<Input> inputList) {
		this.inputList = inputList;
	}
	
	/**
	 * Refresh the items in the table, and optionally, select some of them.
	 * @param selection The list of the indices of the items to select after refresh,
	 * or null to use the specified index.
	 * @param index The index to select.
	 */
	void updateTable (int[] selection,
		int index)
	{
		table.removeAll();
		for ( Input inp : inputList ) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(0, inp.relativePath);
			item.setText(1, inp.filterConfigId);
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

}
