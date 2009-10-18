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

package net.sf.okapi.filters.abstractmarkup;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.CharacterEntityReference;
import net.htmlparser.jericho.CharacterReference;
import net.htmlparser.jericho.Config;
import net.htmlparser.jericho.EndTag;
import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.LoggerProvider;
import net.htmlparser.jericho.NumericCharacterReference;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagType;
import net.htmlparser.jericho.StreamedSource;
import net.htmlparser.jericho.Tag;

import net.sf.okapi.common.BOMNewlineEncodingDetector;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.exceptions.OkapiBadFilterInputException;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.filters.AbstractFilter;
import net.sf.okapi.common.filters.EventBuilder;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.PropertyTextUnitPlaceholder;
import net.sf.okapi.common.filters.PropertyTextUnitPlaceholder.PlaceholderType;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.filters.yaml.TaggedFilterConfiguration;
import net.sf.okapi.filters.yaml.TaggedFilterConfiguration.RULE_TYPE;

/**
 * Abstract class useful for creating an {@link IFilter} around the Jericho
 * parser. Jericho can parse non-wellformed HTML, XHTML, XML and various server
 * side scripting languages such as PHP, Mason, Perl (all configurable from
 * Jericho). AbstractMarkupFilter takes care of the parser initialization and
 * provides default handlers for each token type returned by the parser.
 * <p>
 * Handling of translatable text, inline tags, translatable and read-only
 * attributes are configurable through a user defined YAML file. See the Okapi
 * HtmlFilter with defaultConfiguration.yml and OpenXml filters for examples.
 * 
 */
public abstract class AbstractMarkupFilter extends AbstractFilter {
	private static final Logger LOGGER = Logger.getLogger(AbstractMarkupFilter.class.getName());

	private static final int PREVIEW_BYTE_COUNT = 1024;

	private StreamedSource document;
	private ExtractionRuleState ruleState;
	private Iterator<Segment> nodeIterator;
	private boolean hasUtf8Bom;
	private boolean hasUtf8Encoding;
	private boolean preserveWhitespace;
	private EventBuilder eventBuilder;

	static {
		Config.ConvertNonBreakingSpaces = false;
		Config.NewLine = BOMNewlineEncodingDetector.NewlineType.LF.toString();
		Config.LoggerProvider = LoggerProvider.JAVA;
	}

	/**
	 * Default constructor for {@link AbstractMarkupFilter} using default
	 * {@link EventBuilder}
	 */
	public AbstractMarkupFilter() {
		setEventBuilder(new EventBuilder());
		hasUtf8Bom = false;
		hasUtf8Encoding = false;
		preserveWhitespace = true;
		getEventBuilder().setPreserveWhitespace(preserveWhitespace);
	}

	/**
	 * Create a {@link AbstractMarkupFilter} with an {@link EventBuilder} for
	 * {@link AbstractMarkupFilter}
	 */
	public AbstractMarkupFilter(EventBuilder eventBuilder) {
		setEventBuilder(eventBuilder);
		hasUtf8Bom = false;
		hasUtf8Encoding = false;
		preserveWhitespace = true;
		getEventBuilder().setPreserveWhitespace(preserveWhitespace);
	}

	/**
	 * Get the current {@link ExtractionRuleState} object.
	 * 
	 * @return a {@link ExtractionRuleState}
	 */
	public ExtractionRuleState getRuleState() {
		return ruleState;
	}

	/**
	 * Sets the current {@link ExtractionRuleState}
	 * 
	 * @param ruleState
	 *            a {@link ExtractionRuleState}
	 */
	public void setRuleState(ExtractionRuleState ruleState) {
		this.ruleState = ruleState;
	}

	/**
	 * Get the current {@link TaggedFilterConfiguration}. A
	 * TaggedFilterConfiguration is the result of reading in a YAML
	 * configuration file and converting it into Java Objects.
	 * 
	 * @return a {@link TaggedFilterConfiguration}
	 */
	abstract protected TaggedFilterConfiguration getConfig();

	/**
	 * Close the filter and all used resources.
	 */
	public void close() {
		try {
			if (document != null) {
				document.close();
			}
		} catch (IOException e) {
			throw new OkapiIOException("Could not close " + getDocumentName(), e);
		}
		this.document = null; // help Java GC
		LOGGER.log(Level.FINE, getDocumentName() + " has been closed");
	}

