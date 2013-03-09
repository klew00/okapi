/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.lib.ui.segmentation;

import java.io.File;

import net.sf.okapi.common.IHelp;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.UIUtil;
import net.sf.okapi.common.ui.fileupload.OkapiUploadPanel;

import org.eclipse.rwt.lifecycle.UICallBack;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.UploadPanel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class WebFileProcessingDialog {
	
	private Shell shell;
	//private Text edInput;
	//private Text edOutput;
	private OkapiUploadPanel uploadPanel;
	private String inputPath;
	private String outputPath;
	private Button chkHtmlOutput;
	private String[] result = null;
	private IHelp help;
	private boolean uploading;

	@SuppressWarnings("serial")
	public WebFileProcessingDialog (Shell parent,
		IHelp helpParam)
	{
		help = helpParam;
		shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText(Res.getString("testFileDlg.caption")); //$NON-NLS-1$
		UIUtil.inheritIcon(shell, parent);
		shell.setLayout(new GridLayout());
		
		Composite cmpTmp = new Composite(shell, SWT.BORDER);
		cmpTmp.setLayout(new GridLayout(1, false));
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		cmpTmp.setLayoutData(gdTmp);

		Label label = new Label(cmpTmp, SWT.NONE);
		label.setText(Res.getString("testFileDlg.inputPath")); //$NON-NLS-1$
//		label.setText("<a href=\"http://code.google.com/p/okapi/" target=\"_blank\">Okapi Framework</a>");
//		label.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
		gdTmp = new GridData();
		gdTmp.horizontalAlignment = SWT.FILL;
		gdTmp.grabExcessHorizontalSpace = true;
		gdTmp.horizontalSpan = 1;
		label.setLayoutData(gdTmp);
		
		uploadPanel = new OkapiUploadPanel(cmpTmp, UploadPanel.FULL);
		gdTmp = new GridData();
		gdTmp.horizontalAlignment = SWT.FILL;
		gdTmp.horizontalSpan = 1;
		uploadPanel.setLayoutData(gdTmp);
		
//		edInput = new Text(cmpTmp, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
//		gdTmp = new GridData(GridData.FILL_BOTH);
//		edInput.setLayoutData(gdTmp);
//		
//		final FileUpload browseButton = new FileUpload(cmpTmp, SWT.PUSH);
//		browseButton.setText("Browse..."); //$NON-NLS-1$
//		browseButton.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
//		browseButton.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
////				String[] paths = Dialogs.browseFilenames(shell, Res.getString("testFileDlg.getInputCaption"), false, //$NON-NLS-1$
////					null, Res.getString("testFileDlg.getInputFileTypes"), Res.getString("testFileDlg.getInputFilter")); //$NON-NLS-1$ //$NON-NLS-2$
////				if ( paths == null ) return;
////				inputPath = paths[0];
//				inputPath = browseButton.getFileName();
//				edInput.setText(Util.getFilename(inputPath, true));
////				edInput.selectAll();
////				edInput.setFocus();
//				updateOutputPath();
//			}
//		});

//		label = new Label(cmpTmp, SWT.NONE);
//		label.setText(Res.getString("testFileDlg.outputPath")); //$NON-NLS-1$
//		gdTmp = new GridData();
//		gdTmp.horizontalSpan = 2;
//		label.setLayoutData(gdTmp);
//		
//		edOutput = new Text(cmpTmp, SWT.BORDER | SWT.SINGLE);
//		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
//		edOutput.setLayoutData(gdTmp);
//
//		Button btGetOutput = new Button(cmpTmp, SWT.PUSH);
//		btGetOutput.setText("..."); //$NON-NLS-1$
//		btGetOutput.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				String path = Dialogs.browseFilenamesForSave(shell, Res.getString("testFileDlg.getOutputCaption"), //$NON-NLS-1$
//					null, null, 
//					Res.getString("testFileDlg.getOutputFileTypes"), Res.getString("testFileDlg.getOutputFilter")); //$NON-NLS-1$ //$NON-NLS-2$
//				if ( path == null ) return;
////				edOutput.setText(path);
////				edOutput.selectAll();
////				edOutput.setFocus();
//			}
//		});

		chkHtmlOutput = new Button(cmpTmp, SWT.CHECK);
		chkHtmlOutput.setText(Res.getString("testFileDlg.createHTML")); //$NON-NLS-1$
		gdTmp = new GridData();
		gdTmp.horizontalSpan = 2;
		chkHtmlOutput.setLayoutData(gdTmp);
		chkHtmlOutput.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateOutputPath();
			}
		});
		
		//--- Dialog-level buttons

		SelectionAdapter okCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = null;
				uploading = false;
				if ( e.widget.getData().equals("h") ) { //$NON-NLS-1$
					if ( help != null ) help.showWiki("Ratel - Test Segmentation on a File");
					return;
				}
				if ( e.widget.getData().equals("o") ) { //$NON-NLS-1$
					//if ( !saveData() ) return;
					uploading = uploadPanel.start();					
					return; // The form is closed by upload handler when finishes
				}
				shell.close();
			};
		};
		OKCancelPanel pnlActions = new OKCancelPanel(shell, SWT.NONE, okCancelActions, true);
		pnlActions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		shell.setDefaultButton(pnlActions.btOK);
		UICallBack.activate( WebFileProcessingDialog.class.getName() + hashCode() ); // !!!
		
		shell.addShellListener( new ShellAdapter() { // !!!
			private static final long serialVersionUID = 1L;
			@Override
		      public void shellClosed( ShellEvent e ) {
		        if (uploading) 
		        	saveData();
		        UICallBack.deactivate( WebFileProcessingDialog.class.getName() + hashCode() ); // !!!
		      }
		    } );

		shell.pack();
		shell.setMinimumSize(shell.getSize());
		Point startSize = shell.getMinimumSize();
		if ( startSize.x < 600 ) startSize.x = 600;
		shell.setSize(startSize);
		Dialogs.centerWindow(shell, parent);
	}
	
	public String[] showDialog (String inputPath,
		String outputPath,
		boolean htmlOutput)
	{		
		shell.open();
		this.inputPath = Util.isEmpty(inputPath) ? "" : inputPath;
		this.outputPath = Util.isEmpty(outputPath) ? "" : outputPath;			
		chkHtmlOutput.setSelection(htmlOutput);
		
		//edInput.setText(Util.getFilename(this.inputPath, true));
		updateOutputPath();
		
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}		
		return result;
	}

	private boolean saveData () {
		try {
			result = null;
//			if ( edInput.getText().length() == 0 ) {
//				edInput.selectAll();
//				edInput.setFocus();
//				return false;
//			}
//			if (Util.isEmpty(uploadPanel.getSelectedFilename())) {
//				return false;
//			}
//			if ( edOutput.getText().length() == 0 ) {
//				edOutput.selectAll();
//				edOutput.setFocus();
//				return false;
//			}					
			this.inputPath = uploadPanel.getFileName();
			updateOutputPath();
			result = new String[3];			
			result[0] = this.inputPath;
			result[1] = this.outputPath; //edOutput.getText();
			result[2] = (chkHtmlOutput.getSelection() ? "html" : null); //$NON-NLS-1$
			return true;
		}
		catch ( Exception e) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
			return false;
		}
	}

	private String makeHtmlOutputPath (String inputPath) {
		if (Util.isEmpty(inputPath)) return ""; //$NON-NLS-1$
		return inputPath + ".html"; //$NON-NLS-1$
	}
	
	private String makeNonHtmlOutputPath (String inputPath) {
		if (Util.isEmpty(inputPath)) return ""; //$NON-NLS-1$
		String ext = Util.getExtension(inputPath);
		String filename = Util.getFilename(inputPath, false);
		return Util.getDirectoryName(inputPath) + File.separator +
			filename + Res.getString("testFileDlg.outputExtension") + ext; //$NON-NLS-1$
	}
	
	private void updateOutputPath () {
		if ( chkHtmlOutput.getSelection() ) {
			//edOutput.setText(makeHtmlOutputPath(edInput.getText()));
			outputPath = makeHtmlOutputPath(inputPath);
		}
		else {
			//edOutput.setText(makeNonHtmlOutputPath(edInput.getText()));
			outputPath = makeNonHtmlOutputPath(inputPath);
		}
	}
}
