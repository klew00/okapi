package net.sf.okapi.steps.wordcount.categorized;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.pipeline.IPipelineStep;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.steps.wordcount.common.Metrics;
import net.sf.okapi.steps.wordcount.common.MetricsAnnotation;

/**
 * 
 *
 */
public class CategoryResolver {

	private List<String> gmxCategories = new LinkedList<String>(); // Original sequence is preserved by LinkedList
	private List<String> okapiCategories = new LinkedList<String>();
	private Map<String, Long> removedCategories = new HashMap<String, Long>();
		
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

	public void reset() {
		removedCategories.clear();
	}
	
	public void resolve(MetricsAnnotation ma, boolean contributesInTotal) {
		if (ma == null) return;				
		Metrics metrics = ma.getMetrics();
		
		// Can't do it on metrics directly for concurrency reasons
		List<String> m = new LinkedList<String>(metrics);
		
		for (String metric : m) {
			// Remove all metrics lower ranked
			int index = gmxCategories.indexOf(metric);			
			if (index > -1) {
				for (int i = index + 1; i < gmxCategories.size(); i++) {
					removeCategory(metrics, gmxCategories.get(i), contributesInTotal);					
				}
				continue;
			}
			
			// Remove all metrics lower ranked
			index = okapiCategories.indexOf(metric);			
			if (index > -1) {
				for (int i = index + 1; i < okapiCategories.size(); i++) {
					removeCategory(metrics, okapiCategories.get(i), contributesInTotal);
				}
			}			
		}
	}
	
	private void removeCategory(Metrics metrics, String name, boolean contributesInTotal) {
		long value = 0;
		
		if (contributesInTotal) {
			value = Util.getValue(removedCategories, name, 0L);
			value += metrics.getMetric(name);
			if (value == 0) {
				removedCategories.remove(name); // Not to keep 0 values in the map
			}
			else {
				removedCategories.put(name, value);
			}
		}		
		
		metrics.unregisterMetric(name);
	}

	private void resolveTU(TextUnit tu) {
		MetricsAnnotation ma = tu.getAnnotation(MetricsAnnotation.class);
		resolve(ma, true);
		
		TextContainer tc = tu.getSource();
		ma = tc.getAnnotation(MetricsAnnotation.class);
		resolve(ma, false);
		
		for (Segment seg : tc.getSegments()) {
			ma = seg.getAnnotation(MetricsAnnotation.class);
			resolve(ma, false);
		}
		
		for (LocaleId trgLoc : tu.getTargetLocales()) {
			tc = tu.getTarget(trgLoc);
			ma = tc.getAnnotation(MetricsAnnotation.class);
			resolve(ma, false);
			
			for (Segment seg : tc.getSegments()) {
				ma = seg.getAnnotation(MetricsAnnotation.class);
				resolve(ma, false);
			}
		}
	}
	
	public void resolve(IResource res) {
		if (res instanceof TextUnit) {
			resolveTU((TextUnit) res);
		}
		else if (res instanceof Ending) {
			resolveEnding((Ending) res);
		}
	}

	private void resolveEnding(Ending res) {		
		MetricsAnnotation ma = res.getAnnotation(MetricsAnnotation.class);
		
		if (ma != null) {
			Metrics metrics = ma.getMetrics();
			
			// Can't do it on metrics directly for concurrency reasons
			List<String> m = new LinkedList<String>(metrics);
			
			for (String metric : m) {
				long value = metrics.getMetric(metric);
				value -= Util.getValue(removedCategories, metric, 0L);
				
				if (value <= 0) {
					metrics.unregisterMetric(metric);
				}
				else {
					metrics.setMetric(metric, value);
				}
			}
		}
	}
	
}
