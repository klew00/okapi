/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.xliffkit.reader;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	static final String GENERATE_TARGETS = "generateTargets"; //$NON-NLS-1$
	
	private boolean generateTargets; 
	
	public Parameters () {
		reset();
	}
	
	@Override
	public void fromString(String data) {
		reset();
		buffer.fromString(data);
		
		generateTargets = buffer.getBoolean(GENERATE_TARGETS, generateTargets);
	}

	@Override
	public String toString () {
		buffer.reset();		
		buffer.setParameter(GENERATE_TARGETS, generateTargets);		
		return buffer.toString();
	}
	
	@Override
	public void reset() {
		generateTargets = true;
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);		
		desc.add(GENERATE_TARGETS, "Generate target files in the output directory", "Generate targets");		
		return desc;
	}
	
	@Override
	public EditorDescription createEditorDescription(
			ParametersDescription parametersDescription) {
		EditorDescription desc = new EditorDescription("XLIFF Kit Reader Options", true, false);		
		desc.addCheckboxPart(parametersDescription.get(GENERATE_TARGETS));		
		return desc;
	}

	public void setGenerateTargets(boolean generateTargets) {
		this.generateTargets = generateTargets;
	}

	public boolean isGenerateTargets() {
		return generateTargets;
	}

}
