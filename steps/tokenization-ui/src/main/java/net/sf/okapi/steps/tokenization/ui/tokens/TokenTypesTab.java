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

package net.sf.okapi.steps.tokenization.ui.tokens;

import net.sf.okapi.common.ui.abstracteditor.IDialogPage;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Group;

public class TokenTypesTab extends Composite implements IDialogPage {
	private Button btnAllTokens;
	private Button btnOnlyTheseTokens;
	private List list;
	private Button btnAdd;
	private Button btnRemove;
	private Group grpExtract;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public TokenTypesTab(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		grpExtract = new Group(this, SWT.NONE);
		grpExtract.setText("Extract tokens:");
		grpExtract.setLayout(new GridLayout(3, false));
		grpExtract.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpExtract.setData("name", "grpExtract");
		
		btnAllTokens = new Button(grpExtract, SWT.RADIO);
		btnAllTokens.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnAllTokens.setData("name", "btnAllTokens");
		btnAllTokens.setText("All");
		new Label(grpExtract, SWT.NONE);
		
		btnOnlyTheseTokens = new Button(grpExtract, SWT.RADIO);
		btnOnlyTheseTokens.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnOnlyTheseTokens.setData("name", "btnOnlyTheseTokens");
		btnOnlyTheseTokens.setText("Only these:");
		new Label(grpExtract, SWT.NONE);
		new Label(grpExtract, SWT.NONE);
		
		list = new List(grpExtract, SWT.BORDER);
		list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
		list.setData("name", "list");
		
		btnAdd = new Button(grpExtract, SWT.NONE);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gridData.widthHint = 90;
		btnAdd.setLayoutData(gridData);
		btnAdd.setData("name", "btnAdd");
		btnAdd.setText("Add...");
		new Label(grpExtract, SWT.NONE);
		
		btnRemove = new Button(grpExtract, SWT.NONE);
		btnRemove.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnRemove.setData("name", "btnRemove");
		btnRemove.setText("Remove");
		new Label(grpExtract, SWT.NONE);
		new Label(grpExtract, SWT.NONE);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public boolean canClose(boolean isOK) {
		
		return true;
	}

	public void interop(Widget speaker) {
		
	}

	public boolean load(Object data) {
		
		return true;
	}

	public boolean save(Object data) {

		return true;
	}

}
