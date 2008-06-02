package net.sf.okapi.applications.rainbow.utilities.merging;

import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.applications.rainbow.packages.Manifest;
import net.sf.okapi.applications.rainbow.utilities.IUtility;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.pipeline.ThrougputPipeBase;

public class Utility extends ThrougputPipeBase implements IUtility {

	private String           inputRoot;
	private String           outputRoot;
	private String           manifestPath;
	private Manifest         manifest;
	private Merger           merger;
	private final Logger     logger = LoggerFactory.getLogger(Utility.class);
	
	public Utility () {
	}
	
	public void doEpilog () {
	}

	public void doProlog (String sourceLanguage,
		String targetLanguage)
	{
		manifest = new Manifest();
		merger = new Merger();
	}

	public IParameters getParameters () {
		return null;
	}

	public String getInputRoot () {
		return inputRoot;
	}

	public String getOutputRoot () {
		return outputRoot;
	}

	public boolean hasParameters () {
		return false;
	}

	public boolean needsRoots () {
		return true;
	}

	public boolean needsOutputFilter () {
		return false;
	}
	
	public void setParameters (IParameters paramsObject) {
	}

	public void setRoots (String inputRoot,
		String outputRoot)
	{
		if ( inputRoot == null ) throw new NullPointerException();
		if ( outputRoot == null ) throw new NullPointerException();
		this.inputRoot = inputRoot;
		this.outputRoot = outputRoot;
	}

	public void processInput () {
		// Load the manifest file to use
		manifest.load(manifestPath);
		// Check the package where the manifest has been found
		manifest.checkPackageContent();
		// Initialize the merger for this manifest
		merger.initialize(manifest);
		
		// One target language only, and take it from the manifest
		String targetLang = manifest.getTargetLanguage();
		logger.info("Target: " + targetLang);
		
		// Process each selected document in the manifest
		Enumeration<Integer> E = manifest.getItems().keys();
		while ( E.hasMoreElements() ) {
			merger.merge(E.nextElement());
		}
	}

	public boolean isFilterDriven () {
		return false;
	}
	
	public void setInputData (String path,
		String encoding,
		String filterSettings)
	{
		manifestPath = path;
		// Other information are not iused
	}

	public void setOutputData (String path,
		String encoding)
	{
		// Not used in this utility.
	}
}
