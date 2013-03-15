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

public class AltTransBean extends PersistenceBean<Object> {

	private String srcLang;
	private String trgLang;
	private TextUnitBean tu = new TextUnitBean();
	
	@Override
	protected Object createObject(IPersistenceSession session) {
		return null;
	}

	@Override
	protected void fromObject(Object obj, IPersistenceSession session) {
	}

	@Override
	protected void setObject(Object obj, IPersistenceSession session) {
	}

	public String getSrcLang() {
		return srcLang;
	}

	public void setSrcLang(String srcLang) {
		this.srcLang = srcLang;
	}

	public String getTrgLang() {
		return trgLang;
	}

	public void setTrgLang(String trgLang) {
		this.trgLang = trgLang;
	}

	public TextUnitBean getTu() {
		return tu;
	}

	public void setTu(TextUnitBean tu) {
		this.tu = tu;
	}

}
