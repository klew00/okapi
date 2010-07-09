/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.lib.verification;

import java.io.File;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;

class QualityChecker {

	private LocaleId trgLoc;
	private List<PatternItem> patterns;
	private LanguageToolConnector ltConn;
	private Parameters params;
	private List<Issue> issues;
	private URI currentDocId;
	private List<String> sigList;
	private Pattern patDoubledWords;
	private CharsetEncoder encoder;
	private Pattern extraCharsAllowed;
	private Pattern corruption;

	void startProcess (LocaleId targetLocale,
		Parameters params,
		List<Issue> issues)
	{
		this.trgLoc = targetLocale;
		this.params = params;
		this.issues = issues;

		// Compile the patterns
		patterns = params.getPatterns();
		for ( PatternItem item : patterns ) {
			if ( item.enabled ) {
				item.compile();
			}
		}

		ltConn = null;
		if ( params.getCheckWithLT() ) {
			ltConn = new LanguageToolConnector();
			ltConn.initialize(targetLocale, params.getServerURL(), params.translateLTMsg,
				params.ltTranslationSource, params.ltTranslationTarget);
		}

		// Expression for finding doubled words
		// The expression: "\\b(\\w+)\\s+\\1\\b" does not work for extended chars (\w and \s are ASCII based)
		// We have to use the Unicode equivalents
		patDoubledWords = null;
		if ( params.getDoubledWord() ) {
			patDoubledWords = Pattern.compile("\\b([\\p{Ll}\\p{Lu}\\p{Lt}\\p{Lo}\\p{Nd}]+)[\\t\\n\\f\\r\\p{Z}]+\\1\\b",
				Pattern.CASE_INSENSITIVE);
		}
		corruption = null;
		if ( params.getCorruptedCharacters() ) {
			// Some of the most frequent patterns of corrupted characters
			corruption = Pattern.compile("\\u00C3[\\u00A4-\\u00B6]"
				+ "|\\u00C3\\u201E"
				+ "|\\u00C3\\u2026"
				+ "|\\u00C3\\u2013"
			);
		}
		
		// Characters check
		encoder = null;
		extraCharsAllowed = null;
		if ( params.getCheckCharacters() ) {
			// Encoding
			String charsetName = params.charset;
			if ( !Util.isEmpty(charsetName) ) {
				encoder = Charset.forName(charsetName).newEncoder();
			}
			// Extra characters allowed
			if ( !params.getExtraCharsAllowed().isEmpty() ) {
				extraCharsAllowed = Pattern.compile(params.getExtraCharsAllowed());
			}
		}

	}

	void processStartDocument (StartDocument sd,
		List<String> sigList)
	{
		currentDocId = (new File(sd.getName())).toURI();
		this.sigList = sigList;
	}
	
