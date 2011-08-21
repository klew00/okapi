package net.sf.okapi.applications.olifant;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.lib.tmdb.IRecord;
import net.sf.okapi.lib.tmdb.ITm;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

class TmPanel extends Composite {

	private final static int KEYCOLUMNWIDTH = 80;
	
	private final Table table;
	private final EditorPanel editPanel;
	private ITm tm;
	private int currentEntry;
	private List<String> visibleFields;
	private StatusBar statusBar;

	public TmPanel (Composite parent,
		int flags,
		ITm tm,
		StatusBar statusBar)
	{
		super(parent, flags);
		this.tm = tm;
		this.statusBar = statusBar;

		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		setLayout(layout);
		setLayoutData(new GridData(GridData.FILL_BOTH));
		
		// Create the two main parts of the UI
		SashForm sashMain = new SashForm(this, SWT.VERTICAL);
		sashMain.setLayout(new GridLayout(1, false));
		sashMain.setLayoutData(new GridData(GridData.FILL_BOTH));
		sashMain.setSashWidth(4);
		
		// Edit panels
		editPanel = new EditorPanel(sashMain, SWT.VERTICAL);
		editPanel.clear();

		// Table
		table = new Table(sashMain, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION | SWT.CHECK | SWT.V_SCROLL);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		table.addControlListener(new ControlAdapter() {
		    public void controlResized(ControlEvent e) {
		    	try {
		    		table.setRedraw(false);
		    		Rectangle rect = table.getClientArea();
		    		int keyColWidth = table.getColumn(0).getWidth();
		    		int part = (int)((rect.width-keyColWidth) / (table.getColumnCount()-1));
		    		int remainder = (int)((rect.width-keyColWidth) % (table.getColumnCount()-1));
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
		
		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				saveEntry();
				updateCurrentEntry();
            }
		});

		// By default: all and only text fields are visible
		
		visibleFields = new ArrayList<String>();
		for ( String fn : tm.getAvailableFields() ) {
			if ( fn.startsWith("Text_") ) {
				visibleFields.add(fn);
			}
		}
		tm.setRecordFields(visibleFields);
		updateColumns();
		
		sashMain.setWeights(new int[]{1, 2});
	}

	private void updateColumns () {
		try {
			table.setRedraw(false);
			
			// Remove all columns
			while ( table.getColumnCount() > 0 ) {
			    table.getColumns()[0].dispose();
			}
			
			// Add the key column
			TableColumn col = new TableColumn(table, SWT.NONE);
			col.setText("Key");
			col.setWidth(KEYCOLUMNWIDTH);
			
			// Add the new ones
			for ( String fn : visibleFields ) {
				col = new TableColumn(table, SWT.NONE);
				col.setText(fn);
				col.setWidth(150);
			}
			
		}
		finally {
			table.setRedraw(true);
		}
		
	}
	
	@Override
	protected void finalize () {
		dispose();
	}

	public void dispose () {
		super.dispose();
	}

	public void updateCurrentEntry () {
		try {
			int n = table.getSelectionIndex();
			if ( n == -1 ) {
				editPanel.setFields(null, null);
			}
			else {
				IRecord rec = (IRecord)table.getItem(n).getData();
				editPanel.setFields(rec.get(0), rec.get(1));
			}
			currentEntry = n;
			statusBar.setCounter(n, table.getItemCount());
		}
		catch ( Throwable e ) {
			Dialogs.showError(getShell(), "Error while updating entry.\n"+e.getMessage(), null);
		}
	}
	
	public void saveEntry () {
		if ( currentEntry < 0 ) return;
		// Else: save the entry
		TableItem ti = table.getItem(currentEntry);
		if ( editPanel.isSourceModified() ) {
			IRecord rec = (IRecord)ti.getData();
			rec.set(0, editPanel.getSourceText());
			ti.setText(1, rec.get(0));
		}
		if ( editPanel.isTargetModified() ) {
			IRecord rec = (IRecord)ti.getData();
			rec.set(1, editPanel.getTargetText());
			ti.setText(2, rec.get(1));
		}
	}
	
	public void fillTable () {
		table.removeAll();
		currentEntry = -1;
		for ( IRecord rec : tm.getRecords() ) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(0, String.format("%d", rec.getKey()));
			item.setChecked(rec.getFlag());
			for ( int i=0; i<rec.size(); i++ ) {
				item.setText(i+1, rec.get(i)==null ? "" : rec.get(i));
			}
			item.setData(rec);
		}
		if ( table.getItemCount() > 0 ) {
			table.setSelection(0);
			updateCurrentEntry();
		}
	}
	
	public void toggleExtra () {
		editPanel.toggleExtra();
	}

	public EditorPanel getEditorPanel () {
		return editPanel;
	}
	
	public void setVisibleFields () {
		
	}

}
