/*===========================================================================
  Copyright (C) 2009-2012 by the Okapi Framework contributors
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
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.BaseNameable;
import net.sf.okapi.common.resource.StartSubfilter;
import net.sf.okapi.lib.persistence.IPersistenceSession;
import net.sf.okapi.lib.persistence.beans.FactoryBean;

public class StartSubfilterBean extends StartGroupBean {

	private String encoding;
	private boolean isMultilingual;
	private FactoryBean params = new FactoryBean();
	private FactoryBean filterWriter = new FactoryBean();
	private boolean hasUTF8BOM;
	private String lineBreak;
	
	@Override
	protected StartSubfilter createObject(IPersistenceSession session) {
		return new StartSubfilter(getParentId(), getId());
	}

	@Override
	protected void setObject(BaseNameable obj, IPersistenceSession session) {
		super.setObject(obj, session);
	
		if (obj instanceof StartSubfilter) {
			StartSubfilter ssf = (StartSubfilter) obj;
			
			ssf.setEncoding(encoding);
			ssf.setMultilingual(isMultilingual);
			ssf.setParams(params.get(IParameters.class, session));
			ssf.setFilterWriter(filterWriter.get(IFilterWriter.class, session));
			ssf.setHasUTF8BOM(hasUTF8BOM);
			ssf.setLineBreak(lineBreak);
		}
	}

	@Override
	protected void fromObject(BaseNameable obj, IPersistenceSession session) {
		super.fromObject(obj, session);
		
		if (obj instanceof StartSubfilter) {
			StartSubfilter ssf = (StartSubfilter) obj;
			
			encoding = ssf.getEncoding();
			isMultilingual = ssf.isMultilingual();
			params.set(ssf.getParams(), session);
			filterWriter.set(ssf.getFilterWriter(), session);
			hasUTF8BOM = ssf.isHasUTF8BOM();
			lineBreak = ssf.getLineBreak();
		}
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

	public FactoryBean getParams() {
		return params;
	}

	public void setParams(FactoryBean params) {
		this.params = params;
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

}
