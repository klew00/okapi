package net.sf.okapi.lib.ui.segmentation;

import net.sf.okapi.common.FileUtil;
import net.sf.okapi.common.IHelp;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.ui.filedownload.FileDownload;
import net.sf.okapi.common.ui.filedownload.SaveAsDialog;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class WebSRXEditor extends SRXEditor {

	private Shell parent;
	private IHelp helpParam;
	private String serverPath;
	private String clientPath;
	
	public WebSRXEditor(Shell parent, boolean asDialog, IHelp helpParam) {
		super(parent, asDialog, helpParam);		
		this.parent = parent;
		this.helpParam = helpParam;
//		Rectangle shellBounds = shell.getBounds();
		
		// Disable unsupported items on menu system
	    Menu menuBar = parent.getMenuBar();
	    Menu fileMenu = menuBar.getItem(0).getMenu();
	    
	    MenuItem loadFromClipboard = fileMenu.getItem(4);
	    loadFromClipboard.setEnabled(false);
	    
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
	    	    
	    MenuItem saveToClipboard = fileMenu.getItem(8);
	    saveToClipboard.setEnabled(false);
	    
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
	    
//	    if( !shell.getMaximized() && shellBounds.x == 0 && shellBounds.y == 0 ) {
//		      shell.setLocation(100, 50);
//		      shell.setSize(600, 450);
//		}
	}

	protected void loadSRXDocument_rulesLoaded(String path) {
		serverPath = path;
		clientPath = Util.getFilename(path, true);
	}
	
	@Override
	protected String saveSRXDocument_getPath() {
		SaveAsDialog dlg = new SaveAsDialog(parent, Res.getString("edit.saveDocCaption"));
		String result = dlg.showDialog(clientPath);		
		if (Util.isEmpty(result)) return null;
		
		clientPath = result;
		if (Util.isEmpty(serverPath)) {
			// serverPath was not set, no file was uploaded, we create a temp file
			serverPath = FileUtil.createTempFile("~okapi_ratel_webapp_");
		}
		return serverPath;
	}
	
	@Override
	protected String updateCaption_getFileName(String srxPath) {
		return clientPath;
	}
	
	@Override
	protected void saveSRXDocument_afterSave(String path, boolean saveAsMode) {
		if (saveAsMode) {
			FileDownload fd = new FileDownload();
			fd.open(serverPath, clientPath);
		}		
	}	
	
	@Override
	protected String[] segmentTextFile_getPaths(String testInputPath,
			String testOutputPath, boolean htmlOutput) {
		// Get the input file
		WebFileProcessingDialog dlg = new WebFileProcessingDialog(parent, helpParam);
		return dlg.showDialog(testInputPath, testOutputPath, htmlOutput);
	}
	
	@Override
	protected void segmentTextFile_processResult(String testOutputPath) {
		FileDownload fd = new FileDownload();
		fd.open(testOutputPath, Util.getFilename(testOutputPath, true));
	}
}
