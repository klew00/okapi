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

import net.sf.okapi.common.Range;

/**
 * Span of text and its annotation data.
 */
public class AnnotatedSpan {

	/**
	 * Type of annotation.
	 */
	public String type;
	
	/**
	 * The annotation itself (can be null).
	 */
	public InlineAnnotation annotation;

	/**
	 * Copy of the fragment of text to which the annotation is applied.
	 */
	public TextFragment span;
	
	/**
	 * The start and end positions of the span of text in the original
	 * coded text.
	 */
	public Range range;
	
	/**
	 * Creates a new AnnotatedSpan object with a give type of annotation,
	 * its annotation and its fragment of text. 
	 * @param type the type of the annotation for this span of text.
	 * @param annotation the annotation associated with this span of text.
	 * @param span the span of text.
	 * @param start the start position of the span of text in the original
	 * coded text.
	 * @param end the end position of the span of text in the original
	 * coded text.
	 */
	public AnnotatedSpan (String type,
		InlineAnnotation annotation,
		TextFragment span,
		int start,
		int end)
	{
		this.type = type;
		this.annotation = annotation;
		this.span = span;
		range = new Range(start, end);
	}

}
