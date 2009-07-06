package net.sf.okapi.steps.common.javaannotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface StepConfigurationParameter {
	/**
	 * Short UI displayable description of the parameter.	 
	 */
	String description();

	/**
	 * Long UI displayable description of the parameter.	 
	 */
	String longDescription();
}
