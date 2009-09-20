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

package net.sf.okapi.steps.formatconversion;

import net.sf.okapi.common.BaseParameters;

public class TableFilterWriterParameters extends BaseParameters {

	public static final String INLINE_ORIGINAL = "original";
	public static final String INLINE_TMX = "tmx";
	public static final String INLINE_XLIFF = "xliff";
	public static final String INLINE_XLIFF_GX = "xliff_gx";
	public static final String INLINE_GENERIC = "generic";
	
	static final String INLINEFORMAT = "inlineFormat";
	static final String USEDOUBLEQUOTES = "useDoubleQuotes";
	static final String SEPARATOR = "separator";
	
	private String inlineFormat;
	private boolean useDoubleQuotes;
	private String separator;
	
	public TableFilterWriterParameters () {
		reset();
	}

	public String getInlineFormat () {
		return inlineFormat;
	}

	public void setInlineFormat (String inlineFormat) {
		this.inlineFormat = inlineFormat;
	}

	public boolean getUseDoubleQuotes () {
		return useDoubleQuotes;
	}

	public void setUseDoubleQuotes (boolean useDoubleQuotes) {
		this.useDoubleQuotes = useDoubleQuotes;
	}

	public String getSeparator () {
		return separator;
	}

	public void setSeparator (String separator) {
		this.separator = separator;
	}

	public void reset () {
		inlineFormat = INLINE_ORIGINAL;
		useDoubleQuotes = false;
		separator = "\t";
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		inlineFormat = buffer.getString(INLINEFORMAT, inlineFormat);
		useDoubleQuotes = buffer.getBoolean(USEDOUBLEQUOTES, useDoubleQuotes);
		separator = buffer.getString(SEPARATOR, separator);
	}

	public String toString() {
		buffer.reset();
		buffer.setString(INLINEFORMAT, inlineFormat);
		buffer.setBoolean(USEDOUBLEQUOTES, useDoubleQuotes);
		buffer.setString(SEPARATOR, separator);
		return buffer.toString();
	}

//	@Override
//	public ParametersDescription getParametersDescription () {
//		ParametersDescription desc = new ParametersDescription(this);
//		desc.add(INLINEFORMAT, "Inline codes format", "Format of the inline codes");
//		return desc;
//	}
//
//	public EditorDescription createEditorDescription(ParametersDescription paramDesc) {
//		EditorDescription desc = new EditorDescription("Format Conversion", true, false);
//
//		String[] choices = {FORMAT_PO, FORMAT_TMX, FORMAT_TABLE, FORMAT_PENSIEVE};
//		String[] choicesLabels = {"PO File", "TMX Document", "Tab-Delimited Table", "Pensieve TM"};
//		ListSelectionPart lsp = desc.addListSelectionPart(paramDesc.get(OUTPUTFORMAT), choices);
//		lsp.setChoicesLabels(choicesLabels);
//		
//		return desc;
//	}

}
