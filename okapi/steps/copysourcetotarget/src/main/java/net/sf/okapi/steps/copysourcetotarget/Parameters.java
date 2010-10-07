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

package net.sf.okapi.steps.copysourcetotarget;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {	
	private boolean copyProperties;
	private boolean copyContent;	
	private boolean overwriteExisting;

	public Parameters() {
		reset();
	}

	public void reset() {
		copyProperties = true;
		copyContent = true;
		overwriteExisting = false;
	}

	public void fromString(String data) {
		reset();
		// Read the file content as a set of fields
		copyProperties = buffer.getBoolean("copyProperties", copyProperties);
		copyContent = buffer.getBoolean("copyContent", copyContent);
		overwriteExisting = buffer.getBoolean("overwriteExisting", overwriteExisting);
	}

	public String toString() {
		buffer.reset();		
		buffer.setBoolean("copyProperties", copyProperties);
		buffer.setBoolean("copyContent", copyContent);
		buffer.setBoolean("overwriteExisting", overwriteExisting);
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

	@Override
	public ParametersDescription getParametersDescription() {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add("copyProperties", "Copy Source Properties to Target?", "Copy Source Properties to Target?");
		desc.add("copyContent", "Copy Source Content to Target?", "Copy Source Content to Target?");
		desc.add("overwriteExisting", "Overwrite the current target content?", "Overwrite the current target content");
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Copy Source Content To Target", true, false);		
		desc.addCheckboxPart(paramsDesc.get("copyProperties"));
		desc.addCheckboxPart(paramsDesc.get("copyContent"));
		desc.addSeparatorPart();
		desc.addCheckboxPart(paramsDesc.get("overwriteExisting"));
		return desc;
	}
}
