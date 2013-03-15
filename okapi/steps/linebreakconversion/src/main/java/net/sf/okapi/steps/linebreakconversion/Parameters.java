/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
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

package net.sf.okapi.steps.linebreakconversion;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.ListSelectionPart;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	private static final String LINEBREAK = "lineBreak";

	private String lineBreak;

	public String getLineBreak () {
		return lineBreak;
	}

	public void setLineBreak (String lineBreak) {
		this.lineBreak = lineBreak;
	}

	public Parameters () {
		reset();
	}
	
	public void reset() {
		if ( (lineBreak = System.getProperty("line.separator") ) == null ) {
			lineBreak = Util.LINEBREAK_DOS;
		}
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		lineBreak = buffer.getString(LINEBREAK, lineBreak);
	}

	@Override
	public String toString() {
		buffer.reset();
		buffer.setString(LINEBREAK, lineBreak);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(LINEBREAK, "Convert line-breaks to the following type", "Select the new type of line-break for the output.");
		return desc;
	}
	
	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramDesc) {
		EditorDescription desc = new EditorDescription("Line-Break Conversion", true, false);

		String[] values = {
			Util.LINEBREAK_DOS,
			Util.LINEBREAK_UNIX,
			Util.LINEBREAK_MAC
		};
		String[] labels = {
			"DOS/Windows (Carriage-Return + Line-Feed, \\r\\n, 0x0D+0x0A)",
			"Unix/Linux (Line-Feed, \\n, 0x0A)",
			"Macintosh (Carriage-Return, \\r, 0x0D)"
		};
		ListSelectionPart lsp = desc.addListSelectionPart(paramDesc.get(LINEBREAK), values);
		lsp.setChoicesLabels(labels);
		lsp.setListType(ListSelectionPart.LISTTYPE_SIMPLE);

		return desc;
	}
	
}
