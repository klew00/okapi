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

import java.util.LinkedList;

import javax.xml.stream.XMLStreamReader;

import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.filters.ts.stax.Attribute;
import net.sf.okapi.filters.ts.stax.NameSpace;
import net.sf.okapi.filters.ts.stax.StaxObject;

public class StartElement implements StaxObject{
	
	protected String namespace;
	protected String localname;	
	protected LinkedList <NameSpace> namespaces;
	protected LinkedList <Attribute> attributes;
	
	public StartElement(XMLStreamReader reader){
		this.namespaces = new LinkedList<NameSpace>();
		this.attributes = new LinkedList<Attribute>();
		readObject(reader);
	}
	
	public StartElement(String localname){
		this.namespaces = new LinkedList<NameSpace>();
		this.attributes = new LinkedList<Attribute>();
		this.namespace = "";
		this.localname = localname;
	}
	
	public void readObject(XMLStreamReader reader){
		this.namespace = reader.getPrefix();
		this.localname = reader.getLocalName();

		for ( int i=0; i<reader.getNamespaceCount(); i++ ) {
			namespaces.add(new NameSpace(reader.getNamespacePrefix(i),reader.getNamespaceURI(i)));
		}

		for ( int i=0; i<reader.getAttributeCount(); i++ ) {
			if ( !reader.isAttributeSpecified(i) ) continue; // Skip defaults
			attributes.add(new Attribute(reader.getAttributePrefix(i),reader.getAttributeLocalName(i),reader.getAttributeValue(i)));
		}
	}
	

	public String toString(){

		StringBuilder sb = new StringBuilder();
		
		if (( namespace == null ) || ( namespace.length()==0 ))
			sb.append("<"+localname);
		else
			sb.append("<"+namespace+":"+localname);

		for(NameSpace ns: namespaces){
			sb.append(String.format(" xmlns%s=\"%s\"",
					((ns.prefix.length()>0) ? ":"+ns.prefix : ""),
					ns.uri));
		}
		
		for(Attribute attr: attributes){
			sb.append(String.format(" %s%s=\"%s\"",
					(((attr.prefix==null)||(attr.prefix.length()==0)) ? "" : attr.prefix+":"),
					attr.localname,
					attr.value));
		}

		sb.append(">");
		return sb.toString();
	}
	
	/**
	 * Generates the skeleton for the start element.
	 * @return the generated GenericSkeleton. 
	 */	
	public GenericSkeleton getSkeleton(){

		GenericSkeleton skel = new GenericSkeleton();
		
		if (( namespace == null ) || ( namespace.length()==0 ))
			skel.append("<"+localname);
		else
			skel.append("<"+namespace+":"+localname);

		for(NameSpace ns: namespaces){
			skel.append(String.format(" xmlns%s=\"%s\"",
					((ns.prefix.length()>0) ? ":"+ns.prefix : ""),
					ns.uri));
		}
		
		for(Attribute attr: attributes){
			skel.append(String.format(" %s%s=\"%s\"",
					(((attr.prefix==null)||(attr.prefix.length()==0)) ? "" : attr.prefix+":"),
					attr.localname,
					attr.value));
		}

		skel.append(">");
		return skel;
	}
}