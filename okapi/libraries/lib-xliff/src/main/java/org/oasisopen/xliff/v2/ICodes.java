/*===========================================================================
Copyright (C) 2011 by the Okapi Framework contributors
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

package org.oasisopen.xliff.v2;

import java.io.Serializable;

/**
 * Provides the method to store and manipulate codes in a given content.
 */
public interface ICodes extends Serializable {

	/**
	 * Indicates if this list of codes has at least one code.
	 * @return true if this list of codes has at least one code.
	 */
	public boolean hasCode ();
	
	public boolean hasCodeWithOriginalData ();

	public int size ();

	/**
	 * Gets the data store associated with this list of codes.
	 * @return the data store associated with this object.
	 */
	public IDataStore getDataStore ();

	/**
	 * Gets the code at a given position.
	 * @param index the index of the code to retrieve (first code is at 0).
	 * @return the retrieved code. 
	 */
	public ICode get (int index);
	
	public ICode get (String id,
		InlineType type);

	public void add (ICode code);

}
