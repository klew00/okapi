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

	private String      inputRoot;
	private String      inputPath;
	private String      outputRoot;
	private String      outputPath;
	private Parameters  params;
	private IWriter     writer;
	private int         id;
	
	
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
		
		id = 0;
		writer.setParameters(sourceLanguage, targetLanguage,
			"TODO:projectID", params.outputFolder + File.separator + params.pkgName,
			params.makePackageID(), inputRoot);
		writer.writeStartPackage();
	}

	public IParameters getParameters () {
		return params;
	}

	public String getInputRoot () {
		return inputRoot;
	}

	public String getOutputRoot () {
		return outputRoot;
	}

	public boolean hasParameters () {
		return true;
	}

	public boolean needsRoots () {
		return true;
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
		if ( inputRoot == null ) throw new NullPointerException();
		if ( outputRoot == null ) throw new NullPointerException();
		this.inputRoot = inputRoot;
		this.outputRoot = outputRoot;
	}

	@Override
    public void startResource (IResource resource) {
		String relativeInput = inputPath.substring(inputRoot.length()+1);
		String relativeOutput = outputPath.substring(outputRoot.length()+1);
		writer.createDocument(++id, relativeInput,
			relativeOutput, resource.getSourceEncoding(),
			resource.getTargetEncoding(), resource.getFilterSettings(),
			resource.getParameters());
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
		if ( path == null ) throw new NullPointerException();
		inputPath = path;
	}

	public void setOutputData (String path,
		String encoding)
	{
		if ( path == null ) throw new NullPointerException();
		// Not used: if ( encoding == null ) throw new NullPointerException();
		outputPath = path;
		// Not used: outputEncoding = encoding;
	}
}