	/*
	 * Get PREVIEW_BYTE_COUNT bytes so we can sniff out any encoding information
	 * in XML or HTML files
	 */
	private Source getParsedHeader(final InputStream inputStream) {
		try {
			inputStream.mark(PREVIEW_BYTE_COUNT);
			final byte[] bytes = new byte[PREVIEW_BYTE_COUNT];
			int i;
			for (i = 0; i < PREVIEW_BYTE_COUNT; i++) {
				final int nextByte = inputStream.read();
				if (nextByte == -1)
					break;
				bytes[i] = (byte) nextByte;
			}
			Source parsedInput = new Source(new ByteArrayInputStream(bytes, 0, i));
			return parsedInput;
		} catch (IOException e) {
			throw new OkapiIOException("Could not reset the input stream to it's start position", e);
		} finally {
			try {
				inputStream.reset();
			} catch (IOException e) {

			}
		}
	}

	/**
	 * Start a new {@link IFilter} using the supplied {@link RawDocument}.
	 * 
	 * @param input
	 *            - input to the {@link IFilter} (can be a {@link CharSequence},
	 *            {@link URI} or {@link InputStream})
	 */
	public void open(RawDocument input) {
		open(input, true);
		LOGGER.log(Level.FINE, getName() + " has opened an input document");
	}

	/**
	 * Start a new {@link IFilter} using the supplied {@link RawDocument}.
	 * 
	 * @param input
	 *            - input to the {@link IFilter} (can be a {@link CharSequence},
	 *            {@link URI} or {@link InputStream})
	 * @param generateSkeleton
	 *            - true if the {@link IFilter} should store non-translatble
	 *            blocks (aka skeleton), false otherwise.
	 * 
	 * @throws OkapiBadFilterInputException
	 * @throws OkapiIOException
	 */
	public void open(RawDocument input, boolean generateSkeleton) {
		// close StreamedSource from previous run
		if (document != null) {
			close();
		}

		if (input.getInputURI() != null) {
			// YS Sep-27-09: change from tostring() to getPath()
			// to match other filters. But toString() may be better ultimately
			setDocumentName(input.getInputURI().getPath());
		}

		BOMNewlineEncodingDetector detector = new BOMNewlineEncodingDetector(input.getStream(), input.getEncoding());
		detector.detectBom();

		setEncoding(detector.getEncoding());
		hasUtf8Bom = detector.hasUtf8Bom();
		hasUtf8Encoding = detector.hasUtf8Encoding();
		setNewlineType(detector.getNewlineType().toString());

		Source parsedHeader = getParsedHeader(input.getStream());
		String detectedEncoding = parsedHeader.getDocumentSpecifiedEncoding();

		if (detectedEncoding == null && getEncoding() != null) {
			detectedEncoding = getEncoding();
			LOGGER.log(Level.FINE, String.format("Cannot auto-detect encoding. Using the default encoding (%s)",
					getEncoding()));
		} else if (getEncoding() == null) {
			detectedEncoding = parsedHeader.getEncoding(); // get best guess
			LOGGER.log(Level.FINE, String.format(
					"Default encoding and detected encoding not found. Using best guess encoding (%s)",
					detectedEncoding));
		}

		try {
			input.setEncoding(detectedEncoding);
			setOptions(input.getSourceLanguage(), input.getTargetLanguage(), detectedEncoding, generateSkeleton);
			document = new StreamedSource(input.getReader());
		} catch (IOException e) {
			throw new OkapiIOException("Filter could not open input stream", e);
		}

		startFilter();
	}

	public boolean hasNext() {
		return getEventBuilder().hasNext();
	}

