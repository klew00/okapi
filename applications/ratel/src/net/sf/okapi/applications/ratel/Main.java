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

package net.sf.okapi.applications.ratel;

import java.io.File;
import java.net.URLDecoder;

import net.sf.okapi.common.Util;
import net.sf.okapi.lib.ui.segmentation.SRXEditor;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Main {

	public static void main (String args[])
	{
		Display dispMain = null;
		Image img16 = null;
		Image img32 = null;
		try {
			// Compute the path of the help file
			File file = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getFile());
	    	String helpRoot = URLDecoder.decode(file.getAbsolutePath(),"utf-8"); //$NON-NLS-1$
	    	// Remove the JAR file if running an installed version
	    	if ( helpRoot.endsWith(".jar") ) helpRoot = Util.getDirectoryName(helpRoot); //$NON-NLS-1$
	    	// Remove the application folder in all cases
	    	helpRoot = Util.getDirectoryName(helpRoot);
	    	helpRoot += File.separator + "help" + File.separator; //$NON-NLS-1$
			
			// Start the application
			dispMain = new Display();
			Shell shlMain = new Shell(dispMain);
			img16 = new Image(dispMain, Main.class.getResourceAsStream("images/ratel16.png")); //$NON-NLS-1$
			img32 = new Image(dispMain, Main.class.getResourceAsStream("images/ratel32.png")); //$NON-NLS-1$
			shlMain.setImages(new Image[]{img16, img32});
			SRXEditor editor = new SRXEditor(shlMain, false, helpRoot);
			editor.showDialog(null);
		}
		catch ( Throwable e ) {
			e.printStackTrace();
		}
		finally {
			if ( img16 != null ) img16.dispose();
			if ( img32 != null ) img32.dispose();
			if ( dispMain != null ) dispMain.dispose();
		}
    }

}
