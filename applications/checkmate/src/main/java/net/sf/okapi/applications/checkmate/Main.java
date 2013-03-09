/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.applications.checkmate;

import java.io.File;
import java.net.URLDecoder;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.DefaultFilters;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.ui.BaseHelp;
import net.sf.okapi.lib.ui.verification.QualityCheckEditor;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Main {

	public static void main (String args[]) {
		Display dispMain = null;
		try {
			// Compute the path of the help file
			File file = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getFile());
	    	String helpRoot = URLDecoder.decode(file.getAbsolutePath(),"utf-8"); //$NON-NLS-1$
	    	// Remove the JAR file if running an installed version
	    	if ( helpRoot.endsWith(".jar") ) helpRoot = Util.getDirectoryName(helpRoot); //$NON-NLS-1$
	    	// Remove the application folder in all cases
	    	helpRoot = Util.getDirectoryName(helpRoot);
	    	BaseHelp help = new BaseHelp(helpRoot + File.separator + "help"); //$NON-NLS-1$
	    	
			// Create the main shell
			dispMain = new Display();
			Shell shlMain = new Shell(dispMain);
			shlMain.setSize(700, 600);
			
			// Create the editor
			QualityCheckEditor editor = new QualityCheckEditor();
			// Create and fill the configuration mapper
			IFilterConfigurationMapper fcMapper = new FilterConfigurationMapper();
			DefaultFilters.setMappings(fcMapper, false, true);
			// Initialize the editor
			editor.initialize(shlMain, false, help, fcMapper, null);
			// start the application
			if ( args.length > 0 ) editor.showDialog(args[0], true);
			else editor.showDialog(null, false);
		}
		catch ( Throwable e ) {
			e.printStackTrace();
		}
		finally {
			if ( dispMain != null ) dispMain.dispose();
		}
    }

}
