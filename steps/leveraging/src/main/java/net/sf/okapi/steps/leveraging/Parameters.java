/*===========================================================================
  Copyright (C) 2009-2010 by the Okapi Framework contributors
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

	private static final String FILLTARGET = "fillTarget";
	private static final String MAKETMX = "makeTMX";
	private static final String TMXPATH = "tmxPath";
	private static final String USEMTPREFIX = "useMTPrefix";
	
	private String resourceClassName;
	private String resourceParameters;
	private int threshold;
	private boolean fillTarget;
	private boolean makeTMX;
	private String tmxPath;
	private boolean useMTPrefix;

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

	public boolean getFillTarget () {
		return fillTarget;
	}

	public void setFillTarget (boolean fillTarget) {
		this.fillTarget = fillTarget;
	}

	public boolean getMakeTMX () {
		return makeTMX;
	}

	public void setMakeTMX (boolean makeTMX) {
		this.makeTMX = makeTMX;
	}

	public String getTMXPath () {
		return tmxPath;
	}

	public void setTMXPath (String tmxPath) {
		this.tmxPath = tmxPath;
	}

	public boolean getUseMTPrefix () {
		return useMTPrefix;
	}
	
	public void setUseMTPrefix (boolean useMTPrefix) {
		this.useMTPrefix = useMTPrefix;
	}

	@Override
	public void reset() {
		resourceClassName = "net.sf.okapi.connectors.pensieve.PensieveTMConnector";
		resourceParameters = null;
		threshold = 95;
		fillTarget = true;
		makeTMX = false;
		tmxPath = "";
		useMTPrefix = true;
	}

	@Override
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		resourceClassName = buffer.getString("resourceClassName", resourceClassName);
		resourceParameters = buffer.getGroup("resourceParameters", resourceParameters);
		threshold = buffer.getInteger("threshold", threshold);
		fillTarget = buffer.getBoolean(FILLTARGET, fillTarget);
		makeTMX = buffer.getBoolean(MAKETMX, makeTMX);
		tmxPath = buffer.getString(TMXPATH, tmxPath);
		useMTPrefix = buffer.getBoolean(USEMTPREFIX, useMTPrefix);
	}

	@Override
	public String toString() {
		buffer.reset();
		buffer.setString("resourceClassName", resourceClassName);
		buffer.setGroup("resourceParameters", resourceParameters);
		buffer.setInteger("threshold", threshold);
		buffer.setBoolean(FILLTARGET, fillTarget);
		buffer.setBoolean(MAKETMX, makeTMX);
		buffer.setString(TMXPATH, tmxPath);
		buffer.setBoolean(USEMTPREFIX, useMTPrefix);
		return buffer.toString();
	}

}
