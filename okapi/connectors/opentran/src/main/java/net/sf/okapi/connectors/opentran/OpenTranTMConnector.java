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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiNotImplementedException;
import net.sf.okapi.common.query.MatchType;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnitUtil;
import net.sf.okapi.lib.translation.BaseConnector;
import net.sf.okapi.lib.translation.ITMQuery;
import net.sf.okapi.common.query.QueryResult;
import net.sf.okapi.lib.translation.TextMatcher;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class OpenTranTMConnector extends BaseConnector implements ITMQuery {

	private List<QueryResult> results;
	private int current = -1;
	private int maxHits = 12;
	private int threshold = 60;
	private TextMatcher matcher;
	private ScoreComparer scorComp = new ScoreComparer();
	
	class ScoreComparer implements Comparator<QueryResult> {
		public int compare(QueryResult arg0, QueryResult arg1) {
			return (arg0.getFuzzyScore()>arg1.getFuzzyScore() ? -1 : (arg0.getFuzzyScore()==arg1.getFuzzyScore() ? 0 : 1));
		}
	}
	
	@Override
	public String getName () {
		return "OpenTran";
	}

	@Override
	public String getSettingsDisplay () {
		return "REST services at http://open-tran.eu";
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
		// Nothing to do
	}
	
	@Override
	public int query (String plainText) {
		return restQuery(plainText, false);
	}

	private int restQuery (String plainText,
		boolean hasCode)
	{
		results = new ArrayList<QueryResult>();
		current = -1;
		try {
			// Example: http://en.id.open-tran.eu/json/suggest/save%20as
			URL url = new URL("http://" + srcCode + "." + trgCode + ".open-tran.eu/json/suggest/"
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
	        		qr.weight = getWeight();
	        		qr.target = new TextFragment(text);
	        		qr.source = new TextFragment((String)pairs.get("orig_phrase"));
					String tmp = (String)pairs.get("path");
					if ( !Util.isEmpty(tmp) ) {
						qr.origin = tmp;
					}
		        	results.add(qr);
					if ( ++count == maxHits ) break mainLoop;
	        	}
	        }

			// Adjust scores
			fixupResults(plainText, hasCode);

			if ( results.size() > 0 ) current = 0;
			return results.size();
		}
		catch ( MalformedURLException e ) {
			throw new RuntimeException("URL error when querying.", e);
		}
		catch ( UnsupportedEncodingException e ) {
			throw new RuntimeException("Encoding error when querying.", e);
		}
		catch ( IOException e ) {
			throw new RuntimeException("IO error when querying.", e);
		}
		catch ( ParseException e ) {
			throw new RuntimeException("Error when parsing JSON results.", e);
		}
	}

	@Override
	public int query (TextFragment text) {
		return restQuery(TextUnitUtil.getText(text, null), text.hasCode());
	}
	
	@Override
	public List<List<QueryResult>> batchQuery (List<TextFragment> fragments) {
		throw new OkapiNotImplementedException();
	}

	@Override
	public void removeAttribute (String name) {
		// Not used with this connector
	}

	@Override
	public void clearAttributes () {
		//TODO: use platform, etc.
	}

	@Override
	public void setAttribute (String name,
		String value)
	{
		// Not used with this connector
	}

	@Override
	public void setLanguages (LocaleId sourceLocale,
		LocaleId targetLocale)
	{
		super.setLanguages(sourceLocale, targetLocale);
		matcher = new TextMatcher(sourceLocale, sourceLocale);
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
	protected String toInternalCode (LocaleId locale) {
		String code = locale.toPOSIXLocaleId();
		if ( !code.startsWith("zh") && ( code.length() > 2 )) {
			code = code.substring(0, 2);
		}
		return code;
	}

	@Override
	public IParameters getParameters () {
		// Not used with this connector
		return null;
	}

	@Override
	public void setParameters (IParameters params) {
		// Not used with this connector
	}

	@Override
	public void setRootDirectory (String rootDir) {
		// Not used
	}
	
	/**
	 * Re-calculates the scores, re-orders and filters the results based on
	 * more meaning full comparisons.
	 * @param plainText the original text query.
	 */
	private void fixupResults (String plainText,
		boolean hasCodes)
	{
		if ( results.size() == 0 ) return;
		List<String> tokens = matcher.prepareBaseTokens(plainText);
		// Loop through the results
		for ( Iterator<QueryResult> iter = results.iterator(); iter.hasNext(); ) {
			QueryResult qr = iter.next();
			// Compute the adjusted score
			qr.setFuzzyScore(matcher.compareToBaseTokens(plainText, tokens, qr.source));
			// Make sure we don't get exact if there are codes
			if ( hasCodes && ( qr.getFuzzyScore() > 99 )) qr.setFuzzyScore(qr.getFuzzyScore()-1);
			// Remove the item if lower than the threshold 
			if ( qr.getFuzzyScore() < threshold ) {
				iter.remove();
			}
			else { // Set match type
				if ( qr.getFuzzyScore() >= 100 ) qr.matchType = MatchType.EXACT;
				else if ( qr.getFuzzyScore() > 0 ) qr.matchType = MatchType.FUZZY;
			}
		}
		// Re-order the list based on the scores 
		Collections.sort(results, scorComp);
	}

}
