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

package net.sf.okapi.applications.rainbow.utilities.textrewriting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.applications.rainbow.lib.FilterAccess;
import net.sf.okapi.applications.rainbow.utilities.IFilterDrivenUtility;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.pipeline.ThrougputPipeBase;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.lib.segmentation.SRXDocument;
import net.sf.okapi.lib.segmentation.Segmenter;

public class Utility extends ThrougputPipeBase implements IFilterDrivenUtility  {

	private final Logger     logger = LoggerFactory.getLogger("net.sf.okapi.logging");
	private Parameters       params;
	private String           commonFolder;
	private Segmenter        srcSeg;
	private Segmenter        trgSeg;

	
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
	}
	
	public void doEpilog () {
		// Not used for this utility
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
		
		// mark the segments if requested
		if ( params.segment ) {
			if ( tu.hasTarget() ) {
				trgSeg.computeSegments(tu.getTargetContent());
				tu.getTargetContent().createSegments(trgSeg.getSegmentRanges());
				mergeSegments(tu.getTargetContent());
			}
			else {
				srcSeg.computeSegments(tu.getSourceContent());
				tu.getSourceContent().createSegments(srcSeg.getSegmentRanges());
				mergeSegments(tu.getSourceContent());
			}
		}
		
		// Else: do the requested modifications
		// Make sure we have a target where to set data
		if ( !tu.hasTarget() ) {
			tu.setTargetContent(tu.getSourceContent());
		}

		switch ( params.type ) {
		case Parameters.TYPE_XNREPLACE:
			replaceWithXN(tu);
			break;
		}
		if ( params.addPrefix || params.addSuffix || params.addName ) {
			addText(tu);
		}
	}
	
	/**
	 * Merges back segments into a single content, while adding brackets
	 * to denote the segments. 
	 * @param container The TextContainer object to un-segment.
	 */
	private void mergeSegments (TextContainer container) {
		if ( !container.isSegmented() ) return;
		StringBuilder text = new StringBuilder(container.getCodedText());
		for ( int i=0; i<text.length(); i++ ) {
			switch ( text.charAt(i) ) {
			case TextContainer.MARKER_OPENING:
			case TextContainer.MARKER_CLOSING:
			case TextContainer.MARKER_ISOLATED:
				i++; // Skip
				break;
			case TextContainer.MARKER_SEGMENT:
				text.insert(i, '[');
				i += 3; // The bracket, the marker, the code index
				text.insert(i, ']');
				// Next i++ will skip over the closing bracket
				break;
			}
		}
		// Replace the original coded text by the one with brackets
		container.setCodedText(text.toString());
		// Merge all segments
		container.mergeAllSegments();
	}
	
	/**
	 * Replaces letters with Xs and digits with Ns.
	 * @param tu the text unit to process.
	 */
	private void replaceWithXN (TextUnit tu) {
		String tmp = null;
		try {
			tmp = tu.getTargetContent().getCodedText().replaceAll("\\p{Lu}", "X");
			tmp = tmp.replaceAll("\\p{Ll}", "x");
			tmp = tmp.replaceAll("\\d", "N");
			TextContainer cnt = tu.getTargetContent(); 
			cnt.setCodedText(tmp, tu.getSourceContent().getCodes());
		}
		catch ( Exception e ) {
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
			cnt.setCodedText(tmp, tu.getSourceContent().getCodes());
		}
		catch ( Exception e ) {
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
		// Not used for this utility
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

	public void setFilterAccess (FilterAccess filterAccess,
		String paramsFolder)
	{
		// Not used
	}

	public void setContextUI (Object contextUI) {
		// Not used
	}
}
