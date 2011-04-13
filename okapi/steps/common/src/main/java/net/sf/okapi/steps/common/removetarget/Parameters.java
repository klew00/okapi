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

package net.sf.okapi.steps.common.removetarget;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	private static final String TUS_FOR_TARGET_REMOVAL = "tusForTargetRemoval";
	
	private String tusForTargetRemoval;
	
	public Parameters() {
		reset();
	}
	
	public void reset() {
		tusForTargetRemoval = "";
	}

	@Override
	public void fromString(String data) {
		reset();
		buffer.fromString(data);
		// Read the file content as a set of fields
		tusForTargetRemoval = buffer.getString(TUS_FOR_TARGET_REMOVAL, tusForTargetRemoval);
	}

	@Override
	public String toString() {
		buffer.reset();		
		buffer.setString(TUS_FOR_TARGET_REMOVAL, tusForTargetRemoval);
		return buffer.toString();
	}
	
	public void setTusForTargetRemoval(String tusForTargetRemoval) {
		this.tusForTargetRemoval = tusForTargetRemoval;
	}

	public String getTusForTargetRemoval() {
		return tusForTargetRemoval;
	}

	@Override
	public ParametersDescription getParametersDescription() {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(TUS_FOR_TARGET_REMOVAL, "A comma-delimited list of Ids of the TUs which targets are to be removed. " +
				"Leave empty to remove targets in all TUs.", null);
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("TU Ids for target removal", true, false);		
		desc.addTextInputPart(paramsDesc.get(TUS_FOR_TARGET_REMOVAL));
		return desc;
	}

}
