/*===========================================================================
  Copyright (C) 2009-2012 by the Okapi Framework contributors
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

package net.sf.okapi.steps.sentencealigner;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.CheckboxPart;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.PathInputPart;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	private static final String GENERATETMX = "generateTMX";
	private static final String TMXOUTPUTPATH = "tmxOutputPath";
	private static final String COLLAPSEWHITESPACE = "collapseWhitespace"; 
	private static final String OUTPUT_ONE_TO_ONE_MATCHES_ONLY = "outputOneTOneMatchesOnly";
		
	private boolean generateTMX;
	private String tmxOutputPath;

	private boolean segmentSource;
	private boolean useCustomSourceRules;
	private String customSourceRulesPath;

	private boolean segmentTarget;
	private boolean useCustomTargetRules;
	private String customTargetRulesPath;
	
	private boolean collapseWhitespace;
	
	private boolean outputOneTOneMatchesOnly;

	public Parameters() {
		reset();
	}

	public boolean isGenerateTMX() {
		return generateTMX;
	}

	public void setGenerateTMX(boolean generateTMX) {
		this.generateTMX = generateTMX;
	}

	public String getTmxOutputPath() {
		return tmxOutputPath;
	}

	public void setTmxOutputPath(String tmxOutputPath) {
		this.tmxOutputPath = tmxOutputPath;
	}

	public boolean isSegmentSource() {
		return segmentSource;
	}

	public void setSegmentSource(boolean segmentSource) {
		this.segmentSource = segmentSource;
	}

	public boolean isUseCustomSourceRules() {
		return useCustomSourceRules;
	}

	public void setUseCustomSourceRules(boolean useCustomSourceRules) {
		this.useCustomSourceRules = useCustomSourceRules;
	}

	public String getCustomSourceRulesPath() {
		return customSourceRulesPath;
	}

	public void setCustomSourceRulesPath(String customSourceRulesPath) {
		this.customSourceRulesPath = customSourceRulesPath;
	}

	public boolean isSegmentTarget() {
		return segmentTarget;
	}

	public void setSegmentTarget(boolean segmentTarget) {
		this.segmentTarget = segmentTarget;
	}

	public boolean isUseCustomTargetRules() {
		return useCustomTargetRules;
	}

	public void setUseCustomTargetRules(boolean useCustomTargetRules) {
		this.useCustomTargetRules = useCustomTargetRules;
	}

	public String getCustomTargetRulesPath() {
		return customTargetRulesPath;
	}

	public void setCustomTargetRulesPath(String customTargetRulesPath) {
		this.customTargetRulesPath = customTargetRulesPath;
	}

	public boolean isCollapseWhitespace() {
		return collapseWhitespace;
	}

	public void setCollapseWhitespace(boolean collapseWhitespace) {
		this.collapseWhitespace = collapseWhitespace;
	}

	public boolean isOutputOneTOneMatchesOnly() {
		return outputOneTOneMatchesOnly;
	}

	public void setOutputOneTOneMatchesOnly(boolean outputOneTOneMatchesOnly) {
		this.outputOneTOneMatchesOnly = outputOneTOneMatchesOnly;
	}
	
	@Override
	public void reset() {
		tmxOutputPath = "aligned.tmx";
		generateTMX = true;
		segmentSource = true;
		useCustomSourceRules = false;
		customSourceRulesPath = "";
		segmentTarget = true;
		useCustomTargetRules = false;
		customTargetRulesPath = "";
		collapseWhitespace = false;
		outputOneTOneMatchesOnly = false;
	}

	@Override
	public void fromString(String data) {
		reset();
		buffer.fromString(data);
		generateTMX = buffer.getBoolean(GENERATETMX, generateTMX);
		tmxOutputPath = buffer.getString(TMXOUTPUTPATH, tmxOutputPath);

		segmentSource = buffer.getBoolean("segmentSource", segmentSource);
		useCustomSourceRules = buffer.getBoolean("useCustomSourceRules", useCustomSourceRules);
		customSourceRulesPath = buffer.getString("customSourceRulesPath", customSourceRulesPath);
		segmentTarget = buffer.getBoolean("segmentTarget", segmentTarget);
		useCustomTargetRules = buffer.getBoolean("useCustomTargetRules", useCustomTargetRules);
		customTargetRulesPath = buffer.getString("customTargetRulesPath", customTargetRulesPath);
		collapseWhitespace = buffer.getBoolean(COLLAPSEWHITESPACE, collapseWhitespace);
		outputOneTOneMatchesOnly = buffer.getBoolean(OUTPUT_ONE_TO_ONE_MATCHES_ONLY, outputOneTOneMatchesOnly);
	}

	@Override
	public String toString() {
		buffer.reset();
		buffer.setBoolean(GENERATETMX, generateTMX);
		buffer.setParameter(TMXOUTPUTPATH, tmxOutputPath);

		buffer.setBoolean("segmentSource", segmentSource);
		buffer.setBoolean("useCustomSourceRules", useCustomSourceRules);
		buffer.setParameter("customSourceRulesPath", customSourceRulesPath);
		buffer.setBoolean("segmentTarget", segmentTarget);
		buffer.setBoolean("useCustomTargetRules", useCustomTargetRules);
		buffer.setString("customTargetRulesPath", customTargetRulesPath);
		buffer.setBoolean(COLLAPSEWHITESPACE, collapseWhitespace);
		buffer.setBoolean(OUTPUT_ONE_TO_ONE_MATCHES_ONLY, outputOneTOneMatchesOnly);		
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription() {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(GENERATETMX, "Generate the following TMX document:",
				"If generateTMX is false generate bilingual TextUnits, otherwise (true) output a TMX file");
		desc.add(TMXOUTPUTPATH, "TMX output path", "Full path of the output TMX file");

		desc.add("segmentSource",
				"Segment the source content (overriding possible existing segmentation)", null);
		desc.add("useCustomSourceRules",
				"Use custom source segmentation rules (instead of the default ones)", null);
		desc.add("customSourceRulesPath", "SRX path for the source",
				"Full path of the SRX document to use for the source");
		desc.add("segmentTarget",
				"Segment the target content (overriding possible existing segmentation)", null);
		desc.add("useCustomTargetRules",
				"Use custom target segmentation rules (instead of the default ones)", null);
		desc.add("customTargetRulesPath", "SRX path for the target",
				"Full path of the SRX document to use for the target");
		desc.add(COLLAPSEWHITESPACE, "Collapse whitspace", 
				"Collapse whitespace (space, newline etc.) to a single space before segmentation and alignment");
		desc.add(OUTPUT_ONE_TO_ONE_MATCHES_ONLY, "Output 1-1 matches only", 
			"Ouput only 1-1 sentence aligned matches");
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Sentence Aligner", true, false);
		CheckboxPart cbp = desc.addCheckboxPart(paramsDesc.get(GENERATETMX));
		PathInputPart pip = desc.addPathInputPart(paramsDesc.get(TMXOUTPUTPATH),
				"TMX Document to Generate", true);
		pip.setBrowseFilters("TMX Documents (*.tmx)\tAll Files (*.*)", "*.tmx\t*.*");
		pip.setWithLabel(false);
		pip.setMasterPart(cbp, true);

		desc.addSeparatorPart();

		CheckboxPart cbp1 = desc.addCheckboxPart(paramsDesc.get("segmentSource"));
		CheckboxPart cbp2 = desc.addCheckboxPart(paramsDesc.get("useCustomSourceRules"));
		cbp2.setMasterPart(cbp1, true);
		pip = desc.addPathInputPart(paramsDesc.get("customSourceRulesPath"),
				"Segmentation Rules for Source", false);
		pip.setBrowseFilters("SRX Documents (*.srx)\tAll Files (*.*)", "*.srx\t*.*");
		pip.setWithLabel(false);
		pip.setMasterPart(cbp2, true);

		desc.addSeparatorPart();

		cbp1 = desc.addCheckboxPart(paramsDesc.get("segmentTarget"));
		cbp2 = desc.addCheckboxPart(paramsDesc.get("useCustomTargetRules"));
		cbp2.setMasterPart(cbp1, true);
		pip = desc.addPathInputPart(paramsDesc.get("customTargetRulesPath"),
				"Segmentation Rules for Target", false);
		pip.setBrowseFilters("SRX Documents (*.srx)\tAll Files (*.*)", "*.srx\t*.*");
		pip.setWithLabel(false);
		pip.setMasterPart(cbp2, true);
		
		desc.addSeparatorPart();
		desc.addCheckboxPart(paramsDesc.get(COLLAPSEWHITESPACE));		
		desc.addSeparatorPart();
		desc.addCheckboxPart(paramsDesc.get(OUTPUT_ONE_TO_ONE_MATCHES_ONLY));
		
		return desc;
	}
}
