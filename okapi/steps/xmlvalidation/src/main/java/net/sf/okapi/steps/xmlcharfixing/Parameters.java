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
============================================================================*/

package net.sf.okapi.steps.xmlcharfixing;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.TextInputPart;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	private static String REPLACEMENT = "replacement";

	private String replacement;
	
	public Parameters () {
		reset();
	}
	
	public String getReplacement () {
		return replacement;
	}

	public void setReplacement (String replacement) {
		this.replacement = replacement;
	}

	public void reset () {
		replacement = "_#x%X;";
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		replacement = buffer.getString(REPLACEMENT, replacement);
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setString(REPLACEMENT, replacement);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(REPLACEMENT, "Replacement string", "Enter a Java-formatted replacement string.");
		return desc;
	}
	
	@Override
	public EditorDescription createEditorDescription(ParametersDescription paramDesc) {
		EditorDescription desc = new EditorDescription("XML Characters Fixing", true, false);
		TextInputPart tip = desc.addTextInputPart(paramDesc.get(REPLACEMENT));
		tip.setAllowEmpty(true);
		return desc;
	}

}
