package net.sf.okapi.apptest.skeleton;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.LinkedHashMap;
import java.util.Stack;

import net.sf.okapi.apptest.common.IParameters;
import net.sf.okapi.apptest.common.IReferenceable;
import net.sf.okapi.apptest.common.IResource;
import net.sf.okapi.apptest.common.ISkeleton;
import net.sf.okapi.apptest.filters.FilterEvent;
import net.sf.okapi.apptest.filters.IEncoder;
import net.sf.okapi.apptest.filters.IFilterWriter;
import net.sf.okapi.apptest.filters.IWriterHelper;
import net.sf.okapi.apptest.resource.DocumentPart;
import net.sf.okapi.apptest.resource.Ending;
import net.sf.okapi.apptest.resource.StartGroup;
import net.sf.okapi.apptest.resource.TextFragment;
import net.sf.okapi.apptest.resource.TextUnit;
import net.sf.okapi.common.Util;

public class RTFFilterWriter implements IFilterWriter, IWriterHelper {

	private OutputStream output;
	private String encoding;
	private String outputPath;
	private IParameters params;
	private OutputStreamWriter writer;
	private Stack<StorageList> storageStack;
	private LinkedHashMap<String, IResource> references;
	private boolean outputTarget;
	private String language;
	private IEncoder encoder;
	private CharsetEncoder charEncoder;
	
	
	public RTFFilterWriter () {
		references = new LinkedHashMap<String, IResource>();
	}
	
	public void close () {
		try {
			if ( writer != null ) {
				writer.write(Util.RTF_ENDCODE+"}\n");
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
	
	public FilterEvent handleEvent (FilterEvent event) {
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
				processStartGroup((StartGroup)event.getResource());
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
		return event;
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
			if ( part.isReferent() ) {
				references.put(part.getId(), part);
			}
			if ( storageStack.size() > 0 ) {
				storageStack.peek().add(part);
			}
			else if ( !part.isReferent() ) {
				writer.write(Util.escapeToRTF(part.toString(this), true, 1, charEncoder));
			}
		}
	}
	
	private void processDocumentPart (DocumentPart resource) {
		if ( resource.isReferent() ) {
			references.put(resource.getId(), resource);
		}
		else if ( storageStack.size() > 0 ) {
			storageStack.peek().add(resource);
		}
	}
	
	private void processStartGroup (StartGroup resource) {
		if ( resource.isReferent() ) {
			references.put(resource.getId(), resource);
			storageStack.push(new StorageList());
		}
		else if ( storageStack.size() > 0 ) {
			storageStack.peek().add(resource);
			storageStack.push(new StorageList());
		}
	}
	
	private void processEndGroup (Ending resource) {
		if ( storageStack.size() > 0 ) {
			storageStack.pop();
		}
	}
	
	private void createWriter () throws IOException {
		// Create the output writer from the provided stream
		if ( output == null ) {
			output = new BufferedOutputStream(new FileOutputStream(outputPath));
		}
		writer = new OutputStreamWriter(output, encoding);
		Util.writeBOMIfNeeded(writer, true, encoding);
		charEncoder = Charset.forName(encoding).newEncoder();
		storageStack = new Stack<StorageList>();
		
		writer.write("{\\rtf1\\ansi\\ansicpg" + "1252" + "\\uc1\\deff1 \n"+
			"{\\fonttbl \n"+
			"{\\f1 \\fmodern\\fcharset0\\fprq1 Courier New;}\n"+
			"{\\f2 \\fswiss\\fcharset0\\fprq2 Arial;}\n"+
			"{\\f3 \\froman\\fcharset0\\fprq2 Times New Roman;}}\n"+
			"{\\colortbl \\red0\\green0\\blue0;\\red0\\green0\\blue0;\\red0\\green0\\blue255;"+
			"\\red0\\green255\\blue255;\\red0\\green255\\blue0;\\red255\\green0\\blue255;"+
			"\\red255\\green0\\blue0;\\red255\\green255\\blue0;\\red255\\green255\\blue255;"+
			"\\red0\\green0\\blue128;\\red0\\green128\\blue128;\\red0\\green128\\blue0;"+
			"\\red128\\green0\\blue128;\\red128\\green0\\blue0;\\red128\\green128\\blue0;"+
			"\\red128\\green128\\blue128;\\red192\\green192\\blue192;}\n"+
			"{\\stylesheet \n"+
			"{\\s0 \\sb80\\slmult1\\widctlpar\\fs20\\f1 \\snext0 Normal;}\n"+
			"{\\cs1 \\additive \\v\\cf12\\sub\\f1 tw4winMark;}\n"+
			"{\\cs2 \\additive \\cf4\\fs40\\f1 tw4winError;}\n"+
			"{\\cs3 \\additive \\f1\\cf11 tw4winPopup;}\n"+
			"{\\cs4 \\additive \\f1\\cf10 tw4winJump;}\n"+
			"{\\cs5 \\additive \\cf15\\f1\\lang1024\\noproof tw4winExternal;}\n"+
			"{\\cs6 \\additive \\cf6\\f1\\lang1024\\noproof tw4winInternal;}\n"+
			"{\\cs7 \\additive \\cf2 tw4winTerm;}\n"+
			"{\\cs8 \\additive \\cf13\\f1\\lang1024\\noproof DO_NOT_TRANSLATE;}\n"+
			"{\\cs9 \\additive Default Paragraph Font;}"+
			"{\\cs15 \\additive \\v\\f1\\cf12\\sub tw4winMark;}"+
			"}\n"+
			"\\paperw11907\\paperh16840\\viewkind4\\viewscale100\\pard\\plain\\s0\\sb80\\slmult1\\widctlpar\\fs20\\f1 \n"+
			Util.RTF_STARTCODE);
	}
	
	private void writeTextUnit (TextUnit unit) throws IOException {
		if ( unit.isReferent() ) {
			references.put(unit.getId(), unit);
		}
		if ( storageStack.size() > 0 ) {
			storageStack.peek().add(unit);
		}
		else if ( !unit.isReferent() ) {
			TextFragment tf;
			if ( language == null ) tf = unit.getContent();
			else { // TODO: any better way?
				if ( unit.getAnnotation(language) == null ) {
					tf = unit.getContent();
				}
				else {
					tf = ((TextUnit)unit.getAnnotation(language)).getContent();
				}
			}
			writer.write(Util.escapeToRTF(tf.toString(this), true, 1, charEncoder));
		}
	}

	public IReferenceable getReference (String id) {
		if ( references == null ) return null;
		return (IReferenceable)references.get(id);
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
	
	public String getLanguage() {
		return language;
	}

	public void setLanguage (String language) {
		this.language = language;
	}

	public String getLayerAfterCode () {
		return Util.RTF_ENDCODE;
	}

	public String getLayerAfterInline() {
		return Util.RTF_ENDINLINE;
	}

	public String getLayerBeforeCode() {
		return Util.RTF_STARTCODE;
	}

	public String getLayerBeforeInline() {
		return Util.RTF_STARTINLINE;
	}

}
