package net.sf.okapi.ui.filters.html;

import net.sf.okapi.ui.filters.plaintext.common.IDialogPage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

public class ConditionalRuleEditorDialog extends Composite implements IDialogPage {
	private Button btnOk;
	private Button btnCancel;
	private ConditionalRuleEditorComposite conditionalRuleEditorComposite;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ConditionalRuleEditorDialog(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		
		conditionalRuleEditorComposite = new ConditionalRuleEditorComposite(this, SWT.NONE);
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 10);
		formData.right = new FormAttachment(100, -10);
		formData.left = new FormAttachment(0, 10);
		conditionalRuleEditorComposite.setLayoutData(formData);
		conditionalRuleEditorComposite.setData("name", "conditionalRuleEditorComposite");
		
		btnOk = new Button(this, SWT.NONE);
		formData.bottom = new FormAttachment(btnOk, -9);
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		FormData formData_3 = new FormData();
		formData_3.bottom = new FormAttachment(100, -10);
		formData_3.height = 25;
		formData_3.width = 75;
		btnOk.setLayoutData(formData_3);
		btnOk.setData("name", "btnOk");
		btnOk.setText("OK");
		
		btnCancel = new Button(this, SWT.NONE);
		formData_3.right = new FormAttachment(btnCancel, -6);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		FormData formData_4 = new FormData();
		formData_4.right = new FormAttachment(100, -10);
		formData_4.height = 25;
		formData_4.width = 75;
		formData_4.bottom = new FormAttachment(100, -10);
		btnCancel.setLayoutData(formData_4);
		btnCancel.setData("name", "btnCancel");
		btnCancel.setText("Cancel");

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
