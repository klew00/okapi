/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package org.oasisopen.xliff.v2;

import java.io.Serializable;

/**
 * Provides the methods to access a translation candidate.
 */
public interface ICandidate extends Serializable {

	/**
	 * Creates an empty source.
	 * @return the empty source created.
	 */
	public IFragment createSource ();
	
	/**
	 * Creates an empty target.
	 * @return the empty target created.
	 */
	public IFragment createTarget ();

	/**
	 * Gets the source of this candidate.
	 * @return the source of this candidate.
	 */
	public IFragment getSource ();
	
	/**
	 * Indicates if this candidate has a target.
	 * @return true if this candidate has a target.
	 */
	public boolean hasTarget ();

	/**
	 * Gets the target of this candidate.
	 * @return the target of this candidate, or null if no target exists.
	 */
	public IFragment getTarget ();
	
	public IDataStore getDataStore ();
	
	public void setSource (IFragment fragment);

	public void setTarget (IFragment fragment);

	/**
	 * Gets the measure of how the source of this candidate is similar to the source content
	 * it applies to.
	 * @return 100 if the two source and exactly the same, or a value between 99 and 0.
	 * Or -1 if not similarity is set.
	 */
	public int getSimilarity ();

	public void setSimilarity (int similarity);

	public int getQuality ();

	public void setQuality (int quality);
	
}
