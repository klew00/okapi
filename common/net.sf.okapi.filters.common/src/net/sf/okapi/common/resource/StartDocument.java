/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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
===========================================================================*/

package net.sf.okapi.common.resource;

import net.sf.okapi.common.IParameters;

/**
 * Represents the resource associated with the filter event START_DOCUMENT.
 */
public class StartDocument extends BaseNameable {

	protected String language;
	protected String encoding;
	protected boolean isMultilingual;
	protected IParameters params;

	public StartDocument (String id) {
		super();
		this.id = id;
	}
	
	/**
	 * Gets the language of this document. If the document is multilingual this
	 * is the source language.
	 * @return The (source) language of the document.
	 */
	public String getLanguage () {
		return language;
	}
	
	/**
	 * Sets the language of the document. If the document is multilingual this
	 * is the source language.
	 * @param language (Source) language of the document.
	 */
	public void setLanguage (String language) {
		this.language = language;
	}

	/**
	 * Gets the character set encoding of this document. For example "UTF-8"
	 * @return The string identifying the character set of this document.
	 */
	public String getEncoding () {
		return encoding;
	}
	
	/**
	 * Sets the character set encoding of this document.
	 * @param encoding The string identifying the character set encoding of this document.
	 * For example "UTF-8".
	 */
	public void setEncoding (String encoding) {
		this.encoding = encoding;
	}
	
	/**
	 * Indicates if this document is multilingual.
	 * @return True if this document is multi-lingual, false otherwise.
	 */
	public boolean isMultilingual () {
		return isMultilingual;
	}
	
	/**
	 * Sets the flag that indicates if this document is multilingual. 
	 * @param value True to set this document as multilingual, false to set it
	 * as monolingual.
	 */
	public void setIsMultilingual (boolean value) {
		isMultilingual = value;
	}

	/**
	 * Gets the current parameters for this document.
	 * @return The object containing the parameters for this document.
	 */
	public IParameters getFilterParameters () {
		return params;
	}
	
	/**
	 * Sets the parameters for this document.
	 * @param params The object containing the parameters for this document.
	 */
	public void setFilterParameters (IParameters params) {
		this.params = params;
	}

}
