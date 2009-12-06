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

	private List<ScoreInfo> list;
	
	/**
	 * Creates a new ScoresAnnotation object.
	 */
	public ScoresAnnotation () {
		list = new ArrayList<ScoreInfo>();
	}
	
	/**
	 * Adds a new score to the list.
	 * @param value The score to add.
	 */
	public void add (int value,
		String origin)
	{
		list.add(new ScoreInfo(value, origin));
	}
	
	/**
	 * Clears the list of all scores.
	 */
	public void clear () {
		list.clear();
	}

	/**
	 * Gets the score information for a given index.
	 * @param index the index of the score to query.
	 * @return the score information for the score to query, or null
	 * if index is out of bounds.
	 */
	public ScoreInfo get (int index) {
		if (( index < 0 ) || ( index >= list.size() )) return null;
		return list.get(index);
	}
	
	/**
	 * Gets the score for a given index.
	 * @param index the index of the score to query.
	 * @return the score for the given index, or 0 if index is out of bounds.
	 */
	public int getScore (int index) {
		if (( index < 0 ) || ( index >= list.size() )) return 0;
		return list.get(index).score;
	}
	
	/**
	 * Gets the origin of a score for a given index. 
	 * @param index the index of the score to query.
	 * @return the origin for the given index, or null if there is no origin defined or
	 * index is out of bounds.
	 */
	public String getOrigin (int index) {
		if (( index < 0 ) || ( index >= list.size() )) return null;
		return list.get(index).origin;
	}

	/**
	 * Gets the list of scores.
	 * @return the list of scores.
	 */
	public List<ScoreInfo> getList () {
		return list;
	}

}
