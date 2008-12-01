package net.sf.okapi.apptest.skeleton;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Stack;

import net.sf.okapi.apptest.annotation.TargetsAnnotation;
import net.sf.okapi.apptest.common.INameable;
import net.sf.okapi.apptest.common.IParameters;
import net.sf.okapi.apptest.common.IReferenceable;
import net.sf.okapi.apptest.common.IResource;
import net.sf.okapi.apptest.filters.FilterEvent;
import net.sf.okapi.apptest.filters.IEncoder;
import net.sf.okapi.apptest.filters.IFilterWriter;
import net.sf.okapi.apptest.resource.Code;
import net.sf.okapi.apptest.resource.DocumentPart;
import net.sf.okapi.apptest.resource.Ending;
import net.sf.okapi.apptest.resource.Property;
import net.sf.okapi.apptest.resource.StartDocument;
import net.sf.okapi.apptest.resource.StartGroup;
import net.sf.okapi.apptest.resource.StartSubDocument;
import net.sf.okapi.apptest.resource.TextContainer;
import net.sf.okapi.apptest.resource.TextFragment;
import net.sf.okapi.apptest.resource.TextUnit;
import net.sf.okapi.common.Util;

public class GenericSkeletonWriter implements IFilterWriter {

	protected OutputStream output;
	protected String encoding;
	protected String outputPath;
	protected IParameters params;
	protected OutputStreamWriter writer;
	protected Stack<StorageList> storageStack;
	protected LinkedHashMap<String, IReferenceable> referents;
	protected IEncoder encoder;
	protected String outputLang;
	protected boolean multilingual;
	
	public GenericSkeletonWriter () {
		referents = new LinkedHashMap<String, IReferenceable>();
	}
	
