package net.sf.okapi.common.ui.rwt;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractWebApp implements IEntryPoint {

	protected abstract void createUI(Shell shell);
	protected abstract String getName();
	
	@SuppressWarnings("serial")
	@Override
	public int createUI() {
		Locale.setDefault(Locale.ENGLISH); // To have non-localized OK/Cancel etc. buttons in dialogs
		Display display = new Display();
				
		HttpServletRequest request = RWT.getRequest();
		RWT.getSessionStore().setAttribute("userAgent", request.getHeader("User-Agent"));
		RWT.getSessionStore().setAttribute("app", this);
		
		final Shell shell = new Shell(display, SWT.TITLE | SWT.RESIZE | SWT.MAX);
		shell.setText(getName()); // Default title
		createUI(shell);
		
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
}
