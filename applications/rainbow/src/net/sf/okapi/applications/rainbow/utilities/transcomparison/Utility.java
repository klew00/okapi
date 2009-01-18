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
	
	public Utility () {
		params = new Parameters();
		needsSelfOutput = false;
	}
	
	public String getName () {
		return "oku_transcomparison";
	}
	
	public void preprocess () {
		matcher = new TextMatcher();
		if ( params.generateHTML ) {
			writer = new XMLWriter();
		}
		// Start TMX writer (one for all input documents)
		if ( params.generateTMX ) {
			tmx = new TMXWriter();
			tmx.create(params.tmxPath);
			tmx.writeStartDocument(srcLang, trgLang, "TODO", "TODO", "TODO", "TODO", "TODO");
		}
		pathToOpen = null;
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
		if ( params.openOutput && ( pathToOpen != null )) {
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
				writer.writeRawXML("<style>td { font-family: monospace } td { vertical-align: top; } td.p { border-top-style: solid; border-top-width: 1px;}</style>"); //$NON-NLS-1$
				writer.writeEndElement(); // head
				writer.writeStartElement("body"); //$NON-NLS-1$
				writer.writeStartElement("table"); //$NON-NLS-1$
			}
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
    	if ( params.generateHTML ) {
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
		if ( params.generateHTML ) {
			writer.writeRawXML("<tr><td class='p'>"); //$NON-NLS-1$
			if ( isBaseMultilingual ) {
				writer.writeString("src:");
				writer.writeRawXML("</td>"); //$NON-NLS-1$
				writer.writeRawXML("<td class='p'>"); //$NON-NLS-1$
				writer.writeString(tu1.getSourceContent().toString());
				writer.writeRawXML("</td></tr>\n"); //$NON-NLS-1$
				writer.writeRawXML("<tr><td>"); //$NON-NLS-1$
			}
			writer.writeString("t1:");
			writer.writeRawXML("</td>"); //$NON-NLS-1$
			if ( isBaseMultilingual ) writer.writeRawXML("<td>"); //$NON-NLS-1$
			else writer.writeRawXML("<td class='p'>"); //$NON-NLS-1$
			writer.writeString(text1.toString());
			writer.writeRawXML("</td></tr>"); //$NON-NLS-1$
			writer.writeRawXML("<tr><td>"); //$NON-NLS-1$
			writer.writeString("t2:");
			writer.writeRawXML("</td><td>"); //$NON-NLS-1$
			writer.writeString(text2.toString());
			writer.writeRawXML("</td></tr>"); //$NON-NLS-1$
			writer.writeRawXML("<tr><td>"); //$NON-NLS-1$
			writer.writeString("score:");
			writer.writeRawXML("</td><td><b>"); //$NON-NLS-1$
			writer.writeString(String.valueOf(n));
			writer.writeRawXML("</b></td></tr>\n"); //$NON-NLS-1$
		}
		
		if ( params.generateTMX ) {
			TextUnit tmxTu = new TextUnit(tu1.getId());
			if ( isBaseMultilingual ) tmxTu.setSource(tu1.getSource());
			tmxTu.setTargetContent(trgLang, text1);
			tmxTu.setTargetContent(trgLang+"-2", text2);
			tmx.writeItem(tmxTu, null);
		}
	}

}
