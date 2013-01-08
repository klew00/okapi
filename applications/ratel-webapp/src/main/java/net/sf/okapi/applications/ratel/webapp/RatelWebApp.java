package net.sf.okapi.applications.ratel.webapp;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;

import net.sf.okapi.common.ui.BaseHelp;
import net.sf.okapi.common.ui.rwt.AbstractWebApp;
import net.sf.okapi.lib.ui.segmentation.SRXEditor;

public class RatelWebApp extends AbstractWebApp {

	@SuppressWarnings("unused")
	@Override
	protected void createUI(Shell shell) {
		Rectangle shellBounds = shell.getBounds();
		
		BaseHelp help = new BaseHelp("help"); //$NON-NLS-1$
	    SRXEditor editor = new SRXEditor(shell, false, help);
	    	    
	    if( !shell.getMaximized() && shellBounds.x == 0 && shellBounds.y == 0 ) {
		      shell.setLocation(100, 50);
		      shell.setSize(600, 400);
		}
	}

	@Override
	protected String getName() {
		return "Ratel";
	}

}
