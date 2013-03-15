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

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.lib.persistence.IPersistenceSession;
import net.sf.okapi.lib.persistence.PersistenceBean;
import net.sf.okapi.steps.tokenization.common.Token;
import net.sf.okapi.steps.tokenization.common.TokensAnnotation;
import net.sf.okapi.steps.tokenization.tokens.Tokens;

public class TokensAnnotationBean extends PersistenceBean<TokensAnnotation> {

	private List<TokenBean> tokens = new ArrayList<TokenBean>();
	
	@Override
	protected TokensAnnotation createObject(IPersistenceSession session) {
		Tokens tlist = new Tokens();
		for (TokenBean tokenBean : tokens)
			tlist.add(tokenBean.get(Token.class, session));
		
		return new TokensAnnotation(tlist);
	}

	@Override
	protected void fromObject(TokensAnnotation obj, IPersistenceSession session) {
		Tokens tlist = obj.getTokens();
		for (Token token : tlist) {
			TokenBean tokenBean = new TokenBean();
			tokens.add(tokenBean);
			tokenBean.set(token, session);
		}
	}

	@Override
	protected void setObject(TokensAnnotation obj, IPersistenceSession session) {
	}

	public List<TokenBean> getTokens() {
		return tokens;
	}

	public void setTokens(List<TokenBean> tokens) {
		this.tokens = tokens;
	}

}
