package net.sf.okapi.steps.common.skeletonconversion;

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
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.filters.html.HtmlFilter;
import net.sf.okapi.lib.extra.pipelinebuilder.XBatch;
import net.sf.okapi.lib.extra.pipelinebuilder.XBatchItem;
import net.sf.okapi.lib.extra.pipelinebuilder.XPipeline;
import net.sf.okapi.lib.extra.steps.DocumentPartLogger;
import net.sf.okapi.lib.extra.steps.EventListBuilderStep;
import net.sf.okapi.lib.extra.steps.EventLogger;
import net.sf.okapi.lib.extra.steps.TextUnitLogger;
import net.sf.okapi.lib.extra.steps.TuDpLogger;
import net.sf.okapi.steps.common.RawDocumentToFilterEventsStep;
import net.sf.okapi.steps.common.ResourceSimplifierStep;
import net.sf.okapi.steps.common.tufiltering.ITextUnitFilter;

import org.junit.Test;

public class SkeletonConversionStepTest {

private static final LocaleId ENUS = new LocaleId("en", "us");
	
	@Test
	public void testDoubleExtraction () throws URISyntaxException, IOException {
		ArrayList<InputDocument> list = new ArrayList<InputDocument>();				
		String pathBase = Util.getDirectoryName(this.getClass().getResource("aa324.html").getPath()) + "/";
		
		list.add(new InputDocument(pathBase + "aa324.html", null));
		list.add(new InputDocument(pathBase + "form.html", null));
		list.add(new InputDocument(pathBase + "W3CHTMHLTest1.html", null));
		list.add(new InputDocument(pathBase + "msg00058.html", null));
		
		RoundTripComparison rtc = new RoundTripComparison();
		SkeletonConversionStep sks = new SkeletonConversionStep(new ITextUnitFilter() {
			
			@Override
			public boolean accept(ITextUnit tu) {
				return true;
			}
		});
		
		assertTrue(rtc.executeCompare(new HtmlFilter(), list, "UTF-8", ENUS, ENUS, "skeleton", sks));
		//assertTrue(rtc.executeCompare(new HtmlFilter(), list, "UTF-8", ENUS, ENUS, "skeleton"));
		//assertTrue(rtc.executeCompare(new HtmlFilter(), list, "UTF-8", ENUS, ENUS, "skeleton", new ResourceSimplifierStep()));
	}
	
	@Test
	public void testEvents() throws MalformedURLException {
		String pathBase = Util.getDirectoryName(this.getClass().getResource("aa324.html").getPath()) + "/";
		EventListBuilderStep elbs1 = new EventListBuilderStep();
		EventListBuilderStep elbs2 = new EventListBuilderStep();
		
		new XPipeline(
				"Test pipeline for SkeletonConversionStepTest",
				new XBatch(
//						new XBatchItem(
//								new URL("file", null, pathBase + "aa324.html"),
//								"UTF-8",
//								ENUS)
//						,
						new XBatchItem(
								new URL("file", null, pathBase + "form.html"),
								"UTF-8",
								ENUS)
//						,
//						new XBatchItem(
//								new URL("file", null, pathBase + "W3CHTMHLTest1.html"),
//								"UTF-8",
//								ENUS)
						),
						
				new RawDocumentToFilterEventsStep(new HtmlFilter()),
				elbs1,
//				new EventLogger(),
//				new TuDpLogger()
//				,
				new SkeletonConversionStep(new ITextUnitFilter() {
					
					@Override
					public boolean accept(ITextUnit tu) {
						return true;
					}
				}),
//				new EventLogger(),
				new TuDpLogger(),
				elbs2
		).execute();
		
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
//		for (Event event : elbs1.getList()) {
//			if (event.isDocumentPart() && "dp_tu40".equals(event.getResource().getId())) {
//				System.out.println(DocumentPartLogger.getDpInfo(event.getDocumentPart(), ENUS));
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
//		
//		for (Event event : elbs2.getList()) {
//			if (event.isDocumentPart() && "dp_tu40".equals(event.getResource().getId())) {
//				System.out.println(DocumentPartLogger.getDpInfo(event.getDocumentPart(), ENUS));
//			}
//		}
		
		
	}
	
	@Test
	public void testEvents2() throws MalformedURLException {
		String pathBase = Util.getDirectoryName(this.getClass().getResource("aa324.html").getPath()) + "/";
		EventListBuilderStep elbs1 = new EventListBuilderStep();
		EventListBuilderStep elbs2 = new EventListBuilderStep();
		
		new XPipeline(
				"Test pipeline for SkeletonConversionStepTest",
				new XBatch(
						new XBatchItem(
								new URL("file", null, pathBase + "msg00058.html"),
								"UTF-8",
								ENUS)
						),
						
				new RawDocumentToFilterEventsStep(new HtmlFilter()),
				elbs1,
				//new EventLogger(),
				//new TuDpLogger(),
				new SkeletonConversionStep(new ITextUnitFilter() {
					
					@Override
					public boolean accept(ITextUnit tu) {
						return true;
					}
				}),
				new EventLogger(),
				new TuDpLogger(),
				elbs2
		).execute();
	}

}
