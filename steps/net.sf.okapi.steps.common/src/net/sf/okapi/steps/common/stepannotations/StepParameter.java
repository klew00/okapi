package net.sf.okapi.steps.common.stepannotations;

import java.lang.reflect.Type;

public class StepParameter {	 
	private String paramName;
	private Object paramValue;
	private String description;
	private String longDescription;
	private ExternalParameterType type;
	private String requiredStep;
	private boolean stepConfigurationParameter;

	public StepParameter(String paramName, Object object) {
		this.paramName = paramName;
		this.paramValue = object;
	}

	/**
	 * @return the paramName
	 */
	public String getParamName() {
		return paramName;
	}

	/**
	 * @return the paramValue
	 */
	public Object getParamValue() {
		return paramValue;
	}

	public void setParamValue(Class<?> paramValue) {
		this.paramValue = paramValue;
	}

	public Type getJavaType() {
		if (paramValue != null) {
			return paramValue.getClass();
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
	public void setRequiredStep(String requiredStep) {
		this.requiredStep = requiredStep;
	}

	/**
	 * @return the requiredStep
	 */
	public String getRequiredStep() {
		return requiredStep;
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
