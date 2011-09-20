/*===========================================================================
  Copyright (C) 2009-2011 by the Okapi Framework contributors
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

package net.sf.okapi.connectors.crosslanguage;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.crosslang.clgateway.ws.gateway.impl.ClGatewayPortBindingStub;

import net.sf.okapi.common.Base64;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.query.MatchType;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.translation.BaseConnector;
import net.sf.okapi.common.query.QueryResult;
import net.sf.okapi.lib.translation.QueryUtil;

/**
 * Connector for the CrossLanguage MT Gateway Web services.
 * <p>Note that the language pair is determined by the apiKey, not anything else.
 * So the <code>setLanguages</code> method has no effect with this connector. 
 */
public class CrossLanguageMTConnector extends BaseConnector {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");

	private Parameters params;
	private QueryUtil util;
	private ClGatewayPortBindingStub clGateway;
	private String requestTime;
	private String secret;

	public CrossLanguageMTConnector () {
		params = new Parameters();
		util = new QueryUtil();
	}
	
	@Override
	public void close () {
		clGateway = null;
	}

	@Override
	public String getName () {
		return "CrossLanguage Gateway";
	}

	@Override
	public String getSettingsDisplay () {
		return "Server: " + params.getServerURL();
	}

	@Override
	public void open () {
		try {
			URL url = new URL(params.getServerURL());
			clGateway = new ClGatewayPortBindingStub(url, null);
			requestTime = generateTimeStamp();
			secret = generateSecret(params.getUser(), requestTime, params.getPassword());
		}
		catch ( InvalidKeyException e ) {
			throw new RuntimeException("Invalid key.", e);
		}
		catch ( NoSuchAlgorithmException e ) {
			throw new RuntimeException("Encryption error.", e);
		}
		catch ( Throwable e ) {
			throw new RuntimeException("Error when initializing the MT engine.", e);
		}
	}

	@Override
	public int query (String plainText) {
		return queryString(plainText);
	}
	
	@Override
	public int query (TextFragment text) {
		return queryFile(text);
	}

	private int queryString (String text) {
		current = -1;
		try {
			// Call the service
			String res = clGateway.translateSentence(params.getApiKey(), params.getUser(),
				requestTime, secret, text);
			if ( res == null ) return 0;
			
			// Process the result
			result = new QueryResult();
			result.weight = getWeight();
			result.source = new TextFragment(text);
			result.target = new TextFragment(res);
			result.setFuzzyScore(95); // Arbitrary score for MT
			result.origin = getName();
			result.matchType = MatchType.MT;
			current = 0;
		}
		catch ( Throwable e ) {
			throw new RuntimeException("Error querying the server: " + e.getMessage(), e);
		}
		return ((current==0) ? 1 : 0);
	}

	private int queryFile (TextFragment text) {
		current = -1;
		try {
			// Check if there is actually text to translate
			if ( !text.hasText(false) ) return 0;
			// Convert the fragment to coded HTML
			// The charset must be set or the return will be in something else than UTF-8
			String qtext = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><p>"+util.toCodedHTML(text)+"</p>";
			//String qtext = "<p>"+util.toCodedHTML(text)+"<p>";
			// Call the service
			String res = clGateway.translateFile(params.getApiKey(), params.getUser(),
				requestTime, secret, Base64.encodeString(qtext), "html"); 
			
			if ( res == null ) return 0;
			if ( res.startsWith("TransError") ) {
				throw new RuntimeException("Error querying the server: " + res);
			}
			
			// Process the result
			result = new QueryResult();
			result.source = text;
			// Remove the extra header info
			String data = Base64.decodeString(res);
			int pos = data.indexOf("<p>");
			if ( pos > -1 ) data = data.substring(pos+3, data.length()-4);
			// Convert back to internal codes
			if ( text.hasCode() ) {
				result.target = new TextFragment(util.fromCodedHTML(data, text, true),
					text.getClonedCodes());
			}
			else {
				result.target = new TextFragment(util.fromCodedHTML(data, text, true));
			}
			result.setFuzzyScore(95); // Arbitrary score for MT
			result.origin = getName();
			result.matchType = MatchType.MT;
			current = 0;
		}
		catch ( Throwable e ) {
			throw new RuntimeException("Error querying the server: " + e.getMessage(), e);
		}
		return ((current==0) ? 1 : 0);
	}

	@Override
	public IParameters getParameters () {
		return params;
	}

	@Override
	public void setParameters (IParameters params) {
		params = (Parameters)params;
	}

	private String generateTimeStamp () {
		return DATE_FORMAT.format(new Date());
	}
		
	private String generateSecret (String username,
		String timestamp,
		String password)
		throws NoSuchAlgorithmException, InvalidKeyException, IllegalStateException, UnsupportedEncodingException
	{
		StringBuilder sb = new StringBuilder(username).append("#").append(timestamp);
		SecretKeySpec signingKey = new SecretKeySpec(password.getBytes(), "HmacSHA1");
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(signingKey);
		byte[] rawHmac = mac.doFinal(sb.toString().getBytes("UTF-8"));
		return (new String(Base64.encode(rawHmac))).trim();
	}

}
