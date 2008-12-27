/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
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
============================================================================*/

package net.sf.okapi.lib.ui.segmentation;

import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.UIUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class FileProcessingDialog {
	
	private Shell shell;
	private Text edInput;
	private Text edOutput;
	private String[] result = null;
	private String helpPath;

	public FileProcessingDialog (Shell parent,
		String helpPath)
	{
		this.helpPath = helpPath;
		shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText("Segment Test File");
		shell.setImage(parent.getImage());
		shell.setLayout(new GridLayout());
		
		Composite cmpTmp = new Composite(shell, SWT.BORDER);
		cmpTmp.setLayout(new GridLayout(2, false));
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		cmpTmp.setLayoutData(gdTmp);

		Label label = new Label(cmpTmp, SWT.NONE);
		label.setText("Full path of the input test file to segment:");
		gdTmp = new GridData();
		gdTmp.horizontalSpan = 2;
		label.setLayoutData(gdTmp);
		
		edInput = new Text(cmpTmp, SWT.BORDER | SWT.SINGLE);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		edInput.setLayoutData(gdTmp);
		
		Button btGetInput = new Button(cmpTmp, SWT.PUSH);
		btGetInput.setText("...");
		btGetInput.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String[] paths = Dialogs.browseFilenames(shell, "Input Test File", false,
					edInput.getText(), "Text Files (*.txt)\tAll Files (*.*)", "*.txt\t*.*");
				if ( paths == null ) return;
				edInput.setText(paths[0]);
				edInput.selectAll();
				edInput.setFocus();
			}
		});

		label = new Label(cmpTmp, SWT.NONE);
		label.setText("Full path of the output file:");
		gdTmp = new GridData();
		gdTmp.horizontalSpan = 2;
		label.setLayoutData(gdTmp);
		
		edOutput = new Text(cmpTmp, SWT.BORDER | SWT.SINGLE);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		edOutput.setLayoutData(gdTmp);

		Button btGetOutput = new Button(cmpTmp, SWT.PUSH);
		btGetOutput.setText("...");
		btGetOutput.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String path = Dialogs.browseFilenamesForSave(shell, "Output Test File",
					edInput.getText(), "Text Files (*.txt)\tAll Files (*.*)", "*.txt\t*.*");
				if ( path == null ) return;
				edOutput.setText(path);
				edOutput.selectAll();
				edOutput.setFocus();
			}
		});

		//--- Dialog-level buttons

		SelectionAdapter okCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = null;
				if ( e.widget.getData().equals("h") ) {
					callHelp();
					return;
				}
				if ( e.widget.getData().equals("o") ) {
					if ( !saveData() ) return;
				}
				shell.close();
			};
		};
		OKCancelPanel pnlActions = new OKCancelPanel(shell, SWT.NONE, okCancelActions, true);
		pnlActions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		shell.setDefaultButton(pnlActions.btOK);

		shell.pack();
		shell.setMinimumSize(shell.getSize());
		Point startSize = shell.getMinimumSize();
		if ( startSize.x < 600 ) startSize.x = 600;
		shell.setSize(startSize);
		Dialogs.centerWindow(shell, parent);
	}
	
	public String[] showDialog (String inputPath,
		String outputPath)
	{
		shell.open();
		if ( inputPath != null ) edInput.setText(inputPath);
		if ( outputPath != null ) edOutput.setText(outputPath);
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
		return result;
	}

	private boolean saveData () {
		try {
			result = null;
			if ( edInput.getText().length() == 0 ) {
				edInput.selectAll();
				edInput.setFocus();
				return false;
			}
			if ( edOutput.getText().length() == 0 ) {
				edOutput.selectAll();
				edOutput.setFocus();
				return false;
			}
			result = new String[2];
			result[0] = edInput.getText();
			result[1] = edOutput.getText();
			return true;
		}
		catch ( Exception e) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
			return false;
		}
	}

	public void callHelp () {
		if ( helpPath != null ) UIUtil.start(helpPath);
	}

}
