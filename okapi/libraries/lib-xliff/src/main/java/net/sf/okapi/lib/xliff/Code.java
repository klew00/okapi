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

public class Code implements Serializable {

	private static final long serialVersionUID = 0100L;

	public static final int CANDELETE = 0x01;
	public static final int CANREPLICATE = 0x02;
	public static final int CANREORDER = 0x04;
	public static final int CANCHANGEPARENT = 0x08;
	
	String internalId;
	
	private InlineType inlineType;
	private String id;
	private String originalData;
	private int hints = (CANDELETE | CANREPLICATE | CANREORDER | CANCHANGEPARENT);

	public Code (InlineType inlineType,
		String id,
		String originalData)
	{
		if ( inlineType == null ) throw new RuntimeException("inline type cannot be null.");
		if ( id == null ) throw new RuntimeException("Code id cannot be null.");
		this.inlineType = inlineType;
		this.id = id;
		this.internalId = Util.toInternalId(id, inlineType);
		this.originalData = originalData;
	}

	public String getOriginalData () {
		return originalData;
	}
	
	public void setOriginalData (String originalData) {
		this.originalData = originalData;
	}
	
	public boolean hasOriginalData () {
		return !Util.isNullOrEmpty(originalData);
	}
	
	public String getId () {
		return id;
	}
	
	public String getInternalId () {
		return internalId;
	}
	
	public InlineType getInlineType () {
		return inlineType;
	}

	public void setInlineType (InlineType inlineType) {
		this.inlineType = inlineType;
	}

	public int getHints () {
		return hints;
	}
	
	public void setHints (int hints) {
		this.hints = hints;
	}
	
	public boolean canDelete () {
		return (( hints & CANDELETE ) == CANDELETE);
	}

	public void setCanDelete (boolean canDelete) {
		if ( canDelete ) hints |= CANDELETE;
		else hints &= ~CANDELETE;
	}

	public boolean canReplicate () {
		return (( hints & CANREPLICATE ) == CANREPLICATE);
	}

	public void setCanReplicate (boolean canReplicate) {
		if ( canReplicate ) hints |= CANREPLICATE;
		else hints &= ~CANREPLICATE;
	}

	public boolean canReorder () {
		return (( hints & CANREORDER ) == CANREORDER);
	}

	public void setCanReorder (boolean canReorder) {
		if ( canReorder ) hints |= CANREORDER;
		else hints &= ~CANREORDER;
	}

	public boolean canChangeParent () {
		return (( hints & CANCHANGEPARENT ) == CANCHANGEPARENT);
	}

	public void setCanChangeParent (boolean canChangeParent) {
		if ( canChangeParent ) hints |= CANCHANGEPARENT;
		else hints &= ~CANCHANGEPARENT;
	}

}
