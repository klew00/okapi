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

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.TextInputPart;

public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	protected static final String SERVERURL = "serverURL";
	protected static final String USERNAME = "username";
	protected static final String PASSWORD = "password";
	
	private String serverURL;
	private String username;
	private String password;
	
	public String getServerURL () {
		return serverURL;
	}

	public void setServerURL (String serverURL) {
		this.serverURL = serverURL;
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

	public Parameters () {
		reset();
		toString();
	}
	
	public Parameters (String initialData) {
		fromString(initialData);
	}
	
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		serverURL = buffer.getString(SERVERURL, serverURL);
		username = buffer.getString(USERNAME, username);
		password = buffer.getString(PASSWORD, password);
	}

	public void reset () {
		serverURL = "http://ptsdemo.promt.ru/";
		username = "";
		password = "";
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setString(SERVERURL, serverURL);
		buffer.setString(USERNAME, username);
		buffer.setString(PASSWORD, password);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(SERVERURL, "Server URL", "The root URL of the MTserver (e.g. http://ptsdemo.promt.ru/");
		desc.add(USERNAME, "User name", "The login name to use");
		desc.add(PASSWORD, "Password", "The password for the given user name");
		return desc;
	}

	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("ProMT Connector Settings");
		desc.addTextInputPart(paramsDesc.get(SERVERURL));
		desc.addTextInputPart(paramsDesc.get(USERNAME));
		TextInputPart tip = desc.addTextInputPart(paramsDesc.get(PASSWORD));
		tip.setPassword(true);
		return desc;
	}

}
