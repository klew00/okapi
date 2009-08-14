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
	public String tmPath;
	public boolean useFileName;
	public boolean useGroupName;
	public boolean protectAccepted;
	public IParameters xliffOptions;
	public boolean leverageOnlyExact;

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
		tmPath = "";
		useFileName = true;
		useGroupName = true;
		protectAccepted = true;
		xliffOptions = new Options();
		leverageOnlyExact = true;
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
		tmPath = buffer.getString("tmPath", tmPath);
		useFileName = buffer.getBoolean("useFileName", useFileName);
		useGroupName = buffer.getBoolean("useGroupName", useGroupName);
		protectAccepted = buffer.getBoolean("protectAccepted", protectAccepted);
		xliffOptions.fromString(buffer.getGroup("xliffOptions"));
		leverageOnlyExact = buffer.getBoolean("leverageOnlyExact", leverageOnlyExact);
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
		buffer.setString("tmPath", tmPath);
		buffer.setBoolean("useFileName", useFileName);
		buffer.setBoolean("useGroupName", useGroupName);
		buffer.setBoolean("protectAccepted", protectAccepted);
		buffer.setGroup("xliffOptions", xliffOptions.toString());
		buffer.setBoolean("leverageOnlyExact", leverageOnlyExact);
		return buffer.toString();
	}
	
	public String makePackageID () {
		return UUID.randomUUID().toString();
	}

}
