package net.sf.okapi.applications.rainbow.utilities.merging;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.applications.rainbow.packages.Manifest;
import net.sf.okapi.applications.rainbow.utilities.ISimpleUtility;
import net.sf.okapi.common.IParameters;


public class Utility implements ISimpleUtility {

	private String           inputRoot;
	private String           outputRoot;
	private String           manifestPath;
	private Manifest         manifest;
	private Merger           merger;
	private final Logger     logger = LoggerFactory.getLogger("net.sf.okapi.logging");
	
	public Utility () {
	}
	
	
	public void resetLists () {
		// Not used for this utility
	}
	
	public String getID () {
		return "oku_merging";
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
		// Not used in this utility.
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

	public void setParameters (IParameters paramsObject) {
		// Not used in this utility.
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
		Iterator<Integer> iter = manifest.getItems().keySet().iterator();
		while ( iter.hasNext() ) {
			merger.merge(iter.next());
		}
	}

	public boolean isFilterDriven () {
		return false;
	}
	
	public void addInputData (String path,
		String encoding,
		String filterSettings)
	{
		manifestPath = path;
		// Other information are not iused
	}

	public void addOutputData (String path,
		String encoding)
	{
		// Not used in this utility.
	}

	public int getInputCount () {
		return 1;
	}
	
	public String getFolderAfterProcess () {
		return null;
	}
}
