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

	protected static final String HOST = "host";
	protected static final String USERNAME = "username";
	protected static final String PASSWORD = "password";
	
	private String host;
	private String username;
	private String password;
	
	public String getHost () {
		return host;
	}

	public void setHost (String host) {
		this.host = host;
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
		host = buffer.getString(HOST, host);
		username = buffer.getString(USERNAME, username);
		password = buffer.getEncodedString(PASSWORD, password);
	}

	public void reset () {
//		host = "promtamericas.com/";
//		username = "pts-demo\\test2";
//		password = "test2";
		host = "ptsdemo.promt.ru/";
		username = "";
		password = "";
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setString(HOST, host);
		buffer.setString(USERNAME, username);
		buffer.setEncodedString(PASSWORD, password);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(HOST, "Host server", "The root URL of the host server (e.g. http://ptsdemo.promt.ru/");
		desc.add(USERNAME, "User name (optional)", "The login name to use");
		desc.add(PASSWORD, "Password (if needed)", "The password for the given user name");
		return desc;
	}

	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("ProMT Connector Settings");
		desc.addTextInputPart(paramsDesc.get(HOST));
		TextInputPart tip = desc.addTextInputPart(paramsDesc.get(USERNAME));
		tip.setAllowEmpty(true); // Username is optional
		tip = desc.addTextInputPart(paramsDesc.get(PASSWORD));
		tip.setPassword(true);
		tip.setAllowEmpty(true); // Password is optional
		return desc;
	}

}
