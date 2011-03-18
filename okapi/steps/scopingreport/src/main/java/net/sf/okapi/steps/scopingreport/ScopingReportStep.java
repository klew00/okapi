/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.scopingreport;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Logger;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.pipeline.IPipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.query.MatchType;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.lib.extra.steps.CompoundStep;
import net.sf.okapi.lib.reporting.ReportGenerator;
import net.sf.okapi.steps.wordcount.categorized.gmx.GMXAlphanumericOnlyTextUnitWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.gmx.GMXExactMatchedWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.gmx.GMXFuzzyMatchWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.gmx.GMXLeveragedMatchedWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.gmx.GMXMeasurementOnlyTextUnitWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.gmx.GMXNumericOnlyTextUnitWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.gmx.GMXProtectedWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.gmx.GMXRepetitionMatchedWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.okapi.ConcordanceWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.okapi.ExactDocumentContextMatchWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.okapi.ExactLocalContextMatchWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.okapi.ExactMatchWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.okapi.ExactPreviousVersionMatchWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.okapi.ExactRepairedWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.okapi.ExactStructuralMatchWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.okapi.ExactTextOnlyPreviousVersionMatchWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.okapi.ExactTextOnlyUniqueIdMatchWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.okapi.ExactTextOnlyWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.okapi.ExactUniqueIdMatchWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.okapi.FuzzyMatchWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.okapi.FuzzyPreviousVersionMatchWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.okapi.FuzzyRepairedWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.okapi.FuzzyUniqueIdMatchWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.okapi.MTWordCountStep;
import net.sf.okapi.steps.wordcount.categorized.okapi.PhraseAssembledWordCountStep;
import net.sf.okapi.steps.wordcount.common.BaseCounter;
import net.sf.okapi.steps.wordcount.common.GMX;

@UsingParameters(Parameters.class)
public class ScopingReportStep extends CompoundStep {

	/**
	 * General report fields
	 */
	public static final String PROJECT_NAME = "PROJECT_NAME";
	public static final String PROJECT_DATE = "PROJECT_DATE";
	public static final String PROJECT_SOURCE_LOCALE = "PROJECT_SOURCE_LOCALE";
	public static final String PROJECT_TARGET_LOCALE = "PROJECT_TARGET_LOCALE";	
	public static final String PROJECT_TOTAL_WORD_COUNT = "PROJECT_TOTAL_WORD_COUNT";
	public static final String PROJECT_NONTRANSLATABLE_WORD_COUNT = "PROJECT_NONTRANSLATABLE_WORD_COUNT"; 
	public static final String PROJECT_TRANSLATABLE_WORD_COUNT = "PROJECT_TRANSLATABLE_WORD_COUNT";
	
	public static final String ITEM_NAME = "ITEM_NAME";
	public static final String ITEM_SOURCE_LOCALE = "ITEM_SOURCE_LOCALE";
	public static final String ITEM_TARGET_LOCALE = "ITEM_TARGET_LOCALE";
	public static final String ITEM_TOTAL_WORD_COUNT = "ITEM_TOTAL_WORD_COUNT";
	public static final String ITEM_NONTRANSLATABLE_WORD_COUNT = "ITEM_NONTRANSLATABLE_WORD_COUNT"; 
	public static final String ITEM_TRANSLATABLE_WORD_COUNT = "ITEM_TRANSLATABLE_WORD_COUNT";
	
