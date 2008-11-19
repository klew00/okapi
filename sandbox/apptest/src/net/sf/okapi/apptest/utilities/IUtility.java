package net.sf.okapi.apptest.utilities;

import net.sf.okapi.apptest.filters.FilterEvent;

public interface IUtility {

	public String getName ();
	
	public void setOptions (String targetLanguage);
	
	public void doProlog ();
	
	public void handleEvent (FilterEvent event);
	
	public void doEpilog ();
	
}
