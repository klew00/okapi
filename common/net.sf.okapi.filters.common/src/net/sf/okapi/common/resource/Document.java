/*===========================================================================*/
/* Copyright (C) 2008 Asgeir Frimannsson, Jim Hargrave, Yves Savourel        */
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

package net.sf.okapi.common.resource;

import net.sf.okapi.common.IParameters;

public class Document extends ResourceContainer {

	private static final long serialVersionUID = 1L;

	//TODO: Choose between this or direct property
	public String getSourceEncoding () {
		return getProperty("sourceEncoding");
	}
	
	//TODO: Choose between this or direct property
	public void setSourceEncoding (String value) {
		setProperty("sourceEncoding", value);
	}

	//TODO: Choose between this or direct property
	public String getTargetEncoding () {
		return getProperty("targetEncoding");
	}
	
	//TODO: Choose between this or direct property
	public void setTargetEncoding (String value) {
		setProperty("targetEncoding", value);
	}
	
	//TODO: Choose between this or direct property
	public String getSourceLanguage () {
		return getProperty("sourceLanguage");
	}
	
	//TODO: Choose between this or direct property
	public void setSourceLanguage (String value) {
		setProperty("sourceLanguage", value);
	}
	
	//TODO: Choose between this or direct property
	public String getTargetLanguage () {
		return getProperty("targetLanguage");
	}
	
	//TODO: Choose between this or direct property
	public void setTargetLanguage (String value) {
		setProperty("targetLanguage", value);
	}
	
	//TODO: Choose between this or direct property
	public String getFilterSettings () {
		return getProperty("filterSettings");
	}
	
	//TODO: Choose between this or direct property
	public void setFilterSettings (String value) {
		setProperty("filterSettings", value);
	}

	/**
	 * Gets the parameters object associated with this document.
	 * @return A parameter object implementing {@link IParameters}.
	 */
	public IParameters getParameters () {
		// To be overridden by each type of document resource
		return null;
	}
}
