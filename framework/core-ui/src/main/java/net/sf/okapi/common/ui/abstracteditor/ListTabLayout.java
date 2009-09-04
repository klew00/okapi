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

package net.sf.okapi.common.ui.abstracteditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

public class ListTabLayout extends Composite {
	private Label listDescr;
	private List list;
	private Text itemDescr;
	private Button add;
	private Button modify;
	private Button remove;
	private Button up;
	private Button down;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ListTabLayout(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));
		
		listDescr = new Label(this, SWT.NONE);
		listDescr.setData("name", "listDescr");
		new Label(this, SWT.NONE);
		
		list = new List(this, SWT.BORDER | SWT.V_SCROLL);
//		list.addMouseListener(new MouseAdapter() {
//			public void mouseDoubleClick(MouseEvent e) {
//				
//				if (list.getSelectionCount() > 0)
//					//displayEditor(list.getSelection()[0]);
//					add.
//			}
//		});
		list.setItems(new String[] {"111111111111", "2222222222222", "333333333333", "444444444444"});
		list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 10));
		list.setData("name", "list");
		
		add = new Button(this, SWT.NONE);
		add.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		add.setData("name", "add");
		add.setText("Add...");
		
		modify = new Button(this, SWT.NONE);
		modify.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		modify.setData("name", "modify");
		modify.setText("Modify...");
		
		remove = new Button(this, SWT.NONE);
		remove.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		remove.setData("name", "remove");
		remove.setText("Remove");
		new Label(this, SWT.NONE);
		
		up = new Button(this, SWT.NONE);
		up.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		up.setData("name", "up");
		up.setText("Move Up");		
		
		down = new Button(this, SWT.NONE);
		GridData gridData_1 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gridData_1.widthHint = 90;
		down.setLayoutData(gridData_1);
		down.setData("name", "down");
		down.setText("Move Down");
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		itemDescr = new Text(this, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gridData.heightHint = 50;
		gridData.widthHint = 500;
		itemDescr.setLayoutData(gridData);
		itemDescr.setData("name", "itemDescr");
	}

}
