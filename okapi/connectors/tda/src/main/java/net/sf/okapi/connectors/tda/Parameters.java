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

package net.sf.okapi.connectors.tda;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.ListSelectionPart;
import net.sf.okapi.common.uidescription.TextInputPart;

public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	private static final String SERVER = "server";
	private static final String APPKEY = "appKey";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String INDUSTRY = "industry";
	private static final String CONTENTTYPE = "contentType";
	
	private String server;
	private String appKey;
	private String username;
	private String password;
	private int industry;
	private int contentType;
	
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
		appKey = buffer.getEncodedString(APPKEY, appKey);
		username = buffer.getString(USERNAME, username);
		password = buffer.getEncodedString(PASSWORD, password);
		industry = buffer.getInteger(INDUSTRY, industry);
		contentType = buffer.getInteger(CONTENTTYPE, contentType);
	}

	@Override
	public void reset () {
		// Default
		server = "http://www.tausdata.org/api";
		appKey = "";
		username = "";
		password = "";
		industry = 0;
		contentType = 0;
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setString(SERVER, server);
		buffer.setEncodedString(APPKEY, appKey);
		buffer.setString(USERNAME, username);
		buffer.setEncodedString(PASSWORD, password);
		buffer.setInteger(INDUSTRY, industry);
		buffer.setInteger(CONTENTTYPE, contentType);
		return buffer.toString();
	}

	public String getUsername () {
		return username;
	}

	public void setUsername (String username) {
		this.username = username;
	}

	public String getPassword () {
		return password;
	}

	public void setPassword (String password) {
		this.password = password;
	}

	public String getServer () {
		return server;
	}

	public void setServer (String server) {
		this.server = server;
	}

	public String getAppKey () {
		return appKey;
	}

	public void setAppKey (String appKey) {
		this.appKey = appKey;
	}

	public int getIndustry () {
		return industry;
	}

	public void setIndustry (int industry) {
		this.industry = industry;
	}

	public int getContentType () {
		return contentType;
	}

	public void setContentType (int contentType) {
		this.contentType = contentType;
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(USERNAME, "User name", "The login name to use");
		desc.add(PASSWORD, "Password", "The password for the given user name");
		desc.add(SERVER, "Server URL", "URL of the server");
		desc.add(APPKEY, "Application key", "Application key");
		desc.add(INDUSTRY, "Industry", "Keyword for the industry");
		desc.add(CONTENTTYPE, "Content type", "Keyword for the type of content");
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("TDA Search Connector Settings");
		desc.addTextInputPart(paramsDesc.get(USERNAME));
		TextInputPart tip = desc.addTextInputPart(paramsDesc.get(PASSWORD));
		tip.setPassword(true);

		desc.addTextInputPart(paramsDesc.get(SERVER));

		tip = desc.addTextInputPart(paramsDesc.get(APPKEY));
		tip.setPassword(true);

		// List of industries
		//TODO: Get list dynamically from the API
		String[] values1 = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
			"10", "11", "12", "13", "14", "15", "16", "17", "18"};
		String[] labels1 = {
			"Any",
			"Automotive Manufacturing",
			"Consumer Electronics", 
			"Computer Software", 
			"Computer Hardware", 
			"Industrial Manufacturing", 
			"Telecommunications", 
			"Professional and Business Services", 
			"Stores and Retail Distribution", 
			"Industrial Electronics", 
			"Legal Services", 
			"Energy, Water and Utilities", 
			"Financials", 
			"Medical Equipment and Supplies", 
			"Healthcare", 
			"Pharmaceuticals and Biotechnology", 
			"Chemicals", 
			"Undefined Sector", 
			"Leisure, Tourism, and Arts" 
		};
		ListSelectionPart lsp = desc.addListSelectionPart(paramsDesc.get(INDUSTRY), values1);
		lsp.setChoicesLabels(labels1);

		// List of content types
		//TODO: Get list dynamically from the API
		String[] values2 = {"0", "1", "2", "4", "5", "6", "7", "8", "9", "10", "12"};
		String[] labels2 = {
			"Any",
			"Instructions for Use", 
			"Sales and Marketing Material", 
			"Policies, Process and Procedures", 
			"Software Strings and Documentation", 
			"Undefined Content Type", 
			"News Announcements, Reports and Research", 
			"Patents", 
			"Standards, Statutes and Regulations", 
			"Financial Documentation", 
			"Support Content" 
		};
		lsp = desc.addListSelectionPart(paramsDesc.get(CONTENTTYPE), values2);
		lsp.setChoicesLabels(labels2);

		return desc;
	}

}
