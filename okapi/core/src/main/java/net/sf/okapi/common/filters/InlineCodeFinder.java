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

package net.sf.okapi.common.filters;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.sf.okapi.common.ParametersString;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;

/**
 * Implements the methods needed to convert sections of a coded text
 * into in-line codes.
 */
public class InlineCodeFinder {

	/**
	 * Type representing an inline code created with this class.
	 */
	public static final String TAGTYPE = "regxph";
	
	private ArrayList<String> rules;
	private String sample;
	private boolean useAllRulesWhenTesting;
	private Pattern pattern;
	
	/**
	 * Creates a new InlineCodeFinder object.
	 */
	public InlineCodeFinder () {
		reset();
	}

	/**
	 * Resets this finder.
	 */
	public void reset () {
		rules = new ArrayList<String>();
		sample = "";
		useAllRulesWhenTesting = false;
	}
	
	/**
	 * Clones this finder.
	 * @return A new InlineCodeFinder object that is a copy of this one.
	 */
	@Override
	public InlineCodeFinder clone () {
		InlineCodeFinder tmp = new InlineCodeFinder();
		tmp.setSample(sample);
		tmp.setUseAllRulesWhenTesting(useAllRulesWhenTesting);
		tmp.getRules().addAll(getRules());
		return tmp;
	}

	/**
	 * Adds a new rule to the list.
	 * @param pattern The regular expression pattern for the rule.
	 */
	public void addRule (String pattern) {
		rules.add(pattern);
	}

	/**
	 * Gets a list of all rules.
	 * @return The list of all rules.
	 */
	public ArrayList<String> getRules () {
		return rules;
	}
	
	/**
	 * Gets the sample text to go with the finder.
	 * @return The sample text.
	 */
	public String getSample () {
		return sample;
	}
	
	/**
	 * Sets the sample text that can be used to check the rules
	 * in a regular expression editor.
	 * @param value The sample text.
	 */
	public void setSample (String value) {
		sample = value;
	}
	
	/**
	 * Indicates if all rules should be used when testing the patterns
	 * in a regular expression editor.
	 * @return True if all rules should be used when testing,
	 * false when only the current rule should be used.
	 */
	public boolean useAllRulesWhenTesting () {
		return useAllRulesWhenTesting;
	}
	
	/**
	 * Set the flag that indicates if all rules should be used when testing the patterns
	 * in a regular expression editor.
	 * @param value True to use all rules, false to use only the current rule.
	 */
	public void setUseAllRulesWhenTesting (boolean value) {
		useAllRulesWhenTesting = value;
	}

	/**
	 * Compiles all the rules into a single compiled pattern.
	 * @throws PatternSyntaxException When there is a syntax error in one of the rules. 
	 */
	public void compile () {
		StringBuilder tmp = new StringBuilder();
		for ( String rule : rules ) {
			if ( tmp.length() > 0 ) tmp.append("|");
			tmp.append("("+rule+")");
		}
		if ( tmp.length() == 2 )
			pattern = Pattern.compile("");
		else
			pattern = Pattern.compile(tmp.toString(), Pattern.MULTILINE);
	}

	/**
	 * Applies the rules to a given content and converts all matching sections
	 * into in-line codes. The new codes have the type {@link #TAGTYPE}.
	 * <p>Note that the data of the new may need to be escaped as they are now part of the
	 * fragment skeleton and are not escaped back to the original format when merging.
	 * @param fragment The fragment where to apply the rules.
	 */
	public void process (TextFragment fragment) {
		if ( pattern.pattern().length() == 0 ) return;
		String tmp = fragment.getCodedText();
		Matcher m = pattern.matcher(tmp);
		int start = 0;
		int diff = 0;
		while ( m.find(start) ) {
			diff += fragment.changeToCode(m.start()+diff, m.end()+diff,
				TagType.PLACEHOLDER, TAGTYPE);
			start = m.end();
			// Check the case where the last match was at the end
			// which makes the next start invalid for find().
			if ( start >= tmp.length() ) break;
		}
	}

	/**
	 * Gets a string of all the options for this finder.
	 * @return The string storing all the options for this finder.
	 * @see #fromString(String)
	 */
	@Override
	public String toString () {
		ParametersString tmp = new ParametersString();
		tmp.setInteger("count", rules.size());
		int i = 0;
		for ( String rule : rules ) {
			tmp.setString(String.format("rule%d", i), rule);
			i++;
		}
		tmp.setString("sample", sample);
		tmp.setBoolean("useAllRulesWhenTesting", useAllRulesWhenTesting);
		return tmp.toString();
	}

	/**
	 * Sets the options of this finder with the values stored in a given string.
	 * The provided string can be created by {@link #toString()}.
	 * @param data String storing all the options for this finder.
	 * @see #toString() 
	 */
	public void fromString (String data) {
		reset();
		ParametersString tmp = new ParametersString(data);
		int count = tmp.getInteger("count", 0);
		for ( int i=0; i<count; i++ ) {
			String rule = tmp.getString(String.format("rule%d", i), "");
			if ( rule.length() > 0 ) rules.add(rule);
		}
		sample = tmp.getString("sample", sample);
		useAllRulesWhenTesting = tmp.getBoolean("useAllRulesWhenTesting", useAllRulesWhenTesting);
	}

}
