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

package net.sf.okapi.connectors.simpletm;

import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.PathInputPart;

public class ParametersUI implements IEditorDescriptionProvider {

	public EditorDescription createEditorDescription(ParametersDescription paramDesc) {
		EditorDescription desc = new EditorDescription("SimpleTM Connector Settings", true, false);

		PathInputPart part = desc.addPathInputPart(paramDesc.get(Parameters.DBPATH), "Database File", false);
		part.setBrowseFilters(String.format("Database Files (*%s)\tAll Files (*.*)", Parameters.DB_EXTENSION),
			String.format("*%s\t*.*", Parameters.DB_EXTENSION));
		
		desc.addCheckboxPart(paramDesc.get(Parameters.PENALIZESOURCEWITHDIFFERENTCODES));
		desc.addCheckboxPart(paramDesc.get(Parameters.PENALIZETARGETWITHDIFFERENTCODES));
		
		return desc;
	}

}
