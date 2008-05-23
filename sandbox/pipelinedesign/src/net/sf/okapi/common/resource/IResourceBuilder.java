package net.sf.okapi.common.resource;

public interface IResourceBuilder {
	public RawData getRawData();

	public IResource getResource();

    public void startResource(IResource resource);
    public void endResource(IResource resource);

    public void startExtractionItem(IExtractionItem extractionItem);
    public void endExtractionItem(IExtractionItem extractionItem);

    public void startContainer(IResourceContainer resourceContainer);
    public void endContainer(IResourceContainer resourceCntainer);
}

