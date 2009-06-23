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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

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
	private Group grpColumnDefinitions;
	private Composite composite;
	private Composite composite_1;
	private Button btnExtractByColumn;
	private Button btnExtractFromAll;
	private Button btnDoNotExtract;
	private Button btnFixedNumber;
	private Button btnDefinedByColumn;
	private Button btnDefinedByValues;
	private Spinner spinner;
	private Label label;
	private Group grpType;
	private Label lblSourceIndex;
	private Spinner spinner_1;
	private Label lblDefaultIdSuffix;
	private Text text;
	private Label lblLanguage;
	private Text text_1;
	private Spinner spinner_2;
	private Spinner spinner_3;
	private Label lblEndPosition;
	private Button btnAdd;
	private Button btnRemove;
	private Label lblSpacer;
	private Composite composite_2;
	private Button btnSourceText;
	private Button btnSourceId;
	private Button btnTargetText;
	private Button btnComment;
	private Button btnRecordId;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ColumnsTab(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));
		
		grpExtractionMode = new Group(this, SWT.NONE);
		grpExtractionMode.setText("Extraction mode");
		grpExtractionMode.setLayout(new GridLayout(1, false));
		grpExtractionMode.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		composite = new Composite(grpExtractionMode, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false, 1, 1));
		
		btnExtractByColumn = new Button(composite, SWT.RADIO);
		btnExtractByColumn.setText("Extract by column definitions");
		
		btnExtractFromAll = new Button(composite, SWT.RADIO);
		btnExtractFromAll.setText("Extract from all columns (create separate text units)");
		
		btnDoNotExtract = new Button(composite, SWT.RADIO);
		btnDoNotExtract.setText("Do not extract from any column");
		
		grpNumberOfColumns = new Group(this, SWT.NONE);
		grpNumberOfColumns.setText("Number of columns");
		grpNumberOfColumns.setLayout(new GridLayout(1, false));
		grpNumberOfColumns.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		composite_1 = new Composite(grpNumberOfColumns, SWT.NONE);
		composite_1.setLayout(new GridLayout(3, false));
		composite_1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		
		btnFixedNumber = new Button(composite_1, SWT.RADIO);
		btnFixedNumber.setText("Fixed number of columns");
		
		label = new Label(composite_1, SWT.NONE);
		label.setText("         ");
		
		spinner = new Spinner(composite_1, SWT.BORDER);
		
		btnDefinedByColumn = new Button(composite_1, SWT.RADIO);
		btnDefinedByColumn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		btnDefinedByColumn.setText("Defined by column names");
		
		btnDefinedByValues = new Button(composite_1, SWT.RADIO);
		btnDefinedByValues.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		btnDefinedByValues.setText("Defined by values (may vary in different rows)");
		
		grpColumnDefinitions = new Group(this, SWT.NONE);
		grpColumnDefinitions.setText("Column definitions");
		grpColumnDefinitions.setLayout(new GridLayout(4, false));
		grpColumnDefinitions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 2, 1));
		
		table = new Table(grpColumnDefinitions, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 10));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		tblclmnColumn = new TableColumn(table, SWT.RIGHT);
		tblclmnColumn.setWidth(72);
		tblclmnColumn.setText("Column #");
		
		tblclmnType = new TableColumn(table, SWT.NONE);
		tblclmnType.setWidth(118);
		tblclmnType.setText("Type");
		
		tblclmnSource = new TableColumn(table, SWT.NONE);
		tblclmnSource.setWidth(83);
		tblclmnSource.setText("Source Index");
		
		tblclmnLanguage = new TableColumn(table, SWT.NONE);
		tblclmnLanguage.setWidth(117);
		tblclmnLanguage.setText("Language");
		
		tblclmnSuffix = new TableColumn(table, SWT.NONE);
		tblclmnSuffix.setWidth(106);
		tblclmnSuffix.setText("Default ID Suffix");
		
		tblclmnStart = new TableColumn(table, SWT.NONE);
		tblclmnStart.setWidth(47);
		tblclmnStart.setText("Start");
		
		tblclmnEnd = new TableColumn(table, SWT.NONE);
		tblclmnEnd.setWidth(47);
		tblclmnEnd.setText("End");
		new Label(grpColumnDefinitions, SWT.NONE);
		
		grpType = new Group(grpColumnDefinitions, SWT.NONE);
		grpType.setLayout(new GridLayout(1, false));
		grpType.setText("Type");
		grpType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		
		composite_2 = new Composite(grpType, SWT.NONE);
		composite_2.setLayout(new GridLayout(1, false));
		composite_2.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false, 1, 1));
		
		btnSourceText = new Button(composite_2, SWT.RADIO);
		btnSourceText.setText("Source text");
		
		btnSourceId = new Button(composite_2, SWT.RADIO);
		btnSourceId.setText("Source ID");
		
		btnTargetText = new Button(composite_2, SWT.RADIO);
		btnTargetText.setText("Target text");
		
		btnComment = new Button(composite_2, SWT.RADIO);
		btnComment.setText("Comment");
		
		btnRecordId = new Button(composite_2, SWT.RADIO);
		btnRecordId.setText("Record ID");
		new Label(grpColumnDefinitions, SWT.NONE);
		
		lblSourceIndex = new Label(grpColumnDefinitions, SWT.NONE);
		lblSourceIndex.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSourceIndex.setText("Source index:");
		
		spinner_1 = new Spinner(grpColumnDefinitions, SWT.BORDER);
		new Label(grpColumnDefinitions, SWT.NONE);
		
		lblDefaultIdSuffix = new Label(grpColumnDefinitions, SWT.NONE);
		lblDefaultIdSuffix.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDefaultIdSuffix.setText("Default ID suffix:");
		
		text = new Text(grpColumnDefinitions, SWT.BORDER);
		new Label(grpColumnDefinitions, SWT.NONE);
		
		lblLanguage = new Label(grpColumnDefinitions, SWT.NONE);
		lblLanguage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLanguage.setText("Language:");
		
		text_1 = new Text(grpColumnDefinitions, SWT.BORDER);
		new Label(grpColumnDefinitions, SWT.NONE);
		Label lblStartPosition = new Label(grpColumnDefinitions, SWT.NONE);
		lblStartPosition.setText("Start position:");
		lblStartPosition.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		spinner_2 = new Spinner(grpColumnDefinitions, SWT.BORDER);
		new Label(grpColumnDefinitions, SWT.NONE);
		
		lblEndPosition = new Label(grpColumnDefinitions, SWT.NONE);
		lblEndPosition.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblEndPosition.setText("End position:");
		
		spinner_3 = new Spinner(grpColumnDefinitions, SWT.BORDER);
		new Label(grpColumnDefinitions, SWT.NONE);
		new Label(grpColumnDefinitions, SWT.NONE);
		new Label(grpColumnDefinitions, SWT.NONE);
		new Label(grpColumnDefinitions, SWT.NONE);
		new Label(grpColumnDefinitions, SWT.NONE);
		new Label(grpColumnDefinitions, SWT.NONE);
		new Label(grpColumnDefinitions, SWT.NONE);
		
		btnAdd = new Button(grpColumnDefinitions, SWT.NONE);
		btnAdd.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false, true, 1, 1));
		btnAdd.setText("Add");
		new Label(grpColumnDefinitions, SWT.NONE);
		new Label(grpColumnDefinitions, SWT.NONE);
		
		btnRemove = new Button(grpColumnDefinitions, SWT.NONE);
		btnRemove.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false, false, 1, 1));
		btnRemove.setText("Remove");
		new Label(grpColumnDefinitions, SWT.NONE);
		
		lblSpacer = new Label(grpColumnDefinitions, SWT.NONE);
		new Label(grpColumnDefinitions, SWT.NONE);
		new Label(grpColumnDefinitions, SWT.NONE);
		new Label(grpColumnDefinitions, SWT.NONE);
		
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public boolean canClose(boolean isOK) {
		
		return true;
	}

	public void interop() {
		// TODO Auto-generated method stub
		
	}

	public boolean load(IParameters parameters) {
		
		return true;
	}

	public boolean save(IParameters parameters) {
		
		return true;
	}
}

