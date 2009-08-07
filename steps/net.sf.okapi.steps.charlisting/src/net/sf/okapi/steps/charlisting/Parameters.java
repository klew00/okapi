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

package net.sf.okapi.steps.charlisting;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.ParametersDescription;

public class Parameters extends BaseParameters {

	private String outputPath;
	private boolean autoOpen;

	public Parameters () {
		reset();
	}
	
	public String getOutputPath () {
		return outputPath;
	}

	public void setOutputPath (String outputPath) {
		this.outputPath = outputPath;
	}

	public boolean isAutoOpen () {
		return autoOpen;
	}

	public void setAutoOpen (boolean autoOpen) {
		this.autoOpen = autoOpen;
	}

	public void reset() {
		outputPath = "charlist.txt";
		autoOpen = true;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		outputPath = buffer.getString("outputPath", outputPath);
		autoOpen = buffer.getBoolean("autoOpen", autoOpen);
	}

	@Override
	public String toString() {
		buffer.reset();
		buffer.setParameter("outputPath", outputPath);
		buffer.setParameter("autoOpen", autoOpen);
		return buffer.toString();
	}
	
	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add("outputPath",
			"Path of the result file", "Full path of the result file.");
		desc.add("autoOpen",
			"Open the result file after completion", null);
		return desc;
	}

}
