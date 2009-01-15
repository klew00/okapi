/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.common.ui;

import java.io.File;

public class BaseHelp implements IHelp {

	private static final String PKGROOT = "net.sf.okapi.";
	private String root;
	
	/**
	 * Creates a new BaseHelp object with a root directory
	 * @param rootDirectory The root directory for the help files. 
	 */
	public BaseHelp (String rootDirectory) {
		root = rootDirectory;
		if ( !root.endsWith(File.separator) ) {
			root += File.separator;
		}
	}
	
	public void showTopic (Object object,
		String filenameWithoutExtension)
	{
		String path = "";
		if ( object != null ) {
			// Compute the sub-directories if needed
			// Get the package name
			path = object.getClass().getPackage().getName();
			// Remove the Okapi root
			path = path.replace(PKGROOT, ""); //$NON-NLS-1$
			// Replace the dots by the directories separators
			path = path.replace(".", File.separator); //$NON-NLS-1$
		}
		// Now set the computed full path
		path = root + path + File.separator + filenameWithoutExtension + ".html";; //$NON-NLS-1$
		// Call the help
		UIUtil.start(path);
	}

}
