package net.sf.okapi.steps.leveraging;

import java.util.LinkedList;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.MultiEvent;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.lib.translation.ITMQuery;

public class BatchTmLeveragingStep extends BasePipelineStep {
	private static final int BATCH_LEVERAGE_MAX = 100;
	
	private List<Event> batchedTuEvents;
	private int tuEventCount;
	private LocaleId sourceLocale;
	private LocaleId targetLocale;
	private Parameters params;
	private ITMQuery connector;

	private String rootDir;
	private String inputRootDir; 

	public BatchTmLeveragingStep() {		
		params = new Parameters();
		batchedTuEvents = new LinkedList<Event>();
	}
	
	@StepParameterMapping(parameterType = StepParameterType.SOURCE_LOCALE)
	public void setSourceLocale (LocaleId sourceLocale) {
		this.sourceLocale = sourceLocale;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.TARGET_LOCALE)
	public void setTargetLocale (LocaleId targetLocale) {
		this.targetLocale = targetLocale;
	}

	@StepParameterMapping(parameterType = StepParameterType.ROOT_DIRECTORY)
	public void setRootDirectory (String rootDir) {
		this.rootDir = rootDir;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.INPUT_ROOT_DIRECTORY)
	public void setInputRootDirectory (String inputRootDir) {
		this.inputRootDir = inputRootDir;
	}
	
	@Override
	public String getName() {		
		return "Simple Batch Leveraging Step";
	}

	@Override
	public String getDescription() {
		return "Simple and fast batch leveraging step with ";
	}

	@Override
	public IParameters getParameters () {
		return params;
	}

	@Override
	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}
	
	@Override
	protected Event handleTextUnit(Event event) {
		ITextUnit tu = event.getTextUnit();

		// Do not leverage non-translatable entries
		if (!tu.isTranslatable()) {
			return event;
		}

		boolean approved = false;
		Property prop = tu.getTargetProperty(targetLocale, Property.APPROVED);
		if (prop != null) {
			if ("yes".equals(prop.getValue()))
				approved = true;
		}

		// Do not leverage pre-approved entries
		if (approved) {
			return event;
		}

		tuEventCount++;
		if (tuEventCount >= BATCH_LEVERAGE_MAX) {
			tuEventCount = 0;
			batchLeverage();
			MultiEvent me = new MultiEvent();
			for (Event e : batchedTuEvents) {
				me.addEvent(e);
			}
			batchedTuEvents.clear();
			return new Event(EventType.MULTI_EVENT, me);
		} else {
			batchedTuEvents.add(event);
		}

		return Event.NOOP_EVENT;
	}
	
	@Override
	protected Event handleStartBatch(Event event) {
		try {
			connector = (ITMQuery)Class.forName(params.getResourceClassName()).newInstance();
		}
		catch ( InstantiationException e ) {
			throw new RuntimeException("Error creating connector.", e);
		}
		catch ( IllegalAccessException e ) {
			throw new RuntimeException("Error creating connector.", e);
		}
		catch ( ClassNotFoundException e ) {
			throw new RuntimeException("Error creating connector.", e);
		}

		IParameters connectorParams = connector.getParameters();
		if ( connectorParams != null ) { // Set the parameters only if the connector takes them
			connectorParams.fromString(params.getResourceParameters());
		}
		
		connector.setRootDirectory(rootDir); // Before open()
		connector.setParameters(connectorParams);
		connector.open();
		if (( sourceLocale != null ) && ( targetLocale != null )) {
			connector.setLanguages(sourceLocale, targetLocale);
		}		
		
		connector.setThreshold(params.getThreshold());
		connector.setMaximumHits(5);
	
		return event;
	}
	
	@Override
	protected Event handleEndDocument(Event event) {
		// leverage any remaining batched TextUnits for this document
		if (!batchedTuEvents.isEmpty()) {
			batchLeverage();
			MultiEvent me = new MultiEvent();
			for (Event e : batchedTuEvents) {
				me.addEvent(e);
			}
			batchedTuEvents.clear();

			// add END DOCUMENT event
			me.addEvent(event);
			return new Event(EventType.MULTI_EVENT, me);
		}
		
		return event;
	}

	private void batchLeverage() {
		List<ITextUnit> tus = new LinkedList<ITextUnit>();
		for (Event e : batchedTuEvents) {
			tus.add(e.getTextUnit());
		}		
		connector.batchLeverage(tus);
	}
}
