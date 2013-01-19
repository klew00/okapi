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

package net.sf.okapi.steps.rainbowkit.omegat;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

public class Options extends BaseParameters implements IEditorDescriptionProvider {

	private static final String ALLOWSEGMENTATION = "allowSegmentation"; //$NON-NLS-1$
	
	private boolean allowSegmentation;

	public Options () {
		reset();
	}
	
	@Override
	public void reset() {
		allowSegmentation = true;
	}

	@Override
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		allowSegmentation = buffer.getBoolean(ALLOWSEGMENTATION, allowSegmentation);
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setParameter(ALLOWSEGMENTATION, allowSegmentation);
		return buffer.toString();
	}

	public boolean getAllowSegmentation () {
		return allowSegmentation;
	}

	public void setAllowSegmentation (boolean allowSegmentation) {
		this.allowSegmentation = allowSegmentation;
	}
	
	@Override
	public ParametersDescription getParametersDescription() {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(ALLOWSEGMENTATION, "Allow segmentation in the OmegaT project",
			"Allow or not segmentation in the project. Ignored if there is a segmentation step.");
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("OmegaT Project", true, false);
		desc.addCheckboxPart(paramsDesc.get(ALLOWSEGMENTATION));
		return desc;
	}

}