	/**
	 * Queue up Jericho tokens until we can buld an Okapi {@link Event} and
	 * return it.
	 */
	public Event next() {
		// reset state flags and buffers
		ruleState.reset();

		while (getEventBuilder().hasQueuedEvents()) {
			return getEventBuilder().next();
		}

		while (nodeIterator.hasNext() && !isCanceled()) {
			Segment segment = nodeIterator.next();

			preProcess(segment);

			if (segment instanceof Tag) {
				final Tag tag = (Tag) segment;

				// We just hit a tag that could close the current TextUnit, but
				// only if it was not opened with a TextUnit tag (i.e., complex
				// TextUnits such as <p> etc.)
				boolean inlineTag = false;
				if (getEventBuilder().isInsideTextRun()
						&& (getConfig().getMainRuleType(tag.getName()) == RULE_TYPE.INLINE_ELEMENT
								|| tag.getTagType() == StartTagType.COMMENT || tag.getTagType() == StartTagType.XML_PROCESSING_INSTRUCTION))
					inlineTag = true;

				if (getEventBuilder().isCurrentTextUnit() && !getEventBuilder().isCurrentComplexTextUnit()
						&& !inlineTag) {
					getEventBuilder().endTextUnit();
				}

				if (tag.getTagType() == StartTagType.NORMAL || tag.getTagType() == StartTagType.UNREGISTERED) {
					handleStartTag((StartTag) tag);
				} else if (tag.getTagType() == EndTagType.NORMAL || tag.getTagType() == EndTagType.UNREGISTERED) {
					handleEndTag((EndTag) tag);
				} else if (tag.getTagType() == StartTagType.DOCTYPE_DECLARATION) {
					handleDocTypeDeclaration(tag);
				} else if (tag.getTagType() == StartTagType.CDATA_SECTION) {
					handleCdataSection(tag);
				} else if (tag.getTagType() == StartTagType.COMMENT) {
					handleComment(tag);
				} else if (tag.getTagType() == StartTagType.XML_DECLARATION) {
					handleXmlDeclaration(tag);
				} else if (tag.getTagType() == StartTagType.XML_PROCESSING_INSTRUCTION) {
					handleProcessingInstruction(tag);
				} else if (tag.getTagType() == StartTagType.MARKUP_DECLARATION) {
					handleMarkupDeclaration(tag);
				} else if (tag.getTagType() == StartTagType.SERVER_COMMON) {
					handleServerCommon(tag);
				} else if (tag.getTagType() == StartTagType.SERVER_COMMON_ESCAPED) {
					handleServerCommonEscaped(tag);
				} else { // not classified explicitly by Jericho
					if (tag instanceof StartTag) {
						handleStartTag((StartTag) tag);
					} else if (tag instanceof EndTag) {
						handleEndTag((EndTag) tag);
					} else {
						handleDocumentPart(tag);
					}
				}
			} else if (segment instanceof CharacterEntityReference) {
				handleCharacterEntity(segment);
			} else if (segment instanceof NumericCharacterReference) {
				handleNumericEntity(segment);
			} else {
				// last resort is pure text node
				handleText(segment);
			}

			if (getEventBuilder().hasQueuedEvents()) {
				break;
			}
		}

		if (!nodeIterator.hasNext()) {
			endFilter(); // we are done
		}

		// return one of the waiting events
		return getEventBuilder().next();
	}

	/**
	 * Initialize the filter for every input and send the {@link StartDocument}
	 * {@link Event}
	 */
	protected void startFilter() {
		getEventBuilder().reset();
		getEventBuilder().addFilterEvent(createStartDocumentEvent());

		// Segment iterator
		ruleState = new ExtractionRuleState();

		// This optimizes memory at the expense of performance
		nodeIterator = document.iterator();
	}

	/**
	 * End the current filter processing and send the {@link Ending}
	 * {@link Event}
	 */
	protected void endFilter() {
		getEventBuilder().flushRemainingEvents();
		getEventBuilder().addFilterEvent(createEndDocumentEvent());
	}

	/**
	 * Do any handling needed before the current Segment is processed. Default
	 * is to do nothing.
	 * 
	 * @param segment
	 */
	protected void preProcess(Segment segment) {
	}

	/**
	 * Do any required post-processing on the TextUnit before the {@link Event}
	 * leaves the {@link IFilter}. Default implementation leaves Event
	 * unchanged. Override this method if you need to do format specific handing
	 * such as collapsing whitespace.
	 */
	protected void postProcessTextUnit(TextUnit textUnit) {
	}

	/**
	 * Handle any recognized escaped server tags.
	 * 
	 * @param tag
	 */
	protected abstract void handleServerCommonEscaped(Tag tag);

	/**
	 * Handle any recognized server tags (i.e., PHP, Mason etc.)
	 * 
	 * @param tag
	 */
	protected abstract void handleServerCommon(Tag tag);

	/**
	 * Handle an XML markup declaration.
	 * 
	 * @param tag
	 */
	protected abstract void handleMarkupDeclaration(Tag tag);

