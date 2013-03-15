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

package net.sf.okapi.filters.mosestext;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

@EditorFor(FilterWriterParameters.class)
public class FilterWriterParameters extends BaseParameters implements IEditorDescriptionProvider {

	public static final String NAME = "Moses InlineText Extraction";
	
	private static final String SOURCEANDTARGET = "sourceAndTarget";
	
	private boolean sourceAndTarget;
	
	public FilterWriterParameters () {
		reset();
	}
	
	public boolean getSourceAndTarget () {
		return sourceAndTarget;
	}

	public void setSourceAndTarget (boolean sourceAndTarget) {
		this.sourceAndTarget = sourceAndTarget;
	}

	public void reset() {
		sourceAndTarget = false;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		sourceAndTarget = buffer.getBoolean(SOURCEANDTARGET, sourceAndTarget);
	}

	@Override
	public String toString() {
		buffer.reset();
		buffer.setBoolean(SOURCEANDTARGET, sourceAndTarget);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(SOURCEANDTARGET, "Create two outputs: one for the source and one for the target", null);
		return desc;
	}

	public EditorDescription createEditorDescription (ParametersDescription paramDesc) {
		EditorDescription desc = new EditorDescription(NAME, true, false);

		desc.addCheckboxPart(paramDesc.get(SOURCEANDTARGET));
		
		return desc;
	}

}
