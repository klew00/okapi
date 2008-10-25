package net.sf.okapi.applications.rainbow.utilities;

import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.FilterEventType;
import net.sf.okapi.common.threadedpipeline.BasePipelineStep;
import net.sf.okapi.common.threadedpipeline.IConsumer;
import net.sf.okapi.common.threadedpipeline.IProducer;
import net.sf.okapi.common.threadedpipeline.PipelineReturnValue;

public class UtilityStep extends BasePipelineStep implements IConsumer, IProducer {

	private IFilterDrivenUtility2 utility;

	public UtilityStep (IFilterDrivenUtility2 utility) {
		this.utility = utility;
	}
	
	public void finish () throws InterruptedException {
		utility.finish();
	}

	public String getName () {
		return utility.getName();
	}

	public void initialize () throws InterruptedException {
	}

	public PipelineReturnValue process () throws InterruptedException {
		// Get the event
		FilterEvent event = takeFromQueue();
		//FilterEvent event = (FilterEvent)takeFromQueue(consumerQueue);

		// Handle the event
		//utility.handleEvent(event);
		
		// Pass it to the next step
		addToQueue(event);
		
		// Return a proper status
		if (event.getEventType() == FilterEventType.FINISHED) {
			return PipelineReturnValue.SUCCEDED;
		}
		return PipelineReturnValue.RUNNING;
	}

}
