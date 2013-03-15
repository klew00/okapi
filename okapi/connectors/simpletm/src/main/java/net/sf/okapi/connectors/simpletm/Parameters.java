/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.connectors.simpletm;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.ParametersDescription;

public class Parameters extends BaseParameters {

	static final String DB_EXTENSION = ".h2.db";
	static final String DBPATH = "dbPath";
	static final String PENALIZETARGETWITHDIFFERENTCODES = "penalizeTargetWithDifferentCodes";
	static final String PENALIZESOURCEWITHDIFFERENTCODES = "penalizeSourceWithDifferentCodes";
	
	 /** 
	  * The full path of the database name to open.
	  * The path can have the extension {@link #DB_EXTENSION} or not extension.
	  */
	private String dbPath;
	private boolean penalizeTargetWithDifferentCodes;
	private boolean penalizeSourceWithDifferentCodes;

	public Parameters () {
		reset();
		toString();
	}
	
	public Parameters (String initialData) {
		fromString(initialData);
	}
	
	public String getDbPath () {
		return dbPath;
	}

	public void setDbPath (String dbPath) {
		this.dbPath = dbPath;
	}

	@Override
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		dbPath = buffer.getString(DBPATH, dbPath);
		penalizeTargetWithDifferentCodes = buffer.getBoolean(PENALIZETARGETWITHDIFFERENTCODES, penalizeTargetWithDifferentCodes);
		penalizeSourceWithDifferentCodes = buffer.getBoolean(PENALIZESOURCEWITHDIFFERENTCODES, penalizeSourceWithDifferentCodes);
	}
	
	public boolean getPenalizeTargetWithDifferentCodes () {
		return penalizeTargetWithDifferentCodes;
	}
	
	public void setPenalizeTargetWithDifferentCodes (boolean penalizeTargetWithDifferentCodes) {
		this.penalizeTargetWithDifferentCodes = penalizeTargetWithDifferentCodes;
	}
	
	public boolean getPenalizeSourceWithDifferentCodes () {
		return penalizeSourceWithDifferentCodes;
	}
	
	public void setPenalizeSourceWithDifferentCodes (boolean penalizeSourceWithDifferentCodes) {
		this.penalizeSourceWithDifferentCodes = penalizeSourceWithDifferentCodes;
	}
	
	@Override
	public void reset () {
		dbPath = "";
		penalizeTargetWithDifferentCodes = true;
		penalizeSourceWithDifferentCodes = true;
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setString(DBPATH, dbPath);
		buffer.setBoolean(PENALIZETARGETWITHDIFFERENTCODES, penalizeTargetWithDifferentCodes);
		buffer.setBoolean(PENALIZESOURCEWITHDIFFERENTCODES, penalizeSourceWithDifferentCodes);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(DBPATH,
			"Path of the Database file",
			String.format("Full path of the database file (%s)", DB_EXTENSION));
		desc.add(PENALIZESOURCEWITHDIFFERENTCODES,
			"Penalize exact matches when the source has different codes than the query", null);
		desc.add(PENALIZETARGETWITHDIFFERENTCODES,
			"Penalize exact matches when the target has different codes than the query", null);
		return desc;
	}

}
