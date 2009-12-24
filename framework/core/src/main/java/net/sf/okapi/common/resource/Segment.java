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

package net.sf.okapi.common.resource;

/**
 * Convenience class to group together the text fragment of a segment
 * and its identifier.
 */
public class Segment {
	
	/**
	 * Identifier of this segment.
	 */
	public String id;
	/**
	 * Text fragment of this segment.
	 */
	public TextFragment text;
	
	/**
	 * Creates an empty segment object.
	 */
	public Segment () {
	}
	
	/**
	 * Creates a segment object with a given identifier and a given
	 * text fragment.
	 * @param id identifier for the new segment.
	 * @param text text fragment for the new segment.
	 */
	public Segment (String id,
		TextFragment text)
	{
		this.id = id;
		this.text = text;
	}

	/**
	 * Gets the {@link TextFragment} for this segment.
	 * @return the {@link TextFragment} for this segment.
	 */
	public TextFragment getContent () {
		return text;
	}
	
	/**
	 * Clones this segment.
	 * @return a copy of this segment. 
	 */
	@Override
	public Segment clone () {
		return new Segment(id, text.clone());
	}
	
	/**
	 * Gets the text representation of the text fragment of this segment.
	 * @return the text representation of this segment.
	 */
	@Override
	public String toString () {
		if ( text == null ) return null;
		return text.toString();
	}
	
}
