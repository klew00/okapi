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

package net.sf.okapi.steps.tokenization.ui.locale;

import java.util.ArrayList;

import net.sf.okapi.steps.tokenization.locale.LanguageList;
import net.sf.okapi.common.ui.abstracteditor.IDialogPage;
import net.sf.okapi.common.ui.abstracteditor.TableAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

public class LanguageListPage extends Composite implements IDialogPage {
	private Label lblChooseOneOr;
	private Table table;
	private TableColumn col1;
	private TableColumn col2;
	private TableAdapter adapter;
	private TableColumn col3;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public LanguageListPage(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		lblChooseOneOr = new Label(this, SWT.NONE);
		lblChooseOneOr.setData("name", "lblChooseOneOr");
		lblChooseOneOr.setText("Choose one or more languages from the table below (Ctrl+click, Ctrl+Shift+click for multiple selection):");
		
		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gridData.heightHint = 400;
		table.setLayoutData(gridData);
		table.setData("name", "table");
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		col1 = new TableColumn(table, SWT.NONE);
		col1.setData("name", "col1");
		col1.setWidth(348);
		col1.setText("Language");
		
		col2 = new TableColumn(table, SWT.NONE);
		col2.setData("name", "col2");
		col2.setWidth(150);
		col2.setText("Code Okapi");
		
		adapter = new TableAdapter(table);
		
		col3 = new TableColumn(table, SWT.NONE);
		col3.setData("name", "col3");
		col3.setWidth(150);
		col3.setText("Code CLDR");
		adapter.setRelColumnWidths(new double [] {6, 1.25, 1.25});
	}

	public boolean canClose(boolean isOK) {
		// TODO Auto-generated method stub
		return true;
	}

	public void interop(Widget speaker) {
		// TODO Auto-generated method stub
		
	}

	public boolean load(Object data) {
				
//		list.setItems(LanguageList.getLanguages());
//		list.pack();
		
		String [] languages = LanguageList.getLanguages();
		String [] codes = LanguageList.getLanguageCodes_Okapi();
		String [] codes2 = LanguageList.getLanguageCodes_ICU();
		
		for (int i = 0; i < Math.min(languages.length, codes.length); i++) {
			
			adapter.addRow(new String[] {languages[i], codes[i], codes2[i]}, false);
		}
		
		return true;
	}

	@SuppressWarnings("unchecked")
	public boolean save(Object data) {
		
		if (data instanceof ArrayList) {
		
			ArrayList<String> list = (ArrayList<String>) data;
			
			for (TableItem item : table.getSelection())			
				list.add(item.getText(1));
		}
				
		return true;
	}

}
