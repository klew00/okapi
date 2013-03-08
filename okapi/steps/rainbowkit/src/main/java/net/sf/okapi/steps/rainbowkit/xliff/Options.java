/*===========================================================================
  Copyright (C) 2008-2011 by the Okapi Framework contributors
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

package net.sf.okapi.steps.rainbowkit.xliff;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.CheckboxPart;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

public class Options extends BaseParameters implements IEditorDescriptionProvider {

	private static final String PLACEHOLDERMODE = "placeholderMode"; //$NON-NLS-1$
	private static final String INCLUDENOTRANSLATE = "includeNoTranslate"; //$NON-NLS-1$ 
	private static final String SETAPPROVEDASNOTRANSLATE = "setApprovedAsNoTranslate"; //$NON-NLS-1$
	private static final String COPYSOURCE = "copySource"; //$NON-NLS-1$
	private static final String INCLUDEALTTRANS = "includeAltTrans"; //$NON-NLS-1$
	private static final String INCLUDECODEATTRS = "includeCodeAttrs"; //$NON-NLS-1$
	
	private boolean placeholderMode;
	private boolean includeNoTranslate;
	private boolean setApprovedAsNoTranslate;
	private boolean copySource;
	private boolean includeAltTrans;
	private boolean includeCodeAttrs;

	public Options () {
		reset();
	}
	
	@Override
	public void reset() {
		placeholderMode = true;
		includeNoTranslate = true;
		setApprovedAsNoTranslate = false;
		copySource = true;
		includeAltTrans = true;
		includeCodeAttrs = false;
	}

	@Override
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		placeholderMode = buffer.getBoolean(PLACEHOLDERMODE, placeholderMode);
		includeNoTranslate = buffer.getBoolean(INCLUDENOTRANSLATE, includeNoTranslate);
		setApprovedAsNoTranslate = buffer.getBoolean(SETAPPROVEDASNOTRANSLATE, setApprovedAsNoTranslate);
		copySource = buffer.getBoolean(COPYSOURCE, copySource);
		includeAltTrans = buffer.getBoolean(INCLUDEALTTRANS, includeAltTrans);
		includeCodeAttrs = buffer.getBoolean(INCLUDECODEATTRS, includeCodeAttrs);
		
		// Make sure the we can merge later
		if ( !includeNoTranslate ) {
			setApprovedAsNoTranslate = false;
		}
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setParameter(PLACEHOLDERMODE, placeholderMode);
		buffer.setBoolean(INCLUDENOTRANSLATE, includeNoTranslate);
		buffer.setBoolean(SETAPPROVEDASNOTRANSLATE, setApprovedAsNoTranslate);
		buffer.setBoolean(COPYSOURCE, copySource);
		buffer.setBoolean(INCLUDEALTTRANS, includeAltTrans);
		buffer.setBoolean(INCLUDECODEATTRS, includeCodeAttrs);
		return buffer.toString();
	}

	public boolean getPlaceholderMode () {
		return placeholderMode;
	}

	public void setPlaceholderMode (boolean placeholderMode) {
		this.placeholderMode = placeholderMode;
	}
	
	public boolean getIncludeNoTranslate () {
		return includeNoTranslate;
	}

	public void setIncludeNoTranslate (boolean includeNoTranslate) {
		this.includeNoTranslate = includeNoTranslate;
	}
	
	public boolean getSetApprovedAsNoTranslate () {
		return setApprovedAsNoTranslate;
	}

	public void setSetApprovedAsNoTranslate (boolean setApprovedAsNoTranslate) {
		this.setApprovedAsNoTranslate = setApprovedAsNoTranslate;
	}

	public boolean getCopySource () {
		return copySource;
	}
	
	public void setCopySource (boolean copySource) {
		this.copySource = copySource;
	}

	public boolean getIncludeAltTrans () {
		return includeAltTrans;
	}

	public void setIncludeAltTrans (boolean includeAltTrans) {
		this.includeAltTrans = includeAltTrans;
	}

	public boolean getIncludeCodeAttrs () {
		return includeCodeAttrs;
	}

	public void setIncludeCodeAttrs (boolean includeCodeAttrs) {
		this.includeCodeAttrs = includeCodeAttrs;
	}

	@Override
	public ParametersDescription getParametersDescription() {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(PLACEHOLDERMODE, "Use <g></g> and <x/> notation", null);
		desc.add(INCLUDENOTRANSLATE, "Include non-translatable text units", null);
		desc.add(SETAPPROVEDASNOTRANSLATE, "Set approved entries as non-translatable", null);
		desc.add(COPYSOURCE, "Copy source text in target if no target is available", null);
		desc.add(INCLUDEALTTRANS, "Include <alt-trans> elements", null);
		desc.add(INCLUDECODEATTRS, "Include extended code attributes", null);
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Generic XLIFF Package", true, false);
		CheckboxPart cbp1 = desc.addCheckboxPart(paramsDesc.get(INCLUDENOTRANSLATE));
		CheckboxPart cbp2 = desc.addCheckboxPart(paramsDesc.get(SETAPPROVEDASNOTRANSLATE));
		cbp2.setMasterPart(cbp1, true);
		desc.addCheckboxPart(paramsDesc.get(PLACEHOLDERMODE));
		desc.addCheckboxPart(paramsDesc.get(COPYSOURCE));
		desc.addCheckboxPart(paramsDesc.get(INCLUDEALTTRANS));
		desc.addCheckboxPart(paramsDesc.get(INCLUDECODEATTRS));
		return desc;
	}

}
