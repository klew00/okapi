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

package net.sf.okapi.common;

/**
 * Represents a skeleton object. It is an object that is associated to a resource and carries data
 * about the non extractable part of the resource. Skeleton objects are used by the 
 * {@link net.sf.okapi.common.filterwriter.IFilterWriter} implementations to reconstruct the 
 * original file format.
 */
public interface ISkeleton {

	/**
	 * Gets a string representation of this skeleton object. The value of the returned string depends
	 * on each implementation of class that implements ISkeleton. Different implementations may return
	 * strings that cannot be compared in a meaningful way. 
	 * @return the string representation of this skeleton object, or null.
	 */
	String toString ();
	
	/**
     * Clones this skeleton object.
     * @return a new skeleton object that is a copy of this one.
     */
	ISkeleton clone();

	/**
	 * Sets a parent of this skeleton object. The parent is the resource that attaches this skeleton with SetSkeleton().
	 * Normally the IResorce implementations set themselves as a parent for the skeleton.  
	 * @param parent reference to the resource that attaches this skeleton object with IResource.setSkeleton()
	 */
	void setParent(IResource parent);
	
	/**
	 * Gets the parent resource of this skeleton. 
	 * @return reference to the resource that attaches this skeleton object with IResource.setSkeleton()
	 */
	IResource getParent();
}
