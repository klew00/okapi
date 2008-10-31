package net.sf.okapi.apptest.resource;

import net.sf.okapi.apptest.common.ISkeleton;
import net.sf.okapi.apptest.common.ISkeletonPart;

public class GenericSkeletonPart implements ISkeletonPart, IReferenceable {

	private boolean isReference = false;
	private String id;
	private StringBuilder data;
	
	public GenericSkeletonPart (String data) {
		this.data = new StringBuilder(data);
	}

	public GenericSkeletonPart (String id, String data) {
		this.data = new StringBuilder(data);
		this.id = id;
	}

	public GenericSkeletonPart (String id, String data, boolean isReference) {
		this.data = new StringBuilder(data);
		this.id = id;
		this.isReference = isReference;
	}

	public String toString (BuilderData builderData) {
		if ( data.toString().startsWith(TextFragment.REFMARKER_START) ) {
			Object[] marker = TextFragment.getRefMarker(data);
			if ( marker != null ) {
				String propName = (String)marker[3];
				for ( IReferenceable ref : builderData.references ) {
					if ( ref.getID().equals((String)marker[0]) ) {
						TextContainer tc;
						if ( ref instanceof TextUnit ) {
							if ( builderData.outputTarget ) tc = ((TextUnit)ref).getTargetContent();
							else tc = ((TextUnit)ref).getSourceContent();
							if ( propName == null )
								return tc.toString(builderData);
							else
								return "propVALUE-TODO";
						}
						else if ( ref instanceof GenericSkeletonPart ) {
							if ( propName == null )
								return ref.toString();
							else
								return "propValue-TODO";
						}
						else if ( ref instanceof Group ) {
							return "!!Not supported!!";
						}
						else if ( ref instanceof PropertiesUnit ) {
							return "!!Not supported!!";
						}
						break;
					}
				}
			}
			else return "-ERR: Bad marker-";
		}
		return data.toString();
	}
	
	public boolean isReference() {
		return isReference;
	}

	public void setIsReference (boolean value) {
		isReference = value;
	}

	public String getID () {
		return id;
	}

	public void setID (String id) {
		this.id = id;
	}

	public void append (String data) {
		this.data.append(data);
	}

	public ISkeleton getSkeleton() {
		assert(false); // There is no skeleton of skeleton
		return null;
	}

	public void setSkeleton(ISkeleton skeleton) {
		assert(false); // There is no skeleton of skeleton
	}
}
