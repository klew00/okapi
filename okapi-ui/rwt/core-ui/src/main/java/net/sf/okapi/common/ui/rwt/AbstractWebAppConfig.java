package net.sf.okapi.common.ui.rwt;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.application.Application;
import org.eclipse.rwt.application.Application.OperationMode;
import org.eclipse.rwt.application.ApplicationConfiguration;
import org.eclipse.rwt.lifecycle.IEntryPoint;

public abstract class AbstractWebAppConfig implements ApplicationConfiguration {

	protected abstract String getEntryPointId(); 
	protected abstract Class<? extends IEntryPoint> getEntryPointClass();
	
	public void configure(Application application) {
		application.setOperationMode(OperationMode.SWT_COMPATIBILITY);
		Map<String, String> properties = new HashMap<String, String>();
		//properties.put( WebClient.THEME_ID, FANCY_THEME_ID);
		//properties.put( WebClient.FAVICON, "images/favicon.png" );
		//application.addStyleSheet(FANCY_THEME_ID, "theme/fancy/fancy.css");
		application.addStyleSheet(RWT.DEFAULT_THEME_ID, getThemePath());
		application.addEntryPoint("/" + getEntryPointId(), getEntryPointClass(), properties);
	}
	
	protected String getThemePath() {
		return "theme/fancy/fancy.css";
	}

}
