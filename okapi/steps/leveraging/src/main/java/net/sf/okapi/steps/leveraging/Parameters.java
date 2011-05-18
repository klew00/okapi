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

	private static final String LEVERAGE = "leverage";
	private static final String FILLTARGET = "fillTarget";
	private static final String FILLTARGETTHRESHOLD = "fillTargetThreshold";
	private static final String FILLIFTARGETISEMPTY = "fillIfTargetIsEmpty";
	private static final String FILLIFTARGETISSAMEASSOURCE = "fillIfTargetIsSameAsSource";
	private static final String DOWNGRADEIDENTICALBESTMATCHES = "downgradeIdenticalBestMatches";
	private static final String MAKETMX = "makeTMX";
	private static final String TMXPATH = "tmxPath";
	private static final String USEMTPREFIX = "useMTPrefix";
	private static final String USETARGETPREFIX = "useTargetPrefix";
	private static final String TARGETPREFIX = "targetPrefix";
	private static final String TARGETPREFIXTHRESHOLD = "targetPrefixThreshold";
	private static final String COPYSOURCEONNOTEXT = "copySourceOnNoText";
	
	private String resourceClassName;
	private String resourceParameters;
	private int threshold;
	private boolean fillTarget;
	private int fillTargetThreshold;
	private boolean fillIfTargetIsEmpty;
	private boolean fillIfTargetIsSameAsSource;
	private boolean downgradeIdenticalBestMatches;
	private boolean makeTMX;
	private String tmxPath;
	private boolean useMTPrefix;
	private boolean leverage;
	private boolean useTargetPrefix;
	private String targetPrefix;
	private int targetPrefixThreshold;
	private boolean copySourceOnNoText;

	public Parameters () {
		reset();
	}
	
	public boolean getFillIfTargetIsEmpty () {
		return fillIfTargetIsEmpty;
	}
	
	public void setFillIfTargetIsEmpty (boolean fillIfTargetIsEmpty) {
		this.fillIfTargetIsEmpty = fillIfTargetIsEmpty;
	}
	
	public boolean getFillIfTargetIsSameAsSource () {
		return fillIfTargetIsSameAsSource;
	}
	
	public void setFillIfTargetIsSameAsSource (boolean fillIfTargetIsSameAsSource) {
		this.fillIfTargetIsSameAsSource = fillIfTargetIsSameAsSource;
	}

	public boolean getLeverage () {
		return leverage;
	}
	
	public void setLeverage (boolean leverage) {
		this.leverage = leverage;
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

	public int getFillTargetThreshold () {
		return fillTargetThreshold;
	}

	public void setFillTargetThreshold (int fillTargetThreshold) {
		this.fillTargetThreshold = fillTargetThreshold;
	}

	public boolean getDowngradeIdenticalBestMatches () {
		return downgradeIdenticalBestMatches;
	}

	public void setDowngradeIdenticalBestMatches (boolean downgradeIdenticalBestMatches) {
		this.downgradeIdenticalBestMatches = downgradeIdenticalBestMatches;
	}

	public boolean getMakeTMX () {
		return makeTMX;
	}

	public void setMakeTMX (boolean makeTMX) {
		this.makeTMX = makeTMX;
	}

	public boolean getCopySourceOnNoText () {
		return copySourceOnNoText;
	}

	public void setCopySourceOnNoText (boolean copySourceOnNoText) {
		this.copySourceOnNoText = copySourceOnNoText;
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

	public boolean getUseTargetPrefix () {
		return useTargetPrefix;
	}
	
	public void setUseTargetPrefix (boolean useTargetPrefix) {
		this.useTargetPrefix = useTargetPrefix;
	}

	public String getTargetPrefix () {
		return targetPrefix;
	}
	
	public void setTargetPrefix (String targetPrefix) {
		this.targetPrefix = targetPrefix;
	}

	public int getTargetPrefixThreshold () {
		return targetPrefixThreshold;
	}

	public void setTargetPrefixThreshold (int targetPrefixThreshold) {
		this.targetPrefixThreshold = targetPrefixThreshold;
	}

	@Override
	public void reset() {
		leverage = true;
		resourceClassName = "net.sf.okapi.connectors.pensieve.PensieveTMConnector";
		resourceParameters = null;
		threshold = 95;
		fillTarget = true;
		fillTargetThreshold = 95;
		fillIfTargetIsEmpty = false;
		fillIfTargetIsSameAsSource = false;
		downgradeIdenticalBestMatches = false;
		makeTMX = false;
		tmxPath = "";
		useMTPrefix = true;
		useTargetPrefix = false;
		targetPrefix = "FUZZY__";
		targetPrefixThreshold = 99;
		copySourceOnNoText = false;
	}

	@Override
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		resourceClassName = buffer.getString("resourceClassName", resourceClassName);
		resourceParameters = buffer.getGroup("resourceParameters", resourceParameters);
		threshold = buffer.getInteger("threshold", threshold);
		fillTarget = buffer.getBoolean(FILLTARGET, fillTarget);
		fillTargetThreshold = buffer.getInteger(FILLTARGETTHRESHOLD, fillTargetThreshold);
		fillIfTargetIsEmpty = buffer.getBoolean(FILLIFTARGETISEMPTY, fillIfTargetIsEmpty);
		fillIfTargetIsSameAsSource = buffer.getBoolean(FILLIFTARGETISSAMEASSOURCE, fillIfTargetIsSameAsSource);
		downgradeIdenticalBestMatches = buffer.getBoolean(DOWNGRADEIDENTICALBESTMATCHES, downgradeIdenticalBestMatches);
		makeTMX = buffer.getBoolean(MAKETMX, makeTMX);
		tmxPath = buffer.getString(TMXPATH, tmxPath);
		useMTPrefix = buffer.getBoolean(USEMTPREFIX, useMTPrefix);
		leverage = buffer.getBoolean(LEVERAGE, leverage);
		useTargetPrefix = buffer.getBoolean(USETARGETPREFIX, useTargetPrefix);
		targetPrefix = buffer.getString(TARGETPREFIX, targetPrefix);
		targetPrefixThreshold = buffer.getInteger(TARGETPREFIXTHRESHOLD, targetPrefixThreshold);
		copySourceOnNoText = buffer.getBoolean(COPYSOURCEONNOTEXT, copySourceOnNoText);
	}

	@Override
	public String toString() {
		buffer.reset();
		buffer.setString("resourceClassName", resourceClassName);
		buffer.setGroup("resourceParameters", resourceParameters);
		buffer.setInteger("threshold", threshold);
		buffer.setBoolean(FILLTARGET, fillTarget);
		buffer.setInteger(FILLTARGETTHRESHOLD, fillTargetThreshold);
		buffer.setBoolean(FILLIFTARGETISEMPTY, fillIfTargetIsEmpty);
		buffer.setBoolean(FILLIFTARGETISSAMEASSOURCE, fillIfTargetIsSameAsSource);
		buffer.setBoolean(DOWNGRADEIDENTICALBESTMATCHES, downgradeIdenticalBestMatches);
		buffer.setBoolean(MAKETMX, makeTMX);
		buffer.setString(TMXPATH, tmxPath);
		buffer.setBoolean(USEMTPREFIX, useMTPrefix);
		buffer.setBoolean(LEVERAGE, leverage);
		buffer.setBoolean(USETARGETPREFIX, useTargetPrefix);
		buffer.setString(TARGETPREFIX, targetPrefix);
		buffer.setInteger(TARGETPREFIXTHRESHOLD, targetPrefixThreshold);
		buffer.setBoolean(COPYSOURCEONNOTEXT, copySourceOnNoText);
		return buffer.toString();
	}

}
