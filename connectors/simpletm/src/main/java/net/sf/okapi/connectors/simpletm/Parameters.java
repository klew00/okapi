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

	 /** 
	  * The full path of the database name to open.
	  * The path can have the extension ".data.db" or not extension.
	  */
	private String dbPath;
	
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

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		dbPath = buffer.getString("dbPath", dbPath);
	}

	public void reset () {
		dbPath = "";
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setString("dbPath", dbPath);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add("dbPath",
			"Path of the Database file", "Full path of the database file (.data.db)");
		return desc;
	}

}
