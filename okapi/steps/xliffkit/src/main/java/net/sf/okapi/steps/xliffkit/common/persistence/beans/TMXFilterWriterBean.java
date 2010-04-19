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
import net.sf.okapi.common.filterwriter.GenericFilterWriter;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;
import net.sf.okapi.steps.xliffkit.common.persistence.IPersistenceSession;
import net.sf.okapi.steps.xliffkit.common.persistence.PersistenceBean;

public class TMXFilterWriterBean extends PersistenceBean {

	@Override
	protected Object createObject(IPersistenceSession session) {
		EncoderManager encoderManager = new EncoderManager();
		encoderManager.setMapping(MimeTypeMapper.TMX_MIME_TYPE, "net.sf.okapi.common.encoder.XMLEncoder");
		return new GenericFilterWriter(new GenericSkeletonWriter(), encoderManager);
	}

	@Override
	protected void fromObject(Object obj, IPersistenceSession session) {
	}

	@Override
	protected void setObject(Object obj, IPersistenceSession session) {
	}
}
