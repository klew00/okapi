/*===========================================================================
  Copyright (C) 2008-2013 by the Okapi Framework contributors
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

package net.sf.okapi.steps.xliffkit.reader;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	static final String GENERATE_TARGETS = "generateTargets"; //$NON-NLS-1$
	static final String USE_APPROVED_ONLY = "useApprovedOnly"; //$NON-NLS-1$
	static final String UPDATE_APPROVED_FLAG = "updateApprovedFlag"; //$NON-NLS-1$
	static final String GROUP_BY_PACKAGE_PATH = "groupByPackagePath"; //$NON-NLS-1$
	
	private boolean generateTargets; 
	private boolean useApprovedOnly;
	private boolean updateApprovedFlag;
	private boolean groupByPackagePath;
	
	public Parameters () {
		reset();
	}
	
	@Override
	public void fromString(String data) {
		reset();
		buffer.fromString(data);		
		generateTargets = buffer.getBoolean(GENERATE_TARGETS, generateTargets);
		useApprovedOnly = buffer.getBoolean(USE_APPROVED_ONLY, useApprovedOnly);
		updateApprovedFlag = buffer.getBoolean(UPDATE_APPROVED_FLAG, updateApprovedFlag);
		groupByPackagePath = buffer.getBoolean(GROUP_BY_PACKAGE_PATH, groupByPackagePath);
	}

	@Override
	public String toString () {
		buffer.reset();		
		buffer.setParameter(GENERATE_TARGETS, generateTargets);		
		buffer.setParameter(USE_APPROVED_ONLY, useApprovedOnly);
		buffer.setParameter(UPDATE_APPROVED_FLAG, updateApprovedFlag);
		buffer.setParameter(GROUP_BY_PACKAGE_PATH, groupByPackagePath);
		return buffer.toString();
	}
	
	@Override
	public void reset() {
		generateTargets = true;
		useApprovedOnly = false;
		updateApprovedFlag = true;
		groupByPackagePath = true;
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);		
		desc.add(GENERATE_TARGETS, "Generate target files in the output directory", "Generate targets");		
		desc.add(USE_APPROVED_ONLY, "Update target only if translation was approved", "Use only approved translation");
		desc.add(UPDATE_APPROVED_FLAG, "Update the approved flag if translation was approved", "Update approved flag");
		desc.add(GROUP_BY_PACKAGE_PATH, "Group target files by their paths in the package", "Group targets");
		return desc;
	}
	
	@Override
	public EditorDescription createEditorDescription(
			ParametersDescription parametersDescription) {
		EditorDescription desc = new EditorDescription("XLIFF Kit Reader Options", true, false);		
		desc.addCheckboxPart(parametersDescription.get(GENERATE_TARGETS));		
		desc.addCheckboxPart(parametersDescription.get(USE_APPROVED_ONLY));
		desc.addCheckboxPart(parametersDescription.get(UPDATE_APPROVED_FLAG));
		desc.addCheckboxPart(parametersDescription.get(GROUP_BY_PACKAGE_PATH));
		return desc;
	}

	public void setGenerateTargets(boolean generateTargets) {
		this.generateTargets = generateTargets;
	}

	public boolean isGenerateTargets() {
		return generateTargets;
	}

	public void setUseApprovedOnly(boolean useApprovedOnly) {
		this.useApprovedOnly = useApprovedOnly;
	}

	public boolean isUseApprovedOnly() {
		return useApprovedOnly;
	}

	public void setUpdateApprovedFlag(boolean updateApprovedFlag) {
		this.updateApprovedFlag = updateApprovedFlag;
	}

	public boolean isUpdateApprovedFlag() {
		return updateApprovedFlag;
	}

	public boolean isGroupByPackagePath() {
		return groupByPackagePath;
	}

	public void setGroupByPackagePath(boolean groupByPackagePath) {
		this.groupByPackagePath = groupByPackagePath;
	}

}
