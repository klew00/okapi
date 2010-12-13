/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.moses;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.CheckboxPart;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

@EditorFor(MergingParameters.class)
public class MergingParameters extends BaseParameters implements IEditorDescriptionProvider {

	private static final String COPYTOTARGET = "copyToTarget";
	private static final String OVERWRITEEXISINGTARGET = "overwriteExistingTarget";
	private static final String FORCEALTTRANSOUTPUT = "forceAltTransOutput";
	
	private boolean copyToTarget;
	private boolean overwriteExistingTarget;
	private boolean forceAltTransOutput;
	
	public MergingParameters () {
		reset();
	}
	
	public boolean getCopyToTarget () {
		return copyToTarget;
	}

	public void setCopyToTarget (boolean copyToTarget) {
		this.copyToTarget = copyToTarget;
	}

	public boolean getOverwriteExistingTarget () {
		return overwriteExistingTarget;
	}

	public void setOverwriteExistingTarget (boolean overwriteExistingTarget) {
		this.overwriteExistingTarget = overwriteExistingTarget;
	}

	public boolean getForceAltTransOutput () {
		return forceAltTransOutput;
	}

	public void setForceAltTransOutput (boolean forceAltTransOutput) {
		this.forceAltTransOutput = forceAltTransOutput;
	}

	public void reset() {
		copyToTarget = false;
		overwriteExistingTarget = false;
		forceAltTransOutput = true;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		copyToTarget = buffer.getBoolean(COPYTOTARGET, copyToTarget);
		overwriteExistingTarget = buffer.getBoolean(OVERWRITEEXISINGTARGET, overwriteExistingTarget);
		forceAltTransOutput = buffer.getBoolean(FORCEALTTRANSOUTPUT, forceAltTransOutput);
	}

	@Override
	public String toString() {
		buffer.reset();
		buffer.setBoolean(COPYTOTARGET, copyToTarget);
		buffer.setBoolean(OVERWRITEEXISINGTARGET, overwriteExistingTarget);
		buffer.setBoolean(FORCEALTTRANSOUTPUT, forceAltTransOutput);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(COPYTOTARGET, "Copy the leveraged translation into the target", null);
		desc.add(OVERWRITEEXISINGTARGET, "Overwrite any existing target text", null);
		desc.add(FORCEALTTRANSOUTPUT, "In XLIFF, force the new <alt-trans> in the output", null);
		return desc;
	}

	public EditorDescription createEditorDescription (ParametersDescription paramDesc) {
		EditorDescription desc = new EditorDescription("Moses InlineText Leveraging", true, false);

		CheckboxPart cbp1 = desc.addCheckboxPart(paramDesc.get(COPYTOTARGET));
		CheckboxPart cbp2 = desc.addCheckboxPart(paramDesc.get(OVERWRITEEXISINGTARGET));
		cbp2.setMasterPart(cbp1, true);
		
		desc.addCheckboxPart(paramDesc.get(FORCEALTTRANSOUTPUT));
		
		return desc;
	}

}
