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

import java.net.MalformedURLException;
import java.net.URL;

import net.sf.okapi.common.ClassUtil;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.filters.plaintext.PlainTextFilter;
import net.sf.okapi.lib.extra.pipelinebuilder.XBatch;
import net.sf.okapi.lib.extra.pipelinebuilder.XBatchItem;
import net.sf.okapi.lib.extra.pipelinebuilder.XParameter;
import net.sf.okapi.lib.extra.pipelinebuilder.XPipeline;
import net.sf.okapi.lib.extra.pipelinebuilder.XPipelineStep;
import net.sf.okapi.steps.common.RawDocumentToFilterEventsStep;
import net.sf.okapi.steps.segmentation.Parameters;
import net.sf.okapi.steps.segmentation.SegmentationStep;

import org.junit.Test;

public class TestRepetitionAnalysisStep {
	private String pathBase = Util.ensureSeparator(ClassUtil.getTargetPath(this.getClass()), true);

	@Test
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

	@Test
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
							new XParameter("fuzzyThreshold", 40)
					)
			).execute();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

}
