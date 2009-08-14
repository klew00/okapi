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

package net.sf.okapi.applications.rainbow.utilities.charlisting;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

import net.sf.okapi.applications.rainbow.utilities.BaseFilterDrivenUtility;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.ui.UIUtil;

public class Utility extends BaseFilterDrivenUtility  {

	private Parameters params;
	private Hashtable<Character, Integer> charList;
	private String finalOutput;
	
	public Utility () {
		params = new Parameters();
		needsSelfOutput = false;
	}
	
	public String getName () {
		return "oku_charlisting";
	}
	
	public void preprocess () {
		charList = new Hashtable<Character, Integer>();
	}
	
	public void postprocess () {
		// Generate the report
		PrintWriter writer = null;
		try {
			finalOutput = params.outputPath.replace(VAR_PROJDIR, projectDir);
			logger.info("Output: " + finalOutput);
			Util.createDirectories(finalOutput);
			writer = new PrintWriter(finalOutput, "UTF-8");
			//TODO: generate UTF based on user choice or at least platform
			writer.write('\uFEFF'); // BOM
			// Process all characters
			for ( char key : charList.keySet() ) {
				switch ( key ) {
				case '\t':
				case '\r':
				case '\n':
					writer.println(String.format("U+%04X\t'0x%d'\t%d", (int)key, (int)key, charList.get(key)));
					break;
				default:
					writer.println(String.format("U+%04X\t'%c'\t%d", (int)key, key, charList.get(key)));
				break;
				}
			}
			if ( params.autoOpen ) {
				UIUtil.start(finalOutput);
			}
		}
		catch ( FileNotFoundException e ) {
			logger.severe(e.getMessage());
		}
		catch ( UnsupportedEncodingException e ) {
			logger.severe(e.getMessage());
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

	public boolean hasParameters () {
		return true;
	}

	public boolean needsRoots () {
		return false;
	}

	public void setParameters (IParameters paramsObject) {
		params = (Parameters)paramsObject;
	}

	public boolean isFilterDriven () {
		return true;
	}

	public int requestInputCount () {
		return 1;
	}

	@Override
	public String getFolderAfterProcess () {
		return Util.getDirectoryName(finalOutput);
	}

	public Event handleEvent (Event event) {
		switch ( event.getEventType() ) {
		case TEXT_UNIT:
			processTextUnit((TextUnit)event.getResource());
			break;
		}
		return event;
	}

	private void processTextUnit (TextUnit tu) {
		// Skip non-translatable
		if ( !tu.isTranslatable() ) return;
		// Get the coded text and detect the used characters
		String text = tu.getSourceContent().getCodedText();
		for ( int i=0; i<text.length(); i++ ) {
			switch ( text.charAt(i) ) {
			case TextFragment.MARKER_OPENING:
			case TextFragment.MARKER_CLOSING:
			case TextFragment.MARKER_ISOLATED:
			case TextFragment.MARKER_SEGMENT:
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

}
