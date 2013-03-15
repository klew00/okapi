/*===========================================================================
  Copyright (C) 2008-2011 by the Okapi Framework contributors
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

package net.sf.okapi.filters.ttx;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.encoder.XMLEncoder;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.ListSelectionPart;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	public static final int MODE_AUTO = 0;
	public static final int MODE_EXISTINGSEGMENTS = 1;
	public static final int MODE_ALL = 2;
	
	private final static String SEGMENTMODE = "segmentMode";
	
	private boolean escapeGT;
	private int segmentMode;

	public Parameters () {
		reset();
		toString(); // Fill the list
	}
	
	public boolean getEscapeGT () {
		return escapeGT;
	}

	public void setEscapeGT (boolean escapeGT) {
		this.escapeGT = escapeGT;
	}
	
	public int getSegmentMode () {
		return segmentMode;
	}
	
	public void setSegmentMode (int segmentMode) {
		this.segmentMode = segmentMode;
	}

	public void reset () {
		escapeGT = false;
		segmentMode = 0;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		escapeGT = buffer.getBoolean(XMLEncoder.ESCAPEGT, escapeGT);
		segmentMode = buffer.getInteger(SEGMENTMODE, segmentMode);
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setBoolean(XMLEncoder.ESCAPEGT, escapeGT);
		buffer.setInteger(SEGMENTMODE, segmentMode);
		return buffer.toString();
	}
	
	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(XMLEncoder.ESCAPEGT, "Escape the greater-than characters in output", null);
		desc.add(SEGMENTMODE, "Extraction mode", null);
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramDesc) {
		EditorDescription desc = new EditorDescription("TTX Filter Parameters", true, false);
		
		String[] values = {String.valueOf(MODE_AUTO),
			String.valueOf(MODE_EXISTINGSEGMENTS),
			String.valueOf(MODE_ALL)};
		String[] labels = {
			"Auto-detect existing segments (If found: extract only those, otherwise extract all)",
			"Extract only existing segments",
			"Extract all (existing segments and un-segmented text parts)",
		};
		ListSelectionPart lsp = desc.addListSelectionPart(paramDesc.get(SEGMENTMODE), values);
		lsp.setChoicesLabels(labels);
		
		desc.addCheckboxPart(paramDesc.get(XMLEncoder.ESCAPEGT));
		return desc;
	}

}
