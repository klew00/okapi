/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.common.filters;

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import static net.sf.okapi.common.MimeTypeMapper.*;

public final class FilterFactory {

	private static final Logger LOGGER = Logger.getLogger(FilterFactory.class.getName());
	private static final Hashtable<String, String> mimeMap;

	/**
	 * Creates a new filter manager, with default pre-defined filters loaded.
	 */
	static {
		mimeMap = new Hashtable<String, String>();
		mimeMap.put(XML_MIME_TYPE, "net.sf.okapi.filters.xml.XMLFilter");
		mimeMap.put(ODF_MIME_TYPE, "net.sf.okapi.filters.openoffice.ODFFilter");
		mimeMap.put(HTML_MIME_TYPE, "net.sf.okapi.filters.html.HtmlFilter");
		mimeMap.put(SERVER_SIDE_INCLUDE_MIME_TYPE, "net.sf.okapi.filters.html.HtmlFilter");
		mimeMap.put(PO_MIME_TYPE, "net.sf.okapi.filters.po.POFilter");
		mimeMap.put(RTF_MIME_TYPE, "net.sf.okapi.filters.rtf.RTFFilter");
		mimeMap.put(JAVASCRIPT_MIME_TYPE, "net.sf.okapi.filters.javascript.JavascriptFilter");
		mimeMap.put(CSV_MIME_TYPE, "net.sf.okapi.filters.csv.CsvFilter");
		mimeMap.put(MIF_MIME_TYPE, "net.sf.okapi.filters.mif.MIFFilter");
		mimeMap.put(PROPERTIES_MIME_TYPE, "net.sf.okapi.filters.properties.PropertiesFilter");
		mimeMap.put(DOCX_MIME_TYPE, "net.sf.okapi.filters.openxml.OpenXmlFilter");
		mimeMap.put(DOCM_MIME_TYPE, "net.sf.okapi.filters.openxml.OpenXmlFilter");
		mimeMap.put(XLSX_MIME_TYPE, "net.sf.okapi.filters.openxml.OpenXmlFilter");
		mimeMap.put(XLSM_MIME_TYPE, "net.sf.okapi.filters.openxml.OpenXmlFilter");
		mimeMap.put(PPTX_MIME_TYPE, "net.sf.okapi.filters.openxml.OpenXmlFilter");
		mimeMap.put(PPTM_MIME_TYPE, "net.sf.okapi.filters.openxml.OpenXmlFilter");
	}

	/**
	 * Adds a mapping to the manager. If a mapping for this MIME type exists
	 * already in the manager, it will be overridden by this new one.
	 * 
	 * @param mimeType
	 *            The MIME type identifier for this mapping.
	 * @param className
	 *            The class name of the filter to use.
	 */
	public static void addMapping(String mimeType, String className) {
		mimeMap.put(mimeType, className);
	}

	/**
	 * Creates the {@link IFilter} with its default configuration.
	 * 
	 * @param mimeType
	 *            The MIME type identifier for the filter to use now. If there
	 *            is no mapping for the given MIME type a null is returned.
	 */
	public static IFilter getDefaultFilter(String mimeType) {
		try {
			if (mimeType == null)
				return null;

			if ( mimeMap.get(mimeType) == null) {
				return null; // No mapping found
			}
			else { // Instantiate the default filter based on the class
				return (IFilter)Class.forName(mimeMap.get(mimeType)).newInstance();
			}
		} catch ( Exception e ) {
			RuntimeException re = new RuntimeException(e);
			LOGGER.log(Level.SEVERE, String.format("Could not instantiate %s for the MIME type %s.",
				mimeMap.get(mimeType), mimeType), re);
			throw re;
		}
	}

}
