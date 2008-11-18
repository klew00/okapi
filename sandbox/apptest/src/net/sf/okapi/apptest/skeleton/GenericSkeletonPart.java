package net.sf.okapi.apptest.skeleton;

import net.sf.okapi.apptest.common.IReferenceable;
import net.sf.okapi.apptest.common.IResource;
import net.sf.okapi.apptest.common.ISkeleton;
import net.sf.okapi.apptest.common.ISkeletonPart;
import net.sf.okapi.apptest.filters.IWriterHelper;
import net.sf.okapi.apptest.resource.DocumentPart;
import net.sf.okapi.apptest.resource.StartGroup;
import net.sf.okapi.apptest.resource.TextContainer;
import net.sf.okapi.apptest.resource.TextFragment;
import net.sf.okapi.apptest.resource.TextUnit;

public class GenericSkeletonPart implements ISkeletonPart, IReferenceable, IResource {

	private boolean isReferent = false;
	private String id;
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

	public String toString (IWriterHelper refProv) {
		if ( data.toString().startsWith(TextFragment.REFMARKER_START) ) {
			Object[] marker = TextFragment.getRefMarker(data);
			if ( marker != null ) {
				String propName = (String)marker[3];
				IReferenceable ref = refProv.getReference((String)marker[0]);
				if ( ref == null ) return "-ERR: Bad marker-";
				// Else: process the reference
				TextContainer tc;
				if ( ref instanceof TextUnit ) {
					if ( refProv.useTarget() ) tc = ((TextUnit)ref).getTargetContent();
					else tc = ((TextUnit)ref).getSourceContent();
					if ( propName == null )
						return tc.toString(refProv);
					else
						return "propVALUE-TODO";
				}
				else if ( ref instanceof GenericSkeletonPart ) {
					if ( propName == null )
						return ref.toString();
					else
						return "propValue-TODO";
				}
				else if ( ref instanceof StartGroup ) {
					if ( propName == null )
						return ref.toString(refProv);
					else
						return "propValue-TODO";
				}
				else if ( ref instanceof DocumentPart ) {
					return "!!Not supported!!";
				}
			}
		}
		return data.toString();
	}
	
	public boolean isReferent () {
		return isReferent;
	}

	public void setIsReferent (boolean value) {
		isReferent = value;
	}

	public String getId () {
		return id;
	}

	public void setId (String id) {
		this.id = id;
	}

	public void append (String data) {
		this.data.append(data);
	}

	public ISkeleton getSkeleton () {
		// Never used in this class
		return null;
	}

	public void setSkeleton (ISkeleton skeleton) {
		// Never used in this class
	}

}
