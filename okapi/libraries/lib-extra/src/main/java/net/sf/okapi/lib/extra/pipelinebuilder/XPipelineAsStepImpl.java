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

package net.sf.okapi.lib.extra.pipelinebuilder;

import net.sf.okapi.common.pipeline.BasePipelineStep;

/**
 * Pipeline as step delegate. Helper class implementing a pipeline's functionality as a pipeline step.
 * This class allows a pipeline to be inserted in another pipeline as a step.
 * Pipeline step method calls are delegated to an instance of this class instantiated inside the pipeline.
 */
public class XPipelineAsStepImpl extends BasePipelineStep {

	private String description;
	
	public String getName() {
		return "Pipeline as step delegate.";
	}
	public String getDescription() {

		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

}
