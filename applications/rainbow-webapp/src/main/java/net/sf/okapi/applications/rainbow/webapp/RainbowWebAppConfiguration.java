package net.sf.okapi.applications.rainbow.webapp;

import net.sf.okapi.common.ui.rwt.AbstractWebAppConfig;

import org.eclipse.rwt.lifecycle.IEntryPoint;

public class RainbowWebAppConfiguration extends AbstractWebAppConfig {

	@Override
	protected String getEntryPointId() {
		return "ui";
	}

	@Override
	protected Class<? extends IEntryPoint> getEntryPointClass() {
		return RainbowWebApp.class;
	}
	
	@Override
	protected String getFaviconPath() {
		return "Rainbow.png";
	}
	
	@Override
	protected String getPageTitle() {
		return "Okapi Rainbow";
	}
}
