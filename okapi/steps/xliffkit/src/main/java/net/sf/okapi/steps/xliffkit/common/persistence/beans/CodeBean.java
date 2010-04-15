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

package net.sf.okapi.steps.xliffkit.common.persistence.beans;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.steps.xliffkit.common.persistence.IPersistenceBean;
import net.sf.okapi.steps.xliffkit.common.persistence.IPersistenceSession;
import net.sf.okapi.steps.xliffkit.common.persistence.PersistenceBean;

public class CodeBean extends PersistenceBean {

	private String data;
	
	public CodeBean(IPersistenceSession session) {
		super(session);
	}
	
	@Override
	public <T> T get(T obj) {
		return obj;
	}

	@Override
	public <T> T get(Class<T> classRef) {
		List<Code> codes = Code.stringToCodes(data);
		if (Util.isEmpty(codes))
			return null;
		
		return classRef.cast(codes.get(0));
	}
	
	@Override
	public IPersistenceBean set(Object obj) {
		if (obj instanceof Code) {
			Code code = (Code) obj;

			List<Code> codes = new ArrayList<Code>();
			codes.add(code);
			data = Code.codesToString(codes);
		}
		return this;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