	void processTextUnit (TextUnit tu) {
		// Skip non-translatable entries
		if ( !tu.isTranslatable() ) return;
		
		// Get the containers
		TextContainer srcCont = tu.getSource();
		TextContainer trgCont = tu.getTarget(trgLoc);
		
		// Check if we have a target (even if option disabled)
		if ( trgCont == null ) {
			// No translation available
			reportIssue(IssueType.MISSING_TARGETTU, tu, null,
				"Missing translation.",
				0, -1, 0, -1, Issue.SEVERITY_HIGH, srcCont.toString(), "");
			return;
		}
		
		tu.synchronizeSourceSegmentation(trgLoc);
		ISegments srcSegs = srcCont.getSegments();
		ISegments trgSegs = trgCont.getSegments();
		
		for ( Segment srcSeg : srcSegs ) {
			Segment trgSeg = trgSegs.get(srcSeg.getId());
			if ( trgSeg == null ) {
				reportIssue(IssueType.MISSING_TARGETSEG, tu, srcSeg.getId(),
					"The source segment has no corresponding target segment.",
					0, -1, 0, -1, Issue.SEVERITY_HIGH, srcSeg.toString(), "");
				continue; // Cannot go further for that segment
			}
			
			// Check for empty target, if requested
			if ( params.getEmptyTarget() ) {
				if ( trgSeg.text.isEmpty() && !srcSeg.text.isEmpty() ) {
					reportIssue(IssueType.EMPTY_TARGETSEG, tu, srcSeg.getId(),
						"The target segment is empty, but its source is not empty.",
						0, -1, 0, -1, Issue.SEVERITY_HIGH, srcSeg.toString(), "");
					continue; // No need to check more if it's empty
				}
			}
			// Check for empty source when target is not empty, if requested
			if ( params.getEmptySource() ) {
				if ( srcSeg.text.isEmpty() && !trgSeg.text.isEmpty() ) {
					reportIssue(IssueType.EMPTY_SOURCESEG, tu, srcSeg.getId(),
						"The target segment is not empty, but its source is empty.",
						0, -1, 0, -1, Issue.SEVERITY_HIGH, srcSeg.toString(), "");
					continue; // No need to check more if the source is empty
				}
			}
			
			// Check code differences, if requested
			if ( params.getCodeDifference() ) {
				checkInlineCodes(srcSeg, trgSeg, tu);
			}
			
			// Check for target is the same as source, if requested
			if ( params.getTargetSameAsSource() ) {
				if ( srcSeg.text.hasText() ) {
					if ( srcSeg.text.compareTo(trgSeg.text, params.getTargetSameAsSourceWithCodes()) == 0 ) {
						// Is the string of the cases where target should be the same? (URL, etc.)
						boolean warn = true;
						if ( patterns != null ) {
							for ( PatternItem item : patterns ) {
								String ctext = srcSeg.text.getCodedText();
								if ( item.target.equals(PatternItem.SAME) ) {
									Matcher m = item.getSourcePattern().matcher(ctext);
									if ( m.find() ) {
										warn = !ctext.equals(m.group());
										break;
									}
								}
							}
						}
						if ( warn ) {
							reportIssue(IssueType.TARGET_SAME_AS_SOURCE, tu, srcSeg.getId(),
								"Translation is the same as the source.",
								0, -1, 0, -1, Issue.SEVERITY_MEDIUM, srcSeg.toString(), trgSeg.toString());
						}
					}
				}
			}
			
			// Check for patterns, if requested
			if ( params.getCheckPatterns() ) {
				checkPatterns(srcSeg, trgSeg, tu);
			}
			
			// Check length
			if ( params.getCheckMaxCharLength() || params.getCheckMinCharLength() ) {
				checkLengths(srcSeg, trgSeg, tu);
			}
			
			// Check all suspect patterns
			checkSuspectPatterns(srcSeg, trgSeg, tu);
			
			// Run a check with LanguageTool connector
			if ( ltConn != null ) {
				if ( ltConn.checkSegment(currentDocId, trgSeg, tu) > 0 ) {
					for ( Issue issue : ltConn.getIssues() ) {
						reportIssue(issue.issueType, tu, issue.segId, issue.message, issue.srcStart, issue.srcEnd,
							issue.trgStart, issue.trgEnd, issue.severity, srcSeg.toString(), trgSeg.toString());
						if ( issue.srcEnd == -99 ) {
							// Special marker indicating a server error
							ltConn = null; // Do not check it again until next re-processing
						}
					}
				}
			}
		
		}

		// Check for orphan target segments
		for ( Segment trgSeg : trgSegs ) {
			Segment srcSeg = srcSegs.get(trgSeg.getId());
			if ( srcSeg == null ) {
				reportIssue(IssueType.EXTRA_TARGETSEG, tu, trgSeg.getId(),
					String.format("Extra target segment (id=%s).", trgSeg.getId()),
					0, -1, 0, -1, Issue.SEVERITY_HIGH, "", trgSeg.toString());
				continue; // Cannot go further for that segment
			}
		}
		
		String srcOri = null;
		if ( srcCont.contentIsOneSegment() ) {
			srcOri = srcCont.toString();
		}
		else {
			srcOri = srcCont.getUnSegmentedContentCopy().toString();
		}
		
		String trgOri = null;
		if ( trgCont.contentIsOneSegment() ) {
			trgOri = trgCont.toString();
		}
		else {
			trgOri = trgCont.getUnSegmentedContentCopy().toString();
		}

		if ( params.getCorruptedCharacters() ) {
			checkCorruptedCharacters(srcOri, trgOri, tu);
		}
		
		checkWhiteSpaces(srcOri, trgOri, tu);
		
		if ( params.getCheckCharacters() ) {
			checkCharacters(srcOri, trgOri, tu);
		}
	}

