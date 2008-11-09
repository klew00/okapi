/*===========================================================================*/
/* Copyright (C) 2008 by the Okapi Framework contributors                    */
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

package net.sf.okapi.applications.rainbow.utilities.textrewriting;

import net.sf.okapi.applications.rainbow.utilities.BaseUtility;
import net.sf.okapi.applications.rainbow.utilities.IFilterDrivenUtility;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.Document;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.lib.segmentation.SRXDocument;
import net.sf.okapi.lib.segmentation.Segmenter;
import net.sf.okapi.lib.translation.QueryResult;
import net.sf.okapi.tm.simpletm.SimpleTMConnector;

public class Utility extends BaseUtility implements IFilterDrivenUtility  {

	private static final char TMPSTARTSEG = '\u0002';
	private static final char TMPENDSEG = '\u0003';
	private static final char STARTSEG = '[';
	private static final char ENDSEG = ']';
	
	private Parameters params;
	private String commonFolder;
	private Segmenter srcSeg;
	private Segmenter trgSeg;
	private SimpleTMConnector tmQ;
	
	public Utility () {
		params = new Parameters();
	}
	
	public void resetLists () {
		// Not used for this utility
	}
	
	public String getID () {
		return "oku_textrewriting";
	}
	
	public void doProlog (String sourceLanguage,
		String targetLanguage)
	{
		commonFolder = null;

		// Load the segmentation rules
		if ( params.segment ) {
			SRXDocument doc = new SRXDocument();
			doc.loadRules(params.sourceSrxPath);
			if ( doc.hasWarning() ) logger.warn(doc.getWarning());
			srcSeg = doc.applyLanguageRules(sourceLanguage, null);
			if ( !params.sourceSrxPath.equals(params.targetSrxPath) ) {
				doc.loadRules(params.targetSrxPath);
				if ( doc.hasWarning() ) logger.warn(doc.getWarning());
			}
			trgSeg = doc.applyLanguageRules(targetLanguage, null);
		}
		
		if ( params.type == Parameters.TYPE_TRANSLATEEXACTMATCHES ) {
			tmQ = new SimpleTMConnector();
			tmQ.open(params.tmPath);
		}
	}
	
	public void doEpilog () {
		if ( tmQ != null ) {
			tmQ.close();
			tmQ = null;
		}
	}
	
	public IParameters getParameters () {
		return params;
	}

	public String getInputRoot () {
		return null;
	}
	
	public String getOutputRoot () {
		return null;
	}

	public boolean hasParameters () {
		return true;
	}

	public boolean needsRoots () {
		return false;
	}

	public boolean needsOutputFilter () {
		return true;
	}

	public void setParameters (IParameters paramsObject) {
		params = (Parameters)paramsObject;
	}

	public void setRoots (String inputRoot,
		String outputRoot)
	{
	}

	@Override
    public void startResource (Document resource) {
    	if ( tmQ != null ) {
    		tmQ.setAttribute("FileName", Util.getFilename(resource.getName(), true));
    	}
    	super.startResource(resource);
    }
	
	@Override
    public void endExtractionItem (TextUnit item) {
		try {
			processTU(item);
			if ( item.hasChild() ) {
				for ( TextUnit tu : item.childTextUnitIterator() ) {
					processTU(tu);
				}
			}
		}
		finally {
			super.endExtractionItem(item);
		}
    }
	
	private void processTU (TextUnit tu) {
		// Skip non-translatable
		if ( !tu.isTranslatable() ) return;
		// Skip if already translate (only if required)
		if ( tu.hasTarget() && !params.applyToExistingTarget ) return;
		
		// Apply the segmentation and/or segment marks if requested
		if ( params.segment || params.markSegments ) {
			if ( tu.hasTarget() ) {
				if ( params.segment ) {
					trgSeg.computeSegments(tu.getTargetContent());
					tu.getTargetContent().createSegments(trgSeg.getSegmentRanges());
				}
			}
			else {
				if ( params.segment ) {
					srcSeg.computeSegments(tu.getSourceContent());
					tu.getSourceContent().createSegments(srcSeg.getSegmentRanges());
				}
			}
		}
		
		// Else: do the requested modifications
		// Make sure we have a target where to apply the modifications
		if ( !tu.hasTarget() ) {
			tu.setTargetContent(tu.getSourceContent().clone());
		}
		
		// Translate is done before we merge possible segments
		if ( params.type == Parameters.TYPE_TRANSLATEEXACTMATCHES ) {
			translate(tu);
		}

		// Merge all segments if needed
		if ( params.segment || params.markSegments ) {
			mergeSegments(tu.getTargetContent());
		}

		// Other text modification are done after merging all segments
		switch ( params.type ) {
		case Parameters.TYPE_XNREPLACE:
			replaceWithXN(tu);
			break;
		case Parameters.TYPE_KEEPINLINE:
			removeText(tu);
			break;
		}
	
		if ( params.addPrefix || params.addSuffix || params.addName ) {
			addText(tu);
		}
	}

