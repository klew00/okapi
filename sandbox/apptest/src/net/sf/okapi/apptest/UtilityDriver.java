package net.sf.okapi.apptest;

import org.eclipse.swt.widgets.Shell;

import net.sf.okapi.apptest.filters.DummyInputFilter;
import net.sf.okapi.apptest.filters.DummyOutputFilter;
import net.sf.okapi.apptest.filters.OutputFilterStep;
import net.sf.okapi.apptest.utilities.PseudoTranslate;
import net.sf.okapi.apptest.utilities.UtilityStep;
import net.sf.okapi.common.pipeline2.ILinearPipeline;
import net.sf.okapi.common.pipeline2.LinearPipeline;
import net.sf.okapi.common.pipeline2.PipelineReturnValue;
import net.sf.okapi.common.ui.Dialogs;

public class UtilityDriver {

	Shell shell;
	MainForm mf;
	
	public UtilityDriver (Shell shell,
		MainForm main)
	{
		this.shell = shell;
		mf = main;
	}
	
	public void execute (String utilityId) {
		try {
			ILinearPipeline pipeline = new LinearPipeline();
			pipeline.addPipleLineStep(new DummyInputFilter());
			
			UtilityStep utility = new UtilityStep();
			utility.setUtility(new PseudoTranslate());
			pipeline.addPipleLineStep(utility);
			
			OutputFilterStep outputFilter = new OutputFilterStep();
			outputFilter.setOutputFilter(new DummyOutputFilter());
			pipeline.addPipleLineStep(outputFilter);
			
			pipeline.execute();

			String canceled = "";
			int count = 0;
			while ( pipeline.getState() == PipelineReturnValue.RUNNING
				|| pipeline.getState() == PipelineReturnValue.PAUSED )
			{
				if ( !shell.isDisposed() ) {
					if ( !shell.getDisplay().readAndDispatch() ) {
						shell.getDisplay().sleep();
					}
				}
				if ( pipeline.getState() == PipelineReturnValue.PAUSED ) {
					if ( mf.inProgress() ) pipeline.resume();
				}
				else {
					count++;
					if ( mf.isPaused() ) pipeline.pause();
					else if ( mf.isCanceled() ) {
						pipeline.cancel();
						canceled = " (Canceled)";
					}
					/*else if ( count == 1 ) {
						mf.pause();
						pipeline.pause();
					}*/
				}
			}
			
			Dialogs.showError(shell, String.format("Done! count=%d"+canceled, count), null);
			mf.makeIdle();
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getMessage(), null);
			e.printStackTrace();
		}
	}
}
