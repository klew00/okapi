package net.sf.okapi.ui.filters.html;

import org.eclipse.swt.widgets.Composite;

public class PreserveWhitespaceRules extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public PreserveWhitespaceRules(Composite parent, int style) {
		super(parent, style);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
