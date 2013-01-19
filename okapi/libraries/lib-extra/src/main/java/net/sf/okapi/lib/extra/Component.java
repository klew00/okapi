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

package net.sf.okapi.lib.extra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.common.Util;

/**
 * 
 * 
 * @version 0.1 08.07.2009
 */

public class Component implements IComponent, INotifiable {

	private String name;
	private String description;	
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public Component() {
		super();		
	}

	public Component(String name, String description) {
		super();
		
		this.name = name;
		this.description = description;
	}

	protected void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	protected void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		//return this.getClass().getName();
		return description;
	}

	@Override
	public String toString() {
		if (!Util.isEmpty(name) && !Util.isEmpty(description))
			return String.format("%s [%s]", name, description);
		else if (!Util.isEmpty(name))
			return name;
		else
			return super.toString();
	}

	public boolean exec(Object sender, String command, Object info) {
		return false;
	}

	
}
