package net.sf.okapi.applications.rainbow.utilities.xsltransform;

import java.io.File;
import java.util.Hashtable;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import net.sf.okapi.applications.rainbow.utilities.ISimpleUtility;
import net.sf.okapi.common.ConfigurationString;
import net.sf.okapi.common.IParameters;

public class Utility implements ISimpleUtility {

	private String                     srcLang;
	private String                     trgLang;
	private Source                     xsltInput;
	private String                     inputPath;
	private String                     outputPath;
	private Hashtable<String, String>  paramList;
	private Transformer                trans;
	private IParameters                params;

	
	public Utility () {
		params = new Parameters();
	}
	
	public String getID () {
		return "oku_xsltransform";
	}
	
	public void processInput () {
		try {
			trans.reset();
			fillParameters();
			// Create the source for the XML input
			Source xmlInput = new javax.xml.transform.stream.StreamSource(new File(inputPath));
			// Create the output
			Result result = new javax.xml.transform.stream.StreamResult(new File(outputPath));
			trans.transform(xmlInput, result);
		}
		catch ( TransformerException e ) {
			throw new RuntimeException("Error when transforming.", e);
		}
	}

	public void doEpilog () {
	}

	public void doProlog (String sourceLanguage,
		String targetLanguage)
	{
		try {
			// Store the languages
			srcLang = sourceLanguage;
			trgLang = targetLanguage;
			
			ConfigurationString cfgString = new ConfigurationString(
				params.getParameter("paramList"));
			paramList = cfgString.toHashtable();
			
			// Create the source for the XSLT
			xsltInput = new javax.xml.transform.stream.StreamSource(
				new File(params.getParameter("xsltPath")));
			
			// Create an instance of TransformerFactory
			javax.xml.transform.TransformerFactory fact =
				javax.xml.transform.TransformerFactory.newInstance();
			trans = fact.newTransformer(xsltInput);
		}
		catch ( TransformerConfigurationException e ) {
			throw new RuntimeException("Error in the XSLT input.", e);
		}
	}

	public String getInputRoot () {
		return null;
	}

	public String getOutputRoot () {
		return null;
	}

	public IParameters getParameters () {
		return params;
	}

	public boolean hasParameters () {
		return true;
	}

	public boolean isFilterDriven () {
		return false;
	}

	public boolean needsRoots () {
		return false;
	}

	public void setInputData (String path,
		String encoding,
		String filterSettings)
	{
		inputPath = path;
	}

	public void setOutputData(String path,
		String encoding)
	{
		outputPath = path;
	}

	public void setParameters (IParameters paramsObject) {
		params = paramsObject;
	}

	public void setRoots (String inputRoot,
		String outputRoot)
	{
		// Not used in this utility.
	}

	private void fillParameters () {
		trans.clearParameters();
		String value;
		for ( String key : paramList.keySet() ) {
			//TODO: implement a common way to do lang-macros (see pathBuilder)
			//TODO: implement macros for filename <$Input1>, etc.
			value = paramList.get(key).replace("<$SrcLang>", srcLang);
			value = value.replace("<$TrgLang>", trgLang);
			trans.setParameter(key, value);
		}
	}
}
