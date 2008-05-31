package net.sf.okapi.common.resource;

import java.util.ArrayList;
import java.util.List;

public abstract class ResourceBase implements IResource {

    private String                name;
    private List<IExtractionItem> extractionItems = new ArrayList<IExtractionItem>();
    private String                sourceEncoding;
    private String                targetEncoding;
    private String                filterSettings;
   
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<IExtractionItem> getExtractionItems() {
        return extractionItems;
    }

	public String getFilterSettings () {
		return filterSettings;
	}

	public String getSourceEncoding () {
		return sourceEncoding;
	}

	public String getTargetEncoding () {
		return targetEncoding;
	}

	public void setFilterSettings (String filterSettings) {
		if ( filterSettings == null ) throw new NullPointerException();
		this.filterSettings = filterSettings;
	}

	public void setSourceEncoding (String encoding) {
		if ( encoding == null ) throw new NullPointerException();
		sourceEncoding = encoding;
	}

	public void setTargetEncoding (String encoding) {
		if ( encoding == null ) throw new NullPointerException();
		targetEncoding = encoding;
	}
   
}
