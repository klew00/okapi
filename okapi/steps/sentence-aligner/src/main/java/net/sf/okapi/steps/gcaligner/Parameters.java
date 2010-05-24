/*===========================================================================
  Copyright (C) 2009-2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.gcaligner;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.CheckboxPart;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.PathInputPart;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	private String tmxOutputPath;
	private boolean generateTMX;	
	private boolean sourceAlreadySegmented;
	private boolean usingCustomTargetSegmentation; // DWH 5-24-10 added this and all relevant code below
	private String srxTargetSegmentationPath=""; // DWH 5-24-10 added this and all relevant code below

	public Parameters () {
		reset();
	}
	
	public String getTmxPath () {
		return tmxOutputPath;
	}

	public void setTmxPath (String tmxOutputPath) {
		this.tmxOutputPath = tmxOutputPath;
	}

	public String getSrxTargetSegmentationPath () {
		return srxTargetSegmentationPath;
	}

	public void setSrxTargetSegmentationPath (String srxTargetSegmentationPath) {
		this.srxTargetSegmentationPath = srxTargetSegmentationPath;
	}

	@Override
	public void reset() {
		tmxOutputPath = "aligned.tmx";
		generateTMX = true;
		sourceAlreadySegmented = false;
		usingCustomTargetSegmentation = false;
		srxTargetSegmentationPath = "";		
	}

	@Override
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		tmxOutputPath = buffer.getString("tmxPath", tmxOutputPath);
		generateTMX = buffer.getBoolean("generateTMX");
		sourceAlreadySegmented = buffer.getBoolean("sourceAlreadySegmented");
		usingCustomTargetSegmentation = buffer.getBoolean("usingCustomTargetSegmentation");
		srxTargetSegmentationPath = buffer.getString("srxTargetSegmentationPath", srxTargetSegmentationPath);
	}

	@Override
	public String toString() {
		buffer.reset();
		buffer.setParameter("tmxPath", tmxOutputPath);
		buffer.setBoolean("generateTMX", generateTMX);
		buffer.setBoolean("alreadySourceSegmented", sourceAlreadySegmented);
		buffer.setBoolean("usingCustomTargetSegmentation", usingCustomTargetSegmentation);
		buffer.setParameter("srxTargetSegmentationPath",srxTargetSegmentationPath);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add("tmxPath",	"TMX output path", "Full path of the output TMX file");
		desc.add("generateTMX",	"Generate TMX?", "If generateTMX is false generate bilingual TextUnits, otherwise (true) output a TMX file");		
		desc.add("sourceAlreadySegmented","Source Already Segmented?", "Have the source input files arelady been segmented?");
		desc.add("usingCustomTargetSegmentation","Use custom target segmentation?","Specify custom target segmentation file?");
		desc.add("srxTargetSegmentationPath",	"Target SRX Path", "Full path of the target segmentation rules");
		return desc;
	}
	
	@Override
	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Gale and Church Sentence Aligner", true, false);	
		desc.addCheckboxPart(paramsDesc.get("sourceAlreadySegmented"));
		CheckboxPart cbp = desc.addCheckboxPart(paramsDesc.get("generateTMX"));
		PathInputPart pip = desc.addPathInputPart(paramsDesc.get("tmxPath"), "TMX Document", true);
		pip.setBrowseFilters("TMX Documents (*.tmx)\tAll Files (*.*)", "*.tmx\t*.*");
		pip.setWithLabel(false);
		pip.setMasterPart(cbp, true);
		CheckboxPart cbp2 = desc.addCheckboxPart(paramsDesc.get("usingCustomTargetSegmentation"));
		PathInputPart pipSRX = desc.addPathInputPart(paramsDesc.get("srxTargetSegmentationPath"), "Target (SRX) Segmentation Rules", false);
		pipSRX.setBrowseFilters("SRX Documents (*.srx)\tAll Files (*.*)", "*.srx\t*.*");
		pipSRX.setWithLabel(false);
		pipSRX.setMasterPart(cbp2, true);
		return desc;
	}

	public boolean isGenerateTMX() {
		return generateTMX;
	}
	
	public void setGenerateTMX (boolean generateTMX) {
		this.generateTMX = generateTMX;
	}

	public boolean isSourceAlreadySegmented() {
		return sourceAlreadySegmented;
	}

	public void setSourceAlreadySegmented(boolean sourceAlreadySegmented) {
		this.sourceAlreadySegmented = sourceAlreadySegmented;
	}

	public boolean isUsingCustomTargetSegmentation() {
		return usingCustomTargetSegmentation;
	}

	public void setUsingCustomTargetSegmentation(boolean usingCustomTargetSegmentation) {
		this.usingCustomTargetSegmentation = usingCustomTargetSegmentation;
	}
}
