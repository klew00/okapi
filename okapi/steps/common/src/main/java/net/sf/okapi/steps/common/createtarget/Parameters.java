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

package net.sf.okapi.steps.common.createtarget;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	private static final String COPYCONTENT = "copyContent";
	private static final String COPYPROPERTIES = "copyProperties";
	private static final String OVERWRITEEXISTING = "overwriteExisting";
	private static final String CREATEONNONTRANSLATABLE = "createOnNonTranslatable";
	
	private boolean copyProperties;
	private boolean copyContent;	
	private boolean overwriteExisting;
	private boolean createOnNonTranslatable; 
	
	public Parameters() {
		reset();
	}

	public void reset() {
		copyProperties = true;
		copyContent = true;
		overwriteExisting = false;
		createOnNonTranslatable = true;
	}

	public void fromString(String data) {
		reset();
		buffer.fromString(data);		
		// Read the file content as a set of fields
		copyProperties = buffer.getBoolean(COPYPROPERTIES, copyProperties);
		copyContent = buffer.getBoolean(COPYCONTENT, copyContent);
		overwriteExisting = buffer.getBoolean(OVERWRITEEXISTING, overwriteExisting);
		createOnNonTranslatable = buffer.getBoolean(CREATEONNONTRANSLATABLE, createOnNonTranslatable);
	}

	public String toString() {
		buffer.reset();		
		buffer.setBoolean(COPYPROPERTIES, copyProperties);
		buffer.setBoolean(COPYCONTENT, copyContent);
		buffer.setBoolean(OVERWRITEEXISTING, overwriteExisting);
		buffer.setBoolean(CREATEONNONTRANSLATABLE, createOnNonTranslatable);
		return buffer.toString();
	}

	public boolean isCopyProperties() {
		return copyProperties;
	}

	public void setCopyProperties(boolean copyProperties) {
		this.copyProperties = copyProperties;
	}

	public boolean isCopyContent() {
		return copyContent;
	}

	public void setCopyContent(boolean copyContent) {
		this.copyContent = copyContent;
	}

	public boolean isOverwriteExisting() {
		return overwriteExisting;
	}

	public void setOverwriteExisting(boolean overwriteExisting) {
		this.overwriteExisting = overwriteExisting;
	}

	public boolean isCreateOnNonTranslatable() {
		return createOnNonTranslatable;
	}

	public void setCreateOnNonTranslatable(boolean createOnNonTranslatable) {
		this.createOnNonTranslatable = createOnNonTranslatable;
	}

	@Override
	public ParametersDescription getParametersDescription() {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(COPYCONTENT, "Copy the source content to the target", null);
		desc.add(COPYPROPERTIES, "Copy the source properties to the target", null);
		desc.add(OVERWRITEEXISTING, "Overwrite the current target content", null);
		desc.add(CREATEONNONTRANSLATABLE, "Creates target for non-translatable text units", null);
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Create Target", true, false);		
		desc.addCheckboxPart(paramsDesc.get(COPYCONTENT));
		desc.addCheckboxPart(paramsDesc.get(COPYPROPERTIES));
		desc.addSeparatorPart();
		desc.addCheckboxPart(paramsDesc.get(OVERWRITEEXISTING));
		desc.addSeparatorPart();
		desc.addCheckboxPart(paramsDesc.get(CREATEONNONTRANSLATABLE));
		return desc;
	}
}
