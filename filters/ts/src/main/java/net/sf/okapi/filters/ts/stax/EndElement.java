/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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
============================================================================*/

package net.sf.okapi.filters.ts.stax;

import javax.xml.stream.XMLStreamReader;

import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.filters.ts.stax.StaxObject;

public class EndElement implements StaxObject{
	String namespace;
	String localname;
	
	public EndElement(XMLStreamReader reader){
		readObject(reader);
	}

	public EndElement(String localname) {
		this.namespace = "";
		this.localname = localname;
	}

	public void readObject(XMLStreamReader reader){
		this.namespace = reader.getPrefix();
		this.localname = reader.getLocalName();
	}
	
	public String toString(){
		if (( namespace == null ) || ( namespace.length()==0 ))
			return "</"+localname+">";
		else
			return "</"+namespace+":"+localname+">";
	}
	
	public GenericSkeleton getSkeleton(){
		if (( namespace == null ) || ( namespace.length()==0 ))
			return new GenericSkeleton("</"+localname+">");
		else
			return new GenericSkeleton("</"+namespace+":"+localname+">");
	}
}