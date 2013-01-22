package net.sf.okapi.applications.checkmate.webapp;

import net.sf.okapi.common.ui.rwt.AbstractWebAppConfig;

import org.eclipse.rwt.lifecycle.IEntryPoint;

public class CheckMateWebAppConfiguration extends AbstractWebAppConfig {

	@Override
	protected String getEntryPointId() {
		return "ui";
	}

	@Override
	protected Class<? extends IEntryPoint> getEntryPointClass() {
		return CheckMateWebApp.class;
	}
	
	@Override
	protected String getFaviconPath() {
		return "checkmate16.png";
	}
	
	@Override
	protected String getPageTitle() {
		return "Okapi CheckMate";
	}
}
