package net.sf.okapi.ui.filters.abstractmarkup;

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

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