	private void checkCharacters (String srcOri,
		String trgOri,
		TextUnit tu)
	{
		StringBuilder badChars = new StringBuilder();
		int pos = -1;
		int badChar = 0;
		int count = 0;
		
		for ( int i=0; i<trgOri.length(); i++ ) {
			char ch = trgOri.charAt(i);
			
			if ( encoder != null ) {
				if ( encoder.canEncode(ch) ) {
					continue; // Allowed, move to the next character
				}
				else { // Not included in the target charset
					// Check if it is included in the extra characters list
					if ( extraCharsAllowed != null ) {
						Matcher m = extraCharsAllowed.matcher(trgOri.subSequence(i, i+1));
						if ( m.find() ) {
							// Part of the extra character list: it's OK
							continue; // Move to the next character
						}
						// Else: not allowed: fall thru
					}
				}
			}
			else { // Not charset defined, try just the extra characters list
				if ( extraCharsAllowed != null ) {
					Matcher m = extraCharsAllowed.matcher(trgOri.subSequence(i, i+1));
					if ( m.find() ) {
						// Part of the extra character list: it's OK
						continue; // Move to the next character
					}
					// Else: not allowed: fall thru
				}
				// Else: not in charset, nor in extra characters list: not allowed
			}
		
			// The character is not allowed: add the error
			if ( ++count > 1 ) {
				if ( badChars.indexOf(String.valueOf(ch)) == -1 ) { 
					badChars.append(ch);
				}
			}
			else {
				pos = i;
				badChar = ch;
			}
		}

		// Do we have one or more errors?
		if ( pos > -1 ) {
			if ( count > 1 ) {
				reportIssue(IssueType.ALLOWED_CHARACTERS, tu, null,
					String.format("The character '%c' (U+%04X) is not allowed in the target text."
						+ " Other forbidden characters found: ", badChar, (int)badChar)+badChars.toString(),
						0, -1, pos, pos+1, Issue.SEVERITY_MEDIUM, srcOri, trgOri);
			}
			else {
				reportIssue(IssueType.ALLOWED_CHARACTERS, tu, null,
					String.format("The character '%c' (U+%04X) is not allowed in the target text.", badChar, (int)badChar),
					0, -1, pos, pos+1, Issue.SEVERITY_MEDIUM, srcOri, trgOri);
			}
		}
		
	}
	
	// Create a copy of the codes and strip out any that has empty data.
	// They correspond to process-only codes like <df> in TTX or <mrk> in XLIFF
	private List<Code> stripNoiseCodes (Segment seg) {
		List<Code> list = new ArrayList<Code>(seg.text.getCodes());
		Iterator<Code> iter = list.iterator();
		while ( iter.hasNext() ) {
			Code code = iter.next();
			if ( Util.isEmpty(code.getData()) ) {
				iter.remove();
			}
		}
		return list;
	}
	
	private String buildCodeList (List<Code> list) {
		StringBuilder tmp = new StringBuilder();
		for ( Code code : list ) {
			if ( tmp.length() > 0 ) tmp.append(", ");
			tmp.append("\""+code.getData()+"\"");
		}
		return tmp.toString();
	}
	
	private void checkInlineCodes (Segment srcSeg,
		Segment trgSeg,
		TextUnit tu)
	{
		List<Code> srcList = stripNoiseCodes(srcSeg);
		List<Code> trgList = stripNoiseCodes(trgSeg);

		// If no codes: don't check
		if (( srcList.size() == 0 ) && ( trgList.size() == 0 )) return;

		// Quick check: if strings are the same, no differneces
		if ( srcList.toString().equals(trgList.toString()) ) return;
		
//		//--- temporary way of verifying the codes
//		String srcCodes = srcList.toString();
//		String trgCodes = trgList.toString();
//		if ( !srcCodes.equals(trgCodes) ) {
//			reportIssue(IssueType.CODE_DIFFERENCE, tu, srcSeg.getId(),
//				"The translation does not have the same codes as the source.",
//				0, -1, 0, -1, Issue.SEVERITY_MEDIUM, srcSeg.toString(), trgSeg.toString());
//		}
//		//if ( true ) return;
//		//--- end temp way
	
		// Check codes missing in target
		Iterator<Code> srcIter = srcList.iterator();
		while ( srcIter.hasNext() ) {
			Code srcCode = srcIter.next();
			Iterator<Code> trgIter = trgList.iterator();
			while ( trgIter.hasNext() ) {
				Code trgCode = trgIter.next();
				if ( trgCode.getData().equals(srcCode.getData()) ) {
					// Found: remove them from lists
					trgIter.remove();
					srcIter.remove();
					break;
				}
			}
		}

		// What is left in the source list are the codes missing in the target
		if ( !srcList.isEmpty() ) {
			reportIssue(IssueType.CODE_DIFFERENCE, tu, srcSeg.getId(),
				"Codes in the source but missing in the target: "+buildCodeList(srcList),
				0, -1, 0, -1, Issue.SEVERITY_MEDIUM, srcSeg.toString(), trgSeg.toString());
		}
		
		// What is left in the target list are the extra codes in the target
		if ( !trgList.isEmpty() ) {
			reportIssue(IssueType.CODE_DIFFERENCE, tu, srcSeg.getId(),
				"Extra codes in the target not in the source: "+buildCodeList(trgList),
				0, -1, 0, -1, Issue.SEVERITY_MEDIUM, srcSeg.toString(), trgSeg.toString());
		}
		
//		// If both list are empty but we get here:
//		// This means the codes are the same but in a different order
//		if ( srcList.isEmpty() && trgList.isEmpty() ) {
//			reportIssue(IssueType.CODE_DIFFERENCE, tu, srcSeg.getId(),
//				"Codes are in a different order in the source and target.",
//				0, -1, 0, -1, Issue.SEVERITY_MEDIUM, srcSeg.toString(), trgSeg.toString());
//		}
	}
	
