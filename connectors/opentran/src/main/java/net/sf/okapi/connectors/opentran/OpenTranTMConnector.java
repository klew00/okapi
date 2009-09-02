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

package net.sf.okapi.connectors.opentran;

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

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.translation.ITMQuery;
import net.sf.okapi.lib.translation.QueryResult;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class OpenTranTMConnector implements ITMQuery {

	private static boolean useREST = true;
	
	private XmlRpcClient client;
	private String srcLang;
	private String trgLang;
	private List<QueryResult> results;
	private int current = -1;
	private int maxHits = 25;
	
	public String getName () {
		return "OpenTran-Repository";
	}

	public void close () {
		if ( !useREST ) {
			if ( client != null ) {
				client = null; // Free to garbage collect
			}
		}
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
		if ( useREST ) return;
		// Else:
		try {
			//TODO: use the connection string
			XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			config.setServerURL(new URL("http://open-tran.eu/RPC2"));
			client = new XmlRpcClient();
			client.setConfig(config);
		}
		catch ( MalformedURLException e ) {
			throw new RuntimeException(e);
		}
	}
	
	public int query (String plainText) {
		if ( useREST ) return restQuery(plainText);
		else return rpcQuery(plainText);
	}

	private int restQuery (String plainText) {
		results = new ArrayList<QueryResult>();
		current = -1;
		try {
			// Example: http://en.id.open-tran.eu/json/suggest/save%20as
			URL url = new URL("http://" + srcLang + "." + trgLang + ".open-tran.eu/json/suggest/"
				+ URLEncoder.encode(plainText, "UTF-8").replace("+", "%20"));
			URLConnection conn = url.openConnection();
			
			// Get the response
			JSONParser parser = new JSONParser();
			JSONArray array = (JSONArray)parser.parse(new InputStreamReader(conn.getInputStream(), "UTF-8"));

			QueryResult qr;
			int count = 0;
			mainLoop:
	        for ( int i=0; i<array.size(); i++ ) {
	        	@SuppressWarnings("unchecked")
	        	Map<String, Object> map = (Map<String, Object>)array.get(i);
	        	String text = (String)map.get("text");
	        	JSONArray projects = (JSONArray)map.get("projects");
	        	for ( int p=0; p<projects.size(); p++ ) {
	        		@SuppressWarnings("unchecked")
	        		Map<String, Object> pairs = (Map<String, Object>)projects.get(p);
	        		qr = new QueryResult();
	        		qr.target = new TextFragment(text);
	        		qr.source = new TextFragment((String)pairs.get("orig_phrase"));
		        	results.add(qr);
					if ( ++count == maxHits ) break mainLoop;
	        	}
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

	@SuppressWarnings("unchecked")
	private int rpcQuery (String plainText) {
		try {
			current = -1;
			results = new ArrayList<QueryResult>();
			// Prepare the parameters
			Object[] params = new Object[] {
				new String(plainText),
				new String(srcLang),
				new String(trgLang),
				new Integer(maxHits)};
			
			// Do the query
			Object[] array = (Object[])client.execute("suggest3", params);
			if (( array == null ) || ( array.length == 0 )) return 0;
			
			// Convert the results
			QueryResult qr;
			int count = 0;
			mainLoop:
			for ( Object obj1 : array ) {
				Map<String, Object> map1 = (Map<String, Object>)obj1;
				String trgText = (String)map1.get("text");
				//int value = (Integer)map1.get("value");
				//int count = (Integer)map1.get("count");
				Object[] projects = (Object[])map1.get("projects");
				for ( Object obj2 : projects ) {
					Map<String, Object> map2 = (Map<String, Object>)obj2;
					qr = new QueryResult();
					qr.target = new TextContainer();
					qr.target.append(trgText);
					String srcText = (String)map2.get("orig_phrase");
					qr.source = new TextContainer();
					qr.source.append(srcText);
					results.add(qr);
					// suggest3 maximum parameters limits the number of
					// level-1 object returned, not the total number
					if ( ++count == maxHits ) break mainLoop;
				}
			}

			current = 0;
			return results.size();
		}
		catch ( XmlRpcException e ) {
			throw new RuntimeException(e);
		}
	}

	public int query (TextFragment text) {
		return query(text.getCodedText());
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
		if ( !code.startsWith("zh") && ( code.length() > 2 )) {
			code = code.substring(0, 2);
		}
		return code;
	}

	public IParameters getParameters () {
		// Not used with this connector
		return null;
	}

	public void setParameters (IParameters params) {
		// Not used with this connector
	}

}
