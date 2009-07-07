package net.sf.okapi.steps.common.stepannotations;

import java.lang.reflect.Field;
import java.util.HashMap;

import net.sf.okapi.common.exceptions.OkapiBadStepInputException;
import net.sf.okapi.common.pipeline.IPipelineStep;

public final class StepAnnotationIntrospector {

	public static HashMap<String, StepParameter> getStepParameters(IPipelineStep step) {
		HashMap<String, StepParameter> parameters = new HashMap<String, StepParameter>();

		// get all the declared fields (skipping any inherited ones) of the
		// object including protected and private
		Field[] fields = step.getClass().getDeclaredFields();

		// iterate over the fields and pull out the ones that have
		// StepConfigurationParameter or ExternalParameter annotations
		for (Field f : fields) {
			// if not set to true we cannot access private or protected fields
			f.setAccessible(true);
			if (f.isAnnotationPresent(ExternalParameter.class)
					|| f.isAnnotationPresent(StepConfigurationParameter.class)) {
				// a step parameter should only have one of these annotations,
				// if it has more than one throw an exception
				if (f.isAnnotationPresent(ExternalParameter.class)
						&& f.isAnnotationPresent(StepConfigurationParameter.class)) {
					throw new OkapiBadStepInputException(
							"A step parameter cannot have both ExternalParameter and StepConfigurationParameter annotations");
				}
				try {
					if (f.get(step) == null) {
						throw new OkapiBadStepInputException("A step parameter must have a default value");
					}

					StepParameter p = new StepParameter(f.getName(), f.get(step));
					if (f.isAnnotationPresent(ExternalParameter.class)) {
						ExternalParameter ep = f.getAnnotation(ExternalParameter.class);
						p.setDescription(ep.description());
						p.setLongDescription(ep.longDescription());
						p.setRequiredSteps(ep.requiredSteps());
						p.setType(ep.type());
						p.setStepConfigurationParameter(false);
					} else if (f.isAnnotationPresent(StepConfigurationParameter.class)) {
						StepConfigurationParameter scp = f.getAnnotation(StepConfigurationParameter.class);
						p.setDescription(scp.description());
						p.setLongDescription(scp.longDescription());
						p.setStepConfigurationParameter(true);
					}
					parameters.put(p.getName(), p);
				} catch (IllegalArgumentException e) {
					throw new OkapiBadStepInputException("Step parameter does not exist: " + f.getName(), e);
				} catch (IllegalAccessException e) {
					throw new OkapiBadStepInputException("Error accessing step parameter: " + f.getName(), e);
				}
			}
		}
		return parameters;
	}
}
