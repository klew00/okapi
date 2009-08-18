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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Widget;

public class ContentExclusionRulesTab extends Composite implements IDialogPage {
	private Group grpTagExclusionRules;
	private Table table;
	private TableColumn tblclmnTagName;
	private TableColumn tblclmnRuleType;
	private TableColumn tblclmnConditionalRules;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ContentExclusionRulesTab(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		grpTagExclusionRules = new Group(this, SWT.NONE);
		grpTagExclusionRules.setLayout(new FillLayout(SWT.HORIZONTAL));
		grpTagExclusionRules.setText("Tag Exclusion Rules");
		grpTagExclusionRules.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpTagExclusionRules.setData("name", "grpTagExclusionRules");
		
		table = new Table(grpTagExclusionRules, SWT.BORDER | SWT.FULL_SELECTION);
		table.setData("name", "table");
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		tblclmnTagName = new TableColumn(table, SWT.CENTER);
		tblclmnTagName.setData("name", "tblclmnTagName");
		tblclmnTagName.setWidth(100);
		tblclmnTagName.setText("Tag Name");
		
		tblclmnRuleType = new TableColumn(table, SWT.CENTER);
		tblclmnRuleType.setData("name", "tblclmnRuleType");
		tblclmnRuleType.setWidth(71);
		tblclmnRuleType.setText("Rule Type");
		
		tblclmnConditionalRules = new TableColumn(table, SWT.CENTER);
		tblclmnConditionalRules.setData("name", "tblclmnConditionalRules");
		tblclmnConditionalRules.setWidth(259);
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
