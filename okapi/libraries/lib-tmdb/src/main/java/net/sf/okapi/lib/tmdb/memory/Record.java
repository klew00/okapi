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

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.lib.tmdb.IRecord;

public class Record implements IRecord {

	private ArrayList<Object> fields;
	
	public Record (long key) {
		fields = new ArrayList<Object>();
		fields.add(key);
		fields.add(false);
	}
	
	public Record (long key,
		String ... vars)
	{
		this(key);
		for ( String f : vars ) {
			fields.add(f);
		}
	}

	@Override
	public long getKey () {
		return (Long)fields.get(0);
	}
	
	@Override
	public List<Object> getFields () {
		return fields;
	}

	@Override
	public int size () {
		return fields.size();
	}
	
	@Override
	public Object get (int index) {
		return fields.get(index);
	}

	@Override
	public void add (Object value) {
		fields.add(value);
	}
	
	@Override
	public void set (int index,
		Object value)
	{
		// Make sure the fields have room for this index
		while ( (index+1)-fields.size() > 0 ) {
			fields.add(null);
		}
		// Set the value
		fields.set(index, value);
	}

	@Override
	public boolean getFlag () {
		return (Boolean)fields.get(1);
	}

	@Override
	public void setFlag (boolean flag) {
		fields.set(1, flag);
	}
	
}
