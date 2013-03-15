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

package net.sf.okapi.steps.tokenization.ui.mapping;

import net.sf.okapi.common.ui.abstracteditor.IDialogPage;
import net.sf.okapi.common.ui.abstracteditor.SWTUtil;
import net.sf.okapi.common.ui.abstracteditor.TableAdapter;
import net.sf.okapi.steps.tokenization.ui.mapping.model.MappingItem;
import net.sf.okapi.steps.tokenization.ui.mapping.model.Parameters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;

public class MappingTab extends Composite implements IDialogPage {
	private Table table;
	private TableColumn tblclmnParameters;
	private TableColumn tblclmnEditor;
	private Button btnAdd;
	private Button btnModify;
	private Button btnRemove;
	private Label label;
	private TableAdapter adapter;
	private Label lblThisFormMaps;
	private boolean modified;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public MappingTab(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));
		
		lblThisFormMaps = new Label(this, SWT.NONE);
		lblThisFormMaps.setData("name", "lblThisFormMaps");
		lblThisFormMaps.setText("This table maps parameters editor classes to their parameters classes.");
		new Label(this, SWT.NONE);
		
		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		table.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				
				addModifyRow(table.getItem(new Point(e.x, e.y)));
			}
		});
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 4);
		gridData.heightHint = 400;
		gridData.widthHint = 600;
		table.setLayoutData(gridData);
		table.setData("name", "table");
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		tblclmnEditor = new TableColumn(table, SWT.NONE);
		tblclmnEditor.setData("name", "tblclmnEditor");
		tblclmnEditor.setWidth(100);
		tblclmnEditor.setText("Editor class");
		
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
		btnRemove.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnRemove.setData("name", "btnRemove");
		btnRemove.setText("Remove");
		
		label = new Label(this, SWT.NONE);
		label.setText("                         ");
		label.setData("name", "label");
		
		tblclmnParameters = new TableColumn(table, SWT.NONE);
		tblclmnParameters.setData("name", "tblclmnParameters");
		tblclmnParameters.setWidth(100);
		tblclmnParameters.setText("Parameters class");
		
		adapter = new TableAdapter(table);
		adapter.setRelColumnWidths(new double [] {1, 1});
	}

	public boolean canClose(boolean isOK) {

		return true;
	}

	public void interop(Widget speaker) {

		btnModify.setEnabled(table.getItemCount() > 0 && table.getSelectionIndex() != -1);
		btnRemove.setEnabled(btnModify.getEnabled());
	}

	protected void addModifyRow(TableItem item) {
		
		if (item == null) { // Add new item			
			adapter.unselect();
			
			Object res = SWTUtil.inputQuery(MappingItemPage.class, getShell(), "Add mapping", 
					new String[] {"", ""}, null);
			
			if (res != null) {
				
				modified = true;
				adapter.addModifyRow((String []) res, 1, TableAdapter.DUPLICATE_REPLACE);
			}
			else
				adapter.restoreSelection();
		}
		else {
			
			Object res = SWTUtil.inputQuery(MappingItemPage.class, getShell(), "Modify mapping", 
					SWTUtil.getText(item), null);
			
			if (res != null) {					

				modified = true;
				adapter.modifyRow(item, (String []) res);
			}
		}
		
		adapter.sort(1, false);
		interop(table);  // Selection changes
	}

	public boolean load(Object data) {

		if (data instanceof Parameters) {
			
			Parameters params = (Parameters) data;

			if (!params.loadFromResource("mapper.tprm")) return false;
			
			adapter.clear();
			
			for (MappingItem item : params.getItems())					
				adapter.addRow(new String[] {item.editorClass, item.parametersClass});

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
				params.addMapping(adapter.getValue(i, 1), adapter.getValue(i, 2));
			
			if (modified)
				params.saveToResource("mapper.tprm");
			
			modified = false;
		}

		return true;
	}

}
