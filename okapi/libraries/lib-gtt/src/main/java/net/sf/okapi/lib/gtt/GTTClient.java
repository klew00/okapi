/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package net.sf.okapi.lib.gtt;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;

/**
 * Basic Google Translator Toolkit client allowing to upload and download documents from a java application.
 * See documentation of the API here: http://code.google.com/apis/gtt/
 */
public class GTTClient {

	private static final String HYPHENS = "--";
	private static final String BOUNDARY = "END_OF_PART";
	private static final String LINEBREAK = "\r\n"; // HTTP uses CR+LF
	private static final int RESCODE_OK = 200; 
	private static final int RESCODE_CREATED = 201; 
	private static final int MAXBUFFERSIZE = 1024*8; 

	private DocumentBuilder docBuilder;
	private String credentials;
	private String clientApp;
	private String srcLang;
	private String trgLang;

	public GTTClient (String clientApp) {
		this.clientApp = clientApp;
		DocumentBuilderFactory Fact = DocumentBuilderFactory.newInstance();
		Fact.setValidating(false);
		try {
			docBuilder = Fact.newDocumentBuilder();
		}
		catch ( ParserConfigurationException e ) {
			throw new RuntimeException("Error creating document builder.", e);
		}
	}
	
	public void setCredentials (String email,
		String password)
	{
		DataOutputStream dos = null;
		try {
			URL url = new URL("https://www.google.com/accounts/ClientLogin");
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
		    conn.setDoInput(true);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			dos = new DataOutputStream(conn.getOutputStream());
			writeParameters(dos,
				"accountType", "GOOGLE",
				"Email", email,
				"Passwd", password,
				"service", "gtrans",
				"source", clientApp);
			dos.flush();
			dos.close();
			dos = null;

			int code =  conn.getResponseCode();
			if ( code == RESCODE_OK ) {
				String resp = readResponse(conn);
				int n = resp.indexOf("Auth=");
				if ( n == -1 ) {
					throw new RuntimeException("Invalid response: Auth field missing.");
				}
				credentials = "GoogleLogin auth=" + resp.substring(n+5).trim();
			}
			else {
				throw new RuntimeException("Cannot connect to Google Translator Toolkit:\n"+conn.getResponseMessage());
			}
		}
		catch ( MalformedURLException e ) {
			throw new RuntimeException("Cannot connect to Google Translator Toolkit.", e);
		}
		catch ( IOException e ) {
			throw new RuntimeException("Cannot connect to Google Translator Toolkit.", e);
		}
		finally {
			if ( dos != null ) {
				try {
					dos.close();
				}
				catch ( IOException e ) {
					// Swallow this one
				}
			}
		}
	}
	
	public void setLanguages (LocaleId srcLoc,
		LocaleId trgLoc)
	{
		srcLang = toInternalCode(srcLoc);
		trgLang = toInternalCode(trgLoc);
	}
	
	private String toInternalCode (LocaleId locale) {
		String code = locale.toBCP47();
		if ( !code.startsWith("zh") && ( code.length() > 2 )) {
			code = code.substring(0, 2);
		}
		return code;
	}	

	public String createTM (String name) {
		String id = null;
		DataOutputStream dos = null;
		try {
			URL url = new URL("https://translate.google.com/toolkit/feeds/tm");
			HttpURLConnection conn = createConnection(url, "POST");
			conn.setRequestProperty("Content-Type", "application/atom+xml");

			dos = new DataOutputStream(conn.getOutputStream());
			
			String tmp = String.format("<?xml version='1.0' encoding='UTF-8'?>"
				+ "<entry xmlns='http://www.w3.org/2005/Atom' xmlns:gtt='http://schemas.google.com/gtt/2009/11'>"
				+ "<title>%s</title>"
				+ "<gtt:scope>private</gtt:scope>"
				+ "</entry>",
				name);
			dos.writeBytes(tmp);

			dos.flush();
			dos.close();
			
			int code =  conn.getResponseCode();
			if ( code == RESCODE_CREATED ) {
				Document doc = docBuilder.parse(conn.getInputStream());
				Element elem = (Element)doc.getDocumentElement().getFirstChild();
				id = elem.getTextContent();
				id = id.substring(id.lastIndexOf('/')+1);
			}
		}
		catch ( MalformedURLException e ) {
			throw new RuntimeException("Cannot create a TM.", e);
		}
		catch ( IOException e ) {
			throw new RuntimeException("Cannot create a TM.", e);
		}
		catch ( SAXException e ) {
			throw new RuntimeException("Cannot create a TM.", e);
		}
		finally {
			if ( dos != null ) {
				try {
					dos.close();
				}
				catch ( IOException e ) {
					// Swallow this one
				}
			}
		}
		return id;
	}
	
