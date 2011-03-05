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

package net.sf.okapi.filters.transifex;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Project {

	private static final String HOST = "host";
	private static final String USER = "user";
	private static final String PASSWORD = "password";
	private static final String PROJECTID = "projectId";
	
	private String host;
	private String user;
	private String password;
	private String projectId;
	private List<String> resourceIds;
	
	public Project () {
		reset();
	}
	
	private void reset () {
		resourceIds = new ArrayList<String>();
		setHost("http://www.transifex.net");
		setUser("");
		setPassword("");
		setPassword("");
	}
	
	public String getHost () {
		return host;
	}

	public void setHost (String host) {
		if ( host.endsWith("\\") ) {
			host = host.substring(0, host.length()-1) + "/";
		}
		else if ( !host.endsWith("/") ) {
			host += "/";
		}
		this.host = host;
	}

	public String getProjectId () {
		return projectId;
	}

	public void setProjectId (String projectId) {
		this.projectId = projectId;
	}

	public String getUser () {
		return user;
	}

	public void setUser (String user) {
		this.user = user;
	}

	public String getPassword () {
		return password;
	}

	public void setPassword (String password) {
		this.password = password;
	}
	
	public List<String> getResourceIds () {
		return resourceIds;
	}
	
	public void read (BufferedReader br)
		throws IOException
	{
		reset();
		String line = br.readLine();
		while ( line != null ) {
			line = line.trim();
			if ( line.isEmpty() ) continue; // Skip empty lines
			if ( line.startsWith("#") ) continue; // Skip comments
			
			int n = line.indexOf('=');
			if ( n > 0 ) {
				if ( line.startsWith(HOST) ) {
					setHost(line.substring(n+1).trim());
				}
				else if ( line.startsWith(USER) ) {
					setUser(line.substring(n+1).trim());
				}
				else if ( line.startsWith(PASSWORD) ) {
					setPassword(line.substring(n+1).trim());
				}
				else if ( line.startsWith(PROJECTID) ) {
					setProjectId(line.substring(n+1).trim());
				}
			}
			else {
				// Else: add the resource to the list
				resourceIds.add(line);
			}
			// Next line
			line = br.readLine();
		}
	}

}
