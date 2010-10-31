/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.scopingreport;

import java.net.URI;

import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.ParametersString;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.PathInputPart;
import net.sf.okapi.lib.extra.AbstractParameters;

@EditorFor(Parameters.class)
public class Parameters extends AbstractParameters implements IEditorDescriptionProvider {

	private static final String PROJECTNAME = "projectName";
	private static final String OUTPUTURI = "outputURI";

	private String projectName;
	private URI outputURI;
	
	@Override
	protected void parameters_load(ParametersString buffer) {
		projectName = buffer.getString(PROJECTNAME, "");
		outputURI = Util.toURI(buffer.getString(OUTPUTURI, ""));
	}

	@Override
	protected void parameters_reset() {
		projectName = "";
		outputURI = null;
	}

	@Override
	protected void parameters_save(ParametersString buffer) {
		buffer.setString(PROJECTNAME, projectName);
		buffer.setString(OUTPUTURI, outputURI == null ? "" : outputURI.toString());
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public URI getOutputURI() {
		return outputURI;
	}

	public void setOutputURI(URI outputURI) {
		this.outputURI = outputURI;
	}

	@Override
	public ParametersDescription getParametersDescription() {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(PROJECTNAME,
			"Name of the project", "Name of the project to be display in the report");
		desc.add(OUTPUTURI,
			"Output URI", "Full path of the report to generate");
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Scope Reporting", true, false);
		
		desc.addTextInputPart(paramsDesc.get(PROJECTNAME));
		
		PathInputPart pip = desc.addPathInputPart(paramsDesc.get(OUTPUTURI),
			"Report to Generate", true);
		pip.setBrowseFilters("HTML Files (*.htm;*.html)\tAll Files (*.*)", "*.htm;*.html\t*.*");
		
		return desc;
	}
}
