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

package net.sf.okapi.steps.translationcomparison;

import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.CheckboxPart;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.PathInputPart;
import net.sf.okapi.common.uidescription.TextInputPart;

@EditorFor(Parameters.class)
public class ParametersUI implements IEditorDescriptionProvider {

	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Translation Comparison", true, false);
		
		//TODO: "HTML Output" group
		CheckboxPart cbpHTML = desc.addCheckboxPart(paramsDesc.get("generateHTML"));
		CheckboxPart cbp2 = desc.addCheckboxPart(paramsDesc.get("autoOpen"));
		cbp2.setMasterPart(cbpHTML, true);
		
		//TODO: "TMX Output" group
		CheckboxPart cbpTMX = desc.addCheckboxPart(paramsDesc.get("generateTMX"));
		PathInputPart pip = desc.addPathInputPart(paramsDesc.get("tmxPath"), "TMX Document", true);
		pip.setBrowseFilters("TMX Documents (*.tmx)\tAll Files (*.*)", "*.tmx\t*.*");
		pip.setMasterPart(cbpTMX, true);
		pip.setWithLabel(false);
		TextInputPart tip = desc.addTextInputPart(paramsDesc.get(Parameters.TARGET2SUFFIX));
		tip.setMasterPart(cbpTMX, true);
		tip = desc.addTextInputPart(paramsDesc.get(Parameters.TARGET3SUFFIX));
		tip.setMasterPart(cbpTMX, true);
		
		// HTML group
		tip = desc.addTextInputPart(paramsDesc.get(Parameters.DOCUMENT1LABEL));
		tip.setMasterPart(cbpHTML, true);
		tip.setVertical(false);
		tip = desc.addTextInputPart(paramsDesc.get(Parameters.DOCUMENT2LABEL));
		tip.setMasterPart(cbpHTML, true);
		tip.setVertical(false);
		tip = desc.addTextInputPart(paramsDesc.get(Parameters.DOCUMENT3LABEL));
		tip.setMasterPart(cbpHTML, true);
		tip.setVertical(false);
		
		//TODO: "Comparison Options" group
		desc.addCheckboxPart(paramsDesc.get("caseSensitive"));
		desc.addCheckboxPart(paramsDesc.get("whitespaceSensitive"));
		desc.addCheckboxPart(paramsDesc.get("punctuationSensitive"));
		
		return desc;
	}

}
