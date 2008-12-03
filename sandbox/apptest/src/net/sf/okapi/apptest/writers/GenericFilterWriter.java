package net.sf.okapi.apptest.writers;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import net.sf.okapi.apptest.common.IParameters;
import net.sf.okapi.apptest.filters.FilterEvent;
import net.sf.okapi.apptest.filters.IFilterWriter;
import net.sf.okapi.apptest.resource.DocumentPart;
import net.sf.okapi.apptest.resource.Ending;
import net.sf.okapi.apptest.resource.StartDocument;
import net.sf.okapi.apptest.resource.StartGroup;
import net.sf.okapi.apptest.resource.StartSubDocument;
import net.sf.okapi.apptest.resource.TextUnit;
import net.sf.okapi.apptest.skeleton.ISkeletonWriter;
import net.sf.okapi.common.Util;

public class GenericFilterWriter implements IFilterWriter {

	private ISkeletonWriter skelWriter;
	private OutputStream output;
	private String outputPath;
	private OutputStreamWriter writer;
	private String language;
	private String encoding;
	
	public GenericFilterWriter (ISkeletonWriter skelWriter) {
		this.skelWriter = skelWriter;
	}
	
	public void close () {
		try {
			if ( writer != null ) {
				writer.close();
				writer = null;
				//TODO: do we need to close the underlying stream???
			}
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	public String getName () {
		return "GenericFilterWriter";
	}

	public IParameters getParameters () {
		return null;
	}

	public FilterEvent handleEvent (FilterEvent event) {
		try {
			switch ( event.getEventType() ) {
			case START:
				processStart();
				break;
			case FINISHED:
				processFinished();
				break;
			case START_DOCUMENT:
				processStartDocument((StartDocument)event.getResource());
				break;
			case END_DOCUMENT:
				processEndDocument((Ending)event.getResource());
				close();
				break;
			case START_SUBDOCUMENT:
				processStartSubDocument((StartSubDocument)event.getResource());
				break;
			case END_SUBDOCUMENT:
				processEndSubDocument((Ending)event.getResource());
				break;
			case START_GROUP:
				processStartGroup((StartGroup)event.getResource());
				break;
			case END_GROUP:
				processEndGroup((Ending)event.getResource());
				break;
			case TEXT_UNIT:
				processTextUnit((TextUnit)event.getResource());
				break;
			case DOCUMENT_PART:
				processDocumentPart((DocumentPart)event.getResource());
				break;
			}
		}
		catch ( FileNotFoundException e ) {
			throw new RuntimeException(e);
		}
		catch ( UnsupportedEncodingException e ) {
			throw new RuntimeException(e);
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
		return event;
	}

	private void processStart () {
		// Create the output
		createWriter();
		skelWriter.processStart(language, encoding, null);
	}

	private void processFinished () {
		skelWriter.processFinished();
		close();
	}
	
	private void processStartDocument(StartDocument resource) throws IOException {
		// Write the skeleton
		writer.write(skelWriter.processStartDocument(resource));
	}

	private void processEndDocument(Ending resource) throws IOException {
		// Write the skeleton
		writer.write(skelWriter.processEndDocument(resource));
	}

	private void processStartSubDocument (StartSubDocument resource) throws IOException {
		writer.write(skelWriter.processStartSubDocument(resource));
	}

	private void processEndSubDocument (Ending resource) throws IOException {
		writer.write(skelWriter.processEndSubDocument(resource));
	}

	private void processStartGroup (StartGroup resource) throws IOException {
		writer.write(skelWriter.processStartGroup(resource));
	}

	private void processEndGroup (Ending resource) throws IOException {
		writer.write(skelWriter.processEndGroup(resource));
	}

	private void processTextUnit (TextUnit resource) throws IOException {
		writer.write(skelWriter.processTextUnit(resource));
	}

	private void processDocumentPart (DocumentPart resource) throws IOException {
		writer.write(skelWriter.processDocumentPart(resource));
	}

	public void setOptions (String language,
		String defaultEncoding)
	{
		//NOT USED: outputLang = language;
		encoding = defaultEncoding;
	}

	public void setOutput (String path) {
		close(); // Make sure previous is closed
		this.outputPath = path;
	}

	public void setOutput (OutputStream output) {
		close(); // Make sure previous is closed
		this.output = output; // then assign the new stream
	}

	public void setParameters (IParameters params) {
	}

	private void createWriter () {
		try {
			// Create the output writer from the provided stream
			// or from the path if there is no stream provided
			if ( output == null ) {
				output = new BufferedOutputStream(new FileOutputStream(outputPath));
			}
			writer = new OutputStreamWriter(output, encoding);
			Util.writeBOMIfNeeded(writer, true, encoding);
		}
		catch ( FileNotFoundException e ) {
			throw new RuntimeException(e);
		}
		catch ( UnsupportedEncodingException e ) {
			throw new RuntimeException(e);
		}
	}
	
}
