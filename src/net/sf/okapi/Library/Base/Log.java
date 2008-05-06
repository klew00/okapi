/*===========================================================================*/
/* Copyright (C) 2007 ENLASO Corporation, Okapi Development Team             */
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
/* Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA              */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.Library.Base;

public class Log implements ILog {

	private boolean     m_bInProgress;
	private int         m_nErrorCount;
	private int         m_nWarningCount;

	public Log () {
		m_bInProgress = false;
		m_nErrorCount = 0;
		m_nWarningCount = 0;
	}
	
	public boolean beginProcess(String p_sText) {
		if ( inProgress() ) return false;
		if (( p_sText != null ) && ( p_sText.length() > 0 ))
			System.err.println(p_sText);
		m_nErrorCount = m_nWarningCount = 0;
		m_bInProgress = true;
		return true;
	}

	public boolean beginTask(String p_sText) {
		if (( p_sText != null ) && ( p_sText.length() > 0 ))
			System.err.println(p_sText);
		return true;
	}

	public void cancel (boolean p_bAskConfirmation) {
		if ( inProgress() )
		{
			if ( p_bAskConfirmation )
			{
				System.out.print(Res.getString("CONFIRM_CANCEL")); //$NON-NLS-1$
//TODO				char chRes = char.ToLower((char)Console.Read());
//				string sYN = m_RM.GetString("CONFIRM_YESNOLETTERS");
//				if ( chRes != sYN[0] ) return; // No cancellation
			}
			// Cancel the process
			endTask(null);
			endProcess(null);
		}
	}

	public void clear() {
		// Nothing to do
	}
	
	public boolean canContinue() {
		//TODO: Implement user-cancel with escape key
		return m_bInProgress;
	}

	public void endProcess(String p_sText) {
		if (( p_sText != null ) && ( p_sText.length() > 0 ))
			System.err.println(p_sText);
		m_bInProgress = false;
	}

	public void endTask(String p_sText) {
		if (( p_sText != null ) && ( p_sText.length() > 0 ))
			System.err.println(p_sText);
	}

	public boolean error(String p_sText)
	{
		System.err.println(Res.getString("ERROR") + p_sText); //$NON-NLS-1$
		m_nErrorCount++;
		return canContinue();
	}

	public long getCallerData() {
		// Nothing to do
		return 0;
	}

	public int getErrorAndWarningCount()
	{
		return m_nErrorCount + m_nWarningCount;
	}

	public int getErrorCount() {
		return m_nErrorCount;
	}

	public int getWarningCount() {
		return m_nWarningCount;
	}

	public void hide() {
		// Nothing to do
	}

	public boolean inProgress() {
		return m_bInProgress;
	}

	public boolean isVisible() {
		return true;
	}

	public boolean message(String p_sText) {
		System.err.println(p_sText);
		return canContinue();
	}

	public boolean newLine() {
		System.err.println();
		return canContinue();
	}

	public void save(String p_sPath) {
		// Nothing to do
	}

	public void setCallerData(long p_lData) {
		// Nothing to do
	}

	public void setHelp(String p_sPath) {
		// Nothing to do
	}

	public boolean setLog (int p_nType,
		int p_nValue,
		String p_sValue)
	{
		switch ( p_nType )
		{
			case LogType.ERROR:
				return error(p_sValue);
			case LogType.WARNING:
				return warning(p_sValue);
			case LogType.MESSAGE:
				return message(p_sValue);
			case LogType.USERFEEDBACK:
				return canContinue();
			case LogType.MAINPROGRESS:
			case LogType.SUBPROGRESS:
				// No progress displayed, but make sure the user feedback
				// gets processed and returned
		}
		return canContinue();
	}

	public void setMainProgressMode(int p_nValue) {
		// Nothing to do
	}

	public boolean setOnTop(boolean p_bValue) {
		// Nothing to do
		return true;
	}

	public void setSubProgressMode(int p_nValue) {
		// Nothing to do
	}

	public void setTitle(String p_sValue) {
		// Nothing to do
	}

	public void show() {
		// Nothing to do
	}

	public boolean warning(String p_sText) {
		System.err.println(Res.getString("WARNING") + p_sText); //$NON-NLS-1$
		m_nWarningCount++;
		return canContinue();
	}

}
