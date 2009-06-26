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

package net.sf.okapi.ui.filters.table;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.ui.filters.plaintext.common.IParametersEditorPage;
import net.sf.okapi.ui.filters.plaintext.common.SWTUtils;
import net.sf.okapi.ui.filters.plaintext.common.Util2;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * 
 * 
 * @version 0.1, 20.06.2009
 * @author Sergei Vasilyev
 */

public class ColumnsTab extends Composite implements IParametersEditorPage {
	private Table table;
	private TableColumn tblclmnColumn;
	private TableColumn tblclmnType;
	private TableColumn tblclmnSource;
	private TableColumn tblclmnSuffix;
	private TableColumn tblclmnLanguage;
	private TableColumn tblclmnStart;
	private TableColumn tblclmnEnd;
	private Group grpExtractionMode;
	private Group grpNumberOfColumns;
	private Group colDefs;
	private Button defs;
	private Button all;
	private Button fix;
	private Button names;
	private Button vals;
	private Spinner num;
	private Button btnAdd;
	private Button btnRemove;
	private Button btnModify;
	private Composite composite_2;
	private Label label_1;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ColumnsTab(final Composite parent, int style) {
		
		super(parent, style);
		setLayout(new GridLayout(2, false));
		
		grpExtractionMode = new Group(this, SWT.NONE);
		grpExtractionMode.setLayout(new FormLayout());
		grpExtractionMode.setText("Extraction mode");
		grpExtractionMode.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		defs = new Button(grpExtractionMode, SWT.RADIO);
		defs.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				interop();
			}
		});
		{
			FormData formData = new FormData();
			formData.right = new FormAttachment(100, -10);
			formData.top = new FormAttachment(0, 10);
			formData.left = new FormAttachment(0, 10);
			defs.setLayoutData(formData);
		}
		defs.setText("Extract by column definitions");
		
		all = new Button(grpExtractionMode, SWT.RADIO);
		all.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				interop();
			}
		});
		{
			FormData formData = new FormData();
			formData.right = new FormAttachment(defs, 0, SWT.RIGHT);
			formData.top = new FormAttachment(defs, 6);
			formData.left = new FormAttachment(defs, 0, SWT.LEFT);
			all.setLayoutData(formData);
		}
		all.setText("Extract from all columns (create separate text units)");
		
		grpNumberOfColumns = new Group(this, SWT.NONE);
		grpNumberOfColumns.setLayout(new FormLayout());
		grpNumberOfColumns.setText("Number of columns");
		grpNumberOfColumns.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		fix = new Button(grpNumberOfColumns, SWT.RADIO);
		fix.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				interop();
			}
		});
		{
			FormData formData = new FormData();
			formData.width = 157;
			formData.top = new FormAttachment(0, 10);
			formData.left = new FormAttachment(0, 10);
			fix.setLayoutData(formData);
		}
		fix.setText("Fixed number of columns");
		
		num = new Spinner(grpNumberOfColumns, SWT.BORDER);
		{
			FormData formData = new FormData();
			formData.top = new FormAttachment(fix, 0, SWT.TOP);
			formData.left = new FormAttachment(fix, 10);
			num.setLayoutData(formData);
		}
		num.setMinimum(1);
		
		names = new Button(grpNumberOfColumns, SWT.RADIO);
		names.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				interop();
			}
		});
		{
			FormData formData = new FormData();
			formData.top = new FormAttachment(fix, 6);
			formData.left = new FormAttachment(fix, 0, SWT.LEFT);
			names.setLayoutData(formData);
		}
		names.setText("Defined by column names");
		
		vals = new Button(grpNumberOfColumns, SWT.RADIO);
		vals.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				interop();
			}
		});
		{
			FormData formData = new FormData();
			formData.top = new FormAttachment(names, 6);
			formData.left = new FormAttachment(fix, 0, SWT.LEFT);
			vals.setLayoutData(formData);
		}
		vals.setText("Defined by values (may vary in different rows)");
		
		colDefs = new Group(this, SWT.NONE);
		colDefs.setText("Column definitions");
		colDefs.setLayout(new GridLayout(2, false));
		colDefs.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 2, 1));
		
		table = new Table(colDefs, SWT.BORDER | SWT.FULL_SELECTION);
		table.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
								
				addModifyRow(table.getItem(new Point(e.x, e.y)));
			}
		});
		table.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				updateColumnWidths();
			}			
		});
		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				interop();
			}
		});
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 4));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		tblclmnColumn = new TableColumn(table, SWT.RIGHT);
		tblclmnColumn.setWidth(72);
		tblclmnColumn.setText("Column #");
		
		tblclmnType = new TableColumn(table, SWT.NONE);
		tblclmnType.setWidth(93);
		tblclmnType.setText("Type");
		
		tblclmnSource = new TableColumn(table, SWT.NONE);
		tblclmnSource.setWidth(95);
		tblclmnSource.setText("Source column");
		
		tblclmnLanguage = new TableColumn(table, SWT.NONE);
		tblclmnLanguage.setWidth(84);
		tblclmnLanguage.setText("Language");
		
		tblclmnSuffix = new TableColumn(table, SWT.NONE);
		tblclmnSuffix.setWidth(116);
		tblclmnSuffix.setText("ID suffix");
		
		tblclmnStart = new TableColumn(table, SWT.NONE);
		tblclmnStart.setWidth(47);
		tblclmnStart.setText("Start");
		
		tblclmnEnd = new TableColumn(table, SWT.NONE);
		tblclmnEnd.setWidth(47);
		tblclmnEnd.setText("End");
		
		composite_2 = new Composite(colDefs, SWT.NONE);
		composite_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		composite_2.setLayout(new GridLayout(1, false));
		
		btnAdd = new Button(composite_2, SWT.NONE);
		btnAdd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
							
				addModifyRow(null);
			}
		});
		btnAdd.setText("Add...");
		
		btnModify = new Button(composite_2, SWT.NONE);
		btnModify.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnModify.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				addModifyRow(table.getItem(table.getSelectionIndex()));
			}
		});
		btnModify.setText("Modify...");
		
		btnRemove = new Button(composite_2, SWT.NONE);
		btnRemove.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnRemove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				int index = table.getSelectionIndex();
				table.remove(index);
				
				if (index > table.getItemCount() - 1) index = table.getItemCount() - 1;
				if (index > -1)	table.select(index);
				interop();
			}
		});
		btnRemove.setText("Remove");
		
		label_1 = new Label(composite_2, SWT.NONE);
		label_1.setText("                          ");
		new Label(colDefs, SWT.NONE);
		new Label(colDefs, SWT.NONE);
		new Label(colDefs, SWT.NONE);		
	}

	protected void addModifyRow(TableItem item) {
		
		if (item == null) { // Add new item
			table.setSelection(-1);
			
			if (SWTUtils.inputQuery(AddModifyColumnDefPage.class, getShell(), "Add column definition", 
					new String[] {Util2.intToStr(SWTUtils.getColumnMaxValue(table, 0) + 1), "Source", "", "", "", "0", "0"}, 
					null)) {
				
				item = new TableItem (table, SWT.NONE);
				
				item.setText((String []) SWTUtils.getResult());
				table.select(table.indexOf(item));				
			}
			interop();  // Selection changes
		}
		else {
			if (SWTUtils.inputQuery(AddModifyColumnDefPage.class, getShell(), "Modify column definition", 
					SWTUtils.getText(item),
					null)) {					
				
				item.setText((String []) SWTUtils.getResult());					
				table.select(table.indexOf(item));
				interop();
			}
		}
	}

	protected void updateColumnWidths() {
		
		double[] columnPoints = {1.3, 2, 1.5, 2, 3, 1, 1};
		float pointsWidth = 0;
		
		for (int i = 0; i < table.getColumnCount(); i++)
			pointsWidth += ((i < columnPoints.length - 1) ? columnPoints[i]: 1);
			
		float coeff = table.getClientArea().width / pointsWidth;
		
		for (int i = 0; i < table.getColumnCount(); i++)
			table.getColumn(i).setWidth((int)(((i < columnPoints.length - 1) ? columnPoints[i]: 1) * coeff));
		
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents sub-classing of SWT components
	}

	public boolean canClose(boolean isOK) {
		
		return true;
	}

	public void interop() {
		
		SWTUtils.setAllEnabled(colDefs, defs.getSelection());
		num.setEnabled(fix.getSelection());
		
		btnModify.setEnabled(table.getItemCount() > 0 && table.getSelectionIndex() != -1);
		btnRemove.setEnabled(btnModify.getEnabled());			
	}

	public boolean load(IParameters parameters) {
		
		return true;
	}

	public boolean save(IParameters parameters) {
		
		return true;
	}
}

