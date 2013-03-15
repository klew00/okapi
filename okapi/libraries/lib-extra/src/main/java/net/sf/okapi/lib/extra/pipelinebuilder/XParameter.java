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

package net.sf.okapi.lib.extra.pipelinebuilder;

import net.sf.okapi.common.pipeline.annotations.StepParameterType;

public class XParameter {
	private StepParameterType type = null;
	private String name;
	private Object value;
	private boolean asGroup = false;

	// Type safety constructors
	public XParameter(String name, String value) {
		setParameter(name, value);
	}
	
	public XParameter(String name, String value, boolean asGroup) {
		setParameter(name, value);
		this.asGroup = asGroup;
	}

	public XParameter(String name, int value) {
		setParameter(name, value);
	}
	
	public XParameter(String name, boolean value) {
		setParameter(name, value);
	}
	
//	public XParameter(StepParameterType type, RawDocument value) {
//		this(type);
//	}
//	
//	public XParameter(StepParameterType type, URI value) {
//		this(type);
//	}
//	
//	public XParameter(StepParameterType type, LocaleId value) {
//		this(type);
//	}
//	
//	public XParameter(StepParameterType type, String value) {
//		this(type);
//	}
//	
//	public XParameter(StepParameterType type, IFilterConfigurationMapper value) {
//		this(type);
//	}
	
	public XParameter(StepParameterType type, Object value) {
		super();
		this.type = type;
		this.value = value;
	}
	
//	public XParameter(StepParameterType type, int value) {
//		this(type);
//	}

	private void setParameter(String name, Object value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		return super.toString();
	}

	public StepParameterType getType() {
		return type;
	}

	public boolean isAsGroup() {
		return asGroup;
	}	
}
