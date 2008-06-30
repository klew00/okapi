package sf.okapi.lib.ui.segmentation;

import java.util.ArrayList;

import net.sf.okapi.lib.segmentation.Rule;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

class RulesTableModel {
	
	Table               table;
	ArrayList<Rule>     list;


	void linkTable (Table newTable) {
		table = newTable;
		TableColumn col = new TableColumn(table, SWT.NONE);
		col.setText("Type");
		col = new TableColumn(table, SWT.NONE);
		col.setText("Before Break");
		col = new TableColumn(table, SWT.NONE);
		col.setText("After Break");
	}
	
	void setLanguageRules (ArrayList<Rule> list) {
		this.list = list;
	}

	void updateTable () {
		table.removeAll();
		if ( list == null ) return;
		for ( Rule rule : list ) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(0, rule.isBreak() ? "Break" : "No-Break");
			item.setText(1, rule.getBefore());
			item.setText(2, rule.getAfter());
		}
		table.setSelection(0);
	}

}
