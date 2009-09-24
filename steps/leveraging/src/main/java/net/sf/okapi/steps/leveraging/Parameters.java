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

package net.sf.okapi.steps.leveraging;

import net.sf.okapi.common.BaseParameters;

public class Parameters extends BaseParameters {
	
	private String resourceClassName;
	private String resourceParameters;
	private int threshold;
	
	public Parameters () {
		reset();
	}
	
	public String getResourceClassName () {
		return resourceClassName;
	}

	public void setResourceClassName (String resourceClassName) {
		this.resourceClassName = resourceClassName;
	}

	public String getResourceParameters () {
		return resourceParameters;
	}

	public void setResourceParameters (String resourceParameters) {
		this.resourceParameters = resourceParameters;
	}

	public int getThreshold () {
		return threshold;
	}

	public void setThreshold (int threshold) {
		this.threshold = threshold;
	}

	public void reset() {
		resourceClassName = "net.sf.okapi.connectors.pensieve.PensieveTMConnector";
		resourceParameters = null;
		threshold = 95;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		resourceClassName = buffer.getString("resourceClassName", resourceClassName);
		resourceParameters = buffer.getGroup("resourceParameters", resourceParameters);
		threshold = buffer.getInteger("threshold", threshold);
	}

	@Override
	public String toString() {
		buffer.reset();
		buffer.setString("resourceClassName", resourceClassName);
		buffer.setGroup("resourceParameters", resourceParameters);
		buffer.setInteger("threshold", threshold);
		return buffer.toString();
	}

}
