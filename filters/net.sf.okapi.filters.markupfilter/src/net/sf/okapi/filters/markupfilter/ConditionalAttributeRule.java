/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

package net.sf.okapi.filters.markupfilter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.htmlparser.jericho.Attribute;
import net.sf.okapi.filters.yaml.IllegalConditionalAttributeException;

/**
 * Defines a single conditional rule of the type
 * "If attribute is X and the attribute value is Y extract Y"
 * 
 * Exact match and regex comparisons are supported. Comparisons are case
 * insensitive.
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
	public ConditionalAttributeRule(String ruleAttributeName, CONDITIONAL_ATTRIBUTE_TYPE matchType,
			String ruleAttributeValue) {
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
				throw new IllegalConditionalAttributeException(e);
			}
			return result;
		default:
			throw new IllegalConditionalAttributeException("Unkown match type: " + matchType.toString());
		}
	}
}
