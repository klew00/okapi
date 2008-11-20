package net.sf.okapi.apptest.skeleton;

import net.sf.okapi.apptest.common.INameable;
import net.sf.okapi.apptest.common.IReferenceable;
import net.sf.okapi.apptest.common.ISkeleton;
import net.sf.okapi.apptest.common.ISkeletonPart;
import net.sf.okapi.apptest.filters.IWriterHelper;
import net.sf.okapi.apptest.resource.BaseResource;
import net.sf.okapi.apptest.resource.DocumentPart;
import net.sf.okapi.apptest.resource.Property;
import net.sf.okapi.apptest.resource.StartGroup;
import net.sf.okapi.apptest.resource.TextFragment;
import net.sf.okapi.apptest.resource.TextUnit;

public class GenericSkeletonPart extends BaseResource implements ISkeletonPart {

	private StringBuilder data;
	
	public GenericSkeletonPart (String id, String data) {
		this.data = new StringBuilder(data);
		this.id = id;
	}

	public GenericSkeletonPart (String id, String data, boolean isReference) {
		this.data = new StringBuilder(data);
		this.id = id;
		this.isReferent = isReference;
	}
	
	@Override
	public String toString () {
		return data.toString();
	}

	public String toString (IWriterHelper helper) {
		if ( data.toString().startsWith(TextFragment.REFMARKER_START) ) {
			Object[] marker = TextFragment.getRefMarker(data);
			if ( marker != null ) {
				String propName = (String)marker[3];
				IReferenceable ref = helper.getReference((String)marker[0]);
				if ( ref == null ) return "-ERR:REF-NOT-FOUND-";
				// Else: process the reference
				if ( ref instanceof TextUnit ) {
					if ( propName == null ) {
						TextFragment tf;
						TextUnit tu = ((TextUnit)ref);
						if ( helper.getLanguage() == null ) {
							tf = tu.getContent();
						}
						else {
							if ( tu.getAnnotation(helper.getLanguage()) == null ) {
								tf = tu.getContent();
							}
							else {
								tf = ((TextUnit)tu.getAnnotation(helper.getLanguage())).getContent();
							}
						}
						return tf.toString(helper);
					}
					else {
						return getPropertyValue((INameable)ref, propName, helper.getLanguage());
					}
				}
				else if ( ref instanceof GenericSkeletonPart ) {
					if ( propName == null )
						return ref.toString(helper);
					else
						return getPropertyValue((INameable)ref, propName, helper.getLanguage());
				}
				else if ( ref instanceof StartGroup ) {
					if ( propName == null )
						return ref.toString(helper);
					else
						return getPropertyValue((INameable)ref, propName, helper.getLanguage());
				}
				else if ( ref instanceof DocumentPart ) {
					return "!!Not supported!!"; //TODO: why not???
				}
			}
		}
		return data.toString();
	}
	
	private String getPropertyValue (INameable unit,
		String name,
		String language)
	{
		Property prop = unit.getProperty(name);
		if ( prop == null ) return "-ERR:NO-SUCH-PROP-";
		String value;
		if ( language == null ) {
			value = prop.getValue();
		}
		else {
			prop = (Property)prop.getAnnotation(language);
			if ( prop == null ) return unit.getProperty(name).getValue(); // Fall back to source
			value = prop.getValue();
		}
		if ( value == null ) return "-ERR:PROP-NOT-FOUND-";
		else return value;
	}
		
	public void append (String data) {
		this.data.append(data);
	}

	@Override
	public ISkeleton getSkeleton () {
		// Never used in this class
		// This is there only because it is part of the IResource interface
		return null;
	}

	@Override
	public void setSkeleton (ISkeleton skeleton) {
		// Never used in this class
		// This is there only because it is part of the IResource interface
	}

}
