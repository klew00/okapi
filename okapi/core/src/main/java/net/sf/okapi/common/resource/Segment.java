/*===========================================================================
  Copyright (C) 2009-2010 by the Okapi Framework contributors
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

import java.util.Collections;

import net.sf.okapi.common.annotation.Annotations;
import net.sf.okapi.common.annotation.IAnnotation;

/**
 * Implement a special content part that is a segment.
 * A segment is a {@link TextPart} with an identifier.
 */
public class Segment extends TextPart {
	
	public static final String REF_MARKER = "$segment$";
	
	// FIXME - why is this protected?
	protected Annotations annotations;	

	/**
	 * Identifier of this segment.
	 */
	// FIXME - why is this public?
	public String id;
	
	/**
	 * Creates an empty Segment object with a null identifier.
	 */
	public Segment () {
		super(new TextFragment());
	}
	
	/**
	 * Creates an empty Segment object with a given identifier.
	 */
	public Segment (String id) {
		super(new TextFragment());
		this.id = id;
	}
	
	/**
	 * Creates a Segment object with a given identifier and a given
	 * text fragment.
	 * @param id identifier for the new segment (Can be null).
	 * @param text text fragment for the new segment.
	 */
	public Segment (String id,
		TextFragment text)
	{
		super(text);
		this.id = id;
	}

	@Override
	public Segment clone () {
		Segment newSeg = new Segment(id, text.clone());
		if (annotations != null)
			newSeg.annotations = annotations.clone();
		return newSeg;
	}
	
	@Override
	public boolean isSegment () {
		return true;
	}

	/**
	 * Gets the identifier for this segment.
	 * @return the identifier for this segment.
	 */
	public String getId () {
		return id;
	}
	
	/**
	 * Forces the id of this segment to a specific value.
	 * No check is made to validate this ID value. It is the caller's responsability
	 * to avoid duplicates, null value, and other wrong values.
	 * @param id the new value of the segment.
	 */
	public void forceId (String id) {
		this.id = id;
	}

	/**
	 * Gets the annotation object for a given class for this segment.
	 * @param annotationType the class of the annotation object to retrieve.
	 * @return the annotation for the given class for this segment. 
	 */
	public <A extends IAnnotation> A getAnnotation (Class<A> annotationType) {
		if ( annotations == null ) return null;
		return annotationType.cast(annotations.get(annotationType) );
	}

	/**
	 * Sets an annotation object for this segment.
	 * <p>If an annotation of the same type exists already it is overridden.
	 * @param annotation the annotation object to set.
	 */
	public void setAnnotation (IAnnotation annotation) {
		if ( annotations == null ) annotations = new Annotations();
		annotations.set(annotation);
	}

	/**
	 * Gets the iterable list for the annotations of this segment.
	 * @return the iterable list for the annotations of this segment.
	 */
	public Iterable<IAnnotation> getAnnotations () {
		if ( annotations == null ) {
			return Collections.emptyList();
		}
		return annotations;
	}
	
	public static String makeRefMarker(String segId) {
		return TextFragment.makeRefMarker(segId, REF_MARKER);
	}
}
