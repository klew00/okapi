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

public class XLIFFEvent {

	public static enum XLIFFEventType {
		START_DOCUMENT,
		START_SECTION,
		START_GROUP,
		EXTRACTION_UNIT,
		END_GROUP,
		END_SECTION,
		END_DOCUMENT
	};
	
	private XLIFFEventType type;
	private EventData object;
	
	public XLIFFEvent (XLIFFEventType type, 
		EventData object)
	{
		this.type = type;
		this.object = object;
	}
	
	public XLIFFEventType getType () {
		return this.type;
	}

	public boolean isUnit () {
		return (type == XLIFFEventType.EXTRACTION_UNIT);
	}
	
	public Unit getUnit () {
		return (Unit)object;
	}

	public boolean isStartDocument () {
		return (type == XLIFFEventType.START_DOCUMENT);
	}
	
	public DocumentData getDocumentData () {
		return (DocumentData)object;
	}
	
	public boolean isEndDocument () {
		return (type == XLIFFEventType.END_DOCUMENT);
	}
	
	public boolean isStartSection () {
		return (type == XLIFFEventType.START_SECTION);
	}
	
	public boolean isEndSection () {
		return (type == XLIFFEventType.END_SECTION);
	}
	
	public SectionData getSectionData () {
		return (SectionData)object;
	}

	public boolean isStartGroup () {
		return (type == XLIFFEventType.START_GROUP);
	}
	
	public boolean isEndGroup () {
		return (type == XLIFFEventType.END_GROUP);
	}
	
	public GroupData getGroupData () {
		return (GroupData)object;
	}

}
