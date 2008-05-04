package net.sf.okapi.Library.UI;

import net.sf.okapi.Library.Base.Utils;

public class FormatManager {

	public void load (String p_sPath) {
		//TODO
	}
	
	/**
	 * Tries to guess the format and the sourceEncoding of a give document.
	 * @param p_sPath Full path of the document to process.
	 * @return An array of string: 0=guessed sourceEncoding or null,
	 * 1=guessed filter settings or null,
	 */
	public String[] guessFormat (String p_sPath) {
		String[] aRes = Utils.detectFileInformation(p_sPath, false);
		aRes[1] = null; 
		String sExt = Utils.getExtension(p_sPath).toLowerCase();
		if ( sExt.equals(".properties") ) aRes[1] = "okf_properties";
		else if ( sExt.equals(".json") ) aRes[1] = "okf_json";
		return aRes;
	}
}
