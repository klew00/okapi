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

package net.sf.okapi.connectors.apertium;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.okapi.common.HTMLCharacterEntities;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.translation.IQuery;
import net.sf.okapi.lib.translation.QueryResult;

public class ApertiumMTConnector implements IQuery {

	private Parameters params;
	private String srcLang;
	private String trgLang;
	private String pair;
	private boolean hasNext;
	private QueryResult result;
	private Pattern cerPattern;
	private HTMLCharacterEntities entities;
	
	public ApertiumMTConnector () {
		params = new Parameters();
		cerPattern = Pattern.compile("(&\\w*?;)");
		entities = new HTMLCharacterEntities();
		entities.ensureInitialization(true);
	}
	
	public String getName () {
		return "Apertium MT";
	}

	public String getSettingsDisplay () {
		return "Server: "+params.getServer();
	}
	
	public void close () {
		// Nothing to do
		hasNext = false;
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
		return hasNext;
	}

	public QueryResult next () {
		if ( hasNext ) {
			hasNext = false;
			return result;
		}
		// Else: next was already called.
		result = null;
		return result;
	}

	public void open () {
		// Nothing to do
	}

	public int query (String plainText) {
		result = null;
		hasNext = false;
		if ( Util.isEmpty(plainText) ) {
			return 0;
		}
		try {
			URL url = new URL(String.format("%s?mode=%s&text=%s",
				params.getServer(), pair,
				URLEncoder.encode(plainText, "UTF-8").replace("+", "%20")));
			URLConnection conn = url.openConnection();

			// Get the response
	        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
	        StringBuilder res = new StringBuilder();
			String line;
			while (( line = rd.readLine()) != null ) {
				if ( res.length() > 0 ) res.append("\n");
				res.append(line);
			}
	        rd.close();
	        String text = res.toString();

	        if ( text.startsWith("Error: Mode ") ) {
	        	// Most likely this pair is not supported
	        	return 0;
	        }
	        
	        // Unescape the CERs if needed
			Matcher m;
			while ( true ) {
				m = cerPattern.matcher(text);
				if ( !m.find() ) break;
				int val = entities.lookupReference(m.group(0));
				if ( val != -1 ) {
					text = text.replace(m.group(0), String.valueOf((char)val));
				}
				else { // Unknown entity
					//TODO: replace by something meaningful to allow continuing the replacements
					break; // Temporary, to avoid infinite loop
				}
			}
	        
	        result = new QueryResult();
	        result.score = 95; // Fixed score for MT
        	result.source = new TextFragment(plainText);
        	result.target = new TextFragment(text);
			hasNext = (result != null);
			return (hasNext ? 1 : 0);
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
	}

	public int query (TextFragment text) {
		//TODO: Deal with inline codes, maybe using generic codes
		String tmp = text.toString();
		return query(tmp);
	}

	public void setLanguages (String sourceLang,
		String targetLang)
	{
		srcLang = toInternalCode(sourceLang);
		trgLang = toInternalCode(targetLang);
		pair = String.format("%s-%s", srcLang, trgLang);
	}

	private String toInternalCode (String standardCode) {
		String[] codes = Util.splitLanguageCode(standardCode);
		if ( codes[1] != null ) {
			// Temporary fix for the Aranese case (until we get real LocaleID)
			if ( codes[1].equals("aran") ) codes[0] += "_aran";
			// Temporary fix for the Brazilian Portuguese case (until we get real LocaleID)
			if ( codes[1].equals("br") ) codes[0] += "_BR";
		}
		return codes[0];
	}

	public IParameters getParameters () {
		return params;
	}

	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}

	public void clearAttributes () {
		// Nothing to do
	}

	public void removeAttribute (String name) {
		// Nothing to do
	}

	public void setAttribute (String name,
		String value)
	{
		// Nothing to do
	}

}