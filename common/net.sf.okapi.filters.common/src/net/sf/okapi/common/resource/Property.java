/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
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
============================================================================*/

package net.sf.okapi.common.resource;

import net.sf.okapi.common.annotation.Annotations;
import net.sf.okapi.common.annotation.IAnnotation;

public class Property {
	
	private final String name;
	private String value;
	private final boolean isReadOnly;
	protected Annotations annotations;

	public Property (String name, String value, boolean isReadOnly) {
		this.name = name;
		this.value = value;
		this.isReadOnly = isReadOnly;
	}
	
	@Override
	public String toString () {
		return value;
	}
	
	public Property clone () {
		Property prop = new Property(name, value, isReadOnly);
		//TODO: copy annotations?? prop.annotations = annotations.clone();
		return prop;
	}

	public String getName () {
		return name;
	}
	
	public String getValue () {
		return value;
	}
	
	public void setValue (String value) {
		this.value = value;
	}
	
	public boolean isReadOnly () {
		return isReadOnly;
	}
	
	@SuppressWarnings("unchecked")
	public <A> A getAnnotation (Class<? extends IAnnotation> type) {
		if ( annotations == null ) return null;
		return (A)annotations.get(type);
	}

	public void setAnnotation (IAnnotation annotation) {
		if ( annotations == null ) annotations = new Annotations();
		annotations.set(annotation);
	}

}
