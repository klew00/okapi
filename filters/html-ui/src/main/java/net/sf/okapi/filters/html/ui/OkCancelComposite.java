package net.sf.okapi.filters.html.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class OkCancelComposite extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public OkCancelComposite(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(2, true);
		gridLayout.horizontalSpacing = 10;
		setLayout(gridLayout);
		{
			Button btnOK = new Button(this, SWT.RIGHT);
			btnOK.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
			btnOK.setAlignment(SWT.CENTER);
			btnOK.setText("OK");
		}
		{
			Button btnCancel = new Button(this, SWT.RIGHT);
			btnCancel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
			btnCancel.setAlignment(SWT.CENTER);
			btnCancel.setText("Cancel");
		}
	}
}
