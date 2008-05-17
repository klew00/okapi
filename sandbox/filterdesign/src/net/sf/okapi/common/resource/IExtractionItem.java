package net.sf.okapi.common.resource;

/**
 * Similar to a XLIFF trans-unit, a self contained structural item
 * (can be further segmented at a higher level)
 */
public interface IExtractionItem {

    public void setContent(String content);
    public String getContent();
}
