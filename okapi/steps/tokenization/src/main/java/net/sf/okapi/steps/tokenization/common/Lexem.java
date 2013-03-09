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

package net.sf.okapi.steps.tokenization.common;

import net.sf.okapi.common.Range;
import net.sf.okapi.common.annotation.Annotations;
import net.sf.okapi.common.annotation.IAnnotation;

public class Lexem {

	/**
	 * Lexem ID. Unique within the producing lexer. Either hard-coded by the lexer, or 
	 * defined by one and only one of its external rules.
	 * Serializable. 
	 */
	private int id;
	
	/**
	 * Text of lexem.
	 */
	private String value;
	
	/**
	 * Range of text extracted as the given lexem.
	 */
	private Range range;
	
	/**
	 * ID of the lexer that extracted this lexem. Set by a lexers manager when processing the lexem. 
	 * !!! Non-serializable.
	 */
	private int lexerId;
	
	private Annotations annotations;
	
	/**
	 * True if the lexem is considered deleted.
	 */
	private boolean deleted;
	
	/**
	 * True if the lexem cannot be deleted.
	 */
	private boolean immutable;
	
	//public Lexem(int id, String value, Range range, int lexerId) {
	public Lexem(int id, String value, Range range) {
		
		super();
				
		this.id = id;
		this.value = value;
		this.range = range;
	}
	
	//public Lexem(int id, String value, int start, int end, int lexerId) {
	public Lexem(int id, String value, int start, int end) {
		
		this(id, value, new Range(start, end));
	}

	/**
	 * Gets lexem ID. 
	 * !!! Non-serializable.
	 */
	public int getId() {
		
		return id;
	}
	
	public String getValue() {
		return value;
	}

	public Range getRange() {
		
		return range;
	}

	/**
	 * Gets ID of the lexer that extracted this lexem. 
	 * !!! Non-serializable.
	 */
	public int getLexerId() {
		
		return lexerId;
	}

	public void setLexerId(int lexerId) {
		
		this.lexerId = lexerId;
	}
	
	public <A extends IAnnotation> A getAnnotation (Class<A> type) {
		
		if (annotations == null) return null;
		
		return annotations.get(type);
	}

	public void setAnnotation (IAnnotation annotation) {
		
		if (annotations == null) 
			annotations = new Annotations();
		
		annotations.set(annotation);
	}

	public <A extends IAnnotation> A removeAnnotation (Class<A> type) {
		
		if (annotations == null) return null;
		
		return annotations.remove(type);
	}
	
	@Override
	public String toString() {
		
		return String.format("%-20s%4d\t%4d, %4d\t%4d", 
				value, id, range.start, range.end, lexerId);
	}

	public boolean isDeleted() {
		
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		
		this.deleted = deleted;
	}

	public boolean isImmutable() {
		
		return immutable;
	}

	public void setImmutable(boolean immutable) {
		
		this.immutable = immutable;
	}

	public Annotations getAnnotations() {
		return annotations;
	}
}
