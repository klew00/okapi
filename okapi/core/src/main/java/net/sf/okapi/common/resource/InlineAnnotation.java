/*===========================================================================
  Copyright (C) 2009-2012 by the Okapi Framework contributors
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

package net.sf.okapi.common.resource;

import net.sf.okapi.common.annotation.IAnnotation;

/**
 * Base implementation of an annotation that can be used on inline codes.
 * Inline annotations must have a {@link #toString()} and {@link #fromString(String)} 
 * methods to write and read themselves to and from a string.
 * <p>This basic annotation has only a string data. Its usage depends of the type 
 * of the annotation.
 */
public class InlineAnnotation implements IAnnotation {

	protected String data;

	/**
	 * Creates an empty annotation object.
	 */
	public InlineAnnotation () {
	}
	
	/**
	 * Creates a new annotation object with some initial data.
	 * @param data The data to set.
	 */
	public InlineAnnotation (String data) {
		this.data = data;
	}
	
	/**
	 * Clones this annotation.
	 * @return A new InlineAnnotation object that is a copy of this one.
	 */
	@Override
	public InlineAnnotation clone () {
		InlineAnnotation newObj = new InlineAnnotation(this.data);
		return newObj;
	}
	
	/**
	 * Gets a storage string representation of the whole annotation that can
	 * be used for serialization.
	 * @return The storage string representation of this annotation.
	 */
	@Override
	public String toString () {
		// this annotation has just one string.
		return data;
	}
	
	/**
	 * Initializes this annotation from a storage string originally obtained
	 * from {@link #toString()}.
	 * @param storage The storage string to use for the initialization.
	 */
	public void fromString (String storage) {
		// This annotation has just one string.
		this.data = storage;
	}

	/**
	 * Gets the data for this annotation.
	 * @return The data of this annotation.
	 */
	public String getData () {
		return data;
	}
	
	/**
	 * Sets the data for this annotation.
	 * @param data The data to set.
	 */
	public void setData (String data) {
		this.data = data;
	}

}
