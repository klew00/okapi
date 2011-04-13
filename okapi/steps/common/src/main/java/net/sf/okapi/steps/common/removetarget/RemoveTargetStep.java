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

package net.sf.okapi.steps.common.removetarget;

import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.ListUtil;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.resource.TextUnit;

public class RemoveTargetStep extends BasePipelineStep {

	private List<String> tuIds;
	private Parameters params;
			
	public RemoveTargetStep() {
		super();
		params = new Parameters();
	}
	
	public String getDescription() {
		return "Remove targets in all or a given set of Text Units."
			+ " Expects: filter events. Sends back: filter events.";
	}

	public String getName() {
		return "Remove Target";
	}
	
//	@Override
//	public void setParameters(IParameters params) {
//		this.params = (Parameters) params;
//		super.setParameters(params);
//	}
	
	@Override
	public Parameters getParameters() {
		return params;
	}

	@Override
	protected Event handleStartBatch(Event event) {
		tuIds = ListUtil.stringAsList(params.getTusForTargetRemoval());
		return super.handleStartBatch(event);
	}

	@Override
	protected Event handleTextUnit(Event event) {
		String id = event.getResource().getId();
		
		// If there're no TU Ids in the list, remove targets in all TUs 
		if (Util.isEmpty(tuIds) || tuIds.contains(id)) {
			TextUnit tu = (TextUnit) event.getResource();
			for (LocaleId locId : tu.getTargetLocales()) {
				tu.removeTarget(locId);
			}
		}
		return super.handleTextUnit(event);
	}
}
