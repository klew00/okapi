package net.sf.okapi.apptest;

import org.eclipse.swt.widgets.Shell;

import net.sf.okapi.apptest.dummyfilter.DummyFilter;
import net.sf.okapi.apptest.dummyfilter.DummyFilterWriter;
import net.sf.okapi.apptest.dummyutility.PseudoTranslate;
import net.sf.okapi.apptest.filters.FilterEvent;
import net.sf.okapi.apptest.filters.IFilter;
import net.sf.okapi.apptest.filters.IFilterWriter;
import net.sf.okapi.apptest.pipeline.ILinearPipeline;
import net.sf.okapi.apptest.pipeline.LinearPipeline;
import net.sf.okapi.apptest.pipeline.PipelineReturnValue;
import net.sf.okapi.apptest.pipelineutil.FilterStep;
import net.sf.okapi.apptest.pipelineutil.FilterWriterStep;
import net.sf.okapi.apptest.pipelineutil.UtilityStep;
import net.sf.okapi.apptest.skeleton.GenericSkeletonWriter;
import net.sf.okapi.apptest.skeleton.RTFFilterWriter;
import net.sf.okapi.apptest.utilities.IUtility;
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
	
	public void simpleExecute () {
		IFilter inFilter = null;
		IUtility util = null;
		IFilterWriter outFilter = null;
		IFilterWriter genWriter = null;
		try {
			inFilter = new DummyFilter();
			util = new PseudoTranslate();
			outFilter = new DummyFilterWriter();
			
			genWriter = new GenericSkeletonWriter();
			String ext = ".txt";
			String srcLang = "en";
			String trgLang = "en-bz";

			// Set the options
			util.setOptions(trgLang);
			inFilter.setOptions(srcLang, "UTF-8", true);
			outFilter.setOptions(trgLang, "UTF-8");
			outFilter.setOutput("myOutputFile");
			genWriter.setOptions(trgLang, "windows-1252");
			genWriter.setOutput("genericOutput"+ext);
			((GenericSkeletonWriter)genWriter).setOutputTarget(true);
			((GenericSkeletonWriter)genWriter).setEncoder(inFilter.getEncoder());

			// Process
			FilterEvent event;
			inFilter.open("myFile");
			while ( inFilter.hasNext() ) {
				event = inFilter.next();
				util.handleEvent(event);
				outFilter.handleEvent(event);
				genWriter.handleEvent(event);
			}
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getMessage(), null);
			e.printStackTrace();
		}
		finally {
			if ( util != null ) util.doEpilog();
			if ( outFilter != null ) outFilter.close();
			if ( inFilter != null ) inFilter.close();
			if ( genWriter != null ) genWriter.close();
		}
	}
	
	public void pipelineExecute (boolean allowUIInteraction) {
		try {
			ILinearPipeline pipeline = new LinearPipeline<FilterEvent>();
			
			FilterStep inputStep = new FilterStep(new DummyFilter());
			pipeline.addPipleLineStep(inputStep);
			inputStep.getFilter().setOptions("en", "UTF-8", true);
			inputStep.setInput("myFile");
			
			UtilityStep utility = new UtilityStep();
			utility.setUtility(new PseudoTranslate());
			pipeline.addPipleLineStep(utility);
			
			FilterWriterStep outputStep = new FilterWriterStep(new DummyFilterWriter());
			pipeline.addPipleLineStep(outputStep);
			outputStep.getFilterWriter().setOptions("en-bz", "UTF-8");
			outputStep.setOutput("myOutputFile");
			
			pipeline.execute();

			//String canceled = "";
			//int count = 0;
			if ( allowUIInteraction ) {
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
						//count++;
						if ( mf.isPaused() ) pipeline.pause();
						else if ( mf.isCanceled() ) {
							pipeline.cancel();
							//canceled = " (Canceled)";
						}
					}
				}
				//Dialogs.showError(shell, String.format("Done! count=%d"+canceled, count), null);
			}
			else { // No UI interaction
				while ( pipeline.getState() == PipelineReturnValue.RUNNING )
				{
					// Just wait for the end
				}				
			}
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getMessage(), null);
			e.printStackTrace();
		}
	}

}
