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

package net.sf.okapi.connectors.google;

import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.translation.IQuery;
import net.sf.okapi.lib.translation.QueryResult;

public class GoogleMTConnector implements IQuery {

//	private static final String addressSite = "http://translate.google.com/translate_t";
//	private static final String baseQuerySite = "?text=%s&hl=en&ie=UTF8&langpair=%s|%s&oe=UTF8";
//	private static final Pattern patternSite = Pattern.compile("<div\\sid=result_box .*?>(.*?)</div>");

	private static final String addressAjax = "http://ajax.googleapis.com/ajax/services/language/translate";
	private static final String baseQueryAjax = "?v=1.0&q=%s&langpair=%s|%s";
//	private static final Pattern patternAjax = Pattern.compile("\"translatedText\":\"(.*?)\"\\},");

	private static final String CLOSING_CODE = "</s>";
	private static final int CLOSING_CODE_LENGTH = CLOSING_CODE.length();
	private static final Pattern opening = Pattern.compile("\\<s(\\s+)id=['\"](.*?)['\"]>");
	private static final Pattern isolated = Pattern.compile("\\<br(\\s+)id=['\"](.*?)['\"](\\s*?)/>");
	
	private String srcLang;
	private String trgLang;
	private String lastError;
	private QueryResult result;
	private int current = -1;
	private String hostId;
	private JSONParser parser;
	
	public void close () {
		// Nothing to do
	}

	public String getName () {
		return "Google-MT";
	}

	public String getSettingsDisplay () {
		return "Server: " + addressAjax;
	}
	public boolean hasNext () {
		return (current>-1);
	}
	
	public QueryResult next() {
		if ( current > -1 ) { // Only one result
			current = -1;
			return result;
		}
		return null;
	}

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

	public int query (String plainText) {
		TextFragment tf = new TextFragment(plainText);
		return query(tf);
	}
	
	public int query (TextFragment text) {
		lastError = null;
		current = -1;
		return queryAjax(text);
	}

	private int queryAjax (TextFragment fragment) {
		try {
			// Check if there is actually text to translate
			if ( !fragment.hasText(false) ) return 0;
			// Convert the fragment to coded HTML
			String qtext = toCodedHTML(fragment);
			// To compile with Google TOS: no more than 5000 characters at a time
			if ( qtext.length() > 5000 ) {
				return 0; 
			}
			// Create the connection and query
			URL url = new URL(addressAjax + String.format(baseQueryAjax,
				URLEncoder.encode(qtext, "UTF-8"), srcLang, trgLang));
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
			result.source = fragment;
			if ( fragment.hasCode() ) {
				result.target = new TextFragment(fromCodedHTML(res, fragment),
					fragment.getCodes());
			}
			else {
				result.target = new TextFragment(fromCodedHTML(res, fragment));
			}
			// If query and translated result are the same: don't see this as a translation
			if ( result.target.equals(fragment) ) { // Test ignore codes content
				return 0;
			}
			result.score = 95; // Arbitrary score for MT
			current = 0;
		}
		catch ( Throwable e ) {
			lastError = e.getMessage();
		}
		return ((current==0) ? 1 : 0);
	}

	/**
	 * Converts from coded text to coded HTML.
	 * @param fragment the fragment to convert.
	 * @return The resulting HTML string.
	 */
	private String toCodedHTML (TextFragment fragment) {
		if ( fragment == null ) return "";
		Code code;
		StringBuilder sb = new StringBuilder();
		String text = fragment.getCodedText();
		for ( int i=0; i<text.length(); i++ ) {
			switch ( text.charAt(i) ) {
			case TextFragment.MARKER_OPENING:
				code = fragment.getCode(text.charAt(++i));
				sb.append(String.format("<s id='%d'>", code.getId()));
				break;
			case TextFragment.MARKER_CLOSING:
				i++;
				sb.append("</s>");
				break;
			case TextFragment.MARKER_ISOLATED:
				code = fragment.getCode(text.charAt(++i));
				sb.append(String.format("<br id='%d'/>", code.getId()));
				break;
			case TextFragment.MARKER_SEGMENT:
				// Segment-holder text not supported
				throw new RuntimeException("Fragment with segment markers are not supported by the Google connector. Send the segments instead.");
			case '&':
				sb.append("&amp;");
				break;
			case '<':
				sb.append("&lt;");
				break;
			default:
				sb.append(text.charAt(i));
			}
		}
		return sb.toString();
	}
	
