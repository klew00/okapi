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
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.LocaleId;

/**
 * Resource associated with the filter event START_DOCUMENT.
 */
public class StartDocument extends BaseNameable {

	protected LocaleId locale;
	protected String encoding;
	protected boolean isMultilingual;
	protected IParameters params;
	protected IFilterWriter filterWriter;
	protected boolean hasUTF8BOM;
	protected String lineBreak;

	public StartDocument (String id) {
		super();
		this.id = id;
	}
	
	/**
	 * Gets the locale of this document. If the document is multilingual this
	 * is the source locale.
	 * @return the (source) locale of the document.
	 */
	public LocaleId getLocale () {
		return locale;
	}
	
	/**
	 * Sets the locale of the document. If the document is multilingual this
	 * is the source locale.
	 * @param locale (source) locale of the document.
	 */
	public void setLocale (LocaleId locale) {
		this.locale = locale;
	}

	/**
	 * Gets the character set encoding of this document. For example "UTF-8"
	 * @return the string identifying the character set of this document.
	 */
	public String getEncoding () {
		return encoding;
	}
	
	/**
	 * Sets the character set encoding of this document.
	 * @param encoding The string identifying the character set encoding of this document.
	 * For example "UTF-8".
	 * @param hasUTF8BOM true if this document is UTf-8 and has a Byte-Order-Mark.
	 * False in all other cases.
	 */
	public void setEncoding (String encoding,
		boolean hasUTF8BOM)
	{
		this.encoding = encoding;
		this.hasUTF8BOM = hasUTF8BOM;
	}
	
	/**
	 * Indicates if this document is multilingual.
	 * @return true if this document is multilingual, false otherwise.
	 */
	public boolean isMultilingual () {
		return isMultilingual;
	}
	
	/**
	 * Sets the flag that indicates if this document is multilingual. 
	 * @param value true to set this document as multilingual, false to set it
	 * as monolingual.
	 */
	public void setMultilingual (boolean value) {
		isMultilingual = value;
	}
	
	/**
	 * Indicates if this document is encoded as UTF8 and has a Byte-Order-Mark.
	 * @return true if this document is encoded as UTF8 and has a Byte-Order-Mark.
	 * False if the document is not encoded in UTF-8 or if if it is encoded in
	 * UTF-8 and has not a Byte-Order-Mark. 
	 */
	public boolean hasUTF8BOM () {
		return hasUTF8BOM;
	}
	
	/**
	 * Gets the type of line-break used in the original document.
	 * @return the type of line-break used in the original document.
	 */
	public String getLineBreak () {
		return lineBreak;
	}
	
	/**
	 * Sets the type of line-break used in the original document.
	 * @param value the type of line-break of the original document, for
	 * example: "\r\n" (for Windows/DOS line-break).
	 */
	public void setLineBreak (String value) {
		lineBreak = value;
	}

	/**
	 * Gets the filter writer for this document.
	 * @return the filter writer for this document.
	 */
	public IFilterWriter getFilterWriter () {
		return filterWriter;
	}
	
	/**
	 * Sets the filter writer for this document.
	 * @param filterWriter the filter writer for this document.
	 */
	public void setFilterWriter (IFilterWriter filterWriter) {
		this.filterWriter = filterWriter;
	}

	/**
	 * Gets the current parameters for this document.
	 * @return the object containing the parameters for this document.
	 */
	public IParameters getFilterParameters () {
		return params;
	}
	
	/**
	 * Sets the parameters for this document.
	 * @param params the object containing the parameters for this document.
	 */
	public void setFilterParameters (IParameters params) {
		this.params = params;
	}

}
