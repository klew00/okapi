package net.sf.okapi.applications.serval;

import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.lib.translation.QueryManager;
import net.sf.okapi.lib.translation.QueryResult;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class TableModel {

	private Table table;
	private GenericContent fmt;
	
	public TableModel () {
		fmt = new GenericContent();
	}
	
	void linkTable (Table newTable) {
		table = newTable;
		TableColumn col = new TableColumn(table, SWT.NONE);
		col.setText("Score");
		col = new TableColumn(table, SWT.NONE);
		col.setText("Origin");
		col = new TableColumn(table, SWT.NONE);
		col.setText("Source");
		col = new TableColumn(table, SWT.NONE);
		col.setText("Target");
	}

	void updateTable (QueryManager qm) {
		table.removeAll();
		QueryResult qr;
		while ( qm.hasNext() ) {
			qr = qm.next();
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(0, String.format("%d", qr.score));
			item.setText(1, qm.getName(qr.connectorId));
			item.setText(2, fmt.setContent(qr.source).toString(true));
			item.setText(3, fmt.setContent(qr.target).toString());
		}
		if ( table.getItemCount() > 0 ) {
			table.setSelection(0);
		}
	}
}
