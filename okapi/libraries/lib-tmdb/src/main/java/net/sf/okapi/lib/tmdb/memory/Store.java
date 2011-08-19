package net.sf.okapi.lib.tmdb.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.okapi.lib.tmdb.IRecord;

public class Store {

	private ArrayList<IRecord> records;
	private HashMap<String, Integer> fieldNames;
	private ArrayList<Integer> recFields;
	private long lastKey = 0;
	
	public Store () {
		records = new ArrayList<IRecord>();
		fieldNames = new HashMap<String, Integer>();
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

	public IRecord add (Map<String, String> fields) {
		Record rec = new Record(++lastKey);
		for ( String fn : fields.keySet() ) {
			Integer index = fieldNames.get(fn);
			if ( index == null ) {
				fieldNames.put(fn, fieldNames.size());
				index = fieldNames.size()-1;
			}
			rec.set(index, fields.get(fn));
		}
		records.add(rec);
		return rec;
	}
	
	public Record add (String ... vars) {
		Record rec = new Record(++lastKey, vars);
		records.add(rec);
		return rec;
	}

	public List<IRecord> getRecords () {
		List<IRecord> list = new ArrayList<IRecord>();
		for ( IRecord rec : records ) {
			Record outRec = new Record(rec.getKey());
			for ( Integer index : recFields ) {
				outRec.add(rec.get(index));
			}
			list.add(outRec);
		}
		return list;
	}
	
	public List<String> getAvailableFields () {
		return new ArrayList<String>(fieldNames.keySet());
	}
	
}
