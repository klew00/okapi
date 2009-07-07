package net.sf.okapi.steps.common.stepintrospector;

import java.lang.reflect.Type;

public class StepParameter {	 
	private String name;
	private Object value;
	private String description;
	private String longDescription;
	private StepParameterAccessType accessType;
	private StepParameterType type;
	private String requiredStep;	

	public StepParameter(String paramName, Object value) {
		this.name = paramName;
		this.value = value;
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
	 * @param accessType the accessType to set
	 */
	public void setAccessType(StepParameterAccessType accessType) {
		this.accessType = accessType;
	}

	/**
	 * @return the accessType
	 */
	public StepParameterAccessType getAccessType() {
		return accessType;
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
	 * @param type the type to set
	 */
	public void setType(StepParameterType type) {
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public StepParameterType getType() {
		return type;
	}
}
