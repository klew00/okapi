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

package net.sf.okapi.steps.xliffkit.common.persistence.beans;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.steps.xliffkit.common.persistence.FactoryBean;
import net.sf.okapi.steps.xliffkit.common.persistence.IPersistenceBean;
import net.sf.okapi.steps.xliffkit.common.persistence.IPersistenceSession;

public class StartDocumentBean extends BaseNameableBean {

	private String locale;
	private String encoding;
	private boolean isMultilingual;
	private FactoryBean filterParameters = new FactoryBean(getSession());
	private FactoryBean filterWriter = new FactoryBean(getSession());
	private boolean hasUTF8BOM;
	private String lineBreak;
	
	public StartDocumentBean(IPersistenceSession session) {
		super(session);
	}
	
	@Override
	public <T> T get(T obj) {
		obj = super.get(obj);
		
		if (obj instanceof StartDocument) {
			StartDocument sd = (StartDocument) obj;
			
			sd.setLocale(new LocaleId(locale));
			sd.setEncoding(encoding, hasUTF8BOM);
			sd.setMultilingual(isMultilingual);
			sd.setFilterParameters(filterParameters.get(IParameters.class));
			sd.setFilterWriter(filterWriter.get(IFilterWriter.class));
			sd.setLineBreak(lineBreak);
		}		
		return obj;
	}
	
	@Override
	public <T> T get(Class<T> classRef) {
		return classRef.cast(get(new StartDocument(getId())));
	}

	@Override
	public IPersistenceBean set(Object obj) {
		super.set(obj);
		
		if (obj instanceof StartDocument) {
			StartDocument sd = (StartDocument) obj;
	
			LocaleId loc = sd.getLocale(); 
			if (loc != null)
				locale = loc.toString();
			
			encoding = sd.getEncoding();
			isMultilingual = sd.isMultilingual();
			filterParameters.set(sd.getFilterParameters());
			filterWriter.set(sd.getFilterWriter());
			hasUTF8BOM = sd.hasUTF8BOM();
			lineBreak = sd.getLineBreak();
		}
		return this;
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
