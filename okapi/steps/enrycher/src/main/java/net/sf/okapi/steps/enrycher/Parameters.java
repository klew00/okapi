/*===========================================================================
  Copyright (C) 2012 by the Okapi Framework contributors
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

package net.sf.okapi.steps.enrycher;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {
	
	private static final String BASEURL = "baseUrl";
	
	private String baseUrl;
	
	public Parameters() {
		reset();
	}
	
	@Override
	public void reset() {	
		setBaseUrl("http://aidemo.ijs.si/mlw");
	}

	@Override
	public void fromString(String data) {
		reset();
		buffer.fromString(data);
		baseUrl = buffer.getString(BASEURL, baseUrl);
	}
	
	@Override
	public String toString() {
		buffer.reset();
		buffer.setParameter(BASEURL, baseUrl);		
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(BASEURL, "URL of the Enrycher Web service", null);		
		return desc;
	}
	
	@Override
	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Enrycher", true, false);
		desc.addTextInputPart(paramsDesc.get(BASEURL));
		return desc;
	}
	
	public String getBaseUrl () {
		return baseUrl;
	}
	
	public void setBaseUrl (String baseUrl) {
		this.baseUrl = Util.ensureSeparator(baseUrl, true);
	}

}
