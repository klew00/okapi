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
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

public class ConditionalRuleEditorDialog extends Composite implements IDialogPage {
	private ConditionalRuleEditorComposite conditionalRuleEditorComposite;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ConditionalRuleEditorDialog(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		
		conditionalRuleEditorComposite = new ConditionalRuleEditorComposite(this, SWT.NONE);
		FormData formData = new FormData();
		formData.bottom = new FormAttachment(100, -44);
		formData.top = new FormAttachment(0, 10);
		formData.right = new FormAttachment(100, -10);
		formData.left = new FormAttachment(0, 10);
		conditionalRuleEditorComposite.setLayoutData(formData);
		conditionalRuleEditorComposite.setData("name", "conditionalRuleEditorComposite");
		{
			OkCancelComposite okCancelComposite = new OkCancelComposite(this, SWT.NONE);
			{
				FormData formData_1 = new FormData();
				formData_1.top = new FormAttachment(conditionalRuleEditorComposite, 6);
				formData_1.right = new FormAttachment(conditionalRuleEditorComposite, 0, SWT.RIGHT);
				okCancelComposite.setLayoutData(formData_1);
			}
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
