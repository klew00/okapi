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

package net.sf.okapi.steps.common.codesimplifier;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextUnitUtil;
import net.sf.okapi.common.skeleton.GenericSkeleton;

/**
 * !!! It's important to include this step in a pipeline before any source-copying or leveraging steps, because it can modify 
 * codes in the source, and target codes will easily get desynchronized with their sources.
 * The best place for this step -- right after the filter.  
 */
@UsingParameters(Parameters.class)
public class CodeSimplifierStep extends BasePipelineStep {
	
	private Parameters params;

	public CodeSimplifierStep() {
		super();
		params = new Parameters();
	}
	
	@Override
	public String getDescription() {
		return "Merges adjacent inline codes in the source part of a text unit."
			+ " Also where possible, moves leading and trailing codes of the source to the skeleton."
			+ " Expects: filter events. Sends back: filter events.";
	}

	@Override
	public String getName() {
		return "Inline Codes Simplifier";
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
	protected Event handleTextUnit(Event event) {
		ITextUnit tu = event.getTextUnit();
		TextUnitUtil.simplifyCodes(tu, params.getRemoveLeadingTrailingCodes() && 
				tu.getSkeleton() instanceof GenericSkeleton);
		
		return super.handleTextUnit(event);
	}
}
