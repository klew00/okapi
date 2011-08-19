package net.sf.okapi.lib.tmdb.memory;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.lib.tmdb.IRecord;

public class Record implements IRecord {

	private long key;
	private ArrayList<String> fields;
	
	public Record (long key) {
		this.key = key;
		fields = new ArrayList<String>();
	}
	
	public Record (long key,
		String ... vars)
	{
		this(key);
		for ( String f : vars ) {
			fields.add(f);
		}
	}

	public long getKey () {
		return key;
	}
	
	public List<String> getFields () {
		return fields;
	}
	
	public String get (int index) {
		return fields.get(index);
	}

	public void add (String value) {
		fields.add(value);
	}
	
	@Override
	public void set (int index,
		String value)
	{
		// Make sure the fields have room for this index
		while ( (index+1)-fields.size() > 0 ) {
			fields.add(null);
		}
		// Set the value
		fields.set(index, value);
	}
	
}
