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

package net.sf.okapi.common.skeleton;

import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;

/**
 * Part of {@link GenericSkeleton} object.
 */
public class GenericSkeletonPart { // public for OpenXML

	StringBuilder data;
	IResource parent = null;
	LocaleId locId = null;
	
	public GenericSkeletonPart (String data) {
		this.data = new StringBuilder(data);
	}

	public GenericSkeletonPart (char data) {
		this.data = new StringBuilder();
		this.data.append(data);
	}
	
	public GenericSkeletonPart(String data, IResource parent,
			LocaleId locId) {
		super();
		this.data = new StringBuilder(data);
		this.parent = parent;
		this.locId = locId;
	}
	
	public GenericSkeletonPart(char data, IResource parent,
			LocaleId locId) {
		super();
		this.data = new StringBuilder(data);
		this.parent = parent;
		this.locId = locId;
	}

	@Override
	public String toString () {
		return data.toString();
	}

	public void append (String data) {
		this.data.append(data);
	}

	public LocaleId getLocale () {
		return locId;
	}

	public IResource getParent() {
		return parent;
	}

	public StringBuilder getData () {
		return data;
	}

	public void setData(String data) {
		this.data = new StringBuilder(data);
	}
}
