/*===========================================================================
  Copyright (C) 2009-2013 by the Okapi Framework contributors
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

package net.sf.okapi.lib.segmentation.opennlp;

import java.io.FileInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import net.sf.okapi.common.FileUtil;
import net.sf.okapi.common.ISegmenter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Range;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.Span;

public class OkapiMaxEntSegmenter implements ISegmenter {
	private static final String DEFAULT_MODEL_PATH = "net/sf/okapi/lib/segmentation/opennlp/sent";
	private SentenceDetectorME sentenceDetector;
	private TreeMap<Integer, Boolean> splits;
	private List<Integer> finalSplits;
	private ArrayList<Integer> starts;
	private ArrayList<Integer> ends;
	private SentenceModel model;
	private LocaleId locale;
	private Span[] sentencePositions;
	private boolean segmentSubFlows;
	private boolean includeStartCodes;
	private boolean includeEndCodes;
	private boolean includeIsolatedCodes;
	private boolean oneSegmentIncludesAll;
	private boolean trimLeadingWS;
	private boolean trimTrailingWS;
	private boolean trimCodes;

	public OkapiMaxEntSegmenter(URI modelPath, LocaleId locale) {
		reset();
		this.locale = locale;
		
		String mp;
		if (modelPath == null) {
			// find a locale based default model
			mp = FileUtil.getLocaleBasedFile(DEFAULT_MODEL_PATH, "bin", locale);
			if (mp == null) {
				throw new OkapiIOException(
						"Cannot find default OpenNLP sentence breaking model for: "
								+ locale.toString());
			}
		} else {
			mp = modelPath.getPath();
		}

		try {
			model = new SentenceModel(new FileInputStream(mp));
			this.sentenceDetector = new SentenceDetectorME(model,
					new OkapiSentenceDetectorFactory(locale));
		} catch (Exception e) {
			throw new OkapiIOException(
					"Error loading OpenNLP sentence breaking model: " + mp, e);
		}
	}

	/**
	 * Resets the options to their defaults, and the compiled rules to nothing.
	 */
	public void reset() {
		segmentSubFlows = true; // SRX default
		includeStartCodes = false; // SRX default
		includeEndCodes = true; // SRX default
		includeIsolatedCodes = false; // SRX default
		oneSegmentIncludesAll = false; // Extension
		trimLeadingWS = false; // Extension IN TEST (was true for StringInfo)
		trimTrailingWS = false; // Extension IN TEST (was true for StringInfo)
		trimCodes = false; // Extension IN TEST (was false for StringInfo) NOT
							// USED for now
	}

	/**
	 * Sets the options for this segmenter.
	 * 
	 * @param segmentSubFlows
	 *            true to segment sub-flows, false to no segment them.
	 * @param includeStartCodes
	 *            true to include start codes just before a break in the 'left'
	 *            segment, false to put them in the next segment.
	 * @param includeEndCodes
	 *            true to include end codes just before a break in the 'left'
	 *            segment, false to put them in the next segment.
	 * @param includeIsolatedCodes
	 *            true to include isolated codes just before a break in the
	 *            'left' segment, false to put them in the next segment.
	 * @param oneSegmentIncludesAll
	 *            true to include everything in segments that are alone.
	 * @param trimLeadingWS
	 *            true to trim leading white-spaces from the segments, false to
	 *            keep them.
	 * @param trimTrailingWS
	 *            true to trim trailing white-spaces from the segments, false to
	 *            keep them.
	 */
	public void setOptions(boolean segmentSubFlows, boolean includeStartCodes,
			boolean includeEndCodes, boolean includeIsolatedCodes,
			boolean oneSegmentIncludesAll, boolean trimLeadingWS,
			boolean trimTrailingWS) {
		this.segmentSubFlows = segmentSubFlows;
		this.includeStartCodes = includeStartCodes;
		this.includeEndCodes = includeEndCodes;
		this.includeIsolatedCodes = includeIsolatedCodes;
		this.oneSegmentIncludesAll = oneSegmentIncludesAll;
		this.trimLeadingWS = trimLeadingWS;
		this.trimTrailingWS = trimTrailingWS;
	}

	@Override
	public int computeSegments(String text) {
		TextContainer tmp = new TextContainer(text);
		return computeSegments(tmp);
	}

	@Override
	public int computeSegments(TextContainer container) {
		splits = new TreeMap<Integer, Boolean>();
		
		// Do we have codes?
		// Avoid to create an un-segmented copy if we can
		boolean hasCode;
		if (container.contentIsOneSegment()) {
			hasCode = container.getSegments().getFirstContent().hasCode();
		} else {
			hasCode = container.getUnSegmentedContentCopy().hasCode();
		}
		String codedText = container.getCodedText();

		// Set the flag for trimming or not the in-line codes
		boolean isSCWS = (trimCodes ? !includeStartCodes : false);
		boolean isECWS = (trimCodes ? !includeEndCodes : false);
		boolean isICWS = (trimCodes ? !includeIsolatedCodes : false);

		sentencePositions = sentenceDetector.sentPosDetect(codedText);
		int i = 0;
		for (i = 0; i < sentencePositions.length; i++) {
			splits.put(sentencePositions[i].getEnd(), true);
			if (((i+1) < sentencePositions.length) && 
					(sentencePositions[i+1].getStart() - sentencePositions[i].getEnd() > 0)) {
				splits.put(sentencePositions[i+1].getEnd() - sentencePositions[i].getStart(), false);
			}
		}
		// handle trailing non-segment characters
		if (codedText.length() - sentencePositions[i-1].getEnd() > 0) {
			splits.put(codedText.length(), false);
		}

		// Adjust the split positions for in-line codes inclusion/exclusion
		// options
		// And create the list of final splits at the same time
		finalSplits = new ArrayList<Integer>();
		if (hasCode) { // Do this only if we have in-line codes
			int finalPos;
			boolean done;
			for (int pos : splits.keySet()) {
				if (!splits.get(pos))
					continue; // Skip non-break positions
				// Walk back through all sequential codes before the break
				finalPos = pos;
				done = false;
				while ((finalPos > 1) && !done) {
					switch (codedText.charAt(finalPos - 2)) {
					case (char) TextFragment.MARKER_OPENING:
					case (char) TextFragment.MARKER_CLOSING:
					case (char) TextFragment.MARKER_ISOLATED:
						finalPos -= 2;
						break;
					default:
						done = true;
						break;
					}
				}
				
				// Now finalPos points to the first code in the sequence before
				// the break.
				// Check, from that point to the break, if the break needs to
				// change position
				done = false;
				while ((finalPos < pos) && !done) {
					switch (codedText.charAt(finalPos)) {
					case (char) TextFragment.MARKER_OPENING:
						if (includeStartCodes)
							finalPos += 2;
						else
							done = true;
						break;
					case (char) TextFragment.MARKER_CLOSING:
						if (includeEndCodes)
							finalPos += 2;
						else
							done = true;
						break;
					case (char) TextFragment.MARKER_ISOLATED:
						if (includeIsolatedCodes)
							finalPos += 2;
						else
							done = true;
						break;
					default:
						done = true;
						break;
					}
				}
				// Store the updated position
				finalSplits.add(finalPos);
			}
		} else { // Just copy the real splits
			for (int pos : splits.keySet()) {
				if (splits.get(pos))
					finalSplits.add(pos);
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
		for (int pos : finalSplits) {
			// Trim white-spaces and codes as required at the front
			trimmedTextStart = TextFragment.indexOfFirstNonWhitespace(
					codedText, textStart, pos - 1, isSCWS, isECWS, isICWS,
					trimLeadingWS);
			if (trimmedTextStart == -1) { // pos-1 ) {
				// Only spaces in the segment: Continue with the next position
				continue;
			}
			if (trimLeadingWS || trimCodes)
				textStart = trimmedTextStart;
			// Trim white-spaces and codes as required at the back
			if (trimTrailingWS || trimCodes) {
				textEnd = TextFragment.indexOfLastNonWhitespace(codedText,
						pos - 1, 0, isSCWS, isECWS, isICWS, trimTrailingWS);
			} else
				textEnd = pos - 1;
			if (textEnd >= textStart) { // Only if there is something // was >
										// only
				if (textEnd < pos)
					textEnd++; // Adjust for +1 position
				starts.add(textStart);
				ends.add(textEnd);
			}
			textStart = pos;
		}
		// Last one
		int lastPos = codedText.length();
		if (textStart < lastPos) {
			// Trim white-spaces and codes as required at the front
			trimmedTextStart = TextFragment.indexOfFirstNonWhitespace(
					codedText, textStart, lastPos - 1, isSCWS, isECWS, isICWS,
					trimLeadingWS);
			if (trimLeadingWS || trimCodes) {
				if (trimmedTextStart != -1)
					textStart = trimmedTextStart;
			}
			if ((trimmedTextStart != -1) && (trimmedTextStart < lastPos)) {
				// Trim white-spaces and code as required at the back
				if (trimTrailingWS || trimCodes) {
					textEnd = TextFragment.indexOfLastNonWhitespace(codedText,
							lastPos - 1, textStart, isSCWS, isECWS, isICWS,
							trimTrailingWS);
				} else
					textEnd = lastPos - 1;
				if (textEnd >= textStart) { // Only if there is something
					if (textEnd < lastPos)
						textEnd++; // Adjust for +1 position
					starts.add(textStart);
					ends.add(textEnd);
				}
			}
		}

		// Check for single-segment text case
		if ((starts.size() == 1) && (oneSegmentIncludesAll)) {
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

	@Override
	public Range getNextSegmentRange(TextContainer container) {
		return null;
	}

	@Override
	public List<Integer> getSplitPositions() {
		if ( finalSplits == null ) {
			finalSplits = new ArrayList<Integer>();
		}
		return Collections.unmodifiableList(finalSplits);
	}

	@Override
	public List<Range> getRanges() {
		ArrayList<Range> list = new ArrayList<Range>();
		if ( starts == null ) return null;
		for ( int i=0; i<starts.size(); i++ ) {
			list.add(new Range(starts.get(i), ends.get(i)));
		}
		return Collections.unmodifiableList(list);
	}

	@Override
	public LocaleId getLanguage() {
		return locale;
	}

	/**
	 * Indicates if, when there is a single segment in a text, it should include
	 * the whole text (no spaces or codes trim left/right)
	 * 
	 * @return true if a text with a single segment should include the whole
	 *         text.
	 */
	public boolean oneSegmentIncludesAll() {
		return oneSegmentIncludesAll;
	}

	/**
	 * Indicates if sub-flows must be segmented.
	 * 
	 * @return true if sub-flows must be segmented, false otherwise.
	 */
	public boolean segmentSubFlows() {
		return segmentSubFlows;
	}

	/**
	 * Indicates if leading white-spaces should be left outside the segments.
	 * 
	 * @return true if the leading white-spaces should be trimmed.
	 */
	public boolean trimLeadingWhitespaces() {
		return trimLeadingWS;
	}

	/**
	 * Indicates if trailing white-spaces should be left outside the segments.
	 * 
	 * @return true if the trailing white-spaces should be trimmed.
	 */
	public boolean trimTrailingWhitespaces() {
		return trimTrailingWS;
	}

	/**
	 * Indicates if start codes should be included (See SRX implementation
	 * notes).
	 * 
	 * @return true if they should be included, false otherwise.
	 */
	public boolean includeStartCodes() {
		return includeStartCodes;
	}

	/**
	 * Indicates if end codes should be included (See SRX implementation notes).
	 * 
	 * @return true if they should be included, false otherwise.
	 */
	public boolean includeEndCodes() {
		return includeEndCodes;
	}

	/**
	 * Indicates if isolated codes should be included (See SRX implementation
	 * notes).
	 * 
	 * @return true if they should be included, false otherwise.
	 */
	public boolean includeIsolatedCodes() {
		return includeIsolatedCodes;
	}

	public void setSegmentSubFlows(boolean segmentSubFlows) {
		this.segmentSubFlows = segmentSubFlows;
	}

	public void setIncludeStartCodes(boolean includeStartCodes) {
		this.includeStartCodes = includeStartCodes;
	}

	public void setIncludeEndCodes(boolean includeEndCodes) {
		this.includeEndCodes = includeEndCodes;
	}

	public void setIncludeIsolatedCodes(boolean includeIsolatedCodes) {
		this.includeIsolatedCodes = includeIsolatedCodes;
	}

	public void setOneSegmentIncludesAll(boolean oneSegmentIncludesAll) {
		this.oneSegmentIncludesAll = oneSegmentIncludesAll;
	}

	public void setTrimLeadingWS(boolean trimLeadingWS) {
		this.trimLeadingWS = trimLeadingWS;
	}

	public void setTrimTrailingWS(boolean trimTrailingWS) {
		this.trimTrailingWS = trimTrailingWS;
	}

	public void setTrimCodes(boolean trimCodes) {
		this.trimCodes = trimCodes;
	}
}