	/**
	 * Report fields for word counts of the entire project (GMX categories)
	 */	
	public static final String PROJECT_GMX_PROTECTED_WORD_COUNT = "PROJECT_GMX_PROTECTED_WORD_COUNT";
	public static final String PROJECT_GMX_EXACT_MATCHED_WORD_COUNT = "PROJECT_GMX_EXACT_MATCHED_WORD_COUNT";
	public static final String PROJECT_GMX_LEVERAGED_MATCHED_WORD_COUNT = "PROJECT_GMX_LEVERAGED_MATCHED_WORD_COUNT";
	public static final String PROJECT_GMX_REPETITION_MATCHED_WORD_COUNT = "PROJECT_GMX_REPETITION_MATCHED_WORD_COUNT";
	public static final String PROJECT_GMX_FUZZY_MATCHED_WORD_COUNT = "PROJECT_GMX_FUZZY_MATCHED_WORD_COUNT";
	public static final String PROJECT_GMX_ALPHANUMERIC_ONLY_TEXT_UNIT_WORD_COUNT = "PROJECT_GMX_ALPHANUMERIC_ONLY_TEXT_UNIT_WORD_COUNT";
	public static final String PROJECT_GMX_NUMERIC_ONLY_TEXT_UNIT_WORD_COUNT = "PROJECT_GMX_NUMERIC_ONLY_TEXT_UNIT_WORD_COUNT";
	public static final String PROJECT_GMX_MEASUREMENT_ONLY_TEXT_UNIT_WORD_COUNT = "PROJECT_GMX_MEASUREMENT_ONLY_TEXT_UNIT_WORD_COUNT";
	public static final String PROJECT_GMX_NOMATCH = "PROJECT_GMX_NOMATCH";
	
	/**
	 * Report fields for word counts of individual items (GMX categories)
	 */	
	public static final String ITEM_GMX_PROTECTED_WORD_COUNT = "ITEM_GMX_PROTECTED_WORD_COUNT";
	public static final String ITEM_GMX_EXACT_MATCHED_WORD_COUNT = "ITEM_GMX_EXACT_MATCHED_WORD_COUNT";
	public static final String ITEM_GMX_LEVERAGED_MATCHED_WORD_COUNT = "ITEM_GMX_LEVERAGED_MATCHED_WORD_COUNT";
	public static final String ITEM_GMX_REPETITION_MATCHED_WORD_COUNT = "ITEM_GMX_REPETITION_MATCHED_WORD_COUNT";
	public static final String ITEM_GMX_FUZZY_MATCHED_WORD_COUNT = "ITEM_GMX_FUZZY_MATCHED_WORD_COUNT";
	public static final String ITEM_GMX_ALPHANUMERIC_ONLY_TEXT_UNIT_WORD_COUNT = "ITEM_GMX_ALPHANUMERIC_ONLY_TEXT_UNIT_WORD_COUNT";
	public static final String ITEM_GMX_NUMERIC_ONLY_TEXT_UNIT_WORD_COUNT = "ITEM_GMX_NUMERIC_ONLY_TEXT_UNIT_WORD_COUNT";
	public static final String ITEM_GMX_MEASUREMENT_ONLY_TEXT_UNIT_WORD_COUNT = "ITEM_GMX_MEASUREMENT_ONLY_TEXT_UNIT_WORD_COUNT";
	public static final String ITEM_GMX_NOMATCH = "ITEM_GMX_NOMATCH";
	
