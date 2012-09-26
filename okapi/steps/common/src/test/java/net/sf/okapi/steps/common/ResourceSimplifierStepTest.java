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

package net.sf.okapi.steps.common;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.InputDocument;
import net.sf.okapi.common.filters.RoundTripComparison;
import net.sf.okapi.filters.html.HtmlFilter;
import net.sf.okapi.lib.extra.pipelinebuilder.XBatch;
import net.sf.okapi.lib.extra.pipelinebuilder.XBatchItem;
import net.sf.okapi.lib.extra.pipelinebuilder.XPipeline;
import net.sf.okapi.lib.extra.steps.DocumentPartLogger;
import net.sf.okapi.lib.extra.steps.EventListBuilderStep;
import net.sf.okapi.lib.extra.steps.EventLogger;
import net.sf.okapi.lib.extra.steps.TextUnitLogger;
import net.sf.okapi.lib.extra.steps.TuDpSsfLogger;

import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceSimplifierStepTest {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static final LocaleId ENUS = new LocaleId("en", "us");
	
	@Test
	public void testDoubleExtraction () throws URISyntaxException, IOException {
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();				
		String pathBase = Util.getDirectoryName(this.getClass().getResource("aa324.html").toURI().getPath()) + "/";
		
		list.add(new InputDocument(pathBase + "aa324.html", null));
		list.add(new InputDocument(pathBase + "form.html", null));
		list.add(new InputDocument(pathBase + "W3CHTMHLTest1.html", null));
		list.add(new InputDocument(pathBase + "msg00058.html", null));
		
		RoundTripComparison rtc = new RoundTripComparison();
		ResourceSimplifierStep rss = new ResourceSimplifierStep();
		
		assertTrue(rtc.executeCompare(new HtmlFilter(), list, "UTF-8", ENUS, ENUS, "skeleton", rss));
	}
	
	@Test
	public void testEvents() throws MalformedURLException {
		String pathBase = Util.getDirectoryName(this.getClass().getResource("aa324.html").getPath()) + "/";
		EventListBuilderStep elbs1 = new EventListBuilderStep();
		EventListBuilderStep elbs2 = new EventListBuilderStep();
		
		new XPipeline(
				"Test pipeline for ResourceSimplifierStepTest",
				new XBatch(
						new XBatchItem(
								new URL("file", null, pathBase + "aa324.html"),
								"UTF-8",
								ENUS)
//						,
//						new XBatchItem(
//								new URL("file", null, pathBase + "form.html"),
//								"UTF-8",
//								ENUS)
//						,
//						new XBatchItem(
//								new URL("file", null, pathBase + "W3CHTMHLTest1.html"),
//								"UTF-8",
//								ENUS)
						),
						
				new RawDocumentToFilterEventsStep(new HtmlFilter()),
				elbs1,
				new ResourceSimplifierStep(),
				//new EventLogger(),
				new DocumentPartLogger(),
				elbs2
		).execute();
		
//		for (Event event : elbs2.getList()) {
//			if (event.isTextUnit()) {
//				System.out.println(TextUnitLogger.getTuInfo(event.getTextUnit(), ENUS));
//			}
//			else if (event.isDocumentPart()) {
//				System.out.println(DocumentPartLogger.getDpInfo(event.getDocumentPart(), ENUS));
//			}
//		}
		
//		System.out.println("==== Before");
//		for (Event event : elbs1.getList()) {
//			if (event.isTextUnit() && "tu41".equals(event.getResource().getId())) {
//				System.out.println(TextUnitLogger.getTuInfo(event.getTextUnit(), ENUS));
//			}
//		}
//		
//		for (Event event : elbs1.getList()) {
//			if (event.isTextUnit() && "tu40".equals(event.getResource().getId())) {
//				System.out.println(TextUnitLogger.getTuInfo(event.getTextUnit(), ENUS));
//			}
//		}
//		
//		System.out.println("==== After");
//		for (Event event : elbs2.getList()) {
//			if (event.isTextUnit() && "tu41".equals(event.getResource().getId())) {
//				System.out.println(TextUnitLogger.getTuInfo(event.getTextUnit(), ENUS));
//			}
//		}
//		
//		for (Event event : elbs2.getList()) {
//			if (event.isTextUnit() && "tu40".equals(event.getResource().getId())) {
//				System.out.println(TextUnitLogger.getTuInfo(event.getTextUnit(), ENUS));
//			}
//		}
	}
	
	@Test
	public void testEvents2() throws MalformedURLException {
		String pathBase = Util.getDirectoryName(this.getClass().getResource("aa324.html").getPath()) + "/";
		EventListBuilderStep elbs1 = new EventListBuilderStep();
		EventListBuilderStep elbs2 = new EventListBuilderStep();
		
		new XPipeline(
				"Test pipeline for ResourceSimplifierStepTest",
				new XBatch(
						new XBatchItem(
								new URL("file", null, pathBase + "msg00058.html"),
								"UTF-8",
								ENUS)
						),
						
				new RawDocumentToFilterEventsStep(new HtmlFilter()),
//				elbs1,
				//new ResourceSimplifierStep(),
				new EventLogger(),
				elbs2
		).execute();
		
		for (Event event : elbs2.getList()) {
			if (event.isTextUnit()) {
				logger.debug(TextUnitLogger.getTuInfo(event.getTextUnit(), ENUS));
			}
			else if (event.isDocumentPart()) {
				logger.debug(DocumentPartLogger.getDpInfo(event.getDocumentPart(), ENUS));
			}
		}		
	}

	@Test
	public void testEvents3() throws MalformedURLException {
		String pathBase = Util.getDirectoryName(this.getClass().getResource("aa324.html").getPath()) + "/";
		EventListBuilderStep elbs1 = new EventListBuilderStep();
		EventListBuilderStep elbs2 = new EventListBuilderStep();
		
		new XPipeline(
				"Test pipeline for ResourceSimplifierStepTest",
				new XBatch(
						new XBatchItem(
								new URL("file", null, pathBase + "form.html"),
								"UTF-8",
								ENUS)

						),
						
				new RawDocumentToFilterEventsStep(new HtmlFilter()),
				elbs1,
				new ResourceSimplifierStep(),
				//new EventLogger(),
				new DocumentPartLogger(),
				elbs2
		).execute();
	}
	
	@Test
	public void testTuDpSsfEvents() throws URISyntaxException {
		new XPipeline(
				null,
				new XBatch(
						new XBatchItem(
								this.getClass().getResource("form.html").toURI(),
								"UTF-8",
								ENUS)
						),
						
				new RawDocumentToFilterEventsStep(new HtmlFilter()),
				new TuDpSsfLogger()
		).execute();
	}
	
	@Test
	public void testTuDpSsfEvents_simplified() throws URISyntaxException {
		new XPipeline(
				null,
				new XBatch(
						new XBatchItem(
								this.getClass().getResource("form.html").toURI(),
								"UTF-8",
								ENUS)
						),
						
				new RawDocumentToFilterEventsStep(new HtmlFilter()),
				new ResourceSimplifierStep(),
				new TuDpSsfLogger()
		).execute();
	}
	
	@Test
	public void testTuDpSsfEvents2() throws URISyntaxException {
		new XPipeline(
				null,
				new XBatch(
						new XBatchItem(
								this.getClass().getResource("aa324.html").toURI(),
								"UTF-8",
								ENUS)
						),
						
				new RawDocumentToFilterEventsStep(new HtmlFilter()),
				new TuDpSsfLogger()
		).execute();
	}
	
	@Test
	public void testTuDpSsfEvents2_simplified() throws URISyntaxException {
		new XPipeline(
				null,
				new XBatch(
						new XBatchItem(
								this.getClass().getResource("aa324.html").toURI(),
								"UTF-8",
								ENUS)
						),
						
				new RawDocumentToFilterEventsStep(new HtmlFilter()),
				new ResourceSimplifierStep(),
				new TuDpSsfLogger()
		).execute();
	}
	
	@Test
	public void testTuDpSsfEvents3() throws URISyntaxException {
		new XPipeline(
				null,
				new XBatch(
						new XBatchItem(
								this.getClass().getResource("aa324_out.html").toURI(),
								"UTF-8",
								ENUS)
						),
						
				new RawDocumentToFilterEventsStep(new HtmlFilter()),
				new TuDpSsfLogger()
		).execute();
	}
	
	@Test
	public void testTuDpSsfEvents3_simplified() throws URISyntaxException {
		new XPipeline(
				null,
				new XBatch(
						new XBatchItem(
								this.getClass().getResource("aa324_out.html").toURI(),
								"UTF-8",
								ENUS)
						),
						
				new RawDocumentToFilterEventsStep(new HtmlFilter()),
				new ResourceSimplifierStep(),
				new TuDpSsfLogger()
		).execute();
	}
}
