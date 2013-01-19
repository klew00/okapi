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

import net.sf.okapi.common.ISkeleton;

/**
 * Resource associated with the filter events DOCUMENT_PART,
 */
public class DocumentPart extends BaseReferenceable {

	/**
	 * Creates a new DocumentPart object.
	 * @param id The ID of this resource.
	 * @param isReferent Indicates if this resource is a referent (i.e. is referred to
	 * by another resource) or not.
	 */
	public DocumentPart (String id,
		boolean isReferent)
	{
		this.id = id;
		this.refCount = (isReferent ? 1 : 0);
	}

	/**
	 * Creates a new DocumentPart object.
	 * @param id The ID of this resource.
	 * @param isReferent Indicates if this resource is a referent (i.e. is referred to
	 * by another resource) or not.
	 * @param skeleton The skeleton associated with this resource.
	 */
	public DocumentPart (String id,
		boolean isReferent,
		ISkeleton skeleton)
	{
		this.id = id;
		this.refCount = (isReferent ? 1 : 0);
		setSkeleton(skeleton);
	}

	@Override
	public String toString() {
		return getSkeleton().toString();
	}	
}
