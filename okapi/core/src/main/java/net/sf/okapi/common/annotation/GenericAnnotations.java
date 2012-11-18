/*===========================================================================
  Copyright (C) 2012 by the Okapi Framework contributors
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
import java.util.Collections;
import java.util.List;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.InlineAnnotation;

/**
 * Provides access to a list of {@link GenericAnnotation}.
 * <p>This annotation can be used inline as well as on structural objects.
 */
public class GenericAnnotations extends InlineAnnotation {

	private static final String ANNOTATION_SEPARATOR = "\u001d";

	List<GenericAnnotation> list;
	
	/**
	 * Creates an empty annotation set.
	 */
	public GenericAnnotations () {
		// Empty annotation list
	}
	
	/**
	 * Creates an annotation set initialized with a storage string
	 * created with {@link #toString()}.
	 * @param storage the storage string to use.
	 */
	public GenericAnnotations (String storage) {
		fromString(storage);
	}
	
	@Override
	public GenericAnnotations clone () {
		return new GenericAnnotations(this.toString());
	}
	
	/**
	 * Gets the number of annotations in this annotation set.
	 * @return the number of annotations in this annotation set.
	 */
	public int size () {
		if ( list == null ) return 0;
		else return list.size();
	}
	
	/**
	 * Gets a list of all the annotations of a given type.
	 * <p>The returned list is a new lits but its items are the same as the items in the original list.
	 * @param type the type of annotation to look for.
	 * @return the list of all annotations of the given type, the list may be empty but is never null.
	 */
	public List<GenericAnnotation> getAnnotations (String type) {
		if ( Util.isEmpty(list) ) return Collections.emptyList();
		List<GenericAnnotation> res = new ArrayList<GenericAnnotation>(); 
		for ( GenericAnnotation ann : list ) {
			if ( ann.getType().equals(type) ) {
				res.add(ann);
			}
		}
		return res;
	}
	
	/**
	 * Indicates if there is at least one annotation of a given type.
	 * @param type the type of annotation to look for.
	 * @return true if there is at least one annotation of a given type, false otherwise.
	 */
	public boolean hasAnnotation (String type) {
		if ( Util.isEmpty(list) ) return false;
		for ( GenericAnnotation ann : list ) {
			if ( ann.getType().equals(type) ) return true;
		}
		return false;
	}
	
	/**
	 * Removes all annotations from this list.
	 */
	public void clear () {
		if ( list != null ) list.clear();
	}
	
	/**
	 * Removes a given annotation from this list.
	 * @param annotation the annotation to remove.
	 */
	public void remove (GenericAnnotation annotation) {
		list.remove(annotation);
	}

	/**
	 * Creates an new annotation and add it to this list.
	 * @param type the type of annotation to create.
	 * @return the new annotation.
	 */
	public GenericAnnotation add (String type) {
		GenericAnnotation ann = new GenericAnnotation(type);
		if ( list == null ) list = new ArrayList<GenericAnnotation>();
		list.add(ann);
		return ann;
	}
	
	@Override
	public String toString () {
		if ( Util.isEmpty(list) ) return "";
		// Else: store the annotations
		StringBuilder sb = new StringBuilder();
		for ( GenericAnnotation ann : list ) {
			if ( sb.length() > 0 ) sb.append(ANNOTATION_SEPARATOR); 
			sb.append(ann.toString());
		}
		return sb.toString();
	}
	
	@Override
	public void fromString (String storage) {
		String[] parts = storage.split(ANNOTATION_SEPARATOR, 0);
		for ( String data : parts ) {
			GenericAnnotation ann = add("z");
			ann.fromString(data);
		}
	}

}
