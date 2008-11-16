package net.sf.okapi.apptest.skeleton;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Stack;

import net.sf.okapi.apptest.common.IParameters;
import net.sf.okapi.apptest.common.ISkeleton;
import net.sf.okapi.apptest.filters.FilterEvent;
import net.sf.okapi.apptest.filters.IEncoder;
import net.sf.okapi.apptest.filters.IFilterWriter;
import net.sf.okapi.apptest.filters.IWriterHelper;
import net.sf.okapi.apptest.resource.DocumentPart;
import net.sf.okapi.apptest.resource.Ending;
import net.sf.okapi.apptest.resource.Group;
import net.sf.okapi.apptest.resource.IReferenceable;
import net.sf.okapi.apptest.resource.TextContainer;
import net.sf.okapi.apptest.resource.TextUnit;
import net.sf.okapi.common.Util;

public class GenericSkeletonWriter implements IFilterWriter, IWriterHelper {

	protected OutputStream output;
	protected String language;
	protected String encoding;
	protected String outputPath;
	protected IParameters params;
	private OutputStreamWriter writer;
	private Stack<Group> groupStack;
	public LinkedHashMap<String, IReferenceable> references;
	public boolean outputTarget;
	public IEncoder encoder;
	
	public GenericSkeletonWriter () {
		references = new LinkedHashMap<String, IReferenceable>();
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
		return params;
	}

	public void setOutputTarget (boolean value) {
		outputTarget = value;
	}
	
	public void setEncoder (IEncoder encoder) {
		this.encoder = encoder;
	}
	
	public void handleEvent (FilterEvent event) {
		try {
			switch ( event.getEventType() ) {
			case START_DOCUMENT:
				createWriter();
				processSkeleton(event.getResource().getSkeleton());
				break;
			case END_DOCUMENT:
				processSkeleton(event.getResource().getSkeleton());
				close();
				break;
			case START_SUBDOCUMENT:
				processSkeleton(event.getResource().getSkeleton());
				break;
			case END_SUBDOCUMENT:
				processSkeleton(event.getResource().getSkeleton());
				break;
			case START_GROUP:
				processStartGroup((Group)event.getResource());
				processSkeleton(event.getResource().getSkeleton());
				break;
			case END_GROUP:
				processSkeleton(event.getResource().getSkeleton());
				processEndGroup((Ending)event.getResource());
				break;
			case TEXT_UNIT:
				writeTextUnit((TextUnit)event.getResource());
				processSkeleton(event.getResource().getSkeleton());
				break;
			case DOCUMENT_PART:
				processDocumentPart((DocumentPart)event.getResource());
				processSkeleton(event.getResource().getSkeleton());
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
	}

	public void setOptions (String language,
		String defaultEncoding)
	{
		this.language = language;
		this.encoding = defaultEncoding;
	}

	public void setOutput (String path) {
		close();
		this.outputPath = path;
	}

	public void setOutput (OutputStream output) {
		close(); // Make sure previous is closed
		this.output = output; // then assign the new stream
	}

	public void setParameters (IParameters params) {
		this.params = params;
	}

	private void processSkeleton (ISkeleton skeleton) throws IOException {
		if ( skeleton == null ) return; // Nothing to process
		GenericSkeleton skel = (GenericSkeleton)skeleton;
		for ( GenericSkeletonPart part : skel.getParts() ) {
			if ( part.isReference() ) {
				references.put(part.getID(), part);
			}
			if ( groupStack.size() > 0 ) {
				groupStack.peek().add(part);
			}
			else if ( !part.isReference() ) {
				writer.write(part.toString(this));
			}
		}
	}
	
	private void processDocumentPart (DocumentPart resource) {
		if ( resource.isReference() ) {
			references.put(resource.getID(), resource);
		}
		else if ( groupStack.size() > 0 ) {
			groupStack.peek().add(resource);
		}
	}
	
	private void processStartGroup (Group resource) {
		if ( resource.isReference() ) {
			references.put(resource.getID(), resource);
			groupStack.push(resource);
		}
		else if ( groupStack.size() > 0 ) {
			groupStack.peek().add(resource);
			groupStack.push(resource);
		}
	}
	
	private void processEndGroup (Ending resource) {
		if ( groupStack.size() > 0 ) {
			groupStack.pop();
		}
	}
	
	private void createWriter () throws FileNotFoundException, UnsupportedEncodingException {
		// Create the output writer from the provided stream
		if ( output == null ) {
			output = new BufferedOutputStream(new FileOutputStream(outputPath));
		}
		writer = new OutputStreamWriter(output, encoding);
		Util.writeBOMIfNeeded(writer, true, encoding);
		groupStack = new Stack<Group>();
	}
	
	private void writeTextUnit (TextUnit unit) throws IOException {
		if ( unit.isReference() ) {
			references.put(unit.getID(), unit);
		}
		if ( groupStack.size() > 0 ) {
			groupStack.peek().add(unit);
		}
		else if ( !unit.isReference() ) {
			TextContainer tc;
			if ( useTarget() ) {
				if ( unit.hasTarget() ) tc = unit.getTargetContent();
				else tc = unit.getSourceContent();
			}
			else tc = unit.getSourceContent();
			writer.write(tc.toString(this));
		}
	}

	public IReferenceable getReference (String id) {
		if ( references == null ) return null;
		return references.get(id);
	}

	public boolean useTarget() {
		return outputTarget;
	}
	
	public String encode (String text) {
		if ( encoder == null ) return text;
		return encoder.encode(text);
	}
	
	public String encode (char value) {
		if ( encoder == null ) return String.valueOf(value);
		return encoder.encode(value);
	}

	public String getLayerAfterCode () {
		return "";
	}

	public String getLayerAfterInline () {
		return "";
	}

	public String getLayerBeforeCode () {
		return "";
	}

	public String getLayerBeforeInline () {
		return "";
	}

}
