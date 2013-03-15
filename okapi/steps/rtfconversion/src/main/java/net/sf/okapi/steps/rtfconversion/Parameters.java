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
============================================================================*/

package net.sf.okapi.steps.rtfconversion;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.ListSelectionPart;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	public static final int LBTYPE_PLATFORM = 0;
	public static final int LBTYPE_DOS = 1;
	public static final int LBTYPE_UNIX = 2;
	public static final int LBTYPE_MAC = 3;
	
	private static final String LINEBREAK = "lineBreak";
	private static final String BOMONUTF8 = "bomOnUTF8";
	private static final String UPDATEENCODING = "updateEncoding";
	
	private String lineBreak;
	private boolean bomOnUTF8;
	private boolean updateEncoding;

	public Parameters () {
		reset();
	}
	
	public void reset() {
		bomOnUTF8 = true;
		if ( (lineBreak = System.getProperty("line.separator") ) == null ) {
			lineBreak = Util.LINEBREAK_DOS;
		}
		updateEncoding = true;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		lineBreak = buffer.getString(LINEBREAK, lineBreak);
		bomOnUTF8 = buffer.getBoolean(BOMONUTF8, bomOnUTF8);
		updateEncoding = buffer.getBoolean(UPDATEENCODING, updateEncoding);
	}

	public String toString() {
		buffer.reset();
		buffer.setString(LINEBREAK, lineBreak);
		buffer.setBoolean(BOMONUTF8, bomOnUTF8);
		buffer.setBoolean(UPDATEENCODING, updateEncoding);
		return buffer.toString();
	}
	
	public String getLineBreak () {
		return lineBreak;
	}
	
	public void setLineBreak (String lineBreak) {
		this.lineBreak = lineBreak;
	}
	
	public boolean getBomOnUTF8 () {
		return bomOnUTF8;
	}
	
	public void setUpdateEncoding (boolean updateEncoding) {
		this.updateEncoding = updateEncoding;
	}

	public boolean getUpdateEncoding () {
		return updateEncoding;
	}
	
	public void setBomOnUTF8 (boolean bomOnUTF8) {
		this.bomOnUTF8 = bomOnUTF8;
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(LINEBREAK, "Type of line-break to use", "Select the type of line-break to use in the output.");		
		desc.add(BOMONUTF8, "Use Byte-Order-Mark for UTF-8 output", null);
		desc.add(UPDATEENCODING, "Try to update the encoding declarations (when detected)", null);
		return desc;
	}
	
	@Override
	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("RTF Conversion", true, false);	

		desc.addCheckboxPart(paramsDesc.get(BOMONUTF8));
		desc.addCheckboxPart(paramsDesc.get(UPDATEENCODING));

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
		ListSelectionPart lsp = desc.addListSelectionPart(paramsDesc.get(LINEBREAK), values);
		lsp.setChoicesLabels(labels);
		lsp.setListType(ListSelectionPart.LISTTYPE_SIMPLE);

		return desc;
	}

}
