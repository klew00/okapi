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

package net.sf.okapi.filters.html.ui;

import net.sf.okapi.common.ui.abstracteditor.IDialogPage;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class PreserveWhitespaceRulesTab extends Composite implements IDialogPage {
	private Group grpTagsWhoseContent;
	private Table table;
	private TableColumn tblclmnTagName;
	private TableColumn tblclmnConditionalRules;
	private Group groupWhitespaceHandling;
	private Button button;
	private Button btnAlwaysPreserveWhitespace;
	private Group grpPreserveWhitespaceRules;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public PreserveWhitespaceRulesTab(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		grpTagsWhoseContent = new Group(this, SWT.NONE);
		grpTagsWhoseContent.setText(" Whitespace Options");
		grpTagsWhoseContent.setLayout(new GridLayout(1, false));
		grpTagsWhoseContent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpTagsWhoseContent.setData("name", "grpTagsWhoseContent");
		
		groupWhitespaceHandling = new Group(grpTagsWhoseContent, SWT.NONE);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gridData.widthHint = 413;
		groupWhitespaceHandling.setLayoutData(gridData);
		groupWhitespaceHandling.setData("name", "groupWhitespaceHandling");
		groupWhitespaceHandling.setLayout(new GridLayout(1, false));
		
		button = new Button(groupWhitespaceHandling, SWT.RADIO);
		button.setText("Collapse to Single Whitespace");
		button.setData("name", "button");
		
		btnAlwaysPreserveWhitespace = new Button(groupWhitespaceHandling, SWT.RADIO);
		btnAlwaysPreserveWhitespace.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnAlwaysPreserveWhitespace.setText("Preserve Whitespace");
		btnAlwaysPreserveWhitespace.setData("name", "btnAlwaysPreserveWhitespace");
		
		grpPreserveWhitespaceRules = new Group(grpTagsWhoseContent, SWT.NONE);
		grpPreserveWhitespaceRules.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpPreserveWhitespaceRules.setText("Preserve Whitespace Exception Rules");
		grpPreserveWhitespaceRules.setData("name", "grpPreserveWhitespaceRules");
		grpPreserveWhitespaceRules.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		table = new Table(grpPreserveWhitespaceRules, SWT.BORDER | SWT.FULL_SELECTION);
		table.setData("name", "table");
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		tblclmnTagName = new TableColumn(table, SWT.NONE);
		tblclmnTagName.setData("name", "tblclmnTagName");
		tblclmnTagName.setWidth(144);
		tblclmnTagName.setText("Tag Name");
		
		tblclmnConditionalRules = new TableColumn(table, SWT.NONE);
		tblclmnConditionalRules.setData("name", "tblclmnConditionalRules");
		tblclmnConditionalRules.setWidth(285);
		tblclmnConditionalRules.setText("Conditional Rules");

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
		return true;
	}

	public boolean save(Object data) {
		return true;
	}
}
