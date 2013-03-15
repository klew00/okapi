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

package net.sf.okapi.steps.rainbowkit.transifex;

import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;

public class Parameters extends net.sf.okapi.lib.transifex.Parameters {

	private static final String PROJECTNAME = "projectName";
	
	private String projectName;
	
	public Parameters () {
		reset();
		toString();
	}
	
	public Parameters (String initialData) {
		fromString(initialData);
	}

	@Override
	public void fromString (String data) {
		super.fromString(data);
		projectName = buffer.getString(PROJECTNAME, projectName);
	}

	@Override
	public void reset () {
		super.reset();
		// Additional fields
		projectName = "";
	}

	@Override
	public String toString () {
		super.toString();
		buffer.setString(PROJECTNAME, projectName);
		return buffer.toString();
	}

	public String getProjectName () {
		return projectName;
	}

	public void setProjectName (String projectName) {
		this.projectName = projectName;
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = super.getParametersDescription();
		desc.add(PROJECTNAME, "Project name", "Name of the project");
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramsDesc) {
		EditorDescription desc = super.createEditorDescription(paramsDesc);
		desc.addTextInputPart(paramsDesc.get(PROJECTNAME));
		return desc;
	}

}
