/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package net.sf.okapi.lib.tmdb.memory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.sf.okapi.lib.tmdb.IRecord;
import net.sf.okapi.lib.tmdb.ITm;

public class Tm implements ITm {

	private String name;
	private String description;
	private String uuid;
	private long key;
	private Store store;
	
	public Tm (long key,
		String name,
		String description)
	{
		uuid = UUID.randomUUID().toString();
		this.key = key;
		this.name = name;
		this.description = description;
		store = new Store();
	}
	
	@Override
	public long getKey () {
		return key;
	}
	
	@Override
	public String getDescription () {
		return description;
	}

	@Override
	public String getName () {
		return name;
	}

	@Override
	public String getUUID () {
		return uuid;
	}

	@Override
	public void setRecordFields (List<String> names) {
		store.setRecordFields(names);
	}

	@Override
	public List<IRecord> getRecords () {
		return store.getRecords();
	}

	public IRecord addRecord (Map<String, String> fields) {
		return store.add(fields);
	}
	
	@Override
	public long addRecordVar (String ... vars) {
		Record rec = store.add(vars);
		return rec.getKey();
	}

	@Override
	public List<String> getAvailableFields () {
		return store.getAvailableFields();
	}

}
