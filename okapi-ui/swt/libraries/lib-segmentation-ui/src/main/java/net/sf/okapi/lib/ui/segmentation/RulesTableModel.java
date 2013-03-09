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

package net.sf.okapi.lib.ui.segmentation;

import java.util.ArrayList;

import net.sf.okapi.lib.segmentation.Rule;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

class RulesTableModel {
	
	Table table;
	ArrayList<Rule> list;

	void linkTable (Table newTable) {
		table = newTable;
		TableColumn col = new TableColumn(table, SWT.NONE);
		col.setText(Res.getString("ruleTable.type")); //$NON-NLS-1$
		col = new TableColumn(table, SWT.NONE);
		col.setText(Res.getString("ruleTable.beforeBreak")); //$NON-NLS-1$
		col = new TableColumn(table, SWT.NONE);
		col.setText(Res.getString("ruleTable.afterBreak")); //$NON-NLS-1$
	}
	
	void setLanguageRules (ArrayList<Rule> list) {
		this.list = list;
	}

	void updateTable (int selection) {
		table.removeAll();
		if ( list == null ) return;
		for ( Rule rule : list ) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setChecked(rule.isActive());
			item.setText(0, rule.isBreak() ? Res.getString("ruleTable.break") : Res.getString("ruleTable.noBreak")); //$NON-NLS-1$ //$NON-NLS-2$
			item.setText(1, rule.getBefore());
			item.setText(2, rule.getAfter());
		}
		
		if (( selection < 0 ) || ( selection > table.getItemCount()-1 )) {
			selection = table.getItemCount()-1;
		}
		if ( table.getItemCount() > 0 ) {
			table.setSelection(selection);
		}
	}

}
