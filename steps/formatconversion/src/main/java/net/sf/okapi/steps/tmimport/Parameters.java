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

package net.sf.okapi.steps.tmimport;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	private static final String TMDIRECTORY = "tmDirectory";
	
	private String tmDirectory;
	
	public Parameters () {
		reset();
	}

	public String getTmDirectory () {
		return tmDirectory;
	}

	public void setTmDirectory (String tmDirectory) {
		this.tmDirectory = tmDirectory;
	}

	@Override
	public void reset () {
		tmDirectory = "";
	}

	@Override
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		tmDirectory = buffer.getString(TMDIRECTORY, tmDirectory);
	}

	@Override
	public String toString() {
		buffer.reset();
		buffer.setString(TMDIRECTORY, tmDirectory);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(TMDIRECTORY, "Directory of the TM where to import",
			"Full path of directory of the TM where to import");
		return desc;
	}

	public EditorDescription createEditorDescription(ParametersDescription paramDesc) {
		EditorDescription desc = new EditorDescription("TM Import", true, false);
		desc.addFolderInputPart(paramDesc.get(TMDIRECTORY), "TM Directory");
		return desc;
	}

}
