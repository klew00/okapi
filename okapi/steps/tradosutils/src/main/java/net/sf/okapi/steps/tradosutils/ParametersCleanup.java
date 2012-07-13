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
import net.sf.okapi.common.Util;
import net.sf.okapi.common.uidescription.CheckboxPart;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.ListSelectionPart;
import net.sf.okapi.common.uidescription.PathInputPart;
import net.sf.okapi.common.uidescription.TextInputPart;

@EditorFor(ParametersCleanup.class)
public class ParametersCleanup extends BaseParameters implements IEditorDescriptionProvider {

	private static final String USER = "user";
	private static final String PASS = "pass";
	private static final String LOGPATH = "logPath";
	private static final String EXISTINGTM = "existingTm";
	private static final String WHENCHANGED = "whenChanged";
	private static final String USEEXISTING = "useExisting";
	private static final String OVERWRITE = "overwrite";
	private static final String AUTOOPENLOG = "autoOpenLog";
	private static final String APPENDTOLOG = "appendToLog";
	private static final String SENDTM = "sendTm";
	
	private String user;
	private String pass;
	private int whenChanged;
	private String logPath;
	private String existingTm;
	private boolean useExisting;
	private boolean overwrite;
	private boolean autoOpenLog;
	private boolean appendToLog;
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

	public int getWhenChanged() {
		return whenChanged;
	}

	public void setWhenChanged (int whenChanged) {
		this.whenChanged = whenChanged;
	}
	
	public String getLogPath() {
		return logPath;
	}

	public void setLogPath (String logPath) {
		this.logPath = logPath;
	}
	
	public String getExistingTm () {
		return existingTm;
	}

	public void setExistingTm (String existingTm) {
		this.existingTm = existingTm;
	}

	public boolean getUseExisting () {
		return useExisting;
	}

	public void setUseExisting (boolean useExisting) {
		this.useExisting = useExisting;
	}
	
	public boolean getOverwrite () {
		return overwrite;
	}

	public void setOverwrite (boolean overwrite) {
		this.overwrite = overwrite;
	}

	public boolean getAutoOpenLog () {
		return autoOpenLog;
	}
	
	public void setAutoOpenLog (boolean autoOpenLog) {
		this.autoOpenLog = autoOpenLog;
	}
	
	public boolean getAppendToLog () {
		return appendToLog;
	}
	
	public void setAppendToLog (boolean appendToLog) {
		this.appendToLog = appendToLog;
	}

	public boolean getSendTm () {
		return sendTm;
	}
	
	public void setSendTm (boolean sendTm) {
		this.sendTm = sendTm;
	}
	
	public ParametersCleanup () {
		reset();
	}
	
	public void reset() {
		String tmp = System.getProperty("user.name");
		user = (tmp != null ? tmp : "");
		pass = "";
		whenChanged = 0;
		logPath = Util.INPUT_ROOT_DIRECTORY_VAR+"/log.txt";
		existingTm = "";
		useExisting = false;
		overwrite = false;
		autoOpenLog = false;
		appendToLog = true;
		sendTm = false;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		user = buffer.getString(USER, user);
		pass = buffer.getString(PASS, pass);
		whenChanged = buffer.getInteger(WHENCHANGED, whenChanged);
		logPath = buffer.getString(LOGPATH, logPath);
		existingTm = buffer.getString(EXISTINGTM, existingTm);
		useExisting = buffer.getBoolean(USEEXISTING, useExisting);
		overwrite = buffer.getBoolean(OVERWRITE, overwrite);
		autoOpenLog = buffer.getBoolean(AUTOOPENLOG, autoOpenLog);
		appendToLog = buffer.getBoolean(APPENDTOLOG, appendToLog);
		sendTm = buffer.getBoolean(SENDTM, sendTm);
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setBoolean(USEEXISTING, useExisting);
		buffer.setString(EXISTINGTM, existingTm);
		buffer.setBoolean(OVERWRITE, overwrite);
		buffer.setString(USER, user);
		buffer.setString(PASS, pass);

		buffer.setString(LOGPATH, logPath);
		buffer.setBoolean(AUTOOPENLOG, autoOpenLog);
		buffer.setBoolean(APPENDTOLOG, appendToLog);

		buffer.setInteger(WHENCHANGED, whenChanged);
		buffer.setBoolean(SENDTM, sendTm);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(USEEXISTING, "Use or create the following TM:", null);
		desc.add(EXISTINGTM, null, null);
		desc.add(OVERWRITE, "Overwrite if it exists", null);
		desc.add(USER, "User ID", null);
		desc.add(PASS, "TM password", "Password of the TM (leave blank if not needed)");

		desc.add(LOGPATH, "Full path of the log file", null);
		desc.add(APPENDTOLOG, "Append to the log file if one exists already", null);
		desc.add(AUTOOPENLOG, "Open the log file after completion", null);

		desc.add(WHENCHANGED, "Changed translations", null);
		desc.add(SENDTM, "Send the TM to the next step", null);
		return desc;
	}
	
	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramDesc) {
		EditorDescription desc = new EditorDescription("Trados Clean-up", true, false);

		CheckboxPart cbp1 = desc.addCheckboxPart(paramDesc.get(USEEXISTING));
		
		CheckboxPart cbp3 = desc.addCheckboxPart(paramDesc.get(OVERWRITE));
		cbp3.setMasterPart(cbp1, true);
		
		CheckboxPart cbp2 = desc.addCheckboxPart(paramDesc.get(SENDTM));
		cbp2.setMasterPart(cbp1, true);
		
		PathInputPart pip = desc.addPathInputPart(paramDesc.get(EXISTINGTM), "TM File", false);
		pip.setWithLabel(false);
		pip.setMasterPart(cbp1, true);

		// User ID is always needed
		TextInputPart tip = desc.addTextInputPart(paramDesc.get(USER));
		tip.setVertical(false);
		
		tip = desc.addTextInputPart(paramDesc.get(PASS));
		tip.setAllowEmpty(true);
		tip.setPassword(true);
		tip.setVertical(false);
		tip.setMasterPart(cbp1, true);
		
		desc.addSeparatorPart();

		pip = desc.addPathInputPart(paramDesc.get(LOGPATH), "Log Path", true);
		pip.setWithLabel(true);
		
		desc.addCheckboxPart(paramDesc.get(APPENDTOLOG));
		desc.addCheckboxPart(paramDesc.get(AUTOOPENLOG));
		
		desc.addSeparatorPart();

		String[] labels = {
			"No update",
			"Update TM",
			"Update document"
		};
		String[] values = {
			"0",
			"2",
			"3"
		};
		ListSelectionPart lsp = desc.addListSelectionPart(paramDesc.get(WHENCHANGED), values);
		lsp.setMasterPart(cbp1, true);
		lsp.setChoicesLabels(labels);
		lsp.setListType(ListSelectionPart.LISTTYPE_DROPDOWN);

		return desc;
	}
	
}
