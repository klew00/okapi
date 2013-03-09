/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package net.sf.okapi.steps.common.codesimplifier;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	private static final String REMOVE_LEADING_TRAILING_CODES = "removeLeadingTrailingCodes";
	
	private boolean removeLeadingTrailingCodes;
	
	public Parameters() {
		reset();
	}
	
	@Override
	public void reset() {
		removeLeadingTrailingCodes = true;
	}

	@Override
	public void fromString(String data) {
		reset();
		buffer.fromString(data);
		removeLeadingTrailingCodes = buffer.getBoolean(REMOVE_LEADING_TRAILING_CODES, removeLeadingTrailingCodes);
	}

	@Override
	public String toString() {
		buffer.reset();
		buffer.setBoolean(REMOVE_LEADING_TRAILING_CODES, removeLeadingTrailingCodes);
		return buffer.toString();
	}

	public void setRemoveLeadingTrailingCodes(boolean removeLeadingTrailingCodes) {
		this.removeLeadingTrailingCodes = removeLeadingTrailingCodes;
	}

	public boolean getRemoveLeadingTrailingCodes() {
		return removeLeadingTrailingCodes;
	}

	@Override
	public ParametersDescription getParametersDescription() {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(REMOVE_LEADING_TRAILING_CODES,
			"Remove leading and trailing codes",
			"Removes leading and trailing codes from the source and place them in the skeleton.");
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Inline Codes Simplifier", true, false);		
		desc.addCheckboxPart(paramsDesc.get(REMOVE_LEADING_TRAILING_CODES));
		return desc;
	}
}
