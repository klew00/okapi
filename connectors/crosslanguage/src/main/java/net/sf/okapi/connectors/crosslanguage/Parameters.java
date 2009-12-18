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
import net.sf.okapi.common.uidescription.CheckboxPart;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.TextInputPart;

public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	protected static final String SERVERURL = "serverURL";
	protected static final String USER = "user";
	protected static final String APIKEY = "apiKey";
	protected static final String DEPATMENTID = "departmentId";
	protected static final String DICTIONARY = "dictionary";
	protected static final String POLITEFORM = "politeForm";
	
	private String serverURL;
	private String user;
	private String apiKey;
	private String departmentId;
	private String dictionary;
	private boolean politeForm;
	
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

	public String getDepartmentId () {
		return departmentId;
	}

	public void setDepartmentId (String departmentId) {
		this.departmentId = departmentId;
	}

	public String getDictionary () {
		return dictionary;
	}

	public void setDictionary (String dictionary) {
		this.dictionary = dictionary;
	}

	public boolean getPoliteForm () {
		return politeForm;
	}

	public void setPoliteForm (boolean politeForm) {
		this.politeForm = politeForm;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		user = buffer.getString(USER, user);
		apiKey = buffer.getString(APIKEY, apiKey);
		serverURL = buffer.getString(SERVERURL, serverURL);
		departmentId = buffer.getString(DEPATMENTID, departmentId);
		dictionary = buffer.getString(DICTIONARY, dictionary);
		politeForm = buffer.getBoolean(POLITEFORM, politeForm);
	}

	public void reset () {
		user = "myUsername";
		apiKey = "myApiKey";
		serverURL = "http://77.241.87.220/Gateway.svc?wsdl";
		departmentId = "myDeptId";
		dictionary = "General";
		politeForm = true;
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setString(USER, user);
		buffer.setString(APIKEY, apiKey);
		buffer.setString(SERVERURL, serverURL);
		buffer.setString(DEPATMENTID, departmentId);
		buffer.setString(DICTIONARY, dictionary);
		buffer.setBoolean(POLITEFORM, politeForm);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(Parameters.SERVERURL,
			"Server URL", "The full URL of the Gateway server (e.g. http://www.crosslang.com/Gateway.svc?wsdl");
		desc.add(Parameters.USER,
			"User name", "The login name to use");
		desc.add(Parameters.APIKEY,
			"API key", "The API key for the given user, engine and language pair");
		desc.add(Parameters.DEPATMENTID,
			"Department ID", "The customer identifier to use");
		desc.add(Parameters.DICTIONARY,
			"Dictionary", "The domain-specific dictionary to use (e.g. General)");
		desc.add(Parameters.POLITEFORM,
			"Use the polite form (vs. informal form)", null);
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("CrossLanguage MT Connector Settings");
		
		desc.addTextInputPart(paramsDesc.get(SERVERURL));
		
		desc.addTextInputPart(paramsDesc.get(USER));
		
		TextInputPart tip = desc.addTextInputPart(paramsDesc.get(APIKEY));
		tip.setPassword(true);
		
		tip = desc.addTextInputPart(paramsDesc.get(DEPATMENTID));
		tip.setPassword(true);
		
		String[] choices = {"Automotive", "Aviation/Space", "Chemistry", "Colloquial",
			"ComAdder/Data processing", "Earth sciences", "Economics/Business Electronics",
			"Food science", "General", "Legal", "Life sciences", "Mathematics",
			"Mechanical engineering", "Medicine", "Metallurgy", "Military science",
			"Naval/Maritime", "Photography/Optics", "Physics/AtomicEnergy", "Political science"};
		desc.addListSelectionPart(paramsDesc.get(DICTIONARY), choices);
		
		CheckboxPart cbp = desc.addCheckboxPart(paramsDesc.get(POLITEFORM));
		cbp.setVertical(true);
		return desc;
	}

}
