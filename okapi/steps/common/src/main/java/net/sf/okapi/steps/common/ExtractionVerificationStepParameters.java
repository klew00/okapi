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

package net.sf.okapi.steps.common;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.CheckboxPart;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.TextInputPart;

@EditorFor(ExtractionVerificationStepParameters.class)
public class ExtractionVerificationStepParameters extends BaseParameters implements IEditorDescriptionProvider {

	private static final String COMPARESKELETON = "compareSkeleton";
	private static final String STEPENABLED = "stepEnabled";
	private static final String ALLEVENTS = "allEvents";
	private static final String LIMIT = "limit";
	private static final String INTERRUPT = "interrupt";
	
	private boolean compareSkeleton;	
	private boolean stepEnabled;
	private boolean allEvents;
	private int limit;
	private boolean interrupt;
	public boolean monolingual = false;
	
	public ExtractionVerificationStepParameters() {
		reset();
	}

	public void reset() {
		compareSkeleton = true;
		stepEnabled = true;
		allEvents = true;
		limit = 10;
		interrupt = false;
	}

	public void fromString(String data) {
		reset();
		buffer.fromString(data);
		// Read the file content as a set of fields
		compareSkeleton = buffer.getBoolean(COMPARESKELETON, compareSkeleton);
		stepEnabled = buffer.getBoolean(STEPENABLED, stepEnabled);
		allEvents = buffer.getBoolean(ALLEVENTS, allEvents);
		limit = buffer.getInteger(LIMIT, limit);
		interrupt = buffer.getBoolean(INTERRUPT, interrupt);
	}

	public String toString() {
		buffer.reset();		
		buffer.setBoolean(COMPARESKELETON, compareSkeleton);
		buffer.setBoolean(STEPENABLED, stepEnabled);
		buffer.setBoolean(ALLEVENTS, allEvents);
		buffer.setInteger(LIMIT, limit);
		buffer.setBoolean(INTERRUPT, interrupt);
		return buffer.toString();
	}

	public boolean getStepEnabled () {
		return stepEnabled;
	}

	public void setStepEnabled (boolean stepEnabled) {
		this.stepEnabled = stepEnabled;
	}
	
	public boolean getCompareSkeleton () {
		return compareSkeleton;
	}

	public void setCompareSkeleton (boolean compareSkeleton) {
		this.compareSkeleton = compareSkeleton;
	}

	public boolean getAllEvents () {
		return allEvents;
	}

	public void setAllEvents (boolean allEvents) {
		this.allEvents = allEvents;
	}
	
	public int getLimit () {
		return limit;
	}
	
	public void setLimit (int limit) {
		this.limit = limit;
	}
	
	public boolean getInterrupt () {
		return interrupt;
	}

	public void setInterrupt (boolean interrupt) {
		this.interrupt = interrupt;
	}

	@Override
	public ParametersDescription getParametersDescription() {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(STEPENABLED, "Perform the extraction verification", null);
		desc.add(COMPARESKELETON, "Compare skeleton", null);
		desc.add(ALLEVENTS, "Verify all events (otherwise only text units are verified)", null);
		desc.add(LIMIT, "Maximum number of warnings per document", null);
		desc.add(INTERRUPT, "Interrupt after reaching the maximum number of warnings", null);
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Extraction Verification", true, false);

		CheckboxPart cbp = desc.addCheckboxPart(paramsDesc.get(STEPENABLED));
		desc.addSeparatorPart();
		
		CheckboxPart cbp2 = desc.addCheckboxPart(paramsDesc.get(ALLEVENTS));
		cbp2.setMasterPart(cbp, true);
		
		cbp2 = desc.addCheckboxPart(paramsDesc.get(COMPARESKELETON));
		cbp2.setMasterPart(cbp, true);
		
		TextInputPart tip = desc.addTextInputPart(paramsDesc.get(LIMIT));
		tip.setMasterPart(cbp, true);
		
		cbp2 = desc.addCheckboxPart(paramsDesc.get(INTERRUPT));
		cbp2.setMasterPart(cbp, true);
		
		return desc;
	}
}
