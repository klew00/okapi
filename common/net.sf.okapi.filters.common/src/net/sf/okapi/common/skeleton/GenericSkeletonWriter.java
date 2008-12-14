/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
============================================================================*/

package net.sf.okapi.common.skeleton;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Stack;

import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.filters.IEncoder;
import net.sf.okapi.common.filters.ISkeleton;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.INameable;
import net.sf.okapi.common.resource.IReferenceable;
import net.sf.okapi.common.resource.IResource;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.common.resource.TargetsAnnotation;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.writer.ILayerProvider;

public class GenericSkeletonWriter implements ISkeletonWriter {

	private Stack<StorageList> storageStack;
	private LinkedHashMap<String, IReferenceable> referents;
	private String outputLang;
	private boolean isMultilingual;
	private ILayerProvider layer;
	private EncoderManager encoderManager;
	
	private IReferenceable getReference (String id) {
		if ( referents == null ) return null;
		IReferenceable ref = referents.get(id);
		// Remove the object found from the list
		if ( ref != null ) {
			referents.remove(id);
		}
		return ref;
	}

	public void processStart (String language,
		String encoding,
		ILayerProvider layer,
		EncoderManager encoderManager)
	{
		this.encoderManager = encoderManager;
		this.outputLang = language;
		//Not used: encoding;
		this.layer = layer;
		
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
		return getString((GenericSkeleton)resource.getSkeleton(), 1);
	}

	public String processEndDocument (Ending resource) {
		// EndDocument cannot be stored, no need to check for that.
		return getString((GenericSkeleton)resource.getSkeleton(), 1);
	}

	public String processStartSubDocument (StartSubDocument resource) {
		if ( storageStack.size() > 0 ) {
			storageStack.peek().add(resource);
			return "";
		}
		return getString((GenericSkeleton)resource.getSkeleton(), 1);
	}

	public String processEndSubDocument (Ending resource) {
		if ( storageStack.size() > 0 ) {
			storageStack.peek().add(resource);
			return "";
		}
		return getString((GenericSkeleton)resource.getSkeleton(), 1);
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
		return getString((GenericSkeleton)resource.getSkeleton(), 1);
	}
	
	public String processEndGroup (Ending resource) {
		if ( storageStack.size() > 0 ) {
			storageStack.peek().add(resource);
			storageStack.pop();
			return "";
		}
		return getString((GenericSkeleton)resource.getSkeleton(), 1);
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
		return getString(resource, outputLang, 1);
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
		return getString((GenericSkeleton)resource.getSkeleton(), 1);
	}
	
	private String getString (ISkeleton skeleton, int context) {
		if ( skeleton == null ) return "";
		StringBuilder tmp = new StringBuilder();
		for ( GenericSkeletonPart part : ((GenericSkeleton)skeleton).getParts() ) {
			tmp.append(getString(part, context));
		}
		return tmp.toString();
	}
	
	private String getString (GenericSkeletonPart part, int context) {
		// If it is not a reference marker, just use the data
		if ( !part.data.toString().startsWith(TextFragment.REFMARKER_START) ) {
			if ( layer == null ) {
				return part.data.toString();
			}
			else {
				return layer.encode(part.data.toString(), context);
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
					return getContent((TextUnit)part.parent, part.language, context);
				}
				else {
					return getContent((TextUnit)part.parent, (part.language==null) ? outputLang : part.language, context);
				}
			}
			else {
				throw new RuntimeException("self-references to this skeleton part must be a text-unit.");
			}
		}

