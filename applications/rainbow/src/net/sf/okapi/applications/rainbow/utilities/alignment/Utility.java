package net.sf.okapi.applications.rainbow.utilities.alignment;

import net.sf.okapi.applications.rainbow.lib.TMXWriter;
import net.sf.okapi.applications.rainbow.utilities.IFilterDrivenUtility;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.pipeline.ThrougputPipeBase;
import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.common.resource.IResource;
import net.sf.okapi.common.resource.IResourceContainer;

public class Utility extends ThrougputPipeBase implements IFilterDrivenUtility {

	private TMXWriter   writer;
	private Parameters  params;
	
	
	public Utility () {
		writer = new TMXWriter();
		params = new Parameters();
	}
	
	public void doEpilog () {
		writer.writeEndDocument();
		writer.close();
	}

	public void doProlog (String sourceLanguage,
		String targetLanguage)
	{
		writer.create(params.tmxPath);
		writer.writeStartDocument(sourceLanguage, targetLanguage);
	}

	public IParameters getParameters () {
		return params;
	}

	public String getInputRoot () {
		return null;
	}

	public String getOutputRoot () {
		return null;
	}

	public boolean hasParameters () {
		return true;
	}

	public boolean needsRoots () {
		return false;
	}

	public boolean needsOutputFilter () {
		// This utility does not re-write the input
		return false;
	}
	
	public void setParameters (IParameters paramsObject) {
		params = (Parameters)paramsObject;
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
    public void startExtractionItem (IExtractionItem item) {
	}
	
	@Override
    public void endExtractionItem (IExtractionItem item) {
		if ( item.hasTarget() ) {
			if ( !item.getTarget().isEmpty() ) {
				writer.writeItem(item);
			}
		}
	}
    
	@Override
    public void startContainer (IResourceContainer resourceContainer) {
	}

	@Override
	public void endContainer (IResourceContainer resourceCntainer) {
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
