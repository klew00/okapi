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

import org.oasisopen.xliff.v2.INote;

public class Note implements INote {

	private static final long serialVersionUID = 0100L;

	private String content;
	private AppliesTo appliesTo;
	
	public Note (String content,
		AppliesTo appliesTo)
	{
		this.content = content;
		this.appliesTo = appliesTo;
	}

	@Override
	public String toString () {
		return content;
	}
	
	public String getText () {
		return content;
	}
	
	public void setText (String content) {
		this.content = content;
	}
	
	public boolean hasText () {
		return !Util.isNullOrEmpty(content);
	}

	public AppliesTo getAppliesTo () {
		return appliesTo;
	}

	public void setAppliesTo (AppliesTo appliesTo) {
		this.appliesTo = appliesTo;
	}

}