	public void close () {
		try {
			referents.clear();
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

	public void setEncoder (IEncoder encoder) {
		this.encoder = encoder;
	}
	
	public FilterEvent handleEvent (FilterEvent event) {
		try {
			switch ( event.getEventType() ) {
			case START_DOCUMENT:
				createWriter();
				processStartDocument((StartDocument)event.getResource());
				break;
			case END_DOCUMENT:
				processEnding((Ending)event.getResource());
				close();
				break;
			case START_SUBDOCUMENT:
				processStartSubDocument((StartSubDocument)event.getResource());
				break;
			case END_SUBDOCUMENT:
				processEnding((Ending)event.getResource());
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

	public void setOptions (String language,
		String defaultEncoding)
	{
		this.outputLang = language;
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

	public IReferenceable getReference (String id) {
		if ( referents == null ) return null;
		IReferenceable ref = referents.get(id);
		// Remove the object found from the list
		if ( ref != null ) {
			referents.remove(id);
		}
		return ref;
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

	public String getLanguage() {
		return outputLang;
	}

	public void setLanguage (String language) {
		this.outputLang = language;
	}

	protected void createWriter () {
		try {
			// Create the output writer from the provided stream
			if ( output == null ) {
				output = new BufferedOutputStream(new FileOutputStream(outputPath));
			}
			writer = new OutputStreamWriter(output, encoding);
			Util.writeBOMIfNeeded(writer, true, encoding);
			storageStack = new Stack<StorageList>();
		}
		catch ( FileNotFoundException e ) {
			throw new RuntimeException(e);
		}
		catch ( UnsupportedEncodingException e ) {
			throw new RuntimeException(e);
		}
	}
	
	private void processStartDocument (StartDocument resource) throws IOException {
		multilingual = resource.isMultilingual();
		if ( storageStack.size() > 0 ) {
			storageStack.peek().add(resource);
		}
		else {
			writer.write(getString((GenericSkeleton)resource.getSkeleton()));
		}
	}
	
	private void processStartSubDocument (StartSubDocument resource) throws IOException {
		if ( storageStack.size() > 0 ) {
			storageStack.peek().add(resource);
		}
		else {
			writer.write(getString((GenericSkeleton)resource.getSkeleton()));
		}
	}
	
	private void processEnding (Ending resource) throws IOException {
		if ( storageStack.size() > 0 ) {
			storageStack.peek().add(resource);
		}
		else {
			writer.write(getString((GenericSkeleton)resource.getSkeleton()));
		}
	}
	
	private void processStartGroup (StartGroup resource) throws IOException {
		if ( resource.isReferent() ) {
			StorageList sl = new StorageList(resource);
			referents.put(sl.getId(), sl);
			storageStack.push(sl);
		}
		else if ( storageStack.size() > 0 ) {
			StorageList sl = new StorageList(resource);
			storageStack.peek().add(sl);
			storageStack.push(sl);
		}
		else {
			writer.write(getString((GenericSkeleton)resource.getSkeleton()));
		}
	}
	
	private void processEndGroup (Ending resource) throws IOException {
		if ( storageStack.size() > 0 ) {
			storageStack.peek().add(resource);
			storageStack.pop();
		}
		else {
			writer.write(getString((GenericSkeleton)resource.getSkeleton()));
		}
	}
	
	private void processTextUnit (TextUnit resource) throws IOException {
		if ( resource.isReferent() ) {
			referents.put(resource.getId(), resource);
		}
		else if ( storageStack.size() > 0 ) {
			storageStack.peek().add(resource);
		}
		else {
			writer.write(getString(resource, outputLang));
		}
	}

	private void processDocumentPart (DocumentPart resource) throws IOException {
		if ( resource.isReferent() ) {
			referents.put(resource.getId(), resource);
		}
		else if ( storageStack.size() > 0 ) {
			storageStack.peek().add(resource);
		}
		else {
			writer.write(getString((GenericSkeleton)resource.getSkeleton()));
		}
	}
	
	private String getString (GenericSkeleton skeleton) {
		if ( skeleton == null ) return "";
		StringBuilder tmp = new StringBuilder();
		for ( GenericSkeletonPart part : skeleton.getParts() ) {
			tmp.append(getString(part));
		}
		return tmp.toString();
	}
	
	private String getString (GenericSkeletonPart part) {
		// If it is not a reference marker, just use the data
		if ( !part.data.toString().startsWith(TextFragment.REFMARKER_START) ) {
			return part.data.toString();
		}
		// Get the reference info
		Object[] marker = TextFragment.getRefMarker(part.data);
		// Check for problem
		if ( marker == null ) {
			return "-ERR:INVALID-REF-MARKER-";
		}
		String propName = (String)marker[3];
		
		// We use part.parent always for these parts
		if ( propName == null ) { // Reference to the content of the referent
			if ( part.parent instanceof TextUnit ) {
				if ( multilingual ) {
					return getContent((TextUnit)part.parent, part.language);
				}
				else {
					return getContent((TextUnit)part.parent, (part.language==null) ? outputLang : part.language);
				}
			}
			else {
				throw new RuntimeException("self-references to this skeleton part must be a text-unit.");
			}
		}

		// Or to a property of the referent
		return getString((IReferenceable)part.parent, propName, part.language);
	}

	private String getString (IReferenceable ref,
		String propName,
		String langToUse)
	{
		if ( ref == null ) {
			return "-ERR:NULL-REF-";
		}
		if ( propName != null ) {
			return getPropertyValue((INameable)ref, propName, langToUse);
		}
		if ( ref instanceof TextUnit ) {
			return getString((TextUnit)ref, langToUse);
		}
		if ( ref instanceof DocumentPart ) {
			return getString((GenericSkeleton)((IResource)ref).getSkeleton());
		}
		if ( ref instanceof StorageList ) {
			return getString((StorageList)ref, langToUse);
		}
		return "-ERR:INVALID-REFTYPE-";
	}

	private String getString (TextUnit tu, String langToUse) {
		GenericSkeleton skel = (GenericSkeleton)tu.getSkeleton();
		if ( skel == null ) { // No skeleton
			return getContent(tu, langToUse);
		}
		// Else: process the skeleton parts, one of them should
		// refer to the text-unit content itself
		StringBuilder tmp = new StringBuilder();
		for ( GenericSkeletonPart part : skel.getParts() ) {
			tmp.append(getString(part));
		}
		return tmp.toString();
	}

	private String getContent (TextUnit tu, String langToUse) {
		TextFragment tf;
		if ( langToUse == null ) {
			tf = tu.getSourceContent();
		}
		else {
			if ( tu.getAnnotation(TargetsAnnotation.class) == null ) {
				tf = tu.getSourceContent();
			}
			else {
				TextContainer tt = ((TargetsAnnotation)tu.getAnnotation(TargetsAnnotation.class)).get(langToUse);
				if ( tt == null ) {
					tf = tu.getSourceContent();
				}
				else {
					tf = tt.getContent();
				}
			}
		}
		return getContent(tf, langToUse);
	}

	public String getContent (TextFragment tf, String langToUse) {
		if ( !tf.hasCode() ) { // The easy output
			return encode(tf.toString());
		}

		List<Code> codes = tf.getCodes();
		StringBuilder tmp = new StringBuilder();
		String text = tf.getCodedText();
		Code code;
		for ( int i=0; i<text.length(); i++ ) {
			switch ( text.charAt(i) ) {
			case TextFragment.MARKER_OPENING:
				code = codes.get(TextFragment.toIndex(text.charAt(++i)));
				tmp.append(expandCodeContent(code, langToUse));
				break;
			case TextFragment.MARKER_CLOSING:
				code = codes.get(TextFragment.toIndex(text.charAt(++i)));
				tmp.append(expandCodeContent(code, langToUse));
				break;
			case TextFragment.MARKER_ISOLATED:
			case TextFragment.MARKER_SEGMENT:
				code = codes.get(TextFragment.toIndex(text.charAt(++i)));
				tmp.append(expandCodeContent(code, langToUse));
				break;
			default:
				tmp.append(encode(text.charAt(i)));
				break;
			}
		}
		return tmp.toString();
	}
	
	private String expandCodeContent (Code code, String langToUse) {
		if ( !code.hasReference() ) return code.getData();
		// Check for segment
		if ( code.getType().equals(TextFragment.CODETYPE_SEGMENT) ) {
			return "[SEG-"+code.getData()+"]";
		}
		// Else: look for place-holders
		StringBuilder tmp = new StringBuilder(code.getData());
		Object[] marker = null;
		while ( (marker = TextFragment.getRefMarker(tmp)) != null ) {
			int start = (Integer)marker[1];
			int end = (Integer)marker[2];
			String propName = (String)marker[3];
			IReferenceable ref = getReference((String)marker[0]);
			if ( ref == null ) {
				tmp.replace(start, end, "-ERR:REF-NOT-FOUND-");
			}
			else if ( propName != null ) {
				tmp.replace(start, end,
					getPropertyValue((INameable)ref, propName, langToUse));
			}
			else if ( ref instanceof TextUnit ) {
				tmp.replace(start, end, getString((TextUnit)ref, langToUse));
			}
			else if ( ref instanceof GenericSkeletonPart ) {
				tmp.replace(start, end, getString((GenericSkeletonPart)ref));
			}
			else if ( ref instanceof StorageList ) { // == StartGroup
				tmp.replace(start, end, getString((StorageList)ref, langToUse));
			}
			else if ( ref instanceof DocumentPart ) {
				tmp.replace(start, end, getString((GenericSkeleton)((IResource)ref).getSkeleton()));
			}
			else {
				tmp.replace(start, end, "-ERR:INVALID-TYPE-");
			}
		}
		return tmp.toString();
	}
	
	private String getString (StorageList list, String langToUse) {
		StringBuilder tmp = new StringBuilder();
		// Treat the skeleton of this list
		tmp.append(getString((GenericSkeleton)list.getSkeleton()));		
		// Then treat the list itself
		for ( IResource res : list ) {
			if ( res instanceof TextUnit ) {
				tmp.append(getString((TextUnit)res, langToUse));
			}
			else if ( res instanceof StorageList ) {
				tmp.append(getString((StorageList)res, langToUse));
			}
			else if ( res instanceof DocumentPart ) {
				tmp.append(getString((GenericSkeleton)res.getSkeleton()));
			}
			else if ( res instanceof Ending ) {
				tmp.append(getString((GenericSkeleton)res.getSkeleton()));
			}
		}
		return tmp.toString();
	}
	
	private String getPropertyValue (INameable resource,
		String name,
		String langToUse)
	{
		// Get the value based on the output language
		Property prop;
		if ( outputLang == null ) { // Use the source
			prop = resource.getSourceProperty(name);
		}
		else if ( langToUse.length() == 0 ) { // Use the resource-level properties
			prop = resource.getProperty(name);
		}
		else { // Use the given language if possible
			if ( resource.hasTargetProperty(outputLang, name) ) {
				prop = resource.getTargetProperty(outputLang, name);
			}
			else { // Fall back to source
				prop = resource.getSourceProperty(name);				
			}
		}
		// Check the property we got
		if ( prop == null ) return "-ERR:PROP-NOT-FOUND-";
		// Else process the value
		String value = prop.getValue();
		if ( value == null ) return "-ERR:PROP-VALUE-NOT-FOUND-";
		else return value;
	}
	
}
