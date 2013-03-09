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

package net.sf.okapi.steps.bomconversion.ui;

import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.IContext;
import net.sf.okapi.common.IHelp;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.ISWTEmbeddableParametersEditor;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.UIUtil;
import net.sf.okapi.steps.bomconversion.Parameters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

@EditorFor(Parameters.class)
public class ParametersEditor implements IParametersEditor, ISWTEmbeddableParametersEditor {
	
	private Shell shell;
	private boolean result = false;
	private OKCancelPanel pnlActions;
	private Parameters params;
	private Button rdAdd;
	private Label stAdd;
	private Button rdRemove;
	private Label stRemove;
	private Button chkAlsoNonUTF8;
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
	}

	@Override
	public String validateAndSaveParameters () {
		if ( !saveData() ) return null;
		return params.toString();
	}
	
	private void create (Shell parent,
		boolean readOnly)
	{
		shell.setText("Byte-Order-Mark Conversion");
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
					if ( help != null ) help.showTopic(this, "index");
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

		Group group = new Group(mainComposite, SWT.NONE);
		group.setLayout(new GridLayout());
		group.setText("Action on the Byte-Order-Mark");
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		int indent = 16;
		rdRemove = new Button(group, SWT.RADIO);
		rdRemove.setText("Remove the Byte-Order-Mark if it is present");
		stRemove = new Label(group, SWT.NONE);
		stRemove.setText("By default, only UTF-8 files with BOM are modified.");
		GridData gdTmp = new GridData();
		gdTmp.horizontalIndent = indent;
		stRemove.setLayoutData(gdTmp);
		
		chkAlsoNonUTF8 = new Button(group, SWT.CHECK);
		chkAlsoNonUTF8.setText("Remove also UTF-16 BOMs (Not recommended)");
		gdTmp = new GridData();
		gdTmp.horizontalIndent = indent;
		chkAlsoNonUTF8.setLayoutData(gdTmp);

		rdAdd = new Button(group, SWT.RADIO);
		rdAdd.setText("Add the Byte-Order-Mark if it is not already present");
		stAdd = new Label(group, SWT.NONE);
		stAdd.setText("IMPORTANT: The input files without BOM are assumed to be in UTF-8 or UTF-16.");
		gdTmp = new GridData();
		gdTmp.horizontalIndent = indent;
		stAdd.setLayoutData(gdTmp);

		rdRemove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateNotes();
			}
		});
		rdRemove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateNotes();
			}
		});
	}
	
	private void updateNotes () {
		stRemove.setEnabled(rdRemove.getSelection());
		stAdd.setEnabled(rdAdd.getSelection());
		chkAlsoNonUTF8.setEnabled(rdRemove.getSelection());
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
		rdRemove.setSelection(params.removeBOM);
		rdAdd.setSelection(!rdRemove.getSelection());
		chkAlsoNonUTF8.setSelection(params.alsoNonUTF8);
		updateNotes();
	}

	private boolean saveData () {
		params.removeBOM = rdRemove.getSelection();
		params.alsoNonUTF8 = chkAlsoNonUTF8.getSelection();
		result = true;
		return result;
	}
	
}
