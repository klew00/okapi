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

package net.sf.okapi.filters.transtable;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	private static final String ALLOWSEGMENTS = "allowSegments";

	private boolean allowSegments;

	public Parameters () {
		reset();
		toString(); // Fill the list
	}
	
	public boolean getAllowSegments () {
		return allowSegments;
	}
	
	public void setAllowSegments (boolean allowSegments) {
		this.allowSegments = allowSegments;
	}

	public void reset () {
		allowSegments = true;
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setBoolean(ALLOWSEGMENTS, allowSegments);
		return buffer.toString();
	}
	
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		allowSegments = buffer.getBoolean(ALLOWSEGMENTS, allowSegments);
	}

	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramDesc) {
		EditorDescription desc = new EditorDescription("Translation Table Parameters", true, false);
		desc.addCheckboxPart(paramDesc.get(ALLOWSEGMENTS));
		return desc;
	}
	
	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(ALLOWSEGMENTS, "Allow segmentation (one row per segment)", null);
		return desc;
	}

}
