package net.sf.okapi.ui.filters.html;

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

public class PreserveWhitespaceRulesTab extends Composite implements IDialogPage {
	private Group grpTagsWhoseContent;
	private Table table;
	private TableColumn tblclmnTagName;
	private TableColumn tblclmnConditionalRules;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public PreserveWhitespaceRulesTab(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		grpTagsWhoseContent = new Group(this, SWT.NONE);
		grpTagsWhoseContent.setLayout(new FillLayout(SWT.HORIZONTAL));
		grpTagsWhoseContent.setText("Preserve Whitespace Rules");
		grpTagsWhoseContent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpTagsWhoseContent.setData("name", "grpTagsWhoseContent");
		
		table = new Table(grpTagsWhoseContent, SWT.BORDER | SWT.FULL_SELECTION);
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
