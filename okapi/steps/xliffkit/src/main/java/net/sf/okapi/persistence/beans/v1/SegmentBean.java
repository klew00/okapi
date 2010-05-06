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

package net.sf.okapi.persistence.beans.v1;

import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextPart;
import net.sf.okapi.persistence.IPersistenceSession;

public class SegmentBean extends TextPartBean {
	
	private String id;

	@Override
	protected TextPart createObject(IPersistenceSession session) {
		TextFragmentBean textBean = super.getPart();
		TextFragment text = null;
		
		if (textBean != null)
			text = textBean.get(TextFragment.class, session);
		else
			text = new TextFragment();
		
		return new Segment(id, text);
	}

	@Override
	protected void fromObject(TextPart obj, IPersistenceSession session) {
		super.fromObject(obj, session);
		
		if (obj instanceof Segment) {			
			Segment seg = (Segment) obj;
			id = seg.getId();
		}
	}

	@Override
	protected void setObject(TextPart obj, IPersistenceSession session) {
		super.setObject(obj, session);
	}

	
	public String getSegment() {
		return id;
	}

	public void setSegment(String id) {
		this.id = id;
	}
}
