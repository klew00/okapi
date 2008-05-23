package net.sf.okapi.common.resource;

import java.util.List;

/**
 * Similar to a XLIFF group, a structural container
 * to build hierarchies
 */
public interface IResourceContainer {
    public void setName(String name);
    public String getName();

    public void addExtractionItem(IExtractionItem item);
    public List<IExtractionItem> getExtractionItems();
}
