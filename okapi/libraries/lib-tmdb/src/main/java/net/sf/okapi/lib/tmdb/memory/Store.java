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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.okapi.lib.tmdb.IRecord;

class Store {

	private ArrayList<IRecord> records;
	private LinkedHashMap<String, Integer> fieldNames;
	private ArrayList<Integer> recFields;
	private long lastKey = 0;
	
	public Store () {
		records = new ArrayList<IRecord>();
		fieldNames = new LinkedHashMap<String, Integer>();
	}
	
	public void setRecordFields (List<String> names) {
		recFields = new ArrayList<Integer>();
		// No check, this is just for testing the UI
		for ( String fn : names ) {
			Integer index = fieldNames.get(fn);
			if ( index != null ) {
				recFields.add(index);
			}
		}
	}

	public IRecord add (Map<String, Object> fields) {
		Record rec = new Record(++lastKey);
		for ( String fn : fields.keySet() ) {
			Integer index = fieldNames.get(fn);
			if ( index == null ) {
				index = fieldNames.size()+2; // +2 for key and flag
				fieldNames.put(fn, index);
			}
			rec.set(index, fields.get(fn));
		}
		records.add(rec);
		return rec;
	}
	
	public List<IRecord> getRecords () {
		List<IRecord> list = new ArrayList<IRecord>();
		for ( IRecord rec : records ) {
			Record outRec = new Record(rec.getKey());
			for ( Integer index : recFields ) {
				if ( index < rec.size() ) {
					outRec.add(rec.get(index));
				}
				else { // No such field
					outRec.add("");
				}
			}
			list.add(outRec);
		}
		return list;
	}
	
	public Results getResults () {
		return new Results(getRecords());
	}
	
	public List<String> getAvailableFields () {
		return new ArrayList<String>(fieldNames.keySet());
	}
	
}
