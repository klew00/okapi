package net.sf.okapi.steps.common.stepannotations;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import net.sf.okapi.common.pipeline.IPipelineStep;

public final class StepAnnotationIntrospector {

	public static List<StepParameter> getStepParameters(IPipelineStep step) {
		List<StepParameter> parameters = new LinkedList<StepParameter>();
		Field[] fields = step.getClass().getDeclaredFields();
		for (Field f : fields) {
			if (f.isAnnotationPresent(ExternalParameter.class) || f.isAnnotationPresent(StepConfigurationParameter.class)) {
				// each step parameters can only have one of these annotations
				try {
					StepParameter p = new StepParameter(f.getName(), f.get(step));
					if (f.isAnnotationPresent(ExternalParameter.class)) {
						ExternalParameter ep = f.getAnnotation(ExternalParameter.class);
						p.setDescription(ep.description());
						p.setLongDescription(ep.longDescription());
						p.setRequiredStep(ep.requiredStep());
						p.setType(ep.type());
						p.setStepConfigurationParameter(false);
					} else if (f.isAnnotationPresent(StepConfigurationParameter.class)) {
						StepConfigurationParameter scp = f.getAnnotation(StepConfigurationParameter.class);
						p.setDescription(scp.description());
						p.setLongDescription(scp.longDescription());
						p.setStepConfigurationParameter(true);
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
				}
			}
		}
		return parameters;
	}
}
