package net.sf.okapi.ui.filters.html;

import org.eclipse.swt.widgets.Composite;

public class GeneralOptions extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public GeneralOptions(Composite parent, int style) {
		super(parent, style);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
