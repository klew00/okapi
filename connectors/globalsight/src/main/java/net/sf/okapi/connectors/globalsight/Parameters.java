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

package net.sf.okapi.connectors.globalsight;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.ParametersDescription;

public class Parameters extends BaseParameters {

	protected static final String USERNAME = "username";
	protected static final String PASSWORD = "password";
	protected static final String SERVERURL = "serverURL";
	protected static final String TMPROFILE = "tmProfile";
	
	private String username;
	private String password;
	private String serverURL;
	private String tmProfile;
	
	public Parameters () {
		reset();
		toString();
	}
	
	public Parameters (String initialData) {
		fromString(initialData);
	}
	
	public String getUsername () {
		return username;
	}

	public void setUsername (String username) {
		this.username = username;
	}

	public String getPassword () {
		return password;
	}

	public void setPassword (String password) {
		this.password = password;
	}

	public String getServerURL () {
		return serverURL;
	}

	public void setServerURL (String serverURL) {
		this.serverURL = serverURL;
	}

	public String getTmProfile () {
		return tmProfile;
	}

	public void setTmProfile (String tmProfile) {
		this.tmProfile = tmProfile;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		username = buffer.getString(Parameters.USERNAME, username);
		password = buffer.getString(Parameters.PASSWORD, password);
		serverURL = buffer.getString(Parameters.SERVERURL, serverURL);
		tmProfile = buffer.getString(Parameters.TMPROFILE, tmProfile);
	}

	public void reset () {
		username = "";
		password = "";
		serverURL = "http://HOST:PORT/globalsight/services/AmbassadorWebService?wsdl";
		tmProfile = "default";
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setString(Parameters.USERNAME, username);
		buffer.setString(Parameters.PASSWORD, password);
		buffer.setString(Parameters.SERVERURL, serverURL);
		buffer.setString(Parameters.TMPROFILE, tmProfile);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(Parameters.SERVERURL,
			"Server URL", "The full URL of the TM server (e.g. http://xyz:8080/globalsight/services/AmbassadorWebService?wsdl");
		desc.add(Parameters.USERNAME,
			"User name", "The login name to use");
		desc.add(Parameters.PASSWORD,
			"Password", "The password for the given user name");
		desc.add(Parameters.TMPROFILE,
			"TM profile", "The name of the TM profile to use");
		return desc;
	}
}
