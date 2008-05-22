package net.sf.okapi.common.resource;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.filters.RawData;

public class Resource implements IResource {	
    private String name;
    private List<IExtractionItem> extractionItems = new ArrayList<IExtractionItem>();
    private final RawData rawData;
    
    /**
     * Constructs a document without raw-data. Equivalent of <tt>new Document(null)</tt>.
     * 
     * @see #Resource(RawData)
     */
    public Resource() {
       this(null);
    }
    
    /**
     * Constructs a resource with the given raw-data.
     * 
     * @param rawData the raw-data of this document, can be <tt>null</tt>.
     * 
     * @see #Resource()
     */
    public Resource(RawData rawData) {
       this.rawData = rawData;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<IExtractionItem> getExtractionItems() {
        return extractionItems;
    }
}
