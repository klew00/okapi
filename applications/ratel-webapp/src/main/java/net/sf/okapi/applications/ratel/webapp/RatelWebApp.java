package net.sf.okapi.applications.ratel.webapp;

import net.sf.okapi.common.ui.BaseHelp;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.UIUtil;
import net.sf.okapi.common.ui.filedownload.FileDownload;
import net.sf.okapi.common.ui.rwt.AbstractWebApp;
import net.sf.okapi.lib.ui.segmentation.SRXEditor;
import net.sf.okapi.lib.ui.segmentation.WebFileProcessingDialog;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class RatelWebApp extends AbstractWebApp {

	private String testInputPath;
	private String testOutputPath;
	private boolean htmlOutput;
	private SRXEditor editor;
	private BaseHelp help;
	
	@Override
	protected void createUI(Shell shell) {
		Rectangle shellBounds = shell.getBounds();
		
		help = new BaseHelp("help"); //$NON-NLS-1$
		editor = new SRXEditor(shell, false, help);
	    	    
	    // Disable unsupported items on menu system
	    Menu menuBar = shell.getMenuBar();
	    
	    Menu fileMenu = menuBar.getItem(0).getMenu();
	    MenuItem loadFromClipboard = fileMenu.getItem(4);
	    loadFromClipboard.setEnabled(false);
	    MenuItem fileSave = fileMenu.getItem(6);
	    fileSave.setEnabled(false);
	    MenuItem saveToClipboard = fileMenu.getItem(8);
	    saveToClipboard.setEnabled(false);
	    
	    Menu toolsMenu = menuBar.getItem(1).getMenu();
	    MenuItem testSegmentation = toolsMenu.getItem(0);
	    
	    // Remove current selection listeners	    
	    for (Object listener : SelectionEvent.getListeners(testSegmentation)) {
	    	SelectionEvent.removeListener(testSegmentation, (SelectionListener) listener);
		}
	    
		// Set new selection listener
	    testSegmentation.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				segmentTextFile();
            }
		});
	    
	    if( !shell.getMaximized() && shellBounds.x == 0 && shellBounds.y == 0 ) {
		      shell.setLocation(100, 50);
		      shell.setSize(600, 400);
		}
	}

	@Override
	protected String getName() {
		return "Ratel";
	}
	
	private void segmentTextFile () {
		try {
			// Get the input file
			WebFileProcessingDialog dlg = new WebFileProcessingDialog(getShell(), help);
			String[] result = dlg.showDialog(testInputPath, testOutputPath, htmlOutput);
			if ( result == null ) return; // Canceled
			testInputPath = result[0];
			testOutputPath = result[1];
			htmlOutput = (result[2]!=null);

			// Process
			editor.getFileProcessor().process(testInputPath, testOutputPath, htmlOutput, editor.getSegmenter());

			// Show the result TODO !!! Opens on the server, change to download
			//UIUtil.start("file://" + testOutputPath);
			FileDownload.open(testOutputPath);
		}
		catch ( Throwable e ) {
			Dialogs.showError(getShell(), e.getLocalizedMessage(), null);
		}
	}

}