	/**
	 * Removes the text but leaves the inline code.
	 * @param tu the text unit to process.
	 */
	private void removeText (TextUnit tu) {
		String result = tu.getTargetContent().getCodedText();
		StringBuilder sb = new StringBuilder();
		
		for ( int i=0; i<result.length(); i++ ) {
			switch ( result.charAt(i) ) {
			    case TextFragment.MARKER_OPENING:
				case TextFragment.MARKER_CLOSING:
				case TextFragment.MARKER_ISOLATED:
				case TextFragment.MARKER_SEGMENT:
					sb.append(result.charAt(i));
					sb.append(result.charAt(++i));
					break;
				case TMPSTARTSEG: // Keep segment marks if needed
					if ( params.markSegments ) sb.append(STARTSEG);
					break;
				case TMPENDSEG: // Keep segment marks if needed
					if ( params.markSegments ) sb.append(ENDSEG);
					break;					
				default: // Do not keep other characters
					break;
			}
		}
		TextContainer cnt = tu.getTargetContent();
		cnt.setCodedText(sb.toString());
	}	
	
	/**
	 * Merges back segments into a single content, while optionally adding
	 * brackets to denote the segments. If the unit is not segmented, brackets
	 * are added if required. 
	 * @param container The TextContainer object to un-segment.
	 */
	private void mergeSegments (TextContainer container) {
		// Set variables
		StringBuilder text = new StringBuilder(container.getCodedText());
		char start = STARTSEG;
		char end = ENDSEG;
		if ( params.type == Parameters.TYPE_KEEPINLINE ) {
			// Use temporary marks if we need to remove the text after
			// This way '[' and ']' is real text get removed too
			start = TMPSTARTSEG;
			end = TMPENDSEG;
		}
		
		if ( !container.isSegmented() ) {
			if ( params.markSegments ) {
				container.setCodedText(start+text.toString()+end);
			}
			return;
		}
		
		// Add the markers if needed
		if ( params.markSegments ) {
			// Insert the segment marks if requested
			for ( int i=0; i<text.length(); i++ ) {
				switch ( text.charAt(i) ) {
				case TextContainer.MARKER_OPENING:
				case TextContainer.MARKER_CLOSING:
				case TextContainer.MARKER_ISOLATED:
					i++; // Normal skip
					break;
				case TextContainer.MARKER_SEGMENT:
					text.insert(i, start);
					i += 3; // The bracket, the marker, the code index
					text.insert(i, end);
					// Next i increment will skip over the closing mark
					break;
				}
			}
			// Replace the original coded text by the one with markers
			container.setCodedText(text.toString());
		}
		// Merge all segments
		container.mergeAllSegments();
	}

	private void translate (TextUnit tu) {
		try {
			// Target is set if needed
			QueryResult qr;
			tmQ.setAttribute("GrpName", tu.getName());
			TextContainer tc = tu.getTargetContent();
			if ( tc.isSegmented() ) {
				int seg = 0;
				for ( TextFragment tf : tc.getSegments() ) {
					if ( tmQ.query(tf) == 1 ) {
						qr = tmQ.next();
						tc.getSegments().set(seg, qr.target);
					}
					seg++;
				}
			}
			else {
				if ( tmQ.query(tc) == 1 ) {
					qr = tmQ.next();
					tc = new TextContainer();
					tc.append(qr.target);
					tu.setTargetContent(tc);
				}
			}
			
		}
		catch ( Throwable e ) {
			logger.warn("Error while translating: ", e);
		}
	}
	
	/**
	 * Replaces letters with Xs and digits with Ns.
	 * @param tu the text unit to process.
	 */
	private void replaceWithXN (TextUnit tu) {
		String tmp = null;
		try {
			tmp = tu.getTargetContent().getCodedText().replaceAll("\\p{Lu}|\\p{Lo}", "X");
			tmp = tmp.replaceAll("\\p{Ll}", "x");
			tmp = tmp.replaceAll("\\d", "N");
			TextContainer cnt = tu.getTargetContent(); 
			cnt.setCodedText(tmp, tu.getSourceContent().getCodes(), false);
		}
		catch ( Throwable e ) {
			logger.warn("Error when updating content: '"+tmp+"'", e);
		}
	}
	
	/**
	 * Adds prefix and/or suffix to the target. This method assumes that
	 * the item has gone through the first transformation already.
	 * @param tu The text unit to process.
	 */
	private void addText (TextUnit tu) {
		String tmp = null;
		try {
			// Use the target as the text to change.
			tmp = tu.getTargetContent().getCodedText();
			if ( params.addPrefix ) {
				tmp = params.prefix + tmp;
			}
			if ( params.addName ) {
				if ( tu.getName().length() > 0 ) tmp += "_"+tu.getName();
				else tmp += "_"+tu.getID();
			}
			if ( params.addID ) {
				tmp += "_"+tu.getID();
			}
			if ( params.addSuffix ) {
				tmp += params.suffix;
			}
			TextContainer cnt = tu.getTargetContent(); 
			cnt.setCodedText(tmp, tu.getSourceContent().getCodes(), false);
		}
		catch ( Throwable e ) {
			logger.warn("Error when add prefix or suffix: '"+tmp+"'", e);
		}
	}
	
	public boolean isFilterDriven () {
		return true;
	}

	public void addInputData (String path,
		String encoding,
		String filterSettings)
	{
		// Nothing to do here
	}

	public void addOutputData (String path,
		String encoding)
	{
		// Compute the longest common folder
		commonFolder = Util.longestCommonDir(commonFolder,
			Util.getDirectoryName(path), !Util.isOSCaseSensitive());
	}

	public int getInputCount () {
		return 1;
	}

	public String getFolderAfterProcess () {
		return commonFolder;
	}

}
