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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

public class ExtendedAttributes implements Serializable, Iterable<ExtendedAttribute> {

	private static final long serialVersionUID = 0100L;
	
	private ArrayList<ExtendedAttribute> attrs;
	private Map<String, String> namespaces;

	public String getAttributeValue (String namespaceURI,
		String localName)
	{
		if ( attrs == null ) return null;
		for ( ExtendedAttribute att : attrs ) {
			if ( att.getLocalPart().equals(localName)
				&& att.getNamespaceURI().equals(namespaceURI) ) {
				return att.getValue();
			}
		}
		return null;
	}
	
	public ExtendedAttribute getAttribute (String namespaceURI,
		String localName)
	{
		if ( attrs == null ) return null;
		for ( ExtendedAttribute att : attrs ) {
			if ( att.getLocalPart().equals(localName)
				&& att.getNamespaceURI().equals(namespaceURI) ) {
				return att;
			}
		}
		return null;
	}
	
	public ExtendedAttribute setAttribute (ExtendedAttribute attribute) {
		if ( attrs == null ) {
			attrs = new ArrayList<ExtendedAttribute>();
		}
		int pos = 0;
		for ( ExtendedAttribute att : attrs ) {
			if ( att.getLocalPart().equals(attribute.getLocalPart())
				&& att.getNamespaceURI().equals(attribute.getNamespaceURI()) ) {
				attrs.set(pos, attribute);
				return attribute;
			}
			pos++;
		}
		attrs.add(attribute);
		return attribute;
	}

	public ExtendedAttribute setAttribute (String namespaceURI,
		String localName,
		String value)
	{
		if ( attrs == null ) {
			attrs = new ArrayList<ExtendedAttribute>();
		}
		ExtendedAttribute att = getAttribute(namespaceURI, localName);
		if ( att == null ) {
			att = new ExtendedAttribute(new QName(namespaceURI, localName), value);
			attrs.add(att);
		}
		att.setValue(value);
		return att;
	}
	
	public void deleteAttribute (String namespaceURI,
		String localName)
	{
		if ( attrs == null ) return;
		ExtendedAttribute att = getAttribute(namespaceURI, localName);
		if ( att == null ) return;
		attrs.remove(att);
	}

	public int size () {
		if ( attrs == null ) return 0;
		return attrs.size();
	}

	@Override
	public Iterator<ExtendedAttribute> iterator () {
		return new Iterator<ExtendedAttribute>() {
			int current = 0;

			@Override
			public void remove () {
				throw new UnsupportedOperationException("The method remove() not supported.");
			}

			@Override
			public ExtendedAttribute next () {
				return attrs.get((++current)-1);
			}

			@Override
			public boolean hasNext () {
				return (( attrs != null ) && !attrs.isEmpty() && ( current < attrs.size() ));
			}
		};
	}

	public void setNamespace (String prefix,
		String namespaceURI)
	{
		if ( namespaces == null ) {
			namespaces = new LinkedHashMap<String, String>();
		}
		namespaces.put(namespaceURI, prefix);
	}

	public String getNamespacePrefix (String namespaceURI) {
		if ( namespaces == null ) return null;
		return namespaces.get(namespaceURI);
	}
	
	public boolean hasNamespace () {
		if ( namespaces == null ) return false;
		return (namespaces.size() > 0);
	}

	public Set<String> getNamespaces () {
		if ( namespaces == null ) return Collections.emptySet();
		return namespaces.keySet();
	}

}
