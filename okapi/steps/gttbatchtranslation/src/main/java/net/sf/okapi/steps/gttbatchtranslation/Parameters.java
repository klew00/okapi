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

package net.sf.okapi.steps.gttbatchtranslation;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.CheckboxPart;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.PathInputPart;
import net.sf.okapi.common.uidescription.SeparatorPart;
import net.sf.okapi.common.uidescription.TextInputPart;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	private static final String EMAIL = "email";
	private static final String PASSWORD = "password";
	private static final String TMXPATH = "tmxPath";
	private static final String WAITCLASS = "waitClass";
	private static final String MARKASMT = "markAsMT";
	private static final String OPENGTTPAGES = "openGttPages";
	
	private String email;
	private String password;
	private String tmxPath;
	private String waitClass;
	private boolean markAsMT;
	private boolean openGttPages;
	
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
		email = buffer.getString(EMAIL, email);
		password = buffer.getEncodedString(PASSWORD, password);
		tmxPath = buffer.getString(TMXPATH, tmxPath);
		waitClass = buffer.getString(WAITCLASS, waitClass);
		markAsMT = buffer.getBoolean(MARKASMT, markAsMT);
		openGttPages = buffer.getBoolean(OPENGTTPAGES, openGttPages);
	}

	@Override
	public void reset () {
		// Default
		email = "";
		password = "";
		tmxPath = "${rootDir}/tmFromGTT.tmx";
		markAsMT = true;
		openGttPages = true;
		// Default UI
		waitClass = "net.sf.okapi.common.ui.WaitDialog";
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setString(EMAIL, email);
		buffer.setEncodedString(PASSWORD, password);
		buffer.setString(TMXPATH, tmxPath);
		buffer.setString(WAITCLASS, waitClass);
		buffer.setBoolean(MARKASMT, markAsMT);
		buffer.setBoolean(OPENGTTPAGES, openGttPages);
		return buffer.toString();
	}

	public boolean getMarkAsMT () {
		return markAsMT;
	}

	public void setMarkAsMT (boolean markAsMT) {
		this.markAsMT = markAsMT;
	}

	public boolean getOpenGttPages () {
		return openGttPages;
	}

	public void setOpenGttPages (boolean openGttPages) {
		this.openGttPages = openGttPages;
	}

	public String getTmxPath () {
		return tmxPath;
	}

	public void setTmxPath (String tmxPath) {
		this.tmxPath = tmxPath;
	}

	public String getEmail () {
		return email;
	}

	public void setEmail (String email) {
		this.email = email;
	}

	public String getPassword () {
		return password;
	}

	public void setPassword (String password) {
		this.password = password;
	}

	public String getWaitClass () {
		return waitClass;
	}

	public void setWaitClass (String waitClass) {
		this.waitClass = waitClass;
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(EMAIL, "Email address", "Email address of the Google Translator Toolkit account");
		desc.add(PASSWORD, "Password", "Password of the account");
		desc.add(TMXPATH, "TMX document to create", "Full path of the new TMX document to create");
		desc.add(MARKASMT, "Mark the generated translation as machine translation results", null);
		desc.add(OPENGTTPAGES, "Open the GTT edit pages automatically after upload", null);
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Google Translator Toolkit Settings");
		desc.addTextInputPart(paramsDesc.get(EMAIL));
		TextInputPart tip = desc.addTextInputPart(paramsDesc.get(PASSWORD));
		tip.setPassword(true);

		SeparatorPart sp = desc.addSeparatorPart();
		sp.setVertical(true);
		
		CheckboxPart cbp = desc.addCheckboxPart(paramsDesc.get(MARKASMT));
		cbp.setVertical(true);

		cbp = desc.addCheckboxPart(paramsDesc.get(OPENGTTPAGES));
		cbp.setVertical(true);

		sp = desc.addSeparatorPart();
		sp.setVertical(true);

		PathInputPart pip = desc.addPathInputPart(paramsDesc.get(TMXPATH), "TMX Path", true);
		pip.setBrowseFilters("TMX Documents (*.tmx)\tAll Files (*.*)", "*.tmx\t*.*");
		pip.setVertical(true);
		pip.setLabelFlushed(false);
		
		return desc;
	}

}
