/*===========================================================================
  Copyright (C) 2009-2010 by the Okapi Framework contributors
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

import net.sf.okapi.common.IHelp;
import net.sf.okapi.common.ui.UIUtil;

/**
 * Default implementation of the {@link IHelp} interface.
 */
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

	@Override
	public void showWiki (String topic) {
		//Util.openWikiTopic(topic);
		// Resolve spaces
		topic = topic.replace(' ', '_');
		//TODO: get the base URL from a properties file
		UIUtil.start(String.format("http://www.opentag.com/okapi/wiki/index.php?title=%s", topic));
	}
	
	@Override
	public void showTopic (Object object,
		String filename)
	{
		showTopic(object, filename, null);
	}
	
	@Override
	public void showTopic (Object object,
		String filename,
		String query)
	{
		String path = "";
		if ( object != null ) {
			// Compute the sub-directories if needed
			// Get the package name
			path = object.getClass().getPackage().getName();
			// Remove the Okapi root
			path = path.replace(PKGROOT, ""); //$NON-NLS-1$
			// Replace the dots by the directories separators
			path = path.replace(".", "/"); //$NON-NLS-1$
		}
		
		// Now set the computed full path
		path = root + path + File.separator + filename + ".html"; //$NON-NLS-1$
		// Check if we need to add the file protocol
		if ( path.indexOf("://") == -1 ) path = "file://"+path;
		// Add the query if needed
		if ( query != null ) path += ("?" + query); //$NON-NLS-1$
		//Util.openURL(new URL(path).toString());
		UIUtil.start(path);
	}

}
