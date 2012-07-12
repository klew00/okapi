/*===========================================================================
  Copyright (C) 2012 by the Okapi Framework contributors
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

package net.sf.okapi.steps.tradosutils;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.CheckboxPart;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.ListSelectionPart;

@EditorFor(ParametersMSWordResaver.class)
public class ParametersMSWordResaver extends BaseParameters implements IEditorDescriptionProvider {

	private static final String FORMAT = "format";
	private static final String SENDNEW = "sendNew";
	private static final String wdFormatDocumentDefault = "16";
	private static final String wdFormatRTF = "6";
	private static final String wdFormatDocument = "0";
	private static final String wdFormatFilteredHTML = "10";
	private static final String wdFormatHTML = "8";

	private int format;
	private boolean sendNew;

	public int getFormat() {
		return format;
	}

	public void setFormat (int format) {
		this.format = format;
	}
	
	public boolean getSendNew () {
		return sendNew;
	}
	
	public void setSendNew (boolean sendNew) {
		this.sendNew = sendNew;
	}
	
	public ParametersMSWordResaver () {
		reset();
	}
	
	public void reset() {
		format = 6;
		sendNew = true;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		format = buffer.getInteger(FORMAT, format);
		sendNew = buffer.getBoolean(SENDNEW, sendNew);
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setBoolean(SENDNEW, sendNew);
		buffer.setInteger(FORMAT, format);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(FORMAT, "Format to save as:", null);
		desc.add(SENDNEW, "Send resaved document to the next step", null);
		return desc;
	}
	
	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramDesc) {
		EditorDescription desc = new EditorDescription("MS Word Resaver", true, false);

		String[] labels = {
				"Rich Text Format (RTF)",
				"Microsoft Office Word Format (DOC)",
				"Word Default Document Format (DOCX for Word 2007)",
				"Filtered HTML Format",
				"Standard HTML Format"
			};
		
		String[] values = {
				wdFormatRTF,
				wdFormatDocument,
				wdFormatDocumentDefault,
				wdFormatFilteredHTML,
				wdFormatHTML
			};
		
		ListSelectionPart lsp = desc.addListSelectionPart(paramDesc.get(FORMAT), values);
		lsp.setChoicesLabels(labels);
		lsp.setListType(ListSelectionPart.LISTTYPE_DROPDOWN);
		lsp.setVertical(false);
		
		CheckboxPart cbp = desc.addCheckboxPart(paramDesc.get(SENDNEW));
		cbp.setVertical(true);
		
		return desc;
	}
	
}
