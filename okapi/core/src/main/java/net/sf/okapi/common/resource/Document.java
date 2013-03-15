/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.okapi.common.IResource;
import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.annotation.Annotations;
import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.common.exceptions.OkapiNotImplementedException;

/**
 * UNUSED class.
 */
public class Document implements IResource, Iterable<IResource> {

	private Annotations annotations;
	private String id;
	private List<IResource> documentResources;
	
	public Document() {
		documentResources = new ArrayList<IResource>(100);
	}

	public void addResource(IResource resource) {
		documentResources.add(resource);
	}
	
	@Override
	public <A extends IAnnotation> A getAnnotation(Class<A> annotationType) {
		if (annotations == null)
			return null;
		return annotationType.cast(annotations.get(annotationType));
	}

	@Override
	public String getId() {
		return id;
	}
	
	/**
	 * Always throws an exception as there is never a skeleton associated to a RawDocument. 
	 * @return never returns.
	 * @throws OkapiNotImplementedException
	 */
	@Override
	public ISkeleton getSkeleton() {
		throw new OkapiNotImplementedException("The Document resource does not have skeketon");
	}

	@Override
	public void setAnnotation(IAnnotation annotation) {
		if (annotations == null) {
			annotations = new Annotations();
		}
		annotations.set(annotation);
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * This method has no effect as there is never a skeleton for a Document.
	 * @throws OkapiNotImplementedException
	 */
	@Override	
	public void setSkeleton(ISkeleton skeleton) {
		throw new OkapiNotImplementedException("Document has no skeleton");
	}

	@Override
	public Iterator<IResource> iterator() {
		return documentResources.iterator();
	}

	public Annotations getAnnotations() {
		return (annotations == null) ? new Annotations() : annotations;
	}	
}
