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

package net.sf.okapi.connectors.mymemory;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.axis.AxisFault;
import org.tempuri.GetResponse;
import org.tempuri.Match;
import org.tempuri.OtmsSoapStub;
import org.tempuri.Query;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.exceptions.OkapiNotImplementedException;
import net.sf.okapi.common.query.MatchType;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.translation.BaseConnector;
import net.sf.okapi.lib.translation.ITMQuery;
import net.sf.okapi.common.query.QueryResult;
import net.sf.okapi.lib.translation.QueryUtil;

public class MyMemoryTMConnector extends BaseConnector implements ITMQuery {

	private static final String SERVER_URL = "http://mymemory.translated.net/otms/";
	
	private List<QueryResult> results;
	private int current = -1;
	private int maxHits = 25;
	private int threshold = 75;
	private Parameters params;
	private OtmsSoapStub otms;
	private QueryUtil qutil;

	public MyMemoryTMConnector () {
		params = new Parameters();
		qutil = new QueryUtil();
	}

	@Override
	public String getName () {
		return "MyMemory.net";
	}

	@Override
	public String getSettingsDisplay () {
		return String.format("Server: %s\nAllow MT: %s", SERVER_URL,
			((params.getUseMT()==1) ? "Yes" : "No"));
	}
	
	@Override
	public void close () {
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
		try {
			results = new ArrayList<QueryResult>();
			URL url = new URL(SERVER_URL);
			otms = new OtmsSoapStub(url, null);
		}
		catch ( AxisFault e ) {
			throw new RuntimeException("Error creating the MyMemory Web services.", e);
		}
		catch ( MalformedURLException e ) {
			throw new RuntimeException("Invalid server URL.", e);
		}
	}

	@Override
	public int query (TextFragment frag) {
		results.clear();
		current = -1;
		if ( !frag.hasText(false) ) return 0;
		try {
			String text = qutil.separateCodesFromText(frag);
			Query query = new Query(null, text, srcCode, trgCode, null, params.getUseMT());
			GetResponse gresp = otms.otmsGet(params.getKey(), query);
			if ( gresp.isSuccess() ) {
				QueryResult res;
				Match[] matches = gresp.getMatches();
				int i = 0;
				for ( Match match : matches ) {
					if ( ++i > maxHits ) break; // Maximum reached
					res = new QueryResult();
					res.weight = getWeight();
					res.origin = getName();
					if ( match.getTranslator().equals("MT!") ) {
						res.matchType = MatchType.MT;
						res.score = 95; // Standard score for MT
					}
					else res.score = match.getScore();
					// To workaround bug in score calculation
					// Score > 100 should be treated as 100 per Alberto's info.
					if (res.score > 100 ) res.score = 100;
					// Set match type
					if ( res.score >= 100 ) res.matchType = MatchType.EXACT;
					else if ( res.score > 0 ) res.matchType = MatchType.FUZZY;

					if ( res.score < getThreshold() ) break;
					if ( qutil.hasCode() ) {
						res.score--;
						res.source = qutil.createNewFragmentWithCodes(match.getSegment());
						res.target = qutil.createNewFragmentWithCodes(match.getTranslation());
					}
					else {
						res.source = new TextFragment(match.getSegment());
						res.target = new TextFragment(match.getTranslation());
					}
					results.add(res);
				}
			}
		}
		catch ( RemoteException e ) {
			throw new RuntimeException("Error querying TM.", e);
		}
		if ( results.size() > 0 ) current = 0;
		return results.size();
	}


	@Override
	public int query (String plainText) {
		return query(new TextFragment(plainText));
	}
	
	@Override
	public List<List<QueryResult>> batchQuery (List<TextFragment> fragments) {
		throw new OkapiNotImplementedException();
	}

	@Override
	public void removeAttribute (String name) {
		//TODO: use domain
	}

	@Override
	public void clearAttributes () {
		//TODO: use domain
	}

	@Override
	public void setAttribute (String name,
		String value)
	{
		//TODO: use domain
	}

	@Override
	protected String toInternalCode (LocaleId locale) {
		// The expected language code is language-Region with region mandatory
		String lang = locale.getLanguage();
		String reg = locale.getRegion();
		
		//TODO: Use a lookup table and a more complete one
		if ( lang.equals("en") ) reg = "us";
		else if ( lang.equals("pt") ) reg = "br";
		else if ( lang.equals("el") ) reg = "gr";
		else if ( lang.equals("he") ) reg = "il";
		else if ( lang.equals("ja") ) reg = "jp";
		else if ( lang.equals("ko") ) reg = "kr";
		else if ( lang.equals("ms") ) reg = "my";
		else if ( lang.equals("sl") ) reg = "si";
		else if ( lang.equals("sq") ) reg = "al";
		else if ( lang.equals("sv") ) reg = "se";
		else if ( lang.equals("vi") ) reg = "vn";
		else if ( lang.equals("zh") ) {
			if ( reg != null ) reg = "cn";
		}
		else reg = lang;
		return lang+"-"+reg;
	}

	/**
	 * Sets the maximum number of hits to return.
	 */
	@Override
	public void setMaximumHits (int max) {
		if ( max < 1 ) maxHits = 1;
		else maxHits = max;
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
	public IParameters getParameters () {
		return params;
	}

	@Override
	public void setParameters (IParameters params) {
		params = (Parameters)params;
	}

	@Override
	public void setRootDirectory (String rootDir) {
		// Not used
	}	

}
