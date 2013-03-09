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

package net.sf.okapi.steps.fullwidthconversion.ui;

import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.IContext;
import net.sf.okapi.common.IHelp;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.ISWTEmbeddableParametersEditor;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.UIUtil;
import net.sf.okapi.steps.fullwidthconversion.Parameters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

@EditorFor(Parameters.class)
public class ParametersEditor implements IParametersEditor, ISWTEmbeddableParametersEditor {
	
	private Shell shell;
	private boolean result = false;
	private OKCancelPanel pnlActions;
	private Parameters params;
	private IHelp help;
	private Button rdToFullWidth;
	private Button rdToHalfWidth;
	private Button chkAsciiOnly;
	private Button chkIncludeSLA;
	private Button chkIncludeLLS;
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
	}

	@Override
	public String validateAndSaveParameters () {
		if ( !saveData() ) return null;
		return params.toString();
	}
	
	private void create (Shell parent,
		boolean readOnly)
	{
		shell.setText("Full-Width Conversion");
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
					if ( help != null ) help.showWiki("Full-Width Conversion Step");
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

		setData();
		shell.pack();
		shell.setMinimumSize(shell.getSize());
		Dialogs.centerWindow(shell, parent);
	}
	
	private void createComposite (Composite parent) {
		mainComposite = new Composite(parent, SWT.BORDER);
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		mainComposite.setLayout(new GridLayout());
		int indent = 16;
		
		rdToHalfWidth = new Button(mainComposite, SWT.RADIO);
		rdToHalfWidth.setText("Convert full-width characters to half-width or ASCII equivalents");
		
		chkIncludeSLA = new Button(mainComposite, SWT.CHECK);
		chkIncludeSLA.setText("Include Squared Latin Abbreviations of the CJK Compatibility block");
		GridData gdTmp = new GridData();
		gdTmp.horizontalIndent = indent;
		chkIncludeSLA.setLayoutData(gdTmp);
		
		chkIncludeLLS = new Button(mainComposite, SWT.CHECK);
		chkIncludeLLS.setText("Include special characters of the Letter-Like Symbols block");
		gdTmp = new GridData();
		gdTmp.horizontalIndent = indent;
		chkIncludeLLS.setLayoutData(gdTmp);
		
		rdToFullWidth = new Button(mainComposite, SWT.RADIO);
		rdToFullWidth.setText("Convert half-width and ASCII characters to full-width equivalents");
		
		chkAsciiOnly = new Button(mainComposite, SWT.CHECK);
		chkAsciiOnly.setText("Convert only the ASCII characters");
		gdTmp = new GridData();
		gdTmp.horizontalIndent = indent;
		chkAsciiOnly.setLayoutData(gdTmp);

		rdToFullWidth.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateChkBox();
			}
		});
	}
	
	private void updateChkBox() {
		chkIncludeSLA.setEnabled(rdToHalfWidth.getSelection());
		chkIncludeLLS.setEnabled(rdToHalfWidth.getSelection());
		chkAsciiOnly.setEnabled(!rdToHalfWidth.getSelection());
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
		rdToHalfWidth.setSelection(params.toHalfWidth);
		chkIncludeSLA.setSelection(params.includeSLA);
		chkIncludeLLS.setSelection(params.includeLLS);
		rdToFullWidth.setSelection(!params.toHalfWidth);
		chkAsciiOnly.setSelection(params.asciiOnly);
		updateChkBox();
	}

	private boolean saveData () {
		params.toHalfWidth = rdToHalfWidth.getSelection();
		params.includeSLA = chkIncludeSLA.getSelection();
		params.includeLLS = chkIncludeLLS.getSelection();
		params.asciiOnly = chkAsciiOnly.getSelection();
		result = true;
		return result;
	}
	
}
