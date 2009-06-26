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
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * 
 * 
 * @version 0.1, 20.06.2009
 * @author Sergei Vasilyev
 */

public class TableTab extends Composite implements IParametersEditorPage {
	private Group grpTableType;
	private Button btnCSV;
	private Button btnTSV;
	private Button btnFWC;
	private Group grpTableProperties;
	private Label lblValuesStartAt;
	private Spinner start;
	private Spinner cols;
	private Label lcols;
	private Group csvOptions;
	private Button removeQualif;
	private Button nqualif;
	private Button trim;
	private Button allT;
	private Group extr;
	private Button header;
	private Button names;
	private Button allE;
	private Button body;
	private Group csvActions;
	private FormData formData_1;
	private FormData formData_2;
	private FormData formData_3;
	private FormData formData_4;
	private FormData formData_5;
	private FormData formData_12;
	private FormData formData_13;
	private FormData formData_14;
	private FormData formData_15;
	private FormData formData_16;
	private FormData formData_17;
	private FormData formData_9;
	private Text custDelim;
	private Text qualifStart;
	private Text qualifEnd;
	private Combo delim;
	private Combo qualif;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public TableTab(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(3, false));
		
		grpTableType = new Group(this, SWT.NONE);
		grpTableType.setLayout(new FormLayout());
		grpTableType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		grpTableType.setText("Table type");
		
