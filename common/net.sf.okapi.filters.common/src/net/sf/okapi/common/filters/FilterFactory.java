package net.sf.okapi.common.filters;

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.okapi.common.IParameters;
import static net.sf.okapi.common.MimeTypeMapper.*;

public final class FilterFactory {

	private static final Logger LOGGER = Logger.getLogger(FilterFactory.class.getName());

	private static final Hashtable<String, String> mimeMap;

	/**
	 * Creates a new filter manager, with default pre-defined filters loaded.
	 */
	static {
		mimeMap = new Hashtable<String, String>();
		mimeMap.put(XML_MIME_TYPE, "net.sf.okapi.common.filters.xml.XMLFilter");
		mimeMap.put(ODF_MIME_TYPE, "net.sf.okapi.common.filters.openoffice.ODFFilter");
		mimeMap.put(HTML_MIME_TYPE, "net.sf.okapi.common.filters.html.HtmlFilter");
		mimeMap.put(PO_MIME_TYPE, "net.sf.okapi.common.filters.po.POFilter");
		mimeMap.put(RTF_MIME_TYPE, "net.sf.okapi.common.filters.rtf.RTFFilter");
		mimeMap.put(JAVASCRIPT_MIME_TYPE, "net.sf.okapi.common.filters.javascript.JavascriptFilter");
		mimeMap.put(CSV_MIME_TYPE, "net.sf.okapi.common.filters.csv.CsvFilter");
		mimeMap.put(MIF_MIME_TYPE, "net.sf.okapi.common.filters.mif.MIFFilter");
		mimeMap.put(PLAIN_TEXT_MIME_TYPE, "net.sf.okapi.common.filters.plaintext.PlainTextFilter");
		mimeMap.put(QUARK_MIME_TYPE, "net.sf.okapi.common.filters.quark.QuarkFilter");
		mimeMap.put(JAVA_PROPERTIES_MIME_TYPE, "net.sf.okapi.common.filters.properties.PropertiesFilter");
		mimeMap.put(SERVER_SIDE_INCLUDE_MIME_TYPE, "net.sf.okapi.common.filters.html.HtmlFilter");
		mimeMap.put(DOCX_MIME_TYPE, "net.sf.okapi.common.filters.openxml.OpenXmlFilter");
		mimeMap.put(DOCM_MIME_TYPE, "net.sf.okapi.common.filters.openxml.OpenXmlFilter");
		mimeMap.put(XLSX_MIME_TYPE, "net.sf.okapi.common.filters.openxml.OpenXmlFilter");
		mimeMap.put(XLSM_MIME_TYPE, "net.sf.okapi.common.filters.openxml.OpenXmlFilter");
		mimeMap.put(PPTX_MIME_TYPE, "net.sf.okapi.common.filters.openxml.OpenXmlFilter");
		mimeMap.put(PPTM_MIME_TYPE, "net.sf.okapi.common.filters.openxml.OpenXmlFilter");
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
	public static void addMapping(String mimeType, String className, IParameters configuration) {
		mimeMap.put(mimeType, className);
	}

	/**
	 * Creates the {@link IFilter} with its default configuration.
	 * 
	 * @param mimeType
	 *            The MIME type identifier for the encoder to use now. If there
	 *            is no mapping for the given MIME type, the cache is cleared
	 *            and no encoder is active.
	 */
	public static IFilter getDefaultFilter(String mimeType) {
		try {
			if (mimeType == null)
				return null;

			if (mimeMap.get(mimeType) != null) { // Not in the map: Use the
				// default one.
				return null;
			} else {
				// Instantiate the default filter based on the class
				return (IFilter) Class.forName(mimeMap.get(mimeType)).newInstance();
			}
		} catch (Exception e) {
			RuntimeException re = new RuntimeException(e);
			LOGGER.log(Level.SEVERE, "Could not create the filter for mime type: " + mimeType, re);
			throw re;
		}
	}
}
