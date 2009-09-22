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

import net.sf.okapi.common.ui.abstracteditor.IDialogPage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import net.sf.okapi.common.ui.OKCancelPanel;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.List;

public class AttributeTagRulesDialog extends Composite implements IDialogPage {
	private ConditionalRuleEditorComposite conditionalRuleEditorComposite;
	private Text txtEmbeddableTag;
	private Text txtAttributeName;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public AttributeTagRulesDialog(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		{
			Group grpEmbeddableTag = new Group(this, SWT.NONE);
			grpEmbeddableTag.setText("Tag");
			{
				GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
				gridData.heightHint = 40;
				grpEmbeddableTag.setLayoutData(gridData);
			}
			{
				Label lblTagName = new Label(grpEmbeddableTag, SWT.NONE);
				lblTagName.setBounds(10, 18, 43, 31);
				lblTagName.setText("Name:");
			}
			{
				txtEmbeddableTag = new Text(grpEmbeddableTag, SWT.BORDER);
				txtEmbeddableTag.setBounds(56, 15, 173, 23);
			}
		}
		new Label(this, SWT.NONE);
		{
			Group grpAttribute = new Group(this, SWT.NONE);
			grpAttribute.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 0, 0));
			grpAttribute.setText("Attributes");
			GridLayout gridLayout_1 = new GridLayout(1, true);
			gridLayout_1.marginTop = 5;
			gridLayout_1.verticalSpacing = 0;
			gridLayout_1.marginHeight = 0;
			grpAttribute.setLayout(gridLayout_1);
			{
				Composite localizibleAttributesComposite = new Composite(grpAttribute, SWT.NONE);
				GridLayout gridLayout = new GridLayout(2, false);
				gridLayout.verticalSpacing = 0;
				gridLayout.marginWidth = 0;
				gridLayout.marginHeight = 0;
				localizibleAttributesComposite.setLayout(gridLayout);
				localizibleAttributesComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 0));
				{
					txtAttributeName = new Text(localizibleAttributesComposite, SWT.BORDER);
					txtAttributeName.setToolTipText("Attribute Name");
					txtAttributeName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
				}
				{
					Combo attributeTypeCombo = new Combo(localizibleAttributesComposite, SWT.NONE);
					attributeTypeCombo.setToolTipText("Attribute Type");
					attributeTypeCombo.setItems(new String[] {"translatable", "localizaible", "read-only"});
					attributeTypeCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 0));
					attributeTypeCombo.select(0);
				}
			}
			{
				List attributeList = new List(grpAttribute, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
				{
					GridData gridData = new GridData(SWT.FILL, SWT.BOTTOM, true, true, 1, 10);
					gridData.heightHint = 76;
					gridData.verticalIndent = 10;
					attributeList.setLayoutData(gridData);
				}
			}
			new Label(grpAttribute, SWT.NONE);
			new Label(grpAttribute, SWT.NONE);
			{
				AddDeleteComposite addDeleteComposite = new AddDeleteComposite(grpAttribute, SWT.NONE);
				addDeleteComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			}
		}
		{
			Group grpAttributerules = new Group(this, SWT.NONE);
			grpAttributerules.setText("Conditional Attributes");
			grpAttributerules.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
			grpAttributerules.setLayout(new GridLayout(1, false));
			{
				Combo conditionalAttributeTypeCombo = new Combo(grpAttributerules, SWT.NONE);
				conditionalAttributeTypeCombo.setItems(new String[] {"translatable", "localizaible", "read-only"});
				{
					GridData gridData = new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1);
					gridData.widthHint = 128;
					conditionalAttributeTypeCombo.setLayoutData(gridData);
				}
				conditionalAttributeTypeCombo.select(0);
			}
			
			conditionalRuleEditorComposite = new ConditionalRuleEditorComposite(grpAttributerules, SWT.NONE);
			conditionalRuleEditorComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 0, 1));
			GridLayout gridLayout = (GridLayout) conditionalRuleEditorComposite.getLayout();
			gridLayout.makeColumnsEqualWidth = true;
			conditionalRuleEditorComposite.setData("name", "conditionalRuleEditorComposite");
		}
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
