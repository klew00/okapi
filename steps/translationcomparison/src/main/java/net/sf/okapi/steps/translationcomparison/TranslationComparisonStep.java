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

package net.sf.okapi.steps.translationcomparison;

import java.net.URI;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.XMLWriter;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.filterwriter.TMXWriter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.lib.translation.TextMatcher;

public class TranslationComparisonStep extends BasePipelineStep {

	private Parameters params;
	private IFilter input1ToCompare;
	private TextMatcher matcher;
	private XMLWriter writer;
	private TMXWriter tmx;
	private boolean isBaseMultilingual;
	private boolean isToCompareMultilingual;
	private String pathToOpen;
	private int options;
	private Property scoreProp;
	private long scoreTotal;
	private int itemCount;
	private IFilterConfigurationMapper fcMapper;
	private LocaleId targetLocale;
	private LocaleId targetLocaleExtra;
	private LocaleId sourceLocale;
	private URI inputURI;
	private RawDocument secondInput;

	public TranslationComparisonStep () {
		params = new Parameters();
	}
	
	@StepParameterMapping(parameterType = StepParameterType.FILTER_CONFIGURATION_MAPPER)
	public void setFilterConfigurationMapper (IFilterConfigurationMapper fcMapper) {
		this.fcMapper = fcMapper;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.SOURCE_LOCALE)
	public void setsourceLocale (LocaleId sourceLocale) {
		this.sourceLocale = sourceLocale;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.TARGET_LOCALE)
	public void setTargetLocale (LocaleId targetLocale) {
		this.targetLocale = targetLocale;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.INPUT_URI)
	public void setInputURI (URI inputURI) {
		this.inputURI = inputURI;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.SECOND_INPUT_RAWDOC)
	public void setSecondInput (RawDocument secondInput) {
		this.secondInput = secondInput;
	}
	
	public String getName () {
		return "Translation Comparison";
	}

