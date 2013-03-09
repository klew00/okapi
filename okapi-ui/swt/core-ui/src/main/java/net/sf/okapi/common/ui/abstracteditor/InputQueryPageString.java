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

package net.sf.okapi.common.ui.abstracteditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

/**
 * 
 * 
 * @version 0.1, 23.06.2009
 */

public class InputQueryPageString extends Composite implements IInputQueryPage  {
	private Composite composite;
	private Label lblPrompt;
	private Text text;
	private Label label;
	@SuppressWarnings("unused")
	private Label label_1;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public InputQueryPageString(Composite parent, int style) {
		super(parent, SWT.BORDER);
		setLayout(new GridLayout(1, false));
		
		composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		new Label(composite, SWT.NONE);
		
		label = new Label(composite, SWT.NONE);
		label.setText("                                                                  ");
		
		lblPrompt = new Label(composite, SWT.NONE);
		lblPrompt.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPrompt.setText("Prompt:");
		
		text = new Text(composite, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		new Label(composite, SWT.NONE);
		
		label_1 = new Label(composite, SWT.NONE);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public boolean load(Object data) {
		
		if (!(data instanceof String[])) return false;
		
		text.setText(((String[])data)[0]);
		return true;
	}

	public boolean save(Object data) {

		if (!(data instanceof String[])) return false;
		((String[])data)[0] = text.getText();
		
		return true;
	}

	public void setPrompt(String prompt) {
		
		lblPrompt.setText(prompt);
	}

	public void interop(Widget speaker) {
		
	}

	public boolean canClose(boolean isOK) {

		return true;
	}

}

