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

package net.sf.okapi.steps.codesremoval;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

public class Parameters extends BaseParameters implements IEditorDescriptionProvider {
	
	private static final String STRIPSOURCE = "stripSource";
	private static final String STRIPTARGET = "stripTarget";
	
	private boolean stripSource;
	private boolean stripTarget;
	
	public Parameters () {
		reset();
	}
	
	public boolean getStripSource () {
		return stripSource;
	}
	
	public void setStripSource (boolean stripSource) {
		this.stripSource = stripSource;
	}

	public boolean getStripTarget () {
		return stripTarget;
	}
	
	public void setStripTarget (boolean stripTarget) {
		this.stripTarget = stripTarget;
	}

	public void reset() {
		stripSource = true;
		stripTarget = true;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		stripSource = buffer.getBoolean(STRIPSOURCE, stripSource);
		stripTarget = buffer.getBoolean(STRIPTARGET, stripTarget);
	}

	@Override
	public String toString() {
		buffer.reset();
		buffer.setBoolean(STRIPSOURCE, stripSource);
		buffer.setBoolean(STRIPTARGET, stripTarget);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(STRIPSOURCE, "Strip codes in the source text", null);
		desc.add(STRIPTARGET, "Strip codes in the target text", null);
		return desc;
	}
	
	public EditorDescription createEditorDescription (ParametersDescription paramDesc) {
		EditorDescription desc = new EditorDescription("Codes Removal");

		desc.addCheckboxPart(paramDesc.get(STRIPSOURCE));
		desc.addCheckboxPart(paramDesc.get(STRIPTARGET));

		return desc;
	}

}
