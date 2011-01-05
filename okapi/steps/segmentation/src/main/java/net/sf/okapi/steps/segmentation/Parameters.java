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

public class Parameters extends BaseParameters {
	
	public static int TRIM_DEFAULT = -1;
	public static int TRIM_NO = 0;
	public static int TRIM_YES = 1;
	
	private static final String FORCESEGMENTEDOUTPUT = "forceSegmentedOutput";
	
	public boolean segmentSource;
	public boolean segmentTarget;
	public String sourceSrxPath;
	public String targetSrxPath;
	public boolean copySource;
	public boolean checkSegments;
	public int trimSrcLeadingWS;
	public int trimSrcTrailingWS;
	public int trimTrgLeadingWS;
	public int trimTrgTrailingWS;
	private boolean forceSegmentedOutput;

	
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
	}
	
	public boolean getForcesegmentedOutput () {
		return forceSegmentedOutput;
	}

	public void setForcesegmentedOutput (boolean forceSegmentedOutput) {
		this.forceSegmentedOutput = forceSegmentedOutput;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		segmentSource = buffer.getBoolean("segmentSource", segmentSource);
		segmentTarget = buffer.getBoolean("segmentTarget", segmentTarget);
		sourceSrxPath = buffer.getString("sourceSrxPath", sourceSrxPath);
		targetSrxPath = buffer.getString("targetSrxPath", targetSrxPath);
		copySource = buffer.getBoolean("copySource", copySource);
		checkSegments = buffer.getBoolean("checkSegments", checkSegments);
		trimSrcLeadingWS = buffer.getInteger("trimSrcLeadingWS", trimSrcLeadingWS);
		trimSrcTrailingWS = buffer.getInteger("trimSrcTrailingWS", trimSrcTrailingWS);
		trimTrgLeadingWS = buffer.getInteger("trimTrgLeadingWS", trimTrgLeadingWS);
		trimTrgTrailingWS = buffer.getInteger("trimTrgTrailingWS", trimTrgTrailingWS);
		forceSegmentedOutput = buffer.getBoolean(FORCESEGMENTEDOUTPUT, forceSegmentedOutput);
	}

	@Override
	public String toString() {
		buffer.reset();
		buffer.setBoolean("segmentSource", segmentSource);
		buffer.setBoolean("segmentTarget", segmentTarget);
		buffer.setString("sourceSrxPath", sourceSrxPath);
		buffer.setString("targetSrxPath", targetSrxPath);
		buffer.setBoolean("copySource", copySource);
		buffer.setBoolean("checkSegments", checkSegments);
		buffer.setInteger("trimSrcLeadingWS", trimSrcLeadingWS);
		buffer.setInteger("trimSrcTrailingWS", trimSrcTrailingWS);
		buffer.setInteger("trimTrgLeadingWS", trimTrgLeadingWS);
		buffer.setInteger("trimTrgTrailingWS", trimTrgTrailingWS);
		buffer.setBoolean(FORCESEGMENTEDOUTPUT, forceSegmentedOutput);
		return buffer.toString();
	}	
}
