package net.sf.okapi.common.resource;

import net.sf.okapi.common.IParameters;

/**
 * Similar concept to a XLIFF file-element but only
 * the resource view of that. 
 */
public interface IResource extends IResourceContainer{
    
	String getSourceEncoding ();
	
	void setSourceEncoding (String encoding);

	String getTargetEncoding ();
	
	void setTargetEncoding (String encoding);
	
	String getFilterSettings ();
	
	void setFilterSettings (String filterSettings);
	
	IParameters getParameters ();
}
