package net.sf.okapi.common.resource;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filterwriter.IFilterWriter;

public class StartSubfilter extends StartGroup {
	// from sub document
	private String encoding;
	private boolean isMultilingual;
	private IParameters params;
	private IFilterWriter filterWriter;
	private boolean hasUTF8BOM;
	private String lineBreak;
	
	// new for sub filter
	private boolean useParentEncoder;

	/**
	 * Creates a new {@link StartSubfilter} object.
	 * @param parentId The identifier of the parent resource for this sub filter.
	 */
	public StartSubfilter(String parentId) {
		super(parentId);
	}

	/**
	 * Creates a new {@link StartSubfilter} object with the identifier of the group's parent
	 * and the group's identifier.
	 * @param parentId the identifier of the parent resource for this sub filter.
	 * @param id the identifier of this sub filter.
	 */
	public StartSubfilter(String parentId, String id)
	{
		super(parentId);
		this.id = id;
	}
	
	private LocaleId locale;
	/**
	 * @return the locale
	 */
	public LocaleId getLocale() {
		return locale;
	}

	/**
	 * @param locale the locale to set
	 */
	public void setLocale(LocaleId locale) {
		this.locale = locale;
	}

	/**
	 * @return the encoding
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * @param encoding the encoding to set
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * @return the isMultilingual
	 */
	public boolean isMultilingual() {
		return isMultilingual;
	}

	/**
	 * @param isMultilingual the isMultilingual to set
	 */
	public void setMultilingual(boolean isMultilingual) {
		this.isMultilingual = isMultilingual;
	}

	/**
	 * @return the params
	 */
	public IParameters getParams() {
		return params;
	}

	/**
	 * @param params the params to set
	 */
	public void setParams(IParameters params) {
		this.params = params;
	}

	/**
	 * @return the filterWriter
	 */
	public IFilterWriter getFilterWriter() {
		return filterWriter;
	}

	/**
	 * @param filterWriter the filterWriter to set
	 */
	public void setFilterWriter(IFilterWriter filterWriter) {
		this.filterWriter = filterWriter;
	}

	/**
	 * @return the hasUTF8BOM
	 */
	public boolean isHasUTF8BOM() {
		return hasUTF8BOM;
	}

	/**
	 * @param hasUTF8BOM the hasUTF8BOM to set
	 */
	public void setHasUTF8BOM(boolean hasUTF8BOM) {
		this.hasUTF8BOM = hasUTF8BOM;
	}

	/**
	 * @return the lineBreak
	 */
	public String getLineBreak() {
		return lineBreak;
	}

	/**
	 * @param lineBreak the lineBreak to set
	 */
	public void setLineBreak(String lineBreak) {
		this.lineBreak = lineBreak;
	}

	/**
	 * @return the useParentEncoder
	 */
	public boolean isUseParentEncoder() {
		return useParentEncoder;
	}

	/**
	 * @param useParentEncoder the useParentEncoder to set
	 */
	public void setUseParentEncoder(boolean useParentEncoder) {
		this.useParentEncoder = useParentEncoder;
	}	
}
