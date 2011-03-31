package net.sf.okapi.steps.wordcount.categorized;

import java.util.LinkedList;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.pipeline.IPipelineStep;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.steps.wordcount.common.Metrics;
import net.sf.okapi.steps.wordcount.common.MetricsAnnotation;

public class CategoryResolver {

	private LinkedList<String> gmxCategories = new LinkedList<String>();
	private LinkedList<String> okapiCategories = new LinkedList<String>();
		
	public CategoryResolver(LinkedList<IPipelineStep> steps) {
		super();
		for (IPipelineStep step : steps) {
			if (step instanceof CategoryHandler) {
				CategoryHandler handler = (CategoryHandler) step;
				switch (handler.getCategoryGroup()) {
				
				case GMX_WORD_COUNTS:
					gmxCategories.add(handler.getMetric());
					break;
					
				case OKAPI_WORD_COUNTS:
					okapiCategories.add(handler.getMetric());
					break;
				}
			}
		}
	}

	public void resolve(Metrics metrics) {
		for (String metric : metrics) {
			// Remove all metrics lower ranked
			int index = gmxCategories.indexOf(metric);			
			if (index > -1) {
				for (int i = index + 1; i < gmxCategories.size(); i++) {
					metrics.unregisterMetric(gmxCategories.get(i));
				}
				return;
			}
			
			// Remove all metrics lower ranked
			index = okapiCategories.indexOf(metric);			
			if (index > -1) {
				for (int i = index + 1; i < okapiCategories.size(); i++) {
					metrics.unregisterMetric(okapiCategories.get(i));
				}
			}
		}
	}
	
	public void resolve(TextUnit tu) {		
		MetricsAnnotation ma = tu.getAnnotation(MetricsAnnotation.class);
		if (ma != null)	
			resolve(ma.getMetrics());
		
		TextContainer tc = tu.getSource();
		ma = tc.getAnnotation(MetricsAnnotation.class);
		if (ma != null)	
			resolve(ma.getMetrics());
		
		for (Segment seg : tc.getSegments()) {
			ma = seg.getAnnotation(MetricsAnnotation.class);
			if (ma != null)	
				resolve(ma.getMetrics());
		}
		
		for (LocaleId trgLoc : tu.getTargetLocales()) {
			tc = tu.getTarget(trgLoc);
			ma = tc.getAnnotation(MetricsAnnotation.class);
			if (ma != null)	
				resolve(ma.getMetrics());
			
			for (Segment seg : tc.getSegments()) {
				ma = seg.getAnnotation(MetricsAnnotation.class);
				if (ma != null)	
					resolve(ma.getMetrics());
			}
		}
	}
}
