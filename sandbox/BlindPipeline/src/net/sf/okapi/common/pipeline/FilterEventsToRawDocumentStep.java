package net.sf.okapi.common.pipeline;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.exceptions.OkapiFileNotFoundException;
import net.sf.okapi.common.filters.FilterFactory;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filterwriter.GenericFilterWriter;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;

/**
 * Converts filters events into a {@link RawDocument}. This class implements the
 * {@link IPipelineStep} interface for a step that takes filter events and
 * creates an output document using a provided {@link IFilterWriter}
 * implementation. When the document is completed, a {@link RawDocument} is
 * generated.
 * 
 * @see RawDocumentToFilterEventsStep
 * @see FilterEventsWriterStep
 */
public class FilterEventsToRawDocumentStep extends BasePipelineStep {

	private static final Logger LOGGER = Logger.getLogger(FilterEventsToRawDocumentStep.class.getName());

	private IFilterWriter filterWriter;
	private boolean hasNext;
	private String language;
	private String encoding;
	private File outputFile;
	private URI userOutput;

	/**
	 * Create a EventsToRawDocumentStep that creates a temporary file to write out
	 * {@link Event}s. The temporary file is closed after writing and passed down as
	 * a {@link URI} to subsequent steps.
	 */
	public FilterEventsToRawDocumentStep() {
	}

	/**
	 * Create a EventsToRawDocumentStep that takes a user specified {@link URI}
	 * and writes all processed {@link FilterEventsToRawDocumentStep} to this file.
	 * The URI is passed down the {@link IPipeline} for subsequent steps to use
	 * as input.
	 * 
	 * @param userOutput
	 *            user specified URI
	 */
	public FilterEventsToRawDocumentStep (URI userOutput) {
		this.userOutput = userOutput;
	}

	/**
	 * Catch all incoming {@link Event}s and write them out to the a temp or
	 * user specified {@link URI}. This step generates NO_OP events until the
	 * input events are exhausted, at which point a RawDocument event is sent.
	 */
	@Override
	public Event handleEvent (Event event) {
		switch ( event.getEventType() ) {
		case START_DOCUMENT:
			handleStartDocument(event);
			return Event.NOOP_EVENT;

		case END_DOCUMENT:
			return processEndDocument(event);
		
		case START_SUBDOCUMENT:
		case START_GROUP:
		case END_SUBDOCUMENT:
		case END_GROUP:
		case DOCUMENT_PART:
		case TEXT_UNIT:
			// handle all the events between START_DOCUMENT and END_DOCUMENT
			filterWriter.handleEvent(event);
			return Event.NOOP_EVENT;
		}
		
		// Else, just return the event
		return event;
	}

	private Event processEndDocument (Event event) {
		// Handle the END_DOCUMENT event and close the writer
		filterWriter.handleEvent(event);
		filterWriter.close();

		// It should be safe to close the output file on jvm exit
		if ( outputFile.exists() ) {
			outputFile.deleteOnExit();
		}

		// Return the RawDocument Event that is the end result of all
		// previous Events
		RawDocument input = new RawDocument(outputFile.toURI(), encoding, language);
		hasNext = false;
		return new Event(EventType.RAW_DOCUMENT, input);
	}
	
	@Override
	protected void handleStartDocument (Event event) {
		language = getContext().getTargetLanguage(); 
		encoding = getContext().getOutputEncoding(0);
		if ( encoding == null ) ((StartDocument)event.getResource()).getEncoding();

		String mimeType = ((StartDocument) event.getResource()).getMimeType();
		if (mimeType == null) {
			filterWriter = new GenericFilterWriter(new GenericSkeletonWriter());
			LOGGER.log(Level.WARNING, "Missing mime type in START_DOCUMENT");
		} else {
			IFilter filter = FilterFactory.getDefaultFilter(mimeType);
			filterWriter = filter.createFilterWriter();
		}

		filterWriter.setOptions(language, encoding);
		try {
			if (userOutput != null) {
				outputFile = new File(userOutput);
			} else {
				outputFile = File.createTempFile("EventsToRawDocumentStep", ".tmp");
				userOutput = outputFile.toURI();
			}
		} catch (IOException e) {
			OkapiFileNotFoundException re = new OkapiFileNotFoundException(e);
			LOGGER.log(Level.SEVERE, String.format("Cannot create the file (%s) in EventsToRawDocumentStep",
					userOutput.toString()), re);
			throw re;
		}

		filterWriter.setOutput(outputFile.getAbsolutePath());
		filterWriter.handleEvent(event);
		hasNext = true;
	}

	/**
	 * This step generates NO_OP Events until the final RAW_DOCUMENT event is
	 * sent.
	 */
	public boolean hasNext() {
		return hasNext;
	}

	/**
	 * Step description.
	 */
	public String getDescription() {
		return "Combine document events into a full document and pass it along as an event (RawDocument)";
	}

	/**
	 * UI pipeline step displayable name.
	 */
	public String getName() {
		return "Event to Document Converter Step";
	}

}
