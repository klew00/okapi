/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel                                          */
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

package net.sf.okapi.common.ui;

import java.io.IOException;

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
	 * Executes a given program with a given parameter.
	 * @param program The program to execute.
	 * @param parameter The parameter of the program.
	 */
	static public void execute (String program,
		String parameter)
	{
		try {
			Runtime rt = Runtime.getRuntime();
			String[] command = new String[2];
			command[0] = program;
			command[1] = parameter;
			rt.exec(command);
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
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
