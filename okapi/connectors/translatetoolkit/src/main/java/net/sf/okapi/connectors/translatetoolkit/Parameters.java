/*===========================================================================
  Copyright (C) 2009-2011 by the Okapi Framework contributors
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

package net.sf.okapi.connectors.translatetoolkit;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.CheckboxPart;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	private static final String HOST = "host";
	private static final String PORT = "port";
	private static final String SUPPORTCODES = "supportCodes";
	
	private String host;
	private int port;
	private boolean supportCodes;
	
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
		host = buffer.getString(HOST, host);
		port = buffer.getInteger(PORT, port);
		supportCodes = buffer.getBoolean(SUPPORTCODES, supportCodes);
	}

	@Override
	public void reset () {
		host = "amagama.locamotion.org"; //"localhost";
		port = 80; //8080;
		supportCodes = false; 
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setString(HOST, host);
		buffer.setInteger(PORT, port);
		buffer.setBoolean(SUPPORTCODES, supportCodes);
		return buffer.toString();
	}

	public String getHost () {
		return host;
	}

	public void setHost (String host) {
		this.host = host;
	}

	public int getPort () {
		return port;
	}

	public void setPort (int port) {
		this.port = port;
	}

	public boolean getSupportCodes () {
		return supportCodes;
	}

	public void setSupportCodes (boolean supportCodes) {
		this.supportCodes = supportCodes;
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(HOST, "Host", "The host name of the TM server (e.g. localhost)");
		desc.add(PORT, "Port", "The port number of the TM server (e.g. 8080)");
		desc.add(SUPPORTCODES, "Inline codes are letter-coded (e.g. <x1/><g2></g2>)", null);
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Translation Toolkit TM Connector Settings");
		desc.addTextInputPart(paramsDesc.get(HOST));
		desc.addTextInputPart(paramsDesc.get(PORT));
		CheckboxPart cbp = desc.addCheckboxPart(paramsDesc.get(SUPPORTCODES));
		cbp.setVertical(true);
		return desc;
	}

}
