/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.filters.common.framework;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.framework.INotifiable;
import net.sf.okapi.common.framework.Notification;


/**
 * 
 * 
 * @version 0.1, 18.06.2009
 * @author Sergei Vasilyev
 */

public abstract class AbstractParameters extends BaseParameters implements INotifiable {

	protected INotifiable owner = null;
	
	public boolean exec(String command, Object info) {
		
		
		if (command.equalsIgnoreCase(Notification.PARAMETERS_SET_OWNER)) {
			
			if (info instanceof INotifiable)
				owner = (INotifiable) info;
			
			return true;
		}
		return false;
	}
}

