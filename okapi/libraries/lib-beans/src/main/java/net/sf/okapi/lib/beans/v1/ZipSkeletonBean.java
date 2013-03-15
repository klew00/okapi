/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.ZipSkeleton;
import net.sf.okapi.lib.persistence.IPersistenceSession;
import net.sf.okapi.lib.persistence.beans.FactoryBean;

public class ZipSkeletonBean extends GenericSkeletonBean {
	
	//private ZipFileBean original = new ZipFileBean();
	private FactoryBean original = new FactoryBean();
	private String entry;

	@Override
	protected GenericSkeleton createObject(IPersistenceSession session) {
		ZipFile zipFile = null;
		ZipEntry zipEntry = null;
		//List<GenericSkeletonPartBean> parts = super.getParts();
		
		zipFile = original.get(ZipFile.class, session);		
		if (zipFile != null && !Util.isEmpty(entry))
			zipEntry = zipFile.getEntry(entry);
		
		//return new ZipSkeleton(this.get(GenericSkeleton.class, session), zipFile, zipEntry);  
		return new ZipSkeleton(super.createObject(session), zipFile, zipEntry);
	}

	@Override
	protected void fromObject(GenericSkeleton obj, IPersistenceSession session) {		
		super.fromObject(obj, session);
		
		if (obj instanceof ZipSkeleton) {
			ZipSkeleton zs = (ZipSkeleton) obj;
			
			original.set(zs.getOriginal(), session);
			//entry.set(zs.getEntry());
			ZipEntry ze = zs.getEntry();
			if (ze != null)
				entry = ze.getName();
		}
	}

	@Override
	protected void setObject(GenericSkeleton obj, IPersistenceSession session) {
		super.setObject(obj, session);
	}
	
	public FactoryBean getOriginal() {
		return original;
	}

	public void setOriginal(FactoryBean original) {
		this.original = original;
	}

	public String getEntry() {
		return entry;
	}

	public void setEntry(String entry) {
		this.entry = entry;
	}
}
