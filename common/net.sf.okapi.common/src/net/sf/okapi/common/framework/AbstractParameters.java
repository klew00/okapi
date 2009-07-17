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

package net.sf.okapi.common.framework;

import net.sf.okapi.common.BaseParameters;

/**
 * 
 * 
 * @version 0.1, 18.06.2009
 */

public abstract class AbstractParameters extends BaseParameters implements INotifiable {

	protected Component owner = null;
	protected String data; // Available in parameters_load() 
	
	public AbstractParameters() {
		
		super();		
		
		parameters_init();
		reset();
		toString(); // fill the list
	}
	
	/**
	 * Called from the parameters' constructor. Create local objects here or leave empty. Initial values are assigned in reset().
	 */
	abstract protected void parameters_init();
	
	/**
	 * Reset parameters values to defaults.
	 */
	abstract protected void parameters_reset();
	
	/**
	 * Load from buffer. The protected buffer variable is visible in all subclasses of BaseParameters.<p>
	 * @example myParam = buffer.getBoolean("myParam", false);
	 */
	abstract protected void parameters_load();
	
	/**
	 * Save to buffer. The protected buffer variable is visible in all subclasses of BaseParameters.<p>
	 * @example buffer.setBoolean("myParam", myParam);
	 */
	abstract protected void parameters_save();
	
	
	final public void reset() {
		
		parameters_reset();
	}
	
	final public void fromString(String data) {
		
		reset();
		this.data = data; 
		buffer.fromString(data);

		parameters_load();
	}
	
	final public String toString () {
		
		buffer.reset();
		parameters_save();
		
		return buffer.toString();
	}
	
	public boolean exec(Object sender, String command, Object info) {
				
		if (command.equalsIgnoreCase(Notification.PARAMETERS_SET_OWNER)) {
			
			if (info instanceof Component)
				owner = (Component) info;
			
			return true;
		}
		return false;
	}
}

