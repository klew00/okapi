/*===========================================================================*/
/* Copyright (C) 2008 by the Okapi Framework contributors                    */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.common.ui.filters;

import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.UIUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * Dialog to define the options for the InlineCodeFinder object.
 */
public class InlineCodeFinderDialog {
	
	private Shell shell;
	private String help;
	private OKCancelPanel pnlActions;
	private InlineCodeFinderPanel pnlCodeFinder;
	private String result;

	/**
	 * Creates a new InlineCodeFinderDialog object.
	 * @param parent the parent shell for this dialog.
	 * @param captionText the caption of this dialog (can be null).
	 * @param helpFile the path of the help file.
	 */
	public InlineCodeFinderDialog (Shell parent,
		String captionText,
		String helpFile)
	{
		help = helpFile;
		shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		if ( captionText != null ) shell.setText(captionText);
		UIUtil.inheritIcon(shell, parent);
		shell.setLayout(new GridLayout());
		
		Composite cmpTmp = new Composite(shell, SWT.BORDER);
		cmpTmp.setLayout(new GridLayout());
		cmpTmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		pnlCodeFinder = new InlineCodeFinderPanel(cmpTmp, SWT.NONE);
		pnlCodeFinder.setLayoutData(new GridData(GridData.FILL_BOTH));

		//--- Dialog-level buttons

		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = null;
				if ( e.widget.getData().equals("h") ) { //$NON-NLS-1$
					UIUtil.start(help);
					return;
				}
				if ( e.widget.getData().equals("o") ) { //$NON-NLS-1$
					if ( !saveData() ) return;
				}
				shell.close();
			};
		};
		pnlActions = new OKCancelPanel(shell, SWT.NONE, OKCancelActions, (helpFile != null));
		pnlActions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		shell.setDefaultButton(pnlActions.btOK);

		shell.pack();
		shell.setMinimumSize(shell.getSize());
		Point startSize = shell.getMinimumSize();
		//if ( startSize.x < 450 ) startSize.x = 450;
		shell.setSize(startSize);
		Dialogs.centerWindow(shell, parent);
	}

	/**
	 * Opens this dialog. Make sure to call {@link #setData(String)} before
	 * opening the dialog.
	 * @return true if the edit was successful, false if an error occurred or
	 * if the user canceled the operation.
	 */
	public String showDialog () {
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
		return result;
	}

	/**
	 * Sets the rules to edit.
	 * @param codeFinderRules the string storage of the rules to edit.
	 */
	public void setData (String codeFinderRules) {
		pnlCodeFinder.setRules(codeFinderRules);
	}

	private boolean saveData () {
		result = pnlCodeFinder.getRules();
		return (result != null);
	}

}
