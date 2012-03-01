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

package net.sf.okapi.filters.drupal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiIOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DrupalConnector {

	// See http://drupal.org/node/1447020
	
	/*private static final String HYPHENS = "--";
	private static final String BOUNDARY = "oIkPaApKiO";
	private static final String LINEBREAK = "\r\n"; // HTTP uses CR+LF
	private static final int RESCODE_OK = 200; 
	private static final int RESCODE_CREATED = 201; 
	private static final int MAXBUFFERSIZE = 1024*8;*/ 

	//private final SimpleDateFormat dateFormat; 
	//private final JSONParser parser;

	private String host;
//	private String credentials;
//	private String username;

	private String session_cookie;
	private String user;
	private String pass;
	

	
	public DrupalConnector (String host) {
		setHost(host);
		//parser = new JSONParser();
		//dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");		
	}
	
	public void setHost (String host) {
		this.host = Util.ensureSeparator(host, true);
	}
	
	public String getHost () {
		return host;
	}
	
	public void setCredentials (String username, String password)
	{
		//this.username = username;
		//credentials = "Basic " + Base64.encodeString(username+":"+password);
		
		user = username;
		pass = password;
	}
	

	/*private void addFormDataPart (String name,
		String value,
		DataOutputStream dos)
		throws IOException
	{
		dos.writeBytes(HYPHENS + BOUNDARY + LINEBREAK);
		dos.writeBytes("Content-Disposition: form-data; name=\"" + name + "\""
			+ LINEBREAK + LINEBREAK);
		dos.writeBytes(value + LINEBREAK);
	}*/

/*	private String readResponse (HttpURLConnection conn) throws UnsupportedEncodingException, IOException
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
	}*/
	
/*	private void writeData (HttpURLConnection conn, String data) throws IOException
	{
		DataOutputStream dos = null;
		try {
			dos = new DataOutputStream(conn.getOutputStream());
			dos.writeBytes(data);
			dos.flush();
		}
		finally {
			dos.close();
		}
	}*/
	
	public boolean postNode (Node node) {
		try {
			URL url = new URL(host + String.format("rest/node"));
			HttpURLConnection conn = createConnection(url, "POST", true);

			OutputStream os = conn.getOutputStream();
			os.write(node.toString().getBytes());
			os.flush();

			if ( conn.getResponseCode() != HttpURLConnection.HTTP_OK ) {
				System.out.println(conn.getResponseCode());
				System.out.println(conn.getResponseMessage());
				throw new RuntimeException("Operation failed: " + conn.getResponseCode());
			}
			
			conn.disconnect();
			return true;
		}
		catch ( Throwable e ) {
			throw new RuntimeException("Error in postNode(): "+e.getMessage(), e);
		}
	}
	
	public boolean updateNode (Node node) {
		try {
			URL url = new URL(host + String.format("rest/node/"+node.getNid()));
			HttpURLConnection conn = createConnection(url, "PUT", true);

			OutputStream os = conn.getOutputStream();
			os.write(node.toString().getBytes());
			os.flush();

			if ( conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.println(conn.getResponseCode());
				System.out.println(conn.getResponseMessage());
				throw new RuntimeException("Operation failed: " + conn.getResponseCode());
			}
			
			conn.disconnect();
			return true;
		}
		catch ( Throwable e ) {
			throw new RuntimeException("Error in updateNode(): "+e.getMessage(), e);
		}
	}
	
	public Node getNode (String nodeId)
	{
		try {
			URL url = new URL(host + String.format("rest/node/%s", nodeId));
			HttpURLConnection conn = createConnection(url, "GET", false);

			if ( conn.getResponseCode() != HttpURLConnection.HTTP_OK ) {
				conn.disconnect();
				throw new RuntimeException("Error in getNode(): "+conn.getResponseMessage());
			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			
			JSONParser parser=new JSONParser();
			JSONObject node=(JSONObject)parser.parse(reader);
			
			conn.disconnect();
			
		    return new Node(node);
		}
		catch ( Throwable e ) {
			throw new RuntimeException("Error in getNode(): "+e.getMessage(), e);
		}
	}

	/**
	 * Get all nodes
	 * @return
	 */
	public List<NodeInfo> getNodes () {
		URL url;
		HttpURLConnection conn;
		List<NodeInfo> nodes = new ArrayList<NodeInfo>();
		
		try {
			url = new URL(host + "rest/node");
			conn = createConnection(url, "GET", false);
			
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				conn.disconnect();
				throw new RuntimeException("Operation failed: " + conn.getResponseCode());
			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			JSONParser parser=new JSONParser();
			JSONArray array=(JSONArray)parser.parse(reader);

			for ( Object object : array ) {
				JSONObject node = (JSONObject)object;
				String status = (String)node.get("status");
				NodeInfo ni = new NodeInfo((String)node.get("nid"), !status.equals("0"));
				ni.setStatus(status);
				ni.setTitle((String)node.get("title"));
				ni.setType((String)node.get("type"));
				nodes.add(ni);
			}
		
			conn.disconnect();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return nodes;
	}
	
	
	/**
	 * Log in to start session
	 * @param username
	 * @param password
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	boolean login () {
		try {
			URL url = new URL(host + "rest/user/login");
			HttpURLConnection conn = createConnection(url, "POST", true);
			
			JSONObject login = new JSONObject();
			login.put("username", user);
			login.put("password", pass);
			
			OutputStream os = conn.getOutputStream();
			os.write(login.toJSONString().getBytes());
			os.flush();
			
			if ( conn.getResponseCode() != HttpURLConnection.HTTP_OK ) {
				conn.disconnect();
				return false;
			}
	
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		
			JSONParser parser=new JSONParser();
			JSONObject obj =(JSONObject)parser.parse(reader);
			
			session_cookie = obj.get("session_name")+"="+obj.get("sessid");
			conn.disconnect();
		}
		catch ( Throwable e ) {
			throw new OkapiIOException("Error in login().", e);
		}
		return true;
	}
	
	/**
	 * Logout and end session
	 * @return
	 */
	boolean logout () {
		try {
			URL url = new URL(host + "rest/user/logout");
			HttpURLConnection conn = createConnection(url, "POST", false );
			if ( conn.getResponseCode() != HttpURLConnection.HTTP_OK ) {
				conn.disconnect();
				return false;
			}
			conn.disconnect();
		}
		catch ( Throwable e ) {
			throw new OkapiIOException("Error in logout().", e);
		}
		return true;
	}
	
	/**
	 * Connection helper
	 * @param url
	 * @param method
	 * @param setDoOutput
	 * @return
	 * @throws IOException
	 */
	private HttpURLConnection createConnection (URL url, String method, boolean setDoOutput) throws IOException
	{
			HttpURLConnection conn = null;
			conn = (HttpURLConnection)url.openConnection();
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Content-Type", "application/json");
		    conn.setAllowUserInteraction(false);
			conn.setRequestMethod(method);
			conn.setDoOutput(setDoOutput);
			
			if(session_cookie != null){
				conn.setRequestProperty("Cookie", session_cookie);
			}

			return conn;
	}
}