	public String uploadDocument (String path,
		String name,
		String tmId)
	{
		String id = null;
		FileInputStream fis = null;
		DataOutputStream dos = null;
		try {
			// Start by opening the input, making sure it exists
			fis = new FileInputStream(path);

			// Default name is the path
			if ( name == null ) {
				name = path;
			}
			
			URL url = new URL("https://translate.google.com/toolkit/feeds/documents");
			HttpURLConnection conn = createConnection(url, "POST");
			conn.setRequestProperty("Content-Type", "multipart/related; boundary="+BOUNDARY);
			conn.setRequestProperty("Slug", name);

			dos = new DataOutputStream(conn.getOutputStream());
			
			// Atom header
			dos.writeBytes(HYPHENS + BOUNDARY + LINEBREAK);
			dos.writeBytes("Content-Type: application/atom+xml" + LINEBREAK + LINEBREAK);
			String tmp = String.format("<?xml version='1.0' encoding='UTF-8'?>" + LINEBREAK
				+ "<entry xmlns='http://www.w3.org/2005/Atom' xmlns:gtt='http://schemas.google.com/gtt/2009/11'>" + LINEBREAK
				+ "<title>%s</title>" + LINEBREAK
				+ "<gtt:sourceLanguage>%s</gtt:sourceLanguage>" + LINEBREAK
				+ "<gtt:targetLanguage>%s</gtt:targetLanguage>" + LINEBREAK,
				name,
				srcLang,
				trgLang);
				
			if ( tmId != null ) {
				tmp = tmp
				+ "<gtt:translationMemory>"
				+ "<link href='https://translate.google.com/toolkit/feeds/tm/"+tmId+"'/>"
				+ "</gtt:translationMemory>";
			}

			tmp = tmp
			    + "</entry>" + LINEBREAK + LINEBREAK;
			
			dos.writeBytes(tmp);

			// Document to upload
			dos.writeBytes(HYPHENS + BOUNDARY + LINEBREAK);
			dos.writeBytes("Content-Type: text/html" + LINEBREAK + LINEBREAK);
	
			int bytesAvailable = fis.available();
			int bufferSize = Math.min(bytesAvailable, MAXBUFFERSIZE);
			byte[] buffer = new byte[bufferSize];
			// Read and write the file
			int bytesRead = fis.read(buffer, 0, bufferSize);
			while ( bytesRead > 0 ) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fis.available();
			    bufferSize = Math.min(bytesAvailable, MAXBUFFERSIZE);
			    bytesRead = fis.read(buffer, 0, bufferSize);
			}
			fis.close(); fis = null;
			
			dos.writeBytes(LINEBREAK + LINEBREAK);

			// End of content
			dos.writeBytes(HYPHENS + BOUNDARY + HYPHENS + LINEBREAK);
			dos.flush();
			dos.close();
			
			int code =  conn.getResponseCode();
			if ( code == RESCODE_CREATED ) {
				Document doc = docBuilder.parse(conn.getInputStream());
				Element elem = (Element)doc.getDocumentElement().getFirstChild();
				id = elem.getTextContent();
				id = id.substring(id.lastIndexOf('/')+1);
			}
			
		}
		catch ( MalformedURLException e ) {
			throw new RuntimeException("Cannot upload document.", e);
		}
		catch ( IOException e ) {
			throw new RuntimeException("Cannot upload document.", e);
		}
		catch ( SAXException e ) {
			throw new RuntimeException("Cannot upload document.", e);
		}
		finally {
			if ( dos != null ) {
				try {
					dos.close();
				}
				catch ( IOException e ) {
					// Swallow this one
				}
			}
		}
		return id;
	}
	
	public void downloadDocument (String docId,
		File outputFile)
	{
		try {
			URL url = new URL("https://translate.google.com/toolkit/feeds/documents/export/"+docId);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestProperty("Authorization", credentials);
			if ( conn.getResponseCode() == RESCODE_OK ) {
				String resp = readResponse(conn);
				// Save the file
				Util.createDirectories(outputFile.getPath());
				OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8");
				osw.write(resp);
				osw.close();
			}
		}
		catch ( MalformedURLException e ) {
			throw new RuntimeException("Cannot download document.", e);
		}
		catch ( IOException e ) {
			throw new RuntimeException("Cannot download document.", e);
		}
	}
	
	public int deleteDocument (String docId,
		boolean deletePermanently)
	{
		int code = 0;
		try {
			String tmp = "https://translate.google.com/toolkit/feeds/documents/"+docId;
			if ( deletePermanently )  {
				tmp = tmp + "?delete=true";
			}
			URL url = new URL(tmp);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("DELETE");
			conn.setRequestProperty("Authorization", credentials);
			code = conn.getResponseCode();
		}
		catch ( MalformedURLException e ) {
			throw new RuntimeException("Cannot delete document.", e);
		}
		catch ( IOException e ) {
			throw new RuntimeException("Cannot delete document.", e);
		}
		return code;
	}
	
	public int deleteTM (String tmId) {
		int code = 0;
		try {
			URL url = new URL("https://translate.google.com/toolkit/feeds/tm/"+tmId);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("DELETE");
			conn.setRequestProperty("Authorization", credentials);
			code = conn.getResponseCode();
		}
		catch ( MalformedURLException e ) {
			throw new RuntimeException("Cannot delete a TM.", e);
		}
		catch ( IOException e ) {
			throw new RuntimeException("Cannot delete a TM.", e);
		}
		return code;
	}
	
	private void writeParameters (DataOutputStream dos,
		String... args)
		throws IOException
	{
		StringBuilder tmp = new StringBuilder();
		for ( int i=0; i<args.length; i++ ) {
			if ( i > 0 ) tmp.append("&");
			tmp.append(String.format("%s=%s", args[i], URLEncoder.encode(args[i+1], "UTF-8")));
			i++;
		}
		dos.writeBytes(tmp.toString());
	}

	/**
	 * Gets information about a given resource for a given locale.
	 * @param resId the identifier of the resource.
	 * @param locId the locale identifier
	 * @return an array of two objects: On success 0=date 1=completion,
	 * On error: 0=null, 1=null.
	 */
	public Object[] getInformation (String resId,
		LocaleId locId)
	{
		return null;
	}

	/**
	 * Retrieves the list of the resources for the current project, for a given source locale.
	 * @param srcLoc the source locale.
	 * @return an array of object. On success: 0=project id, 1=project short description,
	 * 2=a map of the resource (id and name). On error: 0=null, 1=Error message. 
	 */
	public Object[] getResourceList (LocaleId srcLoc) {
		Object[] res = new Object[3];
		res[0] = null;
		return res;
	}
	
	private HttpURLConnection createConnection (URL url,
		String requestType)
		throws IOException
	{
		HttpURLConnection conn = null;
		conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod(requestType);
		conn.setDoOutput(true);
	    conn.setDoInput(true);
	    conn.setAllowUserInteraction(false);
		conn.setRequestProperty("Authorization", credentials);
		return conn;
	}
	
	private String readResponse (HttpURLConnection conn)
		throws UnsupportedEncodingException, IOException
	{
		StringBuilder tmp = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(
				new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line;
			while ( (line = reader.readLine()) != null ) {
				tmp.append(line+"\n");
			}
		}
		finally {
			if ( reader != null ) {
				reader.close();
			}
		}
		return tmp.toString();
	}

}
