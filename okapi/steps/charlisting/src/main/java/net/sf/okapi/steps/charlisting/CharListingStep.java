/*===========================================================================
  Copyright (C) 2009-2011 by the Okapi Framework contributors
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextPart;
import net.sf.okapi.common.resource.ITextUnit;

@UsingParameters(Parameters.class)
public class CharListingStep extends BasePipelineStep {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private Parameters params;
	private Hashtable<Character, Integer> charList;

	public CharListingStep () {
		params = new Parameters();
	}
	
	@Override
	public void destroy () {
		if ( charList != null ) {
			charList.clear();
			charList = null;
		}
	}
	
	@Override
	public String getDescription () {
		return "Generate a list of all the characters in the source content of a set of documents."
			+ " Expects: filter events. Sends back: filter events.";
	}

	@Override
	public String getName () {
		return "Used Characters Listing";
	}

	@Override
	public IParameters getParameters () {
		return params;
	}

	@Override
	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}
 
	@Override
	protected Event handleStartBatch (Event event) {
		charList = new Hashtable<Character, Integer>();
		return event;
	}
	
	@Override
	protected Event handleEndBatch (Event event) {
		// Generate the report
		PrintWriter writer = null;
		try {
			Util.createDirectories(params.getOutputPath());
			writer = new PrintWriter(params.getOutputPath(), "UTF-8");
			// Generate BOM if we are on windows
			if ( !Util.isOSCaseSensitive() ) {
				writer.write('\uFEFF');
			}
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
			logger.error(
				String.format("Error with '%s'.", params.getOutputPath()), e);
		}
		catch ( UnsupportedEncodingException e ) {
			logger.error(
				String.format("Encoding error with '%s'.", params.getOutputPath()), e);
		}
		finally {
			if ( writer != null ) {
				writer.close();
				writer = null;
				// Open the output if requested
				if ( params.isAutoOpen() ) {
					Util.openURL((new File(params.getOutputPath())).getAbsolutePath());
				}
			}
		}
		
		return event;
	}

	@Override
	protected Event handleTextUnit (Event event) {
		ITextUnit tu = event.getTextUnit();
		// Skip non-translatable
		if ( !tu.isTranslatable() ) return event;
		// Get the coded text of each parts and detect the used characters
		for ( TextPart part : tu.getSource() ) {
			String text = part.text.getCodedText();
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
				}
			}
		}
		
		return event;
	}

}
