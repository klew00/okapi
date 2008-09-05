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

package net.sf.okapi.applications.rainbow.utilities.charlisting;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.applications.rainbow.lib.FilterAccess;
import net.sf.okapi.applications.rainbow.utilities.IFilterDrivenUtility;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.pipeline.ThrougputPipeBase;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.ui.UIUtil;

public class Utility extends ThrougputPipeBase implements IFilterDrivenUtility  {

	private final Logger                    logger = LoggerFactory.getLogger("net.sf.okapi.logging");
	private Parameters                      params;
	private Hashtable<Character, Integer>   charList;

	
	public Utility () {
		params = new Parameters();
	}
	
	public void resetLists () {
		// Not used for this utility
	}
	
	public String getID () {
		return "oku_charlisting";
	}
	
	public void doProlog (String sourceLanguage,
		String targetLanguage)
	{
		charList = new Hashtable<Character, Integer>();
	}
	
	public void doEpilog () {
		// Generate the report
		PrintWriter writer = null;
		try {
			String path = params.getParameter("outputPath");
			logger.info("Output: " + path);
			Util.createDirectories(path);
			writer = new PrintWriter(path, "UTF-8");
			writer.write('\uFEFF'); // BOM
			// Process all characters
			for ( char key : charList.keySet() ) {
				writer.println(String.format("U+%04X\t'%c'\t%d", (int)key, key, charList.get(key)));
			}
			if ( params.getBoolean("autoOpen") ) {
				UIUtil.start(path);
			}
		}
		catch ( FileNotFoundException e ) {
			logger.error(e.getMessage());
		}
		catch ( UnsupportedEncodingException e ) {
			logger.error(e.getMessage());
		}
		finally {
			if ( writer != null ) {
				writer.close();
				writer = null;
			}
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
		// Get the coded text and detect the used characters
		String text = tu.getSourceContent().getCodedText();
		for ( int i=0; i<text.length(); i++ ) {
			switch ( text.charAt(i) ) {
			case TextFragment.MARKER_OPENING:
			case TextFragment.MARKER_CLOSING:
			case TextFragment.MARKER_ISOLATED:
				i++;
				break;
			default:
				if ( charList.containsKey(text.charAt(i)) ) {
					charList.put(text.charAt(i), charList.get(text.charAt(i))+1);
				}
				else {
					charList.put(text.charAt(i), 1);
				}
				break;
			}
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
		// Not used for this utility
	}

	public int getInputCount () {
		return 1;
	}

	public String getFolderAfterProcess () {
		return Util.getDirectoryName(params.getParameter("outputPath"));
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
