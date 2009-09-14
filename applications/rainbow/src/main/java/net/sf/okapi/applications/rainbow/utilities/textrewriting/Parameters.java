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

package net.sf.okapi.applications.rainbow.utilities.textrewriting;

import net.sf.okapi.common.BaseParameters;

public class Parameters extends BaseParameters {
	
	public static final int  TYPE_KEEPORIGINAL = 0;
	public static final int  TYPE_XNREPLACE = 1;
	public static final int  TYPE_KEEPINLINE = 2;
	public static final int  TYPE_TRANSLATEEXACTMATCHES = 3;
	public static final int  TYPE_EXTREPLACE = 4;
	
	protected int type;
	protected boolean addPrefix;
	protected String prefix;
	protected boolean addSuffix;
	protected String suffix;
	protected boolean applyToExistingTarget;
	protected boolean addName;
	protected boolean addID;
	protected boolean segment;
	protected boolean markSegments;
	protected String sourceSrxPath;
	protected String targetSrxPath;
	protected String transResClass;
	protected String transResParams;
	
	public Parameters () {
		reset();
	}
	
	public void reset() {
		type = 0;
		addPrefix = false;
		prefix = "{START_";
		addSuffix = false;
		suffix = "_END}";
		applyToExistingTarget = false;
		addName = false;
		addID = false;
		segment = false;
		markSegments = false;
		sourceSrxPath = "";
		targetSrxPath = "";
		transResClass = "net.sf.okapi.connectors.simpletm.SimpleTMConnector";
		transResParams = null;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		type = buffer.getInteger("type", type);
		addPrefix = buffer.getBoolean("addPrefix", addPrefix);
		prefix = buffer.getString("prefix", prefix);
		addSuffix = buffer.getBoolean("addSuffix", addSuffix);
		suffix = buffer.getString("suffix", suffix);
		applyToExistingTarget = buffer.getBoolean("applyToExistingTarget", applyToExistingTarget);
		addName = buffer.getBoolean("addName", addName);
		addID = buffer.getBoolean("addID", addID);
		segment = buffer.getBoolean("segment", segment);
		markSegments = buffer.getBoolean("markSegments", markSegments);
		sourceSrxPath = buffer.getString("sourceSrxPath", sourceSrxPath);
		targetSrxPath = buffer.getString("targetSrxPath", targetSrxPath);
		transResClass = buffer.getString("transResClass", transResClass);
		transResParams = buffer.getString("transResParams", transResParams);
	}

	@Override
	public String toString() {
		buffer.reset();
		buffer.setInteger("type", type);
		buffer.setBoolean("addPrefix", addPrefix);
		buffer.setString("prefix", prefix);
		buffer.setBoolean("addSuffix", addSuffix);
		buffer.setString("suffix", suffix);
		buffer.setBoolean("applyToExistingTarget", applyToExistingTarget);
		buffer.setBoolean("addName", addName);
		buffer.setBoolean("addID", addID);
		buffer.setBoolean("segment", segment);
		buffer.setBoolean("markSegments", markSegments);
		buffer.setString("sourceSrxPath", sourceSrxPath);
		buffer.setString("targetSrxPath", targetSrxPath);
		buffer.setString("transResClass", transResClass);
		buffer.setGroup("transResParams", transResParams);
		return buffer.toString();
	}
	
}
