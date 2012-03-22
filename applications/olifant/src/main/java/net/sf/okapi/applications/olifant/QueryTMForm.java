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
import net.sf.okapi.lib.tmdb.IIndexAccess;
import net.sf.okapi.lib.tmdb.IRepository;
import net.sf.okapi.lib.tmdb.ITm;
import net.sf.okapi.lib.tmdb.lucene.OTmHit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

class QueryTMForm {
	
	private final Shell shell;
	private final ITm tm;
	private final Text edQuery;
	private final Button btQuery;

	public QueryTMForm (Shell parent,
		ITm tm)
	{
		shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText("Query TM (TEMPORARY)");
		UIUtil.inheritIcon(shell, parent);
		shell.setLayout(new GridLayout(1, false));

		this.tm = tm;

		Label label = new Label(shell, SWT.NONE);
		label.setText("Source text to query:");
		
		edQuery = new Text(shell, SWT.BORDER);
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.widthHint = 300;
		edQuery.setLayoutData(gdTmp);
		
		btQuery = UIUtil.createGridButton(shell, SWT.PUSH, "Search", UIUtil.BUTTON_DEFAULT_WIDTH, 1);
		btQuery.setText("Search");
		btQuery.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				search();
			}
		});

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
			
			IRepository repo = tm.getRepository();
			IIndexAccess ia = repo.getIndexAccess();
			
			String msg = "No match found.";
			if ( ia.search(text) > 0 ) {
				List<OTmHit> res = ia.getHits();
				msg = String.format("Best match (%f) = segId %s", res.get(0).getScore(), res.get(0).getSegKey());
			}
			
			MessageBox dlg = new MessageBox(shell, SWT.ICON_INFORMATION);
			dlg.setMessage(msg);
			dlg.setText("Query results");
			dlg.open();
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, "Error when searching:\n"+e.getMessage(), null);
		}
		finally {
			btQuery.setEnabled(true);
		}
	}
}
