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

package net.sf.okapi.filters.openoffice;

public class ElementData {

	public enum WithinTextType {
		EXTERNAL, INTERNAL, EMBEDDED
	};
	
	public enum CaseType {
		TRANS_EXTERNAL, TRANS_INTERNAL, TRANS_EMBEDDED,
		NOTRANS_EXTERNAL, NOTRANS_INTERNAL, NOTRANS_EMBEDDED
	};
	
	public WithinTextType withinText;
	public boolean translate;
	public boolean group;
	public String id;

	public ElementData (WithinTextType withinText,
		boolean translate)
	{
		this.withinText = withinText;
		this.translate = translate;
	}

	
	public ElementData (WithinTextType withinText,
		boolean translate,
		boolean group)
	{
		this.withinText = withinText;
		this.translate = translate;
		this.group = group;
	}

	public ElementData (WithinTextType withinText,
		boolean translate,
		boolean group,
		String id)
	{
		this.withinText = withinText;
		this.translate = translate;
		this.group = group;
		this.id = id;
	}

	public CaseType getCase () {
		if ( translate ) {
			switch ( withinText ) {
			case EXTERNAL:
				return CaseType.TRANS_EXTERNAL;
			case INTERNAL:
				return CaseType.TRANS_INTERNAL;
			case EMBEDDED:
				return CaseType.TRANS_EMBEDDED;
			}
		}
		else {
			switch ( withinText ) {
			case EXTERNAL:
				return CaseType.NOTRANS_EXTERNAL;
			case INTERNAL:
				return CaseType.NOTRANS_INTERNAL;
			case EMBEDDED:
				return CaseType.NOTRANS_EMBEDDED;
			}
		}
		return null;
	}

}
