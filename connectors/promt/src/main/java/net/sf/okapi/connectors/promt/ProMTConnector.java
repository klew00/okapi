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
import java.io.StringReader;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import net.sf.okapi.common.DefaultEntityResolver;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiNotImplementedException;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.lib.translation.IQuery;
import net.sf.okapi.lib.translation.QueryResult;
import net.sf.okapi.lib.translation.QueryUtil;

public class ProMTConnector implements IQuery {

	private static final String PTS8_SERVICE = "pts8/services/ptservice.asmx/";
	private static final String TRANSLATETEXT = "TranslateText";
	private static final String GETPTSERVICEDATASET = "GetPTServiceDataSet";

	private static final String PTSXLIFF_SERVICE = "ptsxliff/PTSXLIFFTranslator.asmx/";
	private static final String TRANSLATEFORMATTEDTEXT = "TranslateFormattedText";

	private static final Pattern RESULTPATTERN = Pattern.compile("<string(.*?)>(.*?)</string>");

	private String srcLang;
	private Locale srcLoc;
	private String trgLang;
	private Locale trgLoc;
	private QueryResult result;
	private int current = -1;
	private Parameters params;
	private URL serviceURL;
	private String dirId;
	private HashMap<String, String> dirIdentifiers;
	private QueryUtil qutil;

