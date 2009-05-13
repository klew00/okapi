package net.sf.okapi.common.pipeline;

import java.io.File;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiBadStepInputException;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.resource.RawDocument;

public class RawDocumentWriterStep extends BasePipelineStep {

	private boolean hasNext = true;
	
	public String getDescription() {
		return "Write a raw document";
	}

	public String getName () {
		return "RawDocument Writer";
	}

	@Override
	public boolean needsOutput (int inputIndex) {
		return (inputIndex == 0);
	}

	public void preprocess (IDocumentData inputs) {
		super.preprocess(inputs);
		hasNext = true;
	}
	
	@Override
	public boolean hasNext () {
		return hasNext;
	}

	@Override
	public Event handleEvent (Event event) {
		if ( event.getEventType() == EventType.RAW_DOCUMENT ) {
			handleRawDocument(event);
		}
		else {
			hasNext = false;
		}
		return event;
	}

	@Override
	protected void handleRawDocument (Event event) {
		RawDocument rawDoc = (RawDocument)event.getResource();
		try {
			rawDoc = (RawDocument)event.getResource();
			
			if ( rawDoc.getInputCharSequence() != null ) {
				//TODO
				throw new RuntimeException("Not implemented yet");
			}
			else if ( rawDoc.getInputURI() != null ) {
				// Faster to copy using channels
				String inputPath = rawDoc.getInputURI().getPath();
				Util.copyFile(inputPath, inputs.getOutputPath(0), false); // Copy, do not move
			}
			else if ( rawDoc.getInputStream() != null ) {
				throw new RuntimeException("Not implemented yet");
			}
			else {
				// Change this exception to more generic (not just filter)
				throw new OkapiBadStepInputException("RawDocument has no input defined.");
			}
				
			// Set the new raw-document URI and the encoding (in case one was auto-detected)
			// Other info stays the same
			rawDoc.setInputURI((new File(inputs.getOutputPath(0))).toURI());
			hasNext = true;
		}
		catch ( Throwable e ) {
			throw new OkapiIOException("Error writing or copying a RawDocument.", e);
		}

	}
}
