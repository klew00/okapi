/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.lib.beans.v0;

import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.beans.v0.IPersistenceBean;
import net.sf.okapi.lib.beans.v0.IPersistenceSession;

public class SegmentBean implements IPersistenceBean {

	private String id;
	private TextFragmentBean text = new TextFragmentBean();
	
	@Override
	public void init(IPersistenceSession session) {
	}

	@Override
	public <T> T get(Class<T> classRef) {
		Segment seg = new Segment(id, text.get(TextFragment.class));		
		return classRef.cast(seg);
	}

	@Override
	public IPersistenceBean set(Object obj) {
		if (obj instanceof Segment) {
			Segment seg = (Segment) obj;
			id = seg.id;
			text.set(seg.text);
		}
		return this;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public TextFragmentBean getText() {
		return text;
	}

	public void setText(TextFragmentBean text) {
		this.text = text;
	}

}
