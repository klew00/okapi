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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class ConditionalRuleEditorComposite extends Composite {
	private Group group;
	private Composite composite;
	private Text comboAttributeName;
	private Combo comboOperator;
	private Text comboAttributeValue;
	private List list;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ConditionalRuleEditorComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, true));
		
		group = new Group(this, SWT.NONE);
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		group.setText("Conditional Rule");
		group.setData("name", "group");
		
		composite = new Composite(group, SWT.BORDER);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		composite.setLayout(new GridLayout(7, false));
		composite.setData("name", "composite");
		
		comboAttributeName = new Text(composite, SWT.BORDER);
		comboAttributeName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		comboAttributeName.setData("name", "combo_1");
		new Label(composite, SWT.NONE);
		
		comboOperator = new Combo(composite, SWT.NONE);
		comboOperator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		comboOperator.setVisibleItemCount(3);
		comboOperator.setItems(new String[] {"equal", "not equal", "regex"});
		comboOperator.setData("name", "combo_2");
		new Label(composite, SWT.NONE);
		
		comboAttributeValue = new Text(composite, SWT.BORDER);
		comboAttributeValue.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		comboAttributeValue.setData("name", "combo_3");
		
		list = new List(group, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		list.setData("name", "list");
		{
			AddDeleteComposite addDeleteComposite = new AddDeleteComposite(this, SWT.NONE);
			addDeleteComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		}

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
