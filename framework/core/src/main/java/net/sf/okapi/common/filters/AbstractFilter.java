package net.sf.okapi.common.filters;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.BOMNewlineEncodingDetector.NewlineType;
import net.sf.okapi.common.filterwriter.GenericFilterWriter;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

public abstract class AbstractFilter implements IFilter {
	private static final Logger LOGGER = Logger.getLogger(AbstractFilter.class.getName());

	private static final String START_DOCUMENT = "sd"; //$NON-NLS-1$
	private static final String END_DOCUMENT = "ed"; //$NON-NLS-1$

	List<FilterConfiguration> configList = new ArrayList<FilterConfiguration>();
	private int documentId = 0;
	private boolean canceled = false;
	private String documentName;
	private String newlineType;
	private String encoding;
	private String srcLang;
	private String trgLang;
	private String mimeType;
	private IFilterWriter filterWriter;
	private boolean generateSkeleton;
	private boolean multilingual;
	private String name;
	private String description;

	/**
	 * Default constructor
	 */
	public AbstractFilter() {
		// defaults
		setNewlineType("\n"); //$NON-NLS-1$
		setMultilingual(false);
	}

	/**
	 * Each {@link IFilter} has a small set of options beyond normal
	 * configuration that gives the {@link IFilter} the needed information to
	 * properly parse the content.
	 * 
	 * @param sourceLanguage
	 *            - source language of the input document
	 * @param targetLanguage
	 *            - target language if the input document is multilingual.
	 * @param defaultEncoding
	 *            - assumed encoding of the input document. May be overriden if
	 *            a different encoding is detected.
	 * @param generateSkeleton
	 *            - store skeleton (non-translatable parts of the document)
	 *            along with the extracted text.
	 */
	public void setOptions(String sourceLanguage, String targetLanguage, String defaultEncoding,
			boolean generateSkeleton) {
		setEncoding(defaultEncoding);
		setTrgLang(targetLanguage);
		setSrcLang(sourceLanguage);
		setGenerateSkeleton(generateSkeleton);
	}

	/**
	 * create a START_DOCUMENT {@link Event}
	 */
	protected Event createStartDocumentEvent() {
		StartDocument startDocument = new StartDocument(createId(START_DOCUMENT, ++documentId));
		startDocument.setEncoding(getEncoding(), isUtf8Encoding() && isUtf8Bom());
		startDocument.setLanguage(getSrcLang());
		startDocument.setMimeType(getMimeType());
		startDocument.setLineBreak(getNewlineType());
		startDocument.setFilterParameters(getParameters());
		startDocument.setFilterWriter(getFilterWriter());
		startDocument.setName(getDocumentName());
		startDocument.setMultilingual(isMultilingual());
		LOGGER.log(Level.FINE, "Start Document for " + startDocument.getId()); //$NON-NLS-1$
		return new Event(EventType.START_DOCUMENT, startDocument);
	}

	/**
	 * create a END_DOCUMENT {@link Event}
	 */
	protected Event createEndDocumentEvent() {
		Ending endDocument = new Ending(createId(END_DOCUMENT, ++documentId));
		LOGGER.log(Level.FINE, "End Document for " + endDocument.getId()); //$NON-NLS-1$
		return new Event(EventType.END_DOCUMENT, endDocument);
	}

	public boolean addConfigurations(List<FilterConfiguration> configs) {
		if (configList == null)
			return false;

		return configList.addAll(configs);
	}

	public FilterConfiguration getConfiguration(String configId) {
		if (Util.isEmpty(configList))
			return null;

		for (FilterConfiguration config : configList) {

			if (config == null)
				continue;
			if (config.configId.equalsIgnoreCase(configId))
				return config;
		}

		return null;
	}

	public boolean removeConfiguration(String configId) {
		return configList.remove(getConfiguration(configId));
	}

	public List<FilterConfiguration> getConfigurations() {
		List<FilterConfiguration> configs = new ArrayList<FilterConfiguration>();

		for (FilterConfiguration fc : configList)
			configs.add(new FilterConfiguration(fc.configId, getMimeType(), getClass().getName(), fc.name, fc.description,
					fc.parametersLocation));

		return configs;
	}

