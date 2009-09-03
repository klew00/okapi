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

import net.sf.okapi.steps.tokenization.tokens.Parameters;
import net.sf.okapi.steps.tokenization.tokens.TokenType;
import net.sf.okapi.common.ui.abstracteditor.AbstractBaseDialog;
import net.sf.okapi.common.ui.abstracteditor.IDialogPage;
import net.sf.okapi.common.ui.abstracteditor.SWTUtils;
import net.sf.okapi.common.ui.abstracteditor.TableAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

public class TokenTypesPage extends Composite implements IDialogPage {
	private Label lblChooseOneOr;
	private Table table;
	private TableColumn colName;
	private TableColumn colDescr;
	private Button btnAdd;
	private Button btnModify;
	private Button btnRemove;
	private Label label;
	private TableAdapter adapter;
	private boolean modified;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public TokenTypesPage(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));
		
		lblChooseOneOr = new Label(this, SWT.NONE);
		lblChooseOneOr.setText("Choose one or more tokens from the table below (Ctrl+click, Ctrl+Shift+click for multiple selection):");
		lblChooseOneOr.setData("name", "lblChooseOneOr");
		new Label(this, SWT.NONE);
		
		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		table.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				
				addModifyRow(table.getItem(new Point(e.x, e.y)));
			}
		});
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 4);
		gridData.heightHint = 400;
		table.setLayoutData(gridData);
		table.setData("name", "table");
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		colName = new TableColumn(table, SWT.NONE);
		colName.setData("name", "colName");
		colName.setWidth(150);
		colName.setText("Token");
		
		colDescr = new TableColumn(table, SWT.NONE);
		colDescr.setData("name", "colDescr");
		colDescr.setWidth(100);
		colDescr.setText("Description");
		
		btnAdd = new Button(this, SWT.NONE);
		btnAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				addModifyRow(null);
			}
		});
		btnAdd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnAdd.setData("name", "btnAdd");
		btnAdd.setText("Add...");
		
		btnModify = new Button(this, SWT.NONE);
		btnModify.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				addModifyRow(table.getItem(table.getSelectionIndex()));
			}
		});
		btnModify.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnModify.setData("name", "btnModify");
		btnModify.setText("Modify...");
		
		btnRemove = new Button(this, SWT.NONE);
		btnRemove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				adapter.removeSelected();
				modified = true;
				interop(e.widget);
			}
		});
		btnRemove.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		btnRemove.setData("name", "btnRemove");
		btnRemove.setText("Remove");
		
		label = new Label(this, SWT.NONE);
		label.setData("name", "label");
		label.setText("                         ");
		
		adapter = new TableAdapter(table);
		adapter.setRelColumnWidths(new double [] {1, 3});
	}

	protected void addModifyRow(TableItem item) {
		
		if (item == null) { // Add new item			
			adapter.unselect();
			
			if (SWTUtils.inputQuery(AddModifyTokenTypePage.class, getShell(), "Add token type", 
					new String[] {"", "0", ""}, 
					null)) {
				
				modified = true;
				adapter.addModifyRow((String []) SWTUtils.getResult(), 1, TableAdapter.DUPLICATE_REPLACE);
			}
			else
				adapter.restoreSelection();
		}
		else {
			if (SWTUtils.inputQuery(AddModifyTokenTypePage.class, getShell(), "Modify token type", 
					SWTUtils.getText(item),
					null)) {					
				
				modified = true;
				adapter.modifyRow(item, (String []) SWTUtils.getResult());
			}
		}
		
		adapter.sort(1, false);
		interop(table);  // Selection changes
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public boolean canClose(boolean isOK) {
		// TODO Auto-generated method stub
		return true;
	}

	public void interop(Widget speaker) {
		
		btnModify.setEnabled(table.getItemCount() > 0 && table.getSelectionIndex() != -1);
		btnRemove.setEnabled(btnModify.getEnabled());
	}

	public boolean load(Object data) {

		if (data == null) {
			
			Object d = getData("dialog");
			
			if (d instanceof AbstractBaseDialog) {
				
				data = new Parameters();
				((AbstractBaseDialog) d).setData(data);
			}			
		}
		
		if (data instanceof Parameters) {
			
			Parameters params = (Parameters) data;

			if (!params.loadTokenTypes()) return false;
			
			adapter.clear();
			
			for (TokenType tokenType : params.getTokenTypes())					
				adapter.addRow(new String[] {tokenType.id, tokenType.description});

			adapter.sort(1, false);
			modified = false;				
		}		
		
		return true;
	}

	public boolean save(Object data) {
		
		if (data instanceof Parameters) {
			
			Parameters params = (Parameters) data;
			params.reset();
		
			for (int i = 1; i <= adapter.getNumRows(); i++)
				params.addTokenType(adapter.getValue(i, 1), adapter.getValue(i, 2));
			
			if (modified)
				params.saveTokenTypes();
			
			for (TableItem item : table.getSelection())
				params.addSelectedTokenType(item.getText(0), item.getText(1));
			
			modified = false;
		}

		return true;
	}

}
