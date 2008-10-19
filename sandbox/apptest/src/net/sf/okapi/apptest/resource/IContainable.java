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

package net.sf.okapi.apptest.resource;

import net.sf.okapi.apptest.common.IResource;

public interface IContainable extends IResource {

	/**
	 * Gets the extraction ID of the resource.
	 * @return The ID of the resource.
	 */
	public String getID ();
	
	/**
	 * Sets the extraction ID of the resource.
	 * @param value The new ID to set.
	 */
	public void setID (String value);
	
	/**
	 * Indicates if the resource has any content.
	 * @return True if there is non-empty content, false otherwise.
	 */
	public boolean isEmpty ();

	/**
	 * Gets the current parent of the object.
	 * @return The parent of the resource or null if it has no parent.
	 */
	public ITranslatable getParent ();
	
	/**
	 * Sets the parent for the resource.
	 * @param value The new parent of the resource.
	 */
	public void setParent (ITranslatable value);
}
