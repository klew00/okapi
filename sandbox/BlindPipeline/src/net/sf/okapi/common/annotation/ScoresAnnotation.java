/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.common.annotation;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.annotation.IAnnotation;

/**
 * Annotates an object with a list of scores. For example, use it to
 * indicate the TM leveraging score of each segment in a TextContainer. 
 */
public class ScoresAnnotation implements IAnnotation {

	private List<Integer> list;
	
	/**
	 * Creates a new ScoresAnnotation object.
	 */
	public ScoresAnnotation () {
		list = new ArrayList<Integer>();
	}
	
	/**
	 * Adds a new score to the list.
	 * @param value The score to add.
	 */
	public void add (int value) {
		list.add(value);
	}
	
	/**
	 * Clears the list of all scores.
	 */
	public void clear () {
		list.clear();
	}
	
	/**
	 * Gets the score for a given index.
	 * @param index The index of the score to query.
	 * @return The score for the given index.
	 */
	public int getScore (int index) {
		return list.get(index);
	}

	/**
	 * Gets the list of scores.
	 * @return the list of scores.
	 */
	public List<Integer> getList () {
		return list;
	}

}
