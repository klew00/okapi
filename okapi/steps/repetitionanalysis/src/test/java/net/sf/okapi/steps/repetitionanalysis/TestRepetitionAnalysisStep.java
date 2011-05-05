/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package net.sf.okapi.steps.repetitionanalysis;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import net.sf.okapi.common.ClassUtil;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.filters.plaintext.PlainTextFilter;
import net.sf.okapi.lib.extra.pipelinebuilder.XBatch;
import net.sf.okapi.lib.extra.pipelinebuilder.XBatchItem;
import net.sf.okapi.lib.extra.pipelinebuilder.XParameter;
import net.sf.okapi.lib.extra.pipelinebuilder.XPipeline;
import net.sf.okapi.lib.extra.pipelinebuilder.XPipelineStep;
import net.sf.okapi.lib.extra.steps.EventLogger;
import net.sf.okapi.steps.common.RawDocumentToFilterEventsStep;
import net.sf.okapi.steps.segmentation.Parameters;
import net.sf.okapi.steps.segmentation.SegmentationStep;
import net.sf.okapi.tm.pensieve.common.MetadataType;
import net.sf.okapi.tm.pensieve.common.TmHit;
import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import net.sf.okapi.tm.pensieve.common.TranslationUnitVariant;
import net.sf.okapi.tm.pensieve.seeker.ITmSeeker;
import net.sf.okapi.tm.pensieve.seeker.TmSeekerFactory;
import net.sf.okapi.tm.pensieve.writer.ITmWriter;
import net.sf.okapi.tm.pensieve.writer.TmWriterFactory;

import org.junit.Before;
import org.junit.Test;

public class TestRepetitionAnalysisStep {
	private String tmDir;
	private String pathBase;
	private ITmWriter tmWriter;
	private ITmSeeker currentTm;
	
	@Before
	public void setup() {
		pathBase = Util.ensureSeparator(ClassUtil.getTargetPath(this.getClass()), true);
		tmDir = pathBase + "tm/";
		Util.createDirectories(tmDir);
		//System.out.println((new File(tmDir)).getAbsolutePath());
	}
	
	@Test
	public void testTmReadWrite() {
		tmWriter = TmWriterFactory.createFileBasedTmWriter(tmDir, true);
		currentTm = TmSeekerFactory.createFileBasedTmSeeker(tmDir);
		
		TranslationUnit unit1 = new TranslationUnit(				
				new TranslationUnitVariant(LocaleId.ENGLISH, new TextFragment("source1")),
				new TranslationUnitVariant(LocaleId.GERMAN, new TextFragment("target1")));
		unit1.setMetadataValue(MetadataType.ID, "seg1");
		tmWriter.indexTranslationUnit(unit1);		
				
		TranslationUnit unit2 = new TranslationUnit(				
				new TranslationUnitVariant(LocaleId.ENGLISH, new TextFragment("source2")),
				new TranslationUnitVariant(LocaleId.GERMAN, new TextFragment("target2")));
		unit2.setMetadataValue(MetadataType.ID, "seg2");
		tmWriter.indexTranslationUnit(unit2);		
		
		tmWriter.commit();
		
		List<TmHit> hits = currentTm.searchFuzzy(new TextFragment("source1"), 95, 1, null); 
		if (hits.size() > 0) {
			TmHit hit = hits.get(0);
			assertEquals("seg1", hit.getTu().getMetadataValue(MetadataType.ID));
		}
		
		hits = currentTm.searchFuzzy(new TextFragment("source2"), 95, 1, null); 
		if (hits.size() > 0) {
			TmHit hit = hits.get(0);
			assertEquals("seg2", hit.getTu().getMetadataValue(MetadataType.ID));
		}
		
		currentTm.close();
		tmWriter.close();
	}
		
	// @Test
	public void testExactRepetitions() {
		String fname = "test1.txt";
		try {
			new XPipeline(
					"Test pipeline for repetition analysis step",
					new XBatch(						
							new XBatchItem(
									new URL("file", null, pathBase + fname),
									"UTF-8",
									LocaleId.ENGLISH,
									LocaleId.GERMAN)						
							),
							
					new RawDocumentToFilterEventsStep(new PlainTextFilter()),
					//new EventLogger(),
					new XPipelineStep(
							new SegmentationStep(),
							new XParameter("copySource", true),
							new XParameter("sourceSrxPath", pathBase + "default.srx"),
							//new Parameter("sourceSrxPath", pathBase + "myRules.srx")
							new XParameter("trimSrcLeadingWS", Parameters.TRIM_YES),
							new XParameter("trimSrcTrailingWS", Parameters.TRIM_YES)
					),
					new RepetitionAnalysisStep()
			).execute();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	// @Test
	public void testFuzzyRepetitions() {
		String fname = "test1.txt";
		try {
			new XPipeline(
					"Test pipeline for repetition analysis step",
					new XBatch(						
							new XBatchItem(
									new URL("file", null, pathBase + fname),
									"UTF-8",
									LocaleId.ENGLISH,
									LocaleId.GERMAN)						
							),							
					new RawDocumentToFilterEventsStep(new PlainTextFilter()),
					//new EventLogger(),
					new XPipelineStep(
							new SegmentationStep(),
							new XParameter("copySource", true),
							new XParameter("sourceSrxPath", pathBase + "default.srx"),
							//new Parameter("sourceSrxPath", pathBase + "myRules.srx")
							new XParameter("trimSrcLeadingWS", Parameters.TRIM_YES),
							new XParameter("trimSrcTrailingWS", Parameters.TRIM_YES)
					),
					new XPipelineStep(
							new RepetitionAnalysisStep(),
							new XParameter("fuzzyThreshold", 90)
					)
			).execute();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
