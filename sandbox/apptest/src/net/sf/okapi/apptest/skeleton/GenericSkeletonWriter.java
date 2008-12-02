package net.sf.okapi.apptest.skeleton;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Stack;

import net.sf.okapi.apptest.annotation.TargetsAnnotation;
import net.sf.okapi.apptest.common.IEncoder;
import net.sf.okapi.apptest.common.INameable;
import net.sf.okapi.apptest.common.IReferenceable;
import net.sf.okapi.apptest.common.IResource;
import net.sf.okapi.apptest.common.ISkeleton;
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
import net.sf.okapi.apptest.writers.ILayerProvider;

public class GenericSkeletonWriter implements ISkeletonWriter {

	//private String encoding;
	private Stack<StorageList> storageStack;
	private LinkedHashMap<String, IReferenceable> referents;
	private String outputLang;
	private boolean isMultilingual;
	private ILayerProvider layer;
	
	public GenericSkeletonWriter (ILayerProvider layer) {
		this.layer = layer;
	}
	
	public void setLayerProvider (ILayerProvider layer) {
		this.layer = layer;
	}
	
	public ILayerProvider getLayerProvider () {
		return layer;
	}
	
	public void setOptions (String language,
		String encoding)
	{
		this.outputLang = language;
		//this.encoding = encoding;
	}

	private IReferenceable getReference (String id) {
		if ( referents == null ) return null;
		IReferenceable ref = referents.get(id);
		// Remove the object found from the list
		if ( ref != null ) {
			referents.remove(id);
		}
		return ref;
	}

	public void processStart () {
		referents = new LinkedHashMap<String, IReferenceable>();
		storageStack = new Stack<StorageList>();
	}
	
	public void processFinished () {
		referents.clear();
		referents = null;
		storageStack.clear();
		storageStack = null;
	}
	
	public String processStartDocument (StartDocument resource) {
		isMultilingual = resource.isMultilingual();
		// EndDocument cannot be stored, no need to check for that.
		return getString((GenericSkeleton)resource.getSkeleton());
	}

	public String processEndDocument (Ending resource) {
		// EndDocument cannot be stored, no need to check for that.
		return getString((GenericSkeleton)resource.getSkeleton());
	}

	public String processStartSubDocument (StartSubDocument resource) {
		if ( storageStack.size() > 0 ) {
			storageStack.peek().add(resource);
			return "";
		}
		return getString((GenericSkeleton)resource.getSkeleton());
	}

	public String processEndSubDocument (Ending resource) {
		if ( storageStack.size() > 0 ) {
			storageStack.peek().add(resource);
			return "";
		}
		return getString((GenericSkeleton)resource.getSkeleton());
	}
	
	public String processStartGroup (StartGroup resource) {
		if ( resource.isReferent() ) {
			StorageList sl = new StorageList(resource);
			referents.put(sl.getId(), sl);
			storageStack.push(sl);
			return "";
		}
		if ( storageStack.size() > 0 ) {
			StorageList sl = new StorageList(resource);
			storageStack.peek().add(sl);
			storageStack.push(sl);
			return "";
		}
		return getString((GenericSkeleton)resource.getSkeleton());
	}
	
	public String processEndGroup (Ending resource) {
		if ( storageStack.size() > 0 ) {
			storageStack.peek().add(resource);
			storageStack.pop();
			return "";
		}
		return getString((GenericSkeleton)resource.getSkeleton());
	}
	
	public String processTextUnit (TextUnit resource) {
		if ( resource.isReferent() ) {
			referents.put(resource.getId(), resource);
			return "";
		}
		if ( storageStack.size() > 0 ) {
			storageStack.peek().add(resource);
			return "";
		}
		return getString(resource, outputLang);
	}

	public String processDocumentPart (DocumentPart resource) {
		if ( resource.isReferent() ) {
			referents.put(resource.getId(), resource);
			return "";
		}
		if ( storageStack.size() > 0 ) {
			storageStack.peek().add(resource);
			return "";
		}
		return getString((GenericSkeleton)resource.getSkeleton());
	}
	
	private String getString (ISkeleton skeleton) {
		if ( skeleton == null ) return "";
		StringBuilder tmp = new StringBuilder();
		for ( GenericSkeletonPart part : ((GenericSkeleton)skeleton).getParts() ) {
			tmp.append(getString(part));
		}
		return tmp.toString();
	}
	
	private String getString (GenericSkeletonPart part) {
		// If it is not a reference marker, just use the data
		if ( !part.data.toString().startsWith(TextFragment.REFMARKER_START) ) {
			if ( layer == null ) {
				return part.data.toString();
			}
			else {
				return layer.encode(part.data.toString(), 1);
			}
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
				if ( isMultilingual ) {
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
		if ( layer == null ) {
			return getContent(tf, langToUse, tu.getEncoder());
		}
		else {
			return layer.endCode()
				+ getContent(tf, langToUse, tu.getEncoder())
				+ layer.startCode();
		}
	}

	private String getContent (TextFragment tf, String langToUse, IEncoder encoder) {
		if ( !tf.hasCode() ) { // The easy output
			if ( encoder == null ) {
				return tf.toString();
			}
			else {
				return encoder.encode(tf.toString(), 0);				
			}
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
				if ( encoder == null ) {
					tmp.append(text.charAt(i));
				}
				else {
					tmp.append(encoder.encode(text.charAt(i), 0));
				}
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
		if ( langToUse == null ) { // Use the source
			prop = resource.getSourceProperty(name);
		}
		else if ( langToUse.length() == 0 ) { // Use the resource-level properties
			prop = resource.getProperty(name);
		}
		else { // Use the given language if possible
			if ( resource.hasTargetProperty(langToUse, name) ) {
				prop = resource.getTargetProperty(langToUse, name);
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
