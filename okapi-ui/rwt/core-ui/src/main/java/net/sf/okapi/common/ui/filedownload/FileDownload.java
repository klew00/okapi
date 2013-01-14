// http://wiki.eclipse.org/RAP/FAQ#How_to_provide_a_download_link.3F
	
package net.sf.okapi.common.ui.filedownload;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.ui.rwt.AbstractWebApp;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.rwt.service.IServiceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Display;

public class FileDownload {

	private Browser browser;
//	private Shell shell;
	
	public FileDownload() {
		super();
		registerDownloadHandler();
//		if (browser == null) {
//			shell = new Shell(AbstractWebApp.getApp().getShell());
//			browser = new Browser(shell, SWT.NONE);			
//			shell = new Shell(AbstractWebApp.getApp().getShell());
			//browser = new Browser(AbstractWebApp.getApp().getShell(), SWT.NONE);
			browser = new Browser(Display.getCurrent().getActiveShell(), SWT.NONE);
		    //browser.setSize(0, 0);
//		}
	}
	
	public void open(String serverFileName, String clientFileName) {
		String url = createDownloadUrl(serverFileName, clientFileName);				
	    browser.setUrl(url);
		//AbstractWebApp.getApp().openURL(url);		
//		Util.openURL(url);
	}
	
	private void registerDownloadHandler() {
	    IServiceManager manager = RWT.getServiceManager();
	    IServiceHandler handler = new DownloadServiceHandler();
	    manager.registerServiceHandler("downloadServiceHandler", handler);
	}
	
	private String createDownloadUrl(String serverFileName, String clientFileName) {
	    StringBuilder url = new StringBuilder();
	    url.append(RWT.getRequest().getContextPath());
	    url.append(RWT.getRequest().getServletPath());
	    url.append("?");
	    url.append(IServiceHandler.REQUEST_PARAM);
	    url.append("=downloadServiceHandler");
	    url.append("&server-filename=");
	    //url.append(Util.makeURIFromPath("file://" + serverFileName));
	    url.append(serverFileName);
	    url.append("&client-filename=");
	    url.append(Util.getFilename(clientFileName, true)); // always short file name	    
	    return RWT.getResponse().encodeURL(url.toString());
	 }
}
