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

package net.sf.okapi.filters;

public interface IExtractionItem extends ISegment {

	/**
	 * Indicates whether an object is segmented or not. Not that an item may
	 * have been segmented and still be made on a single segment.
	 * @return True if the object has been segmented, false otherwise.
	 */
	boolean isSegmented ();

	/**
	 * Gets the number of segments in this object. The minimum value is 1.
	 * @return The number of segments in the object.
	 */
	int getSegmentCount ();

	/**
	 * Gets the list of all segments in the object.
	 * @return An array of ISegment objects.
	 */
	ISegment[] getSegments ();

	/**
	 * Adds an existing segment to the object.
	 * If the object was not segmented yet, it becomes segmented,
	 * and the content of the original object becomes the first segment,
	 * while this added segment becomes the second segment.
	 */
	void addSegment (ISegment newSegment);
	
	/**
	 * Makes a single segment from all the segments in the object.
	 */
	void makeOneSegment ();
}
