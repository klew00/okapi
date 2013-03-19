/*
 * ===========================================================================
 * Copyright (C) 2013 by the Okapi Framework contributors
 * -----------------------------------------------------------------------------
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
 * ===========================================================================
 */

package net.sf.okapi.steps.languagetool;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	private static final String ENABLEFALSEFRIENDS = "enableFalseFriends";
	private static final String CHECKSOURCE = "checkSource";

	private boolean checkSource;
	private boolean enableFalseFriends;
	
	public Parameters () {
		reset();
	}

	@Override
	public void reset () {
		checkSource = true;
		enableFalseFriends = true;
	}

	@Override
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		checkSource = buffer.getBoolean(CHECKSOURCE, checkSource);
		enableFalseFriends = buffer.getBoolean(ENABLEFALSEFRIENDS, enableFalseFriends);
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setBoolean(CHECKSOURCE, checkSource);
		buffer.setBoolean(ENABLEFALSEFRIENDS, enableFalseFriends);
		return buffer.toString();
	}
	
	public boolean getCheckSource () {
		return checkSource;
	}
	
	public void setCheckSource (boolean chechSource) {
		this.checkSource = chechSource;
	}

	public boolean getEnableFalseFriends () {
		return enableFalseFriends;
	}
	
	public void setEnableFalseFriends (boolean enableFalseFriends) {
		this.enableFalseFriends = enableFalseFriends;
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(CHECKSOURCE, "Check also the source text (in addition to the target)", null);
		desc.add(ENABLEFALSEFRIENDS, "Check for false friends", null);
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramDesc) {
		EditorDescription desc = new EditorDescription("LanguageTool", true, false);
		desc.addCheckboxPart(paramDesc.get(CHECKSOURCE));
		desc.addCheckboxPart(paramDesc.get(ENABLEFALSEFRIENDS));
		return desc;
	}

}
