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

import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.ParametersString;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.PathInputPart;
import net.sf.okapi.lib.extra.AbstractParameters;

@EditorFor(Parameters.class)
public class Parameters extends AbstractParameters implements IEditorDescriptionProvider {

	private static final String PROJECT_NAME = "projectName";
	private static final String CUSTOM_TEMPLATE_URI = "customTemplateURI";
	private static final String OUTPUT_PATH = "outputPath";
	//private static final String EMPTY_URI = "file:///";
	private static final String EMPTY_URI = "";

	private String projectName;
	private String outputPath;
	private String customTemplateURI;
	
	public String getCustomTemplateURI() {
		return customTemplateURI;
	}

	public void setCustomTemplateURI(String customTemplateURI) {
		this.customTemplateURI = customTemplateURI;
	}

	@Override
	protected void parameters_load(ParametersString buffer) {
		parameters_reset();
		projectName = buffer.getString(PROJECT_NAME, projectName);
		//customTemplateURI = Util.toURI(buffer.getString(CUSTOM_TEMPLATE_URI, EMPTY_URI));
		customTemplateURI = buffer.getString(CUSTOM_TEMPLATE_URI, customTemplateURI);
		outputPath = buffer.getString(OUTPUT_PATH, outputPath);		
	}

	@Override
	protected void parameters_reset() {
		// Default values
		projectName = "My Project";
//		try {
//			customTemplateURI = new URI(EMPTY_URI); 
//		} catch (URISyntaxException e) {
//			new RuntimeException(e);
//		}
		customTemplateURI = EMPTY_URI;
		outputPath = "${rootDir}/scoping_report.html";
	}

	@Override
	protected void parameters_save(ParametersString buffer) {
		buffer.setString(PROJECT_NAME, projectName);
		buffer.setString(CUSTOM_TEMPLATE_URI, customTemplateURI == null ? EMPTY_URI : customTemplateURI.toString());
		buffer.setString(OUTPUT_PATH, outputPath);
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getOutputPath () {
		return outputPath;
	}

	public void setOutputPath (String outputPath) {
		this.outputPath = outputPath;
	}

	@Override
	public ParametersDescription getParametersDescription() {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(PROJECT_NAME,
			"Name of the project", "Name of the project to be displayed in the report");
		desc.add(CUSTOM_TEMPLATE_URI,
				"Custom template URI:", "URI of the report template");
		desc.add(OUTPUT_PATH,
			"Output path:", "Full path of the report to generate");
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Scope Reporting", true, false);
		
		desc.addTextInputPart(paramsDesc.get(PROJECT_NAME));
		
		PathInputPart ctpip = desc.addPathInputPart(paramsDesc.get(CUSTOM_TEMPLATE_URI),
				"Custon Template", false);
		ctpip.setAllowEmpty(true);
		ctpip.setBrowseFilters("HTML Files (*.htm;*.html)\tAll Files (*.*)", "*.htm;*.html\t*.*");
		
		PathInputPart pip = desc.addPathInputPart(paramsDesc.get(OUTPUT_PATH),
			"Report to Generate", true);
		pip.setBrowseFilters("HTML Files (*.htm;*.html)\tAll Files (*.*)", "*.htm;*.html\t*.*");
		
		return desc;
	}
	
	public boolean useDefaultTemplate() {
		return EMPTY_URI.equalsIgnoreCase(customTemplateURI.toString()); 
	}
}
