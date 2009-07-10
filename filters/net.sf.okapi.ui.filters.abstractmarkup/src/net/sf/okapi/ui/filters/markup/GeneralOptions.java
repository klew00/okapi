package net.sf.okapi.ui.filters.markup;

import net.sf.okapi.ui.common.dialogs.IDialogPage;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class GeneralOptions extends Composite implements IDialogPage {
	private Group grpWhitespaceOptions;
	private Button btnCollapseToSingle;
	private Button btnPreserveWhitespace;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public GeneralOptions(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		grpWhitespaceOptions = new Group(this, SWT.NONE);
		GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gridData.heightHint = 64;
		gridData.widthHint = 194;
		grpWhitespaceOptions.setLayoutData(gridData);
		grpWhitespaceOptions.setText("Whitespace Handling");
		grpWhitespaceOptions.setData("name", "grpWhitespaceOptions");
		
		btnCollapseToSingle = new Button(grpWhitespaceOptions, SWT.RADIO);
		btnCollapseToSingle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnCollapseToSingle.setBounds(10, 20, 180, 16);
		btnCollapseToSingle.setData("name", "btnCollapseToSingle");
		btnCollapseToSingle.setText("Collapse to Single Whitespace");
		
		btnPreserveWhitespace = new Button(grpWhitespaceOptions, SWT.RADIO);
		btnPreserveWhitespace.setBounds(10, 42, 180, 16);
		btnPreserveWhitespace.setData("name", "btnPreserveWhitespace");
		btnPreserveWhitespace.setText("Preserve Whitespace");
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
