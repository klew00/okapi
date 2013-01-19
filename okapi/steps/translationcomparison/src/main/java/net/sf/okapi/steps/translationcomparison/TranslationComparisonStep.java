/*===========================================================================
  Copyright (C) 2009-2012 by the Okapi Framework contributors
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

import java.io.File;
import java.net.URI;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.XMLWriter;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.common.filterwriter.TMXWriter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.lib.search.lucene.analysis.AlphabeticNgramTokenizer;
import net.sf.okapi.lib.translation.TextMatcher;
import net.sf.okapi.steps.wordcount.common.GMX;
import net.sf.okapi.steps.wordcount.common.Metrics;
import net.sf.okapi.steps.wordcount.common.MetricsAnnotation;

@UsingParameters(Parameters.class)
public class TranslationComparisonStep extends BasePipelineStep {

	private Parameters params;
	private IFilter filter2;
	private IFilter filter3;
	private TextMatcher matcher;
	private XMLWriter writer;
	private TMXWriter tmx;
	private boolean isBaseMultilingual;
	private boolean isInput2Multilingual;
	private boolean isInput3Multilingual;
	private String pathToOpen;
	private int options;
	private Property score1to2Prop;
	private Property score1to3Prop;
	private Property fuzzyScore1to2Prop;
	private Property fuzzyScore1to3Prop;
	private long scoreTotal1to2;
	private long scoreTotal1to3;
	private long scoreTotal2to3;
	private long fuzzyScoreTotal1to2;
	private long fuzzyScoreTotal1to3;
	private long fuzzyScoreTotal2to3;
	private int itemCount;
	private IFilterConfigurationMapper fcMapper;
	private LocaleId targetLocale;
	private LocaleId targetLocale2Extra;
	private LocaleId targetLocale3Extra;
	private LocaleId sourceLocale;
	private URI inputURI;
	private RawDocument rawDoc2;
	private RawDocument rawDoc3;
	private GenericContent fmt;
	private String rootDir;
	private String inputRootDir;
	private AlphabeticNgramTokenizer tokenizer;
	
	private long wcTotal;
	private int bracket1;
	private int bracket2;
	private int bracket3;
	private int wcBracket1;
	private int wcBracket2;
	private int wcBracket3;

	public TranslationComparisonStep () {
		params = new Parameters();
		fmt = new GenericContent();
	}
	
	@StepParameterMapping(parameterType = StepParameterType.FILTER_CONFIGURATION_MAPPER)
	public void setFilterConfigurationMapper (IFilterConfigurationMapper fcMapper) {
		this.fcMapper = fcMapper;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.SOURCE_LOCALE)
	public void setSourceLocale (LocaleId sourceLocale) {
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
		this.rawDoc2 = secondInput;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.THIRD_INPUT_RAWDOC)
	public void setThirdInput (RawDocument thirdInput) {
		this.rawDoc3 = thirdInput;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.ROOT_DIRECTORY)
	public void setRootDirectory (String rootDir) {
		this.rootDir = rootDir;
	}

	@StepParameterMapping(parameterType = StepParameterType.INPUT_ROOT_DIRECTORY)
	public void setInputRootDirectory (String inputRootDir) {
		this.inputRootDir = inputRootDir;
	}

	@Override
	public String getName () {
		return "Translation Comparison";
	}

	@Override
	public String getDescription () {
		return "Compare the translated text units between several documents. "
			+ "Expects: filter events. Sends back: filter events.";
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
		// Both strings are in the target language.
		matcher = new TextMatcher(targetLocale, targetLocale);
		
		if ( params.isGenerateHTML() ) {
			writer = new XMLWriter(getOutputFilename());
		}
		// Start TMX writer (one for all input documents)
		if ( params.isGenerateTMX() ) {
			String resolvedPath = Util.fillRootDirectoryVariable(params.getTmxPath(), rootDir);
			resolvedPath = Util.fillInputRootDirectoryVariable(resolvedPath, inputRootDir);
			resolvedPath = LocaleId.replaceVariables(resolvedPath, sourceLocale, targetLocale);
			tmx = new TMXWriter(resolvedPath);
			tmx.writeStartDocument(sourceLocale, targetLocale,
				getClass().getName(), null, null, null, null);
		}
		pathToOpen = null;
		score1to2Prop = new Property("Txt::Score", "", false);
		fuzzyScore1to2Prop = new Property("Txt::FuzzyScore", "", false);
		targetLocale2Extra = LocaleId.fromString(targetLocale.toString()+params.getTarget2Suffix());
		score1to3Prop = new Property("Txt::Score1to3", "", false);
		fuzzyScore1to3Prop = new Property("Txt::FuzzyScore1to3", "", false);
		targetLocale3Extra = LocaleId.fromString(targetLocale.toString()+params.getTarget3Suffix());
		
		options = 0;
		if ( !params.isCaseSensitive() ) options |= TextMatcher.IGNORE_CASE;
		if ( !params.isWhitespaceSensitive() ) options |= TextMatcher.IGNORE_WHITESPACES;
		if ( !params.isPunctuationSensitive() ) options |= TextMatcher.IGNORE_PUNCTUATION;
		
		tokenizer = net.sf.okapi.lib.search.lucene.scorer.Util.createNgramTokenizer(3, targetLocale);
		
		return event;
	}
	
	@Override
	protected Event handleEndBatch (Event event) {
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
			Util.openURL((new File(pathToOpen)).getAbsolutePath());
		}
		
		return event;
	}
	
	@Override
	protected Event handleStartDocument (Event event1) {
		StartDocument startDoc1 = (StartDocument)event1.getResource();
		initializeDocumentData();
		isBaseMultilingual = startDoc1.isMultilingual();
		
		// Move to start document for second input
		Event event2 = synchronize(filter2, EventType.START_DOCUMENT);
		StartDocument startDoc2 = (StartDocument)event2.getResource();
		isInput2Multilingual = startDoc2.isMultilingual();
		
		// Move to start document for third input
		if ( filter3 != null ) {
			Event event3 = synchronize(filter3, EventType.START_DOCUMENT);
			StartDocument startDoc3 = (StartDocument)event3.getResource();
			isInput3Multilingual = startDoc3.isMultilingual();
		}
		
		scoreTotal1to2 = 0;
		scoreTotal1to3 = 0;
		scoreTotal2to3 = 0;
		fuzzyScoreTotal1to2 = 0;
		fuzzyScoreTotal1to3 = 0;
		fuzzyScoreTotal2to3 = 0;
		wcTotal = 0;
		bracket1 = 0;
		bracket2 = 0;
		bracket3 = 0;
		wcBracket1 = 0;
		wcBracket2 = 0;
		wcBracket3 = 0;
		
		itemCount = 0;
		
		return event1;
	}
	
	@Override
	protected Event handleEndDocument (Event event) {
    	if ( filter2 != null ) {
    		filter2.close();
    	}
    	if ( filter3 != null ) {
    		filter3.close();
    	}
    	if ( params.isGenerateHTML() ) {
			writer.writeEndElement(); // table
    		writer.writeElementString("p", String.format("", itemCount));
    		if ( itemCount > 0 ) {

    			writer.writeElementString("p", String.format("Number of segments = %d", itemCount));
    			
    			writer.writeStartElement("table"); //$NON-NLS-1$
    			writer.writeRawXML("<tr><td>"); //$NON-NLS-1$
    			writer.writeString("Average Scores:");
    			writer.writeRawXML("</td><td><b>"); //$NON-NLS-1$
    			writer.writeString(String.format(" %s to %s = %.2f",
    					params.getDocument1Label(), params.getDocument2Label(), (float)scoreTotal1to2 / itemCount));
    			if (scoreTotal1to3 > 0){
        			writer.writeString(String.format(",  %s to %s = %.2f,  ",
        					params.getDocument1Label(), params.getDocument3Label(), (float)scoreTotal1to3 / itemCount));
        			writer.writeString(String.format("%s to %s = %.2f",
        					params.getDocument2Label(), params.getDocument3Label(), (float)scoreTotal2to3 / itemCount));
    			}
    			writer.writeRawXML("</b></td></tr>\n"); //$NON-NLS-1$
    			
    			writer.writeRawXML("<tr><td>"); //$NON-NLS-1$
    			writer.writeString("Average Fuzzy Scores:");
    			writer.writeRawXML("</td><td><b>"); //$NON-NLS-1$
    			writer.writeString(String.format(" %s to %s = %.2f",
    					params.getDocument1Label(), params.getDocument2Label(), (float)fuzzyScoreTotal1to2 / itemCount));
    			if (scoreTotal1to3 > 0){
        			writer.writeString(String.format(",  %s to %s = %.2f,  ",
        					params.getDocument1Label(), params.getDocument3Label(), (float)fuzzyScoreTotal1to3 / itemCount));
        			writer.writeString(String.format("%s to %s = %.2f",
        					params.getDocument2Label(), params.getDocument3Label(), (float)fuzzyScoreTotal2to3 / itemCount));
    			}
    			writer.writeRawXML("</b></td></tr>\n"); //$NON-NLS-1$
    			
    			writer.writeRawXML("<tr><td>"); //$NON-NLS-1$
    			writer.writeString("Average Word Count:");
    			writer.writeRawXML("</td><td><b>"); //$NON-NLS-1$
    			writer.writeString(String.format("%.2f", (float)wcTotal / itemCount));
    			writer.writeRawXML("</b></td></tr>\n"); //$NON-NLS-1$
    			
    			writer.writeEndElement(); // table
    			
    			//--matrix1--
    			writer.writeElementString("p", " ");
    			
    			writer.writeStartElement("table"); //$NON-NLS-1$
    			writer.writeRawXML("<tr><th style=\"text-align: left; width: 100px;\"> </th><th style=\"text-align: left; width: 100px;\">Segments </th><th style=\"text-align: left; width: 100px;\">% </th><th style=\"text-align: left; width: 100px;\">Words </th><th style=\"text-align: left; width: 100px;\">% </th></tr>");
    			
    			writer.writeRawXML("<tr><td>100</td><td>"); //$NON-NLS-1$
    			writer.writeString(String.format("%d", bracket1));
    			writer.writeRawXML("</td><td>"); //$NON-NLS-1$
    			writer.writeString(((itemCount == 0) ? "NA" : String.format("%.0f", (float)bracket1/itemCount*100)));
    			writer.writeRawXML("</td><td>"); //$NON-NLS-1$
    			writer.writeString(String.format("%d", wcBracket1));
    			writer.writeRawXML("</td><td>"); //$NON-NLS-1$
    			writer.writeString(((wcTotal == 0) ? "NA" : String.format("%.0f", (float)wcBracket1/wcTotal*100)));
    			writer.writeRawXML("</td></tr>\n"); //$NON-NLS-1$
  			
    			writer.writeRawXML("<tr><td>99 - 75</td><td>"); //$NON-NLS-1$
    			writer.writeString(String.format("%d", bracket2));
    			writer.writeRawXML("</td><td>"); //$NON-NLS-1$
    			writer.writeString(((itemCount == 0) ? "NA" : String.format("%.0f", (float)bracket2/itemCount*100)));
    			writer.writeRawXML("</td><td>"); //$NON-NLS-1$
    			writer.writeString(String.format("%d", wcBracket2));
    			writer.writeRawXML("</td><td>"); //$NON-NLS-1$
    			writer.writeString(((wcTotal == 0) ? "NA" : String.format("%.0f", (float)wcBracket2/wcTotal*100)));
    			writer.writeRawXML("</td></tr>\n"); //$NON-NLS-1$
    			
    			writer.writeRawXML("<tr><td>75 - 0</td><td>"); //$NON-NLS-1$
    			writer.writeString(String.format("%d", bracket3));
    			writer.writeRawXML("</td><td>"); //$NON-NLS-1$
    			writer.writeString(((itemCount == 0) ? "NA" : String.format("%.0f", (float)bracket3/itemCount*100)));
    			writer.writeRawXML("</td><td>"); //$NON-NLS-1$
    			writer.writeString(String.format("%d", wcBracket3));
    			writer.writeRawXML("</td><td>"); //$NON-NLS-1$
    			writer.writeString(((wcTotal == 0) ? "NA" : String.format("%.0f", (float)wcBracket3/wcTotal*100)));
    			writer.writeRawXML("</td></tr>\n"); //$NON-NLS-1$
    			
    			writer.writeRawXML("<tr><td>Total</td><td>"); //$NON-NLS-1$
    			writer.writeString(String.format("%d", itemCount));    			
    			writer.writeRawXML("</td><td>"); //$NON-NLS-1$
    			writer.writeString("");
    			writer.writeRawXML("</td><td>"); //$NON-NLS-1$
    			writer.writeString(String.format("%d", wcTotal));
    			writer.writeRawXML("</td><td>"); //$NON-NLS-1$
    			writer.writeString(" ");
    			writer.writeRawXML("</td></tr>\n"); //$NON-NLS-1$
    			writer.writeEndElement(); // table
    		}
			writer.writeEndElement(); // body
			writer.writeEndElement(); // html
    		writer.close();
    	}
    	
    	return event;
	}
	
	@Override
	protected Event handleTextUnit (Event event1) {
		ITextUnit tu1 = event1.getTextUnit();
		// Move to the next TU
		Event event2 = synchronize(filter2, EventType.TEXT_UNIT);
		Event event3 = null;
		if ( filter3 != null ) {
			event3 = synchronize(filter3, EventType.TEXT_UNIT);
		}
		// Skip non-translatable
		if ( !tu1.isTranslatable() ) return event1;
		
		ITextUnit tu2 = event2.getTextUnit();
		ITextUnit tu3 = null;
		if ( event3 != null ) {
			tu3 = event3.getTextUnit();
		}

		TextFragment srcFrag = null;
		if ( isBaseMultilingual ) {
			if ( tu1.getSource().contentIsOneSegment() ) {
				srcFrag = tu1.getSource().getFirstContent();
			}
			else {
				srcFrag = tu1.getSource().getUnSegmentedContentCopy();
			}
		}
		else {
			if ( isInput2Multilingual ) {
				if ( tu2.getSource().contentIsOneSegment() ) {
					srcFrag = tu2.getSource().getFirstContent();
				}
				else {
					srcFrag = tu2.getSource().getUnSegmentedContentCopy();
				}
			}
			else if (( tu3 != null ) && isInput3Multilingual ) {
				if ( tu3.getSource().contentIsOneSegment() ) {
					srcFrag = tu3.getSource().getFirstContent();
				}
				else {
					srcFrag = tu3.getSource().getUnSegmentedContentCopy();
				}
			}
		}
		
		TextContainer trgCont;
		// Get the text for the base translation
		TextFragment trgFrag1;
		if ( isBaseMultilingual ) {
			trgCont = tu1.getTarget(targetLocale);
			if ( trgCont == null ) {
				throw new RuntimeException(String.format("Missing '%s' entry for text unit id='%s' in base document.",
					targetLocale.toString(), tu1.getId()));
			}
			if ( trgCont.contentIsOneSegment() ) {
				trgFrag1 = trgCont.getFirstContent();
			}
			else {
				trgFrag1 = trgCont.getUnSegmentedContentCopy();
			}
		}
		else {
			if ( tu1.getSource().contentIsOneSegment() ) {
				trgFrag1 = tu1.getSource().getFirstContent();
			}
			else {
				trgFrag1 = tu1.getSource().getUnSegmentedContentCopy();
			}
		}

		// Get the text for the to-compare translation 1
		TextFragment trgFrag2;
		if ( isInput2Multilingual ) {
			trgCont = tu2.getTarget(targetLocale);
			if ( trgCont == null ) {
				throw new RuntimeException(String.format("Missing '%s' entry for text unit id='%s' in document 2.",
					targetLocale.toString(), tu2.getId()));
			}
			if ( trgCont.contentIsOneSegment() ) {
				trgFrag2 = trgCont.getFirstContent();
			}
			else {
				trgFrag2 = trgCont.getUnSegmentedContentCopy();
			}
		}
		else {
			if ( tu2.getSource().contentIsOneSegment() ) {
				trgFrag2 = tu2.getSource().getFirstContent();
			}
			else {
				trgFrag2 = tu2.getSource().getUnSegmentedContentCopy();
			}
		}
		
		// Get the text for the to-compare translation 2
		TextFragment trgFrag3 = null;
		if ( tu3 != null ) {
			if ( isInput3Multilingual ) {
				trgCont = tu3.getTarget(targetLocale);
				if ( trgCont == null ) {
					throw new RuntimeException(String.format("Missing '%s' entry for text unit id='%s' in document 3.",
						targetLocale.toString(), tu3.getId()));
				}
				if ( trgCont.contentIsOneSegment() ) {
					trgFrag3 = trgCont.getFirstContent();
				}
				else {
					trgFrag3 = trgCont.getUnSegmentedContentCopy();
				}
			}
			else {
				if ( tu3.getSource().contentIsOneSegment() ) {
					trgFrag3 = tu3.getSource().getFirstContent();
				}
				else {
					trgFrag3 = tu3.getSource().getUnSegmentedContentCopy();
				}
			}
		}
		
		// Do we have a base translation?
		if ( trgFrag1 == null ) {
			// No comparison if there is no base translation
			return event1;
		}
		// Do we have a translation to compare to?
		if ( trgFrag2 == null ) {
			// Create and empty entry
			trgFrag2 = new TextFragment();
		}
		if ( event3 != null ) {
			if ( trgFrag3 == null ) {
				// Create and empty entry
				trgFrag3 = new TextFragment();
			}
		}
		
		// Compute the distance
		int score1to2 = matcher.compare(trgFrag1, trgFrag2, options);
		int fuzzyScore1to2 = Math.round(net.sf.okapi.lib.search.lucene.scorer.Util.calculateNgramDiceCoefficient(
				trgFrag1.getText(), trgFrag2.getText(), tokenizer));
		int score1to3 = -1;
		int fuzzyScore1to3 = -1;
		int score2to3 = -1;
		int fuzzyScore2to3 = -1;
		if ( event3 != null ) {
			score1to3 = matcher.compare(trgFrag1, trgFrag3, options);
			fuzzyScore1to3 = Math.round(net.sf.okapi.lib.search.lucene.scorer.Util.calculateNgramDiceCoefficient(
					trgFrag1.getText(), trgFrag3.getText(), tokenizer));
			score2to3 = matcher.compare(trgFrag2, trgFrag3, options);
			fuzzyScore2to3 = Math.round(net.sf.okapi.lib.search.lucene.scorer.Util.calculateNgramDiceCoefficient(
					trgFrag2.getText(), trgFrag3.getText(), tokenizer));
		}
		
		// Store the scores for the average
		scoreTotal1to2 += score1to2;
		scoreTotal1to3 += score1to3;
		scoreTotal2to3 += score2to3;
		fuzzyScoreTotal1to2 += fuzzyScore1to2;
		fuzzyScoreTotal1to3 += fuzzyScore1to3;
		fuzzyScoreTotal2to3 += fuzzyScore2to3;
		
		MetricsAnnotation sma = tu1.getSource().getAnnotation(MetricsAnnotation.class);
		long srcWC = 0;
		if (sma != null) {
			Metrics m = sma.getMetrics();
			srcWC = m.getMetric(GMX.TotalWordCount);
			wcTotal += srcWC;
		}
		
		// Populate the matrix
		if (score1to2 == 100){
			bracket1++;
			wcBracket1 += srcWC;
		}else if (score1to2 >= 75){
			bracket2++;
			wcBracket2 += srcWC;
		}else{
			bracket3++;
			wcBracket3 += srcWC;
		}
		
		itemCount++;

		// Output in HTML
		if ( params.isGenerateHTML() ) {
			writer.writeRawXML("<tr><td class='p'>"); //$NON-NLS-1$
			// Output source if we have one
			if ( srcFrag != null ) {
				writer.writeString("Src:");
				writer.writeRawXML("</td>"); //$NON-NLS-1$
				writer.writeRawXML("<td class='p'>"); //$NON-NLS-1$
				fmt.setContent(srcFrag);
				writer.writeString(fmt.toString(!params.getGenericCodes()));
				
				writer.writeRawXML("</td></tr>\n"); //$NON-NLS-1$
				writer.writeRawXML("<tr><td>"); //$NON-NLS-1$
			}
			writer.writeString(params.getDocument1Label()+":");
			writer.writeRawXML("</td>"); //$NON-NLS-1$
			if ( srcFrag != null ) writer.writeRawXML("<td>"); //$NON-NLS-1$
			else writer.writeRawXML("<td class='p'>"); //$NON-NLS-1$
			fmt.setContent(trgFrag1);
			writer.writeString(fmt.toString(!params.getGenericCodes()));
			writer.writeRawXML("</td></tr>"); //$NON-NLS-1$
			// T2
			writer.writeRawXML("<tr><td>"); //$NON-NLS-1$
			writer.writeString(params.getDocument2Label()+":");
			writer.writeRawXML("</td><td>"); //$NON-NLS-1$
			fmt.setContent(trgFrag2);
			writer.writeString(fmt.toString(!params.getGenericCodes()));
			writer.writeRawXML("</td></tr>"); //$NON-NLS-1$
			// T3
			if ( filter3 != null ) {
				writer.writeRawXML("<tr><td>"); //$NON-NLS-1$
				writer.writeString(params.getDocument3Label()+":");
				writer.writeRawXML("</td><td>"); //$NON-NLS-1$
				fmt.setContent(trgFrag3);
				writer.writeString(fmt.toString(!params.getGenericCodes()));
				writer.writeRawXML("</td></tr>"); //$NON-NLS-1$
			}
			writer.writeRawXML("<tr><td>"); //$NON-NLS-1$
			writer.writeString("Scores:");
			writer.writeRawXML("</td><td><b>"); //$NON-NLS-1$
			writer.writeString(String.format("%s to %s = %d",
				params.getDocument1Label(), params.getDocument2Label(), score1to2));
			if ( score1to3 > -1 ) {
				writer.writeString(String.format(",  %s to %s = %d",
					params.getDocument1Label(), params.getDocument3Label(), score1to3));
				writer.writeString(String.format(",  %s to %s = %d",
					params.getDocument2Label(), params.getDocument3Label(), score2to3));
			}
			writer.writeRawXML("</b></td></tr>\n"); //$NON-NLS-1$
			
			writer.writeRawXML("<tr><td>"); //$NON-NLS-1$
			writer.writeString("Fuzzy Scores:");
			writer.writeRawXML("</td><td><b>"); //$NON-NLS-1$
			writer.writeString(String.format("%s to %s = %d",
				params.getDocument1Label(), params.getDocument2Label(), fuzzyScore1to2));
			if ( score1to3 > -1 ) {
				writer.writeString(String.format(",  %s to %s = %d",
					params.getDocument1Label(), params.getDocument3Label(), fuzzyScore1to3));
				writer.writeString(String.format(",  %s to %s = %d",
					params.getDocument2Label(), params.getDocument3Label(), fuzzyScore2to3));
			}
			writer.writeRawXML("</b></td></tr>\n"); //$NON-NLS-1$

			if (sma != null) {
				writer.writeRawXML("<tr><td>Src Word Count:</td><td><b>");
				writer.writeString(String.format("%d", srcWC));
				writer.writeRawXML("</b></td></tr>\n");
			}
		}
		if ( params.isGenerateTMX() ) {
			ITextUnit tmxTu = new TextUnit(tu1.getId());
			// Set the source: Use the tu1 if possible
			if ( isBaseMultilingual ) tmxTu.setSource(tu1.getSource());
			else if ( srcFrag != null ) {
				// Otherwise at least try to use the content of tu2
				tmxTu.setSourceContent(srcFrag);
			}
			tmxTu.setTargetContent(targetLocale, trgFrag1);
			tmxTu.setTargetContent(targetLocale2Extra, trgFrag2);
			score1to2Prop.setValue(String.format("%03d", score1to2));
			fuzzyScore1to2Prop.setValue(String.format("%03d", fuzzyScore1to2));
			tmxTu.setTargetProperty(targetLocale2Extra, score1to2Prop);
			tmxTu.setTargetProperty(targetLocale2Extra, fuzzyScore1to2Prop);						
			if ( filter3 != null ) {
				tmxTu.setTargetContent(targetLocale3Extra, trgFrag3);
				score1to3Prop.setValue(String.format("%03d", score1to3));
				fuzzyScore1to3Prop.setValue(String.format("%03d", fuzzyScore1to3));
				tmxTu.setTargetProperty(targetLocale3Extra, score1to3Prop);
				tmxTu.setTargetProperty(targetLocale3Extra, fuzzyScore1to3Prop);
			}
			tmx.writeTUFull(tmxTu);
		}
		
		return event1;
	}

    private String getOutputFilename(){
       return inputURI.getPath() + ".html"; //$NON-NLS-1$
    }

	private void initializeDocumentData () {
		// Initialize the filter to read the translation 1 to compare
		filter2 = fcMapper.createFilter(rawDoc2.getFilterConfigId(), filter2);
		// Open the second input for this batch item
		filter2.open(rawDoc2);

		if ( rawDoc3 != null ) {
			// Initialize the filter to read the translation 2 to compare
			filter3 = fcMapper.createFilter(
				rawDoc3.getFilterConfigId(), filter3);
			// Open the third input for this batch item
			filter3.open(rawDoc3);
		}
			
		// Start HTML output
		if ( writer != null ) writer.close();
		if ( params.isGenerateHTML() ) {
			// Use the to-compare file for the output name
			if ( pathToOpen == null ) {
				pathToOpen = getOutputFilename();
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
			writer.writeString(String.format("Base document: %s (%s)",
				inputURI.getPath(), params.getDocument1Label()));
			writer.writeRawXML("<br>");
			writer.writeString(String.format("Comparison 1: %s (%s)",
				rawDoc2.getInputURI().getPath(), params.getDocument2Label()));
			if ( rawDoc3 != null ) {
				writer.writeRawXML("<br>");
				writer.writeString(String.format("Comparison 2: %s (%s)",
					rawDoc3.getInputURI().getPath(), params.getDocument3Label()));
			}
			writer.writeString(".");
			writer.writeEndElement();
			writer.writeStartElement("table"); //$NON-NLS-1$
		}
	}

	private Event synchronize (IFilter filter,
		EventType untilType)
	{
		boolean found = false;
		Event event = null;
		while ( !found && filter.hasNext() ) {
			event = filter.next();
			found = (event.getEventType() == untilType);
    	}
   		if ( !found ) {
    		throw new RuntimeException("The document to compare is de-synchronized.");
    	}
   		return event;
	}
	
}
