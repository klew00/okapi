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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.htmlparser.jericho.Attribute;
import net.sf.okapi.common.groovy.IllegalConditionalAttributeMatchTypeException;

/**
 * A conditional attribute is used to test extraction of elements or attributes.
 * Conditional rules may also hold meta data to be based on to extraction items.
 * Rules are evaluated in context which means the values passed in may will be
 * interpreteted as elements or attributes based on the context of evaluation.
 */
public class ConditionalAttributeRule {

	public static enum CONDITIONAL_ATTRIBUTE_TYPE {
		ALWAYS_EQUALS, EQUALS, NOT_EQUALS, MATCH
	};

	private String ruleAttributeName;
	private String ruleAttributeValue;
	private CONDITIONAL_ATTRIBUTE_TYPE matchType;
	private Pattern matchPattern;

	public ConditionalAttributeRule(String ruleAttributeName) {
		this.ruleAttributeName = ruleAttributeName;
		this.ruleAttributeValue = null;
		this.matchType = CONDITIONAL_ATTRIBUTE_TYPE.ALWAYS_EQUALS;
	}

	/**
	 * Create a conditional rule with the following values.
	 * 
	 */
	public ConditionalAttributeRule(String ruleAttributeName,
			CONDITIONAL_ATTRIBUTE_TYPE matchType, String ruleAttributeValue) {		
		this.ruleAttributeName = ruleAttributeName;
		this.ruleAttributeValue = ruleAttributeValue;
		this.matchType = matchType;

		if (this.matchType == CONDITIONAL_ATTRIBUTE_TYPE.MATCH) {
			matchPattern = Pattern.compile(ruleAttributeValue);
		}
	}

	public boolean applyCondition(Attribute currentAttribute) {
		// if this current attribute doesn't have a value no rule will fire
		if (!currentAttribute.hasValue()) {
			return false;
		}

		String currentAttributeName = currentAttribute.getName();
		String currentAttributeValue = currentAttribute.getValue();

		// does the attribute name match?
		if (!currentAttributeName.equalsIgnoreCase(ruleAttributeName)) {
			return false;
		}

		// apply conditional rules if any
		switch (matchType) {
		case ALWAYS_EQUALS:
			return true;
		case EQUALS:
			return currentAttributeValue.equalsIgnoreCase(ruleAttributeValue);
		case NOT_EQUALS:
			return !(currentAttributeValue.equalsIgnoreCase(ruleAttributeValue));
		case MATCH:
			boolean result = false;			
			try {
				Matcher m = matchPattern.matcher(currentAttributeValue);
				result = m.matches();
			} catch (PatternSyntaxException e) {
				throw new IllegalConditionalAttributeMatchTypeException(e);
			}
			return result;
		default:
			throw new IllegalConditionalAttributeMatchTypeException(
					"Unkown match type: " + matchType.toString());
		}
	}
}
