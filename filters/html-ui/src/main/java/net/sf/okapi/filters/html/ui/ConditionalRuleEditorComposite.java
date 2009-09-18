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
import org.eclipse.swt.layout.FillLayout;

public class ConditionalRuleEditorComposite extends Composite {
	private Label label;
	private Combo combo;
	private Group group;
	private Composite composite;
	private Combo comboAttributeName;
	private Combo comboOperator;
	private Combo comboAttributeValue;
	private List list;

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
		formData_2.bottom = new FormAttachment(100, -40);
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
		formData_3.left = new FormAttachment(0, 10);
		formData_3.right = new FormAttachment(100, -10);
		formData_3.top = new FormAttachment(0, 10);
		composite.setLayoutData(formData_3);
		composite.setData("name", "composite");
		
		comboAttributeName = new Combo(composite, SWT.NONE);
		comboAttributeName.setData("name", "combo_1");
		
		comboOperator = new Combo(composite, SWT.NONE);
		comboOperator.setVisibleItemCount(3);
		comboOperator.setItems(new String[] {"equal", "not equal", "regex"});
		comboOperator.setData("name", "combo_2");
		
		comboAttributeValue = new Combo(composite, SWT.NONE);
		comboAttributeValue.setData("name", "combo_3");
		
		list = new List(group, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		FormData formData_4 = new FormData();
		formData_4.bottom = new FormAttachment(100);
		formData_4.top = new FormAttachment(composite, 14);
		formData_4.left = new FormAttachment(0, 10);
		formData_4.right = new FormAttachment(100, -10);
		list.setLayoutData(formData_4);
		list.setData("name", "list");
		{
			AddDeleteComposite addDeleteComposite = new AddDeleteComposite(this, SWT.NONE);
			{
				FormData formData_5 = new FormData();
				formData_5.top = new FormAttachment(group, 6);
				formData_5.right = new FormAttachment(100, -10);
				addDeleteComposite.setLayoutData(formData_5);
			}
		}

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
