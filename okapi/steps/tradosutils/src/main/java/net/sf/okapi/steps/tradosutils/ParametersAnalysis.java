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
import net.sf.okapi.common.uidescription.PathInputPart;
import net.sf.okapi.common.uidescription.SpinInputPart;
import net.sf.okapi.common.uidescription.TextInputPart;

@EditorFor(ParametersAnalysis.class)
public class ParametersAnalysis extends BaseParameters implements IEditorDescriptionProvider {

	private static final String USEEXISTING = "useExisting";
	private static final String EXISTINGTM = "existingTm";
	private static final String USER = "user";
	private static final String PASS = "pass";
	private static final String LOGPATH = "logPath";
	private static final String AUTOOPENLOG = "autoOpenLog";
	private static final String APPENDTOLOG = "appendToLog";
	private static final String CREATEPRJTM = "createPrjTm";
	private static final String PRJTMPATH = "prjTmPath";
	private static final String EXPORTUNKNOWN = "exportUnknown";
	private static final String TMXPATH = "tmxPath";	
	private static final String MAXMATCH = "maxMatch";
	private static final String SENDTMX = "sendTmx";
	
	private String user;
	private String pass;
	private int maxMatch;
	private String logPath;
	private String tmxPath;
	private String existingTm;
	private boolean createPrjTm;
	private String prjTmPath;
	private boolean exportUnknown;
	private boolean useExisting;
	private boolean autoOpenLog;
	private boolean appendToLog;
	private boolean sendTmx;

	public String getPrjTmPath() {
		return prjTmPath;
	}

	public void setPrjTmPath(String prjTmPath) {
		this.prjTmPath = prjTmPath;
	}

	public boolean isCreatePrjTm() {
		return createPrjTm;
	}

	public void setCreatePrjTm(boolean createPrjTm) {
		this.createPrjTm = createPrjTm;
	}

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

	public int getMaxMatch () {
		return maxMatch;
	}

	public void setMaxMatch (int maxMatch) {
		this.maxMatch = maxMatch;
	}
	
	public String getLogPath() {
		return logPath;
	}

	public void setLogPath (String logPath) {
		this.logPath = logPath;
	}
	
	public String getTmxPath () {
		return tmxPath;
	}

	public void setTmxPath (String tmxPath) {
		this.tmxPath = tmxPath;
	}
	
	public String getExistingTm () {
		return existingTm;
	}

	public void setExistingTm (String existingTm) {
		this.existingTm = existingTm;
	}

	public boolean getExportUnknown () {
		return exportUnknown;
	}

