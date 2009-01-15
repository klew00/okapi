/*===========================================================================*/
/* Copyright (C) 2008 Jim Hargrave                                           */
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
/* Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA              */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.filters.html;

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
import net.sf.okapi.common.markupfilter.ExtractionRuleState;
import net.sf.okapi.common.markupfilter.Parameters;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.yaml.TaggedFilterConfiguration;
import net.sf.okapi.common.yaml.TaggedFilterConfiguration.RULE_TYPE;

public class HtmlFilter extends BaseFilter {
	private Source htmlDocument;
	private Iterator<Segment> nodeIterator;
	private ExtractionRuleState ruleState;
	private Parameters parameters;

	public HtmlFilter() {
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
		htmlDocument = new Source(input);
		initialize();
	}

	public void open(InputStream input) {
		try {
			if (getEncoding() != null) {
				BufferedReader r = new BufferedReader(new InputStreamReader(input, getEncoding()));
				htmlDocument = new Source(r);
			} else {
				// try to guess encoding
				htmlDocument = new Source(input);
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
				htmlDocument = new Source(r);
			} else {
				// try to guess encoding
				htmlDocument = new Source(input);
			}
		} catch (IOException e) {
			// TODO: Wrap unchecked exception
			throw new RuntimeException(e);
		}
		initialize();
	}

	@Override
	public void initialize() {
		super.initialize();

		setMimeType("text/html"); //$NON-NLS-1$

		if (parameters == null) {
			parameters = new Parameters();
			URL url = HtmlFilter.class.getResource("/net/sf/okapi/filters/html/defaultConfiguration.yml"); //$NON-NLS-1$
			parameters.setTaggedConfig(new TaggedFilterConfiguration(url));
		}

		// Segment iterator
		ruleState = new ExtractionRuleState();
		htmlDocument.fullSequentialParse();
		nodeIterator = htmlDocument.getNodeIterator();
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

	private void handleCdataSection(Tag tag) {
		addToDocumentPart(tag.toString());
		// TODO: special handling for CDATA sections (may call sub-filters or
		// unescape content etc.)
	}

	private void handleText(Segment text) {
		// if in excluded state everything is skeleton including text
		if (ruleState.isExludedState()) {
			addToDocumentPart(text.toString());
			return;
		}

		// check for ignorable whitespace and add it to the skeleton
		// The Jericho html parser always pulls out the largest stretch of text
		// so standalone whitespace should always be ignorable if we are not
		// already processing inline text
		if (text.isWhiteSpace() && !isInsideTextRun()) {
			addToDocumentPart(text.toString());
			return;
		}

		if (canStartNewTextUnit()) {
			startTextUnit(text.toString());
		} else {
			addToTextUnit(text.toString());
		}
	}

	private void handleSkeleton(Tag tag) {
		addToDocumentPart(tag.toString());
	}

	private void handleStartTag(StartTag startTag) {
		// if in excluded state everything is skeleton including text
		if (ruleState.isExludedState()) {
			addToDocumentPart(startTag.toString());
			// process these tag types to update parser state
			switch (parameters.getTaggedConfig().getMainRuleType(startTag.getName())) {
			case EXCLUDED_ELEMENT:
				ruleState.pushExcludedRule(startTag.getName());
				break;
			case INCLUDED_ELEMENT:
				ruleState.pushIncludedRule(startTag.getName());
				break;
			case PRESERVE_WHITESPACE:
				ruleState.pushPreserverWhitespaceRule(startTag.getName());
				break;
			}
			return;
		}

		switch (parameters.getTaggedConfig().getMainRuleType(startTag.getName())) {
		case INLINE_ELEMENT:
			if (canStartNewTextUnit()) {
				startTextUnit();
			}
			addCodeToCurrentTextUnit(startTag);
			break;

		case ATTRIBUTES_ONLY:
			// we assume we have already ended any (non-complex) TextUnit in
			// the main while loop above
			List<PropertyTextUnitPlaceholder> propertyTextUnitPlaceholders;

			if (parameters.getTaggedConfig().hasActionableAttributes(startTag.getName())) {
				propertyTextUnitPlaceholders = createPropertyTextUnitPlaceholders(startTag);
				startDocumentPart(startTag.toString(), startTag.getName(), propertyTextUnitPlaceholders);
				endDocumentPart();
			} else {
				// This shouldn't happen if rules are consistent
				addToDocumentPart(startTag.toString());
			}
			break;
		case GROUP_ELEMENT:
			ruleState.pushGroupRule(startTag.getName());
			startGroup(new GenericSkeleton(startTag.toString()));
			break;
		case EXCLUDED_ELEMENT:
			ruleState.pushExcludedRule(startTag.getName());
			addToDocumentPart(startTag.toString());
			break;
		case INCLUDED_ELEMENT:
			ruleState.pushIncludedRule(startTag.getName());
			addToDocumentPart(startTag.toString());
			break;
		case TEXT_UNIT_ELEMENT:
			ruleState.pushTextUnitRule(startTag.getName());
			startTextUnit(new GenericSkeleton(startTag.toString()));
			break;
		case PRESERVE_WHITESPACE:
			ruleState.pushPreserverWhitespaceRule(startTag.getName());
			addToDocumentPart(startTag.toString());
			break;
		default:
			addToDocumentPart(startTag.toString());
		}
	}

	private void handleEndTag(EndTag endTag) {
		// if in excluded state everything is skeleton including text
		if (ruleState.isExludedState()) {
			addToDocumentPart(endTag.toString());
			// process these tag types to update parser state
			switch (parameters.getTaggedConfig().getMainRuleType(endTag.getName())) {
			case EXCLUDED_ELEMENT:
				ruleState.popExcludedIncludedRule();
				break;
			case INCLUDED_ELEMENT:
				ruleState.popExcludedIncludedRule();
				break;
			case PRESERVE_WHITESPACE:
				ruleState.popPreserverWhitespaceRule();
				break;
			}

			return;
		}

		switch (parameters.getTaggedConfig().getMainRuleType(endTag.getName())) {
		case INLINE_ELEMENT:
			if (canStartNewTextUnit()) {
				startTextUnit();
			}
			addCodeToCurrentTextUnit(endTag);
			break;
		case GROUP_ELEMENT:
			ruleState.popGroupRule();
			endGroup(new GenericSkeleton(endTag.toString()));
			break;
		case EXCLUDED_ELEMENT:
			ruleState.popExcludedIncludedRule();
			addToDocumentPart(endTag.toString());
			break;
		case INCLUDED_ELEMENT:
			ruleState.popExcludedIncludedRule();
			addToDocumentPart(endTag.toString());
			break;
		case TEXT_UNIT_ELEMENT:
			ruleState.popTextUnitRule();
			endTextUnit(new GenericSkeleton(endTag.toString()));
			break;
		case PRESERVE_WHITESPACE:
			ruleState.popPreserverWhitespaceRule();
			addToDocumentPart(endTag.toString());
			break;
		default:
			addToDocumentPart(endTag.toString());
			break;
		}
	}

	private void addCodeToCurrentTextUnit(Tag tag) {
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

	private List<PropertyTextUnitPlaceholder> createPropertyTextUnitPlaceholders(StartTag startTag) {
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

	private PropertyTextUnitPlaceholder createPropertyTextUnitPlaceholder(PlaceholderType type, String name,
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.filters.IFilter#getName()
	 */
	public String getName() {
		return "HTML Filter"; //$NON-NLS-1$
	}
}
