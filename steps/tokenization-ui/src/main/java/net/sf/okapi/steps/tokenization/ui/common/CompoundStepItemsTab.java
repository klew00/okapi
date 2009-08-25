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

package net.sf.okapi.steps.tokenization.ui.common;

import net.sf.okapi.common.ui.abstracteditor.IDialogPage;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;

public class CompoundStepItemsTab extends Composite implements IDialogPage {
	private Label lblListedBelowAre;
	private List list;
	private Text text;
	private Button btnAdd;
	private Button btnModify;
	private Button btnRemove;
	private Button btnMoveUp;
	private Button btnMoveDown;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CompoundStepItemsTab(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));
		
		lblListedBelowAre = new Label(this, SWT.NONE);
		lblListedBelowAre.setData("name", "lblListedBelowAre");
		lblListedBelowAre.setText("Listed below are internal steps in the order of invocation.");
		new Label(this, SWT.NONE);
		
		list = new List(this, SWT.BORDER);
		list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 10));
		list.setData("name", "list");
		
		btnAdd = new Button(this, SWT.NONE);
		btnAdd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnAdd.setData("name", "btnAdd");
		btnAdd.setText("Add...");
		
		btnModify = new Button(this, SWT.NONE);
		btnModify.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnModify.setData("name", "btnModify");
		btnModify.setText("Modify...");
		
		btnRemove = new Button(this, SWT.NONE);
		btnRemove.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnRemove.setData("name", "btnRemove");
		btnRemove.setText("Remove");
		new Label(this, SWT.NONE);
		
		btnMoveUp = new Button(this, SWT.NONE);
		btnMoveUp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnMoveUp.setData("name", "btnMoveUp");
		btnMoveUp.setText("Move Up");
		
		btnMoveDown = new Button(this, SWT.NONE);
		GridData gridData_1 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gridData_1.widthHint = 90;
		btnMoveDown.setLayoutData(gridData_1);
		btnMoveDown.setData("name", "btnMoveDown");
		btnMoveDown.setText("Move Down");
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		text = new Text(this, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gridData.heightHint = 50;
		gridData.widthHint = 500;
		text.setLayoutData(gridData);
		text.setData("name", "text");

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public boolean canClose(boolean isOK) {

		return true;
	}

	public void interop(Widget speaker) {
		// TODO Auto-generated method stub
		
	}

	public boolean load(Object data) {

		return true;
	}

	public boolean save(Object data) {

		return true;
	}

}
