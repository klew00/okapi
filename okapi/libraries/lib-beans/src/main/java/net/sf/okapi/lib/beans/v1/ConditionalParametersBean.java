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

import net.sf.okapi.common.IParameters;
import net.sf.okapi.filters.openxml.ConditionalParameters;
import net.sf.okapi.lib.persistence.IPersistenceSession;
import net.sf.okapi.lib.persistence.PersistenceBean;

public class ConditionalParametersBean extends PersistenceBean<IParameters> {
	
	private String data;
	private int fileType = ConditionalParameters.MSWORD;

	@Override
	protected ConditionalParameters createObject(IPersistenceSession session) {
		return new ConditionalParameters();
	}

	@Override
	protected void fromObject(IParameters obj, IPersistenceSession session) {
		if (obj instanceof ConditionalParameters) {
			ConditionalParameters params = (ConditionalParameters) obj;
			data = params.toString();
			fileType = params.nFileType;
		}
	}

	@Override
	protected void setObject(IParameters obj, IPersistenceSession session) {
		if (obj instanceof ConditionalParameters) {
			ConditionalParameters params = (ConditionalParameters) obj;
			params.fromString(data);
			params.nFileType = fileType;
		}
	}

	public int getFileType() {
		return fileType;
	}

	public void setFileType(int fileType) {
		this.fileType = fileType;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getData() {
		return data;
	}

}
