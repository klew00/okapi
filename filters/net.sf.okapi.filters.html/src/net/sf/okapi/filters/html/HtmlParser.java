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

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.EndTag;
import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagType;
import net.htmlparser.jericho.Tag;
import net.sf.okapi.common.filters.IParser;
import net.sf.okapi.common.resource.Group;
import net.sf.okapi.common.resource.IContainable;
import net.sf.okapi.common.resource.SkeletonUnit;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.html.ExtractionRule.EXTRACTION_RULE_TYPE;

public class HtmlParser implements IParser {
	private Source htmlDocument;
	private HtmlFilterConfiguration configuration;
	private Iterator<Segment> nodeIterator;
	protected IContainable currentResource;
	private TextUnit currentText;
	private Group currentGroup;
	private SkeletonUnit currentSkeleton;
	private ParserTokenType currentTokenType;
	private boolean inline = false;
	private boolean excluded = false;
	private int groupID = 0;
	private int itemId = 0;
	private int sklId = 0;

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
		nodeIterator = htmlDocument.getNodeIterator();
	}

	public IContainable getResource() {
		return currentResource;
	}

	public ParserTokenType parseNext() {
		// reset state flags
		inline = false;
		excluded = false;

		// reset our text and skeleton units for a new pass
		if (currentText == null || !currentText.isEmpty())
			currentText = new TextUnit();
		if (currentSkeleton == null || !currentSkeleton.isEmpty())
			currentSkeleton = new SkeletonUnit();

		while (nodeIterator.hasNext()) {
			Segment segment = nodeIterator.next();
			// if in excluded state everything is skeleton
			if (excluded) {
				if (currentSkeleton.isEmpty()) {
					currentSkeleton.setID(String.format("s%d", ++sklId));
					currentSkeleton.setData(segment.toString());
					currentSkeleton.setData(segment.getBegin(), segment.length());
				}
				else {
					currentSkeleton.appendData(segment.toString());
					currentSkeleton.addToLength(segment.length());
				}					
			}
			
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
				} else {
					// something we didn't expect default to skeleton
					// TODO: Add warning?
					handleSkeleton(tag);
				}
			} else {
				handleText(segment);
			}
		}

		return currentTokenType;
	}

	private void handleCdataSection(Tag tag) {		
	}

	private void handleText(Segment text) {
		// possible ignorable whitespace
		if (text.isWhiteSpace()) {
			// SkeletonUnit skeleton = new SkeletonUnit(
			// String.format("s%d", ++sklId), segment.getBegin(),
			// segment.length());
		}
	}

	private void handleSkeleton(Tag tag) {

	}

	private void handleStartTag(StartTag startTag) {
		switch (getRuleType(startTag.getName())) {
		case INLINE_ELEMENT:
			excluded = false;
			break;

		case EXTRACTABLE_ATTRIBUTE_ANY_ELEMENT:
			excluded = false;
			break;

		case EXTRACTABLE_ATTRIBUTES:
			excluded = false;
			break;

		case GROUP_ELEMENT:
			excluded = false;
			break;

		case EXCLUDED_ELEMENT:
			inline = false;
			excluded = true;
			break;

		case INCLUDED_ELEMENT:
			excluded = false;
			break;

		case TEXT_UNIT_ELEMENT:
			break;

		default:
			break;
		}

		/*
		 * // check for translatable attributes for (Attribute attribute :
		 * startTag.getAttributes()) {
		 * 
		 * }
		 */
	}

	private void handleEndTag(EndTag endTag) {
		switch (getRuleType(endTag.getName())) {
		case INLINE_ELEMENT:
			excluded = false;
			break;

		case EXTRACTABLE_ATTRIBUTE_ANY_ELEMENT:
			excluded = false;
			break;

		case EXTRACTABLE_ATTRIBUTES:
			excluded = false;
			break;

		case GROUP_ELEMENT:
			excluded = false;
			break;

		case EXCLUDED_ELEMENT:
			excluded = false;
			break;

		case INCLUDED_ELEMENT:
			excluded = false;
			break;

		case TEXT_UNIT_ELEMENT:
			break;

		default:
			break;
		}
	}

	private EXTRACTION_RULE_TYPE getRuleType(String ruleName) {
		ExtractionRule rule = configuration.getRule(ruleName);
		return rule.getRuleType();
	}

	private TextUnit createTranslatableTextUnit(String text) {
		TextUnit textUnit = new TextUnit(String.format("s%d", ++itemId), text);
		textUnit.setIsTranslatable(true);
		textUnit.setPreserveWhitespaces(configuration.isPreserveWhitespace());
		return textUnit;
	}

	private TextUnit createLocalizableTextUnit(String text) {
		TextUnit textUnit = new TextUnit(String.format("s%d", ++itemId), text);
		textUnit.setIsTranslatable(false);
		textUnit.setPreserveWhitespaces(configuration.isPreserveWhitespace());
		// TODO: textUnit.setProperty(name, value);
		return textUnit;
	}
}
