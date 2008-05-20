package net.sf.okapi.common.resource;

import java.util.ArrayList;
import java.util.List;

public class ResourceBase implements IResource {

    private String name;
    private List<IExtractionItem> extractionItems = new ArrayList<IExtractionItem>();
    
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
