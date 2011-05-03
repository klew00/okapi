/*===========================================================================
  Copyright (C) 2009-2010 by the Okapi Framework contributors
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
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

@EditorFor(ExtractionVerificationStepParameters.class)
public class ExtractionVerificationStepParameters extends BaseParameters implements IEditorDescriptionProvider {

	private static final String COMPARESKELETON = "compareSkeleton";
	private static final String ENABLED = "enabled";
	private static final String ALLEVENTS = "allEvents";
	private static final String LIMIT = "limit";
	private static final String INTERRUPT = "interrupt";
	
	
	private boolean compareSkeleton;	
	private boolean enabled;
	private boolean allEvents;
	private int limit;
	private boolean interrupt;
	
	public ExtractionVerificationStepParameters() {
		reset();
	}

	public void reset() {
		compareSkeleton = true;
		enabled = true;
		allEvents = false;
		limit = 10;
		interrupt = false;
	}

	public void fromString(String data) {
		reset();
		// Read the file content as a set of fields
		compareSkeleton = buffer.getBoolean(COMPARESKELETON, compareSkeleton);
		enabled = buffer.getBoolean(ENABLED, enabled);
		allEvents = buffer.getBoolean(ALLEVENTS, allEvents);
		limit = buffer.getInteger(LIMIT, limit);
		interrupt = buffer.getBoolean(INTERRUPT, interrupt);
	}

	public String toString() {
		buffer.reset();		
		buffer.setBoolean(COMPARESKELETON, compareSkeleton);
		buffer.setBoolean(ENABLED, enabled);
		buffer.setBoolean(ALLEVENTS, allEvents);
		buffer.setInteger(LIMIT, limit);
		buffer.setBoolean(INTERRUPT, interrupt);
		return buffer.toString();
	}

	public boolean isCompareSkeleton() {
		return compareSkeleton;
	}

	public void setCompareSkeleton(boolean compareSkeleton) {
		this.compareSkeleton = compareSkeleton;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean getAllEvents() {
		return allEvents;
	}

	public void setAllEvents(boolean allEvents) {
		this.allEvents = allEvents;
	}
	
	public int getLimit() {
		return limit;
	}
	
	public void setLimit(int limit) {
		this.limit = limit;
	}
	
	public boolean getInterrupt() {
		return interrupt;
	}

	public void setInterrupt(boolean interrupt) {
		this.interrupt = interrupt;
	}

	@Override
	public ParametersDescription getParametersDescription() {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(COMPARESKELETON, "Compare skeleton", null);
		desc.add(ENABLED, "Enable Extraction Verification Step", null);
		desc.add(ALLEVENTS, "Verify all events (By default only TextUnits are verified)", null);
		desc.add(LIMIT, "Max number of verification warnings to report per file", null);
		desc.add(INTERRUPT, "Interrupt after reaching max verification warnings", null);
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Compare TextUnits", true, false);		
		desc.addCheckboxPart(paramsDesc.get(COMPARESKELETON));
		desc.addSeparatorPart();
		desc.addCheckboxPart(paramsDesc.get(ENABLED));
		desc.addSeparatorPart();
		desc.addCheckboxPart(paramsDesc.get(ALLEVENTS));
		desc.addSeparatorPart();
		desc.addTextInputPart(paramsDesc.get(LIMIT));
		desc.addSeparatorPart();
		desc.addCheckboxPart(paramsDesc.get(INTERRUPT));
		return desc;
	}
}
