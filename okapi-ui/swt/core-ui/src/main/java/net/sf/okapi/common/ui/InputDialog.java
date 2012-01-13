/*===========================================================================
  Copyright (C) 2008-2012 by the Okapi Framework contributors
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Simple multi-purpose input dialog box.
 */
public class InputDialog {
	
	private Shell shell;
	private Text edField;
	private String result = null;
	private String help;
	private OKCancelPanel pnlActions;
	private boolean allowEmptyValue = false;
	private Font customFont;

	/**
	 * Creates a simple input dialog with one text field.
	 * @param parent parent shell of the dialog.
	 * @param captionText title of the dialog (can be null).
	 * @param labelText label of the text field (must be set).
	 * @param defaultInputText default input text (can be null).
	 * @param helpFile path to the help file (can be null).
	 * @param buttonOptions indicates if a browse button should be set:
	 * 0=none, 1=directory browser.
	 * @param multiline height hint in pixel if the field is to be multiline,
	 * -1 otherwise.
	 * @param maximum width hint in pixel, or -1 to allow any size.
	 */
	public InputDialog (Shell parent,
		String captionText,
		String labelText,
		String defaultInputText,
		String helpFile,
		int buttonOptions,
		int multiline,
		int maxWidth)
	{
		help = helpFile;
		shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		if ( captionText != null ) shell.setText(captionText);
		UIUtil.inheritIcon(shell, parent);
		shell.setLayout(new GridLayout());
		
		Composite cmpTmp = new Composite(shell, SWT.BORDER);
		cmpTmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layTmp = new GridLayout((buttonOptions>0) ? 2 : 1, false);
		cmpTmp.setLayout(layTmp);

		Label label = new Label(cmpTmp, SWT.NONE);
		label.setText(labelText);
		GridData gdTmp;
		if ( buttonOptions > 0 ) {
			gdTmp = new GridData();
			gdTmp.horizontalSpan = 2;
			label.setLayoutData(gdTmp);
		}
		
		int opt = SWT.BORDER | SWT.SINGLE;
		if ( multiline > 0 ) opt = SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL;
		edField = new Text(cmpTmp, opt);
		gdTmp = new GridData(GridData.FILL_BOTH);
		if ( buttonOptions == 0 ) {
			gdTmp.horizontalSpan = 2;
		}
		else {
			Button btGet = new Button(cmpTmp, SWT.PUSH);
			btGet.setText("...");
			btGet.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					getFolder();
				};
			});
		}
		if ( multiline > 0 ) gdTmp.heightHint = multiline;
		edField.setLayoutData(gdTmp);
		
		//--- Dialog-level buttons

		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = null;
				if ( e.widget.getData().equals("h") ) {
					UIUtil.start(help);
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
		shell.setDefaultButton(pnlActions.btOK);

		shell.pack();
		Point size = shell.getSize();
		if ( maxWidth > -1 ) {
			if ( size.x > maxWidth ) size.x = maxWidth;
		}
		shell.setMinimumSize(size);
		if ( size.x < 450 ) size.x = 450;

		if ( defaultInputText != null ) {
			edField.setText(defaultInputText);
		}
		
		shell.setSize(size);
		Dialogs.centerWindow(shell, parent);
	}

	/**
	 * Calls the dialog.
	 * @return the value entered by the user, or null if the operation was canceled.
	 */
	public String showDialog () {
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
		dispose();
		return result;
	}
	
	private void dispose () {
		if ( customFont != null ) {
			customFont.dispose();
			customFont = null;
		}
	}

	/**
	 * Increases or decreases the size of the text for this input text.
	 * @param change the change to apply, e.g. +2 or -2.
	 */
	public void changeFontSize (int change) {
		Font font = edField.getFont();
		Device device = font.getDevice();
		FontData[] fontData = font.getFontData();
		fontData[0].setHeight(fontData[0].getHeight()+change);
		Font tmpFont = customFont;
		customFont = new Font(device, fontData[0]);
		edField.setFont(customFont);
		if ( tmpFont != null ) {
			tmpFont.dispose();
		}
	}
	
	/**
	 * Sets the default input value.
	 * @param value the default input value.
	 */
	public void setInputValue (String value) {
		if ( value == null ) edField.setText("");
		else edField.setText(value);
	}
	
	/**
	 * Indicates whether or not this dialog should allow empty values.
	 * @param value true to allow empty values, false otherwise.
	 */
	public void setAllowEmptyValue (boolean value) {
		allowEmptyValue = value;
	}

	/**
	 * Indicates whether or not the this dialog should be in read-only mode.
	 * In read-only mode, the "OK" button is not enabled. 
	 * @param value true for read-only mode, false otherwise.
	 */
	public void setReadOnly (boolean value) {
		pnlActions.btOK.setEnabled(!value);
	}

	private void getFolder () {
		try {
			DirectoryDialog dlg = new DirectoryDialog(shell);
			dlg.setFilterPath(edField.getText());
			String dir = dlg.open();
			if (  dir == null ) return;
			edField.setText(dir);
			edField.selectAll();
			edField.setFocus();
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
		}
	}
	
	private boolean saveData () {
		result = edField.getText();
		if ( result.length() == 0 ) {
			if ( allowEmptyValue ) return true;
			edField.selectAll();
			edField.setFocus();
			return false;
		}
		return true;
	}

}
