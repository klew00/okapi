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
import net.sf.okapi.common.filters.PropertyTextUnitPlaceholder;
import net.sf.okapi.common.markupfilter.MarkupFilter;
import net.sf.okapi.common.skeleton.GenericSkeleton;

public class HtmlFilter extends MarkupFilter {	
	public HtmlFilter() {
		super();
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
	protected void handleSkeleton(Tag tag) {
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

			if (getConfig().hasActionableAttributes(startTag.getName())) {
				propertyTextUnitPlaceholders = createPropertyTextUnitPlaceholders(startTag);
				startDocumentPart(startTag.toString(), startTag.getName(), propertyTextUnitPlaceholders);
				endDocumentPart();
			} else {
				// This shouldn't happen if rules are consistent
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
			startTextUnit(new GenericSkeleton(startTag.toString()));
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
	 * @see net.sf.okapi.common.filters.IFilter#getName()
	 */
	public String getName() {
		return "HTML Filter"; //$NON-NLS-1$
	}
}
