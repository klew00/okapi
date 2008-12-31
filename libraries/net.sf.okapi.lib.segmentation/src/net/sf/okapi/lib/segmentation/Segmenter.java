/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
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
============================================================================*/

package net.sf.okapi.lib.segmentation;

import java.awt.Point;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.okapi.common.Range;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;

public class Segmenter {
	
	private boolean segmentSubFlows;
	private boolean cascade;
	private boolean includeStartCodes;
	private boolean includeEndCodes;
	private boolean includeIsolatedCodes;
	private String currentLanguageCode;
	private boolean oneSegmentIncludesAll;
	private boolean trimLeadingWS;
	private boolean trimTrailingWS;
	private boolean trimCodes;
	private ArrayList<CompiledRule> rules;
	private Pattern maskRule;
	private TreeMap<Integer, Boolean> splits;
	private List<Integer> finalSplits;
	private ArrayList<Integer> starts;
	private ArrayList<Integer> ends;

	/**
	 * Creates a new segmenter object.
	 */
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
		maskRule = null;
		splits = null;
		segmentSubFlows = true; // SRX default
		cascade = false; // There is no SRX default for this
		includeStartCodes = false; // SRX default
		includeEndCodes = true; // SRX default
		includeIsolatedCodes = false; // SRX default
		oneSegmentIncludesAll = false; // Extension
		trimLeadingWS = false; // Extension IN TEST (was true for StringInfo)
		trimTrailingWS = false; // Extension IN TEST (was true for StringInfo)
		trimCodes = false; // Extension IN TEST (was false for StringInfo) NOT USED for now
	}

	public void setOptions (boolean segmentSubFlows,
		boolean includeStartCodes,
		boolean includeEndCodes,
		boolean includeIsolatedCodes,
		boolean oneSegmentIncludesAll,
		boolean trimLeadingWS,
		boolean trimTrailingWS)
	{
		this.segmentSubFlows = segmentSubFlows;
		this.includeStartCodes = includeStartCodes;
		this.includeEndCodes = includeEndCodes;
		this.includeIsolatedCodes = includeIsolatedCodes;
		this.oneSegmentIncludesAll = oneSegmentIncludesAll;
		this.trimLeadingWS = trimLeadingWS;
		this.trimTrailingWS = trimTrailingWS;
	}
	
	public boolean oneSegmentIncludesAll () {
		return oneSegmentIncludesAll;
	}

	public boolean segmentSubFlows () {
		return segmentSubFlows;
	}
	
	public boolean cascade () {
		return cascade;
	}
	
	public boolean trimLeadingWhitespaces () {
		return trimLeadingWS;
	}
	
	public boolean trimTrailingWhitespaces () {
		return trimTrailingWS;
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
		TextContainer tmp = new TextContainer(text);
		return computeSegments(tmp);
	}
	
	/**
	 * Calculates the segmentation of a given IContainer object. The assumption is that the container
	 * is not segmented yet.
	 * @param container The object to segment.
	 * @return The number of segment found.
	 */
	public int computeSegments (TextContainer container) {
		if ( currentLanguageCode == null ) {
			// Need to call selectLanguageRule()
			throw new RuntimeException("No language defined for the segmeter.");
		}
		if ( container.isSegmented() ) {
			// Assumes the text is not already segmented
			throw new RuntimeException("Text already segmented.");
		}
		
		// Set the flag for trimming or not the in-line codes
		boolean isSCWS = (trimCodes ? !includeStartCodes : false); 
		boolean isECWS = (trimCodes ? !includeEndCodes : false); 
		boolean isICWS = (trimCodes ? !includeIsolatedCodes : false); 

		// Build the list of split positions
		String codedText = container.getCodedText();
		splits = new TreeMap<Integer, Boolean>();
		Matcher m;
		for ( CompiledRule rule : rules ) {
			m = rule.pattern.matcher(codedText);
			while ( m.find() ) {
				int n = m.start()+m.group(1).length();
				if ( n >= codedText.length() ) continue; // Match the end
				// Already a match: Per SRX algorithm, we use the first one only
				if ( splits.containsKey(n) ) continue;
				// Else add a split marker
				splits.put(n, rule.isBreak);
			}
		}
		
		// Set the additional split positions for mask-rules
		if ( maskRule != null ) {
			m = maskRule.matcher(codedText);
			while ( m.find() ) {
				// Remove any existing marker inside the range
				for ( int n=m.start(); n<m.end(); n++ ) {
					if ( splits.containsKey(n) ) {
						splits.remove(n);
					}
				}
				// Then set the start and end of the range as breaks
				// Don't include a split at 0 because it's an implicit one
				if ( m.start() > 0 ) splits.put(m.start(), true);
				splits.put(m.end(), true);
			}
		}

		
		// Adjust the split positions for in-line codes inclusion/exclusion options
		// And create the list of final splits at the same time
		finalSplits = new ArrayList<Integer>();
		if ( container.hasCode() ) { // Do this only if we have in-line codes
			int finalPos;
			boolean done;
			for ( int pos : splits.keySet() ) {
				if ( !splits.get(pos) ) continue; // Skip non-break positions
				done = false;
				finalPos = pos;
				while (( finalPos > 1 ) && !done ) {
					switch ( codedText.charAt(finalPos-2) ) {
					case (char)TextFragment.MARKER_OPENING:
						if ( !includeStartCodes ) finalPos-=2;
						else done = true;
						break;
					case (char)TextFragment.MARKER_CLOSING:
						if ( !includeEndCodes ) finalPos-=2;
						else done = true;
						break;
					case (char)TextFragment.MARKER_ISOLATED:
						if ( !includeIsolatedCodes ) finalPos-=2;
						else done = true;
						break;
					default:
						done = true;
						break;
					}
				}
				finalSplits.add(finalPos);
			}
		}
		else { // Just copy the real splits
			for ( int pos : splits.keySet() ) {
				if ( splits.get(pos) ) finalSplits.add(pos);
			}
		}
		
		
		// Now build the lists of start and end of each segment
		// but trim them of any white-spaces.
		// Deal also with including or not the in-line codes.
		starts = new ArrayList<Integer>();
		ends = new ArrayList<Integer>();
		int textEnd;
		int textStart = 0;
		int trimmedTextStart;
		for ( int pos : finalSplits ) {
			// Trim white-spaces and codes as required at the front
			trimmedTextStart = TextFragment.getFirstNonWhitespacePosition(codedText,
				textStart, pos-1, isSCWS, isECWS, isICWS, trimLeadingWS);
			if ( trimmedTextStart == pos-1 ) {
				// Only spaces in the segment: Continue with the next position
				continue;
			}
			if ( trimLeadingWS || trimCodes ) textStart = trimmedTextStart;
			// Trim white-spaces and codes as required at the back
			if ( trimTrailingWS || trimCodes ) {
				textEnd = TextFragment.getLastNonWhitespacePosition(codedText,
					pos-1, 0, isSCWS, isECWS, isICWS, trimTrailingWS);
			}
			else textEnd = pos-1;
			if ( textEnd > textStart ) { // Only if there is something
				if ( textEnd < pos ) textEnd++; // Adjust for +1 position
				starts.add(textStart);
				ends.add(textEnd);
			}
			textStart = pos;
		}
		// Last one
		int lastPos = codedText.length();
		if ( textStart < lastPos ) {
			// Trim white-spaces and codes as required at the front
			trimmedTextStart = TextFragment.getFirstNonWhitespacePosition(codedText, textStart,
				lastPos-1, isSCWS, isECWS, isICWS, trimLeadingWS);
			if ( trimLeadingWS || trimCodes  ) textStart = trimmedTextStart;
			if ( trimmedTextStart < lastPos ) {
				// Trim white-spaces and code as required at the back
				if ( trimTrailingWS || trimCodes ) {
					textEnd = TextFragment.getLastNonWhitespacePosition(codedText, lastPos-1,
						textStart, isSCWS, isECWS, isICWS, trimTrailingWS);
				}
				else textEnd = lastPos-1;
				//TODO: fix case of last segment is single letter char surrounded by WS 
				if ( textEnd > textStart ) { // Only if there is something
					if ( textEnd < lastPos ) textEnd++; // Adjust for +1 position
					starts.add(textStart);
					ends.add(textEnd);
				}
			}
		}

		// Check for single-segment text case
		if (( starts.size() == 1 ) && ( oneSegmentIncludesAll )) {
			starts.set(0, 0);
			ends.clear(); // lastPos is added just after
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
		if ( container.isSegmented() ) {
			// Find the last segment marker in the main coded text
			for ( int i=text.length()-1; i>=0; i-- ) {
				if ( text.charAt(i) == TextContainer.MARKER_SEGMENT ) {
					start = i+2; // Just after the marker
					break;
				}
			}
		}

		// Do we have reach the end?
		if ( start >= text.length() ) return null;

		//TODO: implement same trimming at computeSegments()		
		
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
//TODO: implement same trimming at computeSegments()		
		// Trim the white-spaces at the front of the segment
		while ( true ) {
			if ( start > end ) break;
			if ( Character.isWhitespace(text.charAt(start)) ) start++;
			else break;
		}

		// Trim the white-spaces and required codes at the end of the segment
		end = TextFragment.getLastNonWhitespacePosition(text, end, start,
			!includeStartCodes, !includeEndCodes, !includeIsolatedCodes, trimTrailingWS);
		
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
	 * A split position is the first character position of a new segment.
	 * <p><b>IMPORTANT: The position returned here are the position WITHOUT taking in account the options
	 * for trimming or not leading and trailing white-spaces.</b>
	 * @return An array of integers where each value is a split position
	 * in the coded text that was segmented.
	 */
	public List<Integer> getSplitPositions () {
		
		if ( finalSplits == null ) {
			finalSplits = new ArrayList<Integer>();
		}
		return finalSplits;
	}

	/**
	 * Gets the list off all segments ranges calculated when
	 * calling {@link #computeSegments(String)}, or
	 * {@link #computeSegments(TextContainer)}.
	 * @return The list of all segments ranges. each range is stored in
	 * a {@link Range} object where start is the start and end the end of the range.
	 * Return null if no ranges have been defined yet.
	 */
	public List<Range> getSegmentRanges () {
		ArrayList<Range> list = new ArrayList<Range>();
		if ( starts == null ) return null;
		for ( int i=0; i<starts.size(); i++ ) {
			list.add(new Range(starts.get(i), ends.get(i)));
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
	
	protected void setMaskRule (String pattern) {
		if (( pattern != null ) && ( pattern.length() > 0 ))
			maskRule = Pattern.compile(pattern);
		else
			maskRule = null;
	}

}
