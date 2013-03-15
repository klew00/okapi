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

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.persistence.IPersistenceSession;
import net.sf.okapi.lib.persistence.PersistenceBean;

public class TextFragmentBean extends PersistenceBean<TextFragment> {

	private String text;
	private List<CodeBean> codes = new ArrayList<CodeBean>();

	@Override
	protected TextFragment createObject(IPersistenceSession session) {
		return new TextFragment();
	}

	@Override
	protected void fromObject(TextFragment obj, IPersistenceSession session) {
		text = obj.getCodedText();
		
		for (Code code : obj.getCodes()) {
			CodeBean codeBean = new CodeBean();
			codeBean.set(code, session);
			codes.add(codeBean);
		}			
	}

	@Override
	protected void setObject(TextFragment obj, IPersistenceSession session) {
//			for (CodeBean code : codes)
//				tf.getCodes().add(code.get(Code.class)); // tf.getCodes() returns Collections.unmodifiableList, no way to add
		
		List<Code> newCodes = new ArrayList<Code>();
		for (CodeBean code : codes)
			newCodes.add(code.get(Code.class, session));
		
		obj.setCodedText(text, newCodes);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<CodeBean> getCodes() {
		return codes;
	}

	public void setCodes(List<CodeBean> codes) {
		this.codes = codes;
	}
}
