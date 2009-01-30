package net.sf.okapi.filters.markupfilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.EndTag;
import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagType;
import net.htmlparser.jericho.Tag;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.filters.BaseFilter;
import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.PropertyTextUnitPlaceholder;
import net.sf.okapi.common.filters.PropertyTextUnitPlaceholder.PlaceholderType;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.filters.yaml.TaggedFilterConfiguration;
import net.sf.okapi.filters.yaml.TaggedFilterConfiguration.RULE_TYPE;

public abstract class BaseMarkupFilter extends BaseFilter {
	private Source document;
	private ExtractionRuleState ruleState;
	private Parameters parameters;
	private Iterator<Segment> nodeIterator;
	private String defaultConfig;

	public BaseMarkupFilter() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.filters.IFilter#getParameters()
	 */
	public IParameters getParameters() {
		return parameters;
	}

	public ExtractionRuleState getRuleState() {
		return ruleState;
	}

	public void setRuleState(ExtractionRuleState ruleState) {
		this.ruleState = ruleState;
	}

	public TaggedFilterConfiguration getConfig() {
		return parameters.getTaggedConfig();
	}

	public void setOptions(String language, String defaultEncoding, boolean generateSkeleton) {
		setOptions(language, null, defaultEncoding, generateSkeleton);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.okapi.common.filters.IFilter#setParameters(net.sf.okapi.common
	 * .IParameters)
	 */
	public void setParameters(IParameters params) {
		this.parameters = (Parameters) params;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.filters.IFilter#setOptions(java.lang.String,
	 * java.lang.String, java.lang.String, boolean)
	 */
	public void setOptions(String sourceLanguage, String targetLanguage, String defaultEncoding,
			boolean generateSkeleton) {
		setEncoding(defaultEncoding);
		setSrcLang(sourceLanguage);
	}

	public void close() {
	}

	public void open(CharSequence input) {
		document = new Source(input);
		initialize();
	}

	public void open(InputStream input) {
		try {
			if (getEncoding() != null) {
				BufferedReader r = new BufferedReader(new InputStreamReader(input, getEncoding()));
				document = new Source(r);
			} else {
				// try to guess encoding
				document = new Source(input);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		initialize();
	}

	public void open(URL input) {
		try {
			if (getEncoding() != null) {
				BufferedReader r = new BufferedReader(new InputStreamReader(input.openStream(), getEncoding()));
				document = new Source(r);
			} else {
				// try to guess encoding
				document = new Source(input);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		initialize();
	}

	@Override
	protected void initialize() {
		super.initialize();

		if (parameters == null) {
			parameters = new Parameters(defaultConfig);			
		}

		// Segment iterator
		ruleState = new ExtractionRuleState();
		document.fullSequentialParse();
		nodeIterator = document.getNodeIterator();
	}

	public void setDefaultConfig(String defaultConfig) {
		this.defaultConfig = defaultConfig;
	}

	@Override
	public FilterEvent next() {
		// reset state flags and buffers
		ruleState.reset();

		while (hasQueuedEvents()) {
			return super.next();
		}

		while (nodeIterator.hasNext() && !isCanceled()) {
			Segment segment = nodeIterator.next();

			if (segment instanceof Tag) {
				final Tag tag = (Tag) segment;

				// We just hit a tag that could close the current TextUnit, but
				// only if it was not opened with a TextUnit tag (i.e., complex
				// TextUnits such as <p> etc.)
				boolean inlineTag = false;
				if (getConfig().getMainRuleType(tag.getName()) == RULE_TYPE.INLINE_ELEMENT)
					inlineTag = true;
				if (isCurrentTextUnit() && !isCurrentComplexTextUnit() && !inlineTag) {
					endTextUnit();
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
			} else {
				handleText(segment);
			}

			if (hasQueuedEvents()) {
				break;
			}
		}

		if (!nodeIterator.hasNext()) {
			finalize(); // we are done
		}

		// return one of the waiting events
		return super.next();
	}

	protected abstract void handleServerCommonEscaped(Tag tag);

	protected abstract void handleServerCommon(Tag tag);

	protected abstract void handleMarkupDeclaration(Tag tag);

	protected abstract void handleXmlDeclaration(Tag tag);

	protected abstract void handleDocTypeDeclaration(Tag tag);

	protected abstract void handleProcessingInstruction(Tag tag);

	protected abstract void handleComment(Tag tag);

	protected abstract void handleCdataSection(Tag tag);

	protected abstract void handleText(Segment text);

	protected abstract void handleStartTag(StartTag startTag);

	protected abstract void handleEndTag(EndTag endTag);

	protected abstract void handleDocumentPart(Tag endTag);
	
	abstract protected String normalizeAttributeName(String attrName, String attrValue, Tag tag);

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
			// quick test so we can skip createPropertyTextUnitPlaceholders if
			// there are no actionable attributes
			if (getConfig().hasActionableAttributes(startTag.getName())) {
				propertyTextUnitPlaceholders = createPropertyTextUnitPlaceholders(startTag);
			}

			if (propertyTextUnitPlaceholders != null && !propertyTextUnitPlaceholders.isEmpty()) {
				// add code and process actionable attributes
				addToTextUnit(codeType, literalTag, startTag.getName(), propertyTextUnitPlaceholders);
			} else {
				// no actionable attributes, just add the code as-is
				addToTextUnit(codeType, literalTag, startTag.getName());
			}
		} else { // end or unknown tag
			if (tag.getTagType() == EndTagType.NORMAL || tag.getTagType() == EndTagType.UNREGISTERED) {
				codeType = TextFragment.TagType.CLOSING;
			} else {
				codeType = TextFragment.TagType.PLACEHOLDER;
			}
			addToTextUnit(codeType, literalTag, tag.getName());
		}
	}

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

	protected PropertyTextUnitPlaceholder createPropertyTextUnitPlaceholder(PlaceholderType type, String name,
			String value, Tag tag, Attribute attribute) {
		// offset of attribute
		int mainStartPos = attribute.getBegin() - tag.getBegin();
		int mainEndPos = attribute.getEnd() - tag.getBegin();

		// offset of value of the attribute
		int valueStartPos = attribute.getValueSegment().getBegin() - tag.getBegin();
		int valueEndPos = attribute.getValueSegment().getEnd() - tag.getBegin();		
	
		return new PropertyTextUnitPlaceholder(type, normalizeAttributeName(name, value, tag), value, mainStartPos, mainEndPos, valueStartPos, valueEndPos);
	}
}