	/**
	 * Report fields for word counts of the entire project (Okapi categories)
	 */	
	public static final String PROJECT_EXACT_UNIQUE_ID = "PROJECT_EXACT_UNIQUE_ID";	
	public static final String PROJECT_EXACT_PREVIOUS_VERSION = "PROJECT_EXACT_PREVIOUS_VERSION";
	public static final String PROJECT_EXACT_LOCAL_CONTEXT = "PROJECT_EXACT_LOCAL_CONTEXT";
	public static final String PROJECT_EXACT_DOCUMENT_CONTEXT = "PROJECT_EXACT_DOCUMENT_CONTEXT";
	public static final String PROJECT_EXACT_STRUCTURAL = "PROJECT_EXACT_STRUCTURAL";
	public static final String PROJECT_EXACT = "PROJECT_EXACT";
	public static final String PROJECT_EXACT_TEXT_ONLY_PREVIOUS_VERSION = "PROJECT_EXACT_TEXT_ONLY_PREVIOUS_VERSION";
	public static final String PROJECT_EXACT_TEXT_ONLY_UNIQUE_ID = "PROJECT_EXACT_TEXT_ONLY_UNIQUE_ID";
	public static final String PROJECT_EXACT_TEXT_ONLY = "PROJECT_EXACT_TEXT_ONLY";	
	public static final String PROJECT_EXACT_REPAIRED = "PROJECT_EXACT_REPAIRED";
	public static final String PROJECT_FUZZY_PREVIOUS_VERSION = "PROJECT_FUZZY_PREVIOUS_VERSION";
	public static final String PROJECT_FUZZY_UNIQUE_ID = "PROJECT_FUZZY_UNIQUE_ID";
	public static final String PROJECT_FUZZY = "PROJECT_FUZZY";
	public static final String PROJECT_FUZZY_REPAIRED = "PROJECT_FUZZY_REPAIRED";
	public static final String PROJECT_PHRASE_ASSEMBLED = "PROJECT_PHRASE_ASSEMBLED";
	public static final String PROJECT_MT = "PROJECT_MT";
	public static final String PROJECT_CONCORDANCE = "PROJECT_CONCORDANCE";
	public static final String PROJECT_NOMATCH = "PROJECT_NOMATCH";
	
	/**
	 * Report fields for word counts of individual items (Okapi categories)
	 */	
	public static final String ITEM_EXACT_UNIQUE_ID = "ITEM_EXACT_UNIQUE_ID";	
	public static final String ITEM_EXACT_PREVIOUS_VERSION = "ITEM_EXACT_PREVIOUS_VERSION";
	public static final String ITEM_EXACT_LOCAL_CONTEXT = "ITEM_EXACT_LOCAL_CONTEXT";
	public static final String ITEM_EXACT_DOCUMENT_CONTEXT = "ITEM_EXACT_DOCUMENT_CONTEXT";
	public static final String ITEM_EXACT_STRUCTURAL = "ITEM_EXACT_STRUCTURAL";
	public static final String ITEM_EXACT = "ITEM_EXACT";
	public static final String ITEM_EXACT_TEXT_ONLY_PREVIOUS_VERSION = "ITEM_EXACT_TEXT_ONLY_PREVIOUS_VERSION";
	public static final String ITEM_EXACT_TEXT_ONLY_UNIQUE_ID = "ITEM_EXACT_TEXT_ONLY_UNIQUE_ID";
	public static final String ITEM_EXACT_TEXT_ONLY = "ITEM_EXACT_TEXT_ONLY";	
	public static final String ITEM_EXACT_REPAIRED = "ITEM_EXACT_REPAIRED";
	public static final String ITEM_FUZZY_PREVIOUS_VERSION = "ITEM_FUZZY_PREVIOUS_VERSION";
	public static final String ITEM_FUZZY_UNIQUE_ID = "ITEM_FUZZY_UNIQUE_ID";
	public static final String ITEM_FUZZY = "ITEM_FUZZY";
	public static final String ITEM_FUZZY_REPAIRED = "ITEM_FUZZY_REPAIRED";
	public static final String ITEM_PHRASE_ASSEMBLED = "ITEM_PHRASE_ASSEMBLED";
	public static final String ITEM_MT = "ITEM_MT";
	public static final String ITEM_CONCORDANCE = "ITEM_CONCORDANCE";
	public static final String ITEM_NOMATCH = "ITEM_NOMATCH";
	
	
	private static final String DEFAULT_TEMPLATE = "scoping_report.html";
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	private Parameters params;
	private ReportGenerator gen;
	private String rootDir;
	
	public ScopingReportStep() {
		super();
		params = new Parameters();
		setParameters(params);
		setName("Scoping Report");
		setDescription("Create a template-based scoping report based on word count and leverage annotations."
			+" Expects: filter events. Sends back: filter events.");
		gen = new ReportGenerator(getTemplateStream());
	}
	
