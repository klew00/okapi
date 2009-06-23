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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * 
 * @version 0.1, 20.06.2009
 * @author Sergei Vasilyev
 */

public class TableTab extends Composite implements IParametersEditorPage {
	private Group grpTableType;
	private Composite composite;
	private Button btnCsvcolumnsSeparated;
	private Button btnTsvcolumnsSeparated;
	private Button btnFixedwidthColumns;
	private Group grpTableProperties;
	private Composite composite_1;
	private Label lblValuesStartAt;
	private Spinner spinner;
	private Spinner spinner_1;
	private Label lblLineWithColumn;
	private Button btnSendHeaderLines;
	private Button btnAll;
	private Button btnColumnNamesOnly;
	private Group grpCsvOptions;
	private Composite composite_2;
	private Label lblFieldDelimiter;
	private Combo combo;
	private Text text;
	private Label lblTextQualifier;
	private Combo combo_1;
	private Label lblStart;
	private Text text_1;
	private Label lblEnd;
	private Text text_2;
	private Button btnRemoveQualifiers;
	private Button btnNonqualifiedOnly;
	private Label label;
	private Button btnTrimValuesbound;
	private Button btnAll_1;
	private Label label_2;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public TableTab(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		grpTableType = new Group(this, SWT.NONE);
		grpTableType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpTableType.setLayout(new GridLayout(1, false));
		grpTableType.setText("Table type");
		
		composite = new Composite(grpTableType, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, true, 1, 1));
		
		btnCsvcolumnsSeparated = new Button(composite, SWT.RADIO);
		btnCsvcolumnsSeparated.setBounds(4, 4, 314, 16);
		btnCsvcolumnsSeparated.setText("CSV (Columns, separated by a comma, semicolon, etc.)");
		
		btnTsvcolumnsSeparated = new Button(composite, SWT.RADIO);
		btnTsvcolumnsSeparated.setBounds(4, 24, 314, 16);
		btnTsvcolumnsSeparated.setText("TSV (Columns, separated by one or more tabs)");
		
		btnFixedwidthColumns = new Button(composite, SWT.RADIO);
		btnFixedwidthColumns.setBounds(4, 44, 314, 16);
		btnFixedwidthColumns.setText("Fixed-width columns");
		
		grpTableProperties = new Group(this, SWT.NONE);
		grpTableProperties.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpTableProperties.setText("Table properties");
		grpTableProperties.setLayout(new GridLayout(1, false));
		
		composite_1 = new Composite(grpTableProperties, SWT.NONE);
		composite_1.setLayout(new GridLayout(15, false));
		composite_1.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false, 1, 1));
		
		lblValuesStartAt = new Label(composite_1, SWT.NONE);
		lblValuesStartAt.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblValuesStartAt.setAlignment(SWT.RIGHT);
		lblValuesStartAt.setText("Values start at line:");
		
		spinner = new Spinner(composite_1, SWT.BORDER);
		spinner.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		
		lblLineWithColumn = new Label(composite_1, SWT.NONE);
		lblLineWithColumn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLineWithColumn.setText("Line with column names (0 if none):");
		
		spinner_1 = new Spinner(composite_1, SWT.BORDER);
		spinner_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		
		btnSendHeaderLines = new Button(composite_1, SWT.CHECK);
		btnSendHeaderLines.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnSendHeaderLines.setText("Send header lines");
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		
		label_2 = new Label(composite_1, SWT.NONE);
		label_2.setText("    ");
		
		btnAll = new Button(composite_1, SWT.RADIO);
		btnAll.setText("All");
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		
		btnColumnNamesOnly = new Button(composite_1, SWT.RADIO);
		btnColumnNamesOnly.setText("Column names only");
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		
		grpCsvOptions = new Group(this, SWT.NONE);
		grpCsvOptions.setLayout(new GridLayout(1, false));
		grpCsvOptions.setText("CSV options");
		grpCsvOptions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		
		composite_2 = new Composite(grpCsvOptions, SWT.NONE);
		composite_2.setLayout(new GridLayout(9, false));
		composite_2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		
		lblFieldDelimiter = new Label(composite_2, SWT.NONE);
		lblFieldDelimiter.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFieldDelimiter.setText("Field delimiter:");
		
		combo = new Combo(composite_2, SWT.READ_ONLY);
		combo.setItems(new String[] {",", ";", "tab", "space", "Custom"});
		combo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		new Label(composite_2, SWT.NONE);
		
		lblTextQualifier = new Label(composite_2, SWT.NONE);
		lblTextQualifier.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTextQualifier.setText("Text qualifier:");
		
		combo_1 = new Combo(composite_2, SWT.READ_ONLY);
		combo_1.setItems(new String[] {"\"", "'", "none", "Custom"});
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		Label label_1 = new Label(composite_2, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		text = new Text(composite_2, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		new Label(composite_2, SWT.NONE);
		
		lblStart = new Label(composite_2, SWT.NONE);
		lblStart.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblStart.setText("Start");
		
		text_1 = new Text(composite_2, SWT.BORDER);
		
		lblEnd = new Label(composite_2, SWT.NONE);
		lblEnd.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblEnd.setText("End");
		
		text_2 = new Text(composite_2, SWT.BORDER);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		
		btnRemoveQualifiers = new Button(composite_2, SWT.CHECK);
		btnRemoveQualifiers.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnRemoveQualifiers.setText("Remove qualifiers");
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		
		btnTrimValuesbound = new Button(composite_2, SWT.CHECK);
		btnTrimValuesbound.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 7, 1));
		btnTrimValuesbound.setText("Trim values (bound with Options/Preserve white spaces)");
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		
		label = new Label(composite_2, SWT.NONE);
		label.setText("   ");
		
		btnNonqualifiedOnly = new Button(composite_2, SWT.RADIO);
		btnNonqualifiedOnly.setText("Non-qualified only");
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		
		btnAll_1 = new Button(composite_2, SWT.RADIO);
		btnAll_1.setText("All");
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public boolean canClose(boolean isOK) {
		
		return true;
	}

	public void interop() {
		
	}

	public boolean load(IParameters parameters) {

		return true;
	}

	public boolean save(IParameters parameters) {

		return true;
	}
}

