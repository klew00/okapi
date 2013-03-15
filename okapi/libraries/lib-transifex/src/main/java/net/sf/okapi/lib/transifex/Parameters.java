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

package net.sf.okapi.lib.transifex;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.TextInputPart;

public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	private static final String SERVER = "server";
	private static final String PROJECTID = "projectId";
	private static final String USER = "user";
	private static final String PASSWORD = "password";
	
	private String server;
	private String projectId;
	private String user;
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
		projectId = buffer.getString(PROJECTID, projectId);
		user = buffer.getString(USER, user);
		password = buffer.getEncodedString(PASSWORD, password);
	}

	@Override
	public void reset () {
		// Default
		server = "http://www.transifex.net/";
		projectId = "";
		user = "";
		password = "";
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setString(SERVER, server);
		buffer.setString(PROJECTID, projectId);
		buffer.setString(USER, user);
		buffer.setEncodedString(PASSWORD, password);
		return buffer.toString();
	}

	public String getServer () {
		return server;
	}

	public void setServer (String server) {
		if ( server.endsWith("\\") ) {
			server = server.substring(0, server.length()-1) + "/";
		}
		else if ( !server.endsWith("/") ) {
			server += "/";
		}
		this.server = server;
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

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(SERVER, "Server URL", "Full URL of the server (e.g. http://www.transifex.net/)");
		desc.add(USER, "User name", "User name to login");
		desc.add(PASSWORD, "Password", "Password to login");
		desc.add(PROJECTID, "Project ID", "Identifier of the project (case sensitive)");
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Transifex Settings");
		desc.addTextInputPart(paramsDesc.get(SERVER));
		desc.addTextInputPart(paramsDesc.get(USER));
		TextInputPart tip = desc.addTextInputPart(paramsDesc.get(PASSWORD));
		tip.setPassword(true);
		desc.addTextInputPart(paramsDesc.get(PROJECTID));
		return desc;
	}

}
