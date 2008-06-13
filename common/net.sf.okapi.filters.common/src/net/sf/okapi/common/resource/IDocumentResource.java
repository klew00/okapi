package net.sf.okapi.common.resource;

import net.sf.okapi.common.IParameters;

public interface IDocumentResource extends IGroupResource {
	
	String getSourceLanguage ();
	
	void setSourceLanguage (String languageCode);
	
	String getTargetLanguage ();
	
	void setTargetLanguage (String languageCode);
	
	String getSourceEncoding ();
	
	void setSourceEncoding (String encoding);
	
	String getTargetEncoding ();
	
	void setTargetEncoding (String encoding);
	
	String getTargetName ();
	
	void setTargetName (String name);
	
	String getSourceRoot ();
	
	void setSourceRoot (String rootFolder);
	
	String toXML ();
	
	String getFilterSettings ();
	
	void setFilterSettings (String filterSettings);
	
	IParameters getParameters (); 
}
