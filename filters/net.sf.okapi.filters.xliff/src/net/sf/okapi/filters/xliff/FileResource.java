package net.sf.okapi.filters.xliff;

import java.util.List;

import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.common.resource.IResourceContainer;

public class FileResource implements IResourceContainer {

	private String      name;
	
	public List<IExtractionItem> getExtractionItems () {
		return null;
	}

	public String getName () {
		return name;
	}

	public void setName (String name) {
		this.name = name;
	}

}
