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

	private static final String APPID = "appId";
	
	private String appId;
	
	public Parameters () {
		reset();
		toString();
	}
	
	public String getAppId () {
		return appId;
	}

	public void setAppId (String appId) {
		this.appId = appId;
	}

	@Override
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		appId = buffer.getEncodedString(APPID, appId);
	}

	@Override
	public void reset () {
		// Default for tests
		appId = "";
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setEncodedString(APPID, appId);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(APPID,
			"Microsoft AppID (See http://www.bing.com/developers/appids.aspx)", "The AppID to identify the application/user");
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Microsoft MT Connector Settings", true, false);
		TextInputPart tip = desc.addTextInputPart(paramsDesc.get(APPID));
		tip.setPassword(true);
		return desc;
	}

}
