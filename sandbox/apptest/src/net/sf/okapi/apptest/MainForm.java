package net.sf.okapi.apptest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.okapi.common.ui.Dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class MainForm {

	public static enum ProgressType {
		IDLE, IN_PROGRESSS, PAUSED, CANCELED
	}
	
	private Shell shell;
	private Button btStart;
	private Button chkUsePipeline;
	private Button chkAllowUIInteraction;
	private Button btPause;
	private Button btResume;
	private Button btStop;
	private ProgressType progressStatus = ProgressType.IDLE;
	private UtilityDriver utilDriver;
	
	public MainForm (Shell shell) {
		this.shell = shell;
		GridLayout layTmp = new GridLayout();
		shell.setLayout(layTmp);

		utilDriver = new UtilityDriver(shell, this);
		
		btStart = new Button(shell, SWT.PUSH);
		btStart.setLayoutData(new GridData(GridData.CENTER | GridData.FILL_HORIZONTAL));
		btStart.setText("Start");
		btStart.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				start();
			}
		});
		
		chkUsePipeline = new Button(shell, SWT.CHECK);
		chkUsePipeline.setLayoutData(new GridData(GridData.CENTER | GridData.FILL_HORIZONTAL));
		chkUsePipeline.setText("Use the pipeline");
		chkUsePipeline.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				chkAllowUIInteraction.setEnabled(chkUsePipeline.getSelection());
			}
		});
		
		chkAllowUIInteraction = new Button(shell, SWT.CHECK);
		chkAllowUIInteraction.setLayoutData(new GridData(GridData.CENTER | GridData.FILL_HORIZONTAL));
		chkAllowUIInteraction.setText("Allow UI interaction");
		chkAllowUIInteraction.setSelection(true);
		
		btPause = new Button(shell, SWT.PUSH);
		btPause.setLayoutData(new GridData(GridData.CENTER | GridData.FILL_HORIZONTAL));
		btPause.setText("Pause");
		btPause.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				pause();
			}
		});
		
		btResume = new Button(shell, SWT.PUSH);
		btResume.setLayoutData(new GridData(GridData.CENTER | GridData.FILL_HORIZONTAL));
		btResume.setText("Resume");
		btResume.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				resume();
			}
		});
		
		btStop = new Button(shell, SWT.PUSH);
		btStop.setLayoutData(new GridData(GridData.CENTER | GridData.FILL_HORIZONTAL));
		btStop.setText("Stop");
		btStop.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				stop();
			}
		});
		
		shell.pack();
		Rectangle Rect = shell.getBounds();
		shell.setMinimumSize(Rect.width, Rect.height);
		shell.setSize(300, 200);
		Dialogs.centerWindow(shell, null);

		chkAllowUIInteraction.setEnabled(chkUsePipeline.getSelection());
		updateControls();
	}
	
	public void run () {
		try {
			Display Disp = shell.getDisplay();
			while ( !shell.isDisposed() ) {
				if (!Disp.readAndDispatch())
					Disp.sleep();
			}
		}
		finally {
			// Dispose of any global resources
		}
	}
	
	public boolean inProgress () {
		return (progressStatus == ProgressType.IN_PROGRESSS);
	}
	
	public boolean isCanceled () {
		return (progressStatus == ProgressType.CANCELED);
	}
	
	public boolean isPaused () {
		return (progressStatus == ProgressType.PAUSED);
	}
	
	private void showElapsedTime (Date start, Date end) {
		try {
			DateFormat timeOutput = new SimpleDateFormat("'Minutes='mm 'Seconds='ss 'Milliseconds='S"); 
			long diff = (end.getTime() - start.getTime());
			MessageBox dlg = new MessageBox(shell, SWT.ICON_INFORMATION);
			dlg.setMessage(timeOutput.format(diff));
			dlg.setText("Done");
			dlg.open();
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
		}
	}
	
	private void start () {
		progressStatus = ProgressType.IN_PROGRESSS;
		updateControls();
		Date startDate = new Date();
		
		if ( chkUsePipeline.getSelection() ) {
			utilDriver.pipelineExecute(chkAllowUIInteraction.getSelection());
		}
		else {
			utilDriver.simpleExecute();
		}
		Date endDate = new Date();
		showElapsedTime(startDate, endDate);
		progressStatus = ProgressType.IDLE;
		updateControls();
	}

	public void pause () {
		progressStatus = ProgressType.PAUSED;
		updateControls();
	}
	
	private void resume () {
		progressStatus = ProgressType.IN_PROGRESSS;
		updateControls();
	}
	
	private void stop () {
		progressStatus = ProgressType.CANCELED;
		updateControls();
	}
	
	private void updateControls () {
		boolean allowed = chkAllowUIInteraction.getSelection();
		btStart.setEnabled(progressStatus==ProgressType.IDLE);
		btPause.setEnabled(allowed && (progressStatus==ProgressType.IN_PROGRESSS));
		btResume.setEnabled(progressStatus==ProgressType.PAUSED);
		btStop.setEnabled(allowed && (progressStatus==ProgressType.IN_PROGRESSS));
	}
}