	public ProMTConnector () {
		params = new Parameters();
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

	private String getHost () {
		String tmp = params.getHost();
		// Make sure the host ends with a separator
		if ( !tmp.endsWith("/") && tmp.endsWith("\\") ) {
			return tmp + "/";
		}
		return tmp;
	}
	
	public void open () {
		// Try to authenticate if needed
		if ( !Util.isEmpty(params.getUsername()) ) {
			Authenticator.setDefault(new Authenticator() {
			    protected PasswordAuthentication getPasswordAuthentication() {
			        return new PasswordAuthentication(params.getUsername(), params.getPassword().toCharArray());
			    }
			});
		}
		
		initializePairsFromServer();
		// Set the full URL for the service
		try {
			//serviceURL = new URL(getHost()+PTS8_SERVICE+TRANSLATETEXT);
			serviceURL = new URL(getHost()+PTSXLIFF_SERVICE+TRANSLATEFORMATTEDTEXT);
		}
		catch ( MalformedURLException e ) {
			throw new RuntimeException(String.format("Cannot open the connection to '%s'", getHost()+PTS8_SERVICE), e); 
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
			//text = qutil.separateCodesFromText(frag);
			text = qutil.toXLIFF(frag);
		}
		else {
			text = plainText;
		}

		if ( dirId == null ) return 0;
		try {
			// Open a connection
			URLConnection conn = serviceURL.openConnection();
//TODO: handle user/password
//			if ( !Util.isEmpty(params.getUsername()) ) {
//				String buf = String.format("%s:%s", params.getUsername(), params.getPassword());
//				BASE64Encoder enc = new BASE64Encoder();
//				conn.setRequestProperty("Authorization", "Basic " + enc.encode(buf.getBytes()));
//			}
			
			// Set the data
			//DirId=string&TplId=string&Text=string
//			String data = String.format("DirId=%s&TplId=%s&Text=%s",
//				dirId, "General", URLEncoder.encode(text, "UTF-8"));

			// DirId=string&TplId=string&strText=string&FileType=string
			String data = String.format("DirId=%s&TplId=%s&strText=%s&FileType=text/xliff",
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
	        		buffer = buffer.replace("&apos;", "'");
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

	/**
	 * Sets the dirId value if possible. This will not be set if
	 * either the lookup table is not created or the locales are not set,
	 * allowing to call setLanguages() or open() in any order.
	 */
	private void setDirectionId () {
		// Initialize to 'no support'
		dirId = null;
		
		// If the lookup table is not yet initialized it's ok
		// It means the direction id will be set on open()
		if ( dirIdentifiers == null ) return;
		
		// Create the name to lookup
		if (( srcLoc != null ) && ( trgLoc != null )) {
			// getDisplayLanguage will not return null
			String pair = srcLoc.getDisplayLanguage(Locale.ENGLISH)
				+ "-" + trgLoc.getDisplayLanguage(Locale.ENGLISH);
			// Get the dirId (or null if not found)
			dirId = dirIdentifiers.get(pair);
		}
	}
	
	public void setLanguages (LocaleId sourceLocale,
		LocaleId targetLocale)
	{
		// Convert the codes
		srcLang = toInternalCode(sourceLocale);
		trgLang = toInternalCode(targetLocale);
		srcLoc = sourceLocale.toJavaLocale();
		trgLoc = targetLocale.toJavaLocale();
		// Try to set the direction
		setDirectionId();
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

//	private void initializePairs () {
//		dirId = null;
//		dirIdentifiers = new HashMap<String, String>();
//		dirIdentifiers.put("en_ru", "131073");
//		dirIdentifiers.put("ru_en", "65538");
//		dirIdentifiers.put("de_ru", "131076");
//		dirIdentifiers.put("ru_de", "262146");
//		dirIdentifiers.put("fr_ru", "131080");
//		dirIdentifiers.put("ru_fr", "524290");
//		dirIdentifiers.put("es_ru", "131104");
//		dirIdentifiers.put("ru_es", "2097154");
//		dirIdentifiers.put("en_de", "262145");
//		dirIdentifiers.put("de_en", "65540");
//		dirIdentifiers.put("en_fr", "524289");
//		dirIdentifiers.put("fr_en", "65544");
//		dirIdentifiers.put("de_fr", "524292");
//		dirIdentifiers.put("fr_de", "262152");
//		dirIdentifiers.put("en_it", "1048577");
//		dirIdentifiers.put("it_en", "65552");
//		dirIdentifiers.put("en_es", "2097153");
//		dirIdentifiers.put("es_en", "65568");
//		dirIdentifiers.put("de_es", "2097156");
//		dirIdentifiers.put("es_de", "262176");
//		dirIdentifiers.put("fr_es", "2097160");
//		dirIdentifiers.put("es_fr", "524320");
//		dirIdentifiers.put("en_pt", "4194305");
//		dirIdentifiers.put("pt_en", "65600");
//	}

	private void initializePairsFromServer () {
		dirId = null;
		dirIdentifiers = new HashMap<String, String>();
		OutputStreamWriter wr = null;
		BufferedReader rd = null;
		try {
			// Open a connection
			URL url = new URL(getHost()+PTS8_SERVICE+GETPTSERVICEDATASET);
			URLConnection conn = url.openConnection();
			
			// Post the data
			conn.setDoOutput(true);
			wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write("");
			wr.flush();
	        
	        // Get the response
	        rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8")));
	        String buffer;
	        StringBuilder tmp = new StringBuilder();
	        while (( buffer = rd.readLine() ) != null ) {
	            tmp.append(buffer);
	        }
	        
	        // Treat the result
			DocumentBuilderFactory Fact = DocumentBuilderFactory.newInstance();
			Fact.setValidating(false);
			Fact.setNamespaceAware(true);
			DocumentBuilder docBuilder = Fact.newDocumentBuilder();
			docBuilder.setEntityResolver(new DefaultEntityResolver());
			Document doc;
			doc = docBuilder.parse(new InputSource(new StringReader(tmp.toString())));
	        NodeList nodes = doc.getElementsByTagName("Directions");
	        for ( int i=0; i<nodes.getLength(); i++ ) {
	        	Element elem = (Element)nodes.item(i);
				NodeList dirs = elem.getChildNodes();
				String name = null;
				String id = null;
				// Gather the id and name
				for ( int j=0; j<dirs.getLength(); j++ ) {
					Node node = dirs.item(j);
					if ( "id".equals(node.getLocalName()) ) {
						id = Util.getTextContent(node);
					}
					else if ( "Name".equals(node.getLocalName()) ) {
						name = Util.getTextContent(node); 
					}
				}
				// Add the entry if we can
				if ( !Util.isEmpty(id) && !Util.isEmpty(name) ) {
					dirIdentifiers.put(name, id);
				}
	        }
		}
		catch ( MalformedURLException e ) {
			throw new RuntimeException("Error during the initialization.", e);
		}
		catch ( IOException e ) {
			throw new RuntimeException("Error during the initialization.", e);
		}
		catch ( ParserConfigurationException e ) {
			throw new RuntimeException("Error during the initialization.", e);
		}
		catch ( SAXException e ) {
			throw new RuntimeException("Error during the initialization.", e);
		}
		finally {
			setDirectionId();
        	try {
        		if ( wr != null ) wr.close();
    	        if ( rd != null ) rd.close();
   	        }
       		catch ( IOException e ) {
       			// Ignore this exception
	        }
		}
	}


	public static void main (String args[]) {
		ProMTConnector con = new ProMTConnector();
		con.setLanguages(LocaleId.fromString("en"), LocaleId.fromString("fr"));
		con.open();
		
		TextFragment frag = new TextFragment("This <b>is an</b> example.");
		frag.changeToCode(13, 17, TagType.CLOSING, "b");
		frag.changeToCode(5, 8, TagType.OPENING, "b");
		con.query(frag);
		if ( con.hasNext() ) {
			System.out.println(con.next().target.toString());
		}
		
	}
}

