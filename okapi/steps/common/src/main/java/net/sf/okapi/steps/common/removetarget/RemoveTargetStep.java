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

package net.sf.okapi.steps.common.removetarget;

import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.ListUtil;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.resource.ITextUnit;

@UsingParameters(Parameters.class)
public class RemoveTargetStep extends BasePipelineStep {

	private List<String> tuIds;
	private List<LocaleId> targetLocales;
	private Parameters params;

	public RemoveTargetStep() {
		super();
		params = new Parameters();
	}

	@Override
	public String getDescription() {
		return "Remove targets in all or a given set of text units."
				+ " Expects: filter events. Sends back: filter events.";
	}

	@Override
	public String getName() {
		return "Remove Target";
	}

	@Override
	public void setParameters(IParameters params) {
		this.params = (Parameters) params;
	}

	@Override
	public Parameters getParameters() {
		return params;
	}

	@Override
	protected Event handleStartBatch(Event event) {
		tuIds = ListUtil.stringAsList(params.getTusForTargetRemoval());
		targetLocales = ListUtil.stringAsLanguageList(params.getTargetLocalesToKeep());
		return super.handleStartBatch(event);
	}

	@Override
	protected Event handleTextUnit(Event event) {
		String id = event.getResource().getId();

		// are we filtering on ids or locales?
		if (params.isFilterBasedOnIds()) {
			// If there're no TU Ids in the list, remove targets in all TUs
			if (Util.isEmpty(tuIds) || tuIds.contains(id)) {
				ITextUnit tu = event.getTextUnit();
				for (LocaleId locId : tu.getTargetLocales()) {
					tu.removeTarget(locId);
				}
			}
		} else {
			if (!Util.isEmpty(targetLocales)) {
				ITextUnit tu = event.getTextUnit();
				for (LocaleId locId : tu.getTargetLocales()) {
					if (!targetLocales.contains(locId)) {
						tu.removeTarget(locId);
					}					
				}
			}			
		}
		
		if (params.isRemoveTUIfNoTarget()) {
			if (event.getTextUnit().getTargetLocales().isEmpty()) {
				return Event.NOOP_EVENT;
			}
		}
		
		return event;
	}
}