	/**
	 * Handle an XML declaration.
	 * 
	 * @param tag
	 */
	protected abstract void handleXmlDeclaration(Tag tag);

	/**
	 * Handle the XML doc type declaration (DTD).
	 * 
	 * @param tag
	 */
	protected abstract void handleDocTypeDeclaration(Tag tag);

	/**
	 * Handle processing instructions.
	 * 
	 * @param tag
	 */
	protected abstract void handleProcessingInstruction(Tag tag);

	/**
	 * Handle comments.
	 * 
	 * @param tag
	 */
	protected abstract void handleComment(Tag tag);

	/**
	 * Handle CDATA sections.
	 * 
	 * @param tag
	 */
	protected abstract void handleCdataSection(Tag tag);

	/**
	 * Handle all text (PCDATA).
	 * 
	 * @param text
	 */
	protected abstract void handleText(Segment text);

	/**
	 * Handle all Character entities. Default implementation converts entity to
	 * Unicode character.
	 * 
	 * @param entity
	 *            - the character entity
	 */
	protected void handleNumericEntity(Segment entity) {
		String decodedText = CharacterReference.decode(entity.toString(), false);
		if (!getEventBuilder().isCurrentTextUnit()) {
			getEventBuilder().startTextUnit();
		}
		getEventBuilder().addToTextUnit(decodedText);
	}

	/**
	 * Handle all numeric entities. Default implementation converts entity to
	 * Unicode character.
	 * 
	 * @param entity
	 *            - the numeric entity
	 */
	protected void handleCharacterEntity(Segment entity) {
		String decodedText = CharacterReference.decode(entity.toString(), false);
		if (!getEventBuilder().isCurrentTextUnit()) {
			getEventBuilder().startTextUnit();
		}
		getEventBuilder().addToTextUnit(decodedText);
	}

	/**
	 * Handle start tags.
	 * 
	 * @param startTag
	 */
	protected abstract void handleStartTag(StartTag startTag);

	/**
	 * Handle end tags, including empty tags.
	 * 
	 * @param endTag
	 */
	protected abstract void handleEndTag(EndTag endTag);

	/**
	 * Handle anything else not classified by Jericho.
	 * 
	 * @param tag
	 */
	protected abstract void handleDocumentPart(Tag tag);

	/**
	 * Some attributes names are converted to Okapi standards such as HTML
	 * charset to "encoding" and lang to "language"
	 * 
	 * @param attrName
	 *            - the attribute name
	 * @param attrValue
	 *            - the attribute value
	 * @param tag
	 *            - the Jericho {@link Tag} that contains the attribute
	 * @return the attribute name after it as passe through the normalization
	 *         rules
	 */
	abstract protected String normalizeAttributeName(String attrName, String attrValue, Tag tag);

	/**
	 * Add an {@link Code} to the current {@link TextUnit}. Throws an exception
	 * if there is no current {@link TextUnit}.
	 * 
	 * @param tag
	 *            - the Jericho {@link Tag} that is converted to a Okpai
	 *            {@link Code}
	 */
	protected void addCodeToCurrentTextUnit(Tag tag) {
		List<PropertyTextUnitPlaceholder> propertyTextUnitPlaceholders;
		String literalTag = tag.toString();
		TextFragment.TagType codeType;

		// start tag or empty tag
		if (tag.getTagType() == StartTagType.NORMAL || tag.getTagType() == StartTagType.UNREGISTERED) {
			StartTag startTag = ((StartTag) tag);

			// is this an empty tag?
			if (startTag.isSyntacticalEmptyElementTag())
				codeType = TextFragment.TagType.PLACEHOLDER;
			else
				codeType = TextFragment.TagType.OPENING;

			// create a list of Property or Text placeholders for this tag
			// If this list is empty we know that there are no attributes that
			// need special processing
			propertyTextUnitPlaceholders = null;

			propertyTextUnitPlaceholders = createPropertyTextUnitPlaceholders(startTag);
			if (propertyTextUnitPlaceholders != null && !propertyTextUnitPlaceholders.isEmpty()) {
				// add code and process actionable attributes
				getEventBuilder().addToTextUnit(new Code(codeType, startTag.getName(), literalTag),
//TOFIX: get the proper parameter		
//was:		getConfig().getElementType(tag),
						getTrgLoc(),
//end of TOFIX						
						propertyTextUnitPlaceholders);
			} else {
				// no actionable attributes, just add the code as-is
				getEventBuilder().addToTextUnit(new Code(codeType, getConfig().getElementType(tag), literalTag));
			}
		} else { // end or unknown tag
			if (tag.getTagType() == EndTagType.NORMAL || tag.getTagType() == EndTagType.UNREGISTERED) {
				codeType = TextFragment.TagType.CLOSING;
			} else {
				codeType = TextFragment.TagType.PLACEHOLDER;
			}
			getEventBuilder().addToTextUnit(new Code(codeType, getConfig().getElementType(tag), literalTag));
		}
	}

