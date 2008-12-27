/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
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
============================================================================*/

package net.sf.okapi.applications.ratel;

import java.io.File;
import java.net.URLDecoder;

import net.sf.okapi.common.Util;
import net.sf.okapi.lib.ui.segmentation.SRXEditor;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Main {

	public static void main (String args[])
	{
		Display dispMain = null;
		try {
			// Compute the path of the help file
			File file = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getFile());
	    	String helpPath = URLDecoder.decode(file.getAbsolutePath(),"utf-8"); //$NON-NLS-1$
	    	// Remove the JAR file if running an installed version
	    	if ( helpPath.endsWith(".jar") ) helpPath = Util.getDirectoryName(helpPath); //$NON-NLS-1$
	    	// Remove the application folder in all cases
	    	helpPath = Util.getDirectoryName(helpPath);
	    	helpPath += File.separator + "help" + File.separator + "SRXEditor.html";  //$NON-NLS-2$
			
			// Start the application
			dispMain = new Display();
			Shell shlMain = new Shell(dispMain);
			SRXEditor editor = new SRXEditor(shlMain, false, helpPath);
			editor.showDialog(null);
		}
		catch ( Throwable e ) {
			e.printStackTrace();
		}
		finally {
			if ( dispMain != null ) dispMain.dispose();
		}
    }

}
