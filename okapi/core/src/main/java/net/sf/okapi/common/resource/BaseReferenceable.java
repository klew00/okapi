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

/**
 * Implements a nameable resource that can be a referent. 
 */
public class BaseReferenceable extends BaseNameable implements IReferenceable {

	protected int refCount;
	protected String parentId;
	
	public boolean isReferent () {
		return (refCount > 0);
	}

	public void setIsReferent (boolean value) {
		refCount = (value ? 1 : 0);
	}

	public int getReferenceCount () {
		return refCount;
	}
	
	public void setReferenceCount (int value) {
		refCount = value;
	}
	
	/**
	 * Gets the identifier of the parent resource of this resource.
	 * @return the identifier of this resource's parent, or null if there is none.
	 */
	public String getParentId () {
		return parentId;
	}
	
	/**
	 * Sets the identifier of the parent resource of this resource.
	 * @param id the identifier to set.
	 */
	public void setParentId (String id) {
		parentId = id;
	}

}
