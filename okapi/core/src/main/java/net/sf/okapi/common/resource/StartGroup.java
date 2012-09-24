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
 * Resource associated with the filter event START_GROUP.
 */
public class StartGroup extends BaseReferenceable {

	/**
	 * Creates a new StartGroup object.
	 * @param parentId The identifier of the parent resource for this group.
	 */
	public StartGroup (String parentId) {
		this(parentId, null, false);
	}

	/**
	 * Creates a new startGroup object with the identifier of the group's parent
	 * and the group's identifier.
	 * @param parentId the identifier of the parent resource for this group.
	 * @param id the identifier of this group.
	 */
	public StartGroup (String parentId,
		String id)
	{
		this(parentId, id, false);
	}

	/**
	 * Creates a new startGroup object with the identifier of the group's parent,
	 * the group's identifier, and an indicator of wether this group is a referent or not.
	 * @param parentId the identifier of the parent resource for this group.
	 * @param id the identifier of this group.
	 * @param isReference true if this group is referred by another resource.
	 */
	public StartGroup (String parentId,
		String id,
		boolean isReference)
	{
		super();
		this.parentId = parentId;
		this.id = id;
		this.refCount = (isReference ? 1 : 0);
	}

}
