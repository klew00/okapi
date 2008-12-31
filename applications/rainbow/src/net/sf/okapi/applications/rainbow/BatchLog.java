package net.sf.okapi.applications.rainbow;

import net.sf.okapi.applications.rainbow.lib.ILog;
import net.sf.okapi.applications.rainbow.lib.LogType;

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
		System.out.println("");
		return false;
	}

	public void save (String path) {
		// Do nothing
	}

	public void setCallerData (long data) {
		// Do nothing
	}

	public void setHelp (String path) {
		// Do nothing
	}

	public boolean setLog (int p_nType,
		int p_nValue,
		String p_sValue)
	{
		switch ( p_nType ) {
		case LogType.ERROR:
			System.out.println("ERROR: "+p_sValue);
			errCount++;
			break;
		case LogType.WARNING:
			System.out.println("WARNING: "+p_sValue);
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
