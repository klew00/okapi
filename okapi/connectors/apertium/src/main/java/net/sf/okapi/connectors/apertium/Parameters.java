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

package net.sf.okapi.connectors.apertium;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.SpinInputPart;
import net.sf.okapi.common.uidescription.TextInputPart;

public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	private static final String SERVER = "server";
	private static final String APIKEY = "apiKey";
	private static final String TIMEOUT = "timeout";
	
	private String server;
	private String apiKey;
	private int timeout;
	
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
		apiKey = buffer.getEncodedString(APIKEY, apiKey);
		timeout = buffer.getInteger(TIMEOUT, timeout);
	}

	@Override
	public void reset () {
		// Default
		server = "http://api.apertium.org/json/translate";
		apiKey = "";
		timeout = 0;
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setString(SERVER, server);
		buffer.setEncodedString(APIKEY, apiKey);
		buffer.setInteger(TIMEOUT, timeout);
		return buffer.toString();
	}

	public String getServer () {
		return server;
	}

	public void setServer (String server) {
		this.server = server;
	}

	public String getApiKey () {
		return apiKey;
	}

	public void setApiKey (String apiKey) {
		this.apiKey = apiKey;
	}
	
	public int getTimeout () {
		return timeout;
	}
	
	public void setTimeout (int timeout) {
		this.timeout = timeout;
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(SERVER, "Server URL:", "Full URL of the server");
		desc.add(APIKEY, "API Key:", "Recommended key (See http://api.apertium.org/register.jsp)");
		desc.add(TIMEOUT, "Timeout", "Timeout in second after which to give up (use 0 for system timeout)");
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Apertium MT Connector Settings");
		
		desc.addTextInputPart(paramsDesc.get(SERVER));
		
		TextInputPart tip = desc.addTextInputPart(paramsDesc.get(APIKEY));
		tip.setPassword(true);
		tip.setAllowEmpty(true); // API key is optional
		
		SpinInputPart sip = desc.addSpinInputPart(paramsDesc.get(TIMEOUT));
		sip.setRange(0, 60);
		return desc;
	}

}
