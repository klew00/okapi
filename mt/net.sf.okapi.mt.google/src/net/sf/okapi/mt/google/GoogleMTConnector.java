package net.sf.okapi.mt.google;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;

import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.translation.IQuery;
import net.sf.okapi.lib.translation.QueryResult;

public class GoogleMTConnector implements IQuery {

	private static final String address = "http://translate.google.com/translate_t";
	private static final String baseQuery = "?text=%s&hl=en&ie=UTF8&langpair=%s|%s&oe=UTF8";
	private static final Pattern pattern = Pattern.compile("<div\\sid=result_box .*?>(.*?)</div>");

	private String srcLang;
	private String trgLang;
	private String lastError;
	private QueryResult result;
	private int current = -1;
	
	public void close () {
		// Nothing to do
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

	public void open (String connectionString) {
		// Nothing to do
	}

	public int query (String plainText) {
		TextFragment tf = new TextFragment(plainText);
		return query(tf);
	}
	
	public int query (TextFragment text) {
		lastError = null;
		current = -1;
		try {
			String qtext = text.getCodedText();
			if ( text.hasCode() ) {
				StringBuilder tmp = new StringBuilder();
				for ( int i=0; i<qtext.length(); i++ ) {
					switch ( qtext.codePointAt(i) ) {
					case TextFragment.MARKER_OPENING:
					case TextFragment.MARKER_CLOSING:
					case TextFragment.MARKER_ISOLATED:
					case TextFragment.MARKER_SEGMENT:
						i++; // Skip second part of the code
						break;
					default:
						tmp.append(qtext.codePointAt(i));
					}
				}
				qtext = tmp.toString();
			}

			URL url = new URL(address + String.format(baseQuery,
				URLEncoder.encode(qtext, "UTF-8"), srcLang, trgLang));
			URLConnection conn = url.openConnection();
			// Make sure we send a user-agent property, otherwise we get 403 error
			conn.setRequestProperty("User-Agent", "");

			// Get the response
	        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
	        String line;
	        StringBuilder res = new StringBuilder();
	        while ( (line = rd.readLine()) != null ) {
	        	res.append(line);
	        }
	        rd.close();
	        
	        Matcher m = pattern.matcher(res.toString());
	        if ( m.find() ) {
				result = new QueryResult();
				result.source = text;
		        result.target = new TextFragment(unescape(m.group(1)));
				result.score = (text.hasCode() ? 98 : 99);
				current = 0;
	        }
		}
		catch ( UnsupportedEncodingException e ) {
			lastError = e.getLocalizedMessage();
		}
		catch ( MalformedURLException e ) {
			lastError = e.getLocalizedMessage();
		}
		catch ( IOException e ) {
			lastError = e.getLocalizedMessage();
		}
		return ((lastError==null) ? 1 : 0);
	}
	
	private String unescape (String text) {
		if ( text == null ) return "";
		String tmp = text.replace("&#39;", "'");
		tmp = tmp.replace("&lt;", "<");
		tmp = tmp.replace("&gt;", ">");
		tmp = tmp.replace("&quot;", "\"");
		return tmp.replace("&amp;", "&");
	}

	public void setAttribute (String name,
		String value)
	{
		// Not used with this MT engine
	}
	
	public void removeAttribute (String name) {
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

	private String convertLangCode (String standardCode) {
		String code = standardCode.toLowerCase();
		if ( !code.startsWith("zh") && ( code.length() > 2 )) {
			code = code.substring(0, 2);
		}
		return code;
	}

}
