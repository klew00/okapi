package net.sf.okapi.lib.tmdb;

import java.util.List;
import java.util.Map;

import net.sf.okapi.lib.tmdb.memory.Record;

public interface ITm {

	/**
	 * Gets the UUID of this TM.
	 * @return the UUID of this TM.
	 */
	public String getUUID ();
	
	/**
	 * Gets the database key of this TM.
	 * @return the database key of this TM.
	 */
	public long getKey ();
	
	/**
	 * Gets the name of this TM.
	 * @return the name of this TM.
	 */
	public String getName ();
	
	/**
	 * Gets the description of this TM.
	 * @return the description of this TM.
	 */
	public String getDescription ();

	/**
	 * Sets the list of fields to be returned by {@link #getRecords()}.
	 * @param names list of fields to be returned. 
	 */
	public void setRecordFields (List<String> names);

	public IRecord addRecord (Map<String, String> fields);

	// Not sure about this one
	public long addRecordVar (String ... vars);

	//TODO: we should have page-based getters
	public List<IRecord> getRecords ();
	
	/**
	 * Gets a list of all available fields in this TM.
	 * @return the list of all available fields in this TM.
	 */
	public List<String> getAvailableFields ();
	
}
