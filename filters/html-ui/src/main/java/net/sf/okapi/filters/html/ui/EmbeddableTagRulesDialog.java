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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class EmbeddableTagRulesDialog extends Composite implements IDialogPage {
	private ConditionalRuleEditorComposite conditionalRuleEditorComposite;
	private Text txtEmbeddableTagName;
	private Text txtEmbeddabletagType;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public EmbeddableTagRulesDialog(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		{
			Group grpEmbeddableTag = new Group(this, SWT.NONE);
			grpEmbeddableTag.setText("Embeddable Tag");
			{
				GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
				gridData.heightHint = 46;
				grpEmbeddableTag.setLayoutData(gridData);
			}
			{
				Label lblTagName = new Label(grpEmbeddableTag, SWT.NONE);
				lblTagName.setBounds(10, 21, 69, 31);
				lblTagName.setText("Tag Name:");
			}
			{
				txtEmbeddableTagName = new Text(grpEmbeddableTag, SWT.BORDER);
				txtEmbeddableTagName.setBounds(85, 18, 152, 23);
			}
			{
				Label lblType = new Label(grpEmbeddableTag, SWT.NONE);
				lblType.setBounds(250, 21, 39, 31);
				lblType.setText("Type:");
			}
			{
				txtEmbeddabletagType = new Text(grpEmbeddableTag, SWT.BORDER);
				txtEmbeddabletagType.setBounds(295, 18, 150, 23);
			}
		}
		{
			Group grpConditionalRules = new Group(this, SWT.NONE);
			grpConditionalRules.setText("Conditional Rules");
			grpConditionalRules.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			grpConditionalRules.setLayout(new GridLayout(1, false));
			
			conditionalRuleEditorComposite = new ConditionalRuleEditorComposite(grpConditionalRules, SWT.BORDER);
			conditionalRuleEditorComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
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
