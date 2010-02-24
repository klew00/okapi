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

package net.sf.okapi.common;

import java.security.InvalidParameterException;

/**
 * Provides a common way to generate sequential ID that are unique for a given root.
 * <p>Each value generated is made of two main parts separated by a '-':
 * <ol><li>The hexadecimal representation of the hash code of the root.
 * <li>The sequential identifier starting at 1, with a fixed prefix if one was provided.  
 */
public class IdGenerator {
	
	private long seq = 0;
	private String root;
	private String prefix;
	
	/**
	 * Creates a generator with a given root and no prefix.
	 * @param root
	 * 	the root to use (case-sensitive and cannot be null or empty)
	 */
	public IdGenerator (String root) {
		create(root, "");
	}

	/**
	 * Creates a generator with a given root and a given prefix.
	 * @param root
	 * 	the root to use (case-sensitive and cannot be null or empty)
	 * @param prefix
	 * 	the prefix to use (case-sensitive)
	 */
	public IdGenerator (String root,
		String prefix)
	{
		create(root, prefix);
	}

	/**
	 * Returns the same value as {@link #getLastId()}. 
	 */
	@Override
	public String toString () {
		return getLastId();
	}
	
	/**
	 * Creates a new identifier.
	 * @return
	 *  the new identifier.
	 */
	public String createId () {
		return root + "-" + prefix + Long.toString(++seq);
	}
	
	/**
	 * Gets the last identifier generated.
	 * This method allows you to get the last identifier that was returned by {@link #createId()}.
	 * @return
	 *  the last identifier generated.
	 * @throws
	 *  RuntimeException if the method {@link #createId()} has not been called at least once
	 *  before call this method. 
	 */
	public String getLastId () {
		if ( seq <= 0 ) {
			throw new RuntimeException("The method createId() has not been called yet.");
		}
		return root + "-" + prefix + Long.toString(seq);
	}

	private void create (String root,
		String prefix)
	{
		// Set the root part
		if ( Util.isEmpty(root) ) {
			throw new InvalidParameterException("The root argument must not be null or empty.");
		}
		// makeId() uses the String.hashCode which should be reproducible across VM and sessions
		this.root = Util.makeId(root);
	
		// Set the prefix part (empty is OK)
		if ( prefix == null ) this.prefix = "";
		else this.prefix = prefix;
	}
	
}
