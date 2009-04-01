/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
============================================================================*/

package net.sf.okapi.applications.rainbow.lib;

public class FormatManager {

	public void load (String p_sPath) {
		//TODO: Load format manager data from external file
	}
	
	/**
	 * Tries to guess the format and the encoding of a give document.
	 * @param p_sPath Full path of the document to process.
	 * @return An array of string: 0=guessed encoding or null,
	 * 1=guessed filter settings or null,
	 */
	public String[] guessFormat (String p_sPath) {
		String[] aRes = new String[2];
		aRes[0] = Utils.detectEncoding(p_sPath);
		aRes[1] = null; 
		String sExt = Utils.getExtension(p_sPath).toLowerCase();
		if ( sExt.equals(".properties") ) aRes[1] = "okf_properties";
		else if ( sExt.equals(".xlf") ) aRes[1] = "okf_xliff";
		else if ( sExt.equals(".xml") ) aRes[1] = "okf_xml";
		else if ( sExt.equals(".html") ) aRes[1] = "okf_html";
		else if ( sExt.equals(".asp") ) aRes[1] = "okf_html";
		else if ( sExt.equals(".php") ) aRes[1] = "okf_html";
		else if ( sExt.equals(".htm") ) aRes[1] = "okf_html";
		else if ( sExt.equals(".odt") ) aRes[1] = "okf_openoffice";
		else if ( sExt.equals(".ods") ) aRes[1] = "okf_openoffice";
		else if ( sExt.equals(".odp") ) aRes[1] = "okf_openoffice";
		else if ( sExt.equals(".odg") ) aRes[1] = "okf_openoffice";
		else if ( sExt.equals(".ott") ) aRes[1] = "okf_openoffice";
		else if ( sExt.equals(".tmx") ) aRes[1] = "okf_tmx";
		else if ( sExt.equals(".mif") ) aRes[1] = "okf_mif";
		else if ( sExt.equals(".rtf") ) aRes[1] = "okf_rtf";
		else if ( sExt.equals(".idml") ) aRes[1] = "okf_idml";
		else if ( sExt.equals(".po") ) aRes[1] = "okf_po";
		else if ( sExt.equals(".docx") ) aRes[1] = "okf_msoffice";
		else if ( sExt.equals(".xlsx") ) aRes[1] = "okf_msoffice";
		else if ( sExt.equals(".pptx") ) aRes[1] = "okf_msoffice";
		return aRes;
	}
}
