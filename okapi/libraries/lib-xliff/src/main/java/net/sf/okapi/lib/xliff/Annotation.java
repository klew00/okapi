/*===========================================================================
  Copyright (C) 2011-2012 by the Okapi Framework contributors
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

package net.sf.okapi.lib.xliff;

import org.oasisopen.xliff.v2.IAnnotation;
import org.oasisopen.xliff.v2.IMarker;
import org.oasisopen.xliff.v2.InlineType;

public class Annotation implements IAnnotation {

	private static final long serialVersionUID = 0100L;

	private InlineType inlineType;
	private String internalId;
	private String id;
	private String type;
	private String value;
	private String ref;
	private boolean translate = true;

	public Annotation (String id,
		boolean opening,
		String type)
	{
		this.id = id;
		inlineType = (opening ? InlineType.OPENING : InlineType.CLOSING);
		setType(type);
	}

	public Annotation (String id,
		boolean opening,
		String type,
		String value)
	{
		this.id = id;
		inlineType = (opening ? InlineType.OPENING : InlineType.CLOSING);
		setType(type);
		setValue(value);
	}

	public String getType () {
		return type;
	}
	
	public void setType (String type) {
		if ( type == null ) {
			throw new RuntimeException("Annotation type cannot be null.");
		}
		this.type = type;
	}
	
	public String getId () {
		return id;
	}
	
	public void setId (String id) {
		this.id = id;
		this.internalId = id; // For now
//		this.internalId = Util.toInternalId(id, inlineType);
	}

	@Override
	public String getInternalId () {
		return internalId;
	}

	@Override
	public InlineType getInlineType () {
		return inlineType;
	}

	@Override
	public void setInlineType (InlineType inlineType) {
		this.inlineType = inlineType;
	}

	public String getRef () {
		return ref;
	}
	
	public void setRef (String ref) {
		this.ref = ref;
	}

	public String getValue () {
		return value;
	}
	
	public void setValue (String value) {
		this.value = value;
	}

	@Override
	public boolean getTranslate () {
		return translate;
	}

	@Override
	public void setTranslate (boolean translate) {
		this.translate = translate;
	}

	public boolean equals (IMarker marker) {
		if ( marker == null ) {
			throw new NullPointerException("The parameter of Code.equals() must not be null.");
		}
		if ( this == marker ) return true;
		if ( !(marker instanceof IAnnotation) ) return false;
		
		IAnnotation anno = (IAnnotation)marker;
		if ( Util.compareAllowingNull(type, anno.getType()) != 0 ) return false;
		if ( Util.compareAllowingNull(id, anno.getId()) != 0 ) return false;
		if ( Util.compareAllowingNull(ref, anno.getRef()) != 0 ) return false;
		if ( Util.compareAllowingNull(value, anno.getValue()) != 0 ) return false;
		if ( Util.compareAllowingNull(internalId, anno.getInternalId()) != 0 ) return false;
		return (translate == anno.getTranslate());
	}

	@Override
	public boolean isAnnotation () {
		return true;
	}

}
