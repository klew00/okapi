package net.sf.okapi.common.markupfilter;

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
import net.sf.okapi.common.encoder.HtmlEncoder;
import net.sf.okapi.common.filters.BaseFilter;
import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.PropertyTextUnitPlaceholder;
import net.sf.okapi.common.filters.PropertyTextUnitPlaceholder.PlaceholderType;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.yaml.TaggedFilterConfiguration;
import net.sf.okapi.common.yaml.TaggedFilterConfiguration.RULE_TYPE;

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
		// TODO Auto-generated method stub
		return null;
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
		// TODO: Implement generateSkeleton
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
			// TODO Wrap unchecked exception
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
			// TODO: Wrap unchecked exception
			throw new RuntimeException(e);
		}
		initialize();
	}
	
	@Override
	protected void initialize() {
		super.initialize();		

		if (parameters == null) {
			parameters = new Parameters();
			URL url = BaseMarkupFilter.class.getResource(defaultConfig); //$NON-NLS-1$
			parameters.setTaggedConfig(new TaggedFilterConfiguration(url));
		}

		// Segment iterator
		ruleState = new ExtractionRuleState();
		document.fullSequentialParse();
		nodeIterator = document.getNodeIterator();
	}
	
	protected void setDefaultConfig(String defaultConfig) {
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
				if (parameters.getTaggedConfig().getMainRuleType(tag.getName()) == RULE_TYPE.INLINE_ELEMENT)
					inlineTag = true;
				if (isCurrentTextUnit() && !isCurrentComplexTextUnit() && !inlineTag) {
					endTextUnit();
				}

				if (tag.getTagType() == StartTagType.NORMAL || tag.getTagType() == StartTagType.UNREGISTERED) {
					handleStartTag((StartTag) tag);
				} else if (tag.getTagType() == EndTagType.NORMAL || tag.getTagType() == EndTagType.UNREGISTERED) {
					handleEndTag((EndTag) tag);
				} else if (tag.getTagType() == StartTagType.DOCTYPE_DECLARATION) {
					handleSkeleton(tag);
				} else if (tag.getTagType() == StartTagType.CDATA_SECTION) {
					handleCdataSection(tag);
				} else if (tag.getTagType() == StartTagType.COMMENT) {
					handleSkeleton(tag);
				} else if (tag.getTagType() == StartTagType.XML_DECLARATION) {
					handleSkeleton(tag);
				} else if (tag.getTagType() == StartTagType.XML_PROCESSING_INSTRUCTION) {
					handleSkeleton(tag);
				} else if (tag.getTagType() == StartTagType.MARKUP_DECLARATION) {
					handleSkeleton(tag);
				} else if (tag.getTagType() == StartTagType.SERVER_COMMON) {
					// TODO: Handle server formats
					handleSkeleton(tag);
				} else if (tag.getTagType() == StartTagType.SERVER_COMMON_ESCAPED) {
					// TODO: Handle server formats
					handleSkeleton(tag);
				} else { // not classified explicitly by Jericho
					if (tag instanceof StartTag) {
						handleStartTag((StartTag) tag);
					} else if (tag instanceof EndTag) {
						handleEndTag((EndTag) tag);
					} else {
						handleSkeleton(tag);
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
	
	protected void handleCdataSection(Tag tag) {}
	protected void handleText(Segment text) {}
	protected void handleStartTag(StartTag startTag) {}
	protected void handleEndTag(EndTag endTag) {}
	protected void handleSkeleton(Tag endTag) {}
	
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

			if (parameters.getTaggedConfig().hasActionableAttributes(startTag.getName())) {
				// create a list of Property or Text placeholders for this tag
				propertyTextUnitPlaceholders = createPropertyTextUnitPlaceholders(startTag);
				// create code and add it to the current TextUnit 
				addToTextUnit(codeType, literalTag, startTag.getName(), propertyTextUnitPlaceholders);
			}
		} else {  // end or unknown tag			
			if (tag.getTagType() == EndTagType.NORMAL || tag.getTagType() == EndTagType.UNREGISTERED) {
				codeType = TextFragment.TagType.CLOSING;
			} else {
				codeType = TextFragment.TagType.PLACEHOLDER;
			}
			addToTextUnit(new Code(codeType, tag.getName(), literalTag));			
		}
	}

	protected List<PropertyTextUnitPlaceholder> createPropertyTextUnitPlaceholders(StartTag startTag) {
		// list to hold the properties or TextUnits
		List<PropertyTextUnitPlaceholder> propertyOrTextUnitPlaceholders = new LinkedList<PropertyTextUnitPlaceholder>();

		// convert Jericho attributes to HashMap
		Map<String, String> attrs = startTag.getAttributes().populateMap(new HashMap<String, String>(), true);
		for (Attribute attribute : startTag.parseAttributes()) {
			if (parameters.getTaggedConfig().isTranslatableAttribute(startTag.getName(), attribute.getName(), attrs)) {
				propertyOrTextUnitPlaceholders.add(createPropertyTextUnitPlaceholder(PlaceholderType.TRANSLATABLE,
						attribute.getName(), attribute.getValue(), startTag, attribute));
			} else {

				if (parameters.getTaggedConfig().isReadOnlyLocalizableAttribute(startTag.getName(),
						attribute.getName(), attrs)) {
					propertyOrTextUnitPlaceholders.add(createPropertyTextUnitPlaceholder(
							PlaceholderType.READ_ONLY_PROPERTY, attribute.getName(), attribute.getValue(), startTag,
							attribute));
				} else if (parameters.getTaggedConfig().isWritableLocalizableAttribute(startTag.getName(),
						attribute.getName(), attrs)) {
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

		// normalize values for encoder
		if (name.equals(HtmlEncoder.NATIVE_ENCODING)) {
			name = HtmlEncoder.NORMALIZED_ENCODING;
		} else if (name.equals(HtmlEncoder.NATIVE_LANGUAGE)) {
			name = HtmlEncoder.NORMALIZED_LANGUAGE;
		}
		return new PropertyTextUnitPlaceholder(type, name, value, mainStartPos, mainEndPos, valueStartPos, valueEndPos);
	}
}
