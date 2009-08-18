package net.sf.okapi.steps.tokenization.common;


import net.sf.okapi.common.Event;
import net.sf.okapi.common.IContext;
import net.sf.okapi.common.pipeline.IPipelineStep;
import net.sf.okapi.common.pipelinedriver.PipelineContext;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.filters.plaintext.common.OkapiComponent;

/**
 * 
 * 
 * @version 0.1 08.07.2009
 */

abstract public class AbstractPipelineStep extends OkapiComponent implements IPipelineStep {

	private String language;
	private IContext context;
	private boolean isLastStep = false;

	public AbstractPipelineStep() {
		
		super();
	}

	/**
	 * Gets the {@link IContext} of the current pipeline associated
	 * with this step.
	 * 
	 * @return the current {@link PipelineContext} for this step.
	 */
	public IContext getContext() {
		
		return context;
	}

	public void setContext(IContext context) {
		
		this.context = context;
	}
	
	protected String getLanguage() {
		
		return language;
	}
	
	public void cancel() {
		
	}
	
	public void destroy() {
				
	}

	public Event handleEvent(Event event) {
		
		if (event == null) return null;
		
		switch ( event.getEventType() ) {
		
		case START_BATCH:
			
			component_init();
			handleStartBatch(event);
			break;
			
		case END_BATCH:
			
			component_done();
			handleEndBatch(event);
			break;
			
		case START_BATCH_ITEM:
			
			handleStartBatchItem(event);
			break;
			
		case END_BATCH_ITEM:
			
			handleEndBatchItem(event);
			break;
			
		case RAW_DOCUMENT:
			
			handleRawDocument(event);
			break;
			
		case START_DOCUMENT:
			
			handleStartDocument(event);
			break;
			
		case END_DOCUMENT:
			
			handleEndDocument(event);
			break;
			
		case START_SUBDOCUMENT:
			
			handleStartSubDocument(event);
			break;
			
		case END_SUBDOCUMENT:
			
			handleEndSubDocument(event);
			break;
			
		case START_GROUP:
			
			handleStartGroup(event);
			break;
			
		case END_GROUP:
			
			handleEndGroup(event);
			break;
			
		case TEXT_UNIT:
			
			handleTextUnit(event);
			break;
			
		case DOCUMENT_PART:
			
			handleDocumentPart(event);
			break;
			
		case CUSTOM:
			
			handleCustom(event);
			break;
			
		// default:
		// Just pass it through
		}
		return event;
	}

	public int inputCountRequested() {
		
		return 1; // Just the main input
	}

	public boolean isDone() {

		return true;
	}

	public boolean needsOutput(int inputIndex) {

		return false;
	}
	
	public boolean isLastStep() {
		
		return isLastStep;
	}

	public void setLastStep(boolean isLastStep) {
		
		this.isLastStep = isLastStep;
	}

	// By default we simply pass the event on to the next step.
	// Override these methods if you need to process the event

	/**
	 * Handles the {@link net.sf.okapi.common.EventType#START_BATCH} event.
	 * @param event the event itself. 
	 */
	protected void handleStartBatch (Event event) {
	}
	
	/**
	 * Handles the {@link net.sf.okapi.common.EventType#END_BATCH} event.
	 * @param event the event itself. 
	 */
	protected void handleEndBatch (Event event) {
	}
	
	/**
	 * Handles the {@link net.sf.okapi.common.EventType#START_BATCH_ITEM} event.
	 * @param event the event itself. 
	 */
	protected void handleStartBatchItem (Event event) {
	}
	
	/**
	 * Handles the {@link net.sf.okapi.common.EventType#END_BATCH_ITEM} event.
	 * @param event the event itself. 
	 */
	protected void handleEndBatchItem (Event event) {
	}
	
	/**
	 * Handles the {@link net.sf.okapi.common.EventType#RAW_DOCUMENT} event.
	 * @param event the event itself. 
	 */
	protected void handleRawDocument (Event event) {
	}

	/**
	 * Handles the {@link net.sf.okapi.common.EventType#START_DOCUMENT} event.
	 * @param event the event itself. 
	 */
	protected void handleStartDocument (Event event) {
		
		StartDocument sd = (StartDocument) event.getResource();
		
		if (sd != null) language = sd.getLanguage();
	}

	/**
	 * Handles the {@link net.sf.okapi.common.EventType#END_DOCUMENT} event.
	 * @param event the event itself. 
	 */
	protected void handleEndDocument (Event event) {
	}

	/**
	 * Handles the {@link net.sf.okapi.common.EventType#START_SUBDOCUMENT} event.
	 * @param event the event itself. 
	 */
	protected void handleStartSubDocument (Event event) {
	}

	/**
	 * Handles the {@link net.sf.okapi.common.EventType#END_SUBDOCUMENT} event.
	 * @param event the event itself. 
	 */
	protected void handleEndSubDocument (Event event) {
	}

	/**
	 * Handles the {@link net.sf.okapi.common.EventType#START_GROUP} event.
	 * @param event the event itself. 
	 */
	protected void handleStartGroup (Event event) {
	}

	/**
	 * Handles the {@link net.sf.okapi.common.EventType#END_GROUP} event.
	 * @param event the event itself. 
	 */
	protected void handleEndGroup (Event event) {
	}

	/**
	 * Handles the {@link net.sf.okapi.common.EventType#TEXT_UNIT} event.
	 * @param event the event itself. 
	 */
	protected void handleTextUnit (Event event) {
	}

	/**
	 * Handles the {@link net.sf.okapi.common.EventType#DOCUMENT_PART} event.
	 * @param event the event itself. 
	 */
	protected void handleDocumentPart (Event event) {
	}

	/**
	 * Handles the {@link net.sf.okapi.common.EventType#CUSTOM} event.
	 * @param event the event itself. 
	 */
	protected void handleCustom (Event event) {
	}

}
