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

import java.util.zip.ZipEntry;

import net.sf.okapi.lib.persistence.IPersistenceSession;
import net.sf.okapi.lib.persistence.PersistenceBean;

public class ZipEntryBean extends PersistenceBean<ZipEntry> {

	private String name;	// entry name
	private long time = -1;	// modification time (in DOS time)
	private long crc = -1;	// crc-32 of entry data
	private long size = -1;	// uncompressed size of entry data
	private long csize = -1;   	// compressed size of entry data
	private int method = -1;	// compression method
	private byte[] extra;       // optional extra field data for entry
	private String comment;     // optional comment string for entry
	private InputStreamBean inputStream = new InputStreamBean();

	@Override
	protected ZipEntry createObject(IPersistenceSession session) {
		return new ZipEntry(name);
	}

	@Override
	protected void fromObject(ZipEntry obj, IPersistenceSession session) {
		name = obj.getName();
		time = obj.getTime();
		crc = obj.getCrc();
		size = obj.getSize();
		csize = obj.getCompressedSize();
		method = obj.getMethod();
		extra = obj.getExtra();
		comment = obj.getComment();
	}

	@Override
	protected void setObject(ZipEntry obj, IPersistenceSession session) {
		obj.setComment(comment);
		//obj.setCompressedSize(csize); // !!! Do not uncomment, new compression size can be different, and an exception is thrown 
		obj.setCrc(crc);
		obj.setExtra(extra);
		//obj.setMethod(method); // !!! Do not uncomment, let the code decide
		obj.setSize(size);
		obj.setTime(time);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getCrc() {
		return crc;
	}

	public void setCrc(long crc) {
		this.crc = crc;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getCsize() {
		return csize;
	}

	public void setCsize(long csize) {
		this.csize = csize;
	}

	public int getMethod() {
		return method;
	}

	public void setMethod(int method) {
		this.method = method;
	}

	public byte[] getExtra() {
		return extra;
	}

	public void setExtra(byte[] extra) {
		this.extra = extra;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setInputStream(InputStreamBean inputStream) {
		this.inputStream = inputStream;
	}

	public InputStreamBean getInputStream() {
		return inputStream;
	}
}
