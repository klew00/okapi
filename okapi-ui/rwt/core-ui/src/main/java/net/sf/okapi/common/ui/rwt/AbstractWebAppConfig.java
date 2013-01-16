package net.sf.okapi.common.ui.rwt;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.application.Application;
import org.eclipse.rwt.application.Application.OperationMode;
import org.eclipse.rwt.application.ApplicationConfiguration;
import org.eclipse.rwt.client.WebClient;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.resources.IResource;
import org.eclipse.rwt.resources.IResourceManager.RegisterOptions;

public abstract class AbstractWebAppConfig implements ApplicationConfiguration {

	protected abstract String getEntryPointId(); 
	protected abstract Class<? extends IEntryPoint> getEntryPointClass();
	
	public void configure(Application application) {		
		application.setOperationMode(OperationMode.SWT_COMPATIBILITY);
		Map<String, String> properties = new HashMap<String, String>();
		properties.put(WebClient.PAGE_TITLE, getPageTitle());
		properties.put(WebClient.FAVICON, getFaviconPath());
		//application.addStyleSheet(FANCY_THEME_ID, "theme/fancy/fancy.css");
		application.addStyleSheet(RWT.DEFAULT_THEME_ID, getThemePath());
		application.addEntryPoint("/" + getEntryPointId(), getEntryPointClass(), properties);
		application.addResource(createResource(getFaviconPath()));
	}
	
	// http://git.eclipse.org/c/rap/org.eclipse.rap.git/tree/bundles/org.eclipse.rap.examples/src/org/eclipse/rap/examples/internal/ExampleApplication.java?h=streams/1.5-maintenance
	private static IResource createResource(final String resourceName) {
	    return new IResource() {

	      public boolean isJSLibrary() {
	        return false;
	      }

	      public boolean isExternal() {
	        return false;
	      }

	      public RegisterOptions getOptions() {
	        return RegisterOptions.NONE;
	      }

	      public String getLocation() {
	        return resourceName;
	      }

	      public ClassLoader getLoader() {
	        return AbstractWebAppConfig.class.getClassLoader();
	      }

	      public String getCharset() {
	        return null;
	      }
	    };
	  }
	
	protected String getThemePath() {
		return "theme/fancy/fancy.css";
	}
	
	protected String getFaviconPath() {
		return null;
	}
	
	protected String getPageTitle() {
		return null;
	}

}
