/*===========================================================================
  Copyright (C) 2008-2011 by the Okapi Framework contributors
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

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.annotation.AltTranslation;
import net.sf.okapi.common.query.MatchType;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.persistence.IPersistenceSession;
import net.sf.okapi.lib.persistence.PersistenceBean;

public class AltTranslationBean extends PersistenceBean<AltTranslation> {

	private String srcLocId;
	private String trgLocId;
	private TextUnitBean tu = new TextUnitBean();
	private MatchType type;
	private int score;
	private String origin;
	
	@Override
	protected AltTranslation createObject(IPersistenceSession session) {
		ITextUnit tunit = tu.get(ITextUnit.class, session);
		LocaleId srcLoc = null;
		LocaleId trgLoc = null;
		
		if (!Util.isEmpty(srcLocId))
			srcLoc = new LocaleId(srcLocId);
		
		if (!Util.isEmpty(trgLocId))
			trgLoc = new LocaleId(trgLocId);
		
		TextFragment src = null; 
		TextFragment trg = null;
		
		if (tunit != null) {
			src = tunit.getSource().getSegments().getFirstContent(); 
			trg = tunit.getTarget(trgLoc).getSegments().getFirstContent();
		}		
		return new AltTranslation(srcLoc, trgLoc, null, src, trg, type, score, origin);
	}

	@Override
	protected void fromObject(AltTranslation obj, IPersistenceSession session) {		
		srcLocId = obj.getSourceLocale().toString();
		trgLocId = obj.getTargetLocale().toString();
		tu.set(obj.getEntry(), session);
		type = obj.getType();
		score = obj.getCombinedScore();
		origin = obj.getOrigin();
	}

	@Override
	protected void setObject(AltTranslation obj, IPersistenceSession session) {
		// No setters, immutable instance of AltTranslation is created in createObject() 
	}

	public String getSrcLocId() {
		return srcLocId;
	}

	public void setSrcLocId(String srcLocId) {
		this.srcLocId = srcLocId;
	}

	public String getTrgLocId() {
		return trgLocId;
	}

	public void setTrgLocId(String trgLocId) {
		this.trgLocId = trgLocId;
	}

	public TextUnitBean getTu() {
		return tu;
	}

	public void setTu(TextUnitBean tu) {
		this.tu = tu;
	}

	public MatchType getType() {
		return type;
	}

	public void setType(MatchType type) {
		this.type = type;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}
}