	private void checkCorruptedCharacters (String srcOri,
		String trgOri,
		TextUnit tu)
	{
		Matcher m = corruption.matcher(trgOri);
		if ( m.find() ) { // Getting one match is enough
			reportIssue(IssueType.SUSPECT_PATTERN, tu, null,
				String.format("Possible corrupted characters in the target (for example: \"%s\").", m.group()),
				0, -1, m.start(), m.end(), Issue.SEVERITY_HIGH, srcOri, trgOri);
		}
	}
	
	private void checkWhiteSpaces (String srcOri,
		String trgOri,
		TextUnit tu)
	{
		// Check for leading whitespaces
		if ( params.getLeadingWS() ) {
			
			// Missing ones
			for ( int i=0; i<srcOri.length(); i++ ) {
				if ( Character.isWhitespace(srcOri.charAt(i)) ) {
					if ( srcOri.length() > i ) {
						if ( trgOri.charAt(i) != srcOri.charAt(i) ) {
							reportIssue(IssueType.MISSINGORDIFF_LEADINGWS, tu, null,
								String.format("Missing or different leading white space at position %d.", i),
								i, i+1, 0, -1, Issue.SEVERITY_LOW, srcOri, trgOri);
							break;
						}
					}
					else {
						reportIssue(IssueType.MISSING_LEADINGWS, tu, null,
							String.format("Missing leading white space at position %d.", i),
							i, i+1, 0, -1, Issue.SEVERITY_LOW, srcOri, trgOri);
					}
				}
				else break;
			}

			// Extra ones
			for ( int i=0; i<trgOri.length(); i++ ) {
				if ( Character.isWhitespace(trgOri.charAt(i)) ) {
					if ( srcOri.length() > i ) {
						if ( srcOri.charAt(i) != trgOri.charAt(i) ) {
							reportIssue(IssueType.EXTRAORDIFF_LEADINGWS, tu, null,
								String.format("Extra or different leading white space at position %d.", i),
								0, -1, i, i+1, Issue.SEVERITY_LOW, srcOri, trgOri);
							break;
						}
					}
					else {
						reportIssue(IssueType.EXTRA_LEADINGWS, tu, null,
							String.format("Extra leading white space at position %d.", i),
							0, -1, i, i+1, Issue.SEVERITY_LOW, srcOri, trgOri);
					}
				}
				else break;
			}
		}
		
		// Check for trailing whitespaces
		if ( params.getTrailingWS() ) {

			// Missing ones
			int j = trgOri.length()-1;
			for ( int i=srcOri.length()-1; i>=0; i-- ) {
				if ( Character.isWhitespace(srcOri.charAt(i)) ) {
					if ( j >= 0 ) {
						if ( trgOri.charAt(j) != srcOri.charAt(i) ) {
							reportIssue(IssueType.MISSINGORDIFF_TRAILINGWS, tu, null,
								String.format("Missing or different trailing white space at position %d", i),
								i, i+1, 0, -1, Issue.SEVERITY_LOW, srcOri, trgOri);
							break;
						}
					}
					else {
						reportIssue(IssueType.MISSING_TRAILINGWS, tu, null,
							String.format("Missing trailing white space at position %d.", i),
							i, i+1, 0, -1, Issue.SEVERITY_LOW, srcOri, trgOri);
					}
				}
				else break;
				j--;
			}

			// Extra ones
			j = srcOri.length()-1;
			for ( int i=trgOri.length()-1; i>=0; i-- ) {
				if ( Character.isWhitespace(trgOri.charAt(i)) ) {
					if ( j >= 0 ) {
						if ( srcOri.charAt(j) != trgOri.charAt(i) ) {
							reportIssue(IssueType.EXTRAORDIFF_TRAILINGWS, tu, null,
								String.format("Extra or different trailing white space at position %d.", i),
								0, -1, i, i+1, Issue.SEVERITY_LOW, srcOri, trgOri);
							break;
						}
					}
					else {
						reportIssue(IssueType.EXTRA_TRAILINGWS, tu, null,
							String.format("Extra white trailing space at position %d.", i),
							0, -1, i, i+1, Issue.SEVERITY_LOW, srcOri, trgOri);
					}
				}
				else break;
				j--;
			}
		}

	}

