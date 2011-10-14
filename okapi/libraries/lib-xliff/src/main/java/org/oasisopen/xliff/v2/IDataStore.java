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
import java.util.Map;

/**
 * Provides access to an object where codes, markers and other ancillary parts of an XLIFF unit
 * are stored. This storage is common to the different components of the unit. 
 */
public interface IDataStore extends Serializable {

	/**
	 * Indicates if there is at least one code (in the source or the target)
	 * in this data store that has original data.
	 * @return true if there is one code with original data in this store,
	 * false otherwise.
	 */
	public boolean hasCodeWithOriginalData ();
	
	public boolean hasSourceMarker ();
	
	public boolean hasTargetMarker ();
	
	public IMarkers getSourceMarkers ();
	
	public IMarkers getTargetMarkers ();

	public void calculateOriginalDataToIdsMap ();

	public void setOutsideRepresentationMap (Map<String, String> map);

	public Map<String, String> getOutsideRepresentationMap ();

	/**
	 * Gets the id for the outside storage of a given original data.
	 * You must have called {@link #calculateOriginalDataToIdsMap()} before calling this method the first time.
	 * @param originalData the original data
	 * @return the id for the outside storage of the given original data 
	 */
	public String getIdForOriginalData (String originalData);

	public String getOriginalDataForId (String id);
	
}
