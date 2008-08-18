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

import net.sf.okapi.applications.rainbow.utilities.IFilterDrivenUtility;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.pipeline.ThrougputPipeBase;
import net.sf.okapi.common.resource.LocaleData;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;

public class Utility extends ThrougputPipeBase implements IFilterDrivenUtility  {

	private final Logger          logger = LoggerFactory.getLogger("net.sf.okapi.logging");
	private Parameters            params;
	private String                commonFolder;

	
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
    public void endExtractionItem(TextUnit item) {
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
		// Else: do the requested modifications
		// Make sure we have a target where to set data
		if ( !tu.hasTarget() ) {
			tu.setTarget(new LocaleData(tu));
			tu.getTargetContent().setCodedText(
				tu.getSourceContent().getCodedText(),
				tu.getSourceContent().getCodes());
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
	
	private void replaceWithXN (TextUnit item) {
		String tmp = null;
		try {
			tmp = item.getTargetContent().getCodedText().replaceAll("\\p{L}", "X");
			tmp = tmp.replaceAll("\\d", "N");
			TextContainer cnt = item.getTargetContent(); 
			cnt.setCodedText(tmp, item.getSourceContent().getCodes());
		}
		catch ( Exception e ) {
			logger.warn("Error when updating content: '"+tmp+"'", e);
		}
	}
	
	/**
	 * Adds prefix and/or suffix to the target. This method assumes that
	 * the item has gone through the first transformation already.
	 * @param item The item to process.
	 */
	private void addText (TextUnit item) {
		String tmp = null;
		try {
			// Use the target as the text to change.
			tmp = item.getTargetContent().getCodedText();
			if ( params.addPrefix ) {
				tmp = params.prefix + tmp;
			}
			if ( params.addName ) {
				if ( item.getName().length() > 0 ) tmp += "_"+item.getName();
				else tmp += "_"+item.getID();
			}
			if ( params.addID ) {
				tmp += "_"+item.getID();
			}
			if ( params.addSuffix ) {
				tmp += params.suffix;
			}
			TextContainer cnt = item.getTargetContent(); 
			cnt.setCodedText(tmp, item.getSourceContent().getCodes());
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
}
