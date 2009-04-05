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

package net.sf.okapi.applications.rainbow.utilities.transcomparison;

import java.io.File;

import net.sf.okapi.applications.rainbow.utilities.BaseFilterDrivenUtility;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.XMLWriter;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filterwriter.TMXWriter;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.ui.UIUtil;

public class Utility extends BaseFilterDrivenUtility {

	Parameters params;
	IFilter inputToCompare;
	TextMatcher matcher;
	XMLWriter writer;
	TMXWriter tmx;
	boolean isBaseMultilingual;
	boolean isToCompareMultilingual;
	String pathToOpen;
	int options;
	Property scoreProp;
	
	public Utility () {
		params = new Parameters();
		needsSelfOutput = false;
	}
	
	public String getName () {
		return "oku_transcomparison";
	}
	
	public void preprocess () {
		// Both strings are in the target language.
		matcher = new TextMatcher(trgLang, trgLang);
		
		if ( params.generateHTML ) {
			writer = new XMLWriter();
		}
		// Start TMX writer (one for all input documents)
		if ( params.generateTMX ) {
			tmx = new TMXWriter();
			tmx.create(params.tmxPath.replace(VAR_PROJDIR, projectDir));
			tmx.writeStartDocument(srcLang, trgLang, getName(), null, null, null, null);
		}
		pathToOpen = null;
		scoreProp = new Property("Txt::Score", "", false);
		
		options = 0;
		if ( params.ignoreCase ) options |= TextMatcher.IGNORE_CASE;
		if ( params.ignoreWS ) options |= TextMatcher.IGNORE_WHITESPACES;
		if ( params.ignorePunct ) options |= TextMatcher.IGNORE_PUNCTUATION;
	}
	
