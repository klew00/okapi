/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.qualitycheck;

import net.sf.okapi.common.BaseParameters;

public class Parameters extends BaseParameters {
	
	private static final String OUTPUTPATH = "outputPath";
	private static final String AUTOOPEN = "autoOpen";
	private static final String LEADINGWS = "leadingWS";
	private static final String TRAILINGWS = "trailingWS";
	private static final String EMPTYTARGET = "emptyTarget";
	private static final String TARGETSAMEASSOURCE = "targetSameAsSource";
	private static final String TARGETSAMEASSOURCE_WITHCODES = "targetSameAsSourceWithCodes";
	private static final String CODEDIFFERENCE = "codeDifference";
	private static final String PATTERNS = "patterns";

	String outputPath;
	boolean autoOpen;
	boolean leadingWS;
	boolean trailingWS;
	boolean emptyTarget;
	boolean targetSameAsSource;
	boolean targetSameAsSourceWithCodes;
	boolean codeDifference;
	boolean patterns;

	public Parameters () {
		reset();
	}
	
	public String getOutputPath () {
		return outputPath;
	}

	public void setOutputPath (String outputPath) {
		this.outputPath = outputPath;
	}

	public boolean getAutoOpen () {
		return autoOpen;
	}

	public void setAutoOpen (boolean autoOpen) {
		this.autoOpen = autoOpen;
	}

	public boolean getLeadingWS () {
		return leadingWS;
	}

	public void setLeadingWS (boolean leadingWS) {
		this.leadingWS = leadingWS;
	}

	public boolean getTrailingWS () {
		return trailingWS;
	}

	public void setTrailingWS (boolean trailingWS) {
		this.trailingWS = trailingWS;
	}

	public boolean getEmptyTarget () {
		return emptyTarget;
	}

	public void setEmptyTarget (boolean emptyTarget) {
		this.emptyTarget = emptyTarget;
	}

	public boolean getTargetSameAsSource () {
		return targetSameAsSource;
	}

	public void setTargetSameAsSource (boolean targetSameAsSource) {
		this.targetSameAsSource = targetSameAsSource;
	}

	public boolean getTargetSameAsSourceWithCodes () {
		return targetSameAsSourceWithCodes;
	}

	public void setTargetSameAsSourceWithCodes (boolean targetSameAsSourceWithCodes) {
		this.targetSameAsSourceWithCodes = targetSameAsSourceWithCodes;
	}

	public boolean getCodeDifference () {
		return codeDifference;
	}

	public void setCodeDifference (boolean codeDifference) {
		this.codeDifference = codeDifference;
	}

	public boolean getPatterns () {
		return patterns;
	}

	public void setPatterns (boolean patterns) {
		this.patterns = patterns;
	}

	@Override
	public void reset () {
		outputPath = "${rootDir}/qaReport.html";
		autoOpen = false;
		leadingWS = true;
		trailingWS = true;
		emptyTarget = true;
		targetSameAsSource = true;
		targetSameAsSourceWithCodes = true;
		codeDifference = true;
		patterns = true;
	}

	@Override
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		outputPath = buffer.getString(OUTPUTPATH, outputPath);
		autoOpen = buffer.getBoolean(AUTOOPEN, autoOpen);
		leadingWS = buffer.getBoolean(LEADINGWS, leadingWS);
		trailingWS = buffer.getBoolean(TRAILINGWS, trailingWS);
		emptyTarget = buffer.getBoolean(EMPTYTARGET, emptyTarget);
		targetSameAsSource = buffer.getBoolean(TARGETSAMEASSOURCE, targetSameAsSource);
		targetSameAsSourceWithCodes = buffer.getBoolean(TARGETSAMEASSOURCE_WITHCODES, targetSameAsSourceWithCodes);
		codeDifference = buffer.getBoolean(CODEDIFFERENCE, codeDifference);
		patterns = buffer.getBoolean(PATTERNS, patterns);
	}

	@Override
	public String toString() {
		buffer.reset();
		buffer.setString(OUTPUTPATH, outputPath);
		buffer.setBoolean(AUTOOPEN, autoOpen);
		buffer.setBoolean(LEADINGWS, leadingWS);
		buffer.setBoolean(TRAILINGWS, trailingWS);
		buffer.setBoolean(EMPTYTARGET, emptyTarget);
		buffer.setBoolean(TARGETSAMEASSOURCE, targetSameAsSource);
		buffer.setBoolean(TARGETSAMEASSOURCE_WITHCODES, targetSameAsSourceWithCodes);
		buffer.setBoolean(CODEDIFFERENCE, codeDifference);
		buffer.setBoolean(PATTERNS, patterns);
		return buffer.toString();
	}
	
}
