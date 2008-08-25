/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel                                          */
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

package net.sf.okapi.applications.rainbow.utilities.extraction;

import java.io.File;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import sf.okapi.lib.ui.segmentation.SRXEditor;

public class Editor implements IParametersEditor {
	
	private Shell                 shell;
	private boolean               result = false;
	private OKCancelPanel         pnlActions;
	private Parameters            params;
	private List                  lbTypes;
	private Text                  edDescription;
	private Text                  edName;
	private Text                  edOutputFolder;
	private Button                chkCreateZip;
	private Button                chkIncludeMergeData;
	private Text                  edSample;
	private boolean               inInit = true;
	private Button                chkPresegment;
	private Label                 stSourceSRX;
	private Text                  edSourceSRX;
	private Button                btGetSourceSRX;
	private Button                btEditSourceSRX;
	private Label                 stTargetSRX;
	private Text                  edTargetSRX;
	private Button                btGetTargetSRX;
	private Button                btEditTargetSRX;
	
	/**
	 * Invokes the editor for the options of the ExportPackage action.
	 * @param params The option object of the action.
	 * @param object The SWT Shell object of the parent shell in the UI.
	 */
	public boolean edit (IParameters params,
		Object object)
	{
		boolean bRes = false;
		try {
			shell = null;
			this.params = (Parameters)params;
			shell = new Shell((Shell)object, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
			create((Shell)object);
			return showDialog();
		}
		catch ( Exception e ) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
			bRes = false;
		}
		finally {
			// Dispose of the shell, but not of the display
			if ( shell != null ) shell.dispose();
		}
		return bRes;
	}
	
	public IParameters createParameters () {
		return new Parameters();
	}
	
