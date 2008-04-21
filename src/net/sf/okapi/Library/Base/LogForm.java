package net.sf.okapi.Library.Base;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * General-purpose default log as a window.
 * This log is not thread-safe.
 */
public class LogForm implements ILog {

	private Shell            m_Shell;
	//private String           m_sHelp;
	private Text             m_edLog;
	private int              m_nErrorCount;
	private int              m_nWarningCount;
	private long             m_lData = 0;
	private ProgressBar      m_pbPrimary;
	private ProgressBar      m_pbSecondary;
	
	public LogForm (Shell p_Parent) {
		m_Shell = new Shell(p_Parent);
		createContent();
	}
	
	private void createContent ()
	{
		m_Shell.setText("Log");
		GridLayout layTmp = new GridLayout();
		layTmp.numColumns = 4;
		m_Shell.setLayout(layTmp);
		
		//=== Buttons
		
		Composite cmpActions = new Composite(m_Shell, SWT.NONE);
		
		Button btHelp = new Button(cmpActions, SWT.PUSH);
		btHelp.setText("&Help");
		
		Button btClear = new Button(cmpActions, SWT.PUSH);
		btClear.setText("&Clear");
		btClear.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent e) {
	    	  clear();
	      }
	    });
		
		Button btStop = new Button(cmpActions, SWT.PUSH);
		btStop.setText("&Stop");
		
		Button btClose = new Button(cmpActions, SWT.PUSH);
		btClose.setText("&Close");
		
		//=== Progress
		
		m_pbPrimary = new ProgressBar(m_Shell, SWT.HORIZONTAL);
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.verticalSpan = 4;
		m_pbPrimary.setLayoutData(gdTmp);

		m_pbSecondary = new ProgressBar(m_Shell, SWT.HORIZONTAL);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.verticalSpan = 4;
		m_pbSecondary.setLayoutData(gdTmp);
		
		//=== Log itself

		m_edLog = new Text(m_Shell, SWT.MULTI | SWT.BORDER);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.verticalSpan = 4;
		m_edLog.setLayoutData(gdTmp);
		
		m_Shell.pack();
	}

	public boolean beginProcess (String p_sTtext) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean beginTask (String p_sText) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canContinue () {
		// TODO Auto-generated method stub
		return false;
	}

	public void cancel (boolean p_bAskConfirmation) {
		// TODO Auto-generated method stub
	}

	public void clear () {
		m_edLog.setText("");
	}

	public void endProcess (String p_sText) {
		// TODO Auto-generated method stub
	}

	public void endTask (String p_sText) {
		// TODO Auto-generated method stub
	}

	public boolean error (String p_sText) {
		return setLog(LogType.ERROR, 0, p_sText);
	}

	public long getCallerData () {
		return m_lData;
	}

	public int getErrorAndWarningCount () {
		return m_nErrorCount+m_nWarningCount;
	}

	public int getErrorCount () {
		return m_nErrorCount;
	}

	public int getWarningCount () {
		return m_nWarningCount;
	}

	public boolean inProgress () {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean message (String p_sText) {
		return setLog(LogType.MESSAGE, 0, p_sText);
	}

	public boolean newLine () {
		return setLog(LogType.MESSAGE, 0, "\n");
	}

	public void save (String p_sPath) {
		//TODO: Implement save as for the log text
	}

	public void setCallerData (long p_lData) {
		// Not used, just store it
		m_lData = p_lData;
	}

	public void setHelp (String p_sPath) {
		//m_sHelp = p_sPath;
	}

	public boolean setLog (int p_nType,
		int p_nValue,
		String p_sValue)
	{
		//m_edLog.
		switch ( p_nType ) {
			case LogType.ERROR:
				break;
			case LogType.WARNING:
				break;
		}
		return false;
	}

	public void setMainProgressMode (int p_nValue) {
		if ( p_nValue < 0 ) p_nValue = 0;
		if ( p_nValue > 100 ) p_nValue = 100;
		//m_pbPrimary.set.setValue(p_nValue);
	}

	public boolean setOnTop (boolean p_bValue) {
		//TODO boolean bRes = isAlwaysOnTop();
		//setAlwaysOnTop(p_bValue);
		return false; //bRes;
	}

	public void setSubProgressMode (int p_nValue) {
		if ( p_nValue < 0 ) p_nValue = 0;
		if ( p_nValue > 100 ) p_nValue = 100;
		//m_pbSecondary.setValue(p_nValue);
	}

	public boolean warning (String p_sText) {
		return setLog(LogType.WARNING, 0, p_sText);
	}

	public void hide() {
		// TODO Auto-generated method stub
		
	}

	public void setTitle(String value) {
		// TODO Auto-generated method stub
		
	}

	public void show() {
		// TODO Auto-generated method stub
		
	}

	public boolean isVisible() {
		return m_Shell.isVisible();
	}
}
