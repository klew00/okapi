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

package net.sf.okapi.applications.rainbow.utilities.alignment;

import net.sf.okapi.applications.rainbow.lib.SegmentationPanel;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.UIUtil;
import net.sf.okapi.tm.simpletm.Database;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class Editor implements IParametersEditor {
	
	private Shell                 shell;
	private boolean               result = false;
	private OKCancelPanel         pnlActions;
	private Parameters            params;
	private Text                  edTMXPath;
	private Button                chkCreateTM;
	private Text                  edTMPath;
	private Button                chkUseTradosWorkarounds;
	private Button                btGetTMPath;
	private Button                chkCheckSingleSegUnit;
	private Button                chkUseAutoCorrection;
	private SegmentationPanel     pnlSegmentation;
	private boolean               inInit = true;

	/**
	 * Invokes the editor for the parameters of this utility.
	 * @param p_Options The option object of the action.
	 * @param p_Object The SWT Shell object of the parent shell in the UI.
	 */
	public boolean edit (IParameters p_Options,
		Object p_Object)
	{
		boolean bRes = false;
		try {
			shell = null;
			params = (Parameters)p_Options;
			shell = new Shell((Shell)p_Object, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
			create((Shell)p_Object);
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
	
	private void create (Shell parent) {
		shell.setText("ID-Based Alignment");
		if ( parent != null ) shell.setImage(parent.getImage());
		GridLayout layTmp = new GridLayout();
		layTmp.marginBottom = 0;
		layTmp.verticalSpacing = 0;
		shell.setLayout(layTmp);

		TabFolder tfTmp = new TabFolder(shell, SWT.NONE);
		tfTmp.setLayoutData(new GridData(GridData.FILL_BOTH));

		//--- Main tab
		
		Composite cmpTmp = new Composite(tfTmp, SWT.NONE);
		cmpTmp.setLayout(new GridLayout(2, false));
		TabItem tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("Options");
		tiTmp.setControl(cmpTmp);

		Label stTmp = new Label(cmpTmp, SWT.NONE);
		stTmp.setText("Full path of the TMX document to generate:");
		GridData gdTmp = new GridData();
		gdTmp.horizontalSpan = 2;
		stTmp.setLayoutData(gdTmp);
		
		edTMXPath = new Text(cmpTmp, SWT.BORDER);
		edTMXPath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button btTmp = new Button(cmpTmp, SWT.PUSH);
		btTmp.setText("...");
		btTmp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String path = Dialogs.browseFilenamesForSave(shell, "TMX File", null,
					"TMX Documents (*.tmx)\tAll Files (*.*)",
					"*.tmx\t*.*");
				if ( path == null ) return;
				edTMXPath.setText(path);
				edTMXPath.selectAll();
				edTMXPath.setFocus();
			}
		});
		
		chkCreateTM = new Button(cmpTmp, SWT.CHECK);
		chkCreateTM.setText("Create a translation memory with the following path:");
		gdTmp = new GridData();
		gdTmp.horizontalSpan = 2;
		chkCreateTM.setLayoutData(gdTmp);
		chkCreateTM.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				edTMPath.setEnabled(chkCreateTM.getSelection());
				btGetTMPath.setEnabled(chkCreateTM.getSelection());
			}
		});
		
		edTMPath = new Text(cmpTmp, SWT.BORDER);
		edTMPath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		btGetTMPath = new Button(cmpTmp, SWT.PUSH);
		btGetTMPath.setText("...");
		btGetTMPath.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String path = Dialogs.browseFilenamesForSave(shell, "Simple TM File", null,
					"Simple TMs (*"+Database.DATAFILE_EXT+")\tAll Files (*.*)",
					"*"+Database.DATAFILE_EXT+"\t*.*");
				if ( path == null ) return;
				if ( path.endsWith(Database.DATAFILE_EXT) ) {
					path = path.substring(0, path.length()-Database.DATAFILE_EXT.length());
				}
				edTMPath.setText(path);
				edTMPath.selectAll();
				edTMPath.setFocus();
			}
		});
		
		chkUseTradosWorkarounds = new Button(cmpTmp, SWT.CHECK);
		chkUseTradosWorkarounds.setText("Generate Trados workarounds");
		gdTmp = new GridData();
		gdTmp.horizontalSpan = 2;
		chkUseTradosWorkarounds.setLayoutData(gdTmp);
		
		Group grpTmp = new Group(cmpTmp, SWT.NONE);
		grpTmp.setText("Segmentation");
		grpTmp.setLayout(new GridLayout());
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		grpTmp.setLayoutData(gdTmp);
		
		pnlSegmentation = new SegmentationPanel(grpTmp, SWT.NONE,
			"Segment the extracted text using the following SRX rules:");
		pnlSegmentation.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		chkCheckSingleSegUnit = UIUtil.createGridButton(cmpTmp, SWT.CHECK,
			"Verify in-line codes for text units with a single segment", -1, 2);
		chkUseAutoCorrection = UIUtil.createGridButton(cmpTmp, SWT.CHECK,
			"Use auto-correction automatically", -1, 2);
		
		//--- Dialog-level buttons

		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = false;
				if ( e.widget.getData().equals("h") ) {
					//TODO: Call help
					return;
				}
				if ( e.widget.getData().equals("o") ) {
					if ( !saveData() ) return;
					else result = true;
				}
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
		edTMXPath.setText(params.tmxPath);
		chkCreateTM.setSelection(params.createTM);
		edTMPath.setText(params.tmPath);
		chkUseTradosWorkarounds.setSelection(params.useTradosWorkarounds);
		pnlSegmentation.setData(params.segment, params.sourceSrxPath, params.targetSrxPath);
		chkCheckSingleSegUnit.setSelection(params.checkSingleSegUnit);
		chkUseAutoCorrection.setSelection(params.useAutoCorrection);
		edTMPath.setEnabled(chkCreateTM.getSelection());
		btGetTMPath.setEnabled(chkCreateTM.getSelection());
	}

	private boolean saveData () {
		if ( inInit ) return true;
		if ( edTMXPath.getText().length() == 0 ) {
			Dialogs.showError(shell, "You must specify an output file.", null);
			edTMXPath.setFocus();
			return false;
		}
		params.tmxPath = edTMXPath.getText();
		params.createTM = chkCreateTM.getSelection();
		params.tmPath = edTMPath.getText();
		params.segment = pnlSegmentation.getSegment();
		if ( params.segment && pnlSegmentation.getSourceSRX().length() == 0 ) {
			Dialogs.showError(shell, "You must specify an SRX document for the source.", null);
			return false;
		}
		params.sourceSrxPath = pnlSegmentation.getSourceSRX();
		if ( params.segment && pnlSegmentation.getTargetSRX().length() == 0 ) {
			Dialogs.showError(shell, "You must specify an SRX document for the target.", null);
			return false;
		}
		params.targetSrxPath = pnlSegmentation.getTargetSRX();
		params.useTradosWorkarounds = chkUseTradosWorkarounds.getSelection();
		params.checkSingleSegUnit = chkCheckSingleSegUnit.getSelection();
		params.useAutoCorrection = chkUseAutoCorrection.getSelection();
		return true;
	}

}
