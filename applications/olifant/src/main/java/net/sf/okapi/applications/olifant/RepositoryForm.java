/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

import java.io.File;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.TextAndBrowsePanel;
import net.sf.okapi.common.ui.UIUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

class RepositoryForm {

	public static final String REPOTYPE_INMEMORY = "M";
	public static final String REPOTYPE_DEFAULTLOCAL = "L";
	public static final String REPOTYPE_OTHERLOCALORNETWORK = "O";
	public static final String REPOTYPE_SERVER = "S";
	
	private final Shell shell;
	private final Button rdDefaultLocal;
	private final Text edDefaultLocal;
	private final Button rdInMemory;
	private final Text edInMemory;
	private final Button rdOtherLocal;
	private final TextAndBrowsePanel pnlOtherLocal;
	private final Button rdServerBased;
	private final Text edServerBased;
	private final String defaultLocalname;

	private String[] results = null;

	RepositoryForm (Shell parent) {
		shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText("Repository Selection");
		UIUtil.inheritIcon(shell, parent);
		shell.setLayout(new GridLayout());
		
		Group group = new Group(shell, SWT.NONE);
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		rdDefaultLocal = new Button(group, SWT.RADIO);
		rdDefaultLocal.setText("Default local repository");
		rdDefaultLocal.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				updateDisplay();
            }
		});
		
		defaultLocalname = Util.ensureSeparator(System.getProperty("user.home"), false)
			+ "Olifant" + File.separator + "defaultOlifantTMRepository";
		edDefaultLocal = new Text(group, SWT.BORDER);
		edDefaultLocal.setEditable(false);
		edDefaultLocal.setText(defaultLocalname);
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		final int indent = 16;
		gdTmp.horizontalIndent = indent;
		edDefaultLocal.setLayoutData(gdTmp);
		
		rdOtherLocal = new Button(group, SWT.RADIO);
		rdOtherLocal.setText("Other local or network repository");
        rdOtherLocal.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				updateDisplay();
            }
		});

		pnlOtherLocal = new TextAndBrowsePanel(group, SWT.NONE, false);
		pnlOtherLocal.setTitle("Select the repository file");
		pnlOtherLocal.setBrowseFilters("TM Repositories (*.h2.db)\tAll Files (*.*)", "*.h2.db\t*.*");
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalIndent = indent;
		gdTmp.widthHint = 500;
		pnlOtherLocal.setLayoutData(gdTmp);

		rdServerBased = new Button(group, SWT.RADIO);
		rdServerBased.setText("Server-based repository");
		rdServerBased.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				updateDisplay();
            }
		});
		
		edServerBased = new Text(group, SWT.BORDER);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalIndent = indent;
		edServerBased.setLayoutData(gdTmp);
		
		rdInMemory = new Button(group, SWT.RADIO);
		rdInMemory.setText("Memory-based repository");
		rdInMemory.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				updateDisplay();
            }
		});
		
		edInMemory = new Text(group, SWT.NONE);
		edInMemory.setEditable(false);
		edInMemory.setText("(No physical storage: all data are in memory)");
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalIndent = indent;
		edInMemory.setLayoutData(gdTmp);

		updateDisplay();

		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( e.widget.getData().equals("o") ) { //$NON-NLS-1$
					if ( !saveData() ) return;
				}
				shell.close();
			};
		};
		OKCancelPanel pnlActions = new OKCancelPanel(shell, SWT.NONE, OKCancelActions, false);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		pnlActions.setLayoutData(gdTmp);
		shell.setDefaultButton(pnlActions.btOK);

		shell.pack();
		Rectangle Rect = shell.getBounds();
		shell.setMinimumSize(Rect.width, Rect.height);
		Dialogs.centerWindow(shell, parent);
	}
	
	private void updateDisplay () {
		edInMemory.setEnabled(rdInMemory.getSelection());
		edDefaultLocal.setEnabled(rdDefaultLocal.getSelection());
		pnlOtherLocal.setEnabled(rdOtherLocal.getSelection());
		edServerBased.setEnabled(rdServerBased.getSelection());
	}
	
	String[] showDialog () {
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
		return results;
	}

	private boolean saveData () {
		try {
			String[] res = new String[2];
			if ( rdDefaultLocal.getSelection() ) {
				res[0] = REPOTYPE_DEFAULTLOCAL;
				res[1] = defaultLocalname;
			}
			else if ( rdInMemory.getSelection() ) {
				res[0] = REPOTYPE_INMEMORY;
			}
			else if ( rdServerBased.getSelection() ) {
				res[0] = REPOTYPE_SERVER;
			}
			else if ( rdOtherLocal.getSelection() ) {
				String path = pnlOtherLocal.getText().trim();
				if ( path.isEmpty() ) {
					Dialogs.showError(shell, "You must specify a database file.", null);
					pnlOtherLocal.setFocus();
					return false;
				}
				res[0] = REPOTYPE_OTHERLOCALORNETWORK;
				res[1] = path;
			}
			results = res;
		}
		catch ( Exception e ) {
			Dialogs.showError(shell, e.getMessage(), null);
			return false;
		}
		return true;
	}
}
