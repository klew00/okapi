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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;

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
	private LinkedHashMap<Integer, Boolean> splits;
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
	 * @param original The object to segment.
	 * @return The number of segment found.
	 */
	public int computeSegments (TextContainer original) {
		if ( currentLanguageCode == null ) {
			// Need to call selectLanguageRule()
			throw new RuntimeException("No language defined for the segmeter.");
		}
		
		// Remove any existing segmentation
		//TODO: Handle case to allow secondary segmentation (segment the segments)
		//original.joinParts();
		
		// Build the list of split positions
		String codedText = original.getCodedText();
		splits = new LinkedHashMap<Integer, Boolean>();
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
				while ( true ) {
					if ( textStart == pos ) break;
					if ( Character.isSpaceChar(codedText.charAt(textStart)) ) textStart++;
					else break;
				}
				if ( textStart == pos ) {
					// Only spaces in the segment: Continue with the next position
					continue;
				}
				textEnd = pos;
				boolean done = false;
				while ( !done ) {
					switch ( codedText.charAt(textEnd) ) {
					case TextFragment.MARKER_OPENING:
						if ( includeStartCodes ) {
							textEnd += 2; // Includes the marker index too
							done = true;
						}
						break;
					case TextFragment.MARKER_CLOSING:
						if ( includeEndCodes ) {
							textEnd += 2; // Includes the marker index too
							done = true;
						}
						break;
					case TextFragment.MARKER_ISOLATED:
						if ( includeIsolatedCodes ) {
							textEnd += 2; // Includes the marker index too
							done = true;
						}
						break;
					default:
						if ( Character.isSpaceChar(codedText.charAt(textEnd)) ) break;
						done = true; // Probably done
						// Else: Check if it's the index of a marker
						if ( textEnd > 1 ) {
							switch ( codedText.charAt(textEnd-1) ) {
							case TextFragment.MARKER_OPENING:
							case TextFragment.MARKER_CLOSING:
							case TextFragment.MARKER_ISOLATED:
								done = false;
								break;
							}
						}
						break;
					}
					if ( !done ) {
						if ( --textEnd < 0 ) break;
					}
				}
				if ( textEnd < pos ) textEnd++; // Adjust for +1 position
				starts.add(textStart);
				ends.add(textEnd);
				textStart = pos;
			}
		}
		// Last one
		int lastPos = codedText.length();
		if ( textStart < lastPos ) {
			while ( true ) {
				if ( textStart == lastPos ) break;
				if ( Character.isSpaceChar(codedText.charAt(textStart)) ) textStart++;
				else break;
			}
			if ( textStart < lastPos ) {
				textEnd = lastPos-1;
				boolean done = false;
				while ( !done ) {
					switch ( codedText.charAt(textEnd) ) {
					case TextFragment.MARKER_OPENING:
						if ( includeStartCodes ) {
							textEnd += 2; // Includes the marker index too
							done = true;
						}
						break;
					case TextFragment.MARKER_CLOSING:
						if ( includeEndCodes ) {
							textEnd += 2; // Includes the marker index too
							done = true;
						}
						break;
					case TextFragment.MARKER_ISOLATED:
						if ( includeIsolatedCodes ) {
							textEnd += 2; // Includes the marker index too
							done = true;
						}
						break;
					default:
						if ( Character.isSpaceChar(codedText.charAt(textEnd)) ) break;
						done = true; // Probably done
						// Else: Check if it's the index of a marker
						if ( textEnd > 1 ) {
							switch ( codedText.charAt(textEnd-1) ) {
							case TextFragment.MARKER_OPENING:
							case TextFragment.MARKER_CLOSING:
							case TextFragment.MARKER_ISOLATED:
								done = false;
								break;
							}
						}
						break;
					}
					if ( !done ) {
						if ( --textEnd < 0 ) break;
					}
				}
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
	 * Segments a given TextContainer object.
	 * @param original The container to segment.
	 * @return The same container passed as parameter, but now segmented if needed.
	 */
	public TextContainer segment (TextContainer original) {
		if ( computeSegments(original) < 2 ) {
			// No more than 1 segment
			return original;
		}
		
		// Otherwise we have at least two segments:
		// Build a temporary list of these segments
		ArrayList<TextFragment> newParts = new ArrayList<TextFragment>();
		int start = 0;
		int textStart;
		TextFragment tc;
		// Note: Always drive with starts (as ends has one extra value)
		int i;
		for ( i=0; i<starts.size(); i++ ) {
			textStart = starts.get(i);
			if ( start < textStart ) {
				newParts.add(original.subSequence(start, textStart));
				//newParts.get(newParts.size()-1).setIsSegment(false);
			}
			tc = original.subSequence(textStart, ends.get(i));
			//TODO: Change for real markup, this is for test 
			tc.setCodedText("["+tc.getCodedText()+"]");
			newParts.add(tc);
			start = ends.get(i);
		}
		// The last extra value in ends contains the coded text length
		// We use it here to add the possible last non-segment part.
		if ( start < ends.get(i) ) {
			newParts.add(original.subSequence(start, ends.get(i)));
			//newParts.get(newParts.size()-1).setIsSegment(false);
		}
		
		// And rebuild the original container, this time segmented
		original.clear();
		for ( TextFragment part : newParts ) {
			//TODO: handle segmentation setting
			original.append(part);
		}
		return original;
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