	@StepParameterMapping(parameterType = StepParameterType.ROOT_DIRECTORY)
	public void setRootDirectory (String rootDir) {
		this.rootDir = rootDir;
	}

	protected InputStream getTemplateStream() {
		if (params.useDefaultTemplate()) {
			return this.getClass().getResourceAsStream(DEFAULT_TEMPLATE);
		}
		else {
			try {
				return new BufferedInputStream(new FileInputStream(new File(params.getCustomTemplateURI())));
			} catch (IOException e) {
				logger.warning("Error opening the custom template, default one is used");
				return this.getClass().getResourceAsStream(DEFAULT_TEMPLATE);
			} 
		}
	}

	@Override
	protected void addSteps(LinkedList<IPipelineStep> list) {
		// Excluded to be a required external step, default parameters were changed to conform with the scoping report.
//		list.add(
//				new XPipelineStep(
//						new WordCountStep(),
//						new XParameter("countInBatchItems", true),
//						new XParameter("countInBatch", true)
//						)
//				);

		// GMX word count categories
		list.add(new GMXProtectedWordCountStep());
		list.add(new GMXExactMatchedWordCountStep());
		list.add(new GMXLeveragedMatchedWordCountStep());
		list.add(new GMXRepetitionMatchedWordCountStep());
		list.add(new GMXFuzzyMatchWordCountStep());		
		list.add(new GMXAlphanumericOnlyTextUnitWordCountStep());
		list.add(new GMXNumericOnlyTextUnitWordCountStep());
		list.add(new GMXMeasurementOnlyTextUnitWordCountStep());
		
		// Okapi word count categories
		list.add(new ExactUniqueIdMatchWordCountStep());
		list.add(new ExactPreviousVersionMatchWordCountStep());
		list.add(new ExactLocalContextMatchWordCountStep());
		list.add(new ExactDocumentContextMatchWordCountStep());
		list.add(new ExactStructuralMatchWordCountStep());
		list.add(new ExactMatchWordCountStep());
		list.add(new ExactTextOnlyPreviousVersionMatchWordCountStep());
		list.add(new ExactTextOnlyUniqueIdMatchWordCountStep());
		list.add(new ExactTextOnlyWordCountStep());
		list.add(new ExactRepairedWordCountStep());
		list.add(new FuzzyPreviousVersionMatchWordCountStep());
		list.add(new FuzzyUniqueIdMatchWordCountStep());
		list.add(new FuzzyMatchWordCountStep());
		list.add(new FuzzyRepairedWordCountStep());
		list.add(new PhraseAssembledWordCountStep());
		list.add(new MTWordCountStep());
		list.add(new ConcordanceWordCountStep());
	}

	@Override
	protected Event handleStartBatch(Event event) {
		params = getParameters(Parameters.class);
		gen.reset();
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
		gen.setField(PROJECT_NAME, params.getProjectName());
		gen.setField(PROJECT_DATE, df.format(new Date()));
		
		// Values as they were set by the pipeline via step parameters 
		gen.setField(PROJECT_SOURCE_LOCALE, getSourceLocale().toString());
		gen.setField(PROJECT_TARGET_LOCALE, getTargetLocale().toString());
		return super.handleStartBatch(event);
	}
	
