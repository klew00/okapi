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

package net.sf.okapi.applications.rainbow.utilities.extraction;

import java.util.UUID;

import net.sf.okapi.applications.rainbow.packages.xliff.Options;
import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.IParameters;

public class Parameters extends BaseParameters {
	
	public String pkgType;
	public boolean createZip;
	public String pkgName;
	public String outputFolder;
	public boolean preSegment;
	public String sourceSRX;
	public String targetSRX;
	public boolean preTranslate;
	public boolean useFileName;
	public boolean useGroupName;
	public boolean protectAccepted;
	public IParameters xliffOptions;
	public int threshold;
	public String transResClass;
	public String transResParams;
	public boolean useTransRes2;
	public String transResClass2;
	public String transResParams2;

	public Parameters () {
		reset();
	}
	
	public void reset() {
		pkgType = "xliff";
		createZip = false;
		pkgName = "pack1";
		outputFolder = "${ProjDir}";
		preSegment = false;
		sourceSRX = "";
		targetSRX = "";
		preTranslate = false;
		useFileName = false;
		useGroupName = false;
		protectAccepted = true;
		xliffOptions = new Options();
		threshold = 95;
		transResClass = "net.sf.okapi.connectors.simpletm.SimpleTMConnector";
		transResParams = null;
		useTransRes2 = false;
		transResClass2 = "net.sf.okapi.connectors.apertium.ApertiumMTConnector";
		transResParams2 = null;
	}

	public void fromString(String data) {
		reset();
		buffer.fromString(data);
		pkgType = buffer.getString("pkgType", pkgType);
		createZip = buffer.getBoolean("createZip", createZip);
		pkgName = buffer.getString("pkgName", pkgName);
		outputFolder = buffer.getString("outputFolder", outputFolder);
		preSegment = buffer.getBoolean("preSegment", preSegment);
		sourceSRX = buffer.getString("sourceSRX", sourceSRX);
		targetSRX = buffer.getString("targetSRX", targetSRX);
		preTranslate = buffer.getBoolean("preTranslate", preTranslate);
		useFileName = buffer.getBoolean("useFileName", useFileName);
		useGroupName = buffer.getBoolean("useGroupName", useGroupName);
		protectAccepted = buffer.getBoolean("protectAccepted", protectAccepted);
		xliffOptions.fromString(buffer.getGroup("xliffOptions"));
		threshold = buffer.getInteger("threshold", threshold);
		transResClass = buffer.getString("transResClass", transResClass);
		transResParams = buffer.getGroup("transResParams", transResParams);
		useTransRes2 = buffer.getBoolean("useTransRes2", useTransRes2);
		transResClass2 = buffer.getString("transResClass2", transResClass2);
		transResParams2 = buffer.getGroup("transResParams2", transResParams2);
	}

	public String toString() {
		buffer.reset();
		buffer.setString("pkgType", pkgType);
		buffer.setBoolean("createZip", createZip);
		buffer.setString("pkgName", pkgName);
		buffer.setString("outputFolder", outputFolder);
		buffer.setBoolean("preSegment", preSegment);
		buffer.setString("sourceSRX", sourceSRX);
		buffer.setString("targetSRX", targetSRX);
		buffer.setBoolean("preTranslate", preTranslate);
		buffer.setBoolean("useFileName", useFileName);
		buffer.setBoolean("useGroupName", useGroupName);
		buffer.setBoolean("protectAccepted", protectAccepted);
		buffer.setGroup("xliffOptions", xliffOptions.toString());
		buffer.setInteger("threshold", threshold);
		buffer.setString("transResClass", transResClass);
		buffer.setGroup("transResParams", transResParams);
		buffer.setBoolean("useTransRes2", useTransRes2);
		buffer.setString("transResClass2", transResClass2);
		buffer.setGroup("transResParams2", transResParams2);
		
		return buffer.toString();
	}
	
	public String makePackageID () {
		return UUID.randomUUID().toString();
	}

}
