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

package net.sf.okapi.steps.common;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

@EditorFor(ExtractionVerificationStepParameters.class)
public class ExtractionVerificationStepParameters extends BaseParameters implements IEditorDescriptionProvider {

	private static final String COMPARESKELETON = "compareSkeleton";
	private static final String COMPAREPROPERTIES = "compareProperties";
	private static final String COMPAREANNOTATIONS = "compareAnnotations";
	
	private boolean compareSkeleton;	
	private boolean compareProperties;
	private boolean compareAnnotations;
	
	public ExtractionVerificationStepParameters() {
		reset();
	}

	public void reset() {
		compareSkeleton = true;
		compareProperties = true;
		compareAnnotations = true;
	}

	public void fromString(String data) {
		reset();
		// Read the file content as a set of fields
		compareSkeleton = buffer.getBoolean(COMPARESKELETON, compareSkeleton);
		compareProperties = buffer.getBoolean(COMPAREPROPERTIES, compareProperties);
		compareAnnotations = buffer.getBoolean(COMPAREANNOTATIONS, compareAnnotations);
	}

	public String toString() {
		buffer.reset();		
		buffer.setBoolean(COMPARESKELETON, compareSkeleton);
		buffer.setBoolean(COMPAREPROPERTIES, compareProperties);
		buffer.setBoolean(COMPAREANNOTATIONS, compareAnnotations);
		return buffer.toString();
	}


	public boolean isCompareSkeleton() {
		return compareSkeleton;
	}

	public void setCompareSkeleton(boolean compareSkeleton) {
		this.compareSkeleton = compareSkeleton;
	}

	public boolean isCompareProperties() {
		return compareProperties;
	}

	public void setCompareProperties(boolean compareProperties) {
		this.compareProperties = compareProperties;
	}

	public boolean isCompareAnnotations() {
		return compareAnnotations;
	}

	public void setCompareAnnotations(boolean compareAnnotations) {
		this.compareAnnotations = compareAnnotations;
	}

	@Override
	public ParametersDescription getParametersDescription() {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(COMPARESKELETON, "Compare skeleton", null);
		desc.add(COMPAREPROPERTIES, "Compare properties", null);
		desc.add(COMPAREANNOTATIONS, "Compare annotations", null);
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Compare TextUnits", true, false);		
		desc.addCheckboxPart(paramsDesc.get(COMPARESKELETON));
		desc.addSeparatorPart();
		desc.addCheckboxPart(paramsDesc.get(COMPAREPROPERTIES));
		desc.addSeparatorPart();
		desc.addCheckboxPart(paramsDesc.get(COMPAREANNOTATIONS));
		return desc;
	}
}
