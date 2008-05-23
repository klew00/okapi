package net.sf.okapi.common.resource;

import java.util.ArrayList;
import java.util.List;

public class ResourceContainer implements IResourceContainer{

    private String name;
    private List<IExtractionItem> extractionItems = new ArrayList<IExtractionItem>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addExtractionItem(IExtractionItem item) {
    	extractionItems.add(item);
    }

    public List<IExtractionItem> getExtractionItems() {
        return extractionItems;
    }

}
