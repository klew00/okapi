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

package net.sf.okapi.common.ui;

import net.sf.okapi.common.IWaitDialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * Default implementation of the IWaitDialog interface.
 */
public class WaitDialog implements IWaitDialog {

	private Shell shell;
	private int result;

	public WaitDialog () {
		// Need for runtime creation
	}
	
	private void createDialog (String caption,
		String text,
		String okLabel)
	{
		// Take the opportunity to do some clean up if possible
		Runtime rt = Runtime.getRuntime();
		rt.runFinalization();
		rt.gc();

		Shell parent = null;
		shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText(caption);
		shell.setLayout(new GridLayout());
			
		Composite cmpTmp = new Composite(shell, SWT.BORDER);
		cmpTmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layTmp = new GridLayout(2, false);
		cmpTmp.setLayout(layTmp);

		Label label = new Label(cmpTmp, SWT.NONE);
		label.setText(text);
		GridData gdTmp = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.GRAB_HORIZONTAL
			| GridData.VERTICAL_ALIGN_CENTER | GridData.GRAB_VERTICAL);
		label.setLayoutData(gdTmp);

		//--- Dialog-level buttons

		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = 0;
				if ( e.widget.getData().equals("h") ) {
					return;
				}
				if ( e.widget.getData().equals("o") ) {
					result = 1;
				}
				shell.close();
			};
		};

		OKCancelPanel pnlActionsDialog = new OKCancelPanel(shell, SWT.NONE, OKCancelActions, false);
		pnlActionsDialog.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		pnlActionsDialog.setOKText(okLabel);
		shell.setDefaultButton(pnlActionsDialog.btOK);
		
		shell.pack();
		shell.setMinimumSize(shell.getSize());
		Point startSize = shell.getMinimumSize();
		if ( startSize.x < 350 ) startSize.x = 350;
		shell.setSize(startSize);
		Dialogs.centerWindow(shell, parent);
	}

	@Override
	public int waitForUserInput (String message,
		String okLabel) {
		result = 0;
		createDialog("Waiting User Input", message, okLabel);
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
		return result;
	}

}
