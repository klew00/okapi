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

package net.sf.okapi.steps.tokenization.ui.tokens;

import net.sf.okapi.common.ui.abstracteditor.IDialogPage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

public class AddModifyTokenPage extends Composite implements IDialogPage {
	private Label lblToken;
	private Text name;
	private Label lblDescription;
	private Text descr;
	private Label label;
	private Label label_1;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public AddModifyTokenPage(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(5, false));
		
		label = new Label(this, SWT.NONE);
		label.setData("name", "label");
		label.setText("    ");
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		lblToken = new Label(this, SWT.NONE);
		lblToken.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblToken.setData("name", "lblToken");
		lblToken.setText("Token name:");
		
		name = new Text(this, SWT.BORDER);
		name.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent e) {
				
				e.text = e.text.toUpperCase();
			}
		});
		
		name.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		name.setData("name", "name");
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		lblDescription = new Label(this, SWT.NONE);
		lblDescription.setData("name", "lblDescription");
		lblDescription.setText("Token description:");
		
		descr = new Text(this, SWT.BORDER);
		descr.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		descr.setData("name", "descr");
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		label_1 = new Label(this, SWT.NONE);
		label_1.setData("name", "label_1");
		label_1.setText("    ");

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

		if (!(data instanceof String[])) return false;
		
		String[] colDef = (String[]) data;
		if (colDef.length != 2) return false; 
						
		name.setText(colDef[0]);
		descr.setText(colDef[1]);
		
		return true;
	}

	public boolean save(Object data) {

		if (!(data instanceof String[])) return false;
		
		String[] colDef = (String[]) data;
		if (colDef.length != 2) return false;
		
		colDef[0] = name.getText();
		colDef[1] = descr.getText();
		
		return true;
	}
}
