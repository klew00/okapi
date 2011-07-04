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

import java.io.Serializable;

public class CodesStore implements Serializable {

	private static final long serialVersionUID = 0100L;

	private Codes srcCodes;
	private Codes trgCodes;
	
	public CodesStore () {
	}

	public boolean hasNonEmptyCode () {
		if ( srcCodes != null ) {
			if ( srcCodes.hasNonEmptyCode() ) return true;
		}
		if ( trgCodes != null ) {
			if ( trgCodes.hasNonEmptyCode() ) return true;
		}
		return false;
	}
	
	public boolean hasSourceCode () {
		return (( srcCodes != null ) && srcCodes.hasCode() );
	}
	
	public boolean hasTargetCode () {
		return (( trgCodes != null ) && trgCodes.hasCode() );
	}
	
	public Codes getSourceCodes () {
		if ( srcCodes == null ) srcCodes = new Codes(this);
		return srcCodes;
	}
	
	public Codes getTargetCodes () {
		if ( trgCodes == null ) trgCodes = new Codes(this);
		return trgCodes;
	}
	
//	String checkId (String id) {
//		// Create a new ID if the one provided is null or empty
//		if (( id == null ) || id.isEmpty() ) {
//			id = String.valueOf(++lastAutoId);
//		}
//		// Checks if the ID is already used
//		boolean exists = true;
//		while ( exists ) {
//			exists = false;
//			for ( int i=0; i<codes.size(); i++ ) {
//				if ( codes.get(i).getId().equals(id) ) {
//					// If it is, we just try the next auto value
//					id = String.valueOf(++lastAutoId);
//					exists = true;
//					break;
//				}
//			}
//		}
//		// Returns the validated (and possibly modified id)
//		return id;
//	}

}
