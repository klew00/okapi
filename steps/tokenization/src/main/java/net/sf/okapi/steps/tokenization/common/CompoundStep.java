/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.steps.tokenization.common;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.pipeline.IPipeline;
import net.sf.okapi.common.pipeline.Pipeline;
import net.sf.okapi.filters.plaintext.common.Notification;

public class CompoundStep extends AbstractPipelineStep {

	private IPipeline pipeline = new Pipeline();
	private CompoundStepParameters params; 
	
	@Override
	protected void component_init() {
		
		params = getParameters(CompoundStepParameters.class);
		
		if (pipeline == null) return;
		pipeline.startBatch();				
	}

	@Override
	protected void component_done() {
		
		if (pipeline == null) return;
		pipeline.endBatch();
	}

	private void rebuildPipeline() {
		
		if (pipeline == null) return;
		if (params == null) return;
		
		pipeline.clearSteps();
		
		for (CompoundStepParametersItem item : params.getItems())			
			pipeline.addStep(item.step);
	}
	
	@Override
	public boolean exec(Object sender, String command, Object info) {
		
		if (super.exec(sender, command, info)) return true;
		
		if (command.equalsIgnoreCase(Notification.PARAMETERS_CHANGED)) {
			
			rebuildPipeline();
			return true;
		}
		
		return false;
	}

	@Override
	public Event handleEvent(Event event) {
		
		return pipeline.process(event);
	}
	
}
