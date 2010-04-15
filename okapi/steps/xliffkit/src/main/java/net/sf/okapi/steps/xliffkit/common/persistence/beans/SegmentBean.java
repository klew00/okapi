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

package net.sf.okapi.steps.xliffkit.common.persistence.beans;

import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.steps.xliffkit.common.persistence.IPersistenceBean;
import net.sf.okapi.steps.xliffkit.common.persistence.IPersistenceSession;

public class SegmentBean extends TextPartBean {
	
	private String id;
	
	public SegmentBean(IPersistenceSession session) {
		super(session);
	}
	
	@Override
	public <T> T get(T obj) {
		return obj;
	}
	
	@Override
	public <T> T get(Class<T> classRef) {
		TextFragmentBean textBean = super.getPart();
		TextFragment text = null;
		
		if (textBean != null)
			text = textBean.get(TextFragment.class);
		else
			text = new TextFragment();
		
		return classRef.cast(get(new Segment(id, text)));
	}

	@Override
	public IPersistenceBean set(Object obj) {
		super.set(obj);
		
		if (obj instanceof Segment) {			
			Segment seg = (Segment) obj;
			id = seg.getId();
		}
		return this;
	}

	public String getSegment() {
		return id;
	}

	public void setSegment(String id) {
		this.id = id;
	}

//	public void setSegment(TextFragmentBean text) {
//		super.setPart(text);
//	}
//
//	public TextFragmentBean getSegment() {
//		return super.getPart();
//	}
//	
//	@Override
//	@Deprecated
//	public void setPart(TextFragmentBean text) {
//	}
//
//	@Override
//	@Deprecated
//	public TextFragmentBean getPart() {
//		return super.getPart();
//	}
}
