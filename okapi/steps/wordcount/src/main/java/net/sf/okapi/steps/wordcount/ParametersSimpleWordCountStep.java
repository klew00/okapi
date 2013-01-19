/*===========================================================================
  Copyright (C) 2011-2012 by the Okapi Framework contributors
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

package net.sf.okapi.steps.wordcount;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

// For now we don't use the parameters
// @EditorFor(ParametersSimpleWordCountStep.class)
public class ParametersSimpleWordCountStep extends BaseParameters implements IEditorDescriptionProvider {	

	public static final String COUNTTARGETS = "countTargets";
	
	public boolean countTargets;
	
	public ParametersSimpleWordCountStep () {
		reset();
	}

	@Override
	public void reset () {
		countTargets = false;
	}

	@Override
	public void fromString (String data) {
		reset();
		// Read the file content as a set of fields
		buffer.fromString(data);
		countTargets = buffer.getBoolean(COUNTTARGETS, countTargets);
	}
	
	@Override
	public String toString () {
		buffer.reset();
		buffer.setBoolean(COUNTTARGETS, countTargets);		
		return buffer.toString();
	}
	
	public boolean getCountTargets () {
		return countTargets;
	}
	
	public void setCountTargets(boolean countTargets) {
		this.countTargets = countTargets;
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(COUNTTARGETS, "Count also the target entries", null);
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Translation Comparison", true, false);
		desc.addCheckboxPart(paramsDesc.get(COUNTTARGETS));
		return desc;
	}

}
