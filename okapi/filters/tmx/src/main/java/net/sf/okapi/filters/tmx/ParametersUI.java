/*===========================================================================
  Copyright (C) 2009-2010 by the Okapi Framework contributors
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

package net.sf.okapi.filters.tmx;

import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.ListSelectionPart;

@EditorFor(Parameters.class)
public class ParametersUI implements IEditorDescriptionProvider {

	public EditorDescription createEditorDescription (ParametersDescription paramDesc) {
		EditorDescription desc = new EditorDescription("TMX Filter Parameters", true, false);
		desc.addCheckboxPart(paramDesc.get("processAllTargets"));
		desc.addCheckboxPart(paramDesc.get("consolidateDpSkeleton"));
		desc.addCheckboxPart(paramDesc.get("exitOnInvalid"));
	
		String[] values = {String.valueOf(TmxFilter.SEGTYPE_SENTENCE),
			String.valueOf(TmxFilter.SEGTYPE_PARA),
			String.valueOf(TmxFilter.SEGTYPE_OR_SENTENCE),
			String.valueOf(TmxFilter.SEGTYPE_OR_PARA)};
		String[] labels = {
			"Always creates the segment (ignore segtype attribute)",
			"Never creates the segment (ignore segtype attribute)",
			"Creates the segment if segtype is 'sentence' or is undefined",
			"Creates the segment only if segtype is 'sentence'"};
		ListSelectionPart lsp = desc.addListSelectionPart(paramDesc.get("segType"), values);
		lsp.setChoicesLabels(labels);
		
		desc.addSeparatorPart();
		
		desc.addCheckboxPart(paramDesc.get("escapeGT"));
		return desc;
	}

}