	private void checkLengths (Segment srcSeg,
		Segment trgSeg,
		TextUnit tu)
	{
		int srcLen = srcSeg.text.getCodedText().length();
		int trgLen = trgSeg.text.getCodedText().length();
		int n;
		
		if ( params.getCheckMaxCharLength() ) {
			n = (srcLen==0 ? 0 : (int)((srcLen*params.getMaxCharLength())/100));
			if ( trgLen > n ) {
				double d = (((float)trgLen)/(srcLen==0 ? 1.0 : ((float)srcLen)))*100.0;
				reportIssue(IssueType.TARGET_LENGTH, tu, srcSeg.getId(),
					String.format("The target is suspiciously longer than its source (%.2f%% of the source).", d),
					0, -1, 0, -1, Issue.SEVERITY_LOW, 
					srcSeg.toString(), trgSeg.toString());
			}
		}

		if ( params.getCheckMinCharLength() ) {
			n = (srcLen==0 ? 0 : (int)((srcLen*params.getMinCharLength())/100));
			if ( trgSeg.text.getCodedText().length() < n ) {
				double d = (((float)trgLen)/(srcLen==0 ? 1.0 : ((float)srcLen)))*100.0;
				reportIssue(IssueType.TARGET_LENGTH, tu, srcSeg.getId(),
					String.format("The target is suspiciously shorter than its source (%.2f%% of the source).", d),
					0, -1, 0, -1, Issue.SEVERITY_LOW, 
					srcSeg.toString(), trgSeg.toString());
			}
		}
	}
	
	private void checkSuspectPatterns (Segment srcSeg,
		Segment trgSeg,
		TextUnit tu)
	{
		String trgCText = trgSeg.text.getCodedText();
		
		if ( params.getDoubledWord() ) {
			Matcher m = patDoubledWords.matcher(trgCText);
			while ( m.find() ) {
				reportIssue(IssueType.SUSPECT_PATTERN, tu, srcSeg.getId(),
					String.format("Double word: \"%s\" found in the target.", m.group()),
					0, -1,
					fromFragmentToString(trgSeg.text, m.start()),
					fromFragmentToString(trgSeg.text, m.end()),
					Issue.SEVERITY_HIGH, 
					srcSeg.toString(), trgSeg.toString());
			}
		}
	}

