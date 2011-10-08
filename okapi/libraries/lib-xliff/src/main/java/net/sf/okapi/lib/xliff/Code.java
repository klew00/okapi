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

import org.oasisopen.xliff.v2.ICode;
import org.oasisopen.xliff.v2.InlineType;

public class Code implements ICode {

	private static final long serialVersionUID = 0100L;

	public static final int CANDELETE = 0x01;
	public static final int CANREPLICATE = 0x02;
	public static final int CANREORDER = 0x04;
	public static final int CANCHANGEPARENT = 0x08;
	
	private String internalId;
	private InlineType inlineType;
	private String id;
	private String originalData;
	private int hints = (CANDELETE | CANREPLICATE | CANREORDER | CANCHANGEPARENT);
	private String disp;
	private String equiv;
	private String type;
	private String subFlows;

	public Code (InlineType inlineType,
		String id,
		String originalData)
	{
		if ( inlineType == null ) {
			throw new RuntimeException("Inline type cannot be null.");
		}
		if ( id == null ) {
			throw new RuntimeException("Code id cannot be null.");
		}
		this.inlineType = inlineType;
		this.id = id;
		this.internalId = Util.toInternalId(id, inlineType);
		this.originalData = originalData;
	}

	@Override
	public String getOriginalData () {
		return originalData;
	}
	
	@Override
	public void setOriginalData (String originalData) {
		this.originalData = originalData;
	}
	
	@Override
	public boolean hasOriginalData () {
		return !Util.isNullOrEmpty(originalData);
	}
	
	@Override
	public String getId () {
		return id;
	}

	@Override
	public String getInternalId () {
		return internalId;
	}
	
	@Override
	public InlineType getInlineType () {
		return inlineType;
	}

	@Override
	public void setInlineType (InlineType inlineType) {
		this.inlineType = inlineType;
	}

	@Override
	public String getType () {
		return type;
	}
	
	@Override
	public void setType (String type) {
		this.type = type;
	}
	
	@Override
	public String getDisp () {
		return disp;
	}
	
	@Override
	public void setDisp (String disp) {
		this.disp = disp;
	}
	
	@Override
	public String getEquiv () {
		return equiv;
	}
	
	@Override
	public void setEquiv (String equiv) {
		this.equiv = equiv;
	}
	
	@Override
	public String getSubFlows () {
		return subFlows;
	}
	
	@Override
	public void setSubFlows (String subFlows) {
		this.subFlows = subFlows;
	}
	
	@Override
	public int getHints () {
		return hints;
	}
	
	@Override
	public void setHints (int hints) {
		this.hints = hints;
	}
	
	@Override
	public boolean canDelete () {
		return (( hints & CANDELETE ) == CANDELETE);
	}

	@Override
	public void setCanDelete (boolean canDelete) {
		if ( canDelete ) hints |= CANDELETE;
		else hints &= ~CANDELETE;
	}

	@Override
	public boolean canReplicate () {
		return (( hints & CANREPLICATE ) == CANREPLICATE);
	}

	@Override
	public void setCanReplicate (boolean canReplicate) {
		if ( canReplicate ) hints |= CANREPLICATE;
		else hints &= ~CANREPLICATE;
	}

	@Override
	public boolean canReorder () {
		return (( hints & CANREORDER ) == CANREORDER);
	}

	@Override
	public void setCanReorder (boolean canReorder) {
		if ( canReorder ) hints |= CANREORDER;
		else hints &= ~CANREORDER;
	}

	@Override
	public boolean canChangeParent () {
		return (( hints & CANCHANGEPARENT ) == CANCHANGEPARENT);
	}

	@Override
	public void setCanChangeParent (boolean canChangeParent) {
		if ( canChangeParent ) hints |= CANCHANGEPARENT;
		else hints &= ~CANCHANGEPARENT;
	}

	@Override
	public boolean equals (ICode code) {
		if ( this == code ) return true;
		if ( inlineType.compareTo(code.getInlineType()) != 0 ) return false;
		if ( compStr(id, code.getId()) != 0 ) return false;
		if ( compStr(type, code.getType()) != 0 ) return false;
		if ( compStr(subFlows, code.getSubFlows()) != 0 ) return false;
		if ( compStr(originalData, code.getOriginalData()) != 0 ) return false;
		if ( compStr(disp, code.getDisp()) != 0 ) return false;
		if ( compStr(equiv, code.getEquiv()) != 0 ) return false;
		if ( hints != code.getHints() ) return false;
		if ( compStr(internalId, code.getInternalId()) != 0 ) return false;
		return true;
	}
	
	private int compStr (String s1,
		String s2)
	{
		if ( s1 == null ) {
			if ( s2 == null ) return 0;
			else return -1;
		}
		if ( s2 == null ) {
			return 1;
		}
		return s1.compareTo(s2);
	}

}
