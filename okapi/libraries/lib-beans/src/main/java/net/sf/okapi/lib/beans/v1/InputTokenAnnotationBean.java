/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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

import net.sf.okapi.lib.persistence.IPersistenceSession;
import net.sf.okapi.lib.persistence.PersistenceBean;
import net.sf.okapi.steps.tokenization.common.InputTokenAnnotation;
import net.sf.okapi.steps.tokenization.common.Token;

public class InputTokenAnnotationBean extends PersistenceBean<InputTokenAnnotation> {

	private TokenBean inputToken = new TokenBean();
	
	@Override
	protected InputTokenAnnotation createObject(IPersistenceSession session) {
		return new InputTokenAnnotation(inputToken.get(Token.class, session));
	}

	@Override
	protected void fromObject(InputTokenAnnotation obj,
			IPersistenceSession session) {
		inputToken.set(obj.getInputToken(), session);
	}

	@Override
	protected void setObject(InputTokenAnnotation obj,
			IPersistenceSession session) {
		// No setters		
	}

	public void setInputToken(TokenBean inputToken) {
		this.inputToken = inputToken;
	}

	public TokenBean getInputToken() {
		return inputToken;
	}

}
