package net.sf.okapi.applications.rainbow.utilities.alignment;

import net.sf.okapi.applications.rainbow.utilities.IUtility;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.pipeline.ThrougputPipeBase;
import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.common.resource.IResource;
import net.sf.okapi.common.resource.IResourceContainer;

public class Utility extends ThrougputPipeBase implements IUtility {

	private TMXWriter   writer;
	
	
	public Utility () {
		writer = new TMXWriter();
	}
	
	public void doEpilog () {
		writer.writeEndDocument();
	}

	public void doProlog (String sourceLanguage,
		String targetLanguage)
	{
		writer.create("Test.tmx"); //TODO: Get name from the params
		writer.writeStartDocument(sourceLanguage, targetLanguage);
	}

	public IParameters getParameters () {
		return null;
	}

	public String getInputRoot () {
		return null;
	}

	public String getOutputRoot () {
		return null;
	}

	public boolean hasParameters () {
		return false;
	}

	public boolean needsRoots () {
		return true;
	}

	public boolean needsOutputFilter () {
		// This utility does not re-write the input
		return false;
	}
	
	public void setParameters (IParameters paramsObject) {
	}

	public void setRoots (String inputRoot,
		String outputRoot)
	{
	}

	@Override
    public void startResource (IResource resource) {
    }
	
	@Override
    public void endResource (IResource resource) {
	}
	
	@Override
    public void startExtractionItem (IExtractionItem sourceItem,
    	IExtractionItem targetItem) {
	}
	
	@Override
    public void endExtractionItem (IExtractionItem sourceItem,
    	IExtractionItem targetItem)
	{
		if (( targetItem != null ) && ( !targetItem.isEmpty() )) {
			writer.writeItem(sourceItem, targetItem);
		}
	}
    
	@Override
    public void startContainer (IResourceContainer resourceContainer) {
	}

	@Override
	public void endContainer (IResourceContainer resourceCntainer) {
	}

	public void processInput () {
		// Do nothing: this utility is filter-driven.
	}

	public boolean isFilterDriven () {
		return true;
	}

	public void setInputData (String path,
		String encoding,
		String filterSettings)
	{
		// Not used for this utility
	}

	public void setOutputData(String path,
		String encoding)
	{
		// Not used for this utility
	}
}