	public void addConfiguration(FilterConfiguration configuration) {
		configList.add(configuration);
	}

	public void cancel() {
		canceled = true;
	}

	/*
	 * Create a formatted ID for named resources.
	 */
	private String createId(String name, int number) {
		return String.format("%s%d", name, number); //$NON-NLS-1$
	}

	/**
	 * Allows implementers to set the START_DOCUMENT name for the current input.
	 * 
	 * @param documentName
	 *            the input document name or path
	 */
	protected void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	/**
	 * Gets the START_DOCUMENT name for the current input.
	 * 
	 * @return the document name or path of the current input.
	 */
	public String getDocumentName() {
		return documentName;
	}

	/**
	 * Get the newline type used in the input.
	 * 
	 * @return the {@link NewlineType} one of '\n', '\r' or '\r\n'
	 */
	public String getNewlineType() {
		return newlineType;
	}

	/**
	 * Sets the newline type.
	 * 
	 * @param newlineType
	 *            one of '\n', '\r' or '\r\n'.
	 */
	protected void setNewlineType(String newlineType) {
		this.newlineType = newlineType;
	}

	/**
	 * Gets the input document encoding.
	 * 
	 * @return the encoding
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * Sets the input document encoding.
	 * 
	 * @param encoding
	 *            the new encoding
	 */
	protected void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * Gets the input document source language.
	 * 
	 * @return the src lang
	 */
	public String getSrcLang() {
		return srcLang;
	}

	/**
	 * Sets the input document source language.
	 * 
	 * @param srcLang
	 *            the new src lang
	 */
	protected void setSrcLang(String srcLang) {
		this.srcLang = srcLang;
	}

	/**
	 * @param trgLang
	 *            the trgLang to set
	 */
	protected void setTrgLang(String trgLang) {
		this.trgLang = trgLang;
	}

	/**
	 * @return the trgLang
	 */
	public String getTrgLang() {
		return trgLang;
	}

	/**
	 * Gets the input document mime type.
	 * 
	 * @return the mime type
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * Sets the input document mime type.
	 * 
	 * @param mimeType
	 *            the new mime type
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	/**
	 * Checks if the {@link IFilter} has been canceled.
	 * 
	 * @return true, if is canceled
	 */
	public boolean isCanceled() {
		return canceled;
	}

	/**
	 * Gets the filter writer for this filter.
	 * 
	 * @return the filter writer.
	 */
	public IFilterWriter getFilterWriter() {
		return filterWriter;
	}

	/**
	 * Sets the filter writer for this filter.
	 * 
	 * @param filterWriter
	 *            the filter writer to set.
	 */
	public void setFilterWriter(IFilterWriter filterWriter) {
		this.filterWriter = filterWriter;
	}

	public ISkeletonWriter createSkeletonWriter() {
		return new GenericSkeletonWriter();
	}

	public IFilterWriter createFilterWriter() {
		return new GenericFilterWriter(createSkeletonWriter());
	}

	/**
	 * @param generateSkeleton
	 *            the generateSkeleton to set
	 */
	protected void setGenerateSkeleton(boolean generateSkeleton) {
		this.generateSkeleton = generateSkeleton;
	}

	/**
	 * @return the generateSkeleton
	 */
	public boolean isGenerateSkeleton() {
		return generateSkeleton;
	}

	/**
	 * Is the input encoded as UTF-8?
	 * 
	 * @return true if the document is in utf8 encoding.
	 */
	abstract protected boolean isUtf8Encoding();

	/**
	 * Does the input have a UTF-8 Byte Order Mark?
	 * 
	 * @return true if the document has a utf-8 byte order mark.
	 */
	abstract protected boolean isUtf8Bom();

	/**
	 * @param multilingual
	 *            the multilingual to set
	 */
	protected void setMultilingual(boolean multilingual) {
		this.multilingual = multilingual;
	}

	/**
	 * @return the multilingual
	 */
	public boolean isMultilingual() {
		return multilingual;
	}

	protected void setName(String name) {

		this.name = name;
	}

	public String getName() {
		return name;
	}

	protected void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		// return this.getClass().getName();
		return description;
	}
}
