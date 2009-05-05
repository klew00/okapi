/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.steps.charlisting;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.pipeline.IPipelineStep;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;

public class CharListingStep implements IPipelineStep {

	private final Logger logger = Logger.getLogger(getClass().getName());

	private Parameters params;
	private Hashtable<Character, Integer> charList;
	private boolean firstDoc;

	public CharListingStep () {
		firstDoc = true;
	}

	public void destroy () {
		firstDoc = true;
		if ( charList == null ) {
			charList.clear();
			charList = null;
		}
	}

	public String getDescription () {
		return "Generates a list of all the characters in a set of documents.";
	}

	public String getName () {
		return "Used Characters Listing";
	}

	public IParameters getParameters () {
		return params;
	}

	public Event handleEvent (Event event) {
		switch ( event.getEventType() ) {
		case START_DOCUMENT:
			if ( firstDoc ) {
				firstDoc = false;
				charList = new Hashtable<Character, Integer>();
			}
			break;
			
		case TEXT_UNIT:
			processTextUnit((TextUnit)event.getResource());
			break;
			
		case CANCELED:
			firstDoc = true;
			break;
			
		case FINISHED:
			processFinished();
			break;
		}
		return event;
	}

	public boolean hasNext () {
		return false;
	}

	public void postprocess () {
		// Nothing to do
	}

	public void preprocess () {
		// Nothing to do
	}

	public void setParameters (IParameters params) {
		params = (Parameters)params;
	}
 
	private void processFinished () {
		// Generate the report
		PrintWriter writer = null;
		try {
			Util.createDirectories(params.outputPath);
			writer = new PrintWriter(params.outputPath, "UTF-8");
			//TODO: generate BOM based on user choice or at least platform
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
		}
		catch ( FileNotFoundException e ) {
			logger.log(Level.SEVERE, "Error with "+params.outputPath, e);
		}
		catch ( UnsupportedEncodingException e ) {
			logger.log(Level.SEVERE, "Error with "+params.outputPath, e);
		}
		finally {
			firstDoc = true;
			if ( writer != null ) {
				writer.close();
				writer = null;
			}
		}
	}

	private void processTextUnit (TextUnit tu) {
		// Skip non-translatable
		if ( !tu.isTranslatable() ) return;
		// Get the coded text and detect the used characters
		String text = tu.getSourceContent().getCodedText();
		for ( int i=0; i<text.length(); i++ ) {
			if ( TextFragment.isMarker(text.charAt(i))) {
				i++; // Skip the second character of the marker
			}
			else {
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
