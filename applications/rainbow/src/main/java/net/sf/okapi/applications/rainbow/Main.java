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

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Main {

	public static void main (String args[]) {
		
		int exitCode = 0;
		Display dispMain = null;
		try {
			dispMain = new Display();
			Shell shlMain = new Shell(dispMain);

			if ( args.length < 2 ) {
				// Normal mode
				String projectFile = null;
				if ( args.length == 1 ) {
					projectFile = args[0];
				}
				MainForm mf = new MainForm(shlMain, projectFile);
				shlMain.open();
				mf.run();
			}
			else { // Command line mode
				CommandLine cmd = new CommandLine();
				exitCode = cmd.execute(shlMain, args);
			}
		}
		catch ( Throwable e ) {
			e.printStackTrace();
			exitCode = 1;
		}
		finally {
			if ( dispMain != null ) dispMain.dispose();
		}
		if ( exitCode > 0 ) {
			System.exit(exitCode);
		}
    }

}
