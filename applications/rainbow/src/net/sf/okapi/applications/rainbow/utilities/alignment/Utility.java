package net.sf.okapi.applications.rainbow.utilities.alignment;

import net.sf.okapi.applications.rainbow.lib.ILog;
import net.sf.okapi.applications.rainbow.utilities.IUtility;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.pipeline.ThrougputPipeBase;
import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.common.resource.IResource;
import net.sf.okapi.common.resource.IResourceContainer;

public class Utility extends ThrougputPipeBase implements IUtility {

	private ILog        log;
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
		try {
			writer.create("Test.tmx"); //TODO: Get name from the params
			writer.writeStartDocument(sourceLanguage, targetLanguage);
		}
		catch ( Exception E ) {
			log.error(E.getLocalizedMessage());
		}
	}

	public IParameters getParameters () {
		return null;
	}

	public String getRoot () {
		return null;
	}

	public boolean hasParameters () {
		return false;
	}

	public boolean needRoot () {
		return true;
	}

	public boolean needOutput () {
		// This utility does not re-write the input
		return false;
	}
	
	public void setParameters (IParameters paramsObject) {
	}

	public void setRoot (String root) {
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
}
