/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.idaligner;

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
	private static final String REPLACEWITHSOURCE = "replaceWithSource";

	private boolean generateTMX;
	private String tmxOutputPath;
	private boolean replaceWithSource;
	private boolean copyToTarget;
	private boolean storeAsAltTranslation;

	public Parameters() {
		reset();
	}

	public boolean getGenerateTMX() {
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
	
	public boolean getReplaceWithSource() {
		return replaceWithSource;
	}
	
	public void setReplaceWithSource(boolean replaceWithSource) {
		this.replaceWithSource = replaceWithSource;
	}

	public void setCopyToTarget(boolean copyToTarget) {
		this.copyToTarget = copyToTarget;
	}

	public boolean isCopyToTarget() {
		return copyToTarget;
	}
	
	/**
	 * @return the storeAsAltTranslation
	 */
	public boolean isStoreAsAltTranslation() {
		return storeAsAltTranslation;
	}

	/**
	 * @param storeAsAltTranslation the storeAsAltTranslation to set
	 */
	public void setStoreAsAltTranslation(boolean storeAsAltTranslation) {
		this.storeAsAltTranslation = storeAsAltTranslation;
	}

	@Override
	public void reset() {
		tmxOutputPath = "aligned.tmx";
		generateTMX = false;
		replaceWithSource = false;
		copyToTarget = false;
		storeAsAltTranslation = true;
	}

	@Override
	public void fromString(String data) {
		reset();
		buffer.fromString(data);
		generateTMX = buffer.getBoolean(GENERATETMX, generateTMX);
		tmxOutputPath = buffer.getString(TMXOUTPUTPATH, tmxOutputPath);
		replaceWithSource = buffer.getBoolean(REPLACEWITHSOURCE, replaceWithSource);
		copyToTarget = buffer.getBoolean("copyToTarget", copyToTarget);
		storeAsAltTranslation = buffer.getBoolean("storeAsAltTranslation", storeAsAltTranslation);
	}

	@Override
	public String toString() {
		buffer.reset();
		buffer.setBoolean(GENERATETMX, generateTMX);
		buffer.setParameter(TMXOUTPUTPATH, tmxOutputPath);
		buffer.setBoolean(REPLACEWITHSOURCE, replaceWithSource);
		buffer.setParameter("copyToTarget", copyToTarget);
		buffer.setParameter("storeAsAltTranslation", storeAsAltTranslation);
		
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription() {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(GENERATETMX, "Generate a TMX file:",
				"If generateTMX is false generate bilingual TextUnits, otherwise (true) output a TMX file");
		desc.add(TMXOUTPUTPATH, "TMX output path", "Full path of the output TMX file");
		desc.add(REPLACEWITHSOURCE, "Replace with source", "If no target text available, use the source text");
		desc.add("copyToTarget", 
				"Copy to/over the target? (WARNING: Copied target will not be segmented!)", "Copy to/over the target (a leverage annotation " +
				"will still be created). WARNING: Copied target will not be segmented and any exisiting target will be lost.");
		desc.add("storeAsAltTranslation", "Store the target as an alternate translation?", "If true store the matched target so that subsequent steps can see it.");
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Id-based Aligner", true, false);
		CheckboxPart cbp = desc.addCheckboxPart(paramsDesc.get(GENERATETMX));
		PathInputPart pip = desc.addPathInputPart(paramsDesc.get(TMXOUTPUTPATH),
				"TMX Document to Generate", true);
		pip.setBrowseFilters("TMX Documents (*.tmx)\tAll Files (*.*)", "*.tmx\t*.*");
		pip.setWithLabel(false);
		pip.setMasterPart(cbp, true);

		desc.addSeparatorPart();
		desc.addCheckboxPart(paramsDesc.get(REPLACEWITHSOURCE));
		desc.addCheckboxPart(paramsDesc.get("copyToTarget"));
		desc.addCheckboxPart(paramsDesc.get("storeAsAltTranslation"));
		return desc;
	}
}
