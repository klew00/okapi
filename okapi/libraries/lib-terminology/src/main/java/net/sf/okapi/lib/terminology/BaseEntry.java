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

package net.sf.okapi.lib.terminology;

import java.util.LinkedHashMap;

import net.sf.okapi.common.resource.Property;

public class BaseEntry {
	
	protected LinkedHashMap<String, Property> props;

	public void setProperty (Property property) {
		if ( props == null ) props = new LinkedHashMap<String, Property>();
		props.put(property.getName(), property);
	}
	
	public Property getProperty (String name) {
		if ( props == null ) return null;
		return props.get(name);
	}

	public void setProperties (LinkedHashMap<String, Property> props) {
		this.props = props;
	}

	public Iterable<String> propertiesKeys () {
		return props.keySet();
	}

}
