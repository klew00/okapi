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

package net.sf.okapi.connectors.crosslanguage;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.TextInputPart;

public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	protected static final String SERVERURL = "serverURL";
	protected static final String USER = "user";
	protected static final String APIKEY = "apiKey";
	protected static final String PASSWORD = "password";
	
	private String serverURL;
	private String user;
	private String apiKey;
	private String password;
	
	public Parameters () {
		reset();
		toString();
	}
	
	public Parameters (String initialData) {
		fromString(initialData);
	}
	
	public String getUser () {
		return user;
	}

	public void setUser (String user) {
		this.user = user;
	}

	public String getApiKey () {
		return apiKey;
	}

	public void setApiKey (String apiKey) {
		this.apiKey = apiKey;
	}

	public String getServerURL () {
		return serverURL;
	}

	public void setServerURL (String serverURL) {
		this.serverURL = serverURL;
	}

	public String getPassword () {
		return password;
	}

	public void setPassword (String password) {
		this.password = password;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		user = buffer.getString(USER, user);
		apiKey = buffer.getString(APIKEY, apiKey);
		serverURL = buffer.getString(SERVERURL, serverURL);
		password = buffer.getEncodedString(PASSWORD, password);
	}

	public void reset () {
		user = "myUsername";
		apiKey = "myApiKey";
		serverURL = "http://gateway.crosslang.com:8080/services/clGateway?wsdl";
		password = "";
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setString(USER, user);
		buffer.setString(APIKEY, apiKey);
		buffer.setString(SERVERURL, serverURL);
		buffer.setEncodedString(PASSWORD, password);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(Parameters.SERVERURL,
			"Server URL", "The full URL of the server (e.g. http://gateway.crosslang.com:8080/services/clGateway?wsdl");
		desc.add(Parameters.USER,
			"User name", "The login name to use");
		desc.add(Parameters.APIKEY,
			"API key", "The API key for the given user, engine and language pair");
		desc.add(Parameters.PASSWORD,
			"Password", "The login passowrd");
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("CrossLanguage MT Connector Settings");
		
		desc.addTextInputPart(paramsDesc.get(SERVERURL));
		
		desc.addTextInputPart(paramsDesc.get(USER));
		
		desc.addTextInputPart(paramsDesc.get(APIKEY));
		
		TextInputPart tip = desc.addTextInputPart(paramsDesc.get(PASSWORD));
		tip.setPassword(true);
		
		return desc;
	}

}
