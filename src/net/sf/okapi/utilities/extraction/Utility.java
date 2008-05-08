package net.sf.okapi.utilities.extraction;

import java.io.File;

import net.sf.okapi.Filter.IFilter;
import net.sf.okapi.Filter.IFilterItem;
import net.sf.okapi.Library.Base.ILog;
import net.sf.okapi.Library.Base.IParameters;
import net.sf.okapi.Package.IWriter;
import net.sf.okapi.utility.IUtility;

public class Utility implements IUtility {

	Parameters          params;
	String              rootFolder;
	IFilter             filter;
	IWriter             writer;
	ILog                log;
	IFilterItem         targetItem;
	
	public Utility () {
		params = new Parameters();
	}
	
	public void initialize (ILog newLog) {
		log = newLog;
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

	public boolean needRoot () {
		return true;
	}

	public void processEndDocument () {
		writer.writeEndDocument();
	}

	public void processItem (IFilterItem sourceItem,
		IFilterItem targetItem)
	{
		int status = IFilterItem.TSTATUS_TOTRANS;
		if ( !sourceItem.isTranslatable() ) status = IFilterItem.TSTATUS_NOTRANS;
		else if ( sourceItem.isTranslated() ) status = IFilterItem.TSTATUS_TOEDIT;

		writer.writeItem(sourceItem, targetItem, status);
	}

	public void processStartDocument (IFilter newFilter,
		String inputPath,
		String outputPath,
		String outputEncoding)
	{
		filter = newFilter;
		String relativePath = inputPath.substring(rootFolder.length()+1);
		writer.createDocument(relativePath.hashCode(), relativePath);
		writer.writeStartDocument(relativePath);
	}

	public void setParameters (IParameters paramsObject) {
		params = (Parameters)paramsObject;
	}

	public void setRoot (String root) {
		rootFolder = root;
	}

	public void endProcess () {
		if ( writer != null ) {
			writer.writeEndPackage(params.createZip);
			writer = null;
		}
	}

	public void startProcess (String inputLanguage,
		String outputLanguage)
	{
		try {
			if ( params.pkgType.equals("xliff") )
				writer = new net.sf.okapi.Package.XLIFF.Writer(log);
			else if ( params.pkgType.equals("omegat") )
				writer = new net.sf.okapi.Package.OmegaT.Writer(log);
			else if ( params.pkgType.equals("ttx") )
				writer = new net.sf.okapi.Package.ttx.Writer(log);
			else
				throw new Exception("Unknown package type: " + params.pkgType);
			
			writer.setParameters(inputLanguage, outputLanguage,
				"TODO:projectID", params.outputFolder + File.separator + params.pkgName,
				params.makePackageID());
			writer.writeStartPackage();
		}
		catch ( Exception E ) {
			log.error(E.getLocalizedMessage());
		}
	}

}
