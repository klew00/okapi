/*===========================================================================
  Copyright (C) 2009-2011 by the Okapi Framework contributors
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

package net.sf.okapi.steps.common.tufiltering;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	private String tuFilterClassName;
	
	public Parameters() {
		reset();
	}
	
	@Override
	public void reset() {
		tuFilterClassName = null;
	}

	@Override
	public void fromString(String data) {
		reset();
		buffer.fromString(data);
		tuFilterClassName = buffer.getString("tuFilterClassName", tuFilterClassName);
	}

	@Override
	public String toString() {
		buffer.reset();
		buffer.setString("tuFilterClassName", tuFilterClassName);
		return buffer.toString();
	}

	public void setTuFilterClassName(String tuFilterClassName) {
		this.tuFilterClassName = tuFilterClassName;
	}

	public String getTuFilterClassName() {
		return tuFilterClassName;
	}

	@Override
	public ParametersDescription getParametersDescription() {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add("tuFilterClassName", "Class name for the text unit filter", null);
		return desc;
	}
	
	@Override
	public EditorDescription createEditorDescription(
			ParametersDescription parametersDescription) {
		EditorDescription desc = new EditorDescription("Text Unit Filtering", true, false);
		desc.addTextInputPart(parametersDescription.get("tuFilterClassName"));
		return desc;
	}
}
