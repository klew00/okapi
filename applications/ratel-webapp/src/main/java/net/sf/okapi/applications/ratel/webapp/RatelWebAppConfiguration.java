package net.sf.okapi.applications.ratel.webapp;

import net.sf.okapi.common.ui.rwt.AbstractWebAppConfig;

import org.eclipse.rwt.lifecycle.IEntryPoint;

public class RatelWebAppConfiguration extends AbstractWebAppConfig {

	@Override
	protected String getEntryPointId() {
		return "ui";
	}

	@Override
	protected Class<? extends IEntryPoint> getEntryPointClass() {
		return RatelWebApp.class;
	}

}
