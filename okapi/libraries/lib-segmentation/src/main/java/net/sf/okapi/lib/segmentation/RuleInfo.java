/*===========================================================================
  Copyright (C) 2008-2012 by the Okapi Framework contributors
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

package net.sf.okapi.lib.segmentation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.okapi.common.Range;
import net.sf.okapi.common.RegexUtil;

class RuleInfo {
	
	private static final String WB_GROUP = "(.{0})";
	
	private static final String WB_STR = Character.toString(Placeholder.WORD_BOUNDARY);
	private static final Pattern WB = Pattern.compile(WB_STR);
	
	private static final String NON_WB_STR = Character.toString(Placeholder.WORD_NON_BOUNDARY);
	private static final Pattern NON_WB = Pattern.compile(NON_WB_STR);
	
	private static final Pattern G_PATTERN = Pattern.compile("\\\\G(.+)");
	
	// All positions are in the rule, not in text
	private List<Range> setAreas; // List of set areas ([abcd], [^abcd]) in the rule
	private List<Range> quotedAreas;
	private List<Integer> wbGroups; // Regex groups for \b in the rule (1 for \1 etc.)
	private List<Integer> nonWbGroups; // Regex groups for \B in the rule (1 for \1 etc.)
	private String rule;
	
	public RuleInfo(String rule) {		
		setAreas = new ArrayList<Range>(); 
		quotedAreas = new ArrayList<Range>();
		wbGroups = new ArrayList<Integer>();
		nonWbGroups = new ArrayList<Integer>();

		// Detect before and after parts
		int startBeforePos = rule.indexOf(Placeholder.START_BEFORE);
		rule = rule.replace(Character.toString(Placeholder.START_BEFORE), "");
		
		int endBeforePos = rule.indexOf(Placeholder.END_BEFORE);
		rule = rule.replace(Character.toString(Placeholder.END_BEFORE), "");
		
		int startAfterPos = rule.indexOf(Placeholder.START_AFTER);
		rule = rule.replace(Character.toString(Placeholder.START_AFTER), "");
		
		int endAfterPos = rule.indexOf(Placeholder.END_AFTER);
		rule = rule.replace(Character.toString(Placeholder.END_AFTER), "");
		
		// Process G-pattern in both parts (go RTL)
		rule = processGPattern(rule, startAfterPos, endAfterPos);
		rule = processGPattern(rule, startBeforePos, endBeforePos);		
		
		// Collect word boundary patterns
		Matcher m = WB.matcher(rule);
		while (m.find()) {
			int newGroup = RegexUtil.getGroupAtPos(rule, m.start()) + 1;
			wbGroups.add(newGroup);
			rule = RegexUtil.updateGroupReferences(rule, newGroup);
		}
		
		m = NON_WB.matcher(rule);
		while (m.find()) {
			int newGroup = RegexUtil.getGroupAtPos(rule, m.start()) + 1;
			nonWbGroups.add(newGroup);
			rule = RegexUtil.updateGroupReferences(rule, newGroup);
		}
		
		// Modify this.rule
		rule = rule.replace(WB_STR, WB_GROUP);
		rule = rule.replace(NON_WB_STR, WB_GROUP);
		
		// Determine areas between \Q and \E
		quotedAreas = RegexUtil.getQuotedAreas(rule);
		
		// Collect top level sets
		boolean ignoreNext = false;
		int level = 0;
		int start = 0;
		
		for (int i = 0; i < rule.length(); i++) {
			char ch = rule.charAt(i);
			switch (ch) {
			case '[':
				if (ignoreNext) {
					ignoreNext = false;
					continue;
				}
				if (level++ == 0) {
					start = i + 1;
				}
				break;

			case ']':
				if (ignoreNext) {
					ignoreNext = false;
					continue;
				}
				if (--level == 0) {
					setAreas.add(new Range(start, i - 1));
				}
				break;
				
			case '\\':
				if (!isQuotedArea(i)) 
					ignoreNext = true;
				break;
				
			default:
				ignoreNext = false;
				break;
			}			
		}
		
		this.rule = rule;
	}

	private String processGPattern(String rule, int startPos, int endPos) {
		if (startPos == -1 || endPos == -1) return rule;
		String part = rule.substring(startPos, endPos);
		Matcher m = G_PATTERN.matcher(part);
		if (m.find()) { // Only one \G is possible in the part
			// Replace \G with ()+
			String replacement = String.format("(%s)+", m.group(1));
			StringBuilder sb = new StringBuilder(rule);
			sb.replace(startPos + m.start(), endPos, replacement);
			rule = sb.toString();
			int newGroup = RegexUtil.getGroupAtPos(rule, m.start()) + 1;
			rule = RegexUtil.updateGroupReferences(rule, newGroup);
		}
		return rule;
	}

	public boolean isQuotedArea(int pos) {
		for (Range area : quotedAreas) {
			if (area.contains(pos))	return true;
		}
		return false;
	}
	
	public boolean isSetArea(int pos) {
		for (Range area : setAreas) {
			if (area.contains(pos))	return true;
		}
		return false;
	}
	
	public boolean hasWbPatterns() {
		return wbGroups.size() > 0 || nonWbGroups.size() > 0; 
	}

	public boolean verifyPos(int pos, Matcher matcher, List<Integer> boundaries) {
		// Verifies if word boundaries in the rule has matched
		if (!hasWbPatterns()) return true;
		
		int numGroups = matcher.groupCount();
		
		for (int groupNum : wbGroups) {
			if (groupNum >= numGroups) return true;
			int groupPos = matcher.start(groupNum); // matcher.start()
			if (!boundaries.contains(groupPos)) return false; // Should be a boundary
		}
		
		for (int groupNum : nonWbGroups) {
			if (groupNum >= numGroups) return true;
			int groupPos = matcher.start(groupNum);
			if (boundaries.contains(groupPos)) return false; // Should not be a boundary
		}
		
		return true;
	}

	public String getRule() {
		return rule;
	}
}