	/**
	 * Sets per-project fields
	 */
	@Override
	protected Event handleEndBatch(Event event) {
		IResource res = event.getResource();
		if (res != null) {
			gen.setField(PROJECT_TOTAL_WORD_COUNT, BaseCounter.getCount(res, GMX.TotalWordCount));
			// gen.setField(PROJECT_NONTRANSLATABLE_WORD_COUNT, 0); // TODO Calculate
			// gen.setField(PROJECT_TRANSLATABLE_WORD_COUNT, 0); // TODO Calculate
			
			gen.setField(PROJECT_GMX_PROTECTED_WORD_COUNT, BaseCounter.getCount(res, GMX.ProtectedWordCount));
			gen.setField(PROJECT_GMX_EXACT_MATCHED_WORD_COUNT, BaseCounter.getCount(res, GMX.ExactMatchedWordCount));
			gen.setField(PROJECT_GMX_LEVERAGED_MATCHED_WORD_COUNT, BaseCounter.getCount(res, GMX.LeveragedMatchedWordCount));
			gen.setField(PROJECT_GMX_REPETITION_MATCHED_WORD_COUNT, BaseCounter.getCount(res, GMX.RepetitionMatchedWordCount));
			gen.setField(PROJECT_GMX_FUZZY_MATCHED_WORD_COUNT, BaseCounter.getCount(res, GMX.FuzzyMatchedWordCount));
			gen.setField(PROJECT_GMX_ALPHANUMERIC_ONLY_TEXT_UNIT_WORD_COUNT, BaseCounter.getCount(res, GMX.AlphanumericOnlyTextUnitWordCount));
			gen.setField(PROJECT_GMX_NUMERIC_ONLY_TEXT_UNIT_WORD_COUNT, BaseCounter.getCount(res, GMX.NumericOnlyTextUnitWordCount));
			gen.setField(PROJECT_GMX_MEASUREMENT_ONLY_TEXT_UNIT_WORD_COUNT, BaseCounter.getCount(res, GMX.MeasurementOnlyTextUnitWordCount));
			//gen.setField(PROJECT_GMX_NOMATCH, BaseCounter.getCount(res, 0)); // TODO Calculate
			
			gen.setField(PROJECT_EXACT_UNIQUE_ID, BaseCounter.getCount(res, MatchType.EXACT_UNIQUE_ID.name()));
			gen.setField(PROJECT_EXACT_PREVIOUS_VERSION, BaseCounter.getCount(res, MatchType.EXACT_PREVIOUS_VERSION.name()));
			gen.setField(PROJECT_EXACT_LOCAL_CONTEXT, BaseCounter.getCount(res, MatchType.EXACT_LOCAL_CONTEXT.name()));
			gen.setField(PROJECT_EXACT_DOCUMENT_CONTEXT, BaseCounter.getCount(res, MatchType.EXACT_DOCUMENT_CONTEXT.name()));
			gen.setField(PROJECT_EXACT_STRUCTURAL, BaseCounter.getCount(res, MatchType.EXACT_STRUCTURAL.name()));
			gen.setField(PROJECT_EXACT, BaseCounter.getCount(res, MatchType.EXACT.name()));
			gen.setField(PROJECT_EXACT_TEXT_ONLY_PREVIOUS_VERSION, BaseCounter.getCount(res, MatchType.EXACT_TEXT_ONLY_PREVIOUS_VERSION.name()));
			gen.setField(PROJECT_EXACT_TEXT_ONLY_UNIQUE_ID, BaseCounter.getCount(res, MatchType.EXACT_TEXT_ONLY_UNIQUE_ID.name()));
			gen.setField(PROJECT_EXACT_TEXT_ONLY, BaseCounter.getCount(res, MatchType.EXACT_TEXT_ONLY.name()));
			gen.setField(PROJECT_EXACT_REPAIRED, BaseCounter.getCount(res, MatchType.EXACT_REPAIRED.name()));
			gen.setField(PROJECT_FUZZY_PREVIOUS_VERSION, BaseCounter.getCount(res, MatchType.FUZZY_PREVIOUS_VERSION.name()));
			gen.setField(PROJECT_FUZZY_UNIQUE_ID, BaseCounter.getCount(res, MatchType.FUZZY_UNIQUE_ID.name()));
			gen.setField(PROJECT_FUZZY, BaseCounter.getCount(res, MatchType.FUZZY.name()));
			gen.setField(PROJECT_FUZZY_REPAIRED, BaseCounter.getCount(res, MatchType.FUZZY_REPAIRED.name()));
			gen.setField(PROJECT_PHRASE_ASSEMBLED, BaseCounter.getCount(res, MatchType.PHRASE_ASSEMBLED.name()));
			gen.setField(PROJECT_MT, BaseCounter.getCount(res, MatchType.MT.name()));
			gen.setField(PROJECT_CONCORDANCE, BaseCounter.getCount(res, MatchType.CONCORDANCE.name()));
			// gen.setField(PROJECT_NOMATCH, BaseCounter.getCount(res, 0); // TODO Calculate
			
			setEndBatchFields(gen, res);
		}		
		// Generate report
		String report = gen.generate();
		String outPath = Util.fillRootDirectoryVariable(params.getOutputPath(), rootDir);
		outPath = LocaleId.replaceVariables(outPath, getSourceLocale(), getTargetLocale());
		File outFile = new File(outPath);
		Util.createDirectories(outFile.getAbsolutePath());
		
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"));
			writer.write(report);
			writer.close();
		} catch (UnsupportedEncodingException e) {
			throw new OkapiIOException(e);
		} catch (FileNotFoundException e) {
			throw new OkapiIOException(e);
		} catch (IOException e) {
			throw new OkapiIOException(e);
		}
		
