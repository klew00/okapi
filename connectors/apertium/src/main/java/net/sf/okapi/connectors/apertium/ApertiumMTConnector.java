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
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.translation.IQuery;
import net.sf.okapi.lib.translation.QueryResult;
import net.sf.okapi.lib.translation.QueryUtil;

public class ApertiumMTConnector implements IQuery {

	private Parameters params;
	private String srcLang;
	private String trgLang;
	private String pair;
	private boolean hasNext;
	private QueryResult result;
	private Pattern cerPattern;
	private HTMLCharacterEntities entities;
	private QueryUtil store;
	
	public ApertiumMTConnector () {
		params = new Parameters();
		cerPattern = Pattern.compile("(&\\w*?;)");
		entities = new HTMLCharacterEntities();
		entities.ensureInitialization(true);
		store = new QueryUtil();
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

	public LocaleId getSourceLanguage () {
		return LocaleId.fromString(srcLang);
	}

	public LocaleId getTargetLanguage () {
		return LocaleId.fromString(trgLang);
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
		return query(new TextFragment(plainText));
	}

	public int query (TextFragment frag) {
		result = null;
		hasNext = false;
		String plainText;
		if ( frag.hasCode() ) {
			plainText = store.toCodedHTML(frag);
		}
		else {
			plainText = frag.toString();
		}
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
			String transText;
			while (( transText = rd.readLine()) != null ) {
				if ( res.length() > 0 ) res.append("\n");
				res.append(transText);
			}
	        rd.close();
	        transText = res.toString();

	        if ( transText.startsWith("Error: Mode ") ) {
	        	// Most likely this pair is not supported
	        	return 0;
	        }
	        
	        // Unescape the CERs if needed
			Matcher m;
			while ( true ) {
				m = cerPattern.matcher(transText);
				if ( !m.find() ) break;
				int val = entities.lookupReference(m.group(0));
				if ( val != -1 ) {
					transText = transText.replace(m.group(0), String.valueOf((char)val));
				}
				else { // Unknown entity
					//TODO: replace by something meaningful to allow continuing the replacements
					break; // Temporary, to avoid infinite loop
				}
			}
	        
	        result = new QueryResult();
	        result.score = 95; // Fixed score for MT
	        result.origin = Util.ORIGIN_MT;
        	result.source = frag;
			if ( frag.hasCode() ) {
				result.target = new TextFragment(store.fromCodedHTML(transText, frag),
					frag.getCodes());
			}
			else {
				result.target = new TextFragment(store.fromCodedHTML(transText, frag));
			}
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

	public void setLanguages (LocaleId sourceLocale,
		LocaleId targetLocale)
	{
		srcLang = toInternalCode(sourceLocale);
		trgLang = toInternalCode(targetLocale);
		pair = String.format("%s-%s", srcLang, trgLang);
	}

	private String toInternalCode (LocaleId standardCode) {
		String lang = standardCode.getLanguage();
		String reg = standardCode.getRegion();
		if ( reg != null ) {
			// Temporary fix for the Aranese case (until we get real LocaleID)
			if ( reg.equals("aran") ) lang += "_aran";
			// Temporary fix for the Brazilian Portuguese case (until we get real LocaleID)
			if ( reg.equals("br") ) lang += "_BR";
		}
		return lang;
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
