/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel (at ENLASO Corporation)                  */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

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
		String[] aRes = Utils.detectFileInformation(p_sPath, false);
		aRes[1] = null; 
		String sExt = Utils.getExtension(p_sPath).toLowerCase();
		if ( sExt.equals(".properties") ) aRes[1] = "okf_properties";
		else if ( sExt.equals(".json") ) aRes[1] = "okf_json";
		else if ( sExt.equals(".xlf") ) aRes[1] = "okf_xliff";
		else if ( sExt.equals(".xml") ) aRes[1] = "okf_xml";
		else if ( sExt.equals(".html") ) aRes[1] = "okf_html";
		else if ( sExt.equals(".htm") ) aRes[1] = "okf_html";
		return aRes;
	}
}
