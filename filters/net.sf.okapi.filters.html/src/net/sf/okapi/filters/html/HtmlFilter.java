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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
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
import net.sf.okapi.common.filters.FilterEventType;
import net.sf.okapi.common.filters.GroovyFilterConfiguration;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.IContainable;
import net.sf.okapi.common.resource.TextFragment;

public class HtmlFilter extends BaseFilter {
	private Source htmlDocument;
	private GroovyFilterConfiguration configuration;
	private Iterator<Segment> nodeIterator;
	private ExtractionRuleState ruleState;

	public HtmlFilter() {
	}

	public void close() {
	}

	public void open(CharSequence input) {
		htmlDocument = new Source(input);
		initialize();
	}

	public void open(InputStream input) {
		try {
			htmlDocument = new Source(input);
		} catch (IOException e) {
			// TODO Wrap unchecked exception
			throw new RuntimeException(e);
		}
		initialize();
	}

	public void open(URL input) {
		try {
			htmlDocument = new Source(input);
		} catch (IOException e) {
			// TODO: Wrap unchecked exception
			throw new RuntimeException(e);
		}
		initialize();
	}

	public void setHtmlFilterConfiguration(GroovyFilterConfiguration configuration) {
		this.configuration = configuration;
	}

	private void initialize() {
		if (configuration == null) {
			configuration = new GroovyFilterConfiguration("/net/sf/okapi/filters/html/defaultConfiguration.groovy");
		}

		// Segment iterator
		ruleState = new ExtractionRuleState();
		htmlDocument.fullSequentialParse();
		nodeIterator = htmlDocument.getNodeIterator();
	}

	public IContainable getResource() {
		return getFinalizedToken();
	}
	
