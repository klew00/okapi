/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.connectors.microsoft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.query.MatchType;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.translation.BaseConnector;
import net.sf.okapi.common.query.QueryResult;
import net.sf.okapi.lib.translation.QueryUtil;

@UsingParameters(Parameters.class)
public class MicrosoftMTConnector extends BaseConnector {

	private QueryUtil util;
	Parameters params;

	public MicrosoftMTConnector () {
		util = new QueryUtil();
		params = new Parameters();
	}
	
	@Override
	public void close () {
		// Nothing to do
	}

	@Override
	public String getName () {
		return "Micosoft-MT";
	}

	@Override
	public String getSettingsDisplay () {
		return "Service: http://api.microsofttranslator.com/V2/Http.svc" ;
	}

	@Override
	public void open () {
	}

	@Override
	public int query (String plainText) {
		return query(new TextFragment(plainText));
	}
	
//	@Override
//	public int query (String plainText) {
//		current = -1;
//		result = null;
//		if ( Util.isEmpty(plainText) ) return 0;
//		String res = service.translate(params.getAppId(), plainText, srcCode, trgCode);
//		if ( Util.isEmpty(res) ) return 0;
//		result = new QueryResult();
//		result.weight = getWeight();
//		result.source = new TextFragment(plainText);
//		result.target = new TextFragment(res);
//		result.score = 95; // Arbitrary score for MT
//		result.origin = getName();
//		result.matchType = MatchType.MT;
//		current = 0;
//		return 1;
//	}
	
//	@Override
//	public int query (TextFragment text) {
//		current = -1;
//		result = null;
//		if ( !text.hasText(false) ) return 0;
//		String res = service.translate(params.getAppId(), util.separateCodesFromText(text), srcCode, trgCode);
//		if ( Util.isEmpty(res) ) return 0;
//		result = new QueryResult();
//		result.source = text;
//		result.target = util.createNewFragmentWithCodes(res);
//		result.score = 95; // Arbitrary score for MT
//		result.origin = getName();
//		result.matchType = MatchType.MT;
//		current = 0;
//		return 1;
//	}

	static private String fromInputStreamToString (InputStream stream,
		String encoding)
		throws IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(stream, encoding));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ( (line = br.readLine()) != null ) {
			sb.append(line + "\n");
		}
		br.close();
		return sb.toString();
	}
	
	@Override
	public int query (TextFragment frag) {
		current = -1;
		result = null;
		if ( !frag.hasText(false) ) return 0;
		try {
			// Convert the fragment to coded HTML
			String qtext = util.separateCodesFromText(frag);
			URL url = new URL(String.format("http://api.microsofttranslator.com/v2/Http.svc/Translate"
				+ "?appId=%s&text=%s&from=%s&to=%s",
				params.getAppId(),
				URLEncoder.encode(qtext, "UTF-8"),
				srcCode,
				trgCode));
			URLConnection conn = url.openConnection();
			String resp = fromInputStreamToString(conn.getInputStream(), "UTF-8");
			int n1 = resp.indexOf("<string");
			if ( n1 == -1 ) return 0;
			n1 = resp.indexOf('>', n1);
			if ( n1 == -1 ) return 0;
			int n2 = resp.indexOf("</string>", n1+1);
			if ( n2 == -1 ) return 0;
			String res = resp.substring(n1+1, n2);
			
			result = new QueryResult();
			result.weight = getWeight();
			result.source = frag;
			result.target = util.createNewFragmentWithCodes(util.fromPlainTextHTML(res));
			result.score = 95; // Arbitrary score for MT
			result.origin = getName();
			result.matchType = MatchType.MT;
			current = 0;
		}
		catch ( Throwable e) {
			throw new RuntimeException("Error querying the MT server." + e.getMessage(), e);
		}
		return ((current==0) ? 1 : 0);
	}
	
	@Override
	protected String toInternalCode (LocaleId locale) {
		String code = locale.toBCP47();
		if ( code.equals("zh-tw") || code.equals("zh-hant") || code.equals("zh-cht") ) {
			code = "zh-CHT";
		}
		else if ( code.startsWith("zh") ) { // zh-cn, zh-hans, zh-..
			code = "zh-CHS";
		}
		else { // Use just the language otherwise
			code = locale.getLanguage(); 
		}
		return code;
	}

	@Override
	public IParameters getParameters () {
		return params;
	}

	@Override
	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}

}
