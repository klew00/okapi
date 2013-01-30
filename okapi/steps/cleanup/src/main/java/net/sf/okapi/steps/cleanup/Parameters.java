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

package net.sf.okapi.steps.cleanup;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.TextInputPart;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	private static final String NORMALIZEQUOTES = "normalizeQuotes";
	private static final String CHECKCHARACTERS = "checkCharacters";
	private static final String MATCHREGEXEXPRESSIONS = "matchRegexExpressions";
	private static final String MATCHUSERREGEX = "matchUserRegex";
	private static final String USERREGEX = "userRegex";
	private static final String PRUNETEXTUNIT = "pruneTextUnit";

	private boolean normalizeQuotes;
	private boolean checkCharacters;
	private boolean matchRegexExpressions;
	private boolean matchUserRegex;
	private String userRegex;
	private boolean pruneTextUnit;

	public Parameters() {

		reset();
	}

	public void reset() {

		this.normalizeQuotes = true;
		this.checkCharacters = true;
		this.matchRegexExpressions = true;
		this.matchUserRegex = true;
		this.userRegex = null;
		this.pruneTextUnit = true;
	}

	public void fromString(String data) {

		reset();
		buffer.fromString(data);
		normalizeQuotes = buffer.getBoolean(NORMALIZEQUOTES, normalizeQuotes);
		checkCharacters = buffer.getBoolean(CHECKCHARACTERS, checkCharacters);
		matchRegexExpressions = buffer.getBoolean(MATCHREGEXEXPRESSIONS, matchRegexExpressions);
		matchUserRegex = buffer.getBoolean(MATCHUSERREGEX, matchUserRegex);
		userRegex = buffer.getString(USERREGEX, userRegex);
		pruneTextUnit = buffer.getBoolean(PRUNETEXTUNIT, pruneTextUnit);
	}

	public String toString() {

		buffer.reset();
		buffer.setBoolean(NORMALIZEQUOTES, normalizeQuotes);
		buffer.setBoolean(CHECKCHARACTERS, checkCharacters);
		buffer.setBoolean(MATCHREGEXEXPRESSIONS, matchRegexExpressions);
		buffer.setBoolean(MATCHUSERREGEX, matchUserRegex);
		buffer.setString(USERREGEX, userRegex);
		buffer.setBoolean(PRUNETEXTUNIT, pruneTextUnit);
		
		return buffer.toString();
	}

	public boolean getNormalizeQuotes() {

		return normalizeQuotes;
	}

	public void setNormalizeQuotes(boolean normalizeQuotes) {

		this.normalizeQuotes = normalizeQuotes;
	}

	public boolean getCheckCharacters() {

		return checkCharacters;
	}

	public void setCheckCharacters(boolean checkCharacters) {

		this.checkCharacters = checkCharacters;
	}

	public boolean getMatchRegexExpressions() {

		return matchRegexExpressions;
	}

	public void setMatchRegexExpressions(boolean matchRegexExpressions) {

		this.matchRegexExpressions = matchRegexExpressions;
	}

	public boolean getMatchUserRegex() {

		return matchUserRegex;
	}

	public void setMatchUserRegex(boolean matchUserRegex) {

		this.matchUserRegex = matchUserRegex;
	}

	public String getUserRegex() {

		return userRegex;
	}

	public void setUserRegex(String userRegex) {

		this.userRegex = userRegex;
	}

	public boolean getPruneTextUnit() {

		return pruneTextUnit;
	}

	public void setPruneTextUnit(boolean pruneTextUnit) {

		this.pruneTextUnit = pruneTextUnit;
	}

	@Override
	public ParametersDescription getParametersDescription() {

		ParametersDescription desc = new ParametersDescription(this);
		
		desc.add(NORMALIZEQUOTES, "Normalize quotation marks", null);
		desc.add(CHECKCHARACTERS, "Check for corrupt or unexpected characters", null);
		desc.add(MATCHREGEXEXPRESSIONS, "Mark segments matching default regular expressions for removal", null);
		desc.add(MATCHUSERREGEX, "Mark segments matching user defined regular expressions for removal", null);
		desc.add(USERREGEX, "User defined regex string", null);
		desc.add(PRUNETEXTUNIT, "Remove unnecessary segments from text unit", null);
		
		return desc;
	}

	public EditorDescription createEditorDescription(ParametersDescription paramDesc) {

		EditorDescription desc = new EditorDescription("Cleanup", true, false);

		desc.addCheckboxPart(paramDesc.get(NORMALIZEQUOTES));
		desc.addCheckboxPart(paramDesc.get(MATCHREGEXEXPRESSIONS));
		desc.addCheckboxPart(paramDesc.get(MATCHUSERREGEX));
		desc.addCheckboxPart(paramDesc.get(CHECKCHARACTERS));
		TextInputPart tip = desc.addTextInputPart(paramDesc.get(USERREGEX));
		tip.setAllowEmpty(true);
		desc.addCheckboxPart(paramDesc.get(PRUNETEXTUNIT));

		return desc;
	}

}
