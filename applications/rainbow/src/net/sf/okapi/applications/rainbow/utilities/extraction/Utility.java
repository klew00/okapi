package net.sf.okapi.applications.rainbow.utilities.extraction;

import java.io.File;

import net.sf.okapi.applications.rainbow.packages.IWriter;
import net.sf.okapi.applications.rainbow.utilities.IUtility;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.pipeline.ThrougputPipeBase;
import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.common.resource.IResource;
import net.sf.okapi.common.resource.IResourceContainer;

public class Utility extends ThrougputPipeBase implements IUtility {

	private String      rootFolder;
	private Parameters  params;
	private IWriter     writer;
	
	
	public Utility () {
		params = new Parameters();
	}
	
	public void doEpilog () {
		if ( writer != null ) {
			writer.writeEndPackage(params.createZip);
			writer = null;
		}
	}

	public void doProlog (String sourceLanguage,
		String targetLanguage)
	{
		if ( params.pkgType.equals("xliff") )
			writer = new net.sf.okapi.applications.rainbow.packages.xliff.Writer();
		else if ( params.pkgType.equals("omegat") )
			writer = new net.sf.okapi.applications.rainbow.packages.omegat.Writer();
		else if ( params.pkgType.equals("ttx") )
			writer = new net.sf.okapi.applications.rainbow.packages.ttx.Writer();
		else
			throw new RuntimeException("Unknown package type: " + params.pkgType);
			
		writer.setParameters(sourceLanguage, targetLanguage,
			"TODO:projectID", params.outputFolder + File.separator + params.pkgName,
			params.makePackageID(), rootFolder);
		writer.writeStartPackage();
	}

	public IParameters getParameters () {
		return params;
	}

	public String getRoot () {
		return rootFolder;
	}

	public boolean hasParameters () {
		return true;
	}

	public boolean needsRoot () {
		return true;
	}

	public boolean needsOutput () {
		// This utility does not re-write the input
		return false;
	}
	
	public void setParameters (IParameters paramsObject) {
		params = (Parameters)paramsObject;
	}

	public void setRoot (String root) {
		if ( root == null ) throw new NullPointerException();
		rootFolder = root;
	}

	@Override
    public void startResource (IResource resource) {
		//TODO: Check name for problems
		String relativePath = resource.getName().substring(rootFolder.length()+1);
		writer.createDocument(relativePath.hashCode(), relativePath,
			resource.getSourceEncoding(), resource.getTargetEncoding(),
			resource.getFilterSettings(), resource.getParameters());
		writer.writeStartDocument();
    }
	
	@Override
    public void endResource (IResource resource) {
		writer.writeEndDocument();
	}
	
	@Override
    public void startExtractionItem (IExtractionItem sourceItem,
    	IExtractionItem targetItem) {
	}
	
	@Override
    public void endExtractionItem (IExtractionItem sourceItem,
    	IExtractionItem targetItem)
	{
		//int status = IFilterItem.TSTATUS_TOTRANS;
		//if ( !sourceItem.isTranslatable() ) status = IFilterItem.TSTATUS_NOTRANS;
		//else if ( sourceItem.hasTarget() ) status = IFilterItem.TSTATUS_TOEDIT;

		writer.writeItem(sourceItem,
			(params.includeTargets ? targetItem : null), 0); //TODO: status
	}
    
	@Override
    public void startContainer (IResourceContainer resourceContainer) {
	}

	@Override
	public void endContainer (IResourceContainer resourceCntainer) {
	}

	public void execute (String inputPath) {
		// Do nothing: this utility is filter-driven.
	}

	public boolean isFilterDriven () {
		return true;
	}
}
