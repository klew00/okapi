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

package net.sf.okapi.applications.rainbow.utilities.alignment;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
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
	private Button                chkSegment;
	private Text                  edSRXPath;
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
	
	private void create (Shell parent)
	{
		shell.setText("Align Bilingual Documents");
		if ( parent != null ) shell.setImage(parent.getImage());
		GridLayout layTmp = new GridLayout();
		layTmp.marginBottom = 0;
		layTmp.verticalSpacing = 0;
		shell.setLayout(layTmp);

		TabFolder tfTmp = new TabFolder(shell, SWT.NONE);
		tfTmp.setLayoutData(new GridData(GridData.FILL_BOTH));

		//--- Main tab
		
		Composite cmpTmp = new Composite(tfTmp, SWT.NONE);
		cmpTmp.setLayout(new GridLayout());
		TabItem tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("Options");
		tiTmp.setControl(cmpTmp);

		Label stTmp = new Label(cmpTmp, SWT.NONE);
		stTmp.setText("Full path of the TMX document to generate:");
		
		edTMXPath = new Text(cmpTmp, SWT.BORDER);
		edTMXPath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		chkSegment = new Button(cmpTmp, SWT.CHECK);
		chkSegment.setText("Segment the items using the following SRX document:");
		
		edSRXPath = new Text(cmpTmp, SWT.BORDER);
		edSRXPath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
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
		chkSegment.setSelection(params.segment);
		edSRXPath.setText(params.srxPath);
	}

	private boolean saveData () {
		if ( inInit ) return true;
		params.tmxPath = edTMXPath.getText();
		params.segment = chkSegment.getSelection();
		params.srxPath = edSRXPath.getText();
		result = true;
		return true;
	}

}