		// Or to a property of the referent
		return getString((IReferenceable)part.parent, propName, part.language, context);
	}

	private String getString (IReferenceable ref,
		String propName,
		String langToUse,
		int context)
	{
		if ( ref == null ) {
			return "-ERR:NULL-REF-";
		}
		if ( propName != null ) {
			return getPropertyValue((INameable)ref, propName, langToUse);
		}
		if ( ref instanceof TextUnit ) {
			return getString((TextUnit)ref, langToUse, context);
		}
		if ( ref instanceof DocumentPart ) {
			return getString((GenericSkeleton)((IResource)ref).getSkeleton(), context);
		}
		if ( ref instanceof StorageList ) {
			return getString((StorageList)ref, langToUse, context);
		}
		return "-ERR:INVALID-REFTYPE-";
	}

	private String getString (TextUnit tu,
		String langToUse,
		int context)
	{
		GenericSkeleton skel = (GenericSkeleton)tu.getSkeleton();
		if ( skel == null ) { // No skeleton
			return getContent(tu, langToUse, context);
		}
		// Else: process the skeleton parts, one of them should
		// refer to the text-unit content itself
		StringBuilder tmp = new StringBuilder();
		for ( GenericSkeletonPart part : skel.getParts() ) {
			tmp.append(getString(part, context));
		}
		return tmp.toString();
	}

	// context: 0=text, 1=skeleton, 2=inline
	private String getContent (TextUnit tu,
		String langToUse,
		int context) 
	{
		// Update the encoder from the TU's mimetype
		encoderManager.updateEncoder(tu.getMimeType());
		// Get the right text container
		TextFragment tf;
		if ( langToUse == null ) {
			tf = tu.getSourceContent();
		}
		else {
			if ( tu.getAnnotation(TargetsAnnotation.class) == null ) {
				tf = tu.getSourceContent();
			}
			else {
				TextContainer tc = ((TargetsAnnotation)tu.getAnnotation(TargetsAnnotation.class)).get(langToUse);
				if ( tc == null ) {
					tf = tu.getSourceContent();
				}
				else {
					tf = tc.getContent();
				}
			}
		}
		// Apply the layer
		if ( layer == null ) {
			return getContent(tf, langToUse, encoderManager, context);
		}
		else {
			if ( context == 1 ) {
				return layer.endCode()
					+ getContent(tf, langToUse, encoderManager, context)
					+ layer.startCode();
			}
			return layer.endInline()
				+ getContent(tf, langToUse, encoderManager, context)
				+ layer.startInline();
		}
	}

	private String getContent (TextFragment tf,
		String langToUse,
		IEncoder encoder,
		int context)
	{
		context = 0; //TODO: Handle the case of non-trans inline at a high level
		if ( !tf.hasCode() ) { // The easy output
			if ( encoder == null ) {
				if ( layer == null ) {
					return tf.toString();
				}
				else {
					return layer.encode(tf.toString(), context);
				}
			}
			else {
				if ( layer == null ) {
					return encoder.encode(tf.toString(), context);
				}
				else {
					return layer.encode(
						encoder.encode(tf.toString(), context),
						context);
				}
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
				tmp.append(expandCodeContent(code, langToUse, context));
				break;
			case TextFragment.MARKER_CLOSING:
				code = codes.get(TextFragment.toIndex(text.charAt(++i)));
				tmp.append(expandCodeContent(code, langToUse, context));
				break;
			case TextFragment.MARKER_ISOLATED:
			case TextFragment.MARKER_SEGMENT:
				code = codes.get(TextFragment.toIndex(text.charAt(++i)));
				tmp.append(expandCodeContent(code, langToUse, context));
				break;
			default:
				if ( encoder == null ) {
					if ( layer == null ) {
						tmp.append(text.charAt(i));
					}
					else {
						tmp.append(layer.encode(text.charAt(i), context));
					}
				}
				else {
					if ( layer == null ) {
						tmp.append(encoder.encode(text.charAt(i), context));
					}
					else {
						tmp.append(layer.encode(
							encoder.encode(text.charAt(i), context),
							context));
					}
				}
				break;
			}
		}
		return tmp.toString();
	}
	
	private String expandCodeContent (Code code,
		String langToUse,
		int context)
	{
		String codeTmp = code.getData();
		if ( layer != null ) {
			codeTmp = layer.startInline() 
				+ layer.encode(code.getData(), context)
				+ layer.endInline();
		}
		if ( !code.hasReference() ) {
			return codeTmp;
		}
		// Check for segment
		if ( code.getType().equals(TextFragment.CODETYPE_SEGMENT) ) {
			if ( layer == null ) {
				return "[SEG-"+code.getData()+"]";
			}
			else {
				return layer.startCode()
					+ layer.encode("[SEG-"+code.getData()+"]", context)
					+ layer.endInline();
			}
		}
		// Else: look for place-holders
		StringBuilder tmp = new StringBuilder(codeTmp);
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
				tmp.replace(start, end, getString((TextUnit)ref, langToUse, context));
			}
			else if ( ref instanceof GenericSkeletonPart ) {
				tmp.replace(start, end, getString((GenericSkeletonPart)ref, context));
			}
			else if ( ref instanceof StorageList ) { // == StartGroup
				tmp.replace(start, end, getString((StorageList)ref, langToUse, context));
			}
			else if ( ref instanceof DocumentPart ) {
				tmp.replace(start, end, getString((GenericSkeleton)((IResource)ref).getSkeleton(), context));
			}
			else {
				tmp.replace(start, end, "-ERR:INVALID-TYPE-");
			}
		}
		return tmp.toString();
	}
	
	private String getString (StorageList list, String langToUse, int context) {
		StringBuilder tmp = new StringBuilder();
		// Treat the skeleton of this list
		tmp.append(getString((GenericSkeleton)list.getSkeleton(), context));		
		// Then treat the list itself
		for ( IResource res : list ) {
			if ( res instanceof TextUnit ) {
				tmp.append(getString((TextUnit)res, langToUse, context));
			}
			else if ( res instanceof StorageList ) {
				tmp.append(getString((StorageList)res, langToUse, context));
			}
			else if ( res instanceof DocumentPart ) {
				tmp.append(getString((GenericSkeleton)res.getSkeleton(), context));
			}
			else if ( res instanceof Ending ) {
				tmp.append(getString((GenericSkeleton)res.getSkeleton(), context));
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
