package net.sf.okapi.common.ui.rwt;

import net.sf.okapi.common.exceptions.OkapiNotImplementedException;

import org.eclipse.rwt.RWT;

public class RwtNotImplementedException extends OkapiNotImplementedException {
	
	private static final long serialVersionUID = -8633802547824722890L;

	private AbstractWebApp getApp() {
		return (AbstractWebApp) RWT.getSessionStore().getAttribute("app");
	}
	
	public RwtNotImplementedException(String message) {
		getApp().criticalError(String.format("Unimplemented in RAP: %s", 
				message));
	}
	
	public RwtNotImplementedException(Object sender, String message) {
		getApp().criticalError(String.format("Unimplemented in RAP: %s%s", 
				sender.getClass().getName(), message));
	}
}
