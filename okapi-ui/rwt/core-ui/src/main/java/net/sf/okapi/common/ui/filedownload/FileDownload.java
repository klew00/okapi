// http://wiki.eclipse.org/RAP/FAQ#How_to_provide_a_download_link.3F
	
package net.sf.okapi.common.ui.filedownload;

import net.sf.okapi.common.ui.rwt.AbstractWebApp;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.rwt.service.IServiceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;

public class FileDownload {

	private static Browser browser; 
	static {
		registerDownloadHandler();
	}
	
	public static void open(String fileName) {
		if (browser == null) {
			browser = new Browser(AbstractWebApp.getApp().getShell(), SWT.NONE);
		    browser.setSize(0, 0);
		}		
	    browser.setUrl(createDownloadUrl(fileName));
	}
	
	private static void registerDownloadHandler() {
	    IServiceManager manager = RWT.getServiceManager();
	    IServiceHandler handler = new DownloadServiceHandler();
	    manager.registerServiceHandler("downloadServiceHandler", handler);
	}
	
	private static String createDownloadUrl(final String fileName) {
	    StringBuilder url = new StringBuilder();
	    url.append(RWT.getRequest().getContextPath());
	    url.append(RWT.getRequest().getServletPath());
	    url.append("?");
	    url.append(IServiceHandler.REQUEST_PARAM);
	    url.append("=downloadServiceHandler");
	    url.append("&filename=");
	    url.append(fileName);
	    return RWT.getResponse().encodeURL(url.toString());
	 }
}
