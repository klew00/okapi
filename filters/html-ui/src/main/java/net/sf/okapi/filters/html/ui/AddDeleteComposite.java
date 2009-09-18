package net.sf.okapi.filters.html.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class AddDeleteComposite extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public AddDeleteComposite(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(2, true);
		gridLayout.horizontalSpacing = 10;
		setLayout(gridLayout);
		{
			Button btnAdd = new Button(this, SWT.RIGHT);
			btnAdd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
			btnAdd.setAlignment(SWT.CENTER);
			btnAdd.setText("Add...");
		}
		{
			Button btnDelete = new Button(this, SWT.RIGHT);
			btnDelete.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
			btnDelete.setAlignment(SWT.CENTER);
			btnDelete.setText("Delete...");
		}
	}
}
