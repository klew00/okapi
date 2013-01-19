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

package net.sf.okapi.filters.php;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.filters.InlineCodeFinder;
import net.sf.okapi.common.uidescription.CheckboxPart;
import net.sf.okapi.common.uidescription.CodeFinderPart;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {
	
	static final String USECODEFINDER = "useCodeFinder";
	static final String CODEFINDERRULES = "codeFinderRules";
	static final String USEDIRECTIVES = "useDirectives";
	static final String EXTRACTOUTSIDEDIRECTIVES = "extractOutsideDirectives";

	private boolean useCodeFinder;
	private InlineCodeFinder codeFinder;
	private boolean useDirectives;
	private boolean extractOutsideDirectives;

	public Parameters () {
		codeFinder = new InlineCodeFinder();
		reset();
		toString(); // fill the list
	}
	
	public InlineCodeFinder getCodeFinder () {
		return codeFinder;
	}

	public boolean getUseCodeFinder () {
		return useCodeFinder;
	}

	public void setUseCodeFinder (boolean useCodeFinder) {
		this.useCodeFinder = useCodeFinder;
	}

	public String getCodeFinderRules () {
		return codeFinder.toString();
	}

	public void setCodeFinderRules (String codeFinderRules) {
		codeFinder.fromString(codeFinderRules);
	}

	public boolean getUseDirectives () {
		return useDirectives;
	}
	
	public void setUseDirectives (boolean useDirectives) {
		this.useDirectives = useDirectives;
	}
	
	public boolean getExtractOutsideDirectives () {
		return extractOutsideDirectives;
	}
	
	public void setExtractOutsideDirectives (boolean extractOutsideDirectives) {
		this.extractOutsideDirectives = extractOutsideDirectives;
	}

	public void reset () {
		useDirectives = true;
		extractOutsideDirectives = true;
		
		useCodeFinder = true;
		codeFinder.reset();
		codeFinder.setSample("... attr='val'> text <br/> text \\n text <a att='val'> text [VAR1] text\n{VAR2} text <a att='val' ...");
		codeFinder.setUseAllRulesWhenTesting(true);

		// HTML-like tags (including without start or end)
		codeFinder.addRule("(\\A[^<]*?>)|(<[\\w!?/].*?(>|\\Z))");
		// Basic escaped characters
		codeFinder.addRule("\\\\a|\\\\b|\\\\f|\\\\n|\\\\r|\\\\t|\\\\v");
		// Email address
		codeFinder.addRule("(\\w[-._\\w]*\\w@\\w[-._\\w]*\\w\\.\\w{2,3})");
		// [var] and {var} variables
		codeFinder.addRule("[\\[{][\\w_$]+?[}\\]]");
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setBoolean(USEDIRECTIVES, useDirectives);
		buffer.setBoolean(EXTRACTOUTSIDEDIRECTIVES, extractOutsideDirectives);
		buffer.setBoolean(USECODEFINDER, useCodeFinder);
		buffer.setGroup(CODEFINDERRULES, codeFinder.toString());
		return buffer.toString();
	}
	
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		useDirectives = buffer.getBoolean(USEDIRECTIVES, useDirectives);
		extractOutsideDirectives = buffer.getBoolean(EXTRACTOUTSIDEDIRECTIVES, extractOutsideDirectives);
		useCodeFinder = buffer.getBoolean(USECODEFINDER, useCodeFinder);
		codeFinder.fromString(buffer.getGroup(CODEFINDERRULES, ""));
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(USEDIRECTIVES, "Use localization directives", null);
		desc.add(EXTRACTOUTSIDEDIRECTIVES, "Extract outside the scope of the directives", null);
		desc.add(USECODEFINDER, "Has inline codes as defined below:", null);
		desc.add(CODEFINDERRULES, null, "Rules for inline codes");
		return desc;
	}

	public EditorDescription createEditorDescription (ParametersDescription paramDesc) {
		EditorDescription desc = new EditorDescription("PHP Filter Parameters", true, false);

		CheckboxPart cbp1 = desc.addCheckboxPart(paramDesc.get(USEDIRECTIVES));
		CheckboxPart cbp2 = desc.addCheckboxPart(paramDesc.get(EXTRACTOUTSIDEDIRECTIVES));
		cbp2.setMasterPart(cbp1, true);

		cbp1 = desc.addCheckboxPart(paramDesc.get(Parameters.USECODEFINDER));
		CodeFinderPart cfp = desc.addCodeFinderPart(paramDesc.get(Parameters.CODEFINDERRULES));
		cfp.setMasterPart(cbp1, true);
		
		return desc;
	}
	
}
