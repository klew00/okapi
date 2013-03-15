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

package net.sf.okapi.common.ui;

import net.sf.okapi.common.IContext;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.ISWTEmbeddableParametersEditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * Provides a very basic implementation {@link ISWTEmbeddableParametersEditor}.
 * This implementation offers only a plain text edit box where the string
 * representation of the parameters can be manually edited. No validation
 * is provided, except checking that the string is not empty.
 */
public class DefaultEmbeddableEditor implements ISWTEmbeddableParametersEditor {

	private Composite mainComposite;
	private IParameters params;
	private Text text;
	
	@Override
	public Composite getComposite () {
		return mainComposite;
	}
	
	@Override
	public void initializeEmbeddableEditor(Composite parent,
		IParameters paramsObject,
		IContext context)
	{
		this.params = paramsObject;
		createPanel(parent);
	}
	
	@Override
	public String validateAndSaveParameters () {
		try {
			if ( text.getText().length() == 0 ) {
				text.setFocus();
				return null;
			}
			params.fromString(text.getText());
			return params.toString();
		}
		catch ( Throwable e) {
			Dialogs.showError(mainComposite.getShell(), e.getMessage(), null);
			return null;
		}
	}

	private void createPanel (Composite parent) {
		mainComposite = new Composite(parent, SWT.BORDER);
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		mainComposite.setLayout(new GridLayout());
		text = new Text(mainComposite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		text.setLayoutData(new GridData(GridData.FILL_BOTH));
		text.setText(params.toString());
	}
	
}