	/**
	 * Converts back a coded HTML to a coded text.
	 * @param text the coded HTML to convert back.
	 * @return the coded text with its code markers.
	 */
	private String fromCodedHTML (String text,
		TextFragment fragment)
	{
		if ( Util.isEmpty(text) ) return "";
		text = text.toString().replace("&#39;", "'");
		text = text.replace("&lt;", "<");
		text = text.replace("&gt;", ">");
		text = text.replace("&quot;", "\"");
		StringBuilder sb = new StringBuilder();
		sb.append(text.replace("&amp;", "&"));

		Matcher m = opening.matcher(sb.toString());
        while ( m.find() ) {
        	// Replace the HTML fake code by the coded text markers
        	int id = Util.strToInt(m.group(2), -1);
        	String markers = String.format("%c%c", TextFragment.MARKER_OPENING,
        		TextFragment.toChar(fragment.getIndex(id)));
        	sb.replace(m.start(), m.end(), markers);
        	// Search corresponding closing part
        	int n = sb.toString().indexOf(CLOSING_CODE);
        	// Replace closing code by the coded text markers for closing
        	markers = String.format("%c%c", TextFragment.MARKER_CLOSING,
        		TextFragment.toChar(fragment.getIndexForClosing(id)));
        	sb.replace(n, n+CLOSING_CODE_LENGTH, markers);
        	m = opening.matcher(sb.toString());
        }
        
		m = isolated.matcher(sb.toString());
        while ( m.find() ) {
        	// Replace the HTML fake code by the coded text markers
        	int id = Util.strToInt(m.group(2), -1);
        	String markers = String.format("%c%c", TextFragment.MARKER_ISOLATED,
        		TextFragment.toChar(fragment.getIndex(id)));
        	sb.replace(m.start(), m.end(), markers);
        	m = isolated.matcher(sb.toString());
        }

		return sb.toString();
	}

// Old query, scraping the result page
//	public int querySite (TextFragment text) {
//		try {
//			String qtext = text.getCodedText();
//			StringBuilder tmpCodes = new StringBuilder();
//			if ( text.hasCode() ) {
//				StringBuilder tmpText = new StringBuilder();
//				for ( int i=0; i<qtext.length(); i++ ) {
//					switch ( qtext.charAt(i) ) {
//					case TextFragment.MARKER_OPENING:
//					case TextFragment.MARKER_CLOSING:
//					case TextFragment.MARKER_ISOLATED:
//					case TextFragment.MARKER_SEGMENT:
//						tmpCodes.append(qtext.charAt(i));
//						tmpCodes.append(qtext.charAt(++i));
//						break;
//					default:
//						tmpText.append(qtext.charAt(i));
//					}
//				}
//				qtext = tmpText.toString();
//			}
//
//			URL url = new URL(addressSite + String.format(baseQuerySite,
//				URLEncoder.encode(qtext, "UTF-8"), srcLang, trgLang));
//			URLConnection conn = url.openConnection();
//			// Make sure we send a user-agent property, otherwise we get 403 error
//			conn.setRequestProperty("User-Agent", getClass().getName());
//
//			// Get the response
//	        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
//	        StringBuilder res = new StringBuilder();
//			char[] buf = new char[2048];
//			int count = 0;
//			while (( count = rd.read(buf)) != -1 ) {
//				res.append(buf, 0, count);
//			}
//	        rd.close();
//	        
//	        Matcher m = patternSite.matcher(res.toString());
//	        if ( m.find() ) {
//				result = new QueryResult();
//				result.source = text;
//				if ( text.hasCode() ) {
//					result.target = new TextFragment(unescape(m.group(1))+tmpCodes.toString(),
//						text.getCodes());
//				}
//				else {
//					result.target = new TextFragment(unescape(m.group(1)));
//				}
//				result.score = (text.hasCode() ? 98 : 99);
//				current = 0;
//	        }
//		}
//		catch ( Throwable e ) {
//			lastError = e.getMessage();
//		}
//
//		return ((current==0) ? 1 : 0);
//	}
	
	public void setAttribute (String name,
		String value)
	{
		// Not used with this MT engine
	}
	
	public void removeAttribute (String name) {
		// Not used with this MT engine
	}
	
	public void clearAttributes () {
		// Not used with this MT engine
	}

	public void setLanguages (String sourceLang,
		String targetLang)
	{
		srcLang = convertLangCode(sourceLang);
		trgLang = convertLangCode(targetLang);
	}
	
	public String getSourceLanguage () {
		return srcLang;
	}
	
	public String getTargetLanguage () {
		return trgLang;
	}

//	private String unescape (String text) {
//		if ( text == null ) return "";
//		String tmp = text.replace("&#39;", "'");
//		tmp = tmp.replace("&lt;", "<");
//		tmp = tmp.replace("&gt;", ">");
//		tmp = tmp.replace("&quot;", "\"");
//		return tmp.replace("&amp;", "&");
//	}

	private String convertLangCode (String standardCode) {
		String code = standardCode.toLowerCase();
		if ( !code.startsWith("zh") && ( code.length() > 2 )) {
			code = code.substring(0, 2);
		}
		return code;
	}

	public IParameters getParameters () {
		// No parameters are used with this connector
		return null;
	}

	public void setParameters (IParameters params) {
		// No parameters are used with this connector
	}

}
