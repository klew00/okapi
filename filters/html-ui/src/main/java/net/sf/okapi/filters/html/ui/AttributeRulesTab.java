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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class AttributeRulesTab extends Composite implements IDialogPage {
	private Group grpAttributesWhichOccur;
	private Group grpTagsWithAttributes;
	private Table globalAttributeRulesTable;
	private TableColumn tblclmnAttrinuteName;
	private TableColumn tblclmnAllTagsExcept;
	private Table attributeRulesTable;
	private TableColumn tblclmnTagName;
	private TableColumn tblclmnAttribute;
	private TableColumn tblclmnConditionalRule;
	private AddDeleteComposite addDeleteCompositeRules;
	private TableColumn tblclmnAttributeType;
	private AddDeleteComposite addDeleteComposite;
	private TableColumn tblclmnAttributetype;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public AttributeRulesTab(final Composite parent, int style) {
		super(parent, style);		
		setLayout(new GridLayout(1, true));
		
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
		
		tblclmnAttributeType = new TableColumn(globalAttributeRulesTable, SWT.NONE);
		tblclmnAttributeType.setWidth(100);
		tblclmnAttributeType.setText("Attribute Type");
		
		tblclmnAllTagsExcept = new TableColumn(globalAttributeRulesTable, SWT.CENTER);
		tblclmnAllTagsExcept.setData("name", "tblclmnAllTagsExcept");
		tblclmnAllTagsExcept.setWidth(392);
		tblclmnAllTagsExcept.setText("All Tags Except...");
		
		addDeleteComposite = new AddDeleteComposite(this, SWT.NONE);
		addDeleteComposite.getBtnAdd().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {				
			}
		});
		addDeleteComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true, 1, 1));
		{
			GridData gridData_1 = (GridData) addDeleteComposite.getBtnDelete().getLayoutData();
			gridData_1.horizontalAlignment = SWT.RIGHT;
			gridData_1.verticalAlignment = SWT.TOP;
		}
		
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
		
		tblclmnAttribute = new TableColumn(attributeRulesTable, SWT.CENTER);
		tblclmnAttribute.setText("Attribute");
		tblclmnAttribute.setData("name", "tblclmnConditionalRules");
		tblclmnAttribute.setWidth(121);
		
		tblclmnAttributetype = new TableColumn(attributeRulesTable, SWT.NONE);
		tblclmnAttributetype.setWidth(100);
		tblclmnAttributetype.setText("Attribute Type");
		
		tblclmnConditionalRule = new TableColumn(attributeRulesTable, SWT.CENTER);
		tblclmnConditionalRule.setData("name", "tblclmnConditionalRules_1");
		tblclmnConditionalRule.setWidth(241);
		tblclmnConditionalRule.setText("Conditional Rules");
		
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
