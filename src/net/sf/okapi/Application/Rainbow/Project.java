package net.sf.okapi.Application.Rainbow;

import java.util.ArrayList;

import net.sf.okapi.Library.Base.PathBuilder;
import net.sf.okapi.Library.Base.Utils;
import net.sf.okapi.Library.UI.LanguageManager;

public class Project {

	public String            inputRoot;
	public String            outputRoot;
	public String            path;
	public ArrayList<Input>  inputList;
	public PathBuilder       pathBuilder;
	public String            sourceLanguage;
	public String            sourceEncoding;
	public String            targetLanguage;
	public String            targetEncoding;
	
	public Project (LanguageManager lm) {
		inputRoot = System.getProperty("user.home");
		outputRoot = "";
		inputList = new ArrayList<Input>();
		pathBuilder = new PathBuilder();
		pathBuilder.setExtension(".out");
		sourceLanguage = Utils.getDefaultSourceLanguage().toUpperCase();
		targetLanguage = Utils.getDefaultTargetLanguage().toUpperCase();
		sourceEncoding = lm.getDefaultEncodingFromCode(sourceLanguage, Utils.getPlatformType());
		targetEncoding = lm.getDefaultEncodingFromCode(targetLanguage, Utils.getPlatformType());
	}

	/**
	 * Adds a document to the project.
	 * @param newPath Full path of the document to add.
	 * @param encoding Default encoding for the document (can be null).
	 * @param filterSettings Filter settings string for the document (can be null). 
	 * @return 0=Document added, 1=bad root, 2=exists already
	 */
	public int addDocument (String newPath,
		String encoding,
		String filterSettings)
	{
		// Is the root OK?
		if ( newPath.indexOf(inputRoot) == -1 ) return 1;
		newPath = newPath.substring(inputRoot.length());
		
		// Does the path exists already?
		for ( Input tmpInp : inputList ) {
			if ( tmpInp.relativePath.equalsIgnoreCase(newPath) ) return 2; 
		}
		
		// Create the new entry and add it to the list
		Input inp = new Input();
		inp.encoding = ((encoding == null) ? "" : encoding);
		inp.filterSettings = ((filterSettings == null) ? "" : filterSettings);
		inp.relativePath = newPath;
		inputList.add(inp);
		return 0;
	}

	/**
	 * Gets an input item from the list, based on its relative path name.
	 * @param relativePath Relative path of the item to search for.
	 * @return An Input object or null.
	 */
	public Input getItemFromRelativePath (String relativePath) {
		for ( Input inp : inputList ) {
			if ( inp.relativePath.equalsIgnoreCase(relativePath) ) return inp;
		}
		return null;
	}
	
	public void save (String newPath) {
		//TODO
	}
	
	public void load (String newPath) {
		//TODO
	}
}
