package net.sf.okapi.steps.common.stepannotations;

import java.lang.reflect.Type;

public class StepParameter {	 
	private String name;
	private Object value;
	private String description;
	private String longDescription;
	private ExternalParameterType type;
	private String[] requiredSteps;
	private boolean stepConfigurationParameter;

	public StepParameter(String paramName, Object object) {
		this.name = paramName;
		this.value = object;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	public void setValue(Class<?> paramValue) {
		this.value = paramValue;
	}

	public Type getJavaType() {
		if (value != null) {
			return value.getClass();
		}
		return null;
	}

	/**
	 * @param longDescription the longDescription to set
	 */
	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	/**
	 * @return the longDescription
	 */
	public String getLongDescription() {
		return longDescription;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(ExternalParameterType type) {
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public ExternalParameterType getType() {
		return type;
	}

	/**
	 * @param requiredStep the requiredStep to set
	 */
	public void setRequiredSteps(String[] requiredSteps) {
		this.requiredSteps = requiredSteps;
	}

	/**
	 * @return the requiredStep
	 */
	public String[] getRequiredSteps() {
		return requiredSteps;
	}

	/**
	 * @param stepConfigurationParameter the stepConfigurationParameter to set
	 */
	public void setStepConfigurationParameter(boolean stepConfigurationParameter) {
		this.stepConfigurationParameter = stepConfigurationParameter;
	}

	/**
	 * @return the stepConfigurationParameter
	 */
	public boolean isStepConfigurationParameter() {
		return stepConfigurationParameter;
	}
}
