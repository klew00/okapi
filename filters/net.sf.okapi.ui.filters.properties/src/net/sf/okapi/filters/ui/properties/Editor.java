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

package net.sf.okapi.filters.ui.properties;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.filters.InlineCodeFinderPanel;
import net.sf.okapi.common.ui.filters.LDPanel;
import net.sf.okapi.filters.properties.Parameters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
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
	private Button                chkUseKeyFilter;
	private Button                rdExtractOnlyMatchingKey;
	private Button                rdExcludeMatchingKey;
	private Text                  edKeyCondition;
	private Button                chkExtraComments;
	private LDPanel               pnlLD;
	private OKCancelPanel         pnlActions;
	private Parameters            params;
	private Button                chkEscapeExtendedChars;
	private Button                chkUseCodeFinder;
	private InlineCodeFinderPanel pnlCodeFinder;

	/**
	 * Invokes the editor for the Properties filter parameters.
	 * @param p_Options The option object of the action.
	 * @param p_Object The SWT Shell object of the parent shell in the UI.
	 */
	public boolean edit (IParameters p_Options,
		Object p_Object)
	{
		boolean bRes = false;
		shell = null;
		params = (Parameters)p_Options;
		try {
			shell = new Shell((Shell)p_Object, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
			create((Shell)p_Object);
			return showDialog();
		}
		catch ( Exception E ) {
			Dialogs.showError(shell, E.getLocalizedMessage(), null);
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
	
	private void create (Shell p_Parent) {
		shell.setText(Res.getString("EditorCaption"));
		if ( p_Parent != null ) shell.setImage(p_Parent.getImage());
		GridLayout layTmp = new GridLayout();
		layTmp.marginBottom = 0;
		layTmp.verticalSpacing = 0;
		shell.setLayout(layTmp);

		TabFolder tfTmp = new TabFolder(shell, SWT.NONE);
		GridData gdTmp = new GridData(GridData.FILL_BOTH);
		tfTmp.setLayoutData(gdTmp);

		//--- Options tab
		
		Composite cmpTmp = new Composite(tfTmp, SWT.NONE);
		layTmp = new GridLayout();
		cmpTmp.setLayout(layTmp);
		
		Group grpTmp = new Group(cmpTmp, SWT.NONE);
		layTmp = new GridLayout();
		grpTmp.setLayout(layTmp);
		grpTmp.setText(Res.getString("LodDirTitle"));
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		grpTmp.setLayoutData(gdTmp);
		pnlLD = new LDPanel(grpTmp, SWT.NONE);
		
		grpTmp = new Group(cmpTmp, SWT.NONE);
		layTmp = new GridLayout();
		grpTmp.setLayout(layTmp);
		grpTmp.setText(Res.getString("KeyCondTitle"));
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		grpTmp.setLayoutData(gdTmp);
		
		chkUseKeyFilter = new Button(grpTmp, SWT.CHECK);
		chkUseKeyFilter.setText(Res.getString("chkUseKeyFilter"));
		chkUseKeyFilter.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateKeyFilter();
			};
		});

		rdExtractOnlyMatchingKey = new Button(grpTmp, SWT.RADIO);
		rdExtractOnlyMatchingKey.setText(Res.getString("rdExtractOnlyMatchingKey"));
		gdTmp = new GridData();
		gdTmp.horizontalIndent = 16;
		rdExtractOnlyMatchingKey.setLayoutData(gdTmp);

		rdExcludeMatchingKey = new Button(grpTmp, SWT.RADIO);
		rdExcludeMatchingKey.setText(Res.getString("rdExcludeMatchingKey"));
		rdExcludeMatchingKey.setLayoutData(gdTmp);

		edKeyCondition = new Text(grpTmp, SWT.BORDER);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalIndent = 16;
		edKeyCondition.setLayoutData(gdTmp);
		
		Label label = new Label(grpTmp, SWT.WRAP);
		label.setText(Res.getString("KeyCondNote"));
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalIndent = 16;
		gdTmp.widthHint = 300;
		label.setLayoutData(gdTmp);
		
		chkExtraComments = new Button(cmpTmp, SWT.CHECK);
		chkExtraComments.setText(Res.getString("chkExtraComments"));

		TabItem tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText(Res.getString("tabOptions"));
		tiTmp.setControl(cmpTmp);
		
		//--- Inline tab
		
		cmpTmp = new Composite(tfTmp, SWT.NONE);
		layTmp = new GridLayout();
		cmpTmp.setLayout(layTmp);
		
		chkUseCodeFinder = new Button(cmpTmp, SWT.CHECK);
		chkUseCodeFinder.setText("Has in-line codes as defined below:");
		chkUseCodeFinder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateInlineCodes();
			};
		});
		
		pnlCodeFinder = new InlineCodeFinderPanel(cmpTmp, SWT.NONE);
		pnlCodeFinder.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("In-line Codes");
		tiTmp.setControl(cmpTmp);
			

		//--- Output tab
		
		cmpTmp = new Composite(tfTmp, SWT.NONE);
		layTmp = new GridLayout();
		cmpTmp.setLayout(layTmp);
		
		grpTmp = new Group(cmpTmp, SWT.NONE);
		layTmp = new GridLayout();
		grpTmp.setLayout(layTmp);
		grpTmp.setText(Res.getString("grpExtendedChars"));
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		grpTmp.setLayoutData(gdTmp);

		chkEscapeExtendedChars = new Button(grpTmp, SWT.CHECK);
		chkEscapeExtendedChars.setText(Res.getString("chkEscapeExtendedChars"));
		
		tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText(Res.getString("tabOutput"));
		tiTmp.setControl(cmpTmp);
		
		
		//--- Dialog-level buttons

		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = false;
				if ( e.widget.getData().equals("h") ) {
					//TODO: Call help
					return;
				}
				if ( e.widget.getData().equals("o") ) {
					if ( pnlCodeFinder.inEditMode() ) {
						pnlCodeFinder.endEditMode(true);
					}
					if ( !saveData() ) return;
					result = true;
				}
				shell.close();
			};
		};
		pnlActions = new OKCancelPanel(shell, SWT.NONE, OKCancelActions, true);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		pnlActions.setLayoutData(gdTmp);
		shell.setDefaultButton(pnlActions.btOK);

		shell.pack();
		Rectangle Rect = shell.getBounds();
		shell.setMinimumSize(Rect.width, Rect.height);
		Dialogs.centerWindow(shell, p_Parent);
		setData();
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
		pnlLD.setOptions(params.locDir.useLD(), params.locDir.localizeOutside());
		edKeyCondition.setText(params.keyCondition);
		rdExtractOnlyMatchingKey.setSelection(params.extractOnlyMatchingKey);
		rdExcludeMatchingKey.setSelection(!params.extractOnlyMatchingKey);
		chkUseKeyFilter.setSelection(params.useKeyCondition);
		chkExtraComments.setSelection(params.extraComments);
		chkEscapeExtendedChars.setSelection(params.escapeExtendedChars);
		chkUseCodeFinder.setSelection(params.useCodeFinder);
		pnlCodeFinder.setData(params.codeFinder.toString());
		
		updateInlineCodes();
		pnlCodeFinder.updateDisplay();
		pnlLD.updateDisplay();
		updateKeyFilter();
	}
	
	private boolean saveData () {
		params.locDir.setOptions(pnlLD.getUseLD(), pnlLD.getLocalizeOutside());
		params.useKeyCondition = chkUseKeyFilter.getSelection();
		params.keyCondition = edKeyCondition.getText();
		params.extractOnlyMatchingKey = rdExtractOnlyMatchingKey.getSelection();
		params.extraComments = chkExtraComments.getSelection();
		params.escapeExtendedChars = chkEscapeExtendedChars.getSelection();
		params.useCodeFinder = chkUseCodeFinder.getSelection();
		//TODO: check regexp
		String tmp = pnlCodeFinder.getData();
		if ( tmp == null ) {
			return false;
		}
		else {
			params.codeFinder.fromString(tmp);
		}
		return true;
	}
	
	private void updateKeyFilter () {
		edKeyCondition.setEnabled(chkUseKeyFilter.getSelection());
		rdExtractOnlyMatchingKey.setEnabled(chkUseKeyFilter.getSelection());
		rdExcludeMatchingKey.setEnabled(chkUseKeyFilter.getSelection());
	}
	
	private void updateInlineCodes () {
		pnlCodeFinder.enable(chkUseCodeFinder.getSelection());
	}

}
