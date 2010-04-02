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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sf.okapi.steps.xliffkit.common.persistence.IPersistenceBean;

public class ZipFileBean implements IPersistenceBean {

	private String name;  // ZIP file name
	private List<ZipEntryBean> entries = new ArrayList<ZipEntryBean>(); // enumeration of the ZIP file entries 
	
	@Override
	public <T> T get(T obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T get(Class<T> classRef) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPersistenceBean set(Object obj) {
		if (obj instanceof ZipFile) {
			ZipFile zf = (ZipFile) obj;
			
			name = zf.getName();
			
			for (Enumeration<? extends ZipEntry> e = zf.entries(); e.hasMoreElements();) {
				ZipEntry entry = e.nextElement();
				ZipEntryBean entryBean = new ZipEntryBean();
				entryBean.set(entry);
				entries.add(entryBean);
			}
		}
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ZipEntryBean> getEntries() {
		return entries;
	}

	public void setEntries(List<ZipEntryBean> entries) {
		this.entries = entries;
	}

}
