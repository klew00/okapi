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
