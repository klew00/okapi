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
import net.sf.okapi.common.uidescription.SpinInputPart;
import net.sf.okapi.common.uidescription.TextInputPart;

@EditorFor(ParametersTranslation.class)
public class ParametersTranslation extends BaseParameters implements IEditorDescriptionProvider {

	private static final String USER = "user";
	private static final String PASS = "pass";
	private static final String MINMATCH = "minMatch";
	private static final String LOGPATH = "logPath";
	private static final String EXISTINGTM = "existingTm";
	private static final String SEGUNKNOWN = "segUnknown";
	private static final String WHENCHANGED = "whenChanged";
	private static final String USEEXISTING = "useExisting";
	private static final String AUTOOPENLOG = "autoOpenLog";
	private static final String APPENDTOLOG = "appendToLog";
	private static final String OVERWRITE = "overwrite";
	private static final String SENDTM = "sendTm";
	
	private String user;
	private String pass;
	private int minMatch;
	private int whenChanged;
	private String logPath;
	private String existingTm;
	private boolean segUnknown;
	private boolean useExisting;
	private boolean autoOpenLog;
	private boolean appendToLog;
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

	public int getMinMatch () {
		return minMatch;
	}

	public void setMinMatch (int minMatch) {
		this.minMatch = minMatch;
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

	public boolean getSegUnknown () {
		return segUnknown;
	}

	public void setSegUnknown (boolean segUnknown) {
		this.segUnknown = segUnknown;
	}

	public boolean getUseExisting () {
		return useExisting;
	}

	public void setUseExisting (boolean useExisting) {
		this.useExisting = useExisting;
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
	
	public ParametersTranslation () {
		reset();
	}
	
	public void reset() {
		String tmp = System.getProperty("user.name");
		user = (tmp != null ? tmp : "");
		pass = "";
		minMatch = 100;
		whenChanged = 0;
		logPath = Util.INPUT_ROOT_DIRECTORY_VAR+"/log.txt";
		existingTm = "";
		segUnknown = false;
		useExisting = false;
		autoOpenLog = false;
		appendToLog = true;
		overwrite = false;
		sendTm = false;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		user = buffer.getString(USER, user);
		pass = buffer.getString(PASS, pass);
		minMatch = buffer.getInteger(MINMATCH, minMatch);
		whenChanged = buffer.getInteger(WHENCHANGED, whenChanged);
		logPath = buffer.getString(LOGPATH, logPath);
		existingTm = buffer.getString(EXISTINGTM, existingTm);
		segUnknown = buffer.getBoolean(SEGUNKNOWN, segUnknown);
		useExisting = buffer.getBoolean(USEEXISTING, useExisting);
		autoOpenLog = buffer.getBoolean(AUTOOPENLOG, autoOpenLog);
		appendToLog = buffer.getBoolean(APPENDTOLOG, appendToLog);
		overwrite = buffer.getBoolean(OVERWRITE, overwrite);
		sendTm = buffer.getBoolean(SENDTM, sendTm);
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setBoolean(USEEXISTING, useExisting);
		buffer.setBoolean(OVERWRITE, overwrite);
		buffer.setBoolean(SENDTM, sendTm);
		buffer.setString(EXISTINGTM, existingTm);
		buffer.setString(USER, user);
		buffer.setString(PASS, pass);

		buffer.setString(LOGPATH, logPath);
		buffer.setBoolean(AUTOOPENLOG, autoOpenLog);
		buffer.setBoolean(APPENDTOLOG, appendToLog);

		buffer.setInteger(MINMATCH, minMatch);
		buffer.setInteger(WHENCHANGED, whenChanged);
		buffer.setBoolean(SEGUNKNOWN, segUnknown);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(USEEXISTING, "Use or create the following TM:", null);
		desc.add(OVERWRITE, "Overwrite if it exists", null);
		desc.add(SENDTM, "Send the TM to the next step", null);
		desc.add(EXISTINGTM, null, null);
		desc.add(USER, "User ID", null);
		desc.add(PASS, "TM password", "Password of the TM (leave blank if not needed)");

		desc.add(LOGPATH, "Full path of the log file", null);
		desc.add(APPENDTOLOG, "Append to the log file if one exists already", null);
		desc.add(AUTOOPENLOG, "Open the log file after completion", null);

		desc.add(MINMATCH, "Match threshold", "Accept matches with a score equal or above this threshold");
		desc.add(SEGUNKNOWN, "Segment unknown segments", null);
		desc.add(WHENCHANGED, "Update changed transalations", null);
		return desc;
	}
	
	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramDesc) {
		EditorDescription desc = new EditorDescription("Trados Translate", true, false);

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

		SpinInputPart sip = desc.addSpinInputPart(paramDesc.get(MINMATCH));
		sip.setVertical(false);
		sip.setRange(0, 100);

		desc.addCheckboxPart(paramDesc.get(SEGUNKNOWN));

		String[] labels = {
			"No update",
			"Update TM",
			"Update document"
		};
		String[] values = {
			"0",
			"1",
			"2"
		};
		ListSelectionPart lsp = desc.addListSelectionPart(paramDesc.get(WHENCHANGED), values);
		lsp.setMasterPart(cbp1, true);
		lsp.setChoicesLabels(labels);
		lsp.setListType(ListSelectionPart.LISTTYPE_DROPDOWN);
		//lsp.setVertical(false);
		
		return desc;
	}
	
}
