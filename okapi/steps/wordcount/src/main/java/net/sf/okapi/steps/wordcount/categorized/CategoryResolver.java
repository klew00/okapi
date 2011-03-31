package net.sf.okapi.steps.wordcount.categorized;

import java.util.LinkedList;

import net.sf.okapi.common.pipeline.IPipelineStep;

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
}
