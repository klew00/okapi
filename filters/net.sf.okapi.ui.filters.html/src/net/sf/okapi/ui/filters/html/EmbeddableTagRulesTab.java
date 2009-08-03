package net.sf.okapi.ui.filters.html;

import net.sf.okapi.ui.filters.plaintext.common.IDialogPage;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class EmbeddableTagRulesTab extends Composite implements IDialogPage {
	private Group grpEmbeddableTags;
	private Table table;
	private TableColumn embeddableTagColumn;
	private TableColumn conditionalRulesColumn;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public EmbeddableTagRulesTab(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		grpEmbeddableTags = new Group(this, SWT.BORDER);
		grpEmbeddableTags.setLayout(new GridLayout(1, false));
		grpEmbeddableTags.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpEmbeddableTags.setText("Embeddable Tags");
		grpEmbeddableTags.setData("name", "grpEmbeddableTags");
		
		table = new Table(grpEmbeddableTags, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setData("name", "table");
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		embeddableTagColumn = new TableColumn(table, SWT.CENTER);
		embeddableTagColumn.setData("name", "embeddableTagColumn");
		embeddableTagColumn.setWidth(146);
		embeddableTagColumn.setText("EmbedableTag");
		
		conditionalRulesColumn = new TableColumn(table, SWT.CENTER);
		conditionalRulesColumn.setData("name", "conditionalRulesColumn");
		conditionalRulesColumn.setWidth(425);
		conditionalRulesColumn.setText("Conditional Rules");
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
