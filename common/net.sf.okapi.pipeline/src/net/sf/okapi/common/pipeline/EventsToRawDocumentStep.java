package net.sf.okapi.common.pipeline;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.exceptions.OkapiUnsupportedEncodingException;
import net.sf.okapi.common.filters.FilterFactory;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filterwriter.GenericFilterWriter;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;

/**
 * Converts filters events into a {@link RawDocument}.
 * This class implements the {@link IPipelineStep} interface for a step that takes 
 * filter events and creates an output document using a provided {@link IFilterWriter} 
 * implementation. When the document is completed, a {@link RawDocument} is generated.
 * @see RawDocumentToEventsStep
 * @see EventsWriterStep 
 */
public class EventsToRawDocumentStep extends BasePipelineStep {

	private static final Logger LOGGER = Logger.getLogger(EventsToRawDocumentStep.class.getName());

	private IFilterWriter filterWriter;
	private boolean hasNext;
	private String language;
	private String encoding;
	private URI path;
	private ByteArrayOutputStream byteBuffer;

	public EventsToRawDocumentStep() {
		hasNext = true;
		byteBuffer = new ByteArrayOutputStream(1024);
	}

	public EventsToRawDocumentStep(URI path) {
		hasNext = true;
		this.path = path;
	}

	@Override
	public Event handleEvent(Event event) {
		RawDocument input = null;
		IFilter filter = null;
		
		if (event.getEventType() == EventType.START_DOCUMENT) {
			language = ((StartDocument) event.getResource()).getLanguage();
			encoding = ((StartDocument) event.getResource()).getEncoding();

			String mimeType = ((StartDocument) event.getResource()).getMimeType();
			if (mimeType == null) {
				filterWriter = new GenericFilterWriter(new GenericSkeletonWriter());
				LOGGER.log(Level.WARNING, "Missing mime type in START_DOCUMENT");
			} else {
				filter = FilterFactory.getDefaultFilter(mimeType);
				filterWriter = filter.createFilterWriter();
			}
			
			filterWriter.setOptions(language, encoding);
			if (path != null) {
				filterWriter.setOutput(path.getPath());
			} else {
				filterWriter.setOutput(byteBuffer);
			}

			filterWriter.handleEvent(event);
			hasNext = true;
		} else if (event.getEventType() == EventType.END_DOCUMENT) {
			filterWriter.handleEvent(event);
			filterWriter.close();

			if (path != null) {
				input = new RawDocument(path, encoding, language);
			} else {
				try {
					input = new RawDocument(new String(byteBuffer.toByteArray(), encoding), language);
				} catch (UnsupportedEncodingException e) {
					OkapiUnsupportedEncodingException re = new OkapiUnsupportedEncodingException(e);
					LOGGER.log(Level.SEVERE, "Error creating RawDocument. Unsupported encoding: " + encoding, re);
					throw re;
				}
			}
			hasNext = false;
			// return the RawDocument Event that is the end result of all
			// previous Events
			return new Event(EventType.RAW_DOCUMENT, input);
		} else {
			// handle all the events between START_DOCUMENT and END_DOCUMENT
			filterWriter.handleEvent(event);
		}

		// return the NOOP event until we hit an END_DOCUMENT
		return Event.NOOP_EVENT;
	}

	@Override
	public void destroy() {
		filterWriter.close();
	}

	public boolean hasNext() {
		return hasNext;
	}

	public String getDescription() {
		return "Combine document events into a full document and pass it along as an event (RawDocument)";
	}

	public String getName() {
		return "Event to Document Converter Step";
	}
}
