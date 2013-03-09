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

/**
 * Stores a class name and its class-loader, for dynamic loading.
 */
public class ClassInfo {

	/**
	 * Full name of the class.
	 */
	public String name;
	/**
	 * Class loader for this class, or null if the default
	 * class loader should be used.
	 */
	public ClassLoader loader;

	/**
	 * Creates a new ClassInfo object for a given class name and loader.
	 * @param name the full name of the class.
	 * @param loader the class loader for this class, or null to use the
	 * default class loader.
	 */
	public ClassInfo (String name,
		ClassLoader loader)
	{
		this.name = name;
		this.loader = loader;
	}
	
	/**
	 * Convenience method to create a new ClassInfo object for a given
	 * class name. This is the same as calling {@link #ClassInfo(String, ClassLoader)}
	 * with the class loader set to null.
	 * @param name the full name of the class.
	 */
	public ClassInfo (String name) {
		this.name = name;
	}

}
