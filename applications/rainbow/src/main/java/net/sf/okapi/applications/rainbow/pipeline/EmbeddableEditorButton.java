/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.applications.rainbow.pipeline;

import net.sf.okapi.common.IContext;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.ISWTEmbeddableParametersEditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class EmbeddableEditorButton implements ISWTEmbeddableParametersEditor {

	private Composite mainComposite;
	private Button btEdit;
	private IParameters params;
	private IParametersEditor editor;
	private IContext context;
	
	public EmbeddableEditorButton (IParametersEditor editor) {
		this.editor = editor;
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
		this.params = paramsObject;
		this.context = context;
		createPanel(parent);
	}
	
	@Override
	public String validateAndSaveParameters () {
		// Nothing to do as this is done via the edit dialog box
		return params.toString();
	}

	private void createPanel (Composite parent) {
		mainComposite = new Composite(parent, SWT.BORDER);
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		mainComposite.setLayout(new GridLayout());
		btEdit = new Button(mainComposite, SWT.PUSH);
		btEdit.setText("Edit Step Parameters...");
		btEdit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editParameters();
			}
		});
	}
	
	private void editParameters () {
		try {
			editor.edit(params, false, context);
		}
		catch ( Throwable e) {
			Dialogs.showError(mainComposite.getShell(), e.getMessage(), null);
		}
	}

}
