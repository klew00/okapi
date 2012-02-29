/*===========================================================================
  Copyright (C) 2012 by the Okapi Framework contributors
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

package net.sf.okapi.filters.drupal;

import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Node {

	private JSONObject store;
	
	public Node (JSONObject store) {
		this.store = store;
	}
	
	@SuppressWarnings("unchecked")
	public Node (String language,
		String type,
		String title,
		String body)
	{
		store = new JSONObject();
		store.put("language", language);
		store.put("type", type);
		store.put("title", title);
		setBody(language, body);
	}
	
	@SuppressWarnings("unchecked")
	public Node (String nid,
		String language,
		String type,
		String title,
		String body)
	{
		store = new JSONObject();
		store.put("nid", nid);
		store.put("language", language);
		store.put("type", type);
		store.put("title", title);
		setBody(language, body);
	}
	
	public String getNid () {
		return (String)store.get("nid");
	}
	
	public String getTitle () {
		return (String)store.get("title");
	}

	@SuppressWarnings("unchecked")
	public String getContent (String lang) {
		Object obj = store.get("body");
		if ( !(obj instanceof Map) ) {
			return null;
		}
		Map<String, JSONArray> map = (Map<String, JSONArray>)store.get("body");
		// Look for the source
		JSONArray data = map.get(lang);
		if ( data == null ) {
			data = map.get("und");
			if ( data == null ) {
				return null;
			}
		}
		JSONObject cnt = (JSONObject)data.get(0);
		String value = (String)cnt.get("value");
		return value;
	}

	@SuppressWarnings("unchecked")
	public String getSummary (String lang) {
		Object obj = store.get("body");
		if ( !(obj instanceof Map) ) {
			return null;
		}
		Map<String, JSONArray> map = (Map<String, JSONArray>)store.get("body");
		// Look for the source summary
		JSONArray data = map.get(lang);
		if ( data == null ) {
			data = map.get("und");
			if ( data == null ) {
				return null;
			}
		}
		JSONObject cnt = (JSONObject)data.get(0);
		String value = (String)cnt.get("summary");
		return value;
	}

	@Override
	public String toString () {
		return store.toJSONString();
	}
	
	public void setBody (String lang, String body) {
		setBody(lang, body, null);
	}
	
	@SuppressWarnings("unchecked")
	public void setBody (String lang, String body, String summary) {

		JSONObject bodyVal = new JSONObject();
		bodyVal.put("value", body);
		
		if(summary != null){
			bodyVal.put("summary", summary);
		}
		
		JSONArray bodyArr = new JSONArray();
		bodyArr.add(bodyVal);
		
		JSONObject bodyLang = new JSONObject();
		bodyLang.put(lang, bodyArr);
		
		store.put("body", bodyLang);
	}
	
	/**
	 * NOTE: Title is currently treated as a top level attribute.
	 *       With the title translation module it would follow the body format.
	 * @param title
	 */
	@SuppressWarnings("unchecked")
	public void setTitle (String title) {
		store.put("title", title);
	}
}
