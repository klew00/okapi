package net.sf.okapi.apptest.utilities;

import net.sf.okapi.apptest.common.IResource;

public interface IUtility {

	public String getName ();
	
	public void doProlog ();
	
	public void handleEvent (int eventType,
		IResource resource);
	
	public void doEpilog ();
	
}
