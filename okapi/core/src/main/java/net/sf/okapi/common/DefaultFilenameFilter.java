/*===========================================================================
  Copyright (C) 2008-2012 by the Okapi Framework contributors
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
import java.util.regex.Pattern;

/**
 * Implements a default filename filter that supports filtering by wild-cards like ("myFile*.*").
 */
public class DefaultFilenameFilter implements FilenameFilter {

	private Pattern pattern;
	
	/**
	 * Creates a new DefaultFilenameFilter object for a given pattern.
	 * @param pattern to filter on.
	 * @param caseSensitive true to use case-sensitive pattern, false otherwise.
	 * You can use ? to match any single character and * to match any multiple characters.
	 * The pattern is not case-sensitive ("test.*" and "TeSt.*" give the same results)
	 */
	public DefaultFilenameFilter (String pattern,
		boolean caseSensitive)
	{
		if ( pattern == null ) throw new NullPointerException("Mask of the filename cannot be null.");
		pattern = pattern.replace('.', '\b');
		pattern = pattern.replace("*", ".*");
		pattern = pattern.replace('?', '.');
		pattern = pattern.replace("\b", "\\.");
		if ( caseSensitive ) this.pattern = Pattern.compile(pattern);
		else this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
	}

	/**
	 * Creates a new DefaultFilenameFilter object with a given extension value.
	 * This constructor is for backward compatibility and is equivalent to calling
	 * <code>DefaultFilenameFilter("*"+extension, false)</code> 
	 * @param extension the extension to filter on.
	 */
	public DefaultFilenameFilter (String extension) {
		this("*"+extension, false);
	}
	
	/**
	 * Accept or reject a given filename.
	 * @return true if the filename is accepted. If the value is null, the method returns false. 
	 */
	public boolean accept (File directory,
		String fileName)
	{
		if ( fileName == null ) return false;
		return pattern.matcher(fileName).matches();
	}

}

