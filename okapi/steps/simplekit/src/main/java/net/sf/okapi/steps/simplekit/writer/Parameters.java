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

package net.sf.okapi.steps.simplekit.writer;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.CheckboxPart;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	static final String PLACEHOLDERMODE = "placeholderMode"; //$NON-NLS-1$
	static final String INCLUDENOTRANSLATE = "includeNoTranslate"; //$NON-NLS-1$
	static final String SETAPPROVEDASNOTRANSLATE = "setApprovedAsNoTranslate"; //$NON-NLS-1$
	static final String COPYSOURCE = "copySource"; //$NON-NLS-1$ 
	static final String PACKAGENAME = "packageName"; //$NON-NLS-1$
	static final String PACKAGEDIRECTORY = "packageDirectory"; //$NON-NLS-1$ 
	static final String MESSAGE = "message"; //$NON-NLS-1$
	static final String USEMANIFEST = "useManifest"; //$NON-NLS-1$
	
	private boolean placeholderMode;
	private boolean includeNoTranslate;
	private boolean setApprovedAsNoTranslate;
	private boolean copySource;
	private String packageName;
	private String packageDirectory;
	private String message;
	private boolean useManifest;

	public Parameters () {
		reset();
	}
	
	@Override
	public void reset() {
		placeholderMode = false;
		includeNoTranslate = true;
		setApprovedAsNoTranslate = false;
		copySource = true;
		packageName = "pack1";
		packageDirectory = "${rootDir}";
		// Internal
		message = "";
		useManifest = true;
	}

	@Override
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		
		placeholderMode = buffer.getBoolean(PLACEHOLDERMODE, placeholderMode);
		includeNoTranslate = buffer.getBoolean(INCLUDENOTRANSLATE, includeNoTranslate);
		setApprovedAsNoTranslate = buffer.getBoolean(SETAPPROVEDASNOTRANSLATE, setApprovedAsNoTranslate);
		copySource = buffer.getBoolean(COPYSOURCE, copySource);
		packageName = buffer.getString(PACKAGENAME, packageName);
		packageDirectory = buffer.getString(PACKAGEDIRECTORY, packageDirectory);
		// Make sure the we can merge later
		if ( !includeNoTranslate ) {
			setApprovedAsNoTranslate = false;
		}

		// Internal
		message = buffer.getString(MESSAGE, message);
		useManifest = buffer.getBoolean(USEMANIFEST, useManifest);
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setParameter(PLACEHOLDERMODE, placeholderMode);
		buffer.setBoolean(INCLUDENOTRANSLATE, includeNoTranslate);
		buffer.setBoolean(SETAPPROVEDASNOTRANSLATE, setApprovedAsNoTranslate);
		buffer.setBoolean(COPYSOURCE, copySource);
		buffer.setParameter(PACKAGENAME, packageName);
		buffer.setParameter(PACKAGEDIRECTORY, packageDirectory);
		// Internal
		buffer.setParameter(MESSAGE, message);
		buffer.setParameter(USEMANIFEST, useManifest);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(PLACEHOLDERMODE, "Use the <g></g> and <x/> notation", null);
		desc.add(INCLUDENOTRANSLATE, "Include non-translatable text units", null);
		desc.add(SETAPPROVEDASNOTRANSLATE, "Set approved entries as non-translatable", null);
		desc.add(COPYSOURCE, "Copy source text in target if no target is available", null);
		desc.add(PACKAGENAME, "Short name of the package", null);
		desc.add(PACKAGEDIRECTORY, "Directory where to place the package", "Directory location");
		return desc;
		//Note: MESSAGE is for internal use, and not exposed
		//Note: USEMANIFEST is for internal use, and not exposed
	}
	
	public EditorDescription createEditorDescription (ParametersDescription parametersDescription) {
		EditorDescription desc = new EditorDescription("Generic XLIFF Translation Package Options", true, false);
		desc.addCheckboxPart(parametersDescription.get(PLACEHOLDERMODE));
		CheckboxPart cbp1 = desc.addCheckboxPart(parametersDescription.get(INCLUDENOTRANSLATE));
		CheckboxPart cbp2 = desc.addCheckboxPart(parametersDescription.get(SETAPPROVEDASNOTRANSLATE));
		cbp2.setMasterPart(cbp1, true);
		desc.addCheckboxPart(parametersDescription.get(COPYSOURCE));
		desc.addTextInputPart(parametersDescription.get(PACKAGENAME));
		desc.addTextInputPart(parametersDescription.get(PACKAGEDIRECTORY));
		return desc;
		//Note: MESSAGE is for internal use, and not exposed
		//Note: USEMANIFEST is for internal use, and not exposed
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

	public boolean getSetApprovedAsNoTranslate() {
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

	public String getMessage () {
		return message;
	}

	public String getPackageName () {
		return packageName;
	}

	public void setPackageName (String packageName) {
		this.packageName = packageName;
	}

	public String getPackageDirectory () {
		return packageDirectory;
	}

	public void setPackageDirectory (String packageDirectory) {
		this.packageDirectory = packageDirectory;
	}
	
	public void setMessage (String message) {
		this.message = message;
	}
	
	public void setUseManifest (boolean useManifest) {
		this.useManifest = useManifest;
	}

	public boolean getUseManifest () {
		return useManifest;
	}

}
