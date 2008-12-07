/*===========================================================================*/
/* Copyright (C) 2008 by the Okapi Framework contributors                    */
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
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.common.resource;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import net.sf.okapi.common.annotation.Annotations;
import net.sf.okapi.common.annotation.IAnnotation;

public class TextContainer extends TextFragment {

	protected Hashtable<String, Property> properties;
	protected Annotations annotations;
	protected TextFragment masterSegment;
	protected ArrayList<TextFragment> segments;
	
	public TextContainer () {
		super();
		annotations = new Annotations();
	}

	public TextContainer (String text) {
		super(text);
		annotations = new Annotations();
	}
	
	@Override
	public String toString () {
		return text.toString();
	}
	
	@Override
	public TextContainer clone () {
		TextContainer tc = new TextContainer();
		// Clone the content
		tc.setContent(super.clone());
		// Clone the properties
		if ( properties != null ) {
			tc.properties = new Hashtable<String, Property>();
			for ( Property prop : properties.values() ) {
				tc.properties.put(prop.getName(), prop.clone()); 
			}
		}
		//TODO: Clone the annotations ???
		//TODO: Clone the segments
		return tc;
	}

	public TextFragment getContent () {
		return this;
	}
	
	public void setContent (TextFragment content) {
		text = new StringBuilder();
		insert(-1, content);
		// We don't change the current annotations
		// But we reset the segments
		if ( masterSegment != null ) {
			masterSegment = null;
			segments.clear();
		}
	}

	public boolean hasProperty (String name) {
		return (getProperty(name) != null);
	}
	
	public Property getProperty (String name) {
		if ( properties == null ) return null;
		return properties.get(name);
	}

	public Property setProperty (Property property) {
		if ( properties == null ) properties = new Hashtable<String, Property>();
		properties.put(property.getName(), property);
		return property;
	}
	
	public Set<String> getPropertyNames () {
		if ( properties == null ) properties = new Hashtable<String, Property>();
		return properties.keySet();
	}

	@SuppressWarnings("unchecked")
	public <A> A getAnnotation (Class<? extends IAnnotation> type) {
		return (A)annotations.get(type);
	}

	public void setAnnotation (IAnnotation annotation) {
		annotations.set(annotation);
	}

	/**
	 * Indicates if this TextContainer is segmented.
	 * @return True if the this TextContainer is segmented.
	 */
	public boolean isSegmented () {
		return (masterSegment != null);
	}
	
	/**
	 * Gets the list of all current segments, or null if this object is not segmented.
	 * @return The list of all current segments, or null. 
	 */
	public List<TextFragment> getSegments () {
		return segments;
	}
	
}
