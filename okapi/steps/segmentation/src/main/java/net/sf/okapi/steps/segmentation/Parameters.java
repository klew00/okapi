/*===========================================================================
  Copyright (C) 2009-2011 by the Okapi Framework contributors
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

package net.sf.okapi.steps.segmentation;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.ReferenceParameter;

public class Parameters extends BaseParameters {
	
	public static int TRIM_DEFAULT = -1;
	public static int TRIM_NO = 0;
	public static int TRIM_YES = 1;
	
	public enum SegmStrategy {
		KEEP_EXISTING,
		
		OVERWRITE_EXISTING,
		
		DEEPEN_EXISTING
	}
	
	private static final String FORCESEGMENTEDOUTPUT = "forceSegmentedOutput";
	private static final String OVERWRITESEGMENTATION = "overwriteSegmentation";
	private static final String DEEPENSEGMENTATION = "deepenSegmentation";
	private static final String SOURCESRXPATH = "sourceSrxPath";
	private static final String TARGETSRXPATH = "targetSrxPath";
	
	public boolean segmentSource;
	public boolean segmentTarget;
	public boolean copySource;
	public boolean checkSegments;
	public int trimSrcLeadingWS;
	public int trimSrcTrailingWS;
	public int trimTrgLeadingWS;
	public int trimTrgTrailingWS;
	private String sourceSrxPath;
	private String targetSrxPath;
	private boolean forceSegmentedOutput;
	private boolean overwriteSegmentation;
	private boolean deepenSegmentation;

	
	public Parameters () {
		reset();
	}
	
	public void reset() {
		segmentSource = true;
		segmentTarget = false;
		sourceSrxPath = "";
		targetSrxPath = "";
		copySource = true;
		checkSegments = false;
		trimSrcLeadingWS = TRIM_DEFAULT;
		trimSrcTrailingWS = TRIM_DEFAULT;
		trimTrgLeadingWS = TRIM_DEFAULT;
		trimTrgTrailingWS = TRIM_DEFAULT;
		forceSegmentedOutput = true;
		overwriteSegmentation = false;
		deepenSegmentation = false;
	}
	
	public boolean getForcesegmentedOutput () {
		return forceSegmentedOutput;
	}

	public void setForcesegmentedOutput (boolean forceSegmentedOutput) {
		this.forceSegmentedOutput = forceSegmentedOutput;
	}
	
	public void setSourceSrxPath (String sourceSrxPath) {
		this.sourceSrxPath = sourceSrxPath.trim();
	}
	
	@ReferenceParameter
	public String getSourceSrxPath () {
		return sourceSrxPath;
	}
	
	public void setTargetSrxPath (String targetSrxPath) {
		this.targetSrxPath = targetSrxPath.trim();
	}
	
	@ReferenceParameter
	public String getTargetSrxPath () {
		return targetSrxPath;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		segmentSource = buffer.getBoolean("segmentSource", segmentSource);
		segmentTarget = buffer.getBoolean("segmentTarget", segmentTarget);
		sourceSrxPath = buffer.getString(SOURCESRXPATH, sourceSrxPath);
		targetSrxPath = buffer.getString(TARGETSRXPATH, targetSrxPath);
		copySource = buffer.getBoolean("copySource", copySource);
		checkSegments = buffer.getBoolean("checkSegments", checkSegments);
		trimSrcLeadingWS = buffer.getInteger("trimSrcLeadingWS", trimSrcLeadingWS);
		trimSrcTrailingWS = buffer.getInteger("trimSrcTrailingWS", trimSrcTrailingWS);
		trimTrgLeadingWS = buffer.getInteger("trimTrgLeadingWS", trimTrgLeadingWS);
		trimTrgTrailingWS = buffer.getInteger("trimTrgTrailingWS", trimTrgTrailingWS);
		forceSegmentedOutput = buffer.getBoolean(FORCESEGMENTEDOUTPUT, forceSegmentedOutput);
		overwriteSegmentation = buffer.getBoolean(OVERWRITESEGMENTATION, overwriteSegmentation);
		deepenSegmentation = buffer.getBoolean(DEEPENSEGMENTATION, deepenSegmentation);
	}

	@Override
	public String toString() {
		buffer.reset();
		buffer.setBoolean("segmentSource", segmentSource);
		buffer.setBoolean("segmentTarget", segmentTarget);
		buffer.setString(SOURCESRXPATH, sourceSrxPath);
		buffer.setString(TARGETSRXPATH, targetSrxPath);
		buffer.setBoolean("copySource", copySource);
		buffer.setBoolean("checkSegments", checkSegments);
		buffer.setInteger("trimSrcLeadingWS", trimSrcLeadingWS);
		buffer.setInteger("trimSrcTrailingWS", trimSrcTrailingWS);
		buffer.setInteger("trimTrgLeadingWS", trimTrgLeadingWS);
		buffer.setInteger("trimTrgTrailingWS", trimTrgTrailingWS);
		buffer.setBoolean(FORCESEGMENTEDOUTPUT, forceSegmentedOutput);
		buffer.setBoolean(OVERWRITESEGMENTATION, overwriteSegmentation);
		buffer.setBoolean(DEEPENSEGMENTATION, deepenSegmentation);
		return buffer.toString();
	}

	public SegmStrategy getSegmentationStrategy() {
		if (!overwriteSegmentation && deepenSegmentation)
			return SegmStrategy.DEEPEN_EXISTING;
		
		else if (overwriteSegmentation)
			return SegmStrategy.OVERWRITE_EXISTING;
		
		else
			return SegmStrategy.KEEP_EXISTING;
	}
	
	public void setSegmentationStrategy(SegmStrategy strategy) {
		if (strategy == SegmStrategy.DEEPEN_EXISTING) {
			overwriteSegmentation = false;
			deepenSegmentation = true;
		}
		else if (strategy == SegmStrategy.OVERWRITE_EXISTING) {
			overwriteSegmentation = true;
			deepenSegmentation = false;
		}
		else {
			overwriteSegmentation = false;
			deepenSegmentation = false;
		}
	}
}
