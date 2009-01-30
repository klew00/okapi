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

import java.util.List;

import net.htmlparser.jericho.EndTag;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.Tag;
import net.sf.okapi.common.encoder.HtmlEncoder;
import net.sf.okapi.common.filters.PropertyTextUnitPlaceholder;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.filters.markupfilter.BaseMarkupFilter;

public class HtmlFilter extends BaseMarkupFilter {
	public HtmlFilter() {
		super();
		setMimeType("text/html");
		setDefaultConfig("/net/sf/okapi/filters/html/defaultConfiguration.yml");
	}

	@Override
	protected void handleCdataSection(Tag tag) {
		addToDocumentPart(tag.toString());
		// TODO: special handling for CDATA sections (may call sub-filters or
		// unescape content etc.)
	}

	@Override
	protected void handleText(Segment text) {
		// if in excluded state everything is skeleton including text
		if (getRuleState().isExludedState()) {
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

	@Override
	protected void handleDocumentPart(Tag tag) {
		addToDocumentPart(tag.toString());
	}

	@Override
	protected void handleStartTag(StartTag startTag) {
		// if in excluded state everything is skeleton including text
		if (getRuleState().isExludedState()) {
			addToDocumentPart(startTag.toString());
			// process these tag types to update parser state
			switch (getConfig().getMainRuleType(startTag.getName())) {
			case EXCLUDED_ELEMENT:
				getRuleState().pushExcludedRule(startTag.getName());
				break;
			case INCLUDED_ELEMENT:
				getRuleState().pushIncludedRule(startTag.getName());
				break;
			case PRESERVE_WHITESPACE:
				getRuleState().pushPreserverWhitespaceRule(startTag.getName());
				break;
			}
			return;
		}

		switch (getConfig().getMainRuleType(startTag.getName())) {
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

			propertyTextUnitPlaceholders = createPropertyTextUnitPlaceholders(startTag);
			if (propertyTextUnitPlaceholders != null && !propertyTextUnitPlaceholders.isEmpty()) {
				startDocumentPart(startTag.toString(), startTag.getName(), propertyTextUnitPlaceholders);
				endDocumentPart();
			} else {
				// no attributes that need processing - just treat as skeleton
				addToDocumentPart(startTag.toString());
			}
			break;
		case GROUP_ELEMENT:
			getRuleState().pushGroupRule(startTag.getName());
			startGroup(new GenericSkeleton(startTag.toString()));
			break;
		case EXCLUDED_ELEMENT:
			getRuleState().pushExcludedRule(startTag.getName());
			addToDocumentPart(startTag.toString());
			break;
		case INCLUDED_ELEMENT:
			getRuleState().pushIncludedRule(startTag.getName());
			addToDocumentPart(startTag.toString());
			break;
		case TEXT_UNIT_ELEMENT:
			getRuleState().pushTextUnitRule(startTag.getName());
			propertyTextUnitPlaceholders = createPropertyTextUnitPlaceholders(startTag);
			if (propertyTextUnitPlaceholders != null && !propertyTextUnitPlaceholders.isEmpty()) {
				startTextUnit(new GenericSkeleton(startTag.toString()), propertyTextUnitPlaceholders);
			} else {
				startTextUnit(new GenericSkeleton(startTag.toString()));
			}
			break;
		case PRESERVE_WHITESPACE:
			getRuleState().pushPreserverWhitespaceRule(startTag.getName());
			addToDocumentPart(startTag.toString());
			break;
		default:
			addToDocumentPart(startTag.toString());
		}
	}

	@Override
	protected void handleEndTag(EndTag endTag) {
		// if in excluded state everything is skeleton including text
		if (getRuleState().isExludedState()) {
			addToDocumentPart(endTag.toString());
			// process these tag types to update parser state
			switch (getConfig().getMainRuleType(endTag.getName())) {
			case EXCLUDED_ELEMENT:
				getRuleState().popExcludedIncludedRule();
				break;
			case INCLUDED_ELEMENT:
				getRuleState().popExcludedIncludedRule();
				break;
			case PRESERVE_WHITESPACE:
				getRuleState().popPreserverWhitespaceRule();
				break;
			}

			return;
		}

		switch (getConfig().getMainRuleType(endTag.getName())) {
		case INLINE_ELEMENT:
			if (canStartNewTextUnit()) {
				startTextUnit();
			}
			addCodeToCurrentTextUnit(endTag);
			break;
		case GROUP_ELEMENT:
			getRuleState().popGroupRule();
			endGroup(new GenericSkeleton(endTag.toString()));
			break;
		case EXCLUDED_ELEMENT:
			getRuleState().popExcludedIncludedRule();
			addToDocumentPart(endTag.toString());
			break;
		case INCLUDED_ELEMENT:
			getRuleState().popExcludedIncludedRule();
			addToDocumentPart(endTag.toString());
			break;
		case TEXT_UNIT_ELEMENT:
			getRuleState().popTextUnitRule();
			endTextUnit(new GenericSkeleton(endTag.toString()));
			break;
		case PRESERVE_WHITESPACE:
			getRuleState().popPreserverWhitespaceRule();
			addToDocumentPart(endTag.toString());
			break;
		default:
			addToDocumentPart(endTag.toString());
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.sf.okapi.common.markupfilter.BaseMarkupFilter#handleComment(net.
	 * htmlparser.jericho.Tag)
	 */
	@Override
	protected void handleComment(Tag tag) {
		handleDocumentPart(tag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.okapi.common.markupfilter.BaseMarkupFilter#handleDocTypeDeclaration
	 * (net.htmlparser.jericho.Tag)
	 */
	@Override
	protected void handleDocTypeDeclaration(Tag tag) {
		handleDocumentPart(tag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.okapi.common.markupfilter.BaseMarkupFilter#handleMarkupDeclaration
	 * (net.htmlparser.jericho.Tag)
	 */
	@Override
	protected void handleMarkupDeclaration(Tag tag) {
		handleDocumentPart(tag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.okapi.common.markupfilter.BaseMarkupFilter#handleProcessingInstruction
	 * (net.htmlparser.jericho.Tag)
	 */
	@Override
	protected void handleProcessingInstruction(Tag tag) {
		handleDocumentPart(tag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.okapi.common.markupfilter.BaseMarkupFilter#handleServerCommon(
	 * net.htmlparser.jericho.Tag)
	 */
	@Override
	protected void handleServerCommon(Tag tag) {
		handleDocumentPart(tag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.okapi.common.markupfilter.BaseMarkupFilter#handleServerCommonEscaped
	 * (net.htmlparser.jericho.Tag)
	 */
	@Override
	protected void handleServerCommonEscaped(Tag tag) {
		handleDocumentPart(tag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.okapi.common.markupfilter.BaseMarkupFilter#handleXmlDeclaration
	 * (net.htmlparser.jericho.Tag)
	 */
	@Override
	protected void handleXmlDeclaration(Tag tag) {
		handleDocumentPart(tag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.filters.IFilter#getName()
	 */
	public String getName() {
		return "HTML Filter"; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.okapi.common.markupfilter.BaseMarkupFilter#normalizeName(java.
	 * lang.String)
	 */
	@Override
	protected String normalizeAttributeName(String attrName, String attrValue, Tag tag) {
		// normalize values for HTML
		String normalizedName = attrName;

		// <meta http-equiv="Content-Type"
		// content="text/html; charset=ISO-2022-JP">
		if (tag.getName().equals("meta") && attrName.equals(HtmlEncoder.CONTENT)) {
			StartTag st = (StartTag) tag;
			if (st.getAttributeValue("http-equiv") != null) {
				if (st.getAttributeValue("http-equiv").equals("Content-Type")) {
					normalizedName = HtmlEncoder.NORMALIZED_ENCODING;
					return normalizedName;
				}
			}
		}

		// <meta http-equiv="Content-Language" content="en"
		if (tag.getName().equals("meta") && attrName.equals(HtmlEncoder.CONTENT)) {
			StartTag st = (StartTag) tag;
			if (st.getAttributeValue("http-equiv") != null) {
				if (st.getAttributeValue("http-equiv").equals("Content-Language")) {
					normalizedName = HtmlEncoder.NORMALIZED_LANGUAGE;
					return normalizedName;
				}
			}
		}

		return normalizedName;
	}
}