	public String getDescription () {
		return "Compare the translated text units between several documents.";
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
	protected void handleStartBatch (Event event) {
		// Both strings are in the target language.
		matcher = new TextMatcher(targetLocale, targetLocale);
		
		if ( params.isGenerateHTML() ) {
			writer = new XMLWriter(getOutputFilename());
		}
		// Start TMX writer (one for all input documents)
		if ( params.isGenerateTMX() ) {
			tmx = new TMXWriter(params.getTmxPath());
			tmx.writeStartDocument(sourceLocale, targetLocale,
				getClass().getName(), null, null, null, null);
		}
		pathToOpen = null;
		scoreProp = new Property("Txt::Score", "", false);
		targetLocaleExtra = LocaleId.fromString(targetLocale.toString()+params.getTargetSuffix());
		
		options = 0;
		if ( !params.isCaseSensitive() ) options |= TextMatcher.IGNORE_CASE;
		if ( !params.isWhitespaceSensitive() ) options |= TextMatcher.IGNORE_WHITESPACES;
		if ( !params.isPunctuationSensitive() ) options |= TextMatcher.IGNORE_PUNCTUATION;
	}
	
	protected void handleEndBatch (Event event) {
		matcher = null;
		if ( params.isGenerateHTML() && ( writer != null )) {
			writer.close();
			writer = null;
		}
		if ( params.isGenerateTMX() && ( tmx != null )) {
			tmx.writeEndDocument();
			tmx.close();
			tmx = null;
		}
		Runtime.getRuntime().gc();
		if ( params.isAutoOpen() && ( pathToOpen != null )) {
			//TODO: Replace this: getContext().setString("outputFile",  pathToOpen);
		}
	}
	
	@Override
	protected void handleStartDocument (Event event1) {
		StartDocument startDoc1 = (StartDocument)event1.getResource();
		initializeDocumentData();
		isBaseMultilingual = startDoc1.isMultilingual();
		// Move to start document
		Event event2 = synchronize(EventType.START_DOCUMENT);
		StartDocument startDoc2 = (StartDocument)event2.getResource();
		isToCompareMultilingual = startDoc2.isMultilingual();
		scoreTotal = 0;
		itemCount = 0;
	}
	
	@Override
	protected void handleEndDocument (Event event) {
    	if ( input1ToCompare != null ) {
    		input1ToCompare.close();
    	}
    	if ( params.isGenerateHTML() ) {
			writer.writeEndElement(); // table
    		writer.writeElementString("p", String.format("", itemCount));
    		if ( itemCount > 0 ) {
    			writer.writeElementString("p", String.format("Number of items = %d. Average score = %.2f",
    				itemCount, (float)scoreTotal / itemCount));
    		}
			writer.writeEndElement(); // body
			writer.writeEndElement(); // html
    		writer.close();
    	}
	}
	
	@Override
	protected void handleTextUnit (Event event1) {
		TextUnit tu1 = (TextUnit)event1.getResource();
		// Move to the next TU
		Event event2 = synchronize(EventType.TEXT_UNIT);
		// Skip non-translatable
		if ( !tu1.isTranslatable() ) return;
		
		TextUnit tu2 = (TextUnit)event2.getResource();
		TextFragment srcFrag = null;
		if ( isBaseMultilingual ) {
			srcFrag = tu1.getSourceContent();
		}
		else {
			if ( isToCompareMultilingual ) srcFrag = tu2.getSourceContent();
		}
		
		// Get the text for the base translation
		TextFragment trgFrag1;
		if ( isBaseMultilingual ) trgFrag1 = tu1.getTargetContent(targetLocale);
		else trgFrag1 = tu1.getSourceContent();

		// Get the text for the to-compare translation
		TextFragment trgFrag2;
		if ( isToCompareMultilingual ) trgFrag2 = tu2.getTargetContent(targetLocale);
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
		// Store the scores for the average
		scoreTotal += score;
		itemCount++;

		// Output in HTML
		if ( params.isGenerateHTML() ) {
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

		if ( params.isGenerateTMX() ) {
			TextUnit tmxTu = new TextUnit(tu1.getId());
			// Set the source: Use the tu1 if possible
			if ( isBaseMultilingual ) tmxTu.setSource(tu1.getSource());
			else if ( srcFrag != null ) {
				// Otherwise at least try to use the content of tu2
				tmxTu.setSourceContent(srcFrag);
			}
			tmxTu.setTargetContent(targetLocale, trgFrag1);
			tmxTu.setTargetContent(targetLocaleExtra, trgFrag2);
			scoreProp.setValue(String.format("%03d", score));
			tmxTu.setTargetProperty(targetLocaleExtra, scoreProp);
			tmx.writeTUFull(tmxTu);
		}
	}

    private String getOutputFilename(){
       return inputURI.getPath() + ".html"; //$NON-NLS-1$
    }

	private void initializeDocumentData () {
		// Initialize the filter to read the translation to compare
		input1ToCompare = fcMapper.createFilter(
			secondInput.getFilterConfigId(), input1ToCompare);
		
		// Open the second input for this batch item
		input1ToCompare.open(secondInput);
			
		// Start HTML output
		if ( writer != null ) writer.close();
		if ( params.isGenerateHTML() ) {
			// Use the to-compare file for the output name
			if ( pathToOpen == null ) {
				pathToOpen = secondInput.getInputURI().toString();
				pathToOpen += ".html";
			}
			writer = new XMLWriter(getOutputFilename()); //$NON-NLS-1$
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
			writer.writeString(String.format("Comparing %s (T2) against %s (T1).",
				secondInput.getInputURI(), inputURI));
			writer.writeEndElement();
			writer.writeStartElement("table"); //$NON-NLS-1$
		}
	}

	private Event synchronize (EventType untilType) {
		boolean found = false;
		Event event = null;
		while ( !found && input1ToCompare.hasNext() ) {
			event = input1ToCompare.next();
			found = (event.getEventType() == untilType);
    	}
   		if ( !found ) {
    		throw new RuntimeException("The document to compare is de-synchronized.");
    	}
   		return event;
	}
	
}