	public FilterEvent next() {
		if (isFinishedParsing()) {
			setDone();
			return new FilterEvent(FilterEventType.FINISHED);
		}

		// reset state flags and buffers
		ruleState.reset();
		initializeLoop();

		while (!isFinishedToken() && nodeIterator.hasNext() && !isCanceled()) {
			Segment segment = nodeIterator.next();

			if (segment instanceof Tag) {
				final Tag tag = (Tag) segment;

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
		}

		if (isCanceled()) {
			return new FilterEvent(getFinalizedTokenType(), getResource());
		}

		if (!nodeIterator.hasNext()) {
			// take care of the token from the previous run
			finalizeCurrentToken();
			setFinishedParsing(true);
		}

		// return our finalized token
		return new FilterEvent(getFinalizedTokenType(), getResource());
	}

	private void handleCdataSection(Tag tag) {
		// if in excluded state everything is skeleton including text
		if (ruleState.isExludedState()) {
			appendToSkeletonUnit(tag.toString(), tag.getBegin(), tag.length());
			return;
		}

		// TODO: special handling for CDATA sections (may call sub-filters
		// or unescape content etc.)
		appendToSkeletonUnit(tag.toString(), tag.getBegin(), tag.length());
	}

	private void handleText(Segment text) {
		// if in excluded state everything is skeleton including text
		if (ruleState.isExludedState()) {
			appendToSkeletonUnit(text.toString(), text.getBegin(), text.length());
			return;
		}

		// check for ignorable whitespace and add it to the skeleton
		// The Jericho html parser always pulls out the largest stretch of text
		// so standalone whitespace should always be ignorable if we are not
		// already processing inline text
		if (text.isWhiteSpace() && !ruleState.isInline()) {
			appendToSkeletonUnit(text.toString(), text.getBegin(), text.length());
			return;
		}

		// its not pure whitespace so we are now processing inline text
		ruleState.setInline(true);
		appendToTextUnit(text.toString());
	}

	private void handleSkeleton(Tag tag) {
		appendToSkeletonUnit(tag.toString(), tag.getBegin(), tag.length());
	}

	private void handleStartTag(StartTag startTag) {
		// if in excluded state everything is skeleton including text
		if (ruleState.isExludedState()) {
			appendToSkeletonUnit(startTag.toString(), startTag.getBegin(), startTag.length());
			// process these tag types to update parser state
			switch (configuration.getMainRuleType(startTag.getName())) {
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

		switch (configuration.getMainRuleType(startTag.getName())) {
		case INLINE_ELEMENT:
			ruleState.setInline(true);
			addToCurrentTextUnit(startTag);
			break;

		case ATTRIBUTES_ONLY:
			if (configuration.hasActionableAttributes(startTag.getName())) {				
			}
			break;
		case GROUP_ELEMENT:
			startGroup(startTag.getName(), startTag.toString());
			break;
		case EXCLUDED_ELEMENT:
			ruleState.pushExcludedRule(startTag.getName());
			appendToSkeletonUnit(startTag.toString(), startTag.getBegin(), startTag.length());
			break;
		case INCLUDED_ELEMENT:
			ruleState.pushIncludedRule(startTag.getName());
			appendToSkeletonUnit(startTag.toString(), startTag.getBegin(), startTag.length());
			break;
		case TEXT_UNIT_ELEMENT:
			// TODO: I'm wondering if we really need before and after skeleton.
			// If we do need it I need to know which tags to apply it to.
			appendToSkeletonUnit(startTag.toString(), startTag.getBegin(), startTag.length());
			break;
		case PRESERVE_WHITESPACE:
			ruleState.pushPreserverWhitespaceRule(startTag.getName());
			appendToSkeletonUnit(startTag.toString(), startTag.getBegin(), startTag.length());
			break;
		default:
			ruleState.setInline(false);
			appendToSkeletonUnit(startTag.toString(), startTag.getBegin(), startTag.length());
		}
	}

	private void handleEndTag(EndTag endTag) {
		// if in excluded state everything is skeleton including text
		if (ruleState.isExludedState()) {
			appendToSkeletonUnit(endTag.toString(), endTag.getBegin(), endTag.length());
			// process these tag types to update parser state
			switch (configuration.getMainRuleType(endTag.getName())) {
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

		switch (configuration.getMainRuleType(endTag.getName())) {
		case INLINE_ELEMENT:
			ruleState.setInline(true);
			addToCurrentTextUnit(endTag);
			break;
		case GROUP_ELEMENT:
			endGroup(endTag.toString());
			break;
		case EXCLUDED_ELEMENT:
			ruleState.popExcludedIncludedRule();
			appendToSkeletonUnit(endTag.toString(), endTag.getBegin(), endTag.length());
			break;
		case INCLUDED_ELEMENT:
			ruleState.popExcludedIncludedRule();
			appendToSkeletonUnit(endTag.toString(), endTag.getBegin(), endTag.length());
			break;
		case TEXT_UNIT_ELEMENT:
			// TODO: if we really need before and after skeleton I need to know
			// which tags to apply it to.
			appendToSkeletonUnit(endTag.toString(), endTag.getBegin(), endTag.length());
			break;
		case PRESERVE_WHITESPACE:
			ruleState.popPreserverWhitespaceRule();
			appendToSkeletonUnit(endTag.toString(), endTag.getBegin(), endTag.length());
			break;
		default:
			ruleState.setInline(false);
			appendToSkeletonUnit(endTag.toString(), endTag.getBegin(), endTag.length());
			break;
		}
	}

	private void addAttribute(StartTag startTag) {
		// convert Jericho attributes to HashMap
		Map<String, String> attrs = new HashMap<String, String>();
		attrs = startTag.getAttributes().populateMap(attrs, true);
		for (Attribute attribute : startTag.getAttributes()) {
			if (configuration.isTranslatableAttribute(startTag.getName(), attribute.getName(), attrs)) {

			} else if (configuration.isLocalizableAttribute(startTag.getName(), attribute.getName(), attrs)) {

			}
		}
	}
	private void addToCurrentTextUnit(Tag tag) {
		TextFragment.TagType tagType;
		if (tag.getTagType() == StartTagType.NORMAL || tag.getTagType() == StartTagType.UNREGISTERED) {
			if (((StartTag) tag).isSyntacticalEmptyElementTag())
				tagType = TextFragment.TagType.PLACEHOLDER;
			else
				tagType = TextFragment.TagType.OPENING;
		} else if (tag.getTagType() == EndTagType.NORMAL || tag.getTagType() == EndTagType.UNREGISTERED) {
			tagType = TextFragment.TagType.CLOSING;
		} else {
			tagType = TextFragment.TagType.PLACEHOLDER;
		}
		appendToTextUnit(new Code(tagType, tag.getName(), tag.toString()));
	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.filters.IFilter#getName()
	 */
	public String getName() {
		return "HTMLFilter";
	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.filters.IFilter#getParameters()
	 */
	public IParameters getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setOptions (String language,
		String defaultEncoding,
		boolean generateSkeleton)
	{
		// TODO Auto-generated method stub		
	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.filters.IFilter#setParameters(net.sf.okapi.common.IParameters)
	 */
	public void setParameters(IParameters params) {
		// TODO Auto-generated method stub	
	}
}
