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
===========================================================================*/

package net.sf.okapi.steps.paraaligner;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	private static final String OUTPOUT_ONE_TO_ONE_MATCHES_ONLY = "outputOneToneMatchesOnly";	
	private boolean outputOneTOneMatchesOnly;

	public Parameters() {
		reset();
	}

	public boolean isOutputOneTOneMatchesOnly() {
		return outputOneTOneMatchesOnly;
	}

	public void setOutputOneTOneMatchesOnly(boolean outputOneTOneMatchesOnly) {
		this.outputOneTOneMatchesOnly = outputOneTOneMatchesOnly;
	}
	
	@Override
	public void reset() {
		outputOneTOneMatchesOnly = true;
	}

	@Override
	public void fromString(String data) {
		reset();
		buffer.fromString(data);
		outputOneTOneMatchesOnly = buffer.getBoolean(OUTPOUT_ONE_TO_ONE_MATCHES_ONLY, outputOneTOneMatchesOnly);
	}

	@Override
	public String toString() {
		buffer.reset();
		buffer.setBoolean(OUTPOUT_ONE_TO_ONE_MATCHES_ONLY, outputOneTOneMatchesOnly);		
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription() {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(OUTPOUT_ONE_TO_ONE_MATCHES_ONLY, "Output 1-1 Matches Only?", 
			"Ouput only 1-1 aligned paragraphs?");
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Paragraph Aligner", true, false);		
		desc.addCheckboxPart(paramsDesc.get(OUTPOUT_ONE_TO_ONE_MATCHES_ONLY));
		return desc;
	}
}
