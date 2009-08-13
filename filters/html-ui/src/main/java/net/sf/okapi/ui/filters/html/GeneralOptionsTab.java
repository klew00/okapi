package net.sf.okapi.ui.filters.html;


import net.sf.okapi.common.ui.abstracteditor.IDialogPage;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;

public class GeneralOptionsTab extends Composite implements IDialogPage {
	private Group grpWhitespaceOptions;
	private Button btnCollapseToSingle;
	private Button btnPreserveWhitespace;
	private Group grpSubFilterOptions;
	private Combo elementSubFilterCombo;
	private Combo combo_1;
	private Label lblElementSubFilter;
	private Label lblCdataSubFilter;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public GeneralOptionsTab(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		grpWhitespaceOptions = new Group(this, SWT.NONE);
		grpWhitespaceOptions.setLayout(new GridLayout(1, false));
		GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1);
		gridData.heightHint = 86;
		gridData.widthHint = 396;
		grpWhitespaceOptions.setLayoutData(gridData);
		grpWhitespaceOptions.setText("Whitespace Handling");
		grpWhitespaceOptions.setData("name", "grpWhitespaceOptions");
		
		btnCollapseToSingle = new Button(grpWhitespaceOptions, SWT.RADIO);
		btnCollapseToSingle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnCollapseToSingle.setData("name", "btnCollapseToSingle");
		btnCollapseToSingle.setText("Collapse to Single Whitespace");
		
		btnPreserveWhitespace = new Button(grpWhitespaceOptions, SWT.RADIO);
		btnPreserveWhitespace.setData("name", "btnPreserveWhitespace");
		btnPreserveWhitespace.setText("Preserve Whitespace");
		
		grpSubFilterOptions = new Group(this, SWT.NONE);
		grpSubFilterOptions.setLayout(new GridLayout(1, false));
		GridData gridData_1 = new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1);
		gridData_1.heightHint = 127;
		gridData_1.widthHint = 393;
		grpSubFilterOptions.setLayoutData(gridData_1);
		grpSubFilterOptions.setText("Sub Filter Options");
		grpSubFilterOptions.setData("name", "grpSubFilterOptions");
		
		lblElementSubFilter = new Label(grpSubFilterOptions, SWT.NONE);
		lblElementSubFilter.setData("name", "lblElementSubFilter");
		lblElementSubFilter.setText("Element Sub Filter");
		
		elementSubFilterCombo = new Combo(grpSubFilterOptions, SWT.READ_ONLY);
		elementSubFilterCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		elementSubFilterCombo.setData("name", "elementSubFilterCombo");
		
		lblCdataSubFilter = new Label(grpSubFilterOptions, SWT.NONE);
		lblCdataSubFilter.setData("name", "lblCdataSubFilter");
		lblCdataSubFilter.setText("CDATA Sub Filter");
		
		combo_1 = new Combo(grpSubFilterOptions, SWT.READ_ONLY);
		combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		combo_1.setData("name", "combo_1");
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
