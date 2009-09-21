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
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class ConditionalRuleEditorComposite extends Composite {
	private Composite conditionalRuleComposite;
	private Composite ruleComposite;
	private Text attributeNameCombo;
	private Combo operatorCombo;
	private Text attributeValueCombo;
	private List ruleList;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ConditionalRuleEditorComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, true));
		
		conditionalRuleComposite = new Composite(this, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.verticalSpacing = 0;
		conditionalRuleComposite.setLayout(gridLayout);
		conditionalRuleComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 0, 0));
		conditionalRuleComposite.setData("name", "group");
		
		ruleComposite = new Composite(conditionalRuleComposite, SWT.BORDER);
		ruleComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 0));
		GridLayout gridLayout_1 = new GridLayout(7, false);
		gridLayout_1.marginWidth = 0;
		gridLayout_1.marginHeight = 0;
		ruleComposite.setLayout(gridLayout_1);
		ruleComposite.setData("name", "composite");
		
		attributeNameCombo = new Text(ruleComposite, SWT.BORDER);
		attributeNameCombo.setToolTipText("Attribute Name");
		attributeNameCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		attributeNameCombo.setData("name", "combo_1");
		new Label(ruleComposite, SWT.NONE);
		
		operatorCombo = new Combo(ruleComposite, SWT.READ_ONLY);
		operatorCombo.setToolTipText("Compare Operator");
		operatorCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		operatorCombo.setVisibleItemCount(3);
		operatorCombo.setItems(new String[] {"equals", "not equal", "matches"});
		operatorCombo.setData("name", "combo_2");
		operatorCombo.select(0);
		new Label(ruleComposite, SWT.NONE);
		
		attributeValueCombo = new Text(ruleComposite, SWT.BORDER);
		attributeValueCombo.setToolTipText("Attribute Value");
		attributeValueCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		attributeValueCombo.setData("name", "combo_3");
		
		ruleList = new List(conditionalRuleComposite, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		{
			GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 10);
			gridData.verticalIndent = 10;
			ruleList.setLayoutData(gridData);
		}
		ruleList.setData("name", "list");
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
