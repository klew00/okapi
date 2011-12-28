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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

class GoToForm {
	
	private final Shell shell;
	private final Button rdEntryKey;
	private final Text edValue;
	private final Button rdPage;
	private final Spinner spPage;
	
	private Object[] result;

	GoToForm (Shell parent,
		long pageCount)
	{
		shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText("Go To");
		UIUtil.inheritIcon(shell, parent);
		shell.setLayout(new GridLayout(1, false));

		Group group = new Group(shell, SWT.NONE);
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		rdEntryKey = new Button(group, SWT.RADIO);
		rdEntryKey.setText("Go to the segment with the key:");
		rdEntryKey.setSelection(true);
		rdEntryKey.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent (Event e) {
				updateFields();
			}
		});
		
		edValue = new Text(group, SWT.BORDER);
		edValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		rdPage = new Button(group, SWT.RADIO);
		rdPage.setText("Got to the page:");
		rdPage.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent (Event e) {
				updateFields();
			}
		});
		
		spPage = new Spinner(group, SWT.BORDER);
		spPage.setMinimum(1);
		spPage.setMaximum((int)pageCount);
		
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
		gdTmp.horizontalSpan = 3;
		pnlActions.setLayoutData(gdTmp);
		shell.setDefaultButton(pnlActions.btOK);
		
		updateFields();

		shell.pack();
		Rectangle Rect = shell.getBounds();
		shell.setMinimumSize(Rect.width, Rect.height);
		Dialogs.centerWindow(shell, parent);
	}

	Object[] showDialog () {
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
		return result;
	}

	private boolean saveData () {
		long value = -1;
		char type = 'e';
		
		if ( rdEntryKey.getSelection() ) {
			// Check the value
			String tmp = edValue.getText().trim();
			if ( tmp.isEmpty() ) {
				Dialogs.showError(shell, "You must specify a key value.", null);
				edValue.setFocus();
				return false;
			}
			try {
				value = Long.parseLong(tmp);
			}
			catch ( NumberFormatException e ) {
				Dialogs.showError(shell, "Invalid key value.", null);
				edValue.setFocus();
				return false;
			}
		}
		else {
			type = 'p';
			value = spPage.getSelection()-1;
		}
		
		result = new Object[2];
		result[0] = type;
		result[1] = value;
		return true;
	}

	private void updateFields () {
		edValue.setEnabled(rdEntryKey.getSelection());
		spPage.setEnabled(rdPage.getSelection());
	}
}
