package net.sf.okapi.ui.filters.abstractmarkup;

import org.eclipse.swt.widgets.Composite;

public class ProcessingInstructionRulesTab extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ProcessingInstructionRulesTab(Composite parent, int style) {
		super(parent, style);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
