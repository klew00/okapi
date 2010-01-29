/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
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

package net.sf.okapi.steps.xliffkit.writer;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	static final String GMODE = "gMode"; //$NON-NLS-1$
	static final String INCLUDENOTRANSLATE = "includeNoTranslate"; //$NON-NLS-1$
	static final String SETAPPROVEDASNOTRANSLATE = "setApprovedAsNoTranslate"; //$NON-NLS-1$
	static final String MESSAGE = "message"; //$NON-NLS-1$
	
	private boolean gMode;
	private boolean includeNoTranslate;
	private boolean setApprovedAsNoTranslate;
	private String message;
	
	public Parameters () {
		reset();
	}
	
	public void reset() {
		gMode = false;
		includeNoTranslate = true;
		setApprovedAsNoTranslate = false;
		message = "";
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		
		gMode = buffer.getBoolean(GMODE, gMode);
		includeNoTranslate = buffer.getBoolean(INCLUDENOTRANSLATE, includeNoTranslate);
		setApprovedAsNoTranslate = buffer.getBoolean(SETAPPROVEDASNOTRANSLATE, setApprovedAsNoTranslate);
		message = buffer.getString(MESSAGE, message);
		
		// Make sure the we can merge later
		if ( !includeNoTranslate ) {
			setApprovedAsNoTranslate = false;
		}
	}

	public String toString () {
		buffer.reset();
		
		buffer.setParameter(GMODE, gMode);
		buffer.setBoolean(INCLUDENOTRANSLATE, includeNoTranslate);
		buffer.setBoolean(SETAPPROVEDASNOTRANSLATE, setApprovedAsNoTranslate);
		buffer.setParameter(MESSAGE, message);
		
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		
		desc.add(GMODE, "Use <g></g> and <x/> notation", "G-mode");
		desc.add(INCLUDENOTRANSLATE, "Include non-translatable text units", "Include non-translatables");
		desc.add(SETAPPROVEDASNOTRANSLATE, "Set approved entries as non-translatable", "Approved as non-translatable");
		desc.add(MESSAGE, "Description of the XLIFF file", "Description");
		
		return desc;
	}
	
	public EditorDescription createEditorDescription(
			ParametersDescription parametersDescription) {
		EditorDescription desc = new EditorDescription("XLIFF Kit Writer Options", true, false);
		
		desc.addCheckboxPart(parametersDescription.get(GMODE));
		desc.addCheckboxPart(parametersDescription.get(INCLUDENOTRANSLATE));
		desc.addCheckboxPart(parametersDescription.get(SETAPPROVEDASNOTRANSLATE));
		desc.addTextInputPart(parametersDescription.get(MESSAGE));
		
		return desc;
	}

	public boolean isgMode() {
		return gMode;
	}

	public void setgMode(boolean gMode) {
		this.gMode = gMode;
	}

	public boolean isIncludeNoTranslate() {
		return includeNoTranslate;
	}

	public void setIncludeNoTranslate(boolean includeNoTranslate) {
		this.includeNoTranslate = includeNoTranslate;
	}

	public boolean isSetApprovedAsNoTranslate() {
		return setApprovedAsNoTranslate;
	}

	public void setSetApprovedAsNoTranslate(boolean setApprovedAsNoTranslate) {
		this.setApprovedAsNoTranslate = setApprovedAsNoTranslate;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
