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

package net.sf.okapi.connectors.pensieve;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.CheckboxPart;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.FolderInputPart;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.TextInputPart;

public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	private static final String USESERVER = "useServer";
	private static final String HOST = "host";
	private static final String DBDIRECTORY = "dbDirectory";

	private boolean useServer;
	private String host;
	private String dbDirectory;
	
	public Parameters () {
		reset();
		toString();
	}
	
	public Parameters (String initialData) {
		fromString(initialData);
	}
	
	public boolean getUseServer () {
		return useServer;
	}

	public void setUseServer (boolean useServer) {
		this.useServer = useServer;
	}

	public String getHost () {
		return host;
	}

	public void setHost (String host) {
		this.host = host;
	}

	public String getDbDirectory () {
		return dbDirectory;
	}

	public void setDbDirectory(String dbDirectory) {
		this.dbDirectory = dbDirectory;
	}

	@Override
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		useServer = buffer.getBoolean(USESERVER, useServer);
		host = buffer.getString(HOST, host);
		dbDirectory = buffer.getString(DBDIRECTORY, dbDirectory);
	}

	@Override
	public void reset () {
		dbDirectory = "";
		host = "http://localhost:8080/";
		useServer = false;
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setBoolean(USESERVER, useServer);
		buffer.setString(HOST, host);
		buffer.setString(DBDIRECTORY, dbDirectory);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(USESERVER, "Use a server (instead of a locale TM)", null);
		desc.add(HOST, "Server URL", "URL of the server to use (e.g. http://localhost:8080/");
		desc.add(DBDIRECTORY, "TM Directory", "Directory of the TM database");
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Pensieve TM Connector Settings", true, false);
		CheckboxPart cbp = desc.addCheckboxPart(paramsDesc.get(Parameters.USESERVER));
		TextInputPart tip = desc.addTextInputPart(paramsDesc.get(Parameters.HOST));
		tip.setMasterPart(cbp, true);
		FolderInputPart fip = desc.addFolderInputPart(paramsDesc.get(Parameters.DBDIRECTORY), "TM Directory");
		fip.setMasterPart(cbp, false);
		return desc;
	}

}