	public void postprocess () {
		matcher = null;
		if ( params.generateHTML && ( writer != null )) {
			writer.close();
			writer = null;
		}
		if ( params.generateTMX && ( tmx != null )) {
			tmx.writeEndDocument();
			tmx.close();
			tmx = null;
		}
		Runtime.getRuntime().gc();
		if ( params.autoOpen && ( pathToOpen != null )) {
			UIUtil.start(pathToOpen);
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
		// Base file and to-compare file
		return 2;
	}

	public Event handleEvent (Event event) {
		switch ( event.getEventType() ) {
		case START_DOCUMENT:
			processStartDocument((StartDocument)event.getResource());
			break;
		case TEXT_UNIT:
			processTextUnit((TextUnit)event.getResource());
			break;
		case END_DOCUMENT:
			processEndDocument();
			break;
		}
		return event;
	}

	private void initializeData () {
		// Initialize the filter to read the translation to compare
		inputToCompare = fa.loadFilterFromFilterSettingsType1(paramsFolder,
			getInputFilterSettings(1), inputToCompare);
		inputToCompare.setOptions(srcLang, trgLang, getInputEncoding(1), false);
		File f = new File(getInputPath(1));
		inputToCompare.open(f.toURI());
			
		// Start HTML output
		if ( writer != null ) writer.close();
		if ( params.generateHTML ) {
			// Use the to-compare file for the output name
			if ( pathToOpen == null ) {
				pathToOpen = getInputPath(1) + ".html"; //$NON-NLS-1$
			}
			writer.create(getInputPath(1) + ".html"); //$NON-NLS-1$
			writer.writeStartDocument();
			writer.writeStartElement("html"); //$NON-NLS-1$
			writer.writeStartElement("head"); //$NON-NLS-1$
			writer.writeRawXML("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"); //$NON-NLS-1$
			writer.writeRawXML("<style>td { font-family: monospace } td { vertical-align: top; white-space: pre } td.p { border-top-style: solid; border-top-width: 1px;}</style>"); //$NON-NLS-1$
			writer.writeEndElement(); // head
			writer.writeStartElement("body"); //$NON-NLS-1$
			writer.writeStartElement("p"); //$NON-NLS-1$
			writer.writeString("Translation Comparison");
			writer.writeEndElement();
			writer.writeStartElement("p"); //$NON-NLS-1$
			writer.writeString(String.format("Comparing %s (T2) against %s (T1).", getInputPath(1), getInputPath(0)));
			writer.writeEndElement();
			writer.writeStartElement("table"); //$NON-NLS-1$
		}
	}

	private void processStartDocument (StartDocument startDoc) {
		initializeData();
		isBaseMultilingual = startDoc.isMultilingual();
		// Move to start document
		Event event = synchronize(EventType.START_DOCUMENT);
		StartDocument res = (StartDocument)event.getResource();
		isToCompareMultilingual = res.isMultilingual();
	}
	
	private void processEndDocument () {
    	if ( inputToCompare != null ) inputToCompare.close();
    	if ( params.generateHTML ) {
			writer.writeEndElement(); // table
			writer.writeEndElement(); // body
			writer.writeEndElement(); // html
    		writer.close();
    	}
    }

	private Event synchronize (EventType untilType) {
		boolean found = false;
		Event event = null;
		while ( !found && inputToCompare.hasNext() ) {
			event = inputToCompare.next();
			found = (event.getEventType() == untilType);
    	}
   		if ( !found ) {
    		throw new RuntimeException("The document to compare is de-synchronized.");
    	}
   		return event;
	}
	
	private void processTextUnit (TextUnit tu1) {
		// Move to the next TU
		Event event = synchronize(EventType.TEXT_UNIT);
		// Skip non-translatable
		if ( !tu1.isTranslatable() ) return;
		TextUnit tu2 = (TextUnit)event.getResource();
		
		TextFragment srcFrag = null;
		if ( isBaseMultilingual ) {
			srcFrag = tu1.getSourceContent();
		}
		else {
			if ( isToCompareMultilingual ) srcFrag = tu2.getSourceContent();
		}
		
		// Get the text for the base translation
		TextFragment trgFrag1;
		if ( isBaseMultilingual ) trgFrag1 = tu1.getTargetContent(trgLang);
		else trgFrag1 = tu1.getSourceContent();

		// Get the text for the to-compare translation
		TextFragment trgFrag2;
		if ( isToCompareMultilingual ) trgFrag2 = tu2.getTargetContent(trgLang);
		else trgFrag2 = tu2.getSourceContent();
		
		// Do we have a base translation?
		if ( trgFrag1 == null ) {
			// No comparison if there is no base translation
			return;
		}
		// Do we have a translation to compare to?
		if ( trgFrag2 == null ) {
			// Create and empty entry
			trgFrag2 = new TextFragment();
		}
		
		// Compute the distance
		int score = matcher.compare(trgFrag1, trgFrag2, options);

		// Output in HTML
		if ( params.generateHTML ) {
			writer.writeRawXML("<tr><td class='p'>"); //$NON-NLS-1$
			// Output source if we have one
			if ( srcFrag != null ) {
				writer.writeString("Src:");
				writer.writeRawXML("</td>"); //$NON-NLS-1$
				writer.writeRawXML("<td class='p'>"); //$NON-NLS-1$
				writer.writeString(srcFrag.toString());
				writer.writeRawXML("</td></tr>\n"); //$NON-NLS-1$
				writer.writeRawXML("<tr><td>"); //$NON-NLS-1$
			}
			writer.writeString("T1:");
			writer.writeRawXML("</td>"); //$NON-NLS-1$
			if ( srcFrag != null ) writer.writeRawXML("<td>"); //$NON-NLS-1$
			else writer.writeRawXML("<td class='p'>"); //$NON-NLS-1$
			writer.writeString(trgFrag1.toString());
			writer.writeRawXML("</td></tr>"); //$NON-NLS-1$
			writer.writeRawXML("<tr><td>"); //$NON-NLS-1$
			writer.writeString("T2:");
			writer.writeRawXML("</td><td>"); //$NON-NLS-1$
			writer.writeString(trgFrag2.toString());
			writer.writeRawXML("</td></tr>"); //$NON-NLS-1$
			writer.writeRawXML("<tr><td>"); //$NON-NLS-1$
			writer.writeString("Score:");
			writer.writeRawXML("</td><td><b>"); //$NON-NLS-1$
			writer.writeString(String.valueOf(score));
			writer.writeRawXML("</b></td></tr>\n"); //$NON-NLS-1$
		}

		if ( params.generateTMX ) {
			TextUnit tmxTu = new TextUnit(tu1.getId());
			// Set the source: Use the tu1 if possible
			if ( isBaseMultilingual ) tmxTu.setSource(tu1.getSource());
			else if ( srcFrag != null ) {
				// Otherwise at least try to use the content of tu2
				tmxTu.setSourceContent(srcFrag);
			}
			tmxTu.setTargetContent(trgLang, trgFrag1);
			tmxTu.setTargetContent(trgLang+params.trgSuffix, trgFrag2);
			scoreProp.setValue(String.format("%03d", score));
			tmxTu.setTargetProperty(trgLang+params.trgSuffix, scoreProp);
			tmx.writeFullItem(tmxTu);
		}
	}

}
