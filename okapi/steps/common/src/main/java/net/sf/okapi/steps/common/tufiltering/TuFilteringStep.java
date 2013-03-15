/*===========================================================================
  Copyright (C) 2009-2011 by the Okapi Framework contributors
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

package net.sf.okapi.steps.common.tufiltering;

import net.sf.okapi.common.ClassUtil;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.resource.ITextUnit;

public class TuFilteringStep extends BasePipelineStep {
	
	private ITextUnitFilter tuFilter;
	private Parameters params;
	
	public TuFilteringStep() {
		params = new Parameters();
	}
	
	public TuFilteringStep(ITextUnitFilter tuFilter) {
		this();
		this.tuFilter = tuFilter;
	}
	
	@Override
	public IParameters getParameters() {
		return params;
	}
	
	@Override
	public void setParameters(IParameters params) {
		this.params = (Parameters) params;
	}
	
	@Override
	public String getName() {
		return "Text Unit Filtering";
	}

	@Override
	public String getDescription() {
		return "Set the non-translatable flag to the text units accepted by the text unit filter specified in step's parameters."
			+ " Expects: filter events. Sends back: filter events.";
	}

	private void initFilter() {
		if (tuFilter == null) {
			if (Util.isEmpty(params.getTuFilterClassName())) {
				throw new RuntimeException("Text Unit filter class is not specified in step parameters.");
			}
			try {
				tuFilter = (ITextUnitFilter) ClassUtil.instantiateClass(params.getTuFilterClassName());
			} catch (Exception e) {
				throw new RuntimeException(String.format("Cannot instantiate the specified Text Unit filter (%s)", e.toString()));
			}
		}
	}
	
	/**
	 * Process a given text unit event. This method can modify the event's text unit resource,
	 * can drop the event and return NO_OP, can create and return a new event (for example, a DOCUMENT_PART event),
	 * or it can produce several events and return them packed in a MULTI_EVENT's resource.
	 * <p>
	 * This method can be overridden in subclasses to change the way text unit events are processed if accepted.
	 * <p>
	 * If not overridden, clears the "translatable" flag of accepted text units, thus marking the text units
	 * non-translatable.
	 * 
	 * @param tuEvent the text unit event which resource can be modified.
	 * @return the modified event
	 */
	protected Event processFiltered(Event tuEvent) {
		ITextUnit tu = tuEvent.getTextUnit();
		if (tu != null) tu.setIsTranslatable(false);
		return tuEvent;
	}
	
	@Override
	protected Event handleStartBatch(Event event) {
		initFilter();
		return super.handleStartBatch(event);
	}

	@Override
	protected Event handleTextUnit(Event event) {
		initFilter();
		if (tuFilter.accept(event.getTextUnit())) {
			return processFiltered(event);
		}
		else
			return event;
	}	
}
