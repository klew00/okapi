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
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

@EditorFor(XLIFFKitWriterStep.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	static final String GMODE = "gMode"; //$NON-NLS-1$
	static final String INCLUDE_NO_TRANSLATE = "includeNoTranslate"; //$NON-NLS-1$
	static final String SET_APPROVED_AS_NO_TRANSLATE = "setApprovedAsNoTranslate"; //$NON-NLS-1$
	static final String MESSAGE = "message"; //$NON-NLS-1$
	static final String OUTPUT_URI = "outputURI"; //$NON-NLS-1$
	static final String INCLUDE_SOURCE = "includeSource"; //$NON-NLS-1$
	static final String INCLUDE_ORIGINAL = "includeOriginal"; //$NON-NLS-1$
	
	private boolean gMode;
	private boolean includeNoTranslate;
	private boolean setApprovedAsNoTranslate;
	private String message;
	private String outputURI;
	private boolean includeSource;
	private boolean includeOriginal;
	
	public Parameters () {
		reset();
	}
	
	public void reset() {
		gMode = false;
		includeNoTranslate = true;
		setApprovedAsNoTranslate = false;
		message = "";
		outputURI = "";
		includeSource = true;
		includeOriginal = false;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		
		gMode = buffer.getBoolean(GMODE, gMode);
		includeNoTranslate = buffer.getBoolean(INCLUDE_NO_TRANSLATE, includeNoTranslate);
		setApprovedAsNoTranslate = buffer.getBoolean(SET_APPROVED_AS_NO_TRANSLATE, setApprovedAsNoTranslate);
		message = buffer.getString(MESSAGE, message);
		outputURI = buffer.getString(OUTPUT_URI, outputURI);
		includeSource = buffer.getBoolean(INCLUDE_SOURCE, includeSource);
		includeOriginal = buffer.getBoolean(INCLUDE_ORIGINAL, includeOriginal);
		
		// Make sure the we can merge later
		if ( !includeNoTranslate ) {
			setApprovedAsNoTranslate = false;
		}
	}

	public String toString () {
		buffer.reset();
		
		buffer.setParameter(GMODE, gMode);
		buffer.setBoolean(INCLUDE_NO_TRANSLATE, includeNoTranslate);
		buffer.setBoolean(SET_APPROVED_AS_NO_TRANSLATE, setApprovedAsNoTranslate);
		buffer.setParameter(MESSAGE, message);
		buffer.setParameter(OUTPUT_URI, outputURI);
		buffer.setParameter(INCLUDE_SOURCE, includeSource);
		buffer.setParameter(INCLUDE_ORIGINAL, includeOriginal);
		
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		
		desc.add(GMODE, "Use <g></g> and <x/> notation", "G-mode");
		desc.add(INCLUDE_NO_TRANSLATE, "Include non-translatable text units", "Include non-translatables");
		desc.add(SET_APPROVED_AS_NO_TRANSLATE, "Set approved entries as non-translatable", "Approved as non-translatable");
		desc.add(MESSAGE, "Description of the XLIFF file", "Description");
		desc.add(OUTPUT_URI, "Directory of the T-kit file", "T-kit Path");
		desc.add(INCLUDE_SOURCE, "Include source files in the T-kit file", "Include source");
		desc.add(INCLUDE_ORIGINAL, "Include original files in the T-kit file", "Include originals");
		
		return desc;
	}
	
	public EditorDescription createEditorDescription(
			ParametersDescription parametersDescription) {
		EditorDescription desc = new EditorDescription("XLIFF Kit Writer Options", true, false);
		
		desc.addCheckboxPart(parametersDescription.get(GMODE));
		desc.addCheckboxPart(parametersDescription.get(INCLUDE_NO_TRANSLATE));
		desc.addCheckboxPart(parametersDescription.get(SET_APPROVED_AS_NO_TRANSLATE));
		desc.addTextInputPart(parametersDescription.get(MESSAGE));
		desc.addTextInputPart(parametersDescription.get(OUTPUT_URI));
		desc.addCheckboxPart(parametersDescription.get(INCLUDE_SOURCE));
		desc.addCheckboxPart(parametersDescription.get(INCLUDE_ORIGINAL));
		
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

	public String getOutputURI() {
		return outputURI;
	}

	public void setOutputURI(String outputURI) {
		this.outputURI = outputURI;
	}

	public boolean isIncludeSource() {
		return includeSource;
	}

	public void setIncludeSource(boolean includeSource) {
		this.includeSource = includeSource;
	}

	public boolean isIncludeOriginal() {
		return includeOriginal;
	}

	public void setIncludeOriginal(boolean includeOriginal) {
		this.includeOriginal = includeOriginal;
	}	
}
