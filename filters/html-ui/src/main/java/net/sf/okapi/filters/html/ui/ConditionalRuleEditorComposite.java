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

package net.sf.okapi.filters.html.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;

public class ConditionalRuleEditorComposite extends Composite {
	private Label label;
	private Combo combo;
	private Group group;
	private Composite composite;
	private Combo combo_1;
	private Combo combo_2;
	private Combo combo_3;
	private List list;
	private Button button;
	private Button button_1;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ConditionalRuleEditorComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		
		label = new Label(this, SWT.NONE);
		FormData formData = new FormData();
		formData.height = 21;
		formData.top = new FormAttachment(0, 10);
		formData.width = 117;
		formData.left = new FormAttachment(0, 10);
		label.setLayoutData(formData);
		label.setText("Tag Name:");
		label.setData("name", "label");
		
		combo = new Combo(this, SWT.NONE);
		FormData formData_1 = new FormData();
		formData_1.top = new FormAttachment(0, 10);
		formData_1.right = new FormAttachment(100, -142);
		formData_1.left = new FormAttachment(label, 6);
		formData_1.width = 305;
		combo.setLayoutData(formData_1);
		combo.setData("name", "combo");
		
		group = new Group(this, SWT.NONE);
		FormData formData_2 = new FormData();
		formData_2.bottom = new FormAttachment(100);
		formData_2.height = 332;
		formData_2.top = new FormAttachment(label, 15);
		formData_2.right = new FormAttachment(100);
		formData_2.left = new FormAttachment(0);
		group.setLayoutData(formData_2);
		group.setLayout(new FormLayout());
		group.setText("Conditional Rule");
		group.setData("name", "group");
		
		composite = new Composite(group, SWT.NONE);
		FillLayout fillLayout = new FillLayout(SWT.HORIZONTAL);
		fillLayout.spacing = 10;
		composite.setLayout(fillLayout);
		FormData formData_3 = new FormData();
		formData_3.top = new FormAttachment(0, 10);
		formData_3.right = new FormAttachment(100, -10);
		composite.setLayoutData(formData_3);
		composite.setData("name", "composite");
		
		combo_1 = new Combo(composite, SWT.NONE);
		combo_1.setData("name", "combo_1");
		
		combo_2 = new Combo(composite, SWT.NONE);
		combo_2.setVisibleItemCount(3);
		combo_2.setItems(new String[] {"equal", "not equal", "regex"});
		combo_2.setData("name", "combo_2");
		
		combo_3 = new Combo(composite, SWT.NONE);
		combo_3.setData("name", "combo_3");
		
		list = new List(group, SWT.BORDER | SWT.V_SCROLL);
		formData_3.left = new FormAttachment(list, 0, SWT.LEFT);
		FormData formData_4 = new FormData();
		formData_4.top = new FormAttachment(composite, 14);
		formData_4.left = new FormAttachment(0, 10);
		formData_4.right = new FormAttachment(100, -10);
		list.setLayoutData(formData_4);
		list.setData("name", "list");
		
		button = new Button(group, SWT.NONE);
		formData_4.bottom = new FormAttachment(button, -4);
		FormData formData_5 = new FormData();
		formData_5.width = 75;
		formData_5.height = 25;
		button.setLayoutData(formData_5);
		button.setText("Add");
		button.setData("name", "button");
		
		button_1 = new Button(group, SWT.NONE);
		formData_5.bottom = new FormAttachment(button_1, 0, SWT.BOTTOM);
		formData_5.right = new FormAttachment(button_1, -6);
		FormData formData_6 = new FormData();
		formData_6.width = 75;
		formData_6.height = 25;
		formData_6.bottom = new FormAttachment(100);
		formData_6.right = new FormAttachment(100, -10);
		button_1.setLayoutData(formData_6);
		button_1.setText("Remove");
		button_1.setData("name", "button_1");

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
