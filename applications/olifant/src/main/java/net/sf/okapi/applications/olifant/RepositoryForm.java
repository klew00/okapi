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

class RepositoryForm {
	
	private Shell shell;
	private Button rdDefaultLocal;
	private Button rdInMemory;
	private Button rdOtherLocal;
	private Button rdRemote;
	private String[] results = null;

	RepositoryForm (Shell parent) {
		shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText("Repository Selection");
		UIUtil.inheritIcon(shell, parent);
		shell.setLayout(new GridLayout());

		Group group = new Group(shell, SWT.NONE);
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
		group.setText("Available repositories:");
		
		rdDefaultLocal = new Button(group, SWT.RADIO);
		rdDefaultLocal.setText("Default local repository");
		
		rdInMemory = new Button(group, SWT.RADIO);
		rdInMemory.setText("In-Memory repository");
		
		rdOtherLocal = new Button(group, SWT.RADIO);
		rdOtherLocal.setText("Other local or network repository");
        rdOtherLocal.setEnabled(true); // for now

		rdRemote = new Button(group, SWT.RADIO);
		rdRemote.setText("Server-based repository");
rdRemote.setEnabled(false); // for now
		
		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( e.widget.getData().equals("o") ) { //$NON-NLS-1$
					if ( !saveData() ) return;
				}
				shell.close();
			};
		};
		OKCancelPanel pnlActions = new OKCancelPanel(shell, SWT.NONE, OKCancelActions, false);
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		pnlActions.setLayoutData(gdTmp);
		shell.setDefaultButton(pnlActions.btOK);

		shell.pack();
		Rectangle Rect = shell.getBounds();
		shell.setMinimumSize(Rect.width, Rect.height);
		Dialogs.centerWindow(shell, parent);
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
				res[0] = "d";
				res[1] = System.getProperty("user.home");
				res[1] = Util.ensureSeparator(res[1], false)+"Olifant"+File.separator+"defaultOlifantTMrepository";
			}
			else if ( rdInMemory.getSelection() ) {
				res[0] = "m";
			}
			else if ( rdOtherLocal.getSelection() ) {
				res[0] = "oL";
			}
			else {
				return false;
			}
			results = res;
		}
		catch ( Exception E ) {
			return false;
		}
		return true;
	}
}
