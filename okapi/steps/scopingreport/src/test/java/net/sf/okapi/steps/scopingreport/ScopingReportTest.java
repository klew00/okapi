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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.filters.html.HtmlFilter;
import net.sf.okapi.lib.extra.pipelinebuilder.XBatch;
import net.sf.okapi.lib.extra.pipelinebuilder.XBatchItem;
import net.sf.okapi.lib.extra.pipelinebuilder.XParameter;
import net.sf.okapi.lib.extra.pipelinebuilder.XPipeline;
import net.sf.okapi.lib.extra.pipelinebuilder.XPipelineStep;
import net.sf.okapi.lib.extra.steps.EventLogger;
import net.sf.okapi.steps.common.RawDocumentToFilterEventsStep;
import net.sf.okapi.steps.wordcount.categorized.LocalContextExactMatchWordCountStep;
import net.sf.okapi.steps.wordcount.common.GMX;
import net.sf.okapi.steps.wordcount.common.Metrics;
import net.sf.okapi.steps.wordcount.common.MetricsAnnotation;

import org.junit.Test;

public class ScopingReportTest {

	private static final LocaleId EN = new LocaleId("en");
	private static final LocaleId ES = new LocaleId("es");
	
	public static void testPath(String path) {
		System.out.println(new File(path).getAbsolutePath());
	}
	
	@Test
	public void htmlReportTest() throws MalformedURLException {
		String pathBase = Util.getDirectoryName(this.getClass().getResource("aa324.html").getPath()) + "/";
		
		new XPipeline(
				"HTML report test",
				new XBatch(
						new XBatchItem(
								new URL("file", null, pathBase + "aa324.html"),
								"UTF-8",
								EN,
								ES),								
						new XBatchItem(
								new URL("file", null, pathBase + "form.html"),
								"UTF-8",
								EN,
								ES)
						),
				new RawDocumentToFilterEventsStep(new HtmlFilter()),
				new EventLogger(),
				new XPipelineStep(
						new ScopingReportStep(),
						new XParameter("projectName", "Test Scoping Report"),
						//new XParameter("outputURI", this.getClass().getResource("").toString() + "out/test_scoping_report.html")
						new XParameter("outputPath", pathBase + "out/test_scoping_report.html")
						)
		).execute();
		
		testPath(pathBase + "out/test_scoping_report.html");
	}
	
	@Test
	public void htmlReportTest2() throws URISyntaxException {
		String pathBase = Util.getDirectoryName(this.getClass().getResource("aa324.html").getPath()) + "/";
		
		StartDocument sd1 = new StartDocument("sd1");
		sd1.setName(new File(this.getClass().getResource("aa324.html").toURI()).getAbsolutePath());
		StartDocument sd2 = new StartDocument("sd2");
		sd2.setName(new File(this.getClass().getResource("form.html").toURI()).getAbsolutePath());
		
		ScopingReportStep srs = new ScopingReportStep();
		Parameters params = (Parameters) srs.getParameters();
		params.setProjectName("Test scoping report");
		params.setOutputPath(pathBase + "out/test_scoping_report2.html");
		
		srs.handleEvent(new Event(EventType.START_BATCH));
		srs.handleEvent(new Event(EventType.START_DOCUMENT, sd1));
		srs.handleEvent(new Event(EventType.START_DOCUMENT, sd2));
		
		Ending res = new Ending("end_batch");
		MetricsAnnotation ma = new MetricsAnnotation();
		res.setAnnotation(ma);
		Metrics m = ma.getMetrics();		
		
		m.setMetric(GMX.TotalWordCount, 1273);
		m.setMetric(LocalContextExactMatchWordCountStep.METRIC, 72);
		m.setMetric(GMX.ExactMatchedWordCount, 120);
		m.setMetric(GMX.FuzzyMatchedWordCount, 781);
		m.setMetric(GMX.RepetitionMatchedWordCount, 112);
		
		srs.handleEvent(new Event(EventType.END_BATCH, res));
		testPath(pathBase + "out/test_scoping_report2.html");
	}
}

