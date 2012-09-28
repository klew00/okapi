/*===========================================================================
  Copyright (C) 2012 by the Okapi Framework contributors
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

package net.sf.okapi.steps.enrycher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;

public class EnrycherClient {

	private Parameters params;
	private String lang;
	
	public EnrycherClient () {
		params = new Parameters();
		lang = "en";
	}
	
	public IParameters getParameters () {
		return params;
	}
	
	public void setParameters (Parameters params) {
		this.params = params;
	}
	
	public void setLocale (LocaleId locId) {
		lang = locId.getLanguage();
	}

	public String processContent (String text) {
		OutputStreamWriter wr = null;
		BufferedReader rd = null;
		try {
			// Prepare the request
			URL url = new URL(params.getBaseUrl()+lang+"/run.html5its2");
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "text/html");
			wr = new OutputStreamWriter(conn.getOutputStream());
			//TODO HTML conversion
			String data = text;
	    
			// Post the request
			wr.write(data);
			wr.flush();

			// Get the response
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
		    wr.close();
		    rd.close();
		    return sb.toString();
		}
		catch ( MalformedURLException e ) {
			throw new RuntimeException("Invalid URL:\n"+e.getMessage());
		}
		catch ( IOException e ) {
			throw new RuntimeException("Input/Output error:\n"+e.getMessage());
		}
		finally {
			try {
				if ( wr != null ) wr.close();
				if ( rd != null ) rd.close();
			}
			catch ( IOException e ) {
				// Skip this one
			}			
		}
	}
	
}
