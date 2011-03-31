package net.sf.okapi.lib.extra.steps;

import java.util.logging.Logger;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;

public class TextUnitLogger extends BasePipelineStep {

	private final Logger logger = Logger.getLogger(getClass().getName());
	private StringBuilder sb;
	private LocaleId srcLoc;
	
	@Override
	public String getName() {
		return "Text Unit Logger";
	}

	@Override
	public String getDescription() {
		return "Logs Text Unit resources going through the pipeline.";
	}
	
	@Override
	protected Event handleStartBatch(Event event) {
		sb = new StringBuilder("\n\n");
		return super.handleStartBatch(event);
	}

	@Override
	protected Event handleStartDocument(Event event) {
		StartDocument sd = (StartDocument) event.getResource();
		srcLoc = sd.getLocale();
		return super.handleStartDocument(event);
	}
	
	@Override
	protected Event handleTextUnit(Event event) {
		TextUnit tu = (TextUnit) event.getResource();
		fillSB(sb, tu, srcLoc);
		
		return super.handleTextUnit(event);
	}
	
	@Override
	protected Event handleEndBatch(Event event) {
		logger.fine(sb.toString());
		return super.handleEndBatch(event);
	}
	
	private static void fillSB(StringBuilder sb, TextUnit tu, LocaleId srcLoc) {
		sb.append(tu.getId());
		sb.append(":");
		sb.append("\n");
		
		if (tu.getAnnotations() != null) {
//			sb.append("             ");
//			sb.append("Source annotations:");
//			sb.append("\n");
			for (IAnnotation annot : tu.getAnnotations()) {
				sb.append("                    ");
				sb.append(annot.getClass().getName());
				sb.append(" ");
				sb.append(annot.toString());
				sb.append("\n");
			}		
		}
		
		sb.append(String.format("      Source (%s): %s", srcLoc, tu.getSource()));
		sb.append("\n");
		
		TextContainer source = tu.getSource();
		if (source.getAnnotations() != null) {
//			sb.append("             ");
//			sb.append("Source annotations:");
//			sb.append("\n");
			for (IAnnotation annot : source.getAnnotations()) {
				sb.append("                    ");
				sb.append(annot.getClass().getName());
				sb.append(" ");
				sb.append(annot.toString());
				sb.append("\n");
			}		
		}
		
		ISegments segs = source.getSegments(); 
		for (Segment seg : segs) {
			sb.append(String.format("         %s: %s", seg.getId(), seg.getContent()));
			sb.append("\n");
			if (seg.getAnnotations() != null) {
//				sb.append("Source annotations:");
//				sb.append("\n");
				for (IAnnotation annot : seg.getAnnotations()) {
					sb.append("                    ");
					sb.append(annot.getClass().getName());
					sb.append(" ");
					sb.append(annot.toString());
					sb.append("\n");
				}		
			}
		}
		
		for (LocaleId locId : tu.getTargetLocales()) {
			sb.append(String.format("      Target (%s): %s", locId.toString(), tu.getTarget(locId)));
			sb.append("\n");
			
			TextContainer target = tu.getTarget(locId);
			if (source.getAnnotations() != null) {
//				sb.append("             ");
//				sb.append("Target annotations:");
//				sb.append("\n");
				for (IAnnotation annot : target.getAnnotations()) {
					sb.append("                    ");
					sb.append(annot.getClass().getName());
					sb.append(" ");
					sb.append(annot.toString());
					sb.append("\n");
				}		
			}
			
			segs = target.getSegments(); 
			for (Segment seg : segs) {
				sb.append(String.format("         %s: %s", seg.getId(), seg.getContent()));
				sb.append("\n");
				if (seg.getAnnotations() != null) {
//					sb.append("Target annotations:");
//					sb.append("\n");
					for (IAnnotation annot : seg.getAnnotations()) {
						sb.append("                    ");
						sb.append(annot.getClass().getName());
						sb.append(" ");
						sb.append(annot.toString());
						sb.append("\n");
					}		
				}
			}
		}
	}
	
	public static String getTuInfo(TextUnit tu, LocaleId srcLoc) {
		StringBuilder sb = new StringBuilder();
		fillSB(sb, tu, srcLoc);
		return sb.toString();
	}
}
