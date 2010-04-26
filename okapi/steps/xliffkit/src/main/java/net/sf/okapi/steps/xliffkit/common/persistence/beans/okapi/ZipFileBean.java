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

package net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import net.sf.okapi.common.Util;
import net.sf.okapi.steps.xliffkit.common.persistence.IPersistenceSession;
import net.sf.okapi.steps.xliffkit.common.persistence.PersistenceBean;

public class ZipFileBean extends PersistenceBean<ZipFile> {

	private String name;  // ZIP file short name
	private List<ZipEntryBean> entries = new ArrayList<ZipEntryBean>(); // enumeration of the ZIP file entries
	boolean empty = true;
	
	private static ZipFile zipFile;

	@Override
	protected ZipFile createObject(IPersistenceSession session) {
		if (Util.isEmpty(name) || empty) return null;
		
		File tempZip = null;
		try {
			tempZip = File.createTempFile("~temp", ".zip");
		} catch (IOException e) {
			// TODO Handle exception
			e.printStackTrace();
		}
		if (tempZip.exists()) tempZip.delete();			
		tempZip.deleteOnExit();
		
		try {
			ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(tempZip.getAbsolutePath()));
			for (ZipEntryBean entryBean : entries) {
				ZipEntry entry = entryBean.get(ZipEntry.class, session);
				//System.out.println(entry.getName());
				zipOut.putNextEntry(entry);
				if (entryBean.getInputStream() != null)
					zipOut.write(entryBean.getInputStream().getData()); // entryBean.getInputStream().getData().length 
				zipOut.closeEntry();				
			}
			zipOut.close();
		} catch (FileNotFoundException e1) {
			// TODO Handle exception
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Handle exception
			e.printStackTrace();
		}
		
		try {
			zipFile = new ZipFile(tempZip);
		} catch (ZipException e) {
			// TODO Handle exception
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Handle exception
			e.printStackTrace();
		}
		return zipFile;
	}

	@Override
	protected void fromObject(ZipFile obj, IPersistenceSession session) {
		if (obj == null) return;
		
		name = Util.getFilename(obj.getName(), true);
		
		for (Enumeration<? extends ZipEntry> e = obj.entries(); e.hasMoreElements();) {
			ZipEntry entry = e.nextElement();
			//System.out.println(entry.getName());
			
			ZipEntryBean entryBean = new ZipEntryBean();
			entryBean.set(entry, session);
			InputStreamBean isBean = entryBean.getInputStream();
			try {
				isBean.set(obj.getInputStream(entry), session);
			} catch (IOException e1) {
				// TODO Handle exception
				e1.printStackTrace();
			}
			entries.add(entryBean);
		}
		empty = Util.isEmpty(name) || Util.isEmpty(entries);
	}

	@Override
	protected void setObject(ZipFile obj, IPersistenceSession session) {		
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

	public static ZipFile getZipFile() {
		return zipFile;
	}

	public boolean isEmpty() {
		return empty;
	}

	public void setEmpty(boolean empty) {
		this.empty = empty;
	}
}
