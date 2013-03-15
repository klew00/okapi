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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;

public class CompoundStepItemPage extends Composite implements IDialogPage {
	private Label label;
	private Label lblStepClass;
	private Label lblResourceName;
	private Text ctext;
	private Text ptext;
	private Label label_1;
	private Label label_2;
	private Label label_3;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CompoundStepItemPage(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(4, false));
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		label_2 = new Label(this, SWT.NONE);
		label_2.setData("name", "label_2");
		new Label(this, SWT.NONE);
		
		label = new Label(this, SWT.NONE);
		label.setData("name", "label");
		label.setText("    ");
		
		lblStepClass = new Label(this, SWT.NONE);
		lblStepClass.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblStepClass.setData("name", "lblStepClass");
		lblStepClass.setText("Step class (fully qualified):");
		
		ctext = new Text(this, SWT.BORDER);
		{
			GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
			gridData.widthHint = 400;
			ctext.setLayoutData(gridData);
		}
		ctext.setData("name", "ctext");
		
		label_1 = new Label(this, SWT.NONE);
		label_1.setData("name", "label_1");
		label_1.setText("    ");
		new Label(this, SWT.NONE);
		
		lblResourceName = new Label(this, SWT.NONE);
		lblResourceName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblResourceName.setData("name", "lblResourceName");
		lblResourceName.setText("Configuration (short filename):");
		
		ptext = new Text(this, SWT.BORDER);
		ptext.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		ptext.setData("name", "ptext");
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		label_3 = new Label(this, SWT.NONE);
		label_3.setData("name", "label_3");
		new Label(this, SWT.NONE);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public boolean canClose(boolean isOK) {
		
		return true;
	}

	public void interop(Widget speaker) {
		// TODO Auto-generated method stub
		
	}

	public boolean load(Object data) {

		return true;
	}

	public boolean save(Object data) {

		return true;
	}

}
