package net.sf.okapi.filters.plaintext.common;


/**
 * Interface for enabling objects to receive notifications from other objects
 * 
 * @version 0.1, 16.06.2009
 * @author Sergei Vasilyev
 */

public interface INotifiable {

	/**
	 * Sends a notification for the object to react
	 * @param notification a string token identifying the notification
	 * @param info notification-specific  object 
	 * @return true if notification has been processed and doesn't need to be sent on
	 */
	boolean notify(String notification, Object info);
	
}
