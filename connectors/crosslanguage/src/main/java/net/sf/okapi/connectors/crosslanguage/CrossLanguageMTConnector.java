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

package net.sf.okapi.connectors.crosslanguage;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import javax.xml.namespace.QName;

import com.crosslang.ws.ArrayOfstring;
import com.crosslang.ws.Gateway;
import com.crosslang.ws.IGateway;

import net.sf.okapi.common.Base64;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.translation.IQuery;
import net.sf.okapi.lib.translation.QueryResult;
import net.sf.okapi.lib.translation.QueryUtil;

/**
 * Connector for the CrossLanguage MT Gateway Web services.
 */
public class CrossLanguageMTConnector implements IQuery {

	private Parameters params;
	private IGateway gateway;
	private LocaleId srcLoc;
	private LocaleId trgLoc;
	private QueryResult result;
	private int current = -1;
	private QueryUtil util;
	private ArrayOfstring options;
	private Charset csUTF8;

	public CrossLanguageMTConnector () {
		params = new Parameters();
		util = new QueryUtil();
	}
	
	public void close () {
		gateway = null;
	}

	public String getName () {
		return "CrossLanguage-MT";
	}

	public String getSettingsDisplay () {
		return "Server: " + params.getServerURL();
	}

	public boolean hasNext () {
		return (current>-1);
	}
	
	public QueryResult next () {
		if ( current > -1 ) { // Only one result
			current = -1;
			return result;
		}
		return null;
	}

	public void open () {
		URL url;
		try {
			url = new URL(params.getServerURL());
			Gateway gatewayService = new Gateway(url, new QName("http://tempuri.org/", "Gateway"));
			gateway = gatewayService.getDefaultBinding();
			
			options = new ArrayOfstring();
			List<String> list = options.getString();
			if ( !Util.isEmpty(params.getDictionary()) ) {
				list.add("Dictionary");
				list.add(params.getDictionary());
			}
			list.add("Formality");
			list.add(params.getPoliteForm() ? "FormalOrPolite" : "Informal");
			
			csUTF8 = Charset.forName("UTF8");
		}
		catch ( MalformedURLException e ) {
			throw new RuntimeException("URL error when creating service.", e);
		}
	}

	public int query (String plainText) {
		return queryString(plainText);
	}
	
	public int query (TextFragment text) {
		return queryFile(text);
	}
	
	public int queryString (String text) {
		current = -1;
		try {
			// Call the service
			String res = gateway.getTranslatedStringWithOptions(params.getUser(), params.getApiKey(),
				params.getDepartmentId(), text, options);
			if ( res == null ) return 0;
			
			// Process the result
			result = new QueryResult();
			result.source = new TextFragment(text);
			result.target = new TextFragment(res);
			result.score = 95; // Arbitrary score for MT
			result.origin = Util.ORIGIN_MT;
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
			String qtext = util.toCodedHTML(text);
			String data = Base64.encodeString(qtext);
			// Call the service
			String res = gateway.getTranslatedFileWithOptions(params.getUser(), params.getApiKey(),
				params.getDepartmentId(), data, "html", options);
			if ( res == null ) return 0;
			
			// Process the result
			result = new QueryResult();
			result.source = text;
			if ( text.hasCode() ) {
				result.target = new TextFragment(util.fromCodedHTML(Base64.decodeString(res), text),
					text.getCodes());
			}
			else {
				result.target = new TextFragment(util.fromCodedHTML(res, text));
			}
			result.score = 95; // Arbitrary score for MT
			result.origin = Util.ORIGIN_MT;
			current = 0;
		}
		catch ( Throwable e ) {
			throw new RuntimeException("Error querying the server: " + e.getMessage(), e);
		}
		return ((current==0) ? 1 : 0);
	}

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

	public void setLanguages (LocaleId sourceLocale,
		LocaleId targetLocale)
	{
		// Just used for get methods in this MT engine
		srcLoc = sourceLocale;
		trgLoc = targetLocale;
	}
	
	public LocaleId getSourceLanguage () {
		return srcLoc;
	}
	
	public LocaleId getTargetLanguage () {
		return trgLoc;
	}

	public IParameters getParameters () {
		return params;
	}

	public void setParameters (IParameters params) {
		params = (Parameters)params;
	}

}
