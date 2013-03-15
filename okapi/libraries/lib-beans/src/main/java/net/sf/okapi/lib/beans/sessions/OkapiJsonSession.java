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

package net.sf.okapi.lib.beans.sessions;

import net.sf.okapi.common.Event;
import net.sf.okapi.lib.beans.v0.PersistenceMapper;
import net.sf.okapi.lib.beans.v1.OkapiBeans;
import net.sf.okapi.lib.persistence.VersionMapper;
import net.sf.okapi.persistence.json.jackson.JSONPersistenceSession;

public class OkapiJsonSession extends JSONPersistenceSession {

	@Override
	public void registerVersions() {
		VersionMapper.registerVersion(PersistenceMapper.class); // v0
		VersionMapper.registerVersion(OkapiBeans.class);		// v1
	}

	@Override
	protected Class<?> getDefItemClass() {
		return Event.class;
	}

	@Override
	protected String getDefItemLabel() {
		return "event";
	}

	@Override
	protected String getDefVersionId() {
		return OkapiBeans.VERSION;
	}
}
