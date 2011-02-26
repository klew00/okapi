/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package net.sf.okapi.filters.archive;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	/**
	 * MIME type of the filter's container format
	 */
	private String mimeType;
	
	/**
	 * Comma-delimited list of extensions in the same order as configIds. If the container contains a file with 
	 * one of the extensions, the corresponding config Id is looked up in the fileExtensions string,
	 * and the container's filter instantiates a sub-filter to process that internal file.  
	 */
	private String fileExtensions;

	/**
	 * Comma-delimited list of configuration Ids corresponding to the extension
	 */
	private String configIds;
	
	public Parameters () {
		reset();
		toString(); // fill the list
	}
	
	@Override
	public void reset() {
		mimeType = ArchiveFilter.MIME_TYPE;
		fileExtensions = "";
		configIds = "";
	}

	@Override
	public void fromString(String data) {
		reset();
		buffer.fromString(data);
		mimeType = buffer.getString("mimeType", mimeType);
		fileExtensions = buffer.getString("fileExtensions", fileExtensions);
		configIds = buffer.getString("configIds", configIds);
	}

	@Override
	public String toString() {
		buffer.reset();
		buffer.setString("mimeType", mimeType);
		buffer.setString("fileExtensions", fileExtensions);
		buffer.setString("configIds", configIds);
		return buffer.toString();
	}
		
	public void setFileExtensions(String fileExtensions) {
		this.fileExtensions = fileExtensions;
	}

	public String getFileExtensions() {
		return fileExtensions;
	}

	public void setConfigIds(String configIds) {
		this.configIds = configIds;
	}

	public String getConfigIds() {
		return configIds;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getMimeType() {
		return mimeType;
	}

	@Override
	public ParametersDescription getParametersDescription() {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add("mimeType", "MIME type of the filter's container format", null);
		desc.add("fileExtensions", "Extensions:", "Comma-delimited list of extensions in the same order as configIds");
		desc.add("configIds", "Config Ids:", "Comma-delimited list of configuration Ids corresponding to the extension");
		return desc;
	}
	
	@Override
	public EditorDescription createEditorDescription(
			ParametersDescription parametersDescription) {
		EditorDescription desc = new EditorDescription("Archive Filter Parameters", true, false);
		
		desc.addTextInputPart(parametersDescription.get("mimeType"));
		desc.addTextInputPart(parametersDescription.get("fileExtensions"));
		desc.addTextInputPart(parametersDescription.get("configIds"));
		
		return desc;
	}
}