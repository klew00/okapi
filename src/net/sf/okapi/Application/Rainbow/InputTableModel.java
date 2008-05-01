package net.sf.okapi.Application.Rainbow;

import java.sql.SQLException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
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
		if ( selection != null ) 
			table.setSelection(selection);
	}

}
