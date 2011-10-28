/*===========================================================================
  Copyright (C) 2009-2010 by the Okapi Framework contributors
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

package net.sf.okapi.connectors.translatetoolkit;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.exceptions.OkapiNotImplementedException;
import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.common.query.MatchType;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.translation.BaseConnector;
import net.sf.okapi.lib.translation.ITMQuery;
import net.sf.okapi.common.query.QueryResult;

public class TranslateToolkitTMConnector extends BaseConnector implements ITMQuery {

	private Parameters params;
	private String baseURL;
	private List<QueryResult> results;
	private int current = -1;
	private int maxHits = 25;
	private int threshold = 60;
	private JSONParser parser;
	private GenericContent fmt;
	
	public TranslateToolkitTMConnector () {
		params = new Parameters();
		fmt = new GenericContent();
	}
	
	@Override
	public String getName () {
		return "Translate Toolkit TM";
	}

	@Override
	public String getSettingsDisplay () {
		return "Server: "+String.format("http://%s:%d/tmserver/",
			params.getHost(), params.getPort());
	}
	
	@Override
	public void close () {
		// Nothing to do
	}

	@Override
	public boolean hasNext () {
		if ( results == null ) return false;
		if ( current >= results.size() ) {
			current = -1;
		}
		return (current > -1);
	}

	@Override
	public QueryResult next () {
		if ( results == null ) return null;
		if (( current > -1 ) && ( current < results.size() )) {
			current++;
			return results.get(current-1);
		}
		current = -1;
		return null;
	}

	@Override
	public void open () {
		baseURL = String.format("http://%s:%d/tmserver/",
			params.getHost(), params.getPort());
		parser = new JSONParser();
	}

	@Override
	public int query (String plainText) {
		return query(new TextFragment(plainText));
//		
//		results = new ArrayList<QueryResult>();
//		current = -1;
//		if ( Util.isEmpty(plainText) ) {
//			return 0;
//		}
//		try {
//			URL url = new URL(baseURL + srcCode + "/" + trgCode + "/unit/"
//				+ URLEncoder.encode(plainText, "UTF-8").replace("+", "%20"));
//			URLConnection conn = url.openConnection();
//
//			// Get the response
//	        JSONArray array = (JSONArray)parser.parse(new InputStreamReader(conn.getInputStream(), "UTF-8"));
//			QueryResult qr;
//	        for ( int i=0; i<array.size(); i++ ) {
//	        	if ( i >= maxHits ) break; // Stop at maxHits
//	        	@SuppressWarnings("unchecked")
//	        	Map<String, Object> map = (Map<String, Object>)array.get(i);
//	        	qr = new QueryResult();
//	        	qr.weight = getWeight();
//	        	qr.score = ((Double)map.get("quality")).intValue();
//	        	if ( qr.score < threshold ) break; // Done
//	        	qr.source = new TextFragment((String)map.get("source"));
//	        	qr.target = new TextFragment((String)map.get("target"));
//	        	// Set match type
//				if ( qr.score >= 100 ) qr.matchType = MatchType.EXACT;
//				else if ( qr.score > 0 ) qr.matchType = MatchType.FUZZY;
//	        	results.add(qr);
//	        }
//			if ( results.size() > 0 ) current = 0;
//			return results.size();
//		}
//		catch ( MalformedURLException e ) {
//			throw new RuntimeException("Error when querying.", e);
//		}
//		catch ( UnsupportedEncodingException e ) {
//			throw new RuntimeException("Error when querying.", e);
//		}
//		catch ( IOException e ) {
//			throw new RuntimeException("Error when querying.", e);
//		}
//		catch ( ParseException e ) {
//			throw new RuntimeException("Error when parsing JSON results.", e);
//		}
	}

	@Override
	public int query (TextFragment text) {
		// Otherwise, treat the codes depending on the mode
		String plain;
		if ( text.hasCode() && params.getSupportCodes() ) {
			plain = fmt.fromFragmentToLetterCoded(text);
		}
		else {
			plain = text.getCodedText();
		}
		
		results = new ArrayList<QueryResult>();
		current = -1;
		if ( Util.isEmpty(plain) ) {
			return 0;
		}
		
		try {
			URL url = new URL(baseURL + srcCode + "/" + trgCode + "/unit/"
				+ URLEncoder.encode(plain, "UTF-8").replace("+", "%20"));
			URLConnection conn = url.openConnection();

			// Get the response
	        JSONArray array = (JSONArray)parser.parse(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			QueryResult qr;
	        for ( int i=0; i<array.size(); i++ ) {
	        	if ( i >= maxHits ) break; // Stop at maxHits
	        	@SuppressWarnings("unchecked")
	        	Map<String, Object> map = (Map<String, Object>)array.get(i);
	        	qr = new QueryResult();
	        	qr.weight = getWeight();
	        	qr.setFuzzyScore(((Double)map.get("quality")).intValue());
	        	if ( qr.getFuzzyScore() < threshold ) break; // Done
	        	
	        	if ( text.hasCode() && params.getSupportCodes() ) {
	        		qr.source = fmt.fromLetterCodedToFragment((String)map.get("source"), null, false);
	        		qr.target = fmt.fromLetterCodedToFragment((String)map.get("target"), null, false);
	        	}
	        	else {
	        		qr.source = new TextFragment((String)map.get("source"));
	        		qr.target = new TextFragment((String)map.get("target"));
	        	}

	        	// Set match type
				if ( qr.getFuzzyScore() >= 100 ) qr.matchType = MatchType.EXACT;
				else if ( qr.getFuzzyScore() > 0 ) qr.matchType = MatchType.FUZZY;
	        	results.add(qr);
	        }
			if ( results.size() > 0 ) current = 0;
			return results.size();
		}
		catch ( MalformedURLException e ) {
			throw new RuntimeException("Error when querying.", e);
		}
		catch ( UnsupportedEncodingException e ) {
			throw new RuntimeException("Error when querying.", e);
		}
		catch ( IOException e ) {
			throw new RuntimeException("Error when querying.", e);
		}
		catch ( ParseException e ) {
			throw new RuntimeException("Error when parsing JSON results.", e);
		}
	}
	
	@Override
	public List<List<QueryResult>> batchQuery (List<TextFragment> fragments) {
		throw new OkapiNotImplementedException();
	}

	@Override
	public void clearAttributes () {
		// Not used with this connector
	}

	@Override
	public void removeAttribute (String name) {
		// Not used with this connector
	}

	@Override
	public void setAttribute (String name,
		String value)
	{
		// Not used with this connector
	}

	@Override
	public void setMaximumHits (int max) {
		maxHits = max;
	}

	@Override
	public void setThreshold (int threshold) {
		this.threshold = threshold;
	}

	@Override
	public int getMaximumHits () {
		return maxHits;
	}

	@Override
	public int getThreshold () {
		return threshold;
	}
	
	@Override
	protected String toInternalCode (LocaleId standardCode) {
		return standardCode.toPOSIXLocaleId();
	}

	@Override
	public IParameters getParameters () {
		return params;
	}

	@Override
	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}

	@Override
	public void setRootDirectory (String rootDir) {
		// Not used
	}
}
