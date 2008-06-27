package net.sf.okapi.common.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.program.Program;

public class UIUtil {

	public static final int    PFTYPE_WIN        = 0;
	public static final int    PFTYPE_MAC        = 1;
	public static final int    PFTYPE_UNIX       = 2;

	/**
	 * Starts a program or a command.
	 * @param command Command or program to launch. This can also be a 
	 * URL (to open a browser), etc.
	 */
	static public void start (String command) {
		Program.launch(command); 
	}
	
	/**
	 * Gets the type of platform the application is running on.
	 * @return -1 if the type could not be detected. Otherwise one of the PFTYPE_* values.
	 */
	public static int getPlatformType () {
		if ( "win32".equals(SWT.getPlatform()) ) return PFTYPE_WIN;
		if ( "gtk".equals(SWT.getPlatform()) ) return PFTYPE_UNIX;
		if ( "carbon".equals(SWT.getPlatform()) ) return PFTYPE_MAC;
		if ( "motif".equals(SWT.getPlatform()) ) return PFTYPE_UNIX;
		return -1; // Unknown
	}
	
	
}
