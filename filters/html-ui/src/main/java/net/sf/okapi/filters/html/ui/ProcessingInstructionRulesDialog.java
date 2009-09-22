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

public class ProcessingInstructionRulesDialog extends Composite implements IDialogPage {
	private Text txtPIName;
	private Combo piProcessingTypeCombo;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ProcessingInstructionRulesDialog(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		{
			Group grpEmbeddableTag = new Group(this, SWT.NONE);
			grpEmbeddableTag.setText("Processing Instruction");
			grpEmbeddableTag.setLayout(new GridLayout(5, false));
			{
				GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
				gridData.heightHint = 47;
				grpEmbeddableTag.setLayoutData(gridData);
			}
			{
				Label lblTagName = new Label(grpEmbeddableTag, SWT.NONE);
				{
					GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 2);
					gridData.widthHint = 59;
					lblTagName.setLayoutData(gridData);
				}
				lblTagName.setText("Name:");
			}
			{
				txtPIName = new Text(grpEmbeddableTag, SWT.BORDER);
				{
					GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
					gridData.widthHint = 126;
					txtPIName.setLayoutData(gridData);
				}
			}
			new Label(grpEmbeddableTag, SWT.NONE);
			{
				Label lblProcessAs = new Label(grpEmbeddableTag, SWT.NONE);
				lblProcessAs.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
				lblProcessAs.setText("ProcessingType:");
			}
			{
				piProcessingTypeCombo = new Combo(grpEmbeddableTag, SWT.BORDER);
				piProcessingTypeCombo.setItems(new String[] {"skeleton", "embedded tag", "remove"});
				piProcessingTypeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
				piProcessingTypeCombo.select(0);
			}
			new Label(grpEmbeddableTag, SWT.NONE);
			new Label(grpEmbeddableTag, SWT.NONE);
			new Label(grpEmbeddableTag, SWT.NONE);
			new Label(grpEmbeddableTag, SWT.NONE);
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
