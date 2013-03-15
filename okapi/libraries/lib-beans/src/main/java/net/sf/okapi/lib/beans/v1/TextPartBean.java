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

package net.sf.okapi.lib.beans.v1;

import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextPart;
import net.sf.okapi.lib.persistence.IPersistenceSession;
import net.sf.okapi.lib.persistence.PersistenceBean;

public class TextPartBean extends PersistenceBean<TextPart> {
	private TextFragmentBean text = new TextFragmentBean();
	
	@Override
	protected TextPart createObject(IPersistenceSession session) {
		return new TextPart(text.get(TextFragment.class, session));
	}

	@Override
	protected void fromObject(TextPart obj, IPersistenceSession session) {
		if (obj instanceof TextPart) {
			TextPart tp = (TextPart) obj;
			
			text.set(tp.getContent(), session);
		}
	}

	@Override
	protected void setObject(TextPart obj, IPersistenceSession session) {
	}

	public void setPart(TextFragmentBean text) {
		this.text = text;
	}

	public TextFragmentBean getPart() {
		return text;
	}
}