		btnCSV = new Button(grpTableType, SWT.RADIO);
		btnCSV.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				interop();
			}
		});
		{
			formData_4 = new FormData();
			formData_4.left = new FormAttachment(0, 10);
			formData_4.width = 372;
			btnCSV.setLayoutData(formData_4);
		}
		btnCSV.setText("CSV (Columns, separated by a comma, semicolon, etc.)                  ");
		
		btnTSV = new Button(grpTableType, SWT.RADIO);
		btnTSV.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				interop();
			}
		});
		formData_4.right = new FormAttachment(btnTSV, 0, SWT.RIGHT);
		formData_4.bottom = new FormAttachment(btnTSV, -6);
		{
			formData_5 = new FormData();
			formData_5.left = new FormAttachment(0, 10);
			formData_5.right = new FormAttachment(100, -10);
			formData_5.top = new FormAttachment(0, 32);
			btnTSV.setLayoutData(formData_5);
		}
		btnTSV.setText("TSV (Columns, separated by one or more tabs)");
		
		btnFWC = new Button(grpTableType, SWT.RADIO);
		btnFWC.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				interop();
			}
		});
		{
			FormData formData = new FormData();
			formData.left = new FormAttachment(0, 10);
			formData.right = new FormAttachment(100, -10);
			formData.top = new FormAttachment(btnTSV, 6);
			btnFWC.setLayoutData(formData);
		}
		btnFWC.setText("Fixed-width columns");
		
		grpTableProperties = new Group(this, SWT.NONE);
		grpTableProperties.setLayout(new FormLayout());
		grpTableProperties.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpTableProperties.setText("Table properties");
		
		lblValuesStartAt = new Label(grpTableProperties, SWT.NONE);
		{
			formData_3 = new FormData();
			formData_3.left = new FormAttachment(0, 10);
			formData_3.width = 111;
			lblValuesStartAt.setLayoutData(formData_3);
		}
		lblValuesStartAt.setAlignment(SWT.RIGHT);
		lblValuesStartAt.setText("Values start at line:");
		
		start = new Spinner(grpTableProperties, SWT.BORDER);
		start.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				interop();
			}
		});
		formData_3.right = new FormAttachment(start, -6);
		formData_3.top = new FormAttachment(start, 0, SWT.TOP);
		{
			formData_1 = new FormData();
			formData_1.top = new FormAttachment(0, 10);
			formData_1.right = new FormAttachment(100, -10);
			start.setLayoutData(formData_1);
		}
		start.setMinimum(1);
		
		lcols = new Label(grpTableProperties, SWT.NONE);
		lcols.setAlignment(SWT.RIGHT);
		{
			formData_2 = new FormData();
			formData_2.left = new FormAttachment(lblValuesStartAt, 0, SWT.LEFT);
			formData_2.width = 186;
			lcols.setLayoutData(formData_2);
		}
		lcols.setText("Line with column names (0 if none):");
		
		cols = new Spinner(grpTableProperties, SWT.BORDER);
		cols.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				interop();
			}
		});
		formData_2.top = new FormAttachment(cols, 0, SWT.TOP);
		formData_2.right = new FormAttachment(cols, -6);
		{
			FormData formData = new FormData();
			formData.top = new FormAttachment(start, 8);
			formData.right = new FormAttachment(start, 0, SWT.RIGHT);
			cols.setLayoutData(formData);
		}
		
		csvOptions = new Group(this, SWT.NONE);
		csvOptions.setLayout(new GridLayout(1, false));
		csvOptions.setText("CSV options");
		csvOptions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		{
			Composite composite = new Composite(csvOptions, SWT.NONE);
			composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			composite.setLayout(new GridLayout(2, false));
			{
				Label label = new Label(composite, SWT.NONE);
				label.setText("Field delimiter:");
				label.setAlignment(SWT.RIGHT);
				label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			}
			{
				delim = new Combo(composite, SWT.READ_ONLY);
				delim.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						
						interop();
						if (custDelim.getEnabled()) custDelim.setFocus();
					}
				});
				delim.setItems(new String[] {"Comma (,)", "Semi-colon (;)", "Tab", "Space", "Custom"});
				delim.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
				delim.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
				delim.select(0);
			}
			Label label = new Label(composite, SWT.NONE);
			label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			{
				custDelim = new Text(composite, SWT.BORDER);
				custDelim.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
				custDelim.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
			}
			new Label(composite, SWT.NONE);
			new Label(composite, SWT.NONE);
			{
				Label label_1 = new Label(composite, SWT.NONE);
				label_1.setText("Text qualifier:");
				label_1.setAlignment(SWT.RIGHT);
				label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			}
			{
				qualif = new Combo(composite, SWT.READ_ONLY);
				qualif.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						
						interop();
						if (qualifStart.getEnabled()) qualifStart.setFocus();
					}
				});
				qualif.setItems(new String[] {"Double-quote (\")", "Apostrophe (')", "None", "Custom"});
				qualif.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
				qualif.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
				qualif.select(0);
			}
			{
				Label label_1 = new Label(composite, SWT.NONE);
				label_1.setText("Start:");
				label_1.setAlignment(SWT.RIGHT);
				label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			}
			{
				qualifStart = new Text(composite, SWT.BORDER);
				qualifStart.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
				qualifStart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
			}
			{
				Label label_1 = new Label(composite, SWT.NONE);
				label_1.setText("End:");
				label_1.setAlignment(SWT.RIGHT);
				label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			}
			{
				qualifEnd = new Text(composite, SWT.BORDER);
				qualifEnd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
				qualifEnd.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
			}
		}
		
		csvActions = new Group(this, SWT.NONE);
		csvActions.setLayout(new FormLayout());
		csvActions.setText("CSV actions");
		csvActions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		removeQualif = new Button(csvActions, SWT.CHECK);
		{
			formData_12 = new FormData();
			formData_12.left = new FormAttachment(0, 10);
			formData_12.right = new FormAttachment(100, -10);
			formData_12.top = new FormAttachment(0, 10);
			removeQualif.setLayoutData(formData_12);
		}
		removeQualif.setText("Remove qualifiers");
		
		trim = new Button(csvActions, SWT.CHECK);
		trim.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				interop();
			}
		});
		{
			formData_13 = new FormData();
			formData_13.left = new FormAttachment(removeQualif, 0, SWT.LEFT);
			formData_13.top = new FormAttachment(removeQualif, 6);
			formData_13.right = new FormAttachment(100, -10);
			trim.setLayoutData(formData_13);
		}
		trim.setText("Trim values");
		
		nqualif = new Button(csvActions, SWT.RADIO);
		{
			formData_14 = new FormData();
			formData_14.left = new FormAttachment(trim, 10, SWT.LEFT);
			formData_14.top = new FormAttachment(trim, 6);
			formData_14.right = new FormAttachment(100, -10);
			nqualif.setLayoutData(formData_14);
		}
		nqualif.setText("Only entries without qualifiers    ");
		
		allT = new Button(csvActions, SWT.RADIO);
		{
			FormData formData = new FormData();
			formData.left = new FormAttachment(nqualif, 0, SWT.LEFT);
			formData.top = new FormAttachment(0, 76);
			formData.right = new FormAttachment(100, -10);
			allT.setLayoutData(formData);
		}
		allT.setText("All");
		
		extr = new Group(this, SWT.NONE);
		extr.setLayout(new FormLayout());
		extr.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		extr.setText("Extraction mode");
		
		header = new Button(extr, SWT.CHECK);
		header.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				interop();
			}
		});
		{
			formData_15 = new FormData();
			formData_15.top = new FormAttachment(0, 10);
			formData_15.left = new FormAttachment(0, 10);
			formData_15.right = new FormAttachment(100, -10);
			header.setLayoutData(formData_15);
		}
		header.setText("Extract header lines");
		
		names = new Button(extr, SWT.RADIO);
		{
			formData_16 = new FormData();
			formData_16.left = new FormAttachment(header, 10, SWT.LEFT);
			formData_16.top = new FormAttachment(header, 6);
			formData_16.right = new FormAttachment(100, -10);
			names.setLayoutData(formData_16);
		}
		names.setText("Column names only         ");
		
		allE = new Button(extr, SWT.RADIO);
		{
			formData_17 = new FormData();
			formData_17.left = new FormAttachment(names, 0, SWT.LEFT);
			formData_17.top = new FormAttachment(names, 6);
			formData_17.right = new FormAttachment(100, -10);
			allE.setLayoutData(formData_17);
		}
		allE.setText("All");
		
		body = new Button(extr, SWT.CHECK);
		{
			FormData formData = new FormData();
			formData.left = new FormAttachment(header, 0, SWT.LEFT);
			formData.top = new FormAttachment(allE, 6);
			formData.right = new FormAttachment(100, -10);
			body.setLayoutData(formData);
		}
		body.setText("Extract table data");

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public boolean canClose(boolean isOK) {
		
		return true;
	}

	public void interop() {

		SWTUtils.setAllEnabled(csvOptions, btnCSV.getSelection());
		SWTUtils.setAllEnabled(csvActions, btnCSV.getSelection());
		
		custDelim.setEnabled(delim.getSelectionIndex() == 4);		
		
		qualifStart.setEnabled(qualif.getSelectionIndex() == 3);
		qualifEnd.setEnabled(qualif.getSelectionIndex() == 3);		
		
		boolean noQualif = qualif.getSelectionIndex() == 2;
		boolean trimOn = trim.getSelection() && csvActions.getEnabled();
		boolean noHeader = start.getSelection() <= 1;
		boolean headerOn = header.getSelection();
		boolean noColNames = cols.getSelection() == 0;
		
		if (noQualif || !csvActions.getEnabled()) removeQualif.setSelection(false);		
		removeQualif.setEnabled(!noQualif && csvActions.getEnabled());
		
		//----------------------------
		if (noQualif) {
			
			nqualif.setSelection(false);
			nqualif.setEnabled(false);
						
			allT.setEnabled(false);
			allT.setSelection(false);
			
//			trim.setSelection(false);
//			trim.setEnabled(false);
		}
		else {
						
			nqualif.setEnabled(trimOn);
			allT.setEnabled(trimOn);
			
		}

//		trim.setEnabled(csvActions.getEnabled());
		
		if (!trimOn) {
			
			trim.setSelection(false);
			nqualif.setSelection(false);
			allT.setSelection(false);
			
			nqualif.setEnabled(false);
			allT.setEnabled(false);
		}
		else {
			
			nqualif.setEnabled(!noQualif && csvActions.getEnabled());
			allT.setEnabled(csvActions.getEnabled());
			
			if (!nqualif.getSelection() && !allT.getSelection()) { // Default selection if none selected
				
				if (nqualif.getEnabled()) {
					
					nqualif.setSelection(true);
					allT.setSelection(false);
				}
				else {
					
					nqualif.setSelection(false);
					allT.setSelection(true);
				}
			}
		}
		
		//----------------------------
		if (noHeader) {
					
			names.setSelection(false);
			names.setEnabled(false);
			
			allE.setSelection(false);
			allE.setEnabled(false);
		
			header.setSelection(false);
			header.setEnabled(false);
			
			cols.setSelection(0);
			cols.setEnabled(false);
			lcols.setEnabled(false);
		}
		else {
		
			lcols.setEnabled(true);
			cols.setEnabled(true);
			header.setEnabled(true);
			
			names.setEnabled(headerOn);
			allE.setEnabled(headerOn);

			if (!headerOn) {
				
				names.setSelection(false);
				allE.setSelection(false);
			}
			else {
			
				if (!names.getSelection() && !allE.getSelection()) { // Default selection if none selected
					
					names.setSelection(headerOn);
					allE.setSelection(false);
				}
			}
							
		}
		
		cols.setMaximum(start.getSelection() - 1);
		
		if (noColNames) {
			
			names.setSelection(false);
			allE.setSelection(allE.getEnabled());
			names.setEnabled(false);
		}
		else {
			
			names.setEnabled(headerOn);
		}
	}

	public boolean load(IParameters parameters) {

		return true;
	}

	public boolean save(IParameters parameters) {

		return true;
	}
}

