/*===========================================================================*/
/* Copyright (C) 2008 ENLASO Corporation, Okapi Development Team             */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.Application.Borneo;

import net.sf.okapi.Library.Base.ILog;
import net.sf.okapi.Library.Base.LogType;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;

class LogPanel extends Composite implements ILog {

	private Text        m_edLog;
	private Button      m_btStop;
	private ProgressBar m_pbPrimary;
	private ProgressBar m_pbSecondary;
//	private String      m_sHelpPath;
	private long        m_lData = 0;
	private int         m_nErrors = 0;
	private int         m_nWarnings = 0;
	private MainForm    m_MF;
	private boolean     m_bInProgress = false;
	
	LogPanel (Composite p_Parent,
		int p_nFlags,
		MainForm p_MF)
	{
		super(p_Parent, p_nFlags);
		m_MF = p_MF;
		createContent();
	}
	
	private void createContent () {
		GridLayout layTmp = new GridLayout();
		layTmp.numColumns = 2;
		layTmp.marginHeight = 0;
		layTmp.marginWidth = 0;
		layTmp.horizontalSpacing = 2;
		layTmp.verticalSpacing = 2;
		setLayout(layTmp);
		
		m_edLog = new Text(this, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
		GridData gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.verticalSpan = 3;
		m_edLog.setLayoutData(gdTmp);
		
		m_btStop = new Button(this, SWT.PUSH);
		m_btStop.setText("Stop");
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdTmp.widthHint = 80;
		m_btStop.setLayoutData(gdTmp);
		m_btStop.setEnabled(false);
		
		m_pbPrimary = new ProgressBar(this, SWT.NONE);
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdTmp.widthHint = 80;
		m_pbPrimary.setLayoutData(gdTmp);
		
		m_pbSecondary = new ProgressBar(this, SWT.NONE);
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdTmp.widthHint = 80;
		m_pbSecondary.setLayoutData(gdTmp);
	}

	public boolean beginProcess (String p_sText) {
		clear();
		m_nErrors = m_nWarnings = 0;
		m_edLog.insert(Res.getString("LOG_STARTPROCESS")+"\n");
		m_bInProgress = true;
		if ( p_sText != null ) return message(p_sText);
		return true;
	}

	public boolean beginTask (String p_sText) {
		m_edLog.insert(Res.getString("LOG_STARTTASK")
			+ ((p_sText==null) ? "" : (" - "+p_sText)) + "\n");
		return true;
	}

	public boolean canContinue() {
		// TODO Auto-generated method stub
		return true;
	}

	public void cancel (boolean p_bAskConfirmation) {
		// TODO Auto-generated method stub
	}

	public void clear () {
		m_edLog.setText("");
	}

	public void endProcess (String p_sText) {
		if ( p_sText != null ) message(p_sText);
		m_edLog.insert(String.format(Res.getString("LOG_ENDPROCESS"), m_nErrors, m_nWarnings));
		m_bInProgress = false;
	}

	public void endTask (String p_sText) {
		if ( p_sText != null ) message(p_sText);
		m_edLog.insert(Res.getString("LOG_ENDTASK")+"\n");
	}

	public boolean error (String p_sText) {
		return setLog(LogType.ERROR, 0, p_sText);
	}

	public long getCallerData () {
		return m_lData;
	}

	public int getErrorAndWarningCount () {
		return m_nErrors + m_nWarnings;
	}

	public int getErrorCount () {
		return m_nErrors;
	}

	public int getWarningCount () {
		return m_nWarnings;
	}

	public void hide () {
		m_MF.toggleLog(false);
	}

	public boolean inProgress () {
		return m_bInProgress;
	}

	public boolean message (String p_sText) {
		return setLog(LogType.MESSAGE, 0, p_sText);
	}

	public boolean newLine () {
		return setLog(LogType.MESSAGE, 0, "\n");
	}

	public void save (String p_sPath) {
		// TODO: implement save log
	}

	public void setCallerData (long p_lData) {
		m_lData = p_lData;
	}

	public void setHelp (String p_sPath) {
		//m_sHelpPath = p_sPath;
	}

	public boolean setLog (int p_nType,
		int p_nValue,
		String p_sValue)
	{
		switch ( p_nType ) {
			case LogType.ERROR:
				m_edLog.insert("Error: " + p_sValue + "\n");
				m_nErrors++;
				break;
			case LogType.WARNING:
				m_edLog.insert("Warning: " + p_sValue + "\n");
				m_nWarnings++;
				break;
			case LogType.MESSAGE:
				m_edLog.insert(p_sValue + "\n");
				break;
			case LogType.SUBPROGRESS:
			case LogType.MAINPROGRESS:
				break;
			case LogType.USERFEEDBACK:
			default:
				break;
		}
		return canContinue();
	}

	public void setMainProgressMode (int p_nValue) {
		setLog(LogType.MAINPROGRESS, p_nValue, null);
	}

	public boolean setOnTop (boolean p_bValue) {
		// Nothing to do: no notion of top/not-top for the panel
		return true;
	}

	public void setSubProgressMode (int p_nValue) {
		setLog(LogType.SUBPROGRESS, p_nValue, null);
	}

	public void setTitle (String p_sValue) {
		// Not relevant for this class
	}

	public void show () {
		m_MF.toggleLog(true);
	}

	public boolean warning (String p_sText) {
		return setLog(LogType.WARNING, 0, p_sText);
	}
}
