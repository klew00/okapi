/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

import net.sf.okapi.common.IResource;
import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.annotation.Annotations;
import net.sf.okapi.common.annotation.IAnnotation;

/**
 * Resource associated with the filter events END_DOCUMENT,
 * END_SUBDOCUMENT, and END_GROUP.
 */
public class Ending implements IResource {

	protected String id;
	protected ISkeleton skeleton;
	protected Annotations annotations;

	/**
	 * Creates a new Ending object.
	 * @param id The ID of this resource (It should NOT be the same ID as the one set of 
	 * the corresponding starting resource: each resource has a ID are unique).
	 */
	public Ending (String id) {
		annotations = new Annotations();
		this.id = id;
	}

	public String getId () {
		return id;
	}
	
	public void setId (String id) {
		this.id = id;
	}

	public ISkeleton getSkeleton () {
		return skeleton;
	}
	
	public void setSkeleton (ISkeleton skeleton) {
		this.skeleton = skeleton;
		if (skeleton != null) skeleton.setParent(this);
	}

	public <A extends IAnnotation> A getAnnotation (Class<A> annotationType) {
		if ( annotations == null ) return null;
		return annotationType.cast(annotations.get(annotationType) );
	}
	
	public void setAnnotation (IAnnotation annotation) {
		annotations.set(annotation);
	}

	public Annotations getAnnotations() {
		return (annotations == null) ? new Annotations() : annotations;
	}

}
