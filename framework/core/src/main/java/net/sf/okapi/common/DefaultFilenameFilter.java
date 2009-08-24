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
============================================================================*/

package net.sf.okapi.common;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Implements a default filename filter that supports filtering by extensions
 * and an optional by prefix of the filename.
 */
public class DefaultFilenameFilter implements FilenameFilter {

	private String extension;
	private String prefix;
	
	/**
	 * Creates a new DefaultFilenameFilter object with a given extension value.
	 * @param extension the extension to filter on.
	 */
	public DefaultFilenameFilter (String extension) {
		this.extension = extension;
	}

	/**
	 * Creates a new DefaultFilenameFilter object with given extension and
	 * prefix values. Both must be true for the file to be accepted by the filter.
	 * @param prefix the prefix to filter on
	 * @param extension the extension to filter on.
	 */
	public DefaultFilenameFilter (String prefix,
		String extension)
	{
		this.prefix = prefix;
		this.extension = extension;
	}

	/**
	 * Accept or reject a given filename.
	 * @return true if the filename is accepted. 
	 */
	public boolean accept (File directory,
		String fileName)
	{
		boolean bOK = true;
		if ( fileName != null ) {
			if ( extension != null ) {
				bOK &= extension.equalsIgnoreCase(Util.getExtension(fileName));
			}
			if ( prefix != null ) {
				bOK &= fileName.startsWith(prefix);
			}
		}
		return bOK;
	}

}

