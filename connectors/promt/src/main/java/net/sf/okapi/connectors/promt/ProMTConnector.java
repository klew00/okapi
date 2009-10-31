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

package net.sf.okapi.connectors.promt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiNotImplementedException;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.translation.IQuery;
import net.sf.okapi.lib.translation.QueryResult;
import net.sf.okapi.lib.translation.QueryUtil;

public class ProMTConnector implements IQuery {

	private static final String SERVICE = "/pts8/services/ptservice.asmx/TranslateText";
    private static final Pattern RESULTPATTERN = Pattern.compile("<string(.*?)>(.*?)</string>");
	
	private String srcLang;
	private String trgLang;
	private QueryResult result;
	private int current = -1;
	private Parameters params;
	private URL url;
	private long dirId;
	private HashMap<String, Long> dirIdentifiers;
	private QueryUtil qutil;

	public ProMTConnector () {
		params = new Parameters();
		initializePairs();
		qutil = new QueryUtil();
	}

	public String getName () {
		return "ProMT";
	}

	public String getSettingsDisplay () {
		return String.format("Server: %s", params.getHost());
	}
	
	public void close () {
		// Nothing to do
	}

	public void export (String outputPath) {
		throw new OkapiNotImplementedException("The export() method is not supported.");
	}

	public LocaleId getSourceLanguage () {
		return LocaleId.fromString(srcLang);
	}
	
	public LocaleId getTargetLanguage () {
		return LocaleId.fromString(trgLang);
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
		String tmp = params.getHost();
		// Make sure the URL does not end with separator
		if ( tmp.endsWith("/") ) tmp = tmp.substring(0, tmp.length()-1);
		else if ( tmp.endsWith("\\") ) tmp = tmp.substring(0, tmp.length()-1);
		// Set the full URL for the service
		try {
			url = new URL(tmp+SERVICE);
		}
		catch ( MalformedURLException e ) {
			throw new RuntimeException(String.format("Cannot open the connection to '%s'", tmp+SERVICE), e); 
		}
	}

	public int query (String text) {
		if ( Util.isEmpty(text) ) return 0;
		return queryUsingPOST(null, text);
	}

	public int query (TextFragment frag) {
		if ( !frag.hasText(false) ) return 0;
		return queryUsingPOST(frag, null);
	}
	
	// Either frag or plainText must be null
	private int queryUsingPOST (TextFragment frag,
		String plainText)
	{
		current = -1;
		OutputStreamWriter wr = null;
		BufferedReader rd = null;
		
		String text;
		if ( frag != null ) {
			text = qutil.separateCodesFromText(frag);
		}
		else {
			text = plainText;
		}

		if ( dirId == -1L ) return 0;
		try {
			// Try to authenticate if needed
			if ( !Util.isEmpty(params.getUsername()) ) {
				Authenticator.setDefault(new Authenticator() {
				    protected PasswordAuthentication getPasswordAuthentication() {
				        return new PasswordAuthentication(params.getUsername(), params.getPassword().toCharArray());
				    }
				});
			}
			
			// Open a connection
			URLConnection conn = url.openConnection();
//TODO: handle user/password
//			if ( !Util.isEmpty(params.getUsername()) ) {
//				String buf = String.format("%s:%s", params.getUsername(), params.getPassword());
//				BASE64Encoder enc = new BASE64Encoder();
//				conn.setRequestProperty("Authorization", "Basic " + enc.encode(buf.getBytes()));
//			}
			
			// Set the data
			//DirId=string&TplId=string&Text=string
			//524289
			String data = String.format("DirId=%d&TplId=%s&Text=%s",
				dirId, "General", URLEncoder.encode(text, "UTF-8"));

			// Post the data
			conn.setDoOutput(true);
			wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(data);
			wr.flush();
	        
	        // Get the response
	        rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8")));
	        String buffer;
	        StringBuilder tmp = new StringBuilder();
	        while (( buffer = rd.readLine() ) != null ) {
	            tmp.append(buffer);
	        }
	        
	        // Treat the output 
	        Matcher m = RESULTPATTERN.matcher(tmp.toString());
	        if ( m.find() ) {
	        	buffer = m.group(2);
	        	if ( !Util.isEmpty(buffer) ) {
	        		result = new QueryResult();
//TODO: replace by shared method	        		
	        		buffer = buffer.replace("&#39;", "'");
	        		buffer = buffer.replace("&lt;", "<");
	        		buffer = buffer.replace("&gt;", ">");
	        		buffer = buffer.replace("&quot;", "\"");
	        		buffer = buffer.replace("&amp;", "&");
	        		
	        		if ( frag != null ) {
	        			result.source = frag;
	        			result.target = qutil.createNewFragmentWithCodes(buffer);
	        		}
	        		else { // Was plain text
	        			result.source = new TextFragment(text);
	        			result.target = new TextFragment(buffer);
	        		}
	        		
	        		result.score = 95; // Arbitrary score for MT
	        		result.origin = Util.ORIGIN_MT;
	    			current = 0;
	        	}
	        }
		}
		catch ( MalformedURLException e ) {
			throw new RuntimeException("Error during the query.", e);
		}
		catch ( IOException e ) {
e.printStackTrace();
			throw new RuntimeException("Error during the query.", e);
		}
		finally {
        	try {
        		if ( wr != null ) wr.close();
    	        if ( rd != null ) rd.close();
   	        }
       		catch ( IOException e ) {
       			// Ignore this exception
	        }
		}
		return current+1;
	}
	
