package net.sf.okapi.common.ui.fileupload;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiIOException;

import org.eclipse.rap.rwt.supplemental.fileupload.FileUploadEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.ProgressCollector;
import org.eclipse.swt.internal.widgets.UploadPanel;
import org.eclipse.swt.internal.widgets.ValidationHandler;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class OkapiUploadPanel extends UploadPanel {

	private static final long serialVersionUID = -7752303061413758509L;
	private boolean uploadFinished;
	private String fileName;
	private Shell shell;
//	private ProgressCollector progressCollector;
//	private ValidationHandler validationHandler;
	
	public OkapiUploadPanel(Composite parent, int style) {
		super(parent, style);
		shell = parent.getShell();
//		validationHandler = new ValidationHandler() {
//		      @Override
//		      public void updateEnablement() {
//		    	  OkapiUploadPanel.this.updateEnablement();
//		      }
//		    };
//		progressCollector = new ProgressCollector(validationHandler);
//		super.setValidationHandler(validationHandler);
//		super.setProgressCollector(progressCollector);
//		super.setAutoUpload(false);
//	    validationHandler.setNumUploads(1);
	}

//	protected void updateEnablement() {
//		if( progressCollector.isFinished() ) {
//			uploadFinished = true;
//			fileName = OkapiUploadPanel.super.getUploadedFile().getAbsolutePath();
//			shell.close();
//		}
//	}

	@Override
	public void uploadProgress(FileUploadEvent uploadEvent) {
		//super.uploadProgress(uploadEvent);
	}
	
	@Override
	public void uploadFinished(FileUploadEvent uploadEvent) {
		super.uploadFinished(uploadEvent);
		this.getDisplay().asyncExec( new Runnable() {
			@Override
			public void run() {
				uploadFinished = true;
				fileName = OkapiUploadPanel.super.getUploadedFile().getAbsolutePath();
				shell.close(); //!!!
			}			
		});		
	}
	
	@Override
	public void uploadFailed(FileUploadEvent uploadEvent) {
		//super.uploadFailed(uploadEvent);
		uploadFinished = true;
	}

	@Override
	@Deprecated
	public void startUpload() {
	}
	
	public boolean start() {
		if (Util.isEmpty(super.getSelectedFilename())) return false;			
		
		uploadFinished = false; 
		super.startUpload();
		return true;
		
//		while ( !shell.isDisposed() && !uploadFinished) {
//			if ( !shell.getDisplay().readAndDispatch() ); 
//				try {
//					Thread.sleep(1);
//				} catch (InterruptedException e) {
//				}
//		}
		
//		new Runnable() {
//
//			@Override
//			public void run() {
//				OkapiUploadPanel.super.startUpload();				
//			}
//			
//		}.run();
		
//		new Runnable() {
//			@Override
//			public void run() {
//				uploadFinished = false;
//				while (!uploadFinished) {
//					try {
//						Thread.sleep(1);
//					} catch (InterruptedException e) {
//					}
//				}
//			}			
//		}.run();
		
//		while (!uploadFinished) {
//			try {
//				Thread.sleep(1);
//			} catch (InterruptedException e) {
//			}
//		}
	}

	public String getFileName() {
		return fileName;
	}
}
