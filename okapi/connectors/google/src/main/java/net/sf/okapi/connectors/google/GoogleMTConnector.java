/*===========================================================================
  Copyright (C) 2008-2011 by the Okapi Framework contributors
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

package net.sf.okapi.connectors.google;

import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.query.MatchType;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.translation.BaseConnector;
import net.sf.okapi.common.query.QueryResult;
import net.sf.okapi.lib.translation.QueryUtil;

public class GoogleMTConnector extends BaseConnector {

	private static final String addressAjax = "http://ajax.googleapis.com/ajax/services/language/translate";
	private static final String baseQueryAjax = "?v=1.0&q=%s&langpair=%s|%s";

	private String hostId;
	private JSONParser parser;
	private QueryUtil util;

	public GoogleMTConnector () {
		util = new QueryUtil();
	}
	
	@Override
	public void close () {
		// Nothing to do
	}

	@Override
	public String getName () {
		return "Google-MT";
	}

	@Override
	public String getSettingsDisplay () {
		return "Server: " + addressAjax;
	}

	@Override
	public void open () {
		try {
			InetAddress thisIp = InetAddress.getLocalHost();
			hostId = "http://"+thisIp.getHostAddress();
			parser = new JSONParser();
		}
		catch ( UnknownHostException e ) {
			hostId = "http://unkown";
		}
	}

	@Override
	public int query (String plainText) {
		return query(new TextFragment(plainText));
	}
	
	@Override
	public int query (TextFragment text) {
		current = -1;
		return queryAjax(text);
	}

	private int queryAjax (TextFragment fragment) {
		try {
			// Check if there is actually text to translate
			if ( !fragment.hasText(false) ) return 0;
			// Convert the fragment to coded HTML
			String qtext = util.toCodedHTML(fragment);
			// To compile with Google TOS: no more than 5000 characters at a time
			if ( qtext.length() > 5000 ) {
				return 0; 
			}
			// Create the connection and query
			URL url = new URL(addressAjax + String.format(baseQueryAjax,
				URLEncoder.encode(qtext, "UTF-8"), srcCode, trgCode));
			URLConnection conn = url.openConnection();
			// To comply with Google TOS: Make sure we send a user-agent property
			conn.setRequestProperty("User-Agent", getClass().getName());
			// To comply with Google TOS: Make sure we send a referrer property
			conn.setRequestProperty("Referer", hostId); // With one 'r' for official RFC error

			// Get the response
			JSONObject object = (JSONObject)parser.parse(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>)object;
	    	@SuppressWarnings("unchecked")
	    	Map<String, Object> data = (Map<String, Object>)map.get("responseData");
	    	String res = (String)data.get("translatedText");
	        
			result = new QueryResult();
			result.weight = getWeight();
			result.source = fragment;
			if ( fragment.hasCode() ) {
				result.target = new TextFragment(util.fromCodedHTML(res, fragment, true),
					fragment.getClonedCodes());
			}
			else {
				result.target = new TextFragment(util.fromCodedHTML(res, fragment, true));
			}
			result.score = 95; // Arbitrary score for MT
			result.origin = getName();
			result.matchType = MatchType.MT;
			current = 0;
		}
		catch ( Throwable e ) {
			throw new RuntimeException("Error querying the server." + e.getMessage(), e);
		}
		return ((current==0) ? 1 : 0);
	}
	
	@Override
	protected String toInternalCode (LocaleId locale) {
		String code = locale.toBCP47();
		if ( !code.startsWith("zh") && ( code.length() > 2 )) {
			code = code.substring(0, 2);
		}
		return code;
	}	

}
