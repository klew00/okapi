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
===========================================================================*/

package net.sf.okapi.tm.translatetoolkit;

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
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.translation.ITMQuery;
import net.sf.okapi.lib.translation.QueryResult;

public class TranslateToolkitTMConnector implements ITMQuery {

	private Parameters params;
	private String baseURL;
	private String srcLang;
	private String trgLang;
	private List<QueryResult> results;
	private int current = -1;
	private int maxHits = 25;
	
	public TranslateToolkitTMConnector () {
		params = new Parameters();
	}
	
	public String getName () {
		return "Translate Toolkit TM";
	}

	public void close () {
	}

	public void export (String outputPath) {
		throw new UnsupportedOperationException();
	}

	public String getSourceLanguage () {
		return srcLang;
	}

	public String getTargetLanguage () {
		return trgLang;
	}

	public boolean hasNext () {
		if ( results == null ) return false;
		if ( current >= results.size() ) {
			current = -1;
		}
		return (current > -1);
	}

	public QueryResult next () {
		if ( results == null ) return null;
		if (( current > -1 ) && ( current < results.size() )) {
			current++;
			return results.get(current-1);
		}
		current = -1;
		return null;
	}

	public void open () {
		baseURL = String.format("http://%s:%d/tmserver/",
			params.getHost(), params.getPort());
	}

	@SuppressWarnings("unchecked")
	public int query (String plainText) {
		results = new ArrayList<QueryResult>();
		current = -1;
		try {
			URL url = new URL(baseURL + srcLang + "/" + trgLang + "/unit/"
				+ URLEncoder.encode(plainText, "UTF-8").replace("+", "%20"));
			URLConnection conn = url.openConnection();

			// Get the response
			JSONParser parser = new JSONParser();
	        JSONArray array = (JSONArray)parser.parse(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			QueryResult qr;
	        for ( int i=0; i<array.size(); i++ ) {
	        	Map<String, Object> map = (Map<String, Object>)array.get(i);
	        	qr = new QueryResult();
	        	qr.score = ((Double)map.get("quality")).intValue();
	        	qr.source = new TextFragment((String)map.get("source"));
	        	qr.target = new TextFragment((String)map.get("target"));
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

	public int query (TextFragment text) {
		String tmp = text.getCodedText();
		return query(tmp);
	}

	public void removeAttribute (String name) {
		// Not used with this connector
	}

	public void setAttribute (String name,
		String value)
	{
		// Not used with this connector
	}

	public void setLanguages (String sourceLang,
		String targetLang)
	{
		srcLang = toInternalCode(sourceLang);
		trgLang = toInternalCode(targetLang);
	}

	public void setMaximumHits (int max) {
		maxHits = max;
	}

	public void setThreshold (int threshold) {
		// Not used with this connector
	}

	public int getMaximumHits () {
		return maxHits;
	}

	public int getThreshold () {
		// Not used with this connector
		return 0;
	}

	private String toInternalCode (String standardCode) {
		String code = standardCode.toLowerCase().replace('-', '_');
		return code;
	}

	public IParameters getParameters () {
		return params;
	}

	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}

}
