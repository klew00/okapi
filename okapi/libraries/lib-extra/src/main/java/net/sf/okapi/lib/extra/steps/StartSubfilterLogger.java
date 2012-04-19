package net.sf.okapi.lib.extra.steps;

import java.util.logging.Logger;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.StartSubfilter;

public class StartSubfilterLogger extends BasePipelineStep {

	private final Logger logger = Logger.getLogger(getClass().getName());
	private StringBuilder sb;
	private LocaleId srcLoc;
	
	@Override
	public String getName() {
		return "Group Logger";
	}

	@Override
	public String getDescription() {
		return "Logs Start/End Group resources going through the pipeline.";
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
	protected Event handleStartSubfilter(Event event) {
		StartSubfilter ssf = event.getStartSubfilter();
		fillSB(sb, ssf, srcLoc);
		return super.handleDocumentPart(event);
	}
	
	@Override
	protected Event handleEndBatch(Event event) {
		logger.info(sb.toString());
		return super.handleEndBatch(event);
	}
	
	private static void fillSB(StringBuilder sb, StartSubfilter ssf, LocaleId srcLoc) {
		sb.append(String.format("ssf [id=%s name=%s]", ssf.getId(), ssf.getName()));		
		sb.append(":");
		if (ssf.isReferent()) sb.append(" referent");
		sb.append("\n");
		
		if (ssf.getAnnotations() != null) {
//			sb.append("             ");
//			sb.append("Source annotations:");
//			sb.append("\n");
			for (IAnnotation annot : ssf.getAnnotations()) {
				sb.append("                    ");
				sb.append(annot.getClass().getName());
				sb.append(" ");
				sb.append(annot.toString());
				sb.append("\n");
			}		
		}
		
		if (ssf.getPropertyNames() != null && ssf.getPropertyNames().size() > 0) {
			sb.append("             ");
			sb.append("Properties:");
			sb.append("\n");
			for (String name : ssf.getPropertyNames()) {
				sb.append("                    ");
				sb.append(name);
				sb.append(" ");
				sb.append(ssf.getProperty(name).toString());
				sb.append("\n");
			}		
		}
		
		if (ssf.getSourcePropertyNames() != null && ssf.getSourcePropertyNames().size() > 0) {
			sb.append("             ");
			sb.append("Source properties:");
			sb.append("\n");
			
			for (String name : ssf.getSourcePropertyNames()) {
				sb.append("                    ");
				sb.append(name);
				sb.append(" ");
				sb.append(ssf.getSourceProperty(name).toString());
				sb.append("\n");
			}		
		}
				
		for (LocaleId locId : ssf.getTargetLocales()) {
			if (ssf.getTargetPropertyNames(locId) != null && ssf.getTargetPropertyNames(locId).size() > 0) {
				sb.append("             ");
				sb.append("Target properties:");
				sb.append("\n");
				
				for (String name : ssf.getTargetPropertyNames(locId)) {
					sb.append("                    ");
					sb.append(name);
					sb.append(" ");
					sb.append(ssf.getTargetProperty(locId, name).toString());
					sb.append("\n");
				}		
			}
		}		
		
		if (ssf.getSkeleton() != null) {
			sb.append(String.format("      Skeleton: %s", ssf.getSkeleton().toString()));
			sb.append("\n");
		}
	}
	
	public static String getSsfInfo(StartSubfilter ssf, LocaleId srcLoc) {
		StringBuilder sb = new StringBuilder();
		fillSB(sb, ssf, srcLoc);
		return sb.toString();
	}
}
