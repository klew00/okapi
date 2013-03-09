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

package net.sf.okapi.connectors.google;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.TextInputPart;

public class GoogleMTv2Parameters extends BaseParameters implements IEditorDescriptionProvider {

	private static final String APIKEY = "apiKey";
	
	private String apiKey;
	
	public GoogleMTv2Parameters () {
		reset();
		toString();
	}
	
	public String getApiKey () {
		return apiKey;
	}

	public void setApiKey (String apiKey) {
		this.apiKey = apiKey;
	}

	@Override
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		apiKey = buffer.getEncodedString(APIKEY, apiKey);
	}

	@Override
	public void reset () {
		apiKey = "";
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setEncodedString(APIKEY, apiKey);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(APIKEY,
			"Google API key",
			"The Google API key to identify the application/user");
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Google Translate v2 Connector Settings", true, false);
		TextInputPart tip = desc.addTextInputPart(paramsDesc.get(APIKEY));
		tip.setPassword(true);
		return desc;
	}

}
