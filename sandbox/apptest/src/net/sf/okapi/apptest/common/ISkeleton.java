package net.sf.okapi.apptest.common;

public interface ISkeleton {

	/**
	 * Adds some raw text data to the skeleton annotation.
	 * @param data The data to add.
	 */
	public void add (String data);
	
	/**
	 * Adds a reference to the skeleton annotation.
	 * @param refId The reference ID.
	 */
	public void addRef (String refId);

	public void addStartElement (String name);
	
	public void addEndElement (String name);
	
	public void addAttribute (String name, String value);
	
	/**
	 * Closes data after {@link #addStartElement(String)} has been called. You
	 * need to call this method only if the start element was not closed
	 * implicitly by a call to any of the other methods except
	 * {@link #addAttribute(String, String)}. This method can be called
	 * erroneously without causing damages.
	 */
	public void closeStartElement ();

}
