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

import net.sf.okapi.common.annotation.GenericAnnotations;

class ITSTrace {
	
	boolean isChildDone;
	boolean translate;
	int dir;
	int withinText;
	boolean term;
	String termInfo;
	String locNote;
	String locNoteType;
	boolean preserveWS;
	String language;
	String targetPointer;
	String externalRes;
	String localeFilter = "*";
	String idValue;
	String domains;
	String storageSize;
	String storageEncoding;
	String lineBreakType;
	String allowedChars;
	String subFilter;
	String lqIssuesRef;
	GenericAnnotations lqIssues;

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
		locNoteType = initialTrace.locNoteType;
		
		// preserveWS: Inheritance for child elements but not attributes
		preserveWS = initialTrace.preserveWS;
	
		// language: Inheritance for child element including attributes 
		language = initialTrace.language;
		
		// idValue: No inheritance
		
		// external resource reference: No inheritance
		
		// locale filter:  Inheritance for child element including attributes
		localeFilter = initialTrace.localeFilter;
		
		// domain: Inheritance for child elements including attributes
		domains = initialTrace.domains;
		
		// localization quality issue:
		lqIssuesRef = initialTrace.lqIssuesRef;
		lqIssues = initialTrace.lqIssues;
		
		// Allowed chars: Inheritance for child elements but not attributes
		allowedChars = initialTrace.allowedChars;
		
		// Store size: No inheritance
		
		// sub-filter: No inheritance
		
		this.isChildDone = isChildDone;
	}

}
