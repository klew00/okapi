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
import java.net.MalformedURLException;

import net.sf.okapi.applications.rainbow.lib.TMXWriter;
import net.sf.okapi.applications.rainbow.utilities.BaseFilterDrivenUtility;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.XMLWriter;
import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.FilterEventType;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;

public class Utility extends BaseFilterDrivenUtility {

	Parameters params;
	IFilter inputToCompare;
	TextMatcher matcher;
	XMLWriter writer;
	TMXWriter tmx;
	boolean isBaseMultilingual;
	boolean isToCompareMultilingual;
	
	public Utility () {
		params = new Parameters();
		needsSelfOutput = false;
	}
	
	public String getName () {
		return "oku_transcomparison";
	}
	
	public void preprocess () {
		matcher = new TextMatcher();
		writer = new XMLWriter();
		// Start TMX writer (one for all input documents)
		//if ( params.generateTMX ) {
			//tmx = new TMXWriter();
			//tmx.create(params.tmxPath);
			//tmx.writeStartDocument(sourceLanguage, targetLanguage, creationTool, creationToolVersion, segType, originalTMFormat, dataType)
		//}
	}
	
	public void postprocess () {
		matcher = null;
		if ( writer != null ) {
			writer.close();
			writer = null;
		}
		if ( tmx != null ) {
			tmx.close();
			tmx = null;
		}
		Runtime.getRuntime().gc();
	}
	
	public IParameters getParameters () {
		return params;
	}

	public boolean hasParameters () {
		return false;
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

	public FilterEvent handleEvent (FilterEvent event) {
		switch ( event.getEventType() ) {
		case START:
			processStart();
			break;
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

	private void processStart () {
		try {
			// Initialize the filter to read the translation to compare
			inputToCompare = fa.loadFilterFromFilterSettingsType1(paramsFolder,
				getInputFilterSettings(1), inputToCompare);
			inputToCompare.setOptions(srcLang, trgLang, getInputEncoding(1), false);
			File f = new File(getInputPath(1));
			inputToCompare.open(f.toURL());
			
			// Start HTML output
			if ( writer != null ) writer.close();
			writer.create(getInputPath(0) + ".html");
			writer.writeStartDocument();
			writer.writeStartElement("html");
			writer.writeStartElement("head");
			writer.writeRawXML("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
			writer.writeRawXML("<style>td { font-family: monospace } td.s { border-bottom-style: solid; border-bottom-width: 1px }</style>");
			writer.writeEndElement(); // head
			writer.writeStartElement("body");
			writer.writeStartElement("table");
		}
		catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	private void processStartDocument (StartDocument startDoc) {
		isBaseMultilingual = startDoc.isMultilingual();
		// Move to start document
		FilterEvent event = synchronize(FilterEventType.START_DOCUMENT);
		StartDocument res = (StartDocument)event.getResource();
		isToCompareMultilingual = res.isMultilingual();
	}
	
	private void processEndDocument () {
    	if ( inputToCompare != null ) inputToCompare.close();
    	if ( writer != null ) {
			writer.writeEndElement(); // table
			writer.writeEndElement(); // body
			writer.writeEndElement(); // html
    		writer.close();
    	}
    }

	private FilterEvent synchronize (FilterEventType untilType) {
		boolean found = false;
		FilterEvent event = null;
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
		FilterEvent event = synchronize(FilterEventType.TEXT_UNIT);
		// Skip non-translatable
		if ( !tu1.isTranslatable() ) return;
		TextUnit tu2 = (TextUnit)event.getResource();
		
		// Get the text for the base translation
		TextFragment text1;
		if ( isBaseMultilingual ) text1 = tu1.getTargetContent(trgLang);
		else  text1 = tu1.getSourceContent();

		// Get the text for the to-compare translation
		TextFragment text2;
		if ( isToCompareMultilingual ) text2 = tu2.getTargetContent(trgLang);
		else text2 = tu2.getSourceContent();
		
		// Do we have a base translation?
		if ( text1 == null ) {
			// No comparison if there is no base translation
			return;
		}
		// Do we have a translation to compare to?
		if ( text2 == null ) {
			// Create and empty entry
			text2 = new TextFragment();
		}
		
		// Compute the distance
		int n = matcher.compare(text1, text2);

		// Output in HTML
		if ( isBaseMultilingual ) {
			writer.writeStartElement("tr");
			writer.writeStartElement("td");
			writer.writeString("src="+tu1.getSourceContent().toString());
			writer.writeEndElement(); // td
			writer.writeEndElement(); // tr
		}
		writer.writeStartElement("tr");
		writer.writeStartElement("td");
		writer.writeString("t1="+text1.toString());
		writer.writeEndElement(); // td
		writer.writeEndElement(); // tr
		writer.writeStartElement("tr");
		writer.writeStartElement("td");
		writer.writeString("t2="+text2.toString());
		writer.writeEndElement(); // td
		writer.writeEndElement(); // tr
		writer.writeStartElement("tr");
		writer.writeStartElement("td");
		writer.writeAttributeString("class", "s");
		writer.writeString("score="+String.valueOf(n));
		writer.writeEndElement(); // td
		writer.writeEndElementLineBreak(); // tr
		
		if ( params.generateTMX ) {
			//TextUnit tmxTu = new TextUnit(tu1.getId());
			//tmx.writeItem(tmxTu, null)
		}
	}

}
