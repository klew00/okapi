package net.sf.okapi.applications.serval.webapp;

import net.sf.okapi.common.ui.rwt.AbstractWebAppConfig;

import org.eclipse.rwt.lifecycle.IEntryPoint;

public class ServalWebAppConfiguration extends AbstractWebAppConfig {

	@Override
	protected String getEntryPointId() {
		return "ui";
	}

	@Override
	protected Class<? extends IEntryPoint> getEntryPointClass() {
		return ServalWebApp.class;
	}
	
	@Override
	protected String getFaviconPath() {
		return null;
	}
	
	@Override
	protected String getPageTitle() {
		return "Okapi Serval";
	}
}