	/**
	 * For the given Jericho {@link StartTag} parse out all the actionable
	 * attributes and and store them as {@link PropertyTextUnitPlaceholder}.
	 * {@link PlaceholderType} are set based on the filter configuration for
	 * each attribute. for the attribute name and value.
	 * 
	 * @param startTag
	 *            - Jericho {@link StartTag}
	 * @return all actionable (translatable, writable or read-only) attributes
	 *         found in the {@link StartTag}
	 */
	protected List<PropertyTextUnitPlaceholder> createPropertyTextUnitPlaceholders(StartTag startTag) {
		// list to hold the properties or TextUnits
		List<PropertyTextUnitPlaceholder> propertyOrTextUnitPlaceholders = new LinkedList<PropertyTextUnitPlaceholder>();

		// convert Jericho attributes to HashMap
		Map<String, String> attrs = startTag.getAttributes().populateMap(new HashMap<String, String>(), true);
		for (Attribute attribute : startTag.parseAttributes()) {
			if (getConfig().isTranslatableAttribute(startTag.getName(), attribute.getName(), attrs)) {
				propertyOrTextUnitPlaceholders.add(createPropertyTextUnitPlaceholder(PlaceholderType.TRANSLATABLE,
						attribute.getName(), attribute.getValue(), startTag, attribute));
			} else {

				if (getConfig().isReadOnlyLocalizableAttribute(startTag.getName(), attribute.getName(), attrs)) {
					propertyOrTextUnitPlaceholders.add(createPropertyTextUnitPlaceholder(
							PlaceholderType.READ_ONLY_PROPERTY, attribute.getName(), attribute.getValue(), startTag,
							attribute));
				} else if (getConfig().isWritableLocalizableAttribute(startTag.getName(), attribute.getName(), attrs)) {
					propertyOrTextUnitPlaceholders.add(createPropertyTextUnitPlaceholder(
							PlaceholderType.WRITABLE_PROPERTY, attribute.getName(), attribute.getValue(), startTag,
							attribute));
				}
			}
		}

		return propertyOrTextUnitPlaceholders;
	}

	/**
	 * Create a {@link PropertyTextUnitPlaceholder} given the supplied type,
	 * name and Jericho {@link Tag} and {@link Attribute}.
	 * 
	 * @param type
	 *            - {@link PlaceholderType} is one of TRANSLATABLE,
	 *            READ_ONLY_PROPERTY, WRITABLE_PROPERTY
	 * @param name
	 *            - attribute name
	 * @param value
	 *            - attribute value
	 * @param tag
	 *            - Jericho {@link Tag} which contains the attribute
	 * @param attribute
	 *            - attribute as a Jericho {@link Attribute}
	 * @return a {@link PropertyTextUnitPlaceholder} representing the attribute
	 */
	protected PropertyTextUnitPlaceholder createPropertyTextUnitPlaceholder(PlaceholderType type, String name,
			String value, Tag tag, Attribute attribute) {
		// offset of attribute
		int mainStartPos = attribute.getBegin() - tag.getBegin();
		int mainEndPos = attribute.getEnd() - tag.getBegin();

		// offset of value of the attribute
		int valueStartPos = attribute.getValueSegment().getBegin() - tag.getBegin();
		int valueEndPos = attribute.getValueSegment().getEnd() - tag.getBegin();

		return new PropertyTextUnitPlaceholder(type, normalizeAttributeName(name, value, tag), value, mainStartPos,
				mainEndPos, valueStartPos, valueEndPos);
	}

	/**
	 * Is the input encoded as UTF-8?
	 * 
	 * @return true if the document is in utf8 encoding.
	 */
	@Override
	protected boolean isUtf8Encoding() {
		return hasUtf8Encoding;
	}

