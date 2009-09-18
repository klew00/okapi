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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class ProcessingInstructionRulesTab extends Composite {
	private Group grpProcessingInstructionRules;
	private Table table;
	private TableColumn tblclmnProcessingInstruction;
	private TableColumn tblclmnAction;
	private AddDeleteComposite addDeleteComposite;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ProcessingInstructionRulesTab(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		grpProcessingInstructionRules = new Group(this, SWT.NONE);
		grpProcessingInstructionRules.setLayout(new FillLayout(SWT.HORIZONTAL));
		grpProcessingInstructionRules.setText("Processing Instruction Rules");
		grpProcessingInstructionRules.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpProcessingInstructionRules.setData("name", "grpProcessingInstructionRules");
		
		table = new Table(grpProcessingInstructionRules, SWT.BORDER | SWT.FULL_SELECTION);
		table.setData("name", "table");
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		tblclmnProcessingInstruction = new TableColumn(table, SWT.CENTER);
		tblclmnProcessingInstruction.setData("name", "tblclmnProcessingInstruction");
		tblclmnProcessingInstruction.setWidth(133);
		tblclmnProcessingInstruction.setText("Processing Instruction");
		
		tblclmnAction = new TableColumn(table, SWT.CENTER);
		tblclmnAction.setData("name", "tblclmnAction");
		tblclmnAction.setWidth(296);
		tblclmnAction.setText("Action");
		
		addDeleteComposite = new AddDeleteComposite(this, SWT.NONE);
		addDeleteComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
