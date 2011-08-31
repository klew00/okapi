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

package net.sf.okapi.connectors.google;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.query.MatchType;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.translation.BaseConnector;
import net.sf.okapi.common.query.QueryResult;
import net.sf.okapi.lib.translation.QueryUtil;

public class GoogleMTv2Connector extends BaseConnector {

	private static final String baseUrl = "https://www.googleapis.com/language/translate/v2";
	private static final String baseQuery = "?key=%s&source=%s&target=%s";

	private GoogleMTv2Parameters params;
	private JSONParser parser;
	private QueryUtil util;

	public GoogleMTv2Connector () {
		params = new GoogleMTv2Parameters();
		util = new QueryUtil();
		parser = new JSONParser();
	}
	
	@Override
	public void setParameters (IParameters params) {
		this.params = (GoogleMTv2Parameters)params;
	}
	
	@Override
	public IParameters getParameters () {
		return params;
	}
	
	@Override
	public void close () {
		// Nothing to do
	}

	@Override
	public String getName () {
		return "Google-MTv2";
	}

	@Override
	public String getSettingsDisplay () {
		return "Server: " + baseUrl;
	}

	@Override
	public void open () {
		// Nothing to do
	}

	@Override
	public int query (String plainText) {
		return query(new TextFragment(plainText));
	}
	
	@Override
	public int query (TextFragment frag) {
		current = -1;
		try {
			// Check if there is actually text to translate
			if ( !frag.hasText(false) ) return 0;
			// Check that we have some Key available
			if ( Util.isEmpty(params.getApiKey()) ) {
				throw new RuntimeException("You must have a Google API Key to use this connector.");
			}
			// Convert the fragment to coded HTML
			String qtext = util.toCodedHTML(frag);
			// Create the connection and query
			String urlString = baseUrl + String.format(baseQuery, params.getApiKey(), srcCode, trgCode);
			// Check the space needed for the query
			int left = 2000 - (urlString.length()+3); // 3 is for "&q="
			if ( left < qtext.length() ) {
				throw new RuntimeException(String.format("Query too long: Character available: %d, characters in query: %d.",
					left, qtext.length()));
			}
			URL url = new URL(urlString+"&q="+URLEncoder.encode(qtext, "UTF-8"));

			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			int code = conn.getResponseCode();
			if ( code != 200 ) {
				throw new RuntimeException(String.format("Error: response code %d\n"
					+ conn.getResponseMessage(), code)); 
			}
			
			// Get the response
			JSONObject object = (JSONObject)parser.parse(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>)object;
	    	@SuppressWarnings("unchecked")
	    	Map<String, Object> data = (Map<String, Object>)map.get("data");
	    	JSONArray translations = (JSONArray)data.get("translations");
	    	@SuppressWarnings("unchecked")
	    	Map<String, String> resp = (Map<String, String>)translations.get(0);
	    	String res = (String)resp.get("translatedText");
	        
			result = new QueryResult();
			result.weight = getWeight();
			result.source = frag;
			if ( frag.hasCode() ) {
				result.target = new TextFragment(util.fromCodedHTML(res, frag, true),
					frag.getClonedCodes());
			}
			else {
				result.target = new TextFragment(util.fromCodedHTML(res, frag, true));
			}
			result.score = 95; // Arbitrary score for MT
			result.origin = getName();
			result.matchType = MatchType.MT;
			current = 0;
		}
		catch ( Throwable e ) {
			throw new RuntimeException("Error querying the server.\n" + e.getMessage(), e);
		}
		return ((current==0) ? 1 : 0);
	}

	@Override
	public List<List<QueryResult>> batchQuery (List<TextFragment> fragments) {
		current = -1;
		List<List<QueryResult>> allResults = new ArrayList<List<QueryResult>>();
		
		try {
			// Check that we have some Key available
			if ( Util.isEmpty(params.getApiKey()) ) {
				throw new RuntimeException("You must have a Google API Key to use this connector.");
			}
			// Create the query string
			String urlString = baseUrl + String.format(baseQuery, params.getApiKey(), srcCode, trgCode);

			// Add the text of the queries
			for ( TextFragment frag : fragments ) {
				// Check if there is actually text to translate
				if ( !frag.hasText(false) ) continue;
				// Convert the fragment to coded HTML
				String qtext = util.toCodedHTML(frag);

				// Check the space needed for the query
				int left = 2000 - (urlString.length()+3); // 3 is for "&q="
				if ( left < qtext.length() ) {
					throw new RuntimeException(String.format("Query too long: Character available: %d, characters in query: %d.",
						left, qtext.length()));
				}
				urlString += ("&q=" + URLEncoder.encode(qtext, "UTF-8"));
			}

			// Create the connection and query
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			int code = conn.getResponseCode();
			if ( code != 200 ) {
				throw new RuntimeException(String.format("Error: response code %d\n"
					+ conn.getResponseMessage(), code)); 
			}
			
			// Get the results
			JSONObject object = (JSONObject)parser.parse(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>)object;
	    	@SuppressWarnings("unchecked")
	    	Map<String, Object> data = (Map<String, Object>)map.get("data");
	    	JSONArray translations = (JSONArray)data.get("translations");

	    	for ( TextFragment frag : fragments ) {
	    		// Create the list for that query
	    		// Note that with this connector we have always only 1 result per query
	    		List<QueryResult> list = new ArrayList<QueryResult>();
	    		
				// Check if there is actually text to translate
				if ( !frag.hasText(false) ) {
					// If not: just send an empty result
					result = new QueryResult();
					result.weight = getWeight();
					result.source = frag;
					result.target = frag.clone();
					result.score = 95; // Arbitrary score for MT
					result.origin = getName();
					result.matchType = MatchType.MT;
					list.add(result);
					allResults.add(list);
					continue;
				}

				// Else: get the translation
				@SuppressWarnings("unchecked")
	    		Map<String, String> resp = (Map<String, String>)translations.get(0);
	    		String res = (String)resp.get("translatedText");
	        	result = new QueryResult();
	        	result.weight = getWeight();
	        	result.source = frag;
	        	if ( frag.hasCode() ) {
	        		result.target = new TextFragment(util.fromCodedHTML(res, frag, true),
	        			frag.getClonedCodes());
	        	}
	        	else {
	        		result.target = new TextFragment(util.fromCodedHTML(res, frag, true));
	        	}
	        	result.score = 95; // Arbitrary score for MT
	        	result.origin = getName();
	        	result.matchType = MatchType.MT;
	        	list.add(result);
	        	allResults.add(list);
	    	}

		}
		catch ( Throwable e ) {
			throw new RuntimeException("Error querying the server.\n" + e.getMessage(), e);
		}
		
		return allResults;
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
