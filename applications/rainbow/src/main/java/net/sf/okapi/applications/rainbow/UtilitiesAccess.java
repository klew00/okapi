/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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

package net.sf.okapi.applications.rainbow;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.okapi.applications.rainbow.lib.Utils;
import net.sf.okapi.common.Util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class UtilitiesAccess {

	public static final int TYPE_UTILITY         = 0;
	public static final int TYPE_FILTER          = 1;
	public static final int TYPE_PARAMEDITOR     = 2;
	
	private LinkedHashMap<String, UtilitiesAccessItem> items;
	
	public UtilitiesAccess () {
		items = new LinkedHashMap<String, UtilitiesAccessItem>();
	}
	
	public boolean containsID (String id) {
		return items.containsKey(id);
	}
	
	//TODO: Maybe this needs to be an helper static method somewhere
	private Element getFirstElement (Element parent,
		String name)
	{
		NodeList nl = parent.getElementsByTagName(name);
		if (( nl == null ) || ( nl.getLength() == 0 )) return null;
		else return (Element)nl.item(0);
	}

	public Iterator<String> getIterator () {
		return items.keySet().iterator();
	}
	
	public void loadMenu (String path) {
		try {
			DocumentBuilderFactory Fact = DocumentBuilderFactory.newInstance();
			Fact.setValidating(false);
			Document doc = Fact.newDocumentBuilder().parse(new File(path));
			String prefLang = Utils.getCurrentLanguage(); 
			Element rootElem = doc.getDocumentElement();

			int sep = 0;
			NodeList nl = rootElem.getElementsByTagName("plugin");
			for ( int i=0; i<nl.getLength(); i++ ) {
				Element elem = (Element)nl.item(i);
				UtilitiesAccessItem item = new UtilitiesAccessItem();

				String type = elem.getAttribute("type");
				if ( type.equals("separator") ) {
					item.type = -1;
					item.id = String.format("__%d", ++sep);
					items.put(item.id, item);
					continue;
				}
				else if ( type.equals("utility") ) {
					item.type = TYPE_UTILITY;
				}
				else if ( type.equals("filter") ) {
					item.type = TYPE_FILTER;
				}
				else if ( type.equals("editor") ) {
					item.type = TYPE_PARAMEDITOR;
				}
				
				item.id = elem.getAttribute("id");
				if ( item.id.length() == 0 )
					throw new Exception("Attribute 'id' invalid or missing");
				item.pluginClass = elem.getAttribute("pluginClass");
				if ( item.pluginClass.length() == 0 )
					throw new Exception("Attribute 'pluginClass' invalid or missing");
				item.editorClass = elem.getAttribute("editorClass");

				int nDone = 0;
				NodeList infoList = elem.getElementsByTagName("info");
				for ( int j=0; j<infoList.getLength(); j++ ) {
					Element elemInfo = (Element)infoList.item(j);
					String lang = elemInfo.getAttribute("xml:lang");
					int n = 0;
					if ( Util.isSameLanguage(lang, prefLang, true) ) n = 3;
					else if ( Util.isSameLanguage(lang, prefLang, false) ) n = 2;
					else if ( lang.length() == 0 ) n = 1;
					if ( n > nDone ) {
						Element elem2 = getFirstElement(elemInfo, "name");
						item.name = Util.getTextContent(elem2);
						elem2 = getFirstElement(elemInfo, "description");
						item.description = Util.getTextContent(elem2);
						elem2 = getFirstElement(elemInfo, "provider");
						item.provider = Util.getTextContent(elem2);
						nDone = n;
					}
					if ( nDone == 3 ) break; // Best match found
				}

				// Add the new item to the list (overrides any existing one)
				items.put(item.id, item);
			}
		}
		catch ( Exception e ) {
			throw new RuntimeException(e);
		}
	}

	public UtilitiesAccessItem getItem (String id) {
		return items.get(id);
	}

}