		return super.handleEndBatch(event);
	}

	// To be overridden in subclasses
	protected void setEndBatchFields(ReportGenerator gen, IResource res) {
	}
	
	// To be overridden in subclasses
	protected void setEndBatchItemFields(ReportGenerator gen, IResource res) {
	}
	
	// To be overridden in subclasses
	protected void setStartDocumentFields(ReportGenerator gen, IResource res) {
	}
	
	// To be overridden in subclasses
	protected void setEndDocumentFields(ReportGenerator gen, IResource res) {
	}

	@Override
	protected Event handleEndBatchItem(Event event) {
		IResource res = event.getResource();
		if (res != null) {
			gen.setField(ITEM_TOTAL_WORD_COUNT, BaseCounter.getCount(res, GMX.TotalWordCount));
			// gen.setField(ITEM_NONTRANSLATABLE_WORD_COUNT, 0); // TODO Calculate
			// gen.setField(ITEM_TRANSLATABLE_WORD_COUNT, 0); // TODO Calculate
			
			gen.setField(ITEM_GMX_PROTECTED_WORD_COUNT, BaseCounter.getCount(res, GMX.ProtectedWordCount));
			gen.setField(ITEM_GMX_EXACT_MATCHED_WORD_COUNT, BaseCounter.getCount(res, GMX.ExactMatchedWordCount));
			gen.setField(ITEM_GMX_LEVERAGED_MATCHED_WORD_COUNT, BaseCounter.getCount(res, GMX.LeveragedMatchedWordCount));
			gen.setField(ITEM_GMX_REPETITION_MATCHED_WORD_COUNT, BaseCounter.getCount(res, GMX.RepetitionMatchedWordCount));
			gen.setField(ITEM_GMX_FUZZY_MATCHED_WORD_COUNT, BaseCounter.getCount(res, GMX.FuzzyMatchedWordCount));
			gen.setField(ITEM_GMX_ALPHANUMERIC_ONLY_TEXT_UNIT_WORD_COUNT, BaseCounter.getCount(res, GMX.AlphanumericOnlyTextUnitWordCount));
			gen.setField(ITEM_GMX_NUMERIC_ONLY_TEXT_UNIT_WORD_COUNT, BaseCounter.getCount(res, GMX.NumericOnlyTextUnitWordCount));
			gen.setField(ITEM_GMX_MEASUREMENT_ONLY_TEXT_UNIT_WORD_COUNT, BaseCounter.getCount(res, GMX.MeasurementOnlyTextUnitWordCount));
			//gen.setField(ITEM_GMX_NOMATCH, BaseCounter.getCount(res, 0)); // TODO Calculate
			
			gen.setField(ITEM_EXACT_UNIQUE_ID, BaseCounter.getCount(res, MatchType.EXACT_UNIQUE_ID.name()));
			gen.setField(ITEM_EXACT_PREVIOUS_VERSION, BaseCounter.getCount(res, MatchType.EXACT_PREVIOUS_VERSION.name()));
			gen.setField(ITEM_EXACT_LOCAL_CONTEXT, BaseCounter.getCount(res, MatchType.EXACT_LOCAL_CONTEXT.name()));
			gen.setField(ITEM_EXACT_DOCUMENT_CONTEXT, BaseCounter.getCount(res, MatchType.EXACT_DOCUMENT_CONTEXT.name()));
			gen.setField(ITEM_EXACT_STRUCTURAL, BaseCounter.getCount(res, MatchType.EXACT_STRUCTURAL.name()));
			gen.setField(ITEM_EXACT, BaseCounter.getCount(res, MatchType.EXACT.name()));
			gen.setField(ITEM_EXACT_TEXT_ONLY_PREVIOUS_VERSION, BaseCounter.getCount(res, MatchType.EXACT_TEXT_ONLY_PREVIOUS_VERSION.name()));
			gen.setField(ITEM_EXACT_TEXT_ONLY_UNIQUE_ID, BaseCounter.getCount(res, MatchType.EXACT_TEXT_ONLY_UNIQUE_ID.name()));
			gen.setField(ITEM_EXACT_TEXT_ONLY, BaseCounter.getCount(res, MatchType.EXACT_TEXT_ONLY.name()));
			gen.setField(ITEM_EXACT_REPAIRED, BaseCounter.getCount(res, MatchType.EXACT_REPAIRED.name()));
			gen.setField(ITEM_FUZZY_PREVIOUS_VERSION, BaseCounter.getCount(res, MatchType.FUZZY_PREVIOUS_VERSION.name()));
			gen.setField(ITEM_FUZZY_UNIQUE_ID, BaseCounter.getCount(res, MatchType.FUZZY_UNIQUE_ID.name()));
			gen.setField(ITEM_FUZZY, BaseCounter.getCount(res, MatchType.FUZZY.name()));
			gen.setField(ITEM_FUZZY_REPAIRED, BaseCounter.getCount(res, MatchType.FUZZY_REPAIRED.name()));
			gen.setField(ITEM_PHRASE_ASSEMBLED, BaseCounter.getCount(res, MatchType.PHRASE_ASSEMBLED.name()));
			gen.setField(ITEM_MT, BaseCounter.getCount(res, MatchType.MT.name()));
			gen.setField(ITEM_CONCORDANCE, BaseCounter.getCount(res, MatchType.CONCORDANCE.name()));
			// gen.setField(ITEM_NOMATCH, BaseCounter.getCount(res, 0); // TODO Calculate
			
			setEndBatchItemFields(gen, res);
		}			
		
		return super.handleEndBatchItem(event);
	}
	
	@Override
	protected Event handleStartDocument(Event event) {
		Event ev = super.handleStartDocument(event); // Sets srcLoc and other stuff
		
		StartDocument sd = (StartDocument) event.getResource();
		if (sd != null) {
			String fname = sd.getName();
			gen.setField(ITEM_NAME, new File(fname).getAbsolutePath());
			
			setStartDocumentFields(gen, sd);
		}		
		return ev;
	}
	
	@Override
	protected Event handleEndDocument(Event event) {
		// src/target might have changed during processing of the document, so let's set them here and not in handleStartDocument()
		gen.setField(ITEM_SOURCE_LOCALE, getSourceLocale().toString());
		gen.setField(ITEM_TARGET_LOCALE, getTargetLocale().toString());
		
		setEndDocumentFields(gen, event.getResource());		
		
		return super.handleEndDocument(event);
	}
	
}
