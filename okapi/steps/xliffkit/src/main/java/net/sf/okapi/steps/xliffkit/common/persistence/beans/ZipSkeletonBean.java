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

package net.sf.okapi.steps.xliffkit.common.persistence.beans;

import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.ZipSkeleton;
import net.sf.okapi.steps.xliffkit.common.persistence.IPersistenceBean;
import net.sf.okapi.steps.xliffkit.common.persistence.IPersistenceSession;

public class ZipSkeletonBean extends GenericSkeletonBean {

	private ZipFileBean original = new ZipFileBean();
	private String entry;
	
	@Override
	public <T> T get(T obj, IPersistenceSession session) {
		return super.get(obj, session);
	}
	
	@Override
	public <T> T get(Class<T> classRef, IPersistenceSession session) {		
		ZipSkeleton skel = null;
		ZipFile zf = null;
		ZipEntry ze = null;
		List<GenericSkeletonPartBean> parts = super.getParts();
		
		zf = original.get(ZipFile.class, session);		
		if (zf != null && entry != null)
			ze = zf.getEntry(entry);
		
		if (!Util.isEmpty(entry))
			if (zf != null) 
				ze = zf.getEntry(entry);
			else {
				ZipFile extZip = ZipFileBean.getZipFile();
				if (extZip != null) 
					ze = extZip.getEntry(entry);
			}
						
		if (!Util.isEmpty(parts))
			skel = new ZipSkeleton(super.get(GenericSkeleton.class, session), ze);
		else if (zf != null)
			skel = new ZipSkeleton(zf);
		else //if (ze != null)
			skel = new ZipSkeleton(ze);
						
		return classRef.cast(get(skel, session));
	}	

	@Override
	public IPersistenceBean set(Object obj, IPersistenceSession session) {
		super.set(obj, session);
		
		if (obj instanceof ZipSkeleton) {
			ZipSkeleton zs = (ZipSkeleton) obj;
			
			original.set(zs.getOriginal(), session);
			//entry.set(zs.getEntry());
			ZipEntry ze = zs.getEntry();
			if (ze != null)
				entry = ze.getName();
		}
		return this;
	}

	public ZipFileBean getOriginal() {
		return original;
	}

	public void setOriginal(ZipFileBean original) {
		this.original = original;
	}

	public String getEntry() {
		return entry;
	}

	public void setEntry(String entry) {
		this.entry = entry;
	}
}
