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

public class SkeletonUnit implements IContainable {

	private StringBuilder    data;
	protected String         id;
	private long             offset;
	private int              length;


	/**
	 * Creates an empty SkeletonUnit object.
	 */
	public SkeletonUnit () {
		offset = -1;
	}
	
	/**
	 * Creates a SkeletonUnit object with given ID and data.
	 * @param id the ID to use.
	 * @param data the data to use.
	 */
	public SkeletonUnit (String id,
		String data)
	{
		this.id = id;
		setData(data);
	}
	
	/**
	 * Creates a SkeletonUnit with ID and offset information.
	 * @param id The ID to use.
	 * @param offset The offest value to use.
	 * @param length The length value to use.
	 */
	public SkeletonUnit (String id,
		int offset,
		int length)
	{
		this.id = id;
		setData(offset, length);
	}
	
	@Override
	public String toString () {
		if ( isEmpty() ) return "";
		else if ( isOffsetBased() ) return "[offset-based]"; //TODO: Modify for real output, this is test only
		else if ( data == null ) return "";
		else return data.toString();
	}
	
	public String getID () {
		return id;
	}

	public boolean isEmpty () {
		if (  offset != -1 ) return (length==0);
		return (( data == null ) || ( data.length() == 0 ));
	}

	public void setID (String value) {
		id = value;
	}

	/**
	 * Sets the skeleton's data as a string. This method overrides any existing
	 * data. Use {@link #appendData(String)} or {@link #appendData(StringBuilder)}
	 * to add data to the existing one.
	 * @param text The data to set.
	 */
	public void setData (String text) {
		data = new StringBuilder(text);
		offset = -1;
	}

	/**
	 * Sets the skeleton data in the form of an offset and a length. This method
	 * overrides any existing data.
	 * @param offset The offset position.
	 * @param length The length starting from the offset.
	 */
	public void setData (long offset,
		int length)
	{
		if (( offset < 0 ) || ( length < 0 ))
			throw new RuntimeException("Offset and length must be positive values.");
		this.offset = offset;
		this.length = length;
		data = null;
	}

	/**
	 * Appends skeleton data to the object. If previous data where set using
	 * {@link #setData(long, int)} before, this new data overrides the offset
	 * information. In short: you cannot have both direct data and offset at the
	 * same time. 
	 * @param text The data to set.
	 */
	public void appendData (String text) {
		if ( data == null ) data = new StringBuilder(text);
		else data.append(text);
		offset = -1;
	}
	
	/**
	 * Appends skeleton data to the object. If previous data where set using
	 * {@link #setData(long, int)} before, this new data overrides the offset
	 * information. In short: you cannot have both direct data and offset at the
	 * same time. 
	 * @param text The data to set.
	 */
	public void appendData (StringBuilder text) {
		if ( data == null ) data = new StringBuilder(text);
		else data.append(text);
		offset = -1;
	}

	/**
	 * Indicates if the current skeleton's data are offset-based or string.
	 * @return True if the data are offset-based, false if there is no offset
	 * information set.
	 */
	public boolean isOffsetBased () {
		return (offset != -1);
	}
	
	/**
	 * Adds a given length to the current length of the offset-based skeleton.
	 * @param length The length to add to the current one.
	 */
	public void addToLength (int length) {
		this.length += length;
	}
}
