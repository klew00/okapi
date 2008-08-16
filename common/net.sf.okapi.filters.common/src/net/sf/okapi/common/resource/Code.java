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

import net.sf.okapi.common.resource.TextFragment.TagType;

public class Code {

	protected TagType   tagType;
	protected int       id;
	protected String    type;
	protected String    data;
	protected boolean   hasSubflow;


	public Code (TagType tagType, String type, String data) {
		id = -1;
		this.tagType = tagType;
		if ( type == null ) type = "null";
		this.type = type;
		this.data = data;
	}

	@Override
	public String toString () {
		return data;
	}

	/**
	 * Clone the code.
	 * @return A copy of the object.
	 */
	@Override
	public Code clone () {
		Code clone = new Code(tagType, type, data);
		clone.id = id;
		return clone;
	}
	
	/**
	 * Gets the tag type of the code.
	 * @return The tag type of the code.
	 */
	public TagType getTagType () {
		return tagType;
	}
	
	/**
	 * Sets the tag type for the code.
	 * @param value The new tag type to apply. The value must be one of the
	 * values of {@link TagType}.
	 */
	public void setTagType (TagType value) {
		tagType = value;
	}
	
	/**
	 * Gets the abstract type for the code. For example: "bold".
	 * @return The abstract type of the code.
	 */
	public String getType () {
		return type;
	}
	
	/**
	 * Sets the abstract type of the code. This member is used to match up
	 * together opening and closing codes.
	 * @param value The new abstract type of the code.
	 */
	public void setType (String value) {
		if ( value == null ) type = "null";
		else type = value;
	}
	
	/**
	 * Gets the raw data for the code. This does not build a string
	 * with sub-flows content.
	 * @return The raw data of the code.
	 */
	public String getData () {
		return data;
	}
	
	/**
	 * Sets the raw data for the code.
	 * @param value The new raw data of the code.
	 */
	public void setData (String value) {
		data = value;
	}
	
	/**
	 * Gets the ID of the code.
	 * @return The ID of the code.
	 */
	public int getID () {
		return id;
	}

	/**
	 * Sets the ID of the code. Be aware that IDs for in-line codes are
	 * generated automatically when used in {@link TextFragment} and overriding the
	 * values may result in codes with duplicate IDs.
	 * @param value The new ID value to be applied.
	 */
	public void setID (int value) {
		id = value;
	}

	/**
	 * Indicates whether the code has at least one sub-flow part. A sub-flow is a text
	 * unit contained within the code. For example: the text of the ALT attribute in the
	 * HTML IMG element: If the IMG tag is a code, the value of ALT is one of its
	 * sub-flows.
	 * @return True if the code has one sub-flow.
	 */
	public boolean hasSubflow () {
		return hasSubflow;
	}
	
	/**
	 * Sets the sub-flow indicator. 
	 * @param value The new value to apply.
	 */
	public void setHasSubflow (boolean value) {
		hasSubflow = value;
	}

}
