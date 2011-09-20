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
===========================================================================*/

package net.sf.okapi.steps.rainbowkit.xliff;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.ListSelectionPart;
import net.sf.okapi.lib.xliff.Fragment;

public class XLIFF2Options extends BaseParameters implements IEditorDescriptionProvider {

	private static final String INLINESTYLE = "inlineStyle"; //$NON-NLS-1$
	
	private int inlineStyle;

	public XLIFF2Options () {
		reset();
	}
	
	@Override
	public void reset() {
		inlineStyle = Fragment.STYLE_DATAINSIDE;
	}

	@Override
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		inlineStyle = buffer.getInteger(INLINESTYLE, inlineStyle);
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setInteger(INLINESTYLE, inlineStyle);
		return buffer.toString();
	}

	public int getInlineStyle () {
		return inlineStyle;
	}

	public void setInlineStyle (int inlineStyle) {
		this.inlineStyle = inlineStyle;
	}

	@Override
	public ParametersDescription getParametersDescription() {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(INLINESTYLE, "Style to use for the inline codes", null);
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Experimental XLIFF 2.0", true, false);

		String[] values = {
			String.valueOf(Fragment.STYLE_NODATA),
			String.valueOf(Fragment.STYLE_DATAINSIDE),
			String.valueOf(Fragment.STYLE_DATAOUTSIDE)
		};
		String[] labels = {
			"Without original data stored",
			"Original data stored inline",
			"Original data stored outside the content"
		};
		ListSelectionPart lsp = desc.addListSelectionPart(paramsDesc.get(INLINESTYLE), values);
		lsp.setChoicesLabels(labels);
		lsp.setListType(ListSelectionPart.LISTTYPE_SIMPLE);

		return desc;
	}

}
