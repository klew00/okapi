package net.sf.okapi.applications.rainbow.utilities.merging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import net.sf.okapi.applications.rainbow.lib.FilterAccess;
import net.sf.okapi.applications.rainbow.lib.Utils;
import net.sf.okapi.applications.rainbow.packages.IReader;
import net.sf.okapi.applications.rainbow.packages.Manifest;
import net.sf.okapi.applications.rainbow.packages.ManifestItem;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.pipeline.ThrougputPipeBase;
import net.sf.okapi.common.resource.IExtractionItem;

public class Merger extends ThrougputPipeBase {

	private Manifest         manifest;
	private IReader          reader;
	private FilterAccess     fa;

	public Merger () {
		fa = new FilterAccess();

		// Get the location of the class source
		File file = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
	    String rootFolder = file.getAbsolutePath();
	    // Remove the JAR file if running an installed version
	    if ( rootFolder.endsWith(".jar") ) rootFolder = Util.getDirectoryName(rootFolder);
	    // Remove the application folder in all cases
	    rootFolder = Util.getDirectoryName(rootFolder);
		String sharedFolder = Utils.getOkapiSharedFolder(rootFolder);

		//Load the FilterAccess list
		fa.loadList(sharedFolder + File.separator + "filters.xml");
	}

	public void initialize (Manifest manifest) {
		this.manifest = manifest;
		if ( reader != null ) {
			reader.closeDocument();
			reader = null;
		}
	}
	
	public void merge (int docID) {
		try {
			ManifestItem item = manifest.getItem(docID);
			// Skip items not selected for merge
			if ( !item.selected() ) return;
			
			// Original and parameters files
			String originalFile = manifest.getRoot() + File.separator + manifest.getOriginalLocation()
				+ File.separator + String.format("%d.ori", docID);
			String paramsFile = manifest.getRoot() + File.separator + manifest.getOriginalLocation()
				+ File.separator + String.format("%d.fprm", docID);
			// Load the relevant filter
			fa.loadFilter(item.getFilterID(), paramsFile);
			
			// File to merge
			String fileToMerge = manifest.getRoot() + File.separator + manifest.getTargetLocation()
				+ File.separator + item.getRelativeWorkPath();
			// Instantiate a package reader of the proper type
			if ( reader == null ) {
				reader = (IReader)Class.forName(manifest.getReaderClass()).newInstance();
			}
			reader.openDocument(fileToMerge);
			
			// Initializes the input
			InputStream input = new FileInputStream(originalFile);
			fa.inputFilter.initialize(input, originalFile, "TODO:filterSettings???", item.getInputEncoding(),
				manifest.getSourceLanguage(), manifest.getTargetLanguage());
			
			// Initializes the output
			String outputFile = manifest.getItemFullTargetPath(docID);
			OutputStream output = new FileOutputStream(outputFile);
			fa.outputFilter.initialize(output, item.getOutputEncoding(), manifest.getTargetLanguage());

			// Set the pipeline: inputFilter -> merger -> outputFilter 
			fa.inputFilter.setOutput(this);
			this.setOutput(fa.outputFilter);
			
			// Do it
			fa.inputFilter.process();
		}
		catch ( Exception e ) {
			throw new RuntimeException(e);
		}
		finally {
			if ( fa.outputFilter != null ) fa.outputFilter.close();
			if ( reader != null ) reader.closeDocument();
			if ( fa.inputFilter != null ) fa.inputFilter.close();
		}
	}

	@Override
    public void endExtractionItem (IExtractionItem sourceItem,
    	IExtractionItem targetItem)
	{
		//TODO: Merging
		super.endExtractionItem(sourceItem, targetItem);
	}
    
}
