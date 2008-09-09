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

import net.htmlparser.jericho.EndTag;
import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagType;
import net.htmlparser.jericho.Tag;
import net.sf.okapi.common.filters.BaseParser;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.IContainable;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.filters.html.ExtractionRule.EXTRACTION_RULE_TYPE;

public class HtmlParser extends BaseParser {
	private Source htmlDocument;
	private Segment lastSegment;
	private HtmlFilterConfiguration configuration;
	private Iterator<Segment> nodeIterator;
	private IContainable currentResource;
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

		// reset state flags and buffers
		ruleState.reset();
		reset();

		if (!nodeIterator.hasNext()) {
			currentResource = null;
			return ParserTokenType.ENDINPUT;
		}
		
		while (!finished && nodeIterator.hasNext()) {
			Segment segment = nodeIterator.next();		

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
		}

		// should be only one instance of textUnit, skeletonUnit and group,
		// all others must be empty.
		if (!isTextUnitEmtpy()) {
			assert (isSkeletonUnitEmtpy());
			assert (isGroupEmtpy());
			currentResource = getTextUnit();
			return ParserTokenType.TRANSUNIT;
		} else if (!isSkeletonUnitEmtpy()) {
			assert (isTextUnitEmtpy());
			assert (isGroupEmtpy());
			currentResource = getSkeletonUnit();
			return ParserTokenType.SKELETON;
		} else if (!isGroupEmtpy()) {
			assert (isSkeletonUnitEmtpy());
			assert (isTextUnitEmtpy());
			currentResource = getGroup();
			if (ruleState.isGroupState()) {
				return ParserTokenType.STARTGROUP;
			} else {
				return ParserTokenType.ENDGROUP;
			}
		} 		

		// default token type
		return ParserTokenType.NONE;
	}

	private boolean handleCdataSection(Tag tag) {
		// if in excluded state everything is skeleton including text
		if (ruleState.isExludedState()) {
			appendToSkeletonUnit(tag.toString(), tag.getBegin(), tag.length());
			return false;
		}

		// TODO: special handling for CDATA sections
		return false;
	}

	private boolean handleText(Segment text) {
		// if in excluded state everything is skeleton including text
		if (ruleState.isExludedState()) {
			appendToSkeletonUnit(text.toString(), text.getBegin(), text.length());
			return false;
		}

		// check for ignorable whitespace, if not then we are possibly starting a text run
		if (!text.isWhiteSpace()) {
			ruleState.setInline(true);
		}

		if (ruleState.isInline()) {
			if (!isSkeletonUnitEmtpy()) {
				// stop and return the skeleton, save text for next parse loop 
				lastSegment = text;
				return true;
			}
			appendToTextUnit(text.toString());
		} else {
			appendToSkeletonUnit(text.toString(), text.getBegin(), text.length());
		}

		return false;
	}

	private boolean handleSkeleton(Tag tag) {
		appendToSkeletonUnit(tag.toString(), tag.getBegin(), tag.length());
		return false;
	}

	private boolean handleStartTag(StartTag startTag) {
		// if in excluded state everything is skeleton including text
		if (ruleState.isExludedState()) {
			appendToSkeletonUnit(startTag.toString(), startTag.getBegin(), startTag.length());
		}

		switch (getRuleType(startTag.getName())) {
		case INLINE_ELEMENT:
			if (ruleState.isExludedState()) {
				appendToSkeletonUnit(startTag.toString(), startTag.getBegin(), startTag.length());
				return false;
			}

			ruleState.setInline(true);
			addToCurrentTextUnit(startTag);
			// break current skeleton run
			if (!isSkeletonUnitEmtpy()) {
				return true;
			}
			return false;

		case EXTRACTABLE_ATTRIBUTES:
			if (!ruleState.isExludedState()) {
				addToCurrentTextUnit(startTag);
			}
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
		default:
			ruleState.setInline(false);
			// non-extractable element break current extractable run if exists.
			if (!isTextUnitEmtpy()) {
				lastSegment = startTag;
				return true;
			} else {
				
			}
			
			return false;
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
		// if in excluded state everything is skeleton including text
		if (ruleState.isExludedState()) {
			appendToSkeletonUnit(endTag.toString(), endTag.getBegin(), endTag.length());
		}

		switch (getRuleType(endTag.getName())) {
		case INLINE_ELEMENT:
			if (!ruleState.isExludedState()) {
				ruleState.setInline(true);
				addToCurrentTextUnit(endTag);
			}
			break;
		case GROUP_ELEMENT:
			ruleState.popGroupRule();
			return true;
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
		default:
			ruleState.setInline(false);
			// non-extractable element break current extractable run if exists.
			if (!isTextUnitEmtpy()) {
				lastSegment = endTag;
				return true;
			}
			return false;
		}

		return false;
	}

	private EXTRACTION_RULE_TYPE getRuleType(String ruleName) {
		ExtractionRule rule = configuration.getRule(ruleName);
		if (rule == null) {
			return EXTRACTION_RULE_TYPE.NON_EXTRACTABLE;
		}
		return rule.getRuleType();
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
}
