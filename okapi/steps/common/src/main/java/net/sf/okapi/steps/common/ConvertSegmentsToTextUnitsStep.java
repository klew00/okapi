package net.sf.okapi.steps.common;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.resource.IAlignedSegments;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.MultiEvent;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextUnitUtil;

/**
 * Convert single segmented {@link ITextUnit}s to multiple TextUnits, one per aligned sentence pair, for each target locale.
 * If the TextUnit refers to another {@link IResource} or is a referent then pass it on as-is even if it has segments. It's
 * possible these could be safely processed, but it would take considerable effort to implement.
 * 
 * @author hargrave
 */
public class ConvertSegmentsToTextUnitsStep extends BasePipelineStep {

	@Override
	public String getName() {
		return "Segments to TextUnits Converter";
	}

	@Override
	public String getDescription() {
		return "Convert each aligned segment pair (for all target locales) to its own complete TextUnit";
	}

	@Override
	protected Event handleTextUnit(Event event) {
		ITextUnit tu = event.getTextUnit();
		
		// if the TextUnit refers to another resource or 
		// is a referent pass it on as-is
		if (tu == null || tu.isEmpty() || !TextUnitUtil.isStandalone(tu)) {
			return event;
		}		
		
		List<Event> textUnitEvents = new LinkedList<Event>();

		IAlignedSegments alignedSegments = tu.getAlignedSegments();

		for (LocaleId variantTrgLoc : tu.getTargetLocales()) {
			// get iterator on source variant segments
			Iterator<Segment> variantSegments = alignedSegments.iterator(variantTrgLoc);

			// For each segment: create a separate TU
			while (variantSegments.hasNext()) {
				int segCount = 0;
				Segment srcSeg = variantSegments.next();

				// for each target segment
				for (LocaleId l : tu.getTargetLocales()) {
					Segment trgSeg = alignedSegments.getCorrespondingTarget(srcSeg, l);
					if (trgSeg != null) {
						ITextUnit segmentTu = tu.clone();
						segmentTu.setId(segmentTu.getId() + ":" + Integer.toString(++segCount));
						segmentTu.getSource().clear();
						segmentTu.getTarget(l).clear();
						segmentTu.setSourceContent(srcSeg.text);
						segmentTu.setTargetContent(l, trgSeg.text);
						textUnitEvents.add(new Event(EventType.TEXT_UNIT, segmentTu));
					}
				}
			}
		}
		
		return new Event(EventType.MULTI_EVENT, new MultiEvent(textUnitEvents));
	}
}
