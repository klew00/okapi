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

public class EmbeddableTagRulesDialog extends Composite implements IDialogPage {
	private ConditionalRuleEditorComposite conditionalRuleEditorComposite;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public EmbeddableTagRulesDialog(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		{
			Group grpTag = new Group(this, SWT.NONE);
			grpTag.setText("Tag Name");
			{
				GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
				gridData.heightHint = 46;
				grpTag.setLayoutData(gridData);
			}
		}
		
		conditionalRuleEditorComposite = new ConditionalRuleEditorComposite(this, SWT.BORDER);
		GridLayout gridLayout = (GridLayout) conditionalRuleEditorComposite.getLayout();
		gridLayout.makeColumnsEqualWidth = true;
		conditionalRuleEditorComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		conditionalRuleEditorComposite.setData("name", "conditionalRuleEditorComposite");
		{
			OKCancelPanel cancelPanel = new OKCancelPanel(this, SWT.NONE);
			cancelPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
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
