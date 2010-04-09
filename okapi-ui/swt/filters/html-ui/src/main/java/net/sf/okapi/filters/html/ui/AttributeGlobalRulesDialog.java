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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.List;

public class AttributeGlobalRulesDialog extends Composite implements IDialogPage {
	private Text attributeNameTxt;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public AttributeGlobalRulesDialog(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		{
			Group grpEmbeddableTag = new Group(this, SWT.NONE);
			grpEmbeddableTag.setText("Attribute");
			grpEmbeddableTag.setLayout(new GridLayout(4, false));
			{
				GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
				gridData.heightHint = 40;
				grpEmbeddableTag.setLayoutData(gridData);
			}
			{
				Label lblName = new Label(grpEmbeddableTag, SWT.NONE);
				lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
				lblName.setText("Name:");
			}
			{
				attributeNameTxt = new Text(grpEmbeddableTag, SWT.BORDER);
				attributeNameTxt.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
			}
			{
				Label lblType = new Label(grpEmbeddableTag, SWT.NONE);
				lblType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
				lblType.setText("Type:");
			}
			{
				Combo attributeTypeCombo = new Combo(grpEmbeddableTag, SWT.NONE);
				attributeTypeCombo.setItems(new String[] {"translatable", "localizaible", "read-only"});
				attributeTypeCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
			}
		}
		{
			Group grpAllTagsExcept = new Group(this, SWT.NONE);
			grpAllTagsExcept.setText("All Tags Except");
			grpAllTagsExcept.setLayout(new GridLayout(1, false));
			grpAllTagsExcept.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			{
				List allTagsExceptList = new List(grpAllTagsExcept, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
				{
					GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 5);
					gridData.heightHint = 125;
					allTagsExceptList.setLayoutData(gridData);
				}
			}
			{
				AddDeleteComposite addDeleteComposite = new AddDeleteComposite(grpAllTagsExcept, SWT.NONE);
				addDeleteComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
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
