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

package net.sf.okapi.connectors.pensieve;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.ParametersDescription;

public class Parameters extends BaseParameters {

	public static final String DBDIRECTORY = "dbDirectory";

	private String dbDirectory;
	
	public Parameters () {
		reset();
		toString();
	}
	
	public Parameters (String initialData) {
		fromString(initialData);
	}
	
	public String getDbDirectory () {
		return dbDirectory;
	}

	public void setDbDirectory(String dbDirectory) {
		this.dbDirectory = dbDirectory;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		dbDirectory = buffer.getString(DBDIRECTORY, dbDirectory);
	}

	public void reset () {
		dbDirectory = "";
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setString(DBDIRECTORY, dbDirectory);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(DBDIRECTORY, "TM Directory", "Directory of the TM database");
		return desc;
	}

}
