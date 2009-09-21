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

public class AttributeRulesTab extends Composite implements IDialogPage {
	private Group grpAttributesWhichOccur;
	private Group grpTagsWithAttributes;
	private Table globalAttributeRulesTable;
	private TableColumn tblclmnAttrinuteName;
	private TableColumn tblclmnAllTagsExcept;
	private Table attributeRulesTable;
	private TableColumn tblclmnTagName;
	private TableColumn tblclmnConditionalRules;
	private TableColumn tblclmnConditionalRules_1;
	private AddDeleteComposite addDeleteCompositeRules;
	private AddDeleteComposite addDeleteCompositeGlobal;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public AttributeRulesTab(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		grpAttributesWhichOccur = new Group(this, SWT.NONE);
		grpAttributesWhichOccur.setLayout(new FillLayout(SWT.HORIZONTAL));
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gridData.heightHint = 138;
		grpAttributesWhichOccur.setLayoutData(gridData);
		grpAttributesWhichOccur.setToolTipText("Tags with Attributes");
		grpAttributesWhichOccur.setText("Global Attribute Rules");
		grpAttributesWhichOccur.setData("name", "grpAttributesWhichOccur");
		
		globalAttributeRulesTable = new Table(grpAttributesWhichOccur, SWT.BORDER | SWT.FULL_SELECTION);
		globalAttributeRulesTable.setData("name", "table");
		globalAttributeRulesTable.setHeaderVisible(true);
		globalAttributeRulesTable.setLinesVisible(true);
		
		tblclmnAttrinuteName = new TableColumn(globalAttributeRulesTable, SWT.NONE);
		tblclmnAttrinuteName.setData("name", "tblclmnAttrinuteName");
		tblclmnAttrinuteName.setWidth(182);
		tblclmnAttrinuteName.setText("Attrinute Name");
		
		tblclmnAllTagsExcept = new TableColumn(globalAttributeRulesTable, SWT.CENTER);
		tblclmnAllTagsExcept.setData("name", "tblclmnAllTagsExcept");
		tblclmnAllTagsExcept.setWidth(392);
		tblclmnAllTagsExcept.setText("All Tags Except...");
		
		addDeleteCompositeGlobal = new AddDeleteComposite(this, SWT.NONE);
		addDeleteCompositeGlobal.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		grpTagsWithAttributes = new Group(this, SWT.NONE);
		grpTagsWithAttributes.setLayout(new FillLayout(SWT.HORIZONTAL));
		GridData gridData_1 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gridData_1.heightHint = 250;
		grpTagsWithAttributes.setLayoutData(gridData_1);
		grpTagsWithAttributes.setText("Attribute Rules");
		grpTagsWithAttributes.setData("name", "grpTagsWithAttributes");
		
		attributeRulesTable = new Table(grpTagsWithAttributes, SWT.BORDER | SWT.FULL_SELECTION);
		attributeRulesTable.setData("name", "table_1");
		attributeRulesTable.setHeaderVisible(true);
		attributeRulesTable.setLinesVisible(true);
		
		tblclmnTagName = new TableColumn(attributeRulesTable, SWT.CENTER);
		tblclmnTagName.setData("name", "tblclmnTagName");
		tblclmnTagName.setWidth(181);
		tblclmnTagName.setText("Tag Name");
		
		tblclmnConditionalRules = new TableColumn(attributeRulesTable, SWT.CENTER);
		tblclmnConditionalRules.setText("Attributes");
		tblclmnConditionalRules.setData("name", "tblclmnConditionalRules");
		tblclmnConditionalRules.setWidth(121);
		
		tblclmnConditionalRules_1 = new TableColumn(attributeRulesTable, SWT.CENTER);
		tblclmnConditionalRules_1.setData("name", "tblclmnConditionalRules_1");
		tblclmnConditionalRules_1.setWidth(287);
		tblclmnConditionalRules_1.setText("Conditional Attributes");
		
		addDeleteCompositeRules = new AddDeleteComposite(this, SWT.NONE);
		addDeleteCompositeRules.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, false, 1, 1));

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
