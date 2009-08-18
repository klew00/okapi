/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

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
