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
import net.sf.okapi.common.uidescription.TextInputPart;

@EditorFor(ParametersImport.class)
public class ParametersImport extends BaseParameters implements IEditorDescriptionProvider {

	private static final String USER = "user";
	private static final String PASS = "pass";
	private static final String EXISTINGTM = "existingTm";
	private static final String MODE = "mode";
	private static final String FORMAT = "format";
	private static final String REORGANIZE = "reorganize";
	private static final String IGNORENEWFIELDS = "ignoreNewFields";
	private static final String CHECKLANG = "checkLang";
	private static final String OVERWRITE = "overwrite";
	private static final String SENDTM = "sendTm";
	
	private String user;
	private String pass;
	private int mode;
	private int format;
	private String existingTm;
	private boolean reorganize;
	private boolean ignoreNewFields;
	private boolean checkLang;
	private boolean overwrite;
	private boolean sendTm;

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

	public int getMode() {
		return mode;
	}

	public void setMode (int mode) {
		this.mode = mode;
	}
	
	public int getFormat() {
		return format;
	}

	public void setFormat (int format) {
		this.format = format;
	}
	
	public String getExistingTm () {
		return existingTm;
	}

	public void setExistingTm (String existingTm) {
		this.existingTm = existingTm;
	}

	public boolean getCheckLang() {
		return checkLang;
	}

	public void setCheckLang (boolean checkLang) {
		this.checkLang = checkLang;
	}

	public boolean getReorganize () {
		return reorganize;
	}
	
	public void setReorganize (boolean reorganize) {
		this.reorganize = reorganize;
	}
	
	public boolean getIgnoreNewFields () {
		return ignoreNewFields;
	}
	
	public void setIgnoreNewFields (boolean ignoreNewFields) {
		this.ignoreNewFields = ignoreNewFields;
	}
	
	public boolean getOverwrite () {
		return overwrite;
	}

	public void setOverwrite (boolean overwrite) {
		this.overwrite = overwrite;
	}

	public boolean getSendTm () {
		return sendTm;
	}
	
	public void setSendTm (boolean sendTm) {
		this.sendTm = sendTm;
	}
	
	public ParametersImport () {
		reset();
	}
	
	public void reset() {
		String tmp = System.getProperty("user.name");
		user = (tmp != null ? tmp : "");
		pass = "";
		mode = 2;
		format = 9;
		existingTm = "";
		reorganize = false;
		ignoreNewFields = false;
		checkLang = false;
		overwrite = false;
		sendTm = false;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		user = buffer.getString(USER, user);
		pass = buffer.getString(PASS, pass);
		mode = buffer.getInteger(MODE, mode);
		format = buffer.getInteger(FORMAT, format);
		existingTm = buffer.getString(EXISTINGTM, existingTm);
		reorganize = buffer.getBoolean(REORGANIZE, reorganize);
		ignoreNewFields = buffer.getBoolean(IGNORENEWFIELDS, ignoreNewFields);
		checkLang = buffer.getBoolean(CHECKLANG, checkLang);
		overwrite = buffer.getBoolean(OVERWRITE, overwrite);
		sendTm = buffer.getBoolean(SENDTM, sendTm);
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setString(EXISTINGTM, existingTm);
		buffer.setBoolean(OVERWRITE, overwrite);
		buffer.setBoolean(SENDTM, sendTm);
		buffer.setString(USER, user);
		buffer.setString(PASS, pass);

		buffer.setBoolean(REORGANIZE, reorganize);
		buffer.setBoolean(IGNORENEWFIELDS, ignoreNewFields);
		buffer.setBoolean(CHECKLANG, checkLang);

		buffer.setInteger(MODE, mode);
		buffer.setInteger(FORMAT, format);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(EXISTINGTM, "Use or create the TM you want to import into", null);
		desc.add(OVERWRITE, "Overwrite if it exists", null);
		desc.add(SENDTM, "Send the TM to the next step", null);
		
		desc.add(USER, "User ID", null);
		desc.add(PASS, "TM password", "Password of the TM (leave blank if not needed)");

		desc.add(MODE, "Existing translation units:", null);
		desc.add(FORMAT, "Import format:", null);
		desc.add(REORGANIZE, "Large import file (with reorganization) ", null);
		desc.add(IGNORENEWFIELDS, "Ignore new fields", null);
		desc.add(CHECKLANG, "Check matching sub-languages", null);
		
		return desc;
	}
	
	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramDesc) {
		EditorDescription desc = new EditorDescription("Trados Import", true, false);

		desc.addPathInputPart(paramDesc.get(EXISTINGTM), "TM File", false);
		desc.addCheckboxPart(paramDesc.get(OVERWRITE));
		desc.addCheckboxPart(paramDesc.get(SENDTM));
		
		TextInputPart tip = desc.addTextInputPart(paramDesc.get(USER));
		tip.setVertical(false);
		
		tip = desc.addTextInputPart(paramDesc.get(PASS));
		tip.setAllowEmpty(true);
		tip.setPassword(true);
		tip.setVertical(false);
		
		desc.addSeparatorPart();

		String[] labels = {
			"Leave unchanged",
			"Keep most recent",
			"Keep oldest",
			"Merge",
			"Overwrite"
		};
		String[] values = {
			"0",
			"3",
			"4",
			"1",
			"2"
		};
		ListSelectionPart lsp = desc.addListSelectionPart(paramDesc.get(MODE), values);
		lsp.setChoicesLabels(labels);
		lsp.setListType(ListSelectionPart.LISTTYPE_DROPDOWN);
		lsp.setVertical(false);
		
		String[] labels2 = {
				"Translator's Workbench (*.txt)",
				"TMX 1.1 (*.tmx)",
				"TMX 1.4 (*.tmx)",
				"TMX 1.4b (*.tmx)",
				"Systran (*.rtf)",
				"Logos (*.sgm)"
			};
		String[] values2 = {
				"10",
				"6",
				"8",
				"9",
				"2",
				"1"
			};
		lsp = desc.addListSelectionPart(paramDesc.get(FORMAT), values2);
		lsp.setChoicesLabels(labels2);
		lsp.setListType(ListSelectionPart.LISTTYPE_DROPDOWN);
		lsp.setVertical(false);
		
		CheckboxPart cbp = desc.addCheckboxPart(paramDesc.get(REORGANIZE));
		cbp = desc.addCheckboxPart(paramDesc.get(IGNORENEWFIELDS));
		cbp = desc.addCheckboxPart(paramDesc.get(CHECKLANG));
		cbp.setVertical(true);
		
		return desc;
	}
	
}
