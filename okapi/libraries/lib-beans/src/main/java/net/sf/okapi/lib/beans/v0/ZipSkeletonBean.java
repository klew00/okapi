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

package net.sf.okapi.lib.beans.v0;

import java.util.zip.ZipEntry;

import net.sf.okapi.common.skeleton.ZipSkeleton;
import net.sf.okapi.lib.beans.v0.IPersistenceBean;
import net.sf.okapi.lib.beans.v0.IPersistenceSession;

public class ZipSkeletonBean implements IPersistenceBean {

	private String zipFileName;
	//private byte[] bytes = new byte[] {3, 5, 120, 127};
	//private TextContainerBean testBean = new TextContainerBean();
	
	@Override
	public void init(IPersistenceSession session) {
	}
	
	@Override
	public IPersistenceBean set(Object obj) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public <T> T get(Class<T> classRef) {
		ZipSkeleton skel = new ZipSkeleton(null, new ZipEntry(""));
		
		return classRef.cast(skel);
	}

	public void setZipFileName(String zipFileName) {
		this.zipFileName = zipFileName;
	}

	public String getZipFileName() {
		return zipFileName;
	}

//	public byte[] getBytes() {
//		return bytes;
//	}
//
//	public void setBytes(byte[] bytes) {
//		this.bytes = bytes;
//	}
//
//	public void setTestBean(TextContainerBean testBean) {
//		this.testBean = testBean;
//	}
//
//	public TextContainerBean getTestBean() {
//		return testBean;
//	}	

}
