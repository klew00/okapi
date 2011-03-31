package net.sf.okapi.steps.wordcount.categorized;

import java.util.LinkedList;
import java.util.List;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.pipeline.IPipelineStep;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.steps.wordcount.common.Metrics;
import net.sf.okapi.steps.wordcount.common.MetricsAnnotation;

public class CategoryResolver {

	private List<String> gmxCategories = new LinkedList<String>(); // Original sequence is preserved by LinkedList
	private List<String> okapiCategories = new LinkedList<String>();
		
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
		// Can't do it on metrics directly for concurrency reasons
		List<String> m = new LinkedList<String>(metrics);
		
		
		// Reread metrics as new translated metrics might've been added
		m = new LinkedList<String>(metrics);
		
		for (String metric : m) {
			// Remove all metrics lower ranked
			int index = gmxCategories.indexOf(metric);			
			if (index > -1) {
				for (int i = index + 1; i < gmxCategories.size(); i++) {
					metrics.unregisterMetric(gmxCategories.get(i));
				}
				continue;
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
