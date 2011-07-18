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

import java.util.List;

/**
 * Provides the methods to add and retrieve translation candidates for an object. 
 */
public interface IWithNotes {

	/**
	 * Adds a note to the list.
	 * @param note the note to add.
	 */
	public void addNote (INote note);
	
	/**
	 * Gets the list of notes for this object.
	 * @return the list of notes available.
	 * An empty list is returned if there is no note. Never returns null.
	 */
	public List<INote> getNotes ();

	/**
	 * Gets the number of notes for this object.
	 * @return the number of notes available.
	 */
	public int getNoteCount ();

}
