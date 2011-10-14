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

package net.sf.okapi.lib.xliff;

import java.util.ArrayList;
import java.util.Iterator;

import org.oasisopen.xliff.v2.ICode;
import org.oasisopen.xliff.v2.IDataStore;
import org.oasisopen.xliff.v2.IMarker;
import org.oasisopen.xliff.v2.IMarkers;
import org.oasisopen.xliff.v2.InlineType;

public class Markers implements IMarkers {

	private static final long serialVersionUID = 0100L;

	private IDataStore store;
	private ArrayList<IMarker> markers;
	
	public Markers (IDataStore store) {
		this.store = store;
	}

	@Override
	public boolean hasCodeWithOriginalData () {
		if ( markers != null ) {
			for ( IMarker marker : markers ) {
				if ( marker.isAnnotation() ) continue;
				if ( ((ICode)marker).hasOriginalData() ) {
					return true;
				}
			}
		}
		return false;
	}
	
	public IMarker getClosingPart (IMarker openingMarker) {
		for ( IMarker marker : markers ) {
			if ( marker.getId().equals(openingMarker.getId()) ) {
				return marker;
			}
		}
		return null;
	}
	
	public IMarker getOpeningPart (IMarker closingMarker) {
		for ( IMarker marker : markers ) {
			if ( marker.getId().equals(closingMarker.getId()) ) {
				return marker;
			}
		}
		return null;
	}
	
	@Override
	public int size () {
		if ( markers == null ) return 0;
		return markers.size();
	}

	@Override
	public IDataStore getDataStore () {
		return store;
	}

	@Override
	public IMarker get (int index) {
		if ( markers != null ) {
			return markers.get(index);
		}
		// Else: error
		throw new IndexOutOfBoundsException("Empty list of codes.");
	}
	
	@Override
	public IMarker get (String id,
		InlineType type)
	{
		if ( markers == null ) return null;
		String tmp = Util.toInternalId(id, type);
		for ( IMarker marker : markers ) {
			if ( marker.getInternalId().equals(tmp) ) return marker;
		}
		return null; // Not found
	}

	@Override
	public void add (IMarker marker) {
		if ( markers == null ) markers = new ArrayList<IMarker>();
		markers.add(marker);
	}

//	boolean validateClosingTypesMatchOpeningTypes () {
//		if ( markers == null ) return true;
//		for ( IMarker marker : markers ) {
//			if ( marker.getInlineType().equals(InlineType.CLOSING) ) {
//				IMarker opening = getOpeningPart(marker);
//				if ( opening == null ) {
//					// Not found yet
//				}
//				else {
//					String oType = opening.getType();
//					String cType = marker.getType();
//					if ( oType == null ) {
//						if ( cType != null ) {
//							// Error: type defined in closing but not in opening
//						}
//					}
////					else 
//				}
//			}
//		}
//		return true;
//	}

	@Override
	public Iterator<IMarker> iterator () {
		if ( markers == null ) {
			markers = new ArrayList<IMarker>();
		}
		return markers.iterator();
	}

}
