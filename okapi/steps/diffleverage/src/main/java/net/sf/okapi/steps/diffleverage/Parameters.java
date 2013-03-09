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

package net.sf.okapi.steps.diffleverage;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.SpinInputPart;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {
	
	private int fuzzyThreshold;
	private boolean codesensitive;
	private boolean diffOnly;
	private boolean copyToTarget;
	//private boolean diffOnSentences;
	
	public Parameters() {
		reset();
	}
	
	@Override
	public void reset() {	
		// default is exact match
		fuzzyThreshold = 100;
		codesensitive = true;
		diffOnly = false;
		copyToTarget = false;
		//diffOnSentences = false;
	}

	@Override
	public void fromString(String data) {
		reset();
		buffer.fromString(data);
		fuzzyThreshold = buffer.getInteger("fuzzyThreshold", fuzzyThreshold);
		codesensitive = buffer.getBoolean("codesensitive", codesensitive);
		diffOnly = buffer.getBoolean("diffOnly", diffOnly);
		copyToTarget = buffer.getBoolean("copyToTarget", copyToTarget);
		//diffOnSentences = buffer.getBoolean("diffOnSentences", diffOnSentences);
	}
	
	@Override
	public String toString() {
		buffer.reset();
		buffer.setParameter("fuzzyThreshold", fuzzyThreshold);		
		buffer.setParameter("codesensitive", codesensitive);
		buffer.setParameter("diffOnly", diffOnly);
		buffer.setParameter("copyToTarget", copyToTarget);
		//buffer.setParameter("diffOnSentences", diffOnSentences);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add("fuzzyThreshold", "Leverage only if the match is equal or above this score", "Fuzzy Thresholds are between 1 and 100. A score of 100 emans exact match (codes and text) only");		
		desc.add("codesensitive", "Include inline codes in the comparison", "Use codes to compare contents");
		desc.add("diffOnly", "Diff only and mark the TextUnit as matched", "Diff only and do not copy the match or create a leverage annotation");
		desc.add("copyToTarget", 
				"Copy to/over the target? (WARNING: Copied target will not be segmented!)", "Copy to/over the target (a leverage annotation " +
				"will still be created). WARNING: Copied target will not be segmented and any exisiting target will be lost.");
		//desc.add("diffOnSentences", "Diff on sentences or paragraphs (if sentences then source and target must be aligned)?", "Diff On Sentences?");
		return desc;
	}
	
	@Override
	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Diff Leverage", true, false);	
		SpinInputPart sip = desc.addSpinInputPart(paramsDesc.get("fuzzyThreshold"));
		sip.setRange(1, 100);
		sip.setVertical(false);
		desc.addCheckboxPart(paramsDesc.get("codesensitive"));
		desc.addSeparatorPart();
		desc.addCheckboxPart(paramsDesc.get("diffOnly"));
		desc.addCheckboxPart(paramsDesc.get("copyToTarget"));
		//desc.addCheckboxPart(paramsDesc.get("diffOnSentences"));
		return desc;
	}
	
	public int getFuzzyThreshold() {
		return fuzzyThreshold;
	}
	
	public void setFuzzyThreshold(int fuzzyThreshold) {
		this.fuzzyThreshold = fuzzyThreshold;
	}

	public boolean isCodesensitive() {
		return codesensitive;
	}

	public void setCodesensitive(boolean codesensitive) {
		this.codesensitive = codesensitive;
	}

	public boolean isDiffOnly() {
		return diffOnly;
	}

	public void setDiffOnly(boolean diffOnly) {
		this.diffOnly = diffOnly;
	}

	public void setCopyToTarget(boolean copyToTarget) {
		this.copyToTarget = copyToTarget;
	}

	public boolean isCopyToTarget() {
		return copyToTarget;
	}

//	public boolean isDiffOnSentences() {
//		return diffOnSentences;
//	}
//
//	public void setDiffOnSentences(boolean diffOnSentences) {
//		this.diffOnSentences = diffOnSentences;
//	}
}
