package net.sf.okapi.steps.tokenization.ui.engine;

import net.sf.okapi.common.BaseContext;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.ui.abstracteditor.AbstractParametersEditor;
import net.sf.okapi.steps.tokenization.common.CompoundStepParameters;
import net.sf.okapi.steps.tokenization.ui.common.CompoundStepItemsTab;

import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Widget;

public class Configurator extends AbstractParametersEditor {

	private static Configurator configurator = new Configurator();
	private static CompoundStepParameters params = null;
	
	public static void main(String[] args) {
		
		run();
	}
	
	@Override
	protected void createPages(TabFolder pageContainer) {
		
		addPage("Tokenizers", CompoundStepItemsTab.class);
	}

	@Override
	public IParameters createParameters() {

		return new CompoundStepParameters();
	}

	@Override
	protected String getCaption() {
		
		return "Tokenization step configurator";
	}

	@Override
	protected void interop(Widget speaker) {
		
		
	}

	public static boolean run() {
		
		if (configurator == null) return false;
		
		params = (CompoundStepParameters) configurator.createParameters();
		if (params == null) return false;
				
		return configurator.edit(params, false, new BaseContext());
	}
	
}