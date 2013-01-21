package net.sf.okapi.common.ui.rwt;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import net.sf.okapi.common.UserConfiguration;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.widgets.JSExecutor;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.lifecycle.UICallBack;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractWebApp implements IEntryPoint {

	private Shell shell;
	
	protected abstract void createUI(Shell shell);
	protected abstract String getName();
	
	@SuppressWarnings("serial")
	@Override
	public int createUI() {
		UserConfiguration uc = new UserConfiguration();
		Locale.setDefault(Locale.ENGLISH); // To have non-localized OK/Cancel etc. buttons in dialogs
		Display display = new Display();
		
		HttpServletRequest request = RWT.getRequest();
		boolean fullScreenMode = request.getServletPath().endsWith(AbstractWebAppConfig.FULL_SCREEN_SUFFIX);
		RWT.getSessionStore().setAttribute("userAgent", request.getHeader("User-Agent"));
		RWT.getSessionStore().setAttribute("app", this);
		
		shell = new Shell(display, fullScreenMode ? SWT.NONE : SWT.TITLE | SWT.RESIZE | SWT.MAX);
		shell.setText(getName()); // Default title
		if (fullScreenMode) {
			shell.setMaximized(true);
		}
		else {
			// Set default bounds, can be changed in createUI
			shell.setLocation(100, 50);
			shell.setSize(600, 450);
		}
		createUI(shell);
		UICallBack.activate(shell.getClass().getName() + hashCode()); // For async runs to wake up the display

		shell.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent event) {
				UICallBack.deactivate(shell.getClass().getName() + hashCode());
			}			
		});
		
		shell.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event event) {
		        int style = SWT.OK | SWT.CANCEL | SWT.ICON_INFORMATION;
		        MessageBox messageBox = new MessageBox(shell, style);
		        messageBox.setText("Please confirm");
		        messageBox.setMessage(
		        		String.format("You are about to close %s. Refresh your browser to bring it back.",
		        		getName()));
		        event.doit = messageBox.open() == SWT.OK;
		      }
		    });
				
		shell.open();	    
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return 0;
	}
	
	void criticalError(String message) {		
		throw new RuntimeException(message);
	}
	
	public Shell getShell() {
		return shell;
	}
	
	public static AbstractWebApp getApp() {
		return (AbstractWebApp) RWT.getSessionStore().getAttribute("app");
	}
	
	public void openURL(String url) {
//		Util.openURL(url);
//		Browser browser = new Browser(shell, SWT.NONE);
//		browser.setUrl(url);
		//browser.execute("try{window.open(\"http://www.w3schools.com\")} catch(e) {alert(e)});");
		
//		int browserStyle = ExternalBrowser.LOCATION_BAR | ExternalBrowser.NAVIGATION_BAR;
//	    ExternalBrowser.open(getApp().getName(), url, browserStyle );
//		//ExternalBrowser.open("_blank", url, browserStyle );
		if (url.startsWith("mailto:")) {
			(new Browser(shell, SWT.NONE)).setUrl(url);
			return;
		}
//		HelpDialog hd = new HelpDialog(shell, getName() + " Help", url);
//		hd.showDialog();
		//JSExecutor.executeJS("alert( \"Hello World!\" );");
		//JSExecutor.executeJS("window.location=" + url + ";");
		JSExecutor.executeJS("window.open(\"" + url + "\");");
	}
}