	public void removeAttribute (String name) {
		//TODO: use domain
	}

	public void clearAttributes () {
		//TODO: use domain
	}

	public void setAttribute (String name,
		String value)
	{
		//TODO: use domain
	}

	public void setLanguages (LocaleId sourceLocale,
		LocaleId targetLocale)
	{
		srcLang = toInternalCode(sourceLocale);
		trgLang = toInternalCode(targetLocale);
		String pair = srcLang + "_" + trgLang;
		if ( dirIdentifiers.containsKey(pair) ) {
			dirId = dirIdentifiers.get(pair);
		}
		else dirId = -1L;
	}
		
	private String toInternalCode (LocaleId locale) {
		// Reduce the locale code to its base language part
		return locale.getLanguage();
	}

	public IParameters getParameters () {
		return params;
	}

	public void setParameters (IParameters params) {
		params = (Parameters)params;
	}

	private void initializePairs () {
//TODO: Get the list from /pts8/services/ptservice.asmx/GetPTServiceDataSet		
		dirId = -1L;
		dirIdentifiers = new HashMap<String, Long>();
		dirIdentifiers.put("en_ru", 131073L);
		dirIdentifiers.put("ru_en", 65538L);
		dirIdentifiers.put("de_ru", 131076L);
		dirIdentifiers.put("ru_de", 262146L);
		dirIdentifiers.put("fr_ru", 131080L);
		dirIdentifiers.put("ru_fr", 524290L);
		dirIdentifiers.put("es_ru", 131104L);
		dirIdentifiers.put("ru_es", 2097154L);
		dirIdentifiers.put("en_de", 262145L);
		dirIdentifiers.put("de_en", 65540L);
		dirIdentifiers.put("en_fr", 524289L);
		dirIdentifiers.put("fr_en", 65544L);
		dirIdentifiers.put("de_fr", 524292L);
		dirIdentifiers.put("fr_de", 262152L);
		dirIdentifiers.put("en_it", 1048577L);
		dirIdentifiers.put("it_en", 65552L);
		dirIdentifiers.put("en_es", 2097153L);
		dirIdentifiers.put("es_en", 65568L);
		dirIdentifiers.put("de_es", 2097156L);
		dirIdentifiers.put("es_de", 262176L);
		dirIdentifiers.put("fr_es", 2097160L);
		dirIdentifiers.put("es_fr", 524320L);
		dirIdentifiers.put("en_pt", 4194305L);
		dirIdentifiers.put("pt_en", 65600L);
	}

}