	/**
	 * Does the input have a UTF-8 Byte Order Mark?
	 * 
	 * @return true if the document has a utf-8 byte order mark.
	 */
	@Override
	protected boolean isUtf8Bom() {
		return hasUtf8Bom;
	}

	/**
	 * 
	 * @return the preserveWhitespace boolean.
	 */
	protected boolean isPreserveWhitespace() {
		return preserveWhitespace;
	}

	protected void setPreserveWhitespace(boolean preserveWhitespace) {
		this.preserveWhitespace = preserveWhitespace;
		getEventBuilder().setPreserveWhitespace(preserveWhitespace);
	}

	protected void addToDocumentPart(String part) {
		getEventBuilder().addToDocumentPart(part);
	}

	protected void addToTextUnit(String text) {
		getEventBuilder().addToTextUnit(text);
	}

	protected void startTextUnit(String text) {
		getEventBuilder().startTextUnit(text);
	}

	protected boolean canStartNewTextUnit() {
		return getEventBuilder().canStartNewTextUnit();
	}

	protected boolean isInsideTextRun() {
		return getEventBuilder().isInsideTextRun();
	}

	protected void addToTextUnit(Code code) {
		getEventBuilder().addToTextUnit(code);
	}

	protected void addToTextUnit(Code code, 
			List<PropertyTextUnitPlaceholder> propertyTextUnitPlaceholders) {
		getEventBuilder().addToTextUnit(code, propertyTextUnitPlaceholders);
	}

	protected void endDocumentPart() {
		getEventBuilder().endDocumentPart();
	}

	protected void startDocumentPart(String part, String name,
			List<PropertyTextUnitPlaceholder> propertyTextUnitPlaceholders) {
		getEventBuilder().startDocumentPart(part, name, propertyTextUnitPlaceholders);
	}

	protected void startGroup(GenericSkeleton startMarker, String commonTagType) {
		getEventBuilder().startGroup(startMarker, commonTagType);
	}

	protected void startGroup(GenericSkeleton startMarker,
		String commonTagType,
		LocaleId locale,
		List<PropertyTextUnitPlaceholder> propertyTextUnitPlaceholders)
	{
		getEventBuilder().startGroup(startMarker, commonTagType, locale, propertyTextUnitPlaceholders);
	}

	protected void startTextUnit(GenericSkeleton startMarker) {
		getEventBuilder().startTextUnit(startMarker);
	}

	protected void startTextUnit(GenericSkeleton startMarker,
			List<PropertyTextUnitPlaceholder> propertyTextUnitPlaceholders) {
		getEventBuilder().startTextUnit(startMarker, propertyTextUnitPlaceholders);
	}

	protected void endTextUnit(GenericSkeleton endMarker) {
		getEventBuilder().endTextUnit(endMarker);
	}

	protected void endGroup(GenericSkeleton endMarker) {
		getEventBuilder().endGroup(endMarker);
	}

	protected void startTextUnit() {
		getEventBuilder().startTextUnit();
	}
	
	protected int getTextUnitId() {
		return getEventBuilder().getTextUnitId();
	}

	protected void setTextUnitId(int id) {
		getEventBuilder().setTextUnitId(id);
	}

	protected int getDocumentPartId() {
		return getEventBuilder().getDocumentPartId();
	}

	protected void setDocumentPartId(int id) {
		getEventBuilder().setDocumentPartId(id);
	}
	
	protected void appendToFirstSkeletonPart(String text)
	{
		getEventBuilder().appendToFirstSkeletonPart(text);
	}

	protected void addFilterEvent(Event event) {
		getEventBuilder().addFilterEvent(event);
	}
	
	/**
	 * @param eventBuilder the eventBuilder to set
	 */
	public void setEventBuilder(EventBuilder eventBuilder) {
		this.eventBuilder = eventBuilder;
	}

	/**
	 * @return the eventBuilder
	 */
	public EventBuilder getEventBuilder() {
		return eventBuilder;
	}
	
	/**
	 * Sets the input document mime type.
	 * 
	 * @param mimeType
	 *            the new mime type
	 */
	@Override
	public void setMimeType(String mimeType) {
		super.setMimeType(mimeType);
		eventBuilder.setMimeType(mimeType);
	}
}