	public void setExportUnknown (boolean exportUnknown) {
		this.exportUnknown = exportUnknown;
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
	
	public boolean getSendTmx () {
		return sendTmx;
	}
	
	public void setSendTmx (boolean sendTmx) {
		this.sendTmx = sendTmx;
	}
	
	public ParametersAnalysis () {
		reset();
	}
	
	public void reset() {
		useExisting = false;
		existingTm = "";
		String tmp = System.getProperty("user.name");
		user = (tmp != null ? tmp : "");
		pass = "";
		logPath = Util.INPUT_ROOT_DIRECTORY_VAR+"/log.txt";
		autoOpenLog = false;
		appendToLog = true;
		createPrjTm = false;
		prjTmPath = Util.INPUT_ROOT_DIRECTORY_VAR+"/project.tmw";
		exportUnknown = false;
		tmxPath = Util.INPUT_ROOT_DIRECTORY_VAR+"/unknownSegments.tmx";
		maxMatch = 90;
		sendTmx = true;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		useExisting = buffer.getBoolean(USEEXISTING, useExisting);
		existingTm = buffer.getString(EXISTINGTM, existingTm);
		user = buffer.getString(USER, user);
		pass = buffer.getString(PASS, pass);
		logPath = buffer.getString(LOGPATH, logPath);
		autoOpenLog = buffer.getBoolean(AUTOOPENLOG, autoOpenLog);
		appendToLog = buffer.getBoolean(APPENDTOLOG, appendToLog);
		createPrjTm = buffer.getBoolean(CREATEPRJTM, createPrjTm);
		prjTmPath = buffer.getString(PRJTMPATH, prjTmPath);
		exportUnknown = buffer.getBoolean(EXPORTUNKNOWN, exportUnknown);
		tmxPath = buffer.getString(TMXPATH, tmxPath);
		maxMatch = buffer.getInteger(MAXMATCH, maxMatch);
		sendTmx = buffer.getBoolean(SENDTMX, sendTmx);
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setBoolean(USEEXISTING, useExisting);
		buffer.setString(EXISTINGTM, existingTm);
		buffer.setString(USER, user);
		buffer.setString(PASS, pass);

		buffer.setString(LOGPATH, logPath);
		buffer.setBoolean(AUTOOPENLOG, autoOpenLog);
		buffer.setBoolean(APPENDTOLOG, appendToLog);

		buffer.setBoolean(CREATEPRJTM, createPrjTm);
		buffer.setString(PRJTMPATH, prjTmPath);
		buffer.setBoolean(EXPORTUNKNOWN, exportUnknown);
		buffer.setString(TMXPATH, tmxPath);
		buffer.setInteger(MAXMATCH, maxMatch);
		buffer.setBoolean(SENDTMX, sendTmx);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(USEEXISTING, "Use this existing TM:", null);
		desc.add(EXISTINGTM, null, null);
		desc.add(USER, "User ID", null);
		desc.add(PASS, "TM password", "Password of the TM (leave blank if not needed)");

		desc.add(LOGPATH, "Full path of the log file", null);
		desc.add(APPENDTOLOG, "Append to the log file if one exists already", null);
		desc.add(AUTOOPENLOG, "Open the log file after completion", null);

		desc.add(CREATEPRJTM, "Create project TM:", null);
		desc.add(PRJTMPATH, "Full path of the new Trados project TM to create", "Full path of the new Trados project TM to create");
		desc.add(EXPORTUNKNOWN, "Export unknown segments:", null);
		desc.add(TMXPATH, "Full path of the new TMX document to create", "Full path of the new TMX document to create");
		desc.add(MAXMATCH, "Export threshold", "Export segments with no match above this threshold");
		desc.add(SENDTMX, "Send the TMX document to the next step", null);
		return desc;
	}
	
	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramDesc) {
		EditorDescription desc = new EditorDescription("Trados Analysis", true, false);

		CheckboxPart cbp = desc.addCheckboxPart(paramDesc.get(USEEXISTING));
		
		PathInputPart pip = desc.addPathInputPart(paramDesc.get(EXISTINGTM), "TM File", false);
		pip.setWithLabel(false);
		pip.setMasterPart(cbp, true);
		
		// User ID is always required (even for temporary TM)
		TextInputPart tip = desc.addTextInputPart(paramDesc.get(USER));
		tip.setVertical(false);
		
		tip = desc.addTextInputPart(paramDesc.get(PASS));
		tip.setAllowEmpty(true);
		tip.setPassword(true);
		tip.setVertical(false);
		tip.setMasterPart(cbp, true);
		
		desc.addSeparatorPart();
		
		pip = desc.addPathInputPart(paramDesc.get(LOGPATH), "Log Path", true);
		pip.setWithLabel(true);
		
		cbp = desc.addCheckboxPart(paramDesc.get(APPENDTOLOG));

		cbp = desc.addCheckboxPart(paramDesc.get(AUTOOPENLOG));

		desc.addSeparatorPart();

		cbp = desc.addCheckboxPart(paramDesc.get(CREATEPRJTM));
		pip = desc.addPathInputPart(paramDesc.get(PRJTMPATH), "TM Path", true);
		pip.setMasterPart(cbp, true);
		pip.setWithLabel(false);
		
		desc.addSeparatorPart();
		
		cbp = desc.addCheckboxPart(paramDesc.get(EXPORTUNKNOWN));
		pip = desc.addPathInputPart(paramDesc.get(TMXPATH), "TMX Path", true);
		pip.setMasterPart(cbp, true);
		pip.setWithLabel(false);
		SpinInputPart sip = desc.addSpinInputPart(paramDesc.get(MAXMATCH));
		sip.setVertical(false);
		sip.setRange(0, 100);
		sip.setMasterPart(cbp, true);
		CheckboxPart cbp2 = desc.addCheckboxPart(paramDesc.get(SENDTMX));
		cbp2.setMasterPart(cbp, true);
		
		return desc;
	}
	
}
