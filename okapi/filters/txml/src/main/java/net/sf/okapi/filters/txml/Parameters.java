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
============================================================================*/

package net.sf.okapi.filters.txml;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.encoder.XMLEncoder;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	protected static final String FORCESEGMENTS = "forceSegments";
	
	private boolean forceSegments;
	private boolean escapeGT;
	

	public Parameters () {
		reset();
		toString(); // fill the list
	}
	
	public boolean getForceSegments () {
		return forceSegments;
	}

	public void setForceSegments (boolean forceSegments) {
		this.forceSegments = forceSegments;
	}

	public boolean getEscapeGT () {
		return escapeGT;
	}

	public void setEscapeGT (boolean escapeGT) {
		this.escapeGT = escapeGT;
	}

	public void reset () {
		forceSegments = true;
		escapeGT = false;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		forceSegments = buffer.getBoolean(FORCESEGMENTS, forceSegments);
		escapeGT = buffer.getBoolean(XMLEncoder.ESCAPEGT, escapeGT);
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setBoolean(FORCESEGMENTS, forceSegments);
		buffer.setBoolean(XMLEncoder.ESCAPEGT, escapeGT);
		return buffer.toString();
	}
	
	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(FORCESEGMENTS, "Force un-segmented entries to be output as a segment", null);
		desc.add(XMLEncoder.ESCAPEGT, "Escape the greater-than characters", null);
		return desc;
	}

	public EditorDescription createEditorDescription (ParametersDescription paramDesc) {
		EditorDescription desc = new EditorDescription("TXML Filter Parameters", true, false);
		desc.addCheckboxPart(paramDesc.get(FORCESEGMENTS));
		desc.addCheckboxPart(paramDesc.get(XMLEncoder.ESCAPEGT));
		return desc;
	}

}
