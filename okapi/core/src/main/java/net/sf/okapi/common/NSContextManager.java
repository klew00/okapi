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

package net.sf.okapi.common;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

/**
 * Helper class to manage XML namespaces.
 * The reserved <code>xml</code> and <code>xmlns</code> prefixes are hard-coded.
 * <p>The following namespaces are pre-defined:
 * <ul><li>xml : http://www.w3.org/XML/1998/namespace</li>
 * <li>xmlns : http://www.w3.org/2000/xmlns/</li>
 * <li>xsl : http://www.w3.org/1999/XSL/Transform</li>
 * </ul>
 */
public class NSContextManager implements NamespaceContext {

	private Hashtable<String, String> table;
	
	/**
	 * Creates a new NSContextManager object.
	 */
	public NSContextManager () {
		table = new Hashtable<String, String>();
		// Defaults in addition to the hard-coded
		add("xsl", "http://www.w3.org/1999/XSL/Transform");
	}
	
	/**
	 * Gets the namespace URI for a given prefix.
	 * @param prefix The prefix to look up.
	 * @return The namespace URI for the given prefix, or XMLConstants.NULL_NS_URI if none was found. 
	 */
	public String getNamespaceURI (String prefix) {
		if ( table.containsKey(prefix) )
			return table.get(prefix);
		if ( prefix.equals(XMLConstants.XML_NS_PREFIX) )
			return XMLConstants.XML_NS_URI;
		if ( prefix.equals(XMLConstants.XMLNS_ATTRIBUTE) )
			return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
		// Default
		return XMLConstants.NULL_NS_URI;
	}

	/**
	 * Gets the prefix for a given namespace URI.
	 * @param uri The namespace URI to look up.
	 * @return The prefix for the given namespace URI, or null the mamager has no such
	 * namespace URI defined.
	 */
	public String getPrefix (String uri) {
		Enumeration<String> E = table.keys();
		String key;
		while ( E.hasMoreElements() ) {
			key = E.nextElement();
			if ( table.get(key).equals(uri) )
				return key;
		}
		if ( uri.equals(XMLConstants.XML_NS_URI))
			return XMLConstants.XML_NS_PREFIX;
		if ( uri.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI) )
			return XMLConstants.XMLNS_ATTRIBUTE;
		else
			return null;
	}

	/**
	 * Not implemented.
	 * @return Always null.
	 */
	public Iterator<String> getPrefixes (String uri) {
		// Not implemented
		return null;
	} 

	/**
	 * Adds a prefix/uri pair to the manager. No checking is done for existing
	 * prefix: If the same is already defined, it will be overwritten.
	 * @param prefix The prefix of the namespace
	 * @param uri The uri of the namespace
	 */
	public void add (String prefix,
		String uri) {
		table.put(prefix, uri);
	}

}
