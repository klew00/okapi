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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.oasisopen.xliff.v2.IExtendedAttribute;
import org.oasisopen.xliff.v2.IExtendedAttributes;

public class ExtendedAttributes implements IExtendedAttributes {

	private static final long serialVersionUID = 0100L;
	
	private ArrayList<IExtendedAttribute> attrs;
	private Map<String, String> namespaces;

	@Override
	public String getAttributeValue (String namespaceURI,
		String localName)
	{
		if ( attrs == null ) return null;
		for ( IExtendedAttribute att : attrs ) {
			if ( att.getLocalPart().equals(localName)
				&& att.getNamespaceURI().equals(namespaceURI) ) {
				return att.getValue();
			}
		}
		return null;
	}
	
	@Override
	public IExtendedAttribute getAttribute (String namespaceURI,
		String localName)
	{
		if ( attrs == null ) return null;
		for ( IExtendedAttribute att : attrs ) {
			if ( att.getLocalPart().equals(localName)
				&& att.getNamespaceURI().equals(namespaceURI) ) {
				return att;
			}
		}
		return null;
	}
	
	@Override
	public IExtendedAttribute setAttribute (IExtendedAttribute attribute) {
		if ( attrs == null ) {
			attrs = new ArrayList<IExtendedAttribute>();
		}
		int pos = 0;
		for ( IExtendedAttribute att : attrs ) {
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

	@Override
	public IExtendedAttribute setAttribute (String namespaceURI,
		String localName,
		String value)
	{
		if ( attrs == null ) {
			attrs = new ArrayList<IExtendedAttribute>();
		}
		IExtendedAttribute att = getAttribute(namespaceURI, localName);
		if ( att == null ) {
			att = new ExtendedAttribute(new QName(namespaceURI, localName), value);
			attrs.add(att);
		}
		att.setValue(value);
		return att;
	}
	
	@Override
	public void deleteAttribute (String namespaceURI,
		String localName)
	{
		if ( attrs == null ) return;
		IExtendedAttribute att = getAttribute(namespaceURI, localName);
		if ( att == null ) return;
		attrs.remove(att);
	}

	@Override
	public int size () {
		if ( attrs == null ) return 0;
		return attrs.size();
	}

	@Override
	public Iterator<IExtendedAttribute> iterator () {
		return new Iterator<IExtendedAttribute>() {
			int current = 0;

			@Override
			public void remove () {
				throw new UnsupportedOperationException("The method remove() not supported.");
			}

			@Override
			public IExtendedAttribute next () {
				return attrs.get((++current)-1);
			}

			@Override
			public boolean hasNext () {
				return (( attrs != null ) && !attrs.isEmpty() && ( current < attrs.size() ));
			}
		};
	}

	@Override
	public void setNamespace (String prefix,
		String namespaceURI)
	{
		if ( namespaces == null ) {
			namespaces = new LinkedHashMap<String, String>();
		}
		namespaces.put(namespaceURI, prefix);
	}

	@Override
	public String getNamespacePrefix (String namespaceURI) {
		if ( namespaces == null ) return null;
		return namespaces.get(namespaceURI);
	}
	
	@Override
	public boolean hasNamespace () {
		if ( namespaces == null ) return false;
		return (namespaces.size() > 0);
	}

	@Override
	public Set<String> getNamespaces () {
		if ( namespaces == null ) return Collections.emptySet();
		return namespaces.keySet();
	}

}
