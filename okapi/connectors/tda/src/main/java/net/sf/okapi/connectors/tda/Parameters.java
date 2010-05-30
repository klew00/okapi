/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.connectors.tda;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.TextInputPart;

public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	private static final String SERVER = "server";
	private static final String APPKEY = "appKey";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	
	private String server;
	private String appKey;
	private String username;
	private String password;
	
	public Parameters () {
		reset();
		toString();
	}
	
	public Parameters (String initialData) {
		fromString(initialData);
	}

	@Override
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		server = buffer.getString(SERVER, server);
		appKey = buffer.getString(APPKEY, appKey);
		username = buffer.getString(USERNAME, username);
		password = buffer.getString(PASSWORD, password);
	}

	@Override
	public void reset () {
		// Default
		server = "";
		appKey = "";
		username = "";
		password = "";
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setString(SERVER, server);
		buffer.setString(APPKEY, appKey);
		buffer.setString(USERNAME, username);
		buffer.setString(PASSWORD, password);
		return buffer.toString();
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

	public String getServer () {
		return server;
	}

	public void setServer (String server) {
		this.server = server;
	}

	public String getAppKey () {
		return appKey;
	}

	public void setAppKey (String appKey) {
		this.appKey = appKey;
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(USERNAME, "User name", "The login name to use");
		desc.add(PASSWORD, "Password", "The password for the given user name");
		desc.add(SERVER, "Server URL", "URL of the server");
		desc.add(APPKEY, "Application key", "Application key");
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("TDA TM Connector Settings");
		desc.addTextInputPart(paramsDesc.get(SERVER));
		desc.addTextInputPart(paramsDesc.get(USERNAME));
		TextInputPart tip = desc.addTextInputPart(paramsDesc.get(PASSWORD));
		tip.setPassword(true);
		tip = desc.addTextInputPart(paramsDesc.get(APPKEY));
		tip.setPassword(true);
		return desc;
	}

}
