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
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Node {

	private final Logger logger = Logger.getLogger(getClass().getName());
	
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
		setTitle(language, title);
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
		setTitle(language, title);
		setBody(language, body);
	}
	
	public String getNid () {
		return (String)store.get("nid");
	}
	
/*	public String getTitle () {
		return (String)store.get("title");
	}*/
	
	@SuppressWarnings("unchecked")
	public String getTitle (String lang) {
		Object obj = store.get("title_field");
		if ( !(obj instanceof Map) ) {
			return null;
		}
		Map<String, JSONArray> map = (Map<String, JSONArray>)store.get("title_field");
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
		setBody(lang, body, null, true);
	}
	
	public void setBody (String lang, String body, boolean neutralLikeSource) {
		setBody(lang, body, null, neutralLikeSource);
	}
	
	@SuppressWarnings("unchecked")
	public void setBody (String lang, String body, String summary, boolean neutralLikeSource) {

		//if existing update the fields else create field from scratch
		JSONObject field = (JSONObject) store.get("body");
		if(field != null){
			
			//--check language
			if(!field.containsKey(lang)){
				
				if(field.containsKey("und")){
					if(neutralLikeSource){
						lang = "und";
					}else{
						logger.warning("Specified language not found for current body. Content will not be updated. Enable the neutralLikeSource option to write back to \"undefined\" language.");
						return;
					}
				}else{
					//--neither the specified language or an undefined language exists
					//--TODO: update if we're able to create new targets--
					logger.warning("Specified language not found for current body. Content will not be updated.");
					return;
				}
			}	
			
			JSONArray langField = (JSONArray) field.get(lang);
			JSONObject arrayObj = (JSONObject) langField.get(0);
			
			//--overwrite value
			arrayObj.put("value", body);
			arrayObj.put("summary", summary);

		}else{
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
	}
	
	public void setTitle (String lang, String title) {
		setTitle(lang, title, true);
	}
	
	@SuppressWarnings("unchecked")
	public void setTitle (String lang, String title, boolean neutralLikeSource) {

		//if existing update the fields else create field from scratch
		JSONObject field = (JSONObject) store.get("title_field");
		if(field != null){
			
			//--check language
			if(!field.containsKey(lang)){
				if(field.containsKey("und")){
					if(neutralLikeSource){
						lang = "und";
					}else{
						logger.warning("Specified language not found for current title. Content will not be updated. Enable the neutralLikeSource option to write back to \"undefined\" language.");
						return;
					}
				}else{
					//--neither the specified language or an undefined language exists
					//--TODO: update if we're able to create new targets--
					logger.warning("Specified language not found for current title. Content will not be updated.");
					return;
				}
			}
			
			JSONArray langField = (JSONArray) field.get(lang);
			JSONObject arrayObj = (JSONObject) langField.get(0);
			
			//--overwrite value
			arrayObj.put("value", title);
			
		}else{

			JSONObject titleVal = new JSONObject();
			titleVal.put("value", title);
			
			JSONArray titleArr = new JSONArray();
			titleArr.add(titleVal);
			
			JSONObject titleLang = new JSONObject();
			titleLang.put(lang, titleArr);
			
			store.put("title_field", titleLang);
		}
	}
}
