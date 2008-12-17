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

package net.sf.okapi.common.skeleton;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.filters.ISkeleton;
import net.sf.okapi.common.resource.IResource;
import net.sf.okapi.common.resource.TextFragment;

public class GenericSkeleton implements ISkeleton {

	private ArrayList<GenericSkeletonPart> list;
	private boolean createNew = true;
	
	public GenericSkeleton () {
		list = new ArrayList<GenericSkeletonPart>();
	}

	public GenericSkeleton (String data) {
		list = new ArrayList<GenericSkeletonPart>();
		if ( data != null ) add(data);
	}

	public void add (String data) {
		GenericSkeletonPart part = new GenericSkeletonPart(data);
		list.add(part);
		createNew = false;
	}

	public void append (String data) {
		if (( createNew ) || ( list.size() == 0 )) {
			add(data);
		}
		else {
			list.get(list.size()-1).append(data);
		}
	}

	/**
	 * Adds a reference to the resource itself to the skeleton. Defaults to source.
	 * @param referent Resource object.
	 */
	public void addRef (IResource referent)
	{
		GenericSkeletonPart part = new GenericSkeletonPart(TextFragment.makeRefMarker("$self$"));
		part.parent = referent;
		part.language = null;
		list.add(part);
		// Flag that the next append() should start a new part
		createNew = true;
	}
	
	/**
	 * Adds a reference to the resource itself to the skeleton.
	 * @param referent Resource object.
	 * @param language Language or null if the reference is to the source.
	 */
	public void addRef (IResource referent,
		String language)
	{
		GenericSkeletonPart part = new GenericSkeletonPart(TextFragment.makeRefMarker("$self$"));
		part.parent = referent;
		part.language = language;
		list.add(part);
		// Flag that the next append() should start a new part
		createNew = true;
	}

	/**
	 * Adds a reference to the skeleton.
	 * @param referent Resource object.
	 * @param propName Property name or null if the reference is to the text.
	 * @param language Language or null if the reference is to the source.
	 */
	public void addRef (IResource referent,
		String propName,
		String language)
	{
		GenericSkeletonPart part = new GenericSkeletonPart(
			TextFragment.makeRefMarker("$self$", propName));
		part.language = language;
		part.parent = referent;
		list.add(part);
		// Flag that the next append() should start a new part
		createNew = true;
	}
	
	public List<GenericSkeletonPart> getParts () {
		return list;
	}

}
