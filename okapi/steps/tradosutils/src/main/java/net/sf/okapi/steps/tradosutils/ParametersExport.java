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
============================================================================*/

package net.sf.okapi.steps.tradosutils;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.CheckboxPart;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.ListSelectionPart;
import net.sf.okapi.common.uidescription.PathInputPart;
import net.sf.okapi.common.uidescription.TextInputPart;

@EditorFor(ParametersExport.class)
public class ParametersExport extends BaseParameters implements IEditorDescriptionProvider {

	private static final String USER = "user";
	private static final String PASS = "pass";
	private static final String CONSTRAINTSFILE = "constraintsFile";
	private static final String FORMAT = "format";
	private static final String SENDEXPORTEDFILE = "sendExportedFile";
	
	private String user;
	private String pass;
	private int format;
	private String constraintsFile;
	private boolean sendExportedFile;

	public String getUser() {
		return user;
	}

	public void setUser (String user) {
		this.user = user;
	}

	public String getPass () {
		return pass;
	}

	public void setPass (String pass) {
		this.pass = pass;
	}

	public int getFormat() {
		return format;
	}

	public void setFormat (int format) {
		this.format = format;
	}
	
	public String getConstraintsFile () {
		return constraintsFile;
	}

	public void setConstraintsFile (String constraintsFile) {
		this.constraintsFile = constraintsFile;
	}

	public boolean getSendExportedFile () {
		return sendExportedFile;
	}
	
	public void setSendExportedFile (boolean sendExportedFile) {
		this.sendExportedFile = sendExportedFile;
	}
	
	public ParametersExport () {
		reset();
	}
	
	public void reset() {
		String tmp = System.getProperty("user.name");
		user = (tmp != null ? tmp : "");
		pass = "";
		format = 9;
		constraintsFile = "";
		sendExportedFile = false;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		user = buffer.getString(USER, user);
		pass = buffer.getString(PASS, pass);
		format = buffer.getInteger(FORMAT, format);
		constraintsFile = buffer.getString(CONSTRAINTSFILE, constraintsFile);
		sendExportedFile = buffer.getBoolean(SENDEXPORTEDFILE, sendExportedFile);
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setString(CONSTRAINTSFILE, constraintsFile);
		buffer.setString(USER, user);
		buffer.setString(PASS, pass);

		buffer.setBoolean(SENDEXPORTEDFILE, sendExportedFile);
		buffer.setInteger(FORMAT, format);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(CONSTRAINTSFILE, "Select filter constraints file", null);
		desc.add(USER, "User ID", null);
		desc.add(PASS, "TM password", "Password of the TM (leave blank if not needed)");

		desc.add(FORMAT, "Export format:", null);
		desc.add(SENDEXPORTEDFILE, "Send exported document to the next step", null);
	
		return desc;
	}
	
	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramDesc) {
		EditorDescription desc = new EditorDescription("Trados Export", true, false);

		TextInputPart tip = desc.addTextInputPart(paramDesc.get(USER));
		tip.setVertical(false);
		
		tip = desc.addTextInputPart(paramDesc.get(PASS));
		tip.setAllowEmpty(true);
		tip.setPassword(true);
		tip.setVertical(false);
		
		desc.addSeparatorPart();

		String[] labels = {
				"Translator's Workbench (*.txt)",
				"Tmx 1.1 (*.tmx)",
				"Tmx 1.4 (*.tmx)",
				"Tmx 1.4b (*.tmx)",
				"Systran (*.rtf)",
				"Logos (*.sgm)"
			};
		String[] values = {
				"10",
				"6",
				"8",
				"9",
				"2",
				"1"
			};
		ListSelectionPart lsp = desc.addListSelectionPart(paramDesc.get(FORMAT), values);
		lsp.setChoicesLabels(labels);
		lsp.setListType(ListSelectionPart.LISTTYPE_DROPDOWN);
		lsp.setVertical(false);
		
		PathInputPart pip = desc.addPathInputPart(paramDesc.get(CONSTRAINTSFILE), "Select filter constraints file", false);
		pip.setBrowseFilters("Constraint Settings File (*.wcs)\tAll Files (*.*)", "*.wcs\t*.*");
		pip.setAllowEmpty(true);
		
		CheckboxPart cbp = desc.addCheckboxPart(paramDesc.get(SENDEXPORTEDFILE));
		cbp.setVertical(true);
		
		return desc;
	}

	/**
	 * Gets the Okapi filter configuration for a given Trados export format.
	 * @param formatCode the code of the export format.
	 * @return the configuration identifier, or null if none is found.
	 */
	public String getFilterConfigurationForExportFormat (int formatCode) {
		switch ( formatCode ) {
		case 6:
		case 8:
		case 9:
			return "okf_tmx";
		default:
			return null;
		}
	}

}
