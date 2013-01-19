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
============================================================================*/

package net.sf.okapi.steps.fileanalysis;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.PathInputPart;

public class Parameters extends BaseParameters implements IEditorDescriptionProvider {
	
	private static String OUTPUTPATH = "outputPath";
	private static String AUTOOPEN = "autoOpen";
	
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

	public boolean getAutoOpen () {
		return autoOpen;
	}

	public void setAutoOpen (boolean autoOpen) {
		this.autoOpen = autoOpen;
	}

	@Override
	public void reset() {
		outputPath = "file-analysis.txt";
		autoOpen = true;
	}

	@Override
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		outputPath = buffer.getString(OUTPUTPATH, outputPath);
		autoOpen = buffer.getBoolean(AUTOOPEN, autoOpen);
	}

	@Override
	public String toString() {
		buffer.reset();
		buffer.setParameter(OUTPUTPATH, outputPath);
		buffer.setParameter(AUTOOPEN, autoOpen);
		return buffer.toString();
	}
	
	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(OUTPUTPATH,
			"Path of the result file", "Full path of the result file.");
		desc.add(AUTOOPEN,
			"Open the result file after completion", null);
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramDesc) {
		EditorDescription desc = new EditorDescription("File Analysis", true, false);

		PathInputPart part = desc.addPathInputPart(paramDesc.get(OUTPUTPATH), "Result File", true);
		part.setBrowseFilters("Text Files (*.txt)\tAll Files (*.*)", "*.txt\t*.*");
		
		desc.addCheckboxPart(paramDesc.get(AUTOOPEN));
		
		return desc;
	}
	
}