	private void create (Shell parent)
	{
		shell.setText("Export Translation Package");
		if ( parent != null ) shell.setImage(parent.getImage());
		GridLayout layTmp = new GridLayout();
		layTmp.marginBottom = 0;
		layTmp.verticalSpacing = 0;
		shell.setLayout(layTmp);

		TabFolder tfTmp = new TabFolder(shell, SWT.NONE);
		tfTmp.setLayoutData(new GridData(GridData.FILL_BOTH));

		//--- Format tab

		Composite cmpTmp = new Composite(tfTmp, SWT.NONE);
		cmpTmp.setLayout(new GridLayout());
		TabItem tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("Package Format");
		tiTmp.setControl(cmpTmp);

		Label stTmp = new Label(cmpTmp, SWT.NONE);
		stTmp.setText("Type of package to create:");

		lbTypes = new List(cmpTmp, SWT.BORDER);
		lbTypes.add("Generic XLIFF");
		lbTypes.add("OmegaT");
		lbTypes.add("TagEditor TTX");
		lbTypes.add("Original + RTF layer");
		lbTypes.add("Test: Okapi-XML-Test");
		// To use neutral access to the list
		lbTypes.setData("xliff\tomegat\tttx\trtf\ttest");
		GridData gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.heightHint = 70;
		lbTypes.setLayoutData(gdTmp);
		
		edDescription = new Text(cmpTmp, SWT.BORDER | SWT.MULTI);
		edDescription.setEditable(false);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.heightHint = 60;
		edDescription.setLayoutData(gdTmp);

		//--- Name tab
		
		cmpTmp = new Composite(tfTmp, SWT.NONE);
		cmpTmp.setLayout(new GridLayout(2, false));
		tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("Package Location");
		tiTmp.setControl(cmpTmp);

		stTmp = new Label(cmpTmp, SWT.NONE);
		stTmp.setText("Root of the output folder:");
		gdTmp = new GridData();
		gdTmp.horizontalSpan = 2;
		stTmp.setLayoutData(gdTmp);
		
		edOutputFolder = new Text(cmpTmp, SWT.BORDER);
		edOutputFolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		edOutputFolder.addModifyListener(new ModifyListener () {
			public void modifyText(ModifyEvent e) {
				updateSample();
			}
		});

		Button btGetOutputFolder = new Button(cmpTmp, SWT.PUSH);
		btGetOutputFolder.setText("...");
		btGetOutputFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dlg = new DirectoryDialog(shell);
				dlg.setFilterPath(edOutputFolder.getText());
				String tmp = dlg.open();
				if ( tmp != null ) {
					edOutputFolder.setText(tmp);
					edOutputFolder.selectAll();
					edOutputFolder.setFocus();
				}
			}
		});
		
		stTmp = new Label(cmpTmp, SWT.NONE);
		stTmp.setText("Package name:");
		gdTmp = new GridData();
		gdTmp.horizontalSpan = 2;
		stTmp.setLayoutData(gdTmp);
		
		edName = new Text(cmpTmp, SWT.BORDER);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		edName.setLayoutData(gdTmp);
		edName.addModifyListener(new ModifyListener () {
			public void modifyText(ModifyEvent e) {
				updateSample();
			}
		});
		
		stTmp = new Label(cmpTmp, SWT.NONE);
		stTmp.setText("Full path of the directory name:");
		gdTmp = new GridData();
		gdTmp.horizontalSpan = 2;
		stTmp.setLayoutData(gdTmp);
		
		edSample = new Text(cmpTmp, SWT.BORDER);
		edSample.setEditable(false);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		edSample.setLayoutData(gdTmp);

		//--- Options tab
		
		cmpTmp = new Composite(tfTmp, SWT.NONE);
		cmpTmp.setLayout(new GridLayout());
		tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("Options");
		tiTmp.setControl(cmpTmp);

		chkCreateZip = new Button(cmpTmp, SWT.CHECK);
		chkCreateZip.setText("Compress the package into a ZIP file");

		chkIncludeMergeData = new Button(cmpTmp, SWT.CHECK);
		chkIncludeMergeData.setText("Includes the data to merge back the files");
		
		Group grpTmp = new Group(cmpTmp, SWT.NONE);
		grpTmp.setText("Segmentation");
		grpTmp.setLayout(new GridLayout(4, false));
		grpTmp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		chkPresegment = new Button(grpTmp, SWT.CHECK);
		chkPresegment.setText("Pre-segment the extracted text units");
		gdTmp = new GridData();
		gdTmp.horizontalSpan = 4;
		chkPresegment.setLayoutData(gdTmp);
		chkPresegment.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateSRX();
			}
		});

		stSourceSRX = new Label(grpTmp, SWT.NONE);
		stSourceSRX.setText("SRX file for the source:");
		
		edSourceSRX = new Text(grpTmp, SWT.BORDER);
		edSourceSRX.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		btGetSourceSRX = new Button(grpTmp, SWT.PUSH);
		btGetSourceSRX.setText("...");
		btGetSourceSRX.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getSRXFile(edSourceSRX);
			}
		});
		
		btEditSourceSRX = new Button(grpTmp, SWT.PUSH);
		btEditSourceSRX.setText("Edit...");
		btEditSourceSRX.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editSRXFile(edSourceSRX);
			}
		});
		
		stTargetSRX = new Label(grpTmp, SWT.NONE);
		stTargetSRX.setText("SRX file for the target:");
		
		edTargetSRX = new Text(grpTmp, SWT.BORDER);
		edTargetSRX.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		btGetTargetSRX = new Button(grpTmp, SWT.PUSH);
		btGetTargetSRX.setText("...");
		btGetTargetSRX.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getSRXFile(edTargetSRX);
			}
		});
		
		btEditTargetSRX = new Button(grpTmp, SWT.PUSH);
		btEditTargetSRX.setText("Edit...");
		btEditTargetSRX.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editSRXFile(edTargetSRX);
			}
		});
		

		//--- References tab
		
		tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("References");
		//TODO: Reference tab
		
		//--- Dialog-level buttons

		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = false;
				if ( e.widget.getData().equals("h") ) {
					//TODO: Call help
					return;
				}
				if ( e.widget.getData().equals("o") ) saveData();
				shell.close();
			};
		};
		pnlActions = new OKCancelPanel(shell, SWT.NONE, OKCancelActions, true);
		pnlActions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		shell.setDefaultButton(pnlActions.btOK);

		setData();
		inInit = false;
		shell.pack();
		shell.setMinimumSize(shell.getSize());
		Dialogs.centerWindow(shell, parent);
	}
	
	private void getSRXFile (Text edTextField) {
		String caption;
		if ( edTextField == edSourceSRX ) caption = "Select SRX for Source";
		else  caption = "Select SRX for Target";
		String[] paths = Dialogs.browseFilenames(shell, caption, false, null,
			"SRX Documents (*.srx)\tAll Files (*.*)",
			"*.srx\t*.*");
		if ( paths == null ) return;
		edTextField.setText(paths[0]);
		edTextField.selectAll();
		edTextField.setFocus();
	}
	
	private void editSRXFile (Text edTextField) {
		try {
			SRXEditor editor = new SRXEditor(shell);
			String path = edTextField.getText();
			if ( path.length() == 0 ) path = null;
			editor.showDialog(path);
			path = editor.getPath();
			if ( path != null ) {
				edTextField.setText(path); 
				edTextField.selectAll();
				edTextField.setFocus();
			}
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getMessage(), null);
		}
	}
	
	private void updateSRX () {
		boolean enabled = chkPresegment.getSelection();
		stSourceSRX.setEnabled(enabled);
		edSourceSRX.setEnabled(enabled);
		btGetSourceSRX.setEnabled(enabled);
		btEditSourceSRX.setEnabled(enabled);
		stTargetSRX.setEnabled(enabled);
		edTargetSRX.setEnabled(enabled);
		btGetTargetSRX.setEnabled(enabled);
		btEditTargetSRX.setEnabled(enabled);
	}
	
	private boolean showDialog () {
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
		return result;
	}

	private void setData () {
		int n = -1;
		String[] aItems = ((String)lbTypes.getData()).split("\t", -2);
		for ( int i=0; i<aItems.length; i++ ) {
			if ( aItems[i].equals(params.pkgType) ) {
				n = i;
				break;
			}
		}
		if ( n < 0 ) n = 0;
		lbTypes.setSelection(n);
		edOutputFolder.setText(params.outputFolder);
		edName.setText(params.pkgName);
		chkCreateZip.setSelection(params.createZip);
		chkPresegment.setSelection(params.presegment);
		edSourceSRX.setText(params.sourceSRX);
		edTargetSRX.setText(params.targetSRX);
		updateSRX();
		updateSample();
	}

	private boolean saveData () {
		if ( inInit ) return true;
		//m_Opt.setName("TODO:Name"); //TODO "m_edName.getText());
		String[] aItems = ((String)lbTypes.getData()).split("\t", -2);
		params.pkgType = aItems[lbTypes.getSelectionIndex()];
		params.createZip = chkCreateZip.getSelection();
		params.pkgName = edName.getText();
		params.outputFolder = edOutputFolder.getText();
		params.presegment = chkPresegment.getSelection();
		params.sourceSRX = edSourceSRX.getText();
		params.targetSRX = edTargetSRX.getText();
		result = true;
		return true;
	}
	
	private void updateSample () {
		saveData();
//TODO		String[] aRes = m_Opt.makePackageName("<ProjectID>", "<Lang>");
		edSample.setText(edOutputFolder.getText() + File.separator + edName.getText()); //TODO aRes[1]);
	}
}
