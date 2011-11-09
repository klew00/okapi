/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.textmodification.ui;

import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.IContext;
import net.sf.okapi.common.IHelp;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.ISWTEmbeddableParametersEditor;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.UIUtil;
import net.sf.okapi.steps.textmodification.Parameters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

@EditorFor(Parameters.class)
public class ParametersEditor implements IParametersEditor, ISWTEmbeddableParametersEditor {

	private Shell shell;
	private boolean result = false;
	private OKCancelPanel pnlActions;
	private Parameters params;
	private List lbTypes;
	private Button chkAddPrefix;
	private Text edPrefix;
	private Button chkAddSuffix;
	private Text edSuffix;
	private boolean inInit = true;
	private Button chkApplyToExistingTarget;
	private Button chkAddID;
	private Button chkAddName;
	private Button chkMarkSegments;
	private Button chkExpand;
	private Button chkApplyToBlankEntries;
	private IHelp help;
	private Composite mainComposite;
	
	public boolean edit (IParameters params,
		boolean readOnly,
		IContext context)
	{
		boolean bRes = false;
		try {
			shell = null;
			help = (IHelp)context.getObject("help");
			this.params = (Parameters)params;
			shell = new Shell((Shell)context.getObject("shell"), SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
			create((Shell)context.getObject("shell"), readOnly);
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
	
	@Override
	public Composite getComposite () {
		return mainComposite;
	}

	@Override
	public void initializeEmbeddableEditor (Composite parent,
		IParameters paramsObject,
		IContext context)
	{
		params = (Parameters)paramsObject; 
		shell = (Shell)context.getObject("shell");
		createComposite(parent);
		setData();
		inInit = false;
	}

	@Override
	public String validateAndSaveParameters () {
		if ( !saveData() ) return null;
		return params.toString();
	}
	
	private void create (Shell parent,
		boolean readOnly)
	{
		shell.setText("Text Rewriting");
		if ( parent != null ) UIUtil.inheritIcon(shell, parent);
		GridLayout layTmp = new GridLayout();
		layTmp.marginBottom = 0;
		layTmp.verticalSpacing = 0;
		shell.setLayout(layTmp);

		createComposite(shell);
		
		//--- Dialog-level buttons

		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = false;
				if ( e.widget.getData().equals("h") ) {
					if ( help != null ) help.showWiki("Text Modification Step");
					return;
				}
				if ( e.widget.getData().equals("o") ) saveData();
				shell.close();
			};
		};
		pnlActions = new OKCancelPanel(shell, SWT.NONE, OKCancelActions, true);
		pnlActions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		pnlActions.btOK.setEnabled(!readOnly);
		if ( !readOnly ) {
			shell.setDefaultButton(pnlActions.btOK);
		}

		shell.pack();
		shell.setMinimumSize(shell.getSize());
		Point startSize = shell.getMinimumSize();
		if ( startSize.x < 600 ) startSize.x = 600;
		shell.setSize(startSize);
		
		setData();
		inInit = false;
		Dialogs.centerWindow(shell, parent);
	}

	private void createComposite (Composite parent) {
		mainComposite = new Composite(parent, SWT.BORDER);
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		mainComposite.setLayout(new GridLayout());
		
		Label stTmp = new Label(mainComposite, SWT.NONE);
		stTmp.setText("Type of change to perform:");
		GridData gdTmp = new GridData();
		gdTmp.horizontalSpan = 2;
		stTmp.setLayoutData(gdTmp);

		lbTypes = new List(mainComposite, SWT.BORDER | SWT.V_SCROLL);
		lbTypes.add("Keep the original text");
		lbTypes.add("Replace letters by Xs and digits by Ns");
		lbTypes.add("Remove text but keep inline codes");
		lbTypes.add("Replace selected ASCII letters by extended characters");
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.heightHint = 70;
		gdTmp.horizontalSpan = 2;
		lbTypes.setLayoutData(gdTmp);

		chkAddPrefix = new Button(mainComposite, SWT.CHECK);
		chkAddPrefix.setText("Add the following prefix:");
		chkAddPrefix.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				edPrefix.setEnabled(chkAddPrefix.getSelection());
			}
		});
		
		edPrefix = new Text(mainComposite, SWT.BORDER);
		edPrefix.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		chkAddSuffix = new Button(mainComposite, SWT.CHECK);
		chkAddSuffix.setText("Add the following suffix:");
		chkAddSuffix.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				edSuffix.setEnabled(chkAddSuffix.getSelection());
			}
		});
		
		edSuffix = new Text(mainComposite, SWT.BORDER);
		edSuffix.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		chkAddName = new Button(mainComposite, SWT.CHECK);
		chkAddName.setText("Append the name of the item.");
		gdTmp = new GridData();
		gdTmp.horizontalSpan = 2;
		chkAddName.setLayoutData(gdTmp);

		chkAddID = new Button(mainComposite, SWT.CHECK);
		chkAddID.setText("Append the extraction ID of the item.");
		gdTmp = new GridData();
		gdTmp.horizontalSpan = 2;
		chkAddID.setLayoutData(gdTmp);

		chkMarkSegments = new Button(mainComposite, SWT.CHECK);
		chkMarkSegments.setText("Mark segments with '[' and ']' delimiters");
		gdTmp = new GridData();
		gdTmp.horizontalSpan = 2;
		chkMarkSegments.setLayoutData(gdTmp);
		
		chkExpand = new Button(mainComposite, SWT.CHECK);
		chkExpand.setText("Expand the text");
		gdTmp = new GridData();
		gdTmp.horizontalSpan = 2;
		chkExpand.setLayoutData(gdTmp);
		
		Label separator = new Label(mainComposite, SWT.BORDER);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.heightHint = 1;
		gdTmp.horizontalSpan = 2;
		separator.setLayoutData(gdTmp);

		chkApplyToBlankEntries = new Button(mainComposite, SWT.CHECK);
		chkApplyToBlankEntries.setText("Modify also the items without text");
		gdTmp = new GridData();
		gdTmp.horizontalSpan = 2;
		chkApplyToBlankEntries.setLayoutData(gdTmp);
		
		chkApplyToExistingTarget = new Button(mainComposite, SWT.CHECK);
		chkApplyToExistingTarget.setText("Modify also the items with an exiting translation");
		gdTmp = new GridData();
		gdTmp.horizontalSpan = 2;
		chkApplyToExistingTarget.setLayoutData(gdTmp);
		
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
		lbTypes.setSelection(params.type);
		chkAddPrefix.setSelection(params.addPrefix);
		edPrefix.setText(params.prefix);
		chkAddSuffix.setSelection(params.addSuffix);
		edSuffix.setText(params.suffix);
		chkApplyToBlankEntries.setSelection(params.applyToBlankEntries);
		chkApplyToExistingTarget.setSelection(params.applyToExistingTarget);
		chkAddName.setSelection(params.addName);
		chkAddID.setSelection(params.addID);
		chkMarkSegments.setSelection(params.markSegments);
		chkExpand.setSelection(params.expand);

		edPrefix.setEnabled(chkAddPrefix.getSelection());
		edSuffix.setEnabled(chkAddSuffix.getSelection());
	}

	private boolean saveData () {
		if ( inInit ) return true;
		params.type = lbTypes.getSelectionIndex();
		params.addPrefix = chkAddPrefix.getSelection();
		params.prefix = edPrefix.getText();
		params.addSuffix = chkAddSuffix.getSelection();
		params.suffix = edSuffix.getText();
		params.applyToBlankEntries = chkApplyToBlankEntries.getSelection();
		params.applyToExistingTarget = chkApplyToExistingTarget.getSelection();
		params.addName = chkAddName.getSelection();
		params.addID = chkAddID.getSelection();
		params.markSegments = chkMarkSegments.getSelection();
		params.expand = chkExpand.getSelection();
		result = true;
		return true;
	}
	
}
