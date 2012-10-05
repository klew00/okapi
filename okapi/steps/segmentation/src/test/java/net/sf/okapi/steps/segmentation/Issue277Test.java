package net.sf.okapi.steps.segmentation;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.filters.tmx.TmxFilter;
import net.sf.okapi.steps.segmentation.Parameters.SegmStrategy;

import org.junit.Before;
import org.junit.Test;

/**
 * <h2>Incorrect segmentation of TMX file in 2-3 alignments</h2>
 * <p>
 * I have an srx that segments source text and translated texts correctly, but
 * if I feed Rainbow TMX files and ask for segmentation of both flagging the
 * Verify that a target segment matches each source segment when a target
 * content exists´ option, something goes wrong: the target segment is empty if
 * the source segment would be split in 2 and the target in 3 for instance. I
 * would expect that the source and target in such a case would not be touched
 * as the split condition is not present.
 * 
 * @author jimh
 */
public class Issue277Test {

	private SegmentationStep segStep;
	private Parameters params;
	private TmxFilter tmxFilter;
	private List<Event> events; 
	private GenericContent fmt;
	
	
	/**
	 * <h2>Segmentation step params:</h2>
	 * 
	 * <pre>
	 * 	  segmentSource.b=true
	 * 	  segmentTarget.b=true
	 * 	  sourceSrxPath=issue277.srx
	 * 	  targetSrxPath=issue277.srx
	 * 	  copySource.b=false
	 * 	  checkSegments.b=true
	 * 	  trimSrcLeadingWS.i=-1
	 * 	  trimSrcTrailingWS.i=-1
	 * 	  trimTrgLeadingWS.i=-1
	 * 	  trimTrgTrailingWS.i=-1
	 * 	  forceSegmentedOutput.b=false
	 * 	  overwriteSegmentation.b=true
	 * 	  deepenSegmentation.b=false
	 * </pre>
	 * 
	 * @throws URISyntaxException
	 */
	@Before
	public void startUp() throws URISyntaxException {
		fmt = new GenericContent(); 
		
		segStep = new SegmentationStep();
		segStep.setSourceLocale(LocaleId.ENGLISH);
		segStep.setTargetLocales(Arrays.asList(LocaleId.ENGLISH, LocaleId.GERMAN));
		params = (Parameters) segStep.getParameters();
		String srxFile = this.getClass().getResource("/issue277.srx").toURI().getPath();
		params.setSourceSrxPath(srxFile);
		params.setTargetSrxPath(srxFile);
		params.segmentSource = true;
		params.segmentTarget = true;
		params.copySource = false;
		params.checkSegments = true;
		params.trimSrcLeadingWS = -1;
		params.trimSrcTrailingWS = -1;
		params.trimTrgLeadingWS = -1;
		params.trimTrgTrailingWS = -1;
		params.setForcesegmentedOutput(false);
		params.setSegmentationStrategy(SegmStrategy.OVERWRITE_EXISTING);
		segStep.setParameters(params);
		segStep.handleStartBatch(new Event(EventType.START_BATCH));
		segStep.handleStartBatchItem(new Event(EventType.START_BATCH_ITEM));
		segStep.handleStartDocument(new Event(EventType.START_DOCUMENT));
		
		tmxFilter = new TmxFilter();
		tmxFilter.open(new RawDocument(this.getClass().getResource("/issue277.tmx").toURI(), 
				"UTF-8", LocaleId.ENGLISH, LocaleId.GERMAN));
		events = new LinkedList<Event>();
		while (tmxFilter.hasNext()) {
			events.add(tmxFilter.next());
		}
		tmxFilter.close();
		
	}

	/**
	 * Test 2-3 alignments with custom rules and small TMX sample
	 */
	@Test
	public void segmentationTest() {
		ITextUnit tu1 = FilterTestDriver.getTextUnit(events, 1);
		segStep.handleTextUnit(new Event(EventType.TEXT_UNIT, tu1));
		TextContainer source = tu1.getSource();
		TextContainer target = tu1.getTarget(LocaleId.GERMAN);
		assertEquals("[This sentence should be split.][ In two pieces.]", fmt.printSegmentedContent(source, true));
		assertEquals("[Dieser Satz sollte aufgeteilt werden.][ In zwei Teile.]", fmt.printSegmentedContent(target, true));
		
		ITextUnit tu2 = FilterTestDriver.getTextUnit(events, 2);
		segStep.handleTextUnit(new Event(EventType.TEXT_UNIT, tu2));
		source = tu2.getSource();
		target = tu2.getTarget(LocaleId.GERMAN);
		// TODO: the segmenter produces an second segment with whitespace only
		// we really want this to be automatically merged into the previous segment
		// adjust this test case when/if that behavior is added
		assertEquals("[This sentence should not be split.][ ]", fmt.printSegmentedContent(source, true));
		assertEquals("[Dieser Satz sollte nicht geteilt werden.][ Zu viele Segmente hier.]", fmt.printSegmentedContent(target, true));		
	}
}
