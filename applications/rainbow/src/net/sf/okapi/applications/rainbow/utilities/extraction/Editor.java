/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel and Okapi Framework contributors         */
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

import net.sf.okapi.applications.rainbow.lib.SegmentationPanel;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
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
	private SegmentationPanel     pnlSegmentation;
	private boolean               inInit = true;
	
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
		shell.setText("Translation Package Creation");
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
		grpTmp.setLayout(new GridLayout());
		grpTmp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		pnlSegmentation = new SegmentationPanel(grpTmp, SWT.NONE,
			"Pre-segment the extracted text with the following rules:");
		pnlSegmentation.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
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

		shell.pack();
		shell.setMinimumSize(shell.getSize());
		Point startSize = shell.getMinimumSize();
		if ( startSize.x < 600 ) startSize.x = 600;
		shell.setSize(startSize);
		setData();
		inInit = false;
		Dialogs.centerWindow(shell, parent);
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
		pnlSegmentation.setData(params.presegment, params.sourceSRX, params.targetSRX);
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
		params.presegment = pnlSegmentation.getSegment();
		params.sourceSRX = pnlSegmentation.getSourceSRX();
		params.targetSRX = pnlSegmentation.getTargetSRX();
		result = true;
		return true;
	}
	
	private void updateSample () {
		saveData();
//TODO		String[] aRes = m_Opt.makePackageName("<ProjectID>", "<Lang>");
		edSample.setText(edOutputFolder.getText() + File.separator + edName.getText()); //TODO aRes[1]);
	}
}
