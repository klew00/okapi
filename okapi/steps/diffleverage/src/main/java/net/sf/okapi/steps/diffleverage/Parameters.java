/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.TextInputPart;

public class Parameters extends BaseParameters implements IEditorDescriptionProvider {
	private int fuzzyThreshold;
	private boolean codesensitive;
	
	public Parameters() {
		reset();
	}
	
	@Override
	public void reset() {	
		// default is exact match
		fuzzyThreshold = 100;
		codesensitive = true;
	}

	@Override
	public void fromString(String data) {
		reset();
		buffer.fromString(data);
		fuzzyThreshold = buffer.getInteger("fuzzyThreshold", fuzzyThreshold);
		codesensitive = buffer.getBoolean("codesensitive", codesensitive);
	}
	
	@Override
	public String toString() {
		buffer.reset();
		buffer.setParameter("fuzzyThreshold", fuzzyThreshold);		
		buffer.setParameter("codesensitive", codesensitive);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add("fuzzyThreshold", "Fuzzy Threshold", "Fuzzy Threshold between 0 and 100.");		
		desc.add("codesensitive", "Compare Codes", "Use codes to compare TextUnits?");
		return desc;
	}
	
	@Override
	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Diff Leverager", true, false);	
		TextInputPart tp = desc.addTextInputPart(paramsDesc.get("fuzzyThreshold"));
		tp.setRange(0, 100);
		desc.addCheckboxPart(paramsDesc.get("codesensitive"));
		return desc;
	}
	
	public int getFuzyThreshold() {
		return fuzzyThreshold;
	}
	
	public boolean isCodeSensitive() {
		return codesensitive;
	}
}
