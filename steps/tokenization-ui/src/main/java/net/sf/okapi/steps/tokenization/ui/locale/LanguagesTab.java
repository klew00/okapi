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

package net.sf.okapi.steps.tokenization.ui.locale;

import net.sf.okapi.common.ui.abstracteditor.IDialogPage;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Group;

public class LanguagesTab extends Composite implements IDialogPage {
	private Button btnAll;
	private Button btnAllExceptThese;
	private List list;
	private Button btnAdd;
	private Button btnRemove;
	private Button btnOnlyTheseLanguages;
	private List list_1;
	private Button btnAdd_1;
	private Button btnRemove_1;
	private Group grpTokenizeTextIn;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public LanguagesTab(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(3, false));
		
		grpTokenizeTextIn = new Group(this, SWT.NONE);
		grpTokenizeTextIn.setText("Tokenize text in:");
		grpTokenizeTextIn.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		grpTokenizeTextIn.setLayout(new GridLayout(3, false));
		grpTokenizeTextIn.setData("name", "grpTokenizeTextIn");
		
		btnAll = new Button(grpTokenizeTextIn, SWT.RADIO);
		btnAll.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnAll.setData("name", "btnAll");
		btnAll.setText("All languages");
		new Label(grpTokenizeTextIn, SWT.NONE);
		
		btnAllExceptThese = new Button(grpTokenizeTextIn, SWT.RADIO);
		btnAllExceptThese.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnAllExceptThese.setData("name", "btnAllExceptThese");
		btnAllExceptThese.setText("All languages except these:");
		new Label(grpTokenizeTextIn, SWT.NONE);
		new Label(grpTokenizeTextIn, SWT.NONE);
		
		list = new List(grpTokenizeTextIn, SWT.BORDER);
		{
			GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3);
			gridData.heightHint = 100;
			gridData.widthHint = 500;
			list.setLayoutData(gridData);
		}
		list.setData("name", "list");
		
		btnAdd = new Button(grpTokenizeTextIn, SWT.NONE);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gridData.widthHint = 90;
		btnAdd.setLayoutData(gridData);
		btnAdd.setData("name", "btnAdd");
		btnAdd.setText("Add...");
		new Label(grpTokenizeTextIn, SWT.NONE);
		
		btnRemove = new Button(grpTokenizeTextIn, SWT.NONE);
		btnRemove.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnRemove.setData("name", "btnRemove");
		btnRemove.setText("Remove");
		new Label(grpTokenizeTextIn, SWT.NONE);
		new Label(grpTokenizeTextIn, SWT.NONE);
		
		btnOnlyTheseLanguages = new Button(grpTokenizeTextIn, SWT.RADIO);
		btnOnlyTheseLanguages.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnOnlyTheseLanguages.setData("name", "btnOnlyTheseLanguages");
		btnOnlyTheseLanguages.setText("Only these languages:");
		new Label(grpTokenizeTextIn, SWT.NONE);
		new Label(grpTokenizeTextIn, SWT.NONE);
		
		list_1 = new List(grpTokenizeTextIn, SWT.BORDER);
		{
			GridData gridData_1 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3);
			gridData_1.widthHint = 500;
			gridData_1.heightHint = 100;
			list_1.setLayoutData(gridData_1);
		}
		list_1.setData("name", "list_1");
		
		btnAdd_1 = new Button(grpTokenizeTextIn, SWT.NONE);
		btnAdd_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnAdd_1.setData("name", "btnAdd_1");
		btnAdd_1.setText("Add...");
		new Label(grpTokenizeTextIn, SWT.NONE);
		
		btnRemove_1 = new Button(grpTokenizeTextIn, SWT.NONE);
		btnRemove_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnRemove_1.setData("name", "btnRemove_1");
		btnRemove_1.setText("Remove");
		new Label(grpTokenizeTextIn, SWT.NONE);
		new Label(grpTokenizeTextIn, SWT.NONE);

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
