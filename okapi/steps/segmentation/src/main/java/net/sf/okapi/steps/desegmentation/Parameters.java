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

package net.sf.okapi.steps.desegmentation;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {
	
	private static final String DESEGMENTSOURCE = "desegmentSource";
	private static final String DESEGMENTTARGET = "desegmentTarget";
	
	private boolean desegmentSource;
	private boolean desegmentTarget;
	
	public Parameters () {
		reset();
	}
	
	public boolean getDesegmentSource () {
		return desegmentSource;
	}

	public void setDesegmentSource (boolean desegmentSource) {
		this.desegmentSource = desegmentSource;
	}

	public boolean getDesegmentTarget () {
		return desegmentTarget;
	}

	public void setDesegmentTarget (boolean desegmentTarget) {
		this.desegmentTarget = desegmentTarget;
	}

	@Override
	public void reset () {
		desegmentSource = true;
		desegmentTarget = true;
	}

	@Override
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		desegmentSource = buffer.getBoolean(DESEGMENTSOURCE, desegmentSource);
		desegmentTarget = buffer.getBoolean(DESEGMENTTARGET, desegmentTarget);
	}

	@Override
	public String toString() {
		buffer.reset();
		buffer.setBoolean(DESEGMENTSOURCE, desegmentSource);
		buffer.setBoolean(DESEGMENTTARGET, desegmentTarget);
		return buffer.toString();
	}
	
	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(DESEGMENTSOURCE, "Join all segments of the source text", null);
		desc.add(DESEGMENTTARGET, "Join all segments of the target text", null);
		return desc;
	}
	
	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramDesc) {
		EditorDescription desc = new EditorDescription("Desegmentation", true, false);
		desc.addCheckboxPart(paramDesc.get(DESEGMENTSOURCE));
		desc.addCheckboxPart(paramDesc.get(DESEGMENTTARGET));
		return desc;
	}

}
