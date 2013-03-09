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
===========================================================================*/

package net.sf.okapi.applications.rainbow.utilities;

import java.io.File;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.pipeline.IPipelineStep;

public class UtilityStep implements IPipelineStep {

	private IFilterDrivenUtility utility;

	public UtilityStep (IFilterDrivenUtility utility) {
		this.utility = utility;
	}

	public Event handleEvent (Event event) {
		utility.handleEvent(event);
		return event;
	}
	
	public String getHelpLocation () {
		return ".." + File.separator + "help" + File.separator + "steps";
	}

	public void cancel () {
		// Cancel needed here
	}

	public String getName () {
		return utility.getName();
	}

	public String getDescription () {
		// TODO: Implement real descriptions
		return utility.getName();
	}
	
	public void pause () {
	}

	public void postprocess () {
		utility.postprocess();
	}

	public void preprocess () {
		utility.preprocess();
	}

	public void destroy () {
		utility.destroy(); 
	}

	public boolean hasNext () {
		return false;
	}

	
	//======================= Just temporary until we move all utilities to steps
	
	public IParameters getParameters() {
		//x TODO Auto-generated method stub
		return null;
	}

	public void setParameters(IParameters params) {
		//x TODO Auto-generated method stub		
	}

	public boolean isDone() {
		//x TODO Auto-generated method stub
		return false;
	}

	public boolean isLastOutputStep() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setLastOutputStep(boolean isLastStep) {
		// TODO Auto-generated method stub		
	}
}
