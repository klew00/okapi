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

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	private static final String APPID = "appId";
	private static final String TMXPATH = "tmxPath";
	private static final String MARKASMT = "markAsMT";
	private static final String MAXEVENTS = "maxEvents";
	private static final String MAXMATCHES = "maxMatches";
	private static final String THRESHOLD = "threshold";
	
	private String appId;
	private String tmxPath;
	private boolean markAsMT;
	private int maxEvents;
	private int maxMatches;
	private int threshold;
	
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
		appId = buffer.getEncodedString(APPID, appId);
		tmxPath = buffer.getString(TMXPATH, tmxPath);
		markAsMT = buffer.getBoolean(MARKASMT, markAsMT);
		maxEvents = buffer.getInteger(MAXEVENTS, maxEvents);
		maxMatches = buffer.getInteger(MAXMATCHES, maxMatches);
		threshold = buffer.getInteger(THRESHOLD, threshold);
	}

	@Override
	public void reset () {
		// Default
		appId = "";
		tmxPath = "${rootDir}/tmFromMS.tmx";
		markAsMT = true;
		maxEvents = 20;
		maxMatches = 1;
		threshold = 80;
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setEncodedString(APPID, appId);
		buffer.setString(TMXPATH, tmxPath);
		buffer.setBoolean(MARKASMT, markAsMT);
		buffer.setInteger(MAXEVENTS, maxEvents);
		buffer.setInteger(MAXMATCHES, maxMatches);
		buffer.setInteger(THRESHOLD, threshold);
		return buffer.toString();
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

	public String getAppId () {
		return appId;
	}

	public void setAppId (String appId) {
		this.appId = appId;
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(APPID, "Application ID", "Application ID for the account to use");
		desc.add(MAXEVENTS, "Events buffer", "Number of events to store before sending a query");
		desc.add(MAXMATCHES, "Maximum matches", "Maximum number of matches allowed");
		desc.add(THRESHOLD, "Threshold", "Score below which matches are not retained");
		desc.add(TMXPATH, "TMX document to create", "Full path of the new TMX document to create");
		desc.add(MARKASMT, "Mark the generated translation as machine translation results", null);
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Microsoft Batch Translation Settings");
		TextInputPart tip = desc.addTextInputPart(paramsDesc.get(APPID));
		tip.setPassword(true);

		SeparatorPart sp = desc.addSeparatorPart();
		sp.setVertical(true);
		
		SpinInputPart sip = desc.addSpinInputPart(paramsDesc.get(MAXEVENTS));
		sip.setRange(1, 999);
		
		sip = desc.addSpinInputPart(paramsDesc.get(MAXMATCHES));
		sip.setRange(1, 100);
		
		sip = desc.addSpinInputPart(paramsDesc.get(THRESHOLD));
		sip.setRange(1, 100);
		
		CheckboxPart cbp = desc.addCheckboxPart(paramsDesc.get(MARKASMT));
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
