/*===========================================================================
  Copyright (C) 2008-2012 by the Okapi Framework contributors
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

package org.w3c.its;

class ITSTrace {
	
	boolean isChildDone;
	boolean translate;
	int dir;
	int withinText;
	boolean term;
	String termInfo;
	String locNote;
	boolean preserveWS;
	String language;
	String targetPointer;
	String externalRes;
	String localeFilter;
	String idValue;
	String domains;

	ITSTrace () {
		// Default constructor
	}
	
	ITSTrace (ITSTrace initialTrace,
		boolean isChildDone)
	{
		// translate: Inheritance for child elements but not attributes
		translate = initialTrace.translate;
		
		// dir: Inheritance for child element including attributes
		dir = initialTrace.dir;
		
		// withinText: No inheritance
		
		// term: No inheritance
		
		// target: No inheritance
		
		// locNote: Inheritance for child elements including attributes
		locNote = initialTrace.locNote;
		
		// preserveWS: Inheritance for child elements but not attributes
		preserveWS = initialTrace.preserveWS;
	
		// language: Inheritance for child element including attributes 
		language = initialTrace.language;
		
		// idValue: No inheritance
		
		// external resource reference: Inheritance for child element including attributes
		externalRes = initialTrace.externalRes;
		
		// locale filter:  Inheritance for child element including attributes
		localeFilter = initialTrace.localeFilter;
		
		// domain: Inheritance for child elements including attributes
		domains = initialTrace.domains;
		
		this.isChildDone = isChildDone;
	}

}
