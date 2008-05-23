package net.sf.okapi.common.filters.myformat2;

import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.common.resource.IResource;
import net.sf.okapi.common.resource.IResourceBuilder;
import net.sf.okapi.common.resource.IResourceContainer;
import net.sf.okapi.common.resource.RawData;

public class MyFormat2ResourceBuilder implements IResourceBuilder {
	private final RawData rawData;
	private IResource resource;

    public MyFormat2ResourceBuilder(RawData rawData) {
		this.rawData = rawData;
	}

	public void startResource (IResource resource) {
    	try {
    		this.resource = resource;
    	}
    	catch ( Exception e ) {
    		System.err.println(e.getLocalizedMessage());
    	}
    }

    public void endResource(IResource resource) {
    }

    public void startContainer (IResourceContainer resourceContainer) {
    }

    public void endContainer (IResourceContainer resourceCntainer) {
    }

    public void startExtractionItem (IExtractionItem extractionItem) {
    	resource.addExtractionItem(extractionItem);
    }

    public void endExtractionItem(IExtractionItem extractionItem) {
    }

	public RawData getRawData() {
		return rawData;
	}

	public IResource getResource() {
		return resource;
	}
}
