/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel (at ENLASO Corporation)                  */
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
/* Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA              */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.common.filters;

import java.util.List;

public interface IExtractionItem {

	/**
	 * Gets the resource name of the item. This corresponds to the resname attribute
	 * in XLIFF. Uniqueness of resname depends on each filter.
	 * @return The resource name of the item, or null if there is none.
	 */
	//TODO: Return null or empty on no-resname???
	String getResname ();
	
	/**
	 * Sets the resource name of the item.
	 * @param resname The new resource name to set.
	 */
	void setResname (String resname);
	
	/**
	 * Gets the resource type of the item. This corresponds to the restype attribute
	 * in XLIFF.
	 * @return The resource type of the item.
	 */
	String getRestype ();
	
	/**
	 * Sets the resource type of the item.
	 * @param restype The resource type to set.
	 */
	void setRestype (String restype);
	
	/**
	 * Gets the identifier of the item in this input. This value is unique within the current
	 * input. It may be sequential or not, it may change depending on the parameters.
	 * It must be the same for two identical input processed with the same parameters. 
	 * @return The identifier of the item.
	 */
	int getID ();
	
	/**
	 * Sets the identifier of the item.
	 * @param id The identifier value to set.
	 */
	void setID (int id);
	
	/**
	 * Indicates if the item is translatable. Some item may be part of the extraction scope
	 * but because of specific parameters set in the filter, may be seen as non-translatable.
	 * @return True if the content of the item is translatable, false otherwise.
	 */
	boolean isTranslatable ();
	
	/**
	 * Sets the flag that indicates if the item is translatable.
	 * @param isTranslatable The new value to set.
	 */
	void setIsTranslatable (boolean isTranslatable);
	
	/**
	 * Indicates if the item has a corresponding target item. The target item may or may not
	 * be a translation (it could be a copy of the source for example).
	 * @return True if there is a corresponding target item available, false otherwise.
	 */
	boolean hasTarget ();
	
	/**
	 * Sets the flag that indicates if the item has a corresponding target item.
	 * @param hasTarget The new value to set.
	 */
	void setHasTarget (boolean hasTarget);

	/**
	 * Indicates if the content of this item should have its white-spaces preserved.
	 * @return True if the formatting should be preserved, false otherwise.
	 */
	boolean preserveFormatting ();
	
	/**
	 * Sets the flag that indicates if the formatting of the item should be
	 * preserved.
	 * @param preserve The new value to set.
	 */
	void setPreserveFormatting (boolean preserve);
	
	/**
	 * Gets the unsegmented content of the item.
	 * @return The IContainer object with the unsegmented content.
	 */
	IContainer getContent();
	
	/**
	 * Sets the content of the item.
	 * @param data An IContainer object with the new content.
	 */
	void setContent (IContainer data);
	
	/**
	 * Gets the list of the segments for the item.
	 * @return A list of IContainer object corresponding to each segment.
	 */
	List<IContainer> getSegments();

	/**
	 * Gets the list of children items for this item.
	 * @return A list of IExtractionItem objects, one for each child item.
	 */
	List<IExtractionItem> getChildren();
	
	/**
	 * Adds a child item to this item.
	 * @param child The IExtractionItem object to add.
	 */
	void addChild (IExtractionItem child);

	/**
	 * Sets the property value object associated with a given property name.
	 * @param name The name of the property (case sensitive).
	 * @param value The new value to set.
	 */
	void setProperty (String name,
		Object value);
	
	/**
	 * Gets the value object associated with a given property name.
	 * @param name The name of the property (case sensitive).
	 * @return The current object associated with the given property name, this
	 * can be null. Null is also return if there is no property for the given name.
	 */
	Object getProperty (String name);
	
	/**
	 * Removes the list of properties associated with the object.
	 */
	void clearProperties ();
}