	private void checkPatterns (Segment srcSeg,
		Segment trgSeg,
		TextUnit tu)
	{
		//--- Source-based search
		// Get the source text
		String srcCText = srcSeg.text.getCodedText();
		// Search for any enabled pattern in the source
		for ( PatternItem item : patterns ) {
			// Skip disabled items and items that use the target as the base
			if ( !item.enabled || !item.fromSource ) continue;
			
			Matcher srcM = item.getSourcePattern().matcher(srcCText);
			
			// Use a copy for the target: it may get modified for the search
			StringBuilder trgCTextCopy = new StringBuilder(trgSeg.text.getCodedText());

			while ( srcM.find() ) {
				// Get the source text corresponding to the match
				String srcPart = srcCText.substring(srcM.start(), srcM.end());
				int start, end;
				boolean found = false;
				boolean expectSame = item.target.equals(PatternItem.SAME);
				// Try to get the corresponding part in the target
				if ( expectSame ) {
					// If the target pattern is defined as being the same as the source
					// Look for the same text in the source.
					found = ((start = trgCTextCopy.indexOf(srcPart)) != -1);
					end = start + srcPart.length();
				}
				else { // Target part has its own pattern
					Matcher trgM = item.getTargetPattern().matcher(trgCTextCopy);
					found = trgM.find();
					start = trgM.start();
					end = trgM.end();
				}
				// Process result
				if ( found ) { // Remove that match in case source has several occurrences to match
					trgCTextCopy.delete(start, end);
				}
				else { // Generate an issue
					String msg;
					if ( expectSame ) {
						msg = String.format("The source part \"%s\" is not in the target.", srcPart);
					}
					else {
						msg = String.format("The source part \"%s\" has no correspondance in the target.", srcPart);
					}
					reportIssue(IssueType.UNEXPECTED_PATTERN, tu, srcSeg.getId(), msg,
						fromFragmentToString(srcSeg.text, srcM.start()),
						fromFragmentToString(srcSeg.text, srcM.end()),
						0, -1, item.severity,
						srcSeg.toString(), trgSeg.toString());
				}
			}
		}

		//--- Target-based search
		// Get the target text
		String trgCText = trgSeg.text.getCodedText();
		// Search for any enabled pattern in the source
		for ( PatternItem item : patterns ) {
			// Skip disabled items and items that use the source as the base
			if ( !item.enabled || item.fromSource ) continue;
			
			Matcher trgM = item.getTargetPattern().matcher(trgCText);
			
			// Use a copy for the source: it may get modified for the search
			StringBuilder srcCTextCopy = new StringBuilder(srcSeg.text.getCodedText());

			while ( trgM.find() ) {
				// Get the source text corresponding to the match
				String trgPart = trgCText.substring(trgM.start(), trgM.end());
				int start, end;
				boolean found = false;
				boolean expectSame = item.source.equals(PatternItem.SAME);
				// Try to get the corresponding part in the source
				if ( expectSame ) {
					// If the source pattern is defined as being the same as the target
					// Look for the same text in the source.
					found = ((start = srcCTextCopy.indexOf(trgPart)) != -1);
					end = start + trgPart.length();
				}
				else { // Source part has its own pattern
					Matcher srcM = item.getSourcePattern().matcher(srcCTextCopy);
					found = srcM.find();
					start = srcM.start();
					end = srcM.end();
				}
				// Process result
				if ( found ) { // Remove that match in case target has several occurrences to match
					srcCTextCopy.delete(start, end);
				}
				else { // Generate an issue
					String msg;
					if ( expectSame ) {
						msg = String.format("The target part \"%s\" is not in the source.", trgPart);
					}
					else {
						msg = String.format("The target part \"%s\" has no correspondance in the source.", trgPart);
					}
					reportIssue(IssueType.UNEXPECTED_PATTERN, tu, srcSeg.getId(), msg, 0, -1,
						fromFragmentToString(trgSeg.text, trgM.start()),
						fromFragmentToString(trgSeg.text, trgM.end()),
						item.severity,
						srcSeg.toString(), trgSeg.toString());
				}
			}
		}
		
	}
	
	private void reportIssue (IssueType issueType,
		TextUnit tu,
		String segId,
		String message,
		int srcStart,
		int srcEnd,
		int trgStart,
		int trgEnd,
		int severity,
		String srcOri,
		String trgOri)
	{
		Issue issue = new Issue(currentDocId, issueType, tu.getId(), segId, message,
			srcStart, srcEnd, trgStart, trgEnd, severity, tu.getName());
		issues.add(issue);
		issue.enabled = true;
		issue.oriSource = srcOri;
		issue.oriTarget = trgOri;
		
		if ( sigList != null ) {
			// Disable any issue for which we have the signature in the list
			issue.enabled = !sigList.contains(issue.getSignature());
		}
	}

	/**
	 * Gets the position in the string representation of a fragment of a given
	 * position in that fragment. 
	 * @param frag the fragment where the poistion is located.
	 * @param pos the position.
	 * @return the same position, but in the string representation of the fragment.
	 */
	public static int fromFragmentToString (TextFragment frag,
		int pos)
	{
		// No codes means no correction
		if ( !frag.hasCode() ) return pos;

		// Else: correct the position
		int len = 0;
		String text = frag.getCodedText();
		for ( int i=0; i<text.length(); i++ ) {
			if ( i >= pos ) {
				return len;
			}
			if ( TextFragment.isMarker(text.charAt(i)) ) {
				Code code = frag.getCode(text.charAt(++i));
				len += code.getData().length();
				continue;
			}
			else {
				len++;
			}
		}
		return len;
	}
}
