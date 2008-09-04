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
import java.util.Iterator;
import java.util.Stack;

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.EndTag;
import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagType;
import net.htmlparser.jericho.Tag;
import net.sf.okapi.common.filters.BaseParser;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.Group;
import net.sf.okapi.common.resource.IContainable;
import net.sf.okapi.common.resource.SkeletonUnit;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.html.ExtractionRule.EXTRACTION_RULE_TYPE;

public class HtmlParser extends BaseParser {
	private Source htmlDocument;
	private HtmlFilterConfiguration configuration;
	private Iterator<Segment> nodeIterator;
	protected IContainable currentResource;
	private TextUnit currentText;
	private Group currentGroup;
	private SkeletonUnit currentSkeleton;
	private ParserTokenType currentTokenType;
	private Segment lastSegmentRead;
	private boolean inline = false;
	private ExtractionRuleState ruleState;

	public HtmlParser() {
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

	public void setHtmlFilterConfiguration(HtmlFilterConfiguration configuration) {
		this.configuration = configuration;
	}

	private void initialize() {

		if (configuration == null) {
			configuration = new HtmlFilterConfiguration();
			configuration.initializeDefaultRules();
		}

		// Segment iterator
		ruleState = new ExtractionRuleState();
		htmlDocument.fullSequentialParse();
		nodeIterator = htmlDocument.getNodeIterator();
	}

	public IContainable getResource() {
		return currentResource;
	}

	public ParserTokenType parseNext() {
		boolean finished = false;

		// reset state flags
		inline = false;

		reset();

		while (nodeIterator.hasNext()) {
			Segment segment = nodeIterator.next();
			lastSegmentRead = segment;

			// if in excluded state everything is skeleton including text
			if (ruleState.isExludedState()) {
				appendToSkeletonUnit(segment.toString(), segment.getBegin(), segment.length());				
			}

			if (segment instanceof Tag) {
				final Tag tag = (Tag) segment;
				if (tag.getTagType() == StartTagType.NORMAL || tag.getTagType() == StartTagType.UNREGISTERED) {
					finished = handleStartTag((StartTag) tag);
				} else if (tag.getTagType() == EndTagType.NORMAL || tag.getTagType() == EndTagType.UNREGISTERED) {
					finished = handleEndTag((EndTag) tag);
				} else if (tag.getTagType() == StartTagType.DOCTYPE_DECLARATION) {
					finished = handleSkeleton(tag);
				} else if (tag.getTagType() == StartTagType.CDATA_SECTION) {
					finished = handleCdataSection(tag);
				} else if (tag.getTagType() == StartTagType.COMMENT) {
					finished = handleSkeleton(tag);
				} else if (tag.getTagType() == StartTagType.XML_DECLARATION) {
					finished = handleSkeleton(tag);
				} else if (tag.getTagType() == StartTagType.XML_PROCESSING_INSTRUCTION) {
					finished = handleSkeleton(tag);
				} else if (tag.getTagType() == StartTagType.MARKUP_DECLARATION) {
					finished = handleSkeleton(tag);
				} else if (tag.getTagType() == StartTagType.SERVER_COMMON) {
					// TODO: Handle server formats
					finished = handleSkeleton(tag);
				} else if (tag.getTagType() == StartTagType.SERVER_COMMON_ESCAPED) {
					// TODO: Handle server formats
					finished = handleSkeleton(tag);
				} else {
					// something we didn't expect default to skeleton
					// TODO: Add warning?
					finished = handleSkeleton(tag);
				}
			} else {
				finished = handleText(segment);
			}

			// done with current chunk (token)
			if (finished)
				break;
		}

		if (!nodeIterator.hasNext())
			return ParserTokenType.ENDINPUT;
		return currentTokenType;
	}

	private boolean handleCdataSection(Tag tag) {
		return false;
	}

	private boolean handleText(Segment text) {
		// possible ignorable whitespace
		if (text.isWhiteSpace()) {
		}

		return false;
	}

	private boolean handleSkeleton(Tag tag) {
		return false;
	}

	private boolean handleStartTag(StartTag startTag) {
		switch (getRuleType(startTag.getName())) {
		case INLINE_ELEMENT:
			inline = true;
			addToCurrentTextUnit(startTag);
			break;
		case EXTRACTABLE_ATTRIBUTE_ANY_ELEMENT:
			inline = true;
			break;
		case EXTRACTABLE_ATTRIBUTES:
			inline = true;
			break;
		case GROUP_ELEMENT:
			ruleState.pushGroupRule(startTag.getName());
			break;
		case EXCLUDED_ELEMENT:
			ruleState.pushExcludedRule(startTag.getName());
			break;
		case INCLUDED_ELEMENT:
			ruleState.pushIncludedRule(startTag.getName());
			break;
		case TEXT_UNIT_ELEMENT:
			break;
		case PRESERVE_WHITESPACE:
			ruleState.pushPreserverWhitespaceRule(startTag.getName());
			break;
		default: // non-inline element break current inline run if exists.
			inline = false;
			if (inline)
				return true;
		}

		/*
		 * // check for translatable attributes for (Attribute attribute :
		 * startTag.getAttributes()) {
		 * 
		 * }
		 */
		return false;
	}

	private boolean handleEndTag(EndTag endTag) {
		switch (getRuleType(endTag.getName())) {
		case INLINE_ELEMENT:
			inline = true;
			addToCurrentTextUnit(endTag);
			break;

		case GROUP_ELEMENT:
			ruleState.popGroupRule();
			break;

		case EXCLUDED_ELEMENT:
			ruleState.popExcludedIncludedRule();
			break;

		case INCLUDED_ELEMENT:
			ruleState.popExcludedIncludedRule();
			break;

		case TEXT_UNIT_ELEMENT:
			break;

		case PRESERVE_WHITESPACE:
			ruleState.popPreserverWhitespaceRule();
			break;

		default: // non-inline end element
			inline = false;
			break;
		}

		return false;
	}

	private EXTRACTION_RULE_TYPE getRuleType(String ruleName) {
		ExtractionRule rule = configuration.getRule(ruleName);
		return rule.getRuleType();
	}

	private void addToCurrentTextUnit(Tag tag) {		
		TextFragment.TagType tagType;
		if (tag.getTagType() == StartTagType.NORMAL || tag.getTagType() == StartTagType.UNREGISTERED) {
			if (((StartTag)tag).isSyntacticalEmptyElementTag()) 
				tagType = TextFragment.TagType.PLACEHOLDER;
			else
				tagType = TextFragment.TagType.OPENING;
		} else if (tag.getTagType() == EndTagType.NORMAL || tag.getTagType() == EndTagType.UNREGISTERED) {
			tagType = TextFragment.TagType.CLOSING;
		}
		else {
			tagType = TextFragment.TagType.PLACEHOLDER;
		}		
		appendToTextUnit(new Code(tagType, tag.getName(), tag.toString()));
	}
}
