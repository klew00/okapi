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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiNotImplementedException;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.translation.IQuery;
import net.sf.okapi.lib.translation.QueryResult;

public class ProMTConnector implements IQuery {

	private static final String SERVICE = "/pts8/services/ptservice.asmx/TranslateText";
	
	private String srcLang;
	private String trgLang;
	private List<QueryResult> results;
	private int current = -1;
	private Parameters params;
	private URL url;

	public ProMTConnector () {
		params = new Parameters();
	}

	public String getName () {
		return "ProMT";
	}

	public String getSettingsDisplay () {
		return String.format("Server: %s", params.getServerURL());
	}
	
	public void close () {
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
		if ( results == null ) return false;
		if ( current >= results.size() ) {
			current = -1;
		}
		return (current > -1);
	}

	public QueryResult next () {
		if ( results == null ) return null;
		if (( current > -1 ) && ( current < results.size() )) {
			current++;
			return results.get(current-1);
		}
		current = -1;
		return null;
	}

	public void open () {
		results = new ArrayList<QueryResult>();
		String tmp = params.getServerURL();
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
		results.clear();
		current = -1;
		if ( Util.isEmpty(text) ) return 0;
		return queryPost(text);
	}

	public int query (TextFragment frag) {
		results.clear();
		current = -1;
		if ( !frag.hasText(false) ) return 0;
		return queryPost(frag.toString());
	}
	
	private int queryPost (String text) {
		OutputStreamWriter wr = null;
		BufferedReader rd = null;
		try {
			// Open a connection
			URLConnection conn = url.openConnection();
			
			// Set the parameters
			//DirId=string&TplId=string&Text=string
			//524289
			String data = String.format("DirId=%s&TplId=%s&Text=%s",
				"524289", "General", URLEncoder.encode(text, "UTF-8"));

			// Post the data
			conn.setDoOutput(true);
			wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(data);
			wr.flush();
	        
	        // Get the response
	        rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        String line;
	        StringBuilder tmp = new StringBuilder();
	        while (( line = rd.readLine() ) != null ) {
	            tmp.append(line);
	        }
	        
	        // Treat the output 
	        line = tmp.toString();
		}
		catch ( MalformedURLException e ) {
			e.printStackTrace();
		}
		catch ( IOException e ) {
			e.printStackTrace();
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
		return 0;
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
	}
		
	private String toInternalCode (LocaleId locale) {
		// Reduce the locale code to its language part
		return locale.getLanguage();
	}

	public IParameters getParameters () {
		return params;
	}

	public void setParameters (IParameters params) {
		params = (Parameters)params;
	}

	public static void main(String[] args) {
		ProMTConnector conn = new ProMTConnector();
		conn.open();
		conn.query("Hello World!");
	}
	
}

