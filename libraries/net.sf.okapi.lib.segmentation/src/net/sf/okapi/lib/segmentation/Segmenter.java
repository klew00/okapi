/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel                                          */
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
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.lib.segmentation;

import java.awt.Point;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.List;
import java.util.regex.Matcher;

import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;

public class Segmenter {
	
	private boolean     segmentSubFlows;
	private boolean     cascade;
	private boolean     includeStartCodes;
	private boolean     includeEndCodes;
	private boolean     includeIsolatedCodes;
	private String      currentLanguageCode;
	
	private ArrayList<CompiledRule>         rules;
	private TreeMap<Integer, Boolean>       splits;
	private ArrayList<Integer>              starts;
	private ArrayList<Integer>              ends;


	public Segmenter () {
		reset();
	}

	/**
	 * Reset the options to their defaults, and the compiled rules
	 * to nothing.
	 */
	public void reset () {
		currentLanguageCode = null;
		rules = new ArrayList<CompiledRule>();
		splits = null;
		segmentSubFlows = true; // SRX default
		cascade = false; // There is no SRX default for this
		includeStartCodes = false; // SRX default
		includeEndCodes = true; // SRX default
		includeIsolatedCodes = false; // SRX default
	}

	public void setOptions (boolean segmentSubFlows,
		boolean includeStartCodes,
		boolean includeEndCodes,
		boolean includeIsolatedCodes)
	{
		this.segmentSubFlows = segmentSubFlows;
		this.includeStartCodes = includeStartCodes;
		this.includeEndCodes = includeEndCodes;
		this.includeIsolatedCodes = includeIsolatedCodes;
	}
	
	public boolean segmentSubFlows () {
		return segmentSubFlows;
	}
	
	public boolean cascade () {
		return cascade;
	}
	
	/**
	 * Indicates if start codes should be included (See SRX implementation notes).
	 * @return True if they should be included, false otherwise.
	 */
	public boolean includeStartCodes () {
		return includeStartCodes;
	}
	
	/**
	 * Indicates if end codes should be included (See SRX implementation notes).
	 * @return True if they should be included, false otherwise.
	 */
	public boolean includeEndCodes () {
		return includeEndCodes;
	}
	
	/**
	 * Indicates if isolated codes should be included (See SRX implementation notes).
	 * @return True if they should be included, false otherwise.
	 */
	public boolean includeIsolatedCodes () {
		return includeIsolatedCodes;
	}
	
	/**
	 * Calculate the segmentation of a given plain text string.
	 * @param text Plain text to segment.
	 * @return The number of segment found.
	 */
	public int computeSegments (String text) {
		TextContainer tmp = new TextContainer(null);
		return computeSegments(tmp);
	}
	
	/**
	 * calculate the segmentation of a given IContainer object.
	 * @param container The object to segment.
	 * @return The number of segment found.
	 */
	public int computeSegments (TextContainer container) {
		if ( currentLanguageCode == null ) {
			// Need to call selectLanguageRule()
			throw new RuntimeException("No language defined for the segmeter.");
		}
		
		// Remove any existing segmentation
		//TODO: Handle case to allow secondary segmentation (segment the segments)
		//original.joinParts();
		
		// Build the list of split positions
		String codedText = container.getCodedText();
		splits = new TreeMap<Integer, Boolean>();
		Matcher m;
		for ( CompiledRule rule : rules ) {
			m = rule.pattern.matcher(codedText);
			while ( m.find() ) {
				int n = m.start()+m.group(1).length();
				if ( n >= codedText.length() ) continue; // Match the end
				if ( splits.containsKey(n) ) {
					// Do not update if we found a no-break before
					if ( !splits.get(n) ) continue;
				}
				// Add or update split
				splits.put(n, rule.isBreak);
			}
		}
		

		// Now build the lists of start and end of each segment
		// but trim them of any white-spaces.
		// Deal also with including or not the in-line codes.
		starts = new ArrayList<Integer>();
		ends = new ArrayList<Integer>();
		int textEnd;
		int textStart = 0;
		for ( int pos : splits.keySet() ) {
			if ( splits.get(pos) ) {
				// Trim white-spaces at the front
				while ( true ) {
					if ( textStart == pos ) break;
					if ( Character.isWhitespace(codedText.charAt(textStart)) ) textStart++;
					else break;
				}
				if ( textStart == pos ) {
					// Only spaces in the segment: Continue with the next position
					continue;
				}
				// Trim white-spaces and code as required at the back
				textEnd = TextFragment.getLastNonWhitespacePosition(codedText,
					pos-1, 0, !includeStartCodes, !includeEndCodes, !includeIsolatedCodes);
				if ( textEnd < pos ) textEnd++; // Adjust for +1 position
				starts.add(textStart);
				ends.add(textEnd);
				textStart = pos;
			}
		}
		// Last one
		int lastPos = codedText.length();
		if ( textStart < lastPos ) {
			// Trim white-spaces at the front
			while ( true ) {
				if ( textStart == lastPos ) break;
				if ( Character.isWhitespace(codedText.charAt(textStart)) ) textStart++;
				else break;
			}
			if ( textStart < lastPos ) {
				// Trim white-spaces and code as required at the back
				textEnd = TextFragment.getLastNonWhitespacePosition(codedText, lastPos-1,
					0, !includeStartCodes, !includeEndCodes, !includeIsolatedCodes);
				if ( textEnd < lastPos ) textEnd++; // Adjust for +1 position
				starts.add(textStart);
				ends.add(textEnd);
			}
		}
		// Add an extra value in ends to hold the total length of the coded text
		// to avoid having to re-create it when segmenting.
		ends.add(lastPos);
		
		// Return the number of segment found
		// (ends contains one extra value, so make sure to use starts for this)
		return starts.size();
	}
	
