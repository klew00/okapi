package net.sf.okapi.applications.checkmate.webapp;

import net.sf.okapi.common.filters.DefaultFilters;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.ui.BaseHelp;
import net.sf.okapi.common.ui.rwt.AbstractWebApp;
import net.sf.okapi.lib.ui.verification.QualityCheckEditor;

import org.eclipse.swt.widgets.Shell;

public class CheckMateWebApp extends AbstractWebApp {

	@Override
	protected void createUI(Shell shell) {
		if (!shell.getMaximized()) shell.setBounds(50, 50, 800, 500);
		BaseHelp help = new BaseHelp("help"); //$NON-NLS-1$
		
		QualityCheckEditor editor = new QualityCheckEditor();
		// Create and fill the configuration mapper
		IFilterConfigurationMapper fcMapper = new FilterConfigurationMapper();
		DefaultFilters.setMappings(fcMapper, false, true);
		
		// Initialize the editor
		editor.initialize(shell, false, help, fcMapper, null);
	    	    
	    // Disable unsupported items on menu system
//	    Menu menuBar = shell.getMenuBar();
//	    Menu fileMenu = menuBar.getItem(0).getMenu();
//	    
//	    MenuItem loadFromClipboard = fileMenu.getItem(4);
//	    loadFromClipboard.setEnabled(false);
//	    
//	    MenuItem fileSave = fileMenu.getItem(6);
//	    fileSave.setEnabled(false);
//	    
//	    MenuItem fileSaveAs = fileMenu.getItem(7);
//	    
//	    // Remove current selection listeners	    
//	    for (Object listener : SelectionEvent.getListeners(fileSaveAs)) {
//	    	SelectionEvent.removeListener(fileSaveAs, (SelectionListener) listener);
//		}
//	    
//		// Set new selection listener
//	    fileSaveAs.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent event) {
//				(new SRXDocumentHandler()).save(getShell(), editor.getSrxDocument());
//            }
//		});
//	    	    
//	    MenuItem saveToClipboard = fileMenu.getItem(8);
//	    saveToClipboard.setEnabled(false);
//	    
//	    Menu toolsMenu = menuBar.getItem(1).getMenu();
//	    MenuItem testSegmentation = toolsMenu.getItem(0);
//	    
//	    // Remove current selection listeners	    
//	    for (Object listener : SelectionEvent.getListeners(testSegmentation)) {
//	    	SelectionEvent.removeListener(testSegmentation, (SelectionListener) listener);
//		}
//	    
//		// Set new selection listener
//	    testSegmentation.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent event) {
//				segmentTextFile();
//            }
//		});
	    	    
	}

	@Override
	protected String getName() {
		return "CheckMate";
	}
}
