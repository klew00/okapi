package net.sf.okapi.ui.filters.abstractmarkup;

import net.sf.okapi.ui.common.dialogs.IDialogPage;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.List;

public class ConditionalRuleEditorDialog extends Composite implements IDialogPage {
	private Label lblTagName;
	private Combo tagNameCombo;
	private Group grpConditionalRule;
	private Button btnOk;
	private Button btnCancel;
	private Button btnAdd;
	private Button btnRemove;
	private List attributeRulesList;
	private Combo attributeCombo;
	private Combo booleanCompareCombo;
	private Combo attributeValueCombo;
	private Composite composite;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ConditionalRuleEditorDialog(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		
		lblTagName = new Label(this, SWT.NONE);
		FormData formData = new FormData();
		formData.height = 18;
		formData.width = 109;
		formData.top = new FormAttachment(0, 10);
		formData.left = new FormAttachment(0, 10);
		lblTagName.setLayoutData(formData);
		lblTagName.setData("name", "lblTagName");
		lblTagName.setText("Tag Name:");
		
		tagNameCombo = new Combo(this, SWT.NONE);
		FormData tagNameFormData = new FormData();
		tagNameFormData.right = new FormAttachment(100, -118);
		tagNameFormData.left = new FormAttachment(lblTagName, 6);
		tagNameFormData.top = new FormAttachment(0, 10);
		tagNameFormData.width = 194;
		tagNameCombo.setLayoutData(tagNameFormData);
		tagNameCombo.setData("name", "tagNameCombo");
		
		grpConditionalRule = new Group(this, SWT.NONE);
		grpConditionalRule.setLayout(new FormLayout());
		grpConditionalRule.setText("Conditional Rule");
		FormData formData_2 = new FormData();
		formData_2.top = new FormAttachment(lblTagName, 13);
		formData_2.left = new FormAttachment(0, 10);
		formData_2.right = new FormAttachment(100, -10);
		grpConditionalRule.setLayoutData(formData_2);
		grpConditionalRule.setData("name", "grpConditionalRule");
		
		composite = new Composite(grpConditionalRule, SWT.NONE);
		composite.setLayout(new GridLayout(3, true));
		FormData formData_1 = new FormData();
		formData_1.height = 51;
		formData_1.left = new FormAttachment(0, 10);
		formData_1.width = 537;
		composite.setLayoutData(formData_1);
		composite.setData("name", "composite");
		
		attributeCombo = new Combo(composite, SWT.NONE);
		attributeCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		attributeCombo.setData("name", "attributeCombo");
		
		booleanCompareCombo = new Combo(composite, SWT.NONE);
		booleanCompareCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		booleanCompareCombo.setItems(new String[] {"equal", "not equal", "regex"});
		booleanCompareCombo.setData("name", "booleanCompareCombo");
		
		attributeValueCombo = new Combo(composite, SWT.NONE);
		attributeValueCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		attributeValueCombo.setData("name", "attributeValueCombo");
		
		attributeRulesList = new List(grpConditionalRule, SWT.BORDER | SWT.V_SCROLL);
		formData_1.right = new FormAttachment(attributeRulesList, 0, SWT.RIGHT);
		FormData conditionalAttributes = new FormData();
		conditionalAttributes.top = new FormAttachment(0, 57);
		conditionalAttributes.left = new FormAttachment(0, 10);
		conditionalAttributes.right = new FormAttachment(100, -10);
		conditionalAttributes.height = 162;
		attributeRulesList.setLayoutData(conditionalAttributes);
		attributeRulesList.setData("name", "attributeRulesList");
		
		btnAdd = new Button(grpConditionalRule, SWT.NONE);
		conditionalAttributes.bottom = new FormAttachment(btnAdd, -16);
		FormData formData_8 = new FormData();
		formData_8.height = 25;
		formData_8.width = 75;
		btnAdd.setLayoutData(formData_8);
		btnAdd.setData("name", "btnAdd");
		btnAdd.setText("Add");
		
		btnRemove = new Button(grpConditionalRule, SWT.NONE);
		formData_8.bottom = new FormAttachment(btnRemove, 0, SWT.BOTTOM);
		formData_8.right = new FormAttachment(btnRemove, -13);
		FormData formData_9 = new FormData();
		formData_9.right = new FormAttachment(100, -10);
		formData_9.bottom = new FormAttachment(100, -10);
		formData_9.height = 25;
		formData_9.width = 75;
		btnRemove.setLayoutData(formData_9);
		btnRemove.setData("name", "btnRemove");
		btnRemove.setText("Remove");
		
		btnOk = new Button(this, SWT.NONE);
		formData_2.bottom = new FormAttachment(btnOk, -7);
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
