package net.sf.okapi.ui.filters.abstractmarkup;

import net.sf.okapi.ui.common.dialogs.IDialogPage;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class EmbeddableTagRulesTab extends Composite implements IDialogPage {
	private Group grpEmbeddableTags;
	private Table table;
	private TableColumn embeddableTagColumn;
	private TableColumn conditionalRuleColumn;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public EmbeddableTagRulesTab(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		grpEmbeddableTags = new Group(this, SWT.NONE);
		grpEmbeddableTags.setLayout(new GridLayout(1, false));
		grpEmbeddableTags.setText("Embeddable Tags");
		grpEmbeddableTags.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpEmbeddableTags.setData("name", "grpEmbeddableTags");
		
		table = new Table(grpEmbeddableTags, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true, 1, 1));
		table.setData("name", "table");
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		embeddableTagColumn = new TableColumn(table, SWT.NONE);
		embeddableTagColumn.setData("name", "embeddableTagColumn");
		embeddableTagColumn.setWidth(100);
		embeddableTagColumn.setText("Embeddable tag");
		
		conditionalRuleColumn = new TableColumn(table, SWT.NONE);
		conditionalRuleColumn.setData("name", "conditionalRuleColumn");
		conditionalRuleColumn.setWidth(100);
		conditionalRuleColumn.setText("Conditional Rules");

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
