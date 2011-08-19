package net.sf.okapi.lib.tmdb;

import java.util.List;

public interface IRecord {

	public long getKey ();
	
	public List<String> getFields ();
	
	public String get (int index);
	
	public void set (int index,
		String value);
	
}
