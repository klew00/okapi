/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

package net.sf.okapi.applications.rainbow;

import net.sf.okapi.applications.rainbow.lib.ILog;
import net.sf.okapi.applications.rainbow.lib.LogType;
import net.sf.okapi.common.IHelp;

public class BatchLog implements ILog {

	private int warnCount;
	private int errCount;
	private boolean inProgress;
	
	public boolean beginProcess (String text) {
		inProgress = true;
		return inProgress;
	}

	public boolean beginTask (String text) {
		return true;
	}

	public boolean canContinue () {
		return true;
	}

	public void cancel (boolean askConfirmation) {
		// Do nothing
	}

	public void clear () {
		// Do nothing
	}

	public void endProcess (String text) {
		inProgress = false;
	}

	public void endTask (String text) {
		// Do nothing
	}

	public boolean error (String text) {
		return setLog(LogType.ERROR, 0, text);
	}

	public long getCallerData () {
		return 0;
	}

	public int getErrorAndWarningCount () {
		return errCount+warnCount;
	}

	public int getErrorCount () {
		return errCount;
	}

	public int getWarningCount () {
		return warnCount;
	}

	public void hide () {
		// Do nothing
	}

	public boolean inProgress () {
		return inProgress;
	}

	public boolean isVisible () {
		return true;
	}

	public boolean message (String text) {
		return setLog(LogType.MESSAGE, 0, text);
	}

	public boolean newLine () {
		System.out.println(""); //$NON-NLS-1$
		return false;
	}

	public void save (String path) {
		// Do nothing
	}

	public void setCallerData (long data) {
		// Do nothing
	}

	public void setHelp (IHelp helpParam,
		String helpPath)
	{
		// Do nothing
	}

	public boolean setLog (int p_nType,
		int p_nValue,
		String p_sValue)
	{
		switch ( p_nType ) {
		case LogType.ERROR:
			System.out.println(Res.getString("BatchLog.error")+p_sValue); //$NON-NLS-1$
			errCount++;
			break;
		case LogType.WARNING:
			System.out.println(Res.getString("BatchLog.warning")+p_sValue); //$NON-NLS-1$
			warnCount++;
			break;
		case LogType.MESSAGE:
			System.out.println(p_sValue);
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

	public void setMainProgressMode (int value) {
		// Do nothing
	}

	public boolean setOnTop (boolean value) {
		// Do nothing
		return false;
	}

	public void setSubProgressMode (int value) {
		// Do nothing
	}

	public void setTitle (String value) {
		// Do nothing
	}

	public void show () {
		// Do nothing
	}

	public boolean warning (String text) {
		return setLog(LogType.WARNING, 0, text);
	}

}
