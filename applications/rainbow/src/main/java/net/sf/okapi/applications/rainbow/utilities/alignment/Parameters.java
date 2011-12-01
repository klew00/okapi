/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
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
============================================================================*/

package net.sf.okapi.applications.rainbow.utilities.alignment;

import net.sf.okapi.common.BaseParameters;

public class Parameters extends BaseParameters {
	
	public boolean segment;
	public String sourceSrxPath;
	public String targetSrxPath;
	public boolean checkSingleSegUnit;
	public boolean useAutoCorrection;
	public boolean createTMX;
	public String tmxPath;
	public boolean useTradosWorkarounds;
	public boolean createTM;
	public String tmPath;
	public boolean createAttributes;
	public String attributes;
	public boolean useExclusion;
	public String exclusion;
	public boolean createTMXForUnknown;
	public String tmxForUnknownPath;
	public String mtKey;

	public Parameters () {
		reset();
	}
	
	public void reset () {
		createTMX = true;
		tmxPath = "";
		createTMXForUnknown = false;
		tmxForUnknownPath = "";
		useTradosWorkarounds = true;
		createTM = false;
		tmPath = "";
		segment = false;
		sourceSrxPath = "";
		targetSrxPath = "";
		checkSingleSegUnit = true;
		useAutoCorrection = true;
		createAttributes = true;
		attributes = "Txt::FileName=${filename}\nTxt::GroupName=${resname}";
		useExclusion = false;
		exclusion = "";
		mtKey = "";
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		createTMX = buffer.getBoolean("createTMX", createTMX);
		tmxPath = buffer.getString("tmxPath", tmxPath);
		useTradosWorkarounds = buffer.getBoolean("useTradosWorkarounds", useTradosWorkarounds);
		createTM = buffer.getBoolean("createTM", createTM);
		tmPath = buffer.getString("simpletmPath", tmPath);
		segment = buffer.getBoolean("segment", segment);
		sourceSrxPath = buffer.getString("sourceSrxPath", sourceSrxPath);
		targetSrxPath = buffer.getString("targetSrxPath", targetSrxPath);
		checkSingleSegUnit = buffer.getBoolean("checkSingleSegUnit", checkSingleSegUnit);
		useAutoCorrection = buffer.getBoolean("useAutoCorrection", useAutoCorrection);
		createAttributes = buffer.getBoolean("createAttributes", createAttributes);
		attributes = buffer.getString("attributes", attributes);
		useExclusion = buffer.getBoolean("useExclusion", useExclusion);
		exclusion = buffer.getString("exclusion", exclusion);
		createTMXForUnknown = buffer.getBoolean("createTMXForUnknown", createTMXForUnknown);
		tmxForUnknownPath = buffer.getString("tmxForUnknownPath", tmxForUnknownPath);
		mtKey = buffer.getEncodedString("mtKey", mtKey);
	}

	public String toString () {
		buffer.reset();
		buffer.setBoolean("createTMX", createTMX);
		buffer.setString("tmxPath", tmxPath);
		buffer.setBoolean("useTradosWorkarounds", useTradosWorkarounds);
		buffer.setBoolean("createTM", createTM);
		buffer.setBoolean("segment", segment);
		buffer.setString("simpletmPath", tmPath);
		buffer.setString("sourceSrxPath", sourceSrxPath);
		buffer.setString("targetSrxPath", targetSrxPath);
		buffer.setBoolean("checkSingleSegUnit", checkSingleSegUnit);
		buffer.setBoolean("useAutoCorrection", useAutoCorrection);
		buffer.setBoolean("createAttributes", createAttributes);
		buffer.setString("attributes", attributes);
		buffer.setBoolean("useExclusion", useExclusion);
		buffer.setString("exclusion", exclusion);
		buffer.setBoolean("createTMXForUnknown", createTMXForUnknown);
		buffer.setString("tmxForUnknownPath", tmxForUnknownPath);
		buffer.setEncodedString("mtKey", mtKey);
		return buffer.toString();
	}
}
