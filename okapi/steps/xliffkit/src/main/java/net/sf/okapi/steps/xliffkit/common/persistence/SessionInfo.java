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

package net.sf.okapi.steps.xliffkit.common.persistence;


public class SessionInfo implements IPersistenceBean {
	
	private String mimeType;
	private String rootClass;
	private String version;
	private String description;
	
	@Override
	public <T> T get(T obj) {
		return obj;
	}

	@Override
	public <T> T get(Class<T> classRef) {
		return classRef.cast(this);
	}

	@Override
	public IPersistenceBean set(Object obj) {
		if (obj instanceof IPersistenceSession) {
			IPersistenceSession session = (IPersistenceSession) obj;
			version = session.getVersion();
			mimeType = session.getMimeType();
			rootClass = session.getRootClass();
			description = session.getDescription();
		}
		return this;
	}

		public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setRootClass(String rootClass) {
		this.rootClass = rootClass;
	}

	public String getRootClass() {
		return rootClass;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return version;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
