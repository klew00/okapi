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

package net.sf.okapi.steps.segmentation;

import net.sf.okapi.common.BaseParameters;

public class Parameters extends BaseParameters {
	
	public boolean segmentSource;
	public boolean segmentTarget;
	public String sourceSrxPath;
	public String targetSrxPath;
	public boolean copySource;
	public boolean checkSegments;
	public boolean trimSrcLeadingWS;
	public boolean trimSrcTrailingWS;
	public boolean trimTrgLeadingWS;
	public boolean trimTrgTrailingWS;
	
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
		trimSrcLeadingWS = false;
		trimSrcTrailingWS = false;
		trimTrgLeadingWS = false;
		trimTrgTrailingWS = false;
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
		trimSrcLeadingWS = buffer.getBoolean("trimSrcLeadingWS", trimSrcLeadingWS);
		trimSrcTrailingWS = buffer.getBoolean("trimSrcTrailingWS", trimSrcTrailingWS);
		trimTrgLeadingWS = buffer.getBoolean("trimTrgLeadingWS", trimTrgLeadingWS);
		trimTrgTrailingWS = buffer.getBoolean("trimTrgTrailingWS", trimTrgTrailingWS);
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
		buffer.setBoolean("trimSrcLeadingWS", trimSrcLeadingWS);
		buffer.setBoolean("trimSrcTrailingWS", trimSrcTrailingWS);
		buffer.setBoolean("trimTrgLeadingWS", trimTrgLeadingWS);
		buffer.setBoolean("trimTrgTrailingWS", trimTrgTrailingWS);
		return buffer.toString();
	}	
}
