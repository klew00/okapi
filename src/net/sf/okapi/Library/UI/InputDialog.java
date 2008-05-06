/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel (at ENLASO Corporation)                  */
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

package net.sf.okapi.Library.UI;


import net.sf.okapi.Library.UI.Dialogs;
import net.sf.okapi.Library.UI.OKCancelPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class InputDialog {
	
	private Shell            shell;
	private Text             edField;
	private String           result = null;
	private String           helpFile;
	private OKCancelPanel    pnlActions;

	/**
	 * Creates a simple input dialog with one text field.
	 * @param p_Parent Parent shell of the dialog.
	 * @param captionText Title of the dialog (can be null).
	 * @param labelText Label of the text field (must be set).
	 * @param defaultInputText Default input text (can be null).
	 * @param helpFile Path to the help file (can be null).
	 */
	public InputDialog (Shell p_Parent,
		String captionText,
		String labelText,
		String defaultInputText,
		String helpFile)
	{
		this.helpFile = helpFile;
		shell = new Shell(p_Parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		if ( captionText != null ) shell.setText(captionText);
		shell.setImage(p_Parent.getImage());
		shell.setLayout(new GridLayout());
		
		Composite cmpTmp = new Composite(shell, SWT.BORDER);
		cmpTmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layTmp = new GridLayout();
		cmpTmp.setLayout(layTmp);

		Label label = new Label(cmpTmp, SWT.NONE);
		label.setText(labelText);
		
		edField = new Text(cmpTmp, SWT.BORDER | SWT.SINGLE);
		if ( defaultInputText != null ) edField.setText(defaultInputText);
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.widthHint = 640; // No larger than this by default
		edField.setLayoutData(gdTmp);
		
		//--- Dialog-level buttons

		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = null;
				if ( e.widget.getData().equals("h") ) {
					//TODO: Call help
					return;
				}
				if ( e.widget.getData().equals("o") ) {
					if ( !saveData() ) return;
				}
				shell.close();
			};
		};
		pnlActions = new OKCancelPanel(shell, SWT.NONE, OKCancelActions, (helpFile != null));
		pnlActions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		shell.setDefaultButton(pnlActions.m_btOK);

		shell.pack();
		shell.setMinimumSize(shell.getSize());
		Dialogs.centerWindow(shell, p_Parent);
	}
	
	public String showDialog () {
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
		return result;
	}

	public void setInputValue (String value) {
		edField.setText(value);
	}
	
	private boolean saveData () {
		try {
			result = edField.getText();
			if ( result.length() == 0 ) {
				edField.selectAll();
				edField.setFocus();
				return false;
			}
		}
		catch ( Exception E ) {
			return false;
		}
		return true;
	}
}
