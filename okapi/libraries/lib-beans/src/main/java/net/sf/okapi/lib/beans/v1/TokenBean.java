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
import net.sf.okapi.steps.tokenization.common.Lexem;
import net.sf.okapi.steps.tokenization.common.Token;

public class TokenBean extends PersistenceBean<Token> {

	private int tokenId; 
	private LexemBean lexem = new LexemBean();
	private int score;
	
	@Override
	protected Token createObject(IPersistenceSession session) {
		return new Token(tokenId, lexem.get(Lexem.class, session), score);
	}

	@Override
	protected void fromObject(Token obj, IPersistenceSession session) {
		tokenId = obj.getTokenId();
		lexem.set(obj.getLexem(), session);
		score = obj.getScore();
	}

	@Override
	protected void setObject(Token obj, IPersistenceSession session) {
		obj.setScore(score);		
	}

	public int getTokenId() {
		return tokenId;
	}

	public void setTokenId(int tokenId) {
		this.tokenId = tokenId;
	}

	public LexemBean getLexem() {
		return lexem;
	}

	public void setLexem(LexemBean lexem) {
		this.lexem = lexem;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

}
