/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.filters.xliff;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.ParametersDescription;

public class Parameters extends BaseParameters {

	private boolean fallbackToID;
	private boolean escapeGT;
	private boolean addTargetLanguage;
	// Not used for now
	private boolean useStateValues;
	private boolean extractOnlyMatchingValues;
	private String stateValues;
	private boolean extractNoState;

	public Parameters () {
		reset();
		toString(); // fill the list
	}
	
	public boolean getUseStateValues () {
		return useStateValues;
	}

	public void setUseStateValues (boolean useStateValues) {
		this.useStateValues = useStateValues;
	}

	public boolean getExtractOnlyMatchingValues () {
		return extractOnlyMatchingValues;
	}

	public void setExtractOnlyMatchingValues (boolean extractOnlyMatchingValues) {
		this.extractOnlyMatchingValues = extractOnlyMatchingValues;
	}

	public String getStateValues () {
		return stateValues;
	}

	public void setStateValues (String stateValues) {
		this.stateValues = stateValues;
	}

	public boolean getExtractNoState () {
		return extractNoState;
	}

	public void setExtractNoState (boolean extractNoState) {
		this.extractNoState = extractNoState;
	}

	public boolean getEscapeGT () {
		return escapeGT;
	}

	public void setEscapeGT (boolean escapeGT) {
		this.escapeGT = escapeGT;
	}

	public boolean getFallbackToID() {
		return fallbackToID;
	}

	public void setFallbackToID(boolean fallbackToID) {
		this.fallbackToID = fallbackToID;
	}

	public boolean getAddTargetLanguage () {
		return addTargetLanguage;
	}

	public void setAddTargetLanguage (boolean addTargetLanguage) {
		this.addTargetLanguage = addTargetLanguage;
	}

	public void reset () {
		useStateValues = true;
		stateValues = "new|needs-translation";
		extractOnlyMatchingValues = true;
		extractNoState = true;
		fallbackToID = false;
		escapeGT = false;
		addTargetLanguage = true;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		useStateValues = buffer.getBoolean("useStateValues", useStateValues);
		extractOnlyMatchingValues = buffer.getBoolean("extractOnlyMatchingValues", extractOnlyMatchingValues);
		stateValues = buffer.getString("stateValues", stateValues);
		extractNoState = buffer.getBoolean("extractNoState", extractNoState);
		fallbackToID = buffer.getBoolean("fallbackToID", fallbackToID);
		escapeGT = buffer.getBoolean("escapeGT", escapeGT);
		addTargetLanguage = buffer.getBoolean("addTargetLanguage", addTargetLanguage);
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setBoolean("useStateValues", useStateValues);
		buffer.setBoolean("extractOnlyMatchingValues", extractOnlyMatchingValues);
		buffer.setString("stateValues", stateValues);
		buffer.setBoolean("extractNoState", extractNoState);
		
		buffer.setBoolean("fallbackToID", fallbackToID);
		buffer.setBoolean("escapeGT", escapeGT);
		buffer.setBoolean("addTargetLanguage", addTargetLanguage);
		return buffer.toString();
	}
	
	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add("fallbackToID", "Use the trans-unit id attribute for the text unit name if there is no resname", null);
		desc.add("escapeGT", "Escape the greater-than characters", null);
		desc.add("addTargetLanguage", "Add the target-language attribute if not present", null);
		return desc;
	}
}
