/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.common.annotation;

public enum IssueType {

	MISSING_TARGETTU,
	
	MISSING_TARGETSEG,
	EXTRA_TARGETSEG,
	
	EMPTY_TARGETSEG,
	EMPTY_SOURCESEG,
	
	MISSING_LEADINGWS,
	MISSINGORDIFF_LEADINGWS,
	EXTRA_LEADINGWS,
	EXTRAORDIFF_LEADINGWS,
	MISSING_TRAILINGWS,
	MISSINGORDIFF_TRAILINGWS,
	EXTRA_TRAILINGWS,
	EXTRAORDIFF_TRAILINGWS,

	TARGET_SAME_AS_SOURCE,
	
	MISSING_CODE,
	EXTRA_CODE,
	SUSPECT_CODE,
	
	UNEXPECTED_PATTERN,
	
	SUSPECT_PATTERN,
	
	SOURCE_LENGTH,
	TARGET_LENGTH,
	
	ALLOWED_CHARACTERS,
	
	TERMINOLOGY,
	
	LANGUAGETOOL_ERROR;


	public static String getITSType (IssueType issueType) {
		switch (issueType ) {
			case MISSING_TARGETTU:
			case MISSING_TARGETSEG:
			case EMPTY_TARGETSEG:
			case EMPTY_SOURCESEG:
				return "ommission";
				
			case EXTRA_TARGETSEG:
				return "addition";
				
			case MISSING_LEADINGWS:
			case MISSINGORDIFF_LEADINGWS:
			case EXTRA_LEADINGWS:
			case EXTRAORDIFF_LEADINGWS:
			case MISSING_TRAILINGWS:
			case MISSINGORDIFF_TRAILINGWS:
			case EXTRA_TRAILINGWS:
			case EXTRAORDIFF_TRAILINGWS:
				return "whitespace";
				
			case TARGET_SAME_AS_SOURCE:
				return "untranslated";
				
			case MISSING_CODE:
			case EXTRA_CODE:
			case SUSPECT_CODE:
				return "markup";
				
			case UNEXPECTED_PATTERN:
				return "pattern-problem";
				
			case SUSPECT_PATTERN:
			case SOURCE_LENGTH:
			case TARGET_LENGTH:
				return "length";
				
			case ALLOWED_CHARACTERS:
				
			case TERMINOLOGY:
				return "terminology";
				
			case LANGUAGETOOL_ERROR:
				return "other";
				
			default:
				return "uncategorized";
		}
	}
}
