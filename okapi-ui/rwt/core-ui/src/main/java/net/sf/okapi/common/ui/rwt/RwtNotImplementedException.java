package net.sf.okapi.common.ui.rwt;

import net.sf.okapi.common.exceptions.OkapiNotImplementedException;

public class RwtNotImplementedException extends OkapiNotImplementedException {
	
	private static final long serialVersionUID = -8633802547824722890L;

	public RwtNotImplementedException(String message) {
		AbstractWebApp.getApp().criticalError(String.format("Unimplemented in RAP or Okapi: %s", 
				message));
	}
	
	public RwtNotImplementedException(Object sender, String message) {
		AbstractWebApp.getApp().criticalError(String.format("Unimplemented in RAP or Okapi: %s%s", 
				sender.getClass().getName(), message));
	}
}
