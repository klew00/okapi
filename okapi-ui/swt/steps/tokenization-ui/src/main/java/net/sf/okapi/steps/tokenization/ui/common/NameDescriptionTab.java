/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.steps.tokenization.ui.common;

import net.sf.okapi.common.ui.abstracteditor.IDialogPage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swt.widgets.Label;

public class NameDescriptionTab extends Composite implements IDialogPage {
	private Text text;
	private Text text_1;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public NameDescriptionTab(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		{
			Group grpGeneral = new Group(this, SWT.NONE);
			grpGeneral.setText("General");
			grpGeneral.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			grpGeneral.setLayout(new GridLayout(1, false));
			{
				Label lblName = new Label(grpGeneral, SWT.NONE);
				lblName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
				lblName.setText("Name:");
			}
			{
				text = new Text(grpGeneral, SWT.BORDER);
				text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			}
			{
				Label lblDescription = new Label(grpGeneral, SWT.NONE);
				lblDescription.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1));
				lblDescription.setText("Description:");
			}
			{
				text_1 = new Text(grpGeneral, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
				GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
				gridData.widthHint = 400;
				gridData.heightHint = 100;
				text_1.setLayoutData(gridData);
			}
		}
		{
			Group grpSummary = new Group(this, SWT.NONE);
			grpSummary.setText("Summary");
			grpSummary.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		}

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
