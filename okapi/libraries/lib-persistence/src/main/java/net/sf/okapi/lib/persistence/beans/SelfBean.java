/*===========================================================================
  Copyright (C) 2008-2013 by the Okapi Framework contributors
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

package net.sf.okapi.lib.persistence.beans;

import net.sf.okapi.lib.persistence.IPersistenceSession;
import net.sf.okapi.lib.persistence.PersistenceBean;

/**
 * Used as the base class for the objects that can be serialized as are (all getters/setters access only simple types and structures of simple types and no full-blown bean is needed)
 * Extend this class for the classes that contain only serializable fields.
 * !!! Important: in the subclasses provide getters/setters for the internal fields to be serialized. 
 */
public class SelfBean extends PersistenceBean<Object> {

	@Override
	protected Object createObject(IPersistenceSession session) {
		return this;
	}
	
	@Override
	protected void setObject(Object obj,
			IPersistenceSession session) {		
	}

	@Override
	protected void fromObject(Object obj,
			IPersistenceSession session) {
	}
}
