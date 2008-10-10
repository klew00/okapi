package net.sf.okapi.apptest;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class MainForm {

	public static enum ProgressType {
		IDLE, IN_PROGRESSS, PAUSED, CANCELED, INACTIVE
	}
	
	private Shell shell;
	private Button btStart;
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
		
		shell.setSize(300, 200);
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
	
	private void start () {
		progressStatus = ProgressType.IN_PROGRESSS;
		updateControls();
		utilDriver.execute("test");
	}

	public void makeIdle () {
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
		btStart.setEnabled(progressStatus==ProgressType.IDLE);
		btPause.setEnabled(progressStatus==ProgressType.IN_PROGRESSS);
		btResume.setEnabled(progressStatus==ProgressType.PAUSED);
		btStop.setEnabled(progressStatus==ProgressType.IN_PROGRESSS);
	}
}