	/**
	 * Compute the range of the next segment for a given TextContainer content.
	 * The next segment is searched from the first character after the last
	 * segment marker found in the container.
	 * @param container The text container where to look for the next segment. 
	 * @return A range corresponding to the start and end position of the found
	 * segment, or null if no more segments are found.
	 */
	public Point getNextSegmentRange (TextContainer container) {
		String text = container.getCodedText();
		int start = 0;
		Code code;
		if ( container.isSegmented() ) {
			// Find the last segment marker in the main coded text
			for ( int i=text.length()-1; i>=0; i-- ) {
				if ( text.charAt(i) == TextContainer.MARKER_ISOLATED ) {
					code = container.getCode(text.charAt(i+1));
					if ( code.getType().equals(TextContainer.CODETYPE_SEGMENT) ) {
						start = i+2; // Just after the marker
						break;
					}
				}
			}
		}

		// Do we have reach the end?
		if ( start >= text.length() ) return null;
		
		// Else: search for next segment
		Matcher m;
		CompiledRule rule;
		int end = -1;
		int pos = start;
		for ( int i=0; i<rules.size(); i++ ) {
			rule = rules.get(i);
			m = rule.pattern.matcher(text.substring(pos));
			if ( m.find() ) {
				if ( !rule.isBreak ) {
					pos = pos+m.start()+m.group(1).length()+1;
					if ( pos == text.length() ) break;
					i = 0; // Look at all rules again
				}
				else {
					end = pos+m.start()+m.group(1).length();
					break;
				}
			}
		}

		// If not found: take all the remainder as the fragment
		if ( end < 0 ) end = text.length()-1;
		
		// Trim the white-spaces at the front of the segment
		while ( true ) {
			if ( start > end ) break;
			if ( Character.isWhitespace(text.charAt(start)) ) start++;
			else break;
		}

		// Trim the white-spaces and required codes at the end of the segment
		end = TextFragment.getLastNonWhitespacePosition(text, end, start,
			!includeStartCodes, !includeEndCodes, !includeIsolatedCodes);
		
		// Adjust for +1 position (it's a range)
		if ( end == -1 ) return null;
		else end++;

		// Return the range
		if ( start == end ) return null;
		return new Point(start, end);
	}

	/**
	 * Gets the list of all the split positions in the text
	 * that was last segmented. You must call {@link #computeSegment(IContainer)}
	 * or {@link #computeSegments(String)} before calling this method.
	 * @return An array of integers where each value is a split position
	 * in the coded text that was segmented. The split position between two segments
	 * is the first character position of the second segment.
	 */
	public List<Integer> getSplitPositions () {
		ArrayList<Integer> list = new ArrayList<Integer>();
		if ( splits == null ) return list;
		for ( int pos : splits.keySet() ) {
			if ( splits.get(pos) ) {
				list.add(pos);
			}
		}
		return list;
	}

	/**
	 * Gets the list off all segments ranges calculated when
	 * calling {@link #computeSegments(String)}, or
	 * {@link #computeSegments(TextContainer)}.
	 * @return The list of all segments ranges. each range is stored in
	 * a {@link Point} object where x is the start and y the end of the range.
	 */
	public List<Point> getSegmentRanges () {
		ArrayList<Point> list = new ArrayList<Point>();
		if ( starts == null ) return null;
		for ( int i=0; i<starts.size(); i++ ) {
			list.add(new Point(starts.get(i), ends.get(i)));
		}
		return list;
	}
	
	/**
	 * Gets the language used to apply the rules.
	 * @return The language code used to apply the rules, or null, if none is 
	 * specified yet.
	 */
	public String getLanguage () {
		return currentLanguageCode;
	}
	
	protected void setLanguage (String languageCode) {
		currentLanguageCode = languageCode;
	}
	
	protected void setCascade (boolean value) {
		cascade = value;
	}
	
	protected void addRule (CompiledRule compiledRule) {
		rules.add(compiledRule);
	}

}
