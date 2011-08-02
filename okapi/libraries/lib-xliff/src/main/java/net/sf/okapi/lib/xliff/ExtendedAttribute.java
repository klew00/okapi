/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package net.sf.okapi.lib.xliff;

import java.io.Serializable;

import javax.xml.namespace.QName;

public class ExtendedAttribute implements Serializable {

	private static final long serialVersionUID = 0100L;

	private QName qname;
	private String value;

	public ExtendedAttribute (QName qname,
		String value)
	{
		this.qname = qname;
		this.value = value;
	}

	public QName getQName () {
		return qname;
	}
	
	public String getValue () {
		return value;
	}
	
	public void setValue (String value) {
		this.value = value;
	}
	
	public String getLocalPart () {
		return qname.getLocalPart();
	}
	
	public String getNamespaceURI () {
		return qname.getNamespaceURI();
	}
	
	public String getPrefix () {
		return qname.getPrefix();
	}

}
