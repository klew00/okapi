/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.textmodification;

import net.sf.okapi.common.BaseParameters;

public class Parameters extends BaseParameters {
	
	public static final int TYPE_KEEPORIGINAL = 0;
	public static final int TYPE_XNREPLACE = 1;
	public static final int TYPE_KEEPINLINE = 2;
	public static final int TYPE_EXTREPLACE = 3;
	
	private static final String APPLYTOBLANKENTRIES = "applyToBlankEntries";
	private static final String EXPAND = "expand";
	private static final String SCRIPT = "script";
	
	public int type;
	public boolean addPrefix;
	public String prefix;
	public boolean addSuffix;
	public String suffix;
	public boolean applyToExistingTarget;
	public boolean addName;
	public boolean addID;
	public boolean markSegments;
	public boolean applyToBlankEntries;
	public boolean expand;
	public int script;
	
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
		markSegments = false;
		applyToBlankEntries = true; // For backward compatibility
		expand = false;
		script = 0;
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
		markSegments = buffer.getBoolean("markSegments", markSegments);
		applyToBlankEntries = buffer.getBoolean(APPLYTOBLANKENTRIES, applyToBlankEntries);
		expand = buffer.getBoolean(EXPAND, expand);
		script = buffer.getInteger(SCRIPT, script);
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
		buffer.setBoolean("markSegments", markSegments);
		buffer.setBoolean(APPLYTOBLANKENTRIES, applyToBlankEntries);
		buffer.setBoolean(EXPAND, expand);
		buffer.setInteger(SCRIPT, script);
		return buffer.toString();
	}
	
}
