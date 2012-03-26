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

package net.sf.okapi.applications.olifant;

import java.util.List;

import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.UIUtil;
import net.sf.okapi.lib.tmdb.DbUtil;
import net.sf.okapi.lib.tmdb.IIndexAccess;
import net.sf.okapi.lib.tmdb.IRepository;
import net.sf.okapi.lib.tmdb.ITm;
import net.sf.okapi.lib.tmdb.lucene.OTmHit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

class QueryTMForm {
	
	private final Shell shell;
	private final ITm tm;
	private final Text edQuery;
	private final Button btQuery;
	private final Table table;
	private final Spinner spThreshold;

	public QueryTMForm (Shell parent,
		ITm tm)
	{
		shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText("Query TM");
		UIUtil.inheritIcon(shell, parent);
		shell.setLayout(new GridLayout(3, false));

		this.tm = tm;

		Label label = new Label(shell, SWT.NONE);
		label.setText("Source text to query:");
		GridData gdTmp = new GridData();
		gdTmp.horizontalSpan = 3;
		label.setLayoutData(gdTmp);
		
		edQuery = new Text(shell, SWT.BORDER);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 3;
		gdTmp.widthHint = 550;
		edQuery.setLayoutData(gdTmp);
		
		btQuery = UIUtil.createGridButton(shell, SWT.PUSH, "Search", UIUtil.BUTTON_DEFAULT_WIDTH, 1);
		btQuery.setText("Search");
		btQuery.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				search();
			}
		});
		
		label = new Label(shell,SWT.NONE);
		label.setText("Threshold:");
		
		spThreshold = new Spinner(shell, SWT.BORDER);
		spThreshold.setMaximum(100);
		spThreshold.setMinimum(1);
		spThreshold.setPageIncrement(10);
		spThreshold.setSelection(50);

		// Creates the table
		table = new Table(shell, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION | SWT.V_SCROLL);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.heightHint = 200;
		gdTmp.horizontalSpan = 3;
		table.setLayoutData(gdTmp);

		table.addControlListener(new ControlAdapter() {
		    public void controlResized(ControlEvent e) {
		    	try {
		    		table.setRedraw(false);
		    		Rectangle rect = table.getClientArea();
		    		int keyColWidth = table.getColumn(0).getWidth();
		    		int scoreColWidth = table.getColumn(1).getWidth();
		    		table.getColumn(2).setWidth(rect.width-(keyColWidth+scoreColWidth));
		    	}
		    	finally {
		    		table.setRedraw(true);
		    	}
		    }
		});
		
		// Create the table columns
		TableColumn col = new TableColumn(table, SWT.NONE);
		col.setText(DbUtil.SEGKEY_NAME);
		col.setWidth(90);
		col = new TableColumn(table, SWT.NONE);
		col.setText("Score");
		col.setWidth(80);
		col = new TableColumn(table, SWT.NONE);
		col.setText("Text");
		col.setWidth(200);

		shell.setDefaultButton(btQuery);
		
		shell.pack();
		Rectangle Rect = shell.getBounds();
		shell.setMinimumSize(Rect.width, Rect.height);
		Dialogs.centerWindow(shell, parent);
		
	}

	void showDialog () {
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
	}

	private void search () {
		try {
			String text = edQuery.getText();
			if ( text.trim().isEmpty() ) {
				Dialogs.showError(shell, "You must enter a text to query.", null);
				edQuery.setFocus();
				return;
			}
			
			btQuery.setEnabled(false);
			table.removeAll();
			
			IRepository repo = tm.getRepository();
			IIndexAccess ia = repo.getIndexAccess();

			int count = ia.search(text, spThreshold.getSelection(), 20, tm.getUUID());
			if ( count == 0 ) {
				TableItem ti = new TableItem(table, SWT.NONE);
				ti.setText(2, "<No match found>");
				return;
			}
			
			// Else: fill the table
			List<OTmHit> res = ia.getHits();
			for ( OTmHit hit : res ) {
				TableItem ti = new TableItem(table, SWT.NONE);
				ti.setText(hit.getSegKey());
				ti.setText(1, String.format("%f", hit.getScore()));
				ti.setText(2, hit.getTu().toString());
			}
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, "Error when searching:\n"+e.getMessage(), null);
		}
		finally {
			btQuery.setEnabled(true);
		}
	}
}
