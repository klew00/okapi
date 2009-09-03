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

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.ParametersString;
import net.sf.okapi.common.pipeline.IPipelineStep;
import net.sf.okapi.filters.plaintext.common.AbstractParameters;

public class CompoundStepParametersItem extends AbstractParameters {

	public String stepClass;
	public String parametersLocation;
	public boolean enabled;
	
	public IPipelineStep step; // non-serializable
	public IParameters parameters; // non-serializable
	
	@Override
	protected void parameters_reset() {
		
		enabled = true;
		stepClass = "";
		parametersLocation = "";
	}

	@Override
	protected void parameters_load(ParametersString buffer) {

		enabled = buffer.getBoolean("enabled", true);
		stepClass = buffer.getString("stepClass");
		parametersLocation = buffer.getString("parametersLocation");
	}

	@Override
	protected void parameters_save(ParametersString buffer) {
		
		buffer.setBoolean("enabled", enabled);
		buffer.setString("stepClass", stepClass);
		buffer.setString("parametersLocation", parametersLocation);
	}
		
}
