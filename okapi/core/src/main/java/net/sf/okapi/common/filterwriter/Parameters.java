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

package net.sf.okapi.common.filterwriter;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {	
	private static final String WRITE_ALL_PROPERTIES_AS_ATTRIBUTES = "writeAllPropertiesAsAttributes";
		
	private boolean writeAllPropertiesAsAttributes;
	
	public Parameters() {
		reset();
	}
	
	public void reset() {
		writeAllPropertiesAsAttributes = false;
	}

	@Override
	public void fromString(String data) {
		reset();
		buffer.fromString(data);
		// Read the file content as a set of fields		
		writeAllPropertiesAsAttributes = buffer.getBoolean(WRITE_ALL_PROPERTIES_AS_ATTRIBUTES, writeAllPropertiesAsAttributes);
	}

	@Override
	public String toString() {
		buffer.reset();		
		buffer.setBoolean(WRITE_ALL_PROPERTIES_AS_ATTRIBUTES, writeAllPropertiesAsAttributes);
		return buffer.toString();
	}

	public boolean isWriteAllPropertiesAsAttributes() {
		return writeAllPropertiesAsAttributes;
	}
	
	public void setWriteAllPropertiesAsAttributes(boolean writeAllPropertiesAsAttributes) {
		this.writeAllPropertiesAsAttributes = writeAllPropertiesAsAttributes;
	}
	
	@Override
	public ParametersDescription getParametersDescription() {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(WRITE_ALL_PROPERTIES_AS_ATTRIBUTES, 
				"Write all text unit level properties as TMX attributes", 
				null);

		return desc;
	}

	@Override
	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("TMX Filter Writer Options", true, false);
		desc.addCheckboxPart(paramsDesc.get(WRITE_ALL_PROPERTIES_AS_ATTRIBUTES));
		return desc;
	}
}
