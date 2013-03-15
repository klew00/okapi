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

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.BaseNameable;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.lib.persistence.IPersistenceSession;
import net.sf.okapi.lib.persistence.beans.FactoryBean;

public class StartDocumentBean extends BaseNameableBean {

	private String locale;
	private String encoding;
	private boolean isMultilingual;
	private FactoryBean filterParameters = new FactoryBean();
	private FactoryBean filterWriter = new FactoryBean();
	private boolean hasUTF8BOM;
	private String lineBreak;

	@Override
	protected BaseNameable createObject(IPersistenceSession session) {
		return new StartDocument(super.getId());
	}

	@Override
	protected void fromObject(BaseNameable obj, IPersistenceSession session) {
		super.fromObject(obj, session);
		
		if (obj instanceof StartDocument) {
			StartDocument sd = (StartDocument) obj;
	
			LocaleId loc = sd.getLocale(); 
			if (loc != null)
				locale = loc.toString();
			
			encoding = sd.getEncoding();
			isMultilingual = sd.isMultilingual();
			filterParameters.set(sd.getFilterParameters(), session);
			filterWriter.set(sd.getFilterWriter(), session);
			hasUTF8BOM = sd.hasUTF8BOM();
			lineBreak = sd.getLineBreak();
		}
	}

	@Override
	protected void setObject(BaseNameable obj, IPersistenceSession session) {
		super.setObject(obj, session);
		
		if (obj instanceof StartDocument) {
			StartDocument sd = (StartDocument) obj;
			
			sd.setLocale(new LocaleId(locale));
			sd.setEncoding(encoding, hasUTF8BOM);
			sd.setMultilingual(isMultilingual);
			sd.setFilterParameters(filterParameters.get(IParameters.class, session));
			sd.setFilterWriter(filterWriter.get(IFilterWriter.class, session));
			sd.setLineBreak(lineBreak);
		}
	}
	
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public boolean isMultilingual() {
		return isMultilingual;
	}

	public void setMultilingual(boolean isMultilingual) {
		this.isMultilingual = isMultilingual;
	}

	public FactoryBean getFilterWriter() {
		return filterWriter;
	}

	public void setFilterWriter(FactoryBean filterWriter) {
		this.filterWriter = filterWriter;
	}

	public boolean isHasUTF8BOM() {
		return hasUTF8BOM;
	}

	public void setHasUTF8BOM(boolean hasUTF8BOM) {
		this.hasUTF8BOM = hasUTF8BOM;
	}

	public String getLineBreak() {
		return lineBreak;
	}

	public void setLineBreak(String lineBreak) {
		this.lineBreak = lineBreak;
	}

	public void setFilterParameters(FactoryBean filterParameters) {
		this.filterParameters = filterParameters;
	}

	public FactoryBean getFilterParameters() {
		return filterParameters;
	}
}
