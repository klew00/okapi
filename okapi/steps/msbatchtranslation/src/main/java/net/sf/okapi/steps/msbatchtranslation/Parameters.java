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

package net.sf.okapi.steps.msbatchtranslation;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.CheckboxPart;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.PathInputPart;
import net.sf.okapi.common.uidescription.SeparatorPart;
import net.sf.okapi.common.uidescription.SpinInputPart;
import net.sf.okapi.common.uidescription.TextInputPart;
import net.sf.okapi.common.uidescription.TextLabelPart;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	private static final String CLIENTID = "clientId";
	private static final String SECRET = "secret";
	private static final String CATEGORY = "category";
	private static final String CONFIGPATH = "configPath";
	private static final String ANNOTATE = "annotate";
	private static final String MAKETMX = "makeTmx";
	private static final String TMXPATH = "tmxPath";
	private static final String MARKASMT = "markAsMT";
	private static final String MAXEVENTS = "maxEvents";
	private static final String MAXMATCHES = "maxMatches";
	private static final String THRESHOLD = "threshold";
	private static final String FILLTARGET = "fillTarget";
	private static final String FILLTARGETTHRESHOLD = "fillTargetThreshold";
	private static final String ONLYWHENWITHOUTCANDIDATE = "onlyWhenWithoutCandidate";
	private static final String SENDTMX = "sendTmx";
	
	private String clientId;
	private String secret;
	private String category;
	private String configPath;
	private String tmxPath;
	private boolean markAsMT;
	private int maxEvents;
	private int maxMatches;
	private int threshold;
	private boolean makeTmx;
	private boolean annotate;
	private boolean fillTarget;
	private int fillTargetThreshold;
	private boolean onlyWhenWithoutCandidate;
	private boolean sendTmx;
	
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
		clientId = buffer.getEncodedString(CLIENTID, clientId);
		secret = buffer.getEncodedString(SECRET, secret);
		category = buffer.getEncodedString(CATEGORY, category);
		configPath = buffer.getString(CONFIGPATH, configPath);		
		tmxPath = buffer.getString(TMXPATH, tmxPath);
		markAsMT = buffer.getBoolean(MARKASMT, markAsMT);
		maxEvents = buffer.getInteger(MAXEVENTS, maxEvents);
		maxMatches = buffer.getInteger(MAXMATCHES, maxMatches);
		threshold = buffer.getInteger(THRESHOLD, threshold);
		makeTmx = buffer.getBoolean(MAKETMX, makeTmx);
		sendTmx = buffer.getBoolean(SENDTMX, sendTmx);		
		annotate = buffer.getBoolean(ANNOTATE, annotate);
		fillTarget = buffer.getBoolean(FILLTARGET, fillTarget);
		fillTargetThreshold = buffer.getInteger(FILLTARGETTHRESHOLD, fillTargetThreshold);
		onlyWhenWithoutCandidate = buffer.getBoolean(ONLYWHENWITHOUTCANDIDATE, onlyWhenWithoutCandidate);
	}

	@Override
	public void reset () {
		// Default
		clientId = "";
		secret = "";
		category = "";
		tmxPath = "${rootDir}/tmFromMS.tmx";
		configPath = "";
		markAsMT = true;
		maxEvents = 20;
		maxMatches = 1;
		threshold = 80;
		makeTmx = false;
		annotate = true;
		fillTarget = true;
		fillTargetThreshold = 95;
		onlyWhenWithoutCandidate = true;
		sendTmx = false;
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setString(CLIENTID,clientId);
		buffer.setEncodedString(SECRET, secret);
		buffer.setEncodedString(CATEGORY, category);
		buffer.setString(CONFIGPATH, configPath);		
		buffer.setString(TMXPATH, tmxPath);
		buffer.setBoolean(MARKASMT, markAsMT);
		buffer.setInteger(MAXEVENTS, maxEvents);
		buffer.setInteger(MAXMATCHES, maxMatches);
		buffer.setInteger(THRESHOLD, threshold);
		buffer.setBoolean(MAKETMX, makeTmx);
		buffer.setBoolean(SENDTMX, sendTmx);		
		buffer.setBoolean(ANNOTATE, annotate);
		buffer.setBoolean(FILLTARGET, fillTarget);
		buffer.setInteger(FILLTARGETTHRESHOLD, fillTargetThreshold);
		buffer.setBoolean(ONLYWHENWITHOUTCANDIDATE, onlyWhenWithoutCandidate);
		return buffer.toString();
	}

	public boolean getFillTarget () {
		return fillTarget;
	}

	public void setFillTarget (boolean fillTarget) {
		this.fillTarget = fillTarget;
	}

	public int getFillTargetThreshold () {
		return fillTargetThreshold;
	}

	public void setFillTargetThreshold (int fillTargetThreshold) {
		this.fillTargetThreshold = fillTargetThreshold;
	}

	public boolean getMarkAsMT () {
		return markAsMT;
	}

	public void setMarkAsMT (boolean markAsMT) {
		this.markAsMT = markAsMT;
	}

	public int getMaxEvents () {
		return maxEvents;
	}
	
	public void setMaxEvents (int maxEvents) {
		this.maxEvents = maxEvents;
	}
	
	public int getThreshold () {
		return threshold;
	}
	
	public void setThreshold (int threshold) {
		this.threshold = threshold;
	}
	
	public int getMaxMatches () {
		return maxMatches;
	}
	
	public void setMaxMatches (int maxMatches) {
		this.maxMatches = maxMatches;
	}
	
	public String getTmxPath () {
		return tmxPath;
	}

	public void setTmxPath (String tmxPath) {
		this.tmxPath = tmxPath;
	}
	
	public String getConfigPath () {
		return configPath;
	}

	public void setConfigPath (String configPath) {
		this.configPath = configPath;
	}

	public String getClientId () {
		return clientId;
	}

	public void setClientId (String clientId) {
		this.clientId = clientId;
	}
	
	public String getSecret () {
		return secret;
	}

	public String getCategory () {
		return category;
	}

	public void setSecret (String secret) {
		this.secret = secret;
	}
	
	public void setCategory (String category) {
		this.category = category;
	}
	
	public boolean getMakeTmx () {
		return makeTmx;
	}
	
	public void setMakeTmx (boolean makeTmx) {
		this.makeTmx = makeTmx;
	}
	
	public boolean getAnnotate () {
		return annotate;
	}
	
	public void setAnnotate (boolean annotate) {
		this.annotate = annotate;
	}
	
	public boolean getOnlyWhenWithoutCandidate () {
		return onlyWhenWithoutCandidate;
	}
	
	public void setOnlyWhenWithoutCandidate (boolean onlyWhenWithoutCandidate) {
		this.onlyWhenWithoutCandidate = onlyWhenWithoutCandidate;
	}
	
	public boolean getSendTmx () {
		return sendTmx;
	}
	
	public void setSendTmx (boolean sendTmx) {
		this.sendTmx = sendTmx;
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(CLIENTID, "Client ID", "Client ID in Microsoft Azure Marketplace");
		desc.add(SECRET, "Client Secret", "Client Secret from Microsoft Azure Marketplace");
		desc.add(CATEGORY, "Category", "Category code if accessing a trained system");
		desc.add(CONFIGPATH, "Engine mapping", "Full path of the properties file listing the engines");		
		desc.add(MAXEVENTS, "Events buffer", "Number of events to store before sending a query");
		desc.add(MAXMATCHES, "Maximum matches", "Maximum number of matches allowed");
		desc.add(THRESHOLD, "Threshold", "Score below which matches are not retained");
		desc.add(ONLYWHENWITHOUTCANDIDATE, "Query only entries without existing candidate", null);
		desc.add(TMXPATH, null, "Full path of the new TMX document to create");
		desc.add(MARKASMT, "Mark the generated translation as machine translation results", null);
		desc.add(MAKETMX, "Generate a TMX document", null);
		desc.add(SENDTMX, "Send the TMX document to the next step", null);
		desc.add(ANNOTATE, "Annotate the text units with the translations", null);
		desc.add(FILLTARGET, "Fill the target with the best translation candidate", null);
		desc.add(FILLTARGETTHRESHOLD, "Fill threshold", "Fill the target when the best candidate is equal or above this score");
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Microsoft Batch Translation Settings");
		
		TextLabelPart tlp = desc.addTextLabelPart("Powered by Microsoft\u00AE Translator"); // Required by TOS
		tlp.setVertical(true);
		SeparatorPart sp = desc.addSeparatorPart();
		sp.setVertical(true);
		
		TextInputPart tip = desc.addTextInputPart(paramsDesc.get(CLIENTID));
		tip.setPassword(false);
		
		tip = desc.addTextInputPart(paramsDesc.get(SECRET));
		tip.setPassword(true);

		tip = desc.addTextInputPart(paramsDesc.get(CATEGORY));
		tip.setAllowEmpty(true);
		tip.setPassword(false);

		PathInputPart pip1 = desc.addPathInputPart(paramsDesc.get(CONFIGPATH), "Config Path", false);
		pip1.setBrowseFilters("MS HUB Config (*.properties)\tAll Files (*.*)", "*.properties\t*.*");
		pip1.setVertical(false);
		pip1.setWithLabel(true);
		pip1.setAllowEmpty(true);
		
		sp = desc.addSeparatorPart();
		sp.setVertical(true);
		
		SpinInputPart sip = desc.addSpinInputPart(paramsDesc.get(MAXEVENTS));
		sip.setRange(1, 999);
		
		sip = desc.addSpinInputPart(paramsDesc.get(MAXMATCHES));
		sip.setRange(1, 100);
		
		sip = desc.addSpinInputPart(paramsDesc.get(THRESHOLD));
		sip.setRange(1, 100);
		
		CheckboxPart cbp = desc.addCheckboxPart(paramsDesc.get(ONLYWHENWITHOUTCANDIDATE));
		cbp.setVertical(true);
		
		sp = desc.addSeparatorPart();
		sp.setVertical(true);

		cbp = desc.addCheckboxPart(paramsDesc.get(ANNOTATE));
		cbp.setVertical(true);
		
		CheckboxPart master = desc.addCheckboxPart(paramsDesc.get(MAKETMX));
		master.setVertical(true);
		
		PathInputPart pip = desc.addPathInputPart(paramsDesc.get(TMXPATH), "TMX Path", true);
		pip.setBrowseFilters("TMX Documents (*.tmx)\tAll Files (*.*)", "*.tmx\t*.*");
		pip.setVertical(true);
		pip.setWithLabel(false);
		pip.setMasterPart(master, true);
		
		cbp = desc.addCheckboxPart(paramsDesc.get(SENDTMX));
		cbp.setVertical(true);
		cbp.setMasterPart(master, true);
		
		cbp = desc.addCheckboxPart(paramsDesc.get(MARKASMT));
		cbp.setVertical(true);
		cbp.setMasterPart(master, true);

		sp = desc.addSeparatorPart();
		sp.setVertical(true);

		cbp = desc.addCheckboxPart(paramsDesc.get(FILLTARGET));
		cbp.setVertical(true);

		sip = desc.addSpinInputPart(paramsDesc.get(FILLTARGETTHRESHOLD));
		sip.setRange(1, 100);
		sip.setMasterPart(cbp, true);
		
		return desc;
	}

}
