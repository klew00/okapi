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

import net.sf.okapi.common.Range;
import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.lib.persistence.IPersistenceSession;
import net.sf.okapi.lib.persistence.PersistenceBean;
import net.sf.okapi.lib.persistence.beans.FactoryBean;
import net.sf.okapi.steps.tokenization.common.Lexem;

public class LexemBean extends PersistenceBean<Lexem> {

	private int id;
	private String value;
	private RangeBean range = new RangeBean();
	private int lexerId;
	private AnnotationsBean annotations = new AnnotationsBean();
	private boolean deleted;
	private boolean immutable;
	
	@Override
	protected Lexem createObject(IPersistenceSession session) {
		return new Lexem(id, value, range.get(Range.class, session));
	}

	@Override
	protected void fromObject(Lexem obj, IPersistenceSession session) {
		id = obj.getId();
		value = obj.getValue();
		range.set(obj.getRange(), session);
		lexerId = obj.getLexerId();
		annotations.set(obj.getAnnotations(), session);
		deleted = obj.isDeleted();
		immutable = obj.isImmutable();
	}

	@Override
	protected void setObject(Lexem obj, IPersistenceSession session) {
		obj.setLexerId(lexerId);
		for (FactoryBean annotationBean : annotations.getItems())
			obj.setAnnotation(annotationBean.get(IAnnotation.class, session));
		obj.setDeleted(deleted);
		obj.setImmutable(immutable);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public RangeBean getRange() {
		return range;
	}

	public void setRange(RangeBean range) {
		this.range = range;
	}

	public int getLexerId() {
		return lexerId;
	}

	public void setLexerId(int lexerId) {
		this.lexerId = lexerId;
	}

	public AnnotationsBean getAnnotations() {
		return annotations;
	}

	public void setAnnotations(AnnotationsBean annotations) {
		this.annotations = annotations;
	}
	
	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isImmutable() {
		return immutable;
	}

	public void setImmutable(boolean immutable) {
		this.immutable = immutable;
	}
}
