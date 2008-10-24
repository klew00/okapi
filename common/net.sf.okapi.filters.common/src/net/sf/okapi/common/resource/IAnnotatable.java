/*===========================================================================*/
/* Copyright (C) 2008 Asgeir Frimannsson, Jim Hargrave, Yves Savourel        */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.common.resource;

import java.util.Hashtable;

public interface IAnnotatable extends IResource {
	
	/**
	 * Gets the name of the resource. This value is the same as the resname
	 * attribute in XLIFF.
	 * @return The name of the resource.
	 */
	public String getName ();
	
	/**
	 * Sets the name of the resource. This value is the same as the resname
	 * attribute in XLIFF.
	 * @param value The new name to set.
	 */
	public void setName (String value);
	
	/**
	 * Gets the type of the resource. This value is the same as the restype
	 * attribute in XLIFF.
	 * @return The type of the resource.
	 */
	public String getType ();
	
	/**
	 * Sets the typee of the resource. This value is the same as the restype
	 * attribute in XLIFF.
	 * @param value The new type to set.
	 */
	public void setType (String value);
	
	/**
	 * Indicates if the white spaces must be preserved in the content of the
	 * resource.
	 * @return True if the white spaces must be preserved.
	 */
	public boolean preserveWhitespaces ();
	
	/**
	 * Sets the flag indicating if the white spaces must be preserved in the
	 * content of the resource.
	 * @param value The new value of the flag.
	 */
	public void setPreserveWhitespaces (boolean value);

	/**
	 * Gets the read-only property associated to a given name.
	 * @param name The name of the property.
	 * @return The string for the property of the given name, or null if none
	 * exists.
	 */
	public String getProperty (String name);
	
	/**
	 * Sets the value of the read-only property associated with a given name. Note that 
	 * 'read-only' properties are properties for which the filters do not provide a
	 * way to modify. Localizable properties are stored in locale-specific objects.
	 * @param name The name of the property.
	 * @param value The new value to apply.
	 */
	public void setProperty (String name, String value);
	
	/**
	 * Gets the table of all read-only properties for the resource.
	 * @return the list of all read-only properties for the resource.
	 */
	public Hashtable<String, String> getProperties ();
	
	/**
	 * Gets the extension object associated to a given name.
	 * @param name the name of the extension.
	 * @return the extension object for the given name, or null if none
	 * exists.
	 */
	public IExtension getExtension (String name);
	
	/**
	 * Sets the extension object associated to a given name.
	 * @param name The name of the extension.
	 * @param value The new extension object to apply.
	 */
	public void setExtension (String name, IExtension value);

	/**
	 * Gets a list of all extensions for the resource.
	 * @return The list of all the extensions for the resource.
	 */
	public Hashtable<String, IExtension> getExtensions ();
}
