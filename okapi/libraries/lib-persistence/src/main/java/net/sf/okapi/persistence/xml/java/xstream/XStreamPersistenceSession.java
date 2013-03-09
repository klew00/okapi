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

package net.sf.okapi.persistence.xml.java.xstream;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import com.thoughtworks.xstream.XStream;

import net.sf.okapi.common.MimeTypeMapper;
import net.sf.okapi.common.XMLWriter;
import net.sf.okapi.lib.persistence.IPersistenceBean;
import net.sf.okapi.lib.persistence.PersistenceSession;

public abstract class XStreamPersistenceSession extends PersistenceSession {

	private XStream xstream = new XStream();
	private XMLWriter writer; 
	private PrintWriter prWriter;
	//private OutputStream outStream;
	
	@Override
	protected void endReading(InputStream inStream) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void endWriting(OutputStream outStream) {
		writer.writeEndElementLineBreak(); // body
		writer.writeEndDocument();
		writer.close();
	}

	@Override
	protected <T extends IPersistenceBean<?>> T readBean(Class<T> beanClass,
			String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void startReading(InputStream inStream) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void startWriting(OutputStream outStream) {		
		//this.outStream = outStream;
		prWriter = new PrintWriter(outStream);
		writer = new XMLWriter(prWriter);
		writer.writeStartDocument();
		writer.writeStartElement("body");
		writer.writeLineBreak();
	}

	@Override
	protected void writeBean(IPersistenceBean<?> bean, String name) {
		xstream.toXML(bean, prWriter);
	}

	@Override
	public <T extends IPersistenceBean<?>> T convert(Object obj,
			Class<T> expectedClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMimeType() {
		return MimeTypeMapper.XML_MIME_TYPE;
	}

	@Override
	protected String writeBeanToString(IPersistenceBean<?> bean) {
		return null;
	}
	
	@Override
	protected <T extends IPersistenceBean<?>> T readBeanFromString(
			String content, Class<T> beanClass) {
		return null;
	}
}
