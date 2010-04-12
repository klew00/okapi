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

package net.sf.okapi.steps.xliffkit.common.persistence.beans;

import net.sf.okapi.common.MimeTypeMapper;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.filterwriter.ZipFilterWriter;
import net.sf.okapi.steps.xliffkit.common.persistence.IPersistenceBean;

public class ZipFilterWriterBean implements IPersistenceBean {

	@Override
	public <T> T get(T obj) {
		return obj;
	}

	@Override
	public <T> T get(Class<T> classRef) {
		EncoderManager encoderManager = new EncoderManager();
		encoderManager.setMapping(MimeTypeMapper.ODF_MIME_TYPE, "net.sf.okapi.common.encoder.XMLEncoder");
		return classRef.cast(get(new ZipFilterWriter(encoderManager)));
	}

	@Override
	public IPersistenceBean set(Object obj) {
		return this;
	}

}
