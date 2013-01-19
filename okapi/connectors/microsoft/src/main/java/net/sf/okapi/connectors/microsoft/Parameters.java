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

package net.sf.okapi.connectors.microsoft;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.TextInputPart;

public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	private static final String CLIENTID = "clientId";
	private static final String SECRET = "secret";
	private static final String CATEGORY = "category";
	
	private String clientId="";
	private String secret="";
	private String category="";
	
	public Parameters () {
		reset();
		toString();
	}
	
	public String getClientId () {
		return clientId;
	}

	public void setClientId (String clientId) {
		this.clientId = clientId;
	}

	public String getSecret () {
		return secret;
	}

	public void setSecret (String secret) {
		this.secret = secret;
	}

	public String getCategory () {
		return category;
	}

	public void setCategory (String category) {
		this.category = category;
	}

	@Override
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		clientId = buffer.getEncodedString(CLIENTID, clientId);
		secret = buffer.getEncodedString(SECRET, secret);
		category = buffer.getString(CATEGORY, category);
	}

	@Override
	public void reset () {
		// Default values
		clientId = "";
		secret = "";
		category = "";
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setEncodedString(CLIENTID, clientId);
		buffer.setEncodedString(SECRET, secret);
		buffer.setString(CATEGORY, category);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(CLIENTID,
			"Client ID (See http://msdn.microsoft.com/en-us/library/hh454950.aspx)", "The ClientID to identify the user");
		desc.add(SECRET,
			"Secret (See http://msdn.microsoft.com/en-us/library/hh454950.aspx)", "A code obtained from Microsoft Azure");
		desc.add(CATEGORY,
			"Category (See http://hub.microsofttranslator.com", "A category code for an MT system trained by user data, if any");
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Microsoft MT Connector Settings", true, false);
		desc.addTextInputPart(paramsDesc.get(CLIENTID));
		TextInputPart tip = desc.addTextInputPart(paramsDesc.get(SECRET));
		tip.setPassword(true);
		tip = desc.addTextInputPart(paramsDesc.get(CATEGORY));
		tip.setAllowEmpty(true);
		return desc;
	}

}
