/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.steps.ui.segmentation;

import net.sf.okapi.common.IContext;
import net.sf.okapi.common.IHelp;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.UIUtil;
import net.sf.okapi.lib.ui.segmentation.SRXEditor;
import net.sf.okapi.steps.segmentation.Parameters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class ParametersEditor implements IParametersEditor {

	private Shell shell;
	private boolean result = false;
	private OKCancelPanel pnlActions;
	private Parameters params;
	private Button chkSegmentSource;
	private Button btGetSourceSRX;
	private Button btEditSourceSRX;
	private Button chkSegmentTarget;
	private Button btGetTargetSRX;
	private Button btEditTargetSRX;
	private Text edSourceSRX;
	private Text edTargetSRX;
	private IHelp help;
	private String projectDir;
	
	/**
	 * Invokes the editor for the options of the ExportPackage action.
	 * @param params The option object of the action.
	 * @param object The SWT Shell object of the parent shell in the UI.
	 */
	public boolean edit (IParameters params,
		IContext context)
	{
		boolean bRes = false;
		try {
			this.projectDir = context.getString("projDir");
			help = (IHelp)context.getObject("help");
			this.params = (Parameters)params;
			shell = new Shell((Shell)context.getObject("shell"), SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
			create((Shell)context.getObject("shell"));
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
		shell.setText("Segmentation");
		if ( parent != null ) UIUtil.inheritIcon(shell, parent);
		GridLayout layTmp = new GridLayout();
		layTmp.marginBottom = 0;
		layTmp.verticalSpacing = 0;
		shell.setLayout(layTmp);

		TabFolder tfTmp = new TabFolder(shell, SWT.NONE);
		tfTmp.setLayoutData(new GridData(GridData.FILL_BOTH));

		//--- Options tab

		Composite cmpTmp = new Composite(tfTmp, SWT.NONE);
		cmpTmp.setLayout(new GridLayout(3, false));
		TabItem tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("Options");
		tiTmp.setControl(cmpTmp);

		chkSegmentSource = new Button(cmpTmp, SWT.CHECK);
		chkSegmentSource.setText("Segment the source text using the following SRX rules:");
		GridData gdTmp = new GridData();
		gdTmp.horizontalSpan = 3;
		chkSegmentSource.setLayoutData(gdTmp);
		chkSegmentSource.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateSourceDisplay();
			}
		});

		edSourceSRX = new Text(cmpTmp, SWT.BORDER);
		edSourceSRX.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		
		btGetSourceSRX = new Button(cmpTmp, SWT.PUSH);
		btGetSourceSRX.setText("...");
		btGetSourceSRX.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getSRXFile(edSourceSRX);
			}
		});
		
		btEditSourceSRX = new Button(cmpTmp, SWT.PUSH);
		btEditSourceSRX.setText("Edit...");
		btEditSourceSRX.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editSRXFile(edSourceSRX);
			}
		});
		
		chkSegmentTarget = new Button(cmpTmp, SWT.CHECK);
		chkSegmentTarget.setText("Segment existing target text using the following SRX rules:");
		gdTmp = new GridData();
		gdTmp.horizontalSpan = 3;
		chkSegmentTarget.setLayoutData(gdTmp);
		chkSegmentTarget.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateTargetDisplay();
			}
		});

		edTargetSRX = new Text(cmpTmp, SWT.BORDER);
		edTargetSRX.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

		btGetTargetSRX = new Button(cmpTmp, SWT.PUSH);
		btGetTargetSRX.setText("...");
		btGetTargetSRX.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getSRXFile(edTargetSRX);
			}
		});
		
		btEditTargetSRX = new Button(cmpTmp, SWT.PUSH);
		btEditTargetSRX.setText("Edit...");
		btEditTargetSRX.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editSRXFile(edTargetSRX);
			}
		});
		
		//--- Dialog-level buttons

		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = false;
				if ( e.widget.getData().equals("h") ) {
					if ( help != null ) help.showTopic(this, "index");
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

	private void getSRXFile (Text edTextField) {
		String caption;
		if ( edTextField == edSourceSRX ) caption = "Select SRX for Source";
		else  caption = "Select SRX for Target";
		String[] paths = Dialogs.browseFilenames(shell, caption, false, null,
			"SRX Documents (*.srx)\tAll Files (*.*)",
			"*.srx\t*.*");
		if ( paths == null ) return;
		UIUtil.checkProjectFolderAfterPick(paths[0], edTextField, projectDir);
	}
	
	private void editSRXFile (Text edTextField) {
		try {
			SRXEditor editor = new SRXEditor(shell, true, help);
			String oriPath = edTextField.getText();
			if ( projectDir != null ) {
				oriPath = oriPath.replace("${ProjDir}", projectDir);
			}
			if ( oriPath.length() == 0 ) oriPath = null;
			editor.showDialog(oriPath);
			String newPath = editor.getPath();
			UIUtil.checkProjectFolderAfterPick(newPath, edTextField, projectDir);
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getMessage(), null);
		}
	}
	
	private void updateSourceDisplay () {
		edSourceSRX.setEnabled(chkSegmentSource.getSelection());
		btEditSourceSRX.setEnabled(chkSegmentSource.getSelection());
		btGetSourceSRX.setEnabled(chkSegmentSource.getSelection());
	}
	
	private void updateTargetDisplay () {
		edTargetSRX.setEnabled(chkSegmentTarget.getSelection());
		btEditTargetSRX.setEnabled(chkSegmentTarget.getSelection());
		btGetTargetSRX.setEnabled(chkSegmentTarget.getSelection());
	}
	
	private void setData () {
		chkSegmentSource.setSelection(params.segmentSource);
		edSourceSRX.setText(params.sourceSrxPath);
		chkSegmentTarget.setSelection(params.segmentTarget);
		edTargetSRX.setText(params.targetSrxPath);
		updateSourceDisplay();
		updateTargetDisplay();
	}

	private boolean saveData () {
		params.segmentSource = chkSegmentSource.getSelection();
		if ( params.segmentSource ) {
			params.sourceSrxPath = edSourceSRX.getText();
		}
		params.segmentTarget = chkSegmentTarget.getSelection();
		if ( params.segmentTarget ) {
			params.targetSrxPath = edTargetSRX.getText();
		}
		result = true;
		return true;
	}
	
}
