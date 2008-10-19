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

public interface ITranslatable extends IContainable {

	/**
	 * Indicates if the content of the resource is translatable.
	 * @return True if the content is translatable, false otherwise.
	 */
	public boolean isTranslatable ();
	
	/**
	 * Sets the flag indicating if the content of the resource is translatable.
	 * @param value The new value to set.
	 */
	public void setIsTranslatable (boolean value);
	
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

	/**
	 * Indicates if the resource has at least one child.
	 * @return True if the resource has one child or more, false if it has none.
	 */
	public boolean hasChild ();
	
}
