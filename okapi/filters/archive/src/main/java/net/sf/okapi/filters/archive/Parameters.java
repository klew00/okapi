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
	 * Comma-delimited list of file names (masks with ? and * wildcards are allowed). Elements of the list correspond to elements in configIds. 
	 * If the container includes a file which name fits one of the masks or a filename, the corresponding config Id is looked up in 
	 * the fileExtensions string, and the container's filter instantiates a sub-filter to process that internal file.
	 * <p> If the container includes several files with the same name located in different internal ZIP folders, all those files will be processed;
	 * if you want to process only some of them, prefix those file names with path info.
	 * <p> If fileNames is empty, then no contained files are processed, and all content is sent as document part events.  
	 * <p> Examples of fileNames:
	 * <p> document.xml, styles.xml, *notes.xml, word/fontTable.xml, word/theme/theme?.xml  
	 */
	private String fileNames;

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
		fileNames = "";
		configIds = "";
	}

	@Override
	public void fromString(String data) {
		reset();
		buffer.fromString(data);
		mimeType = buffer.getString("mimeType", mimeType);
		fileNames = buffer.getString("fileNames", fileNames);
		configIds = buffer.getString("configIds", configIds);
	}

	@Override
	public String toString() {
		buffer.reset();
		buffer.setString("mimeType", mimeType);
		buffer.setString("fileNames", fileNames);
		buffer.setString("configIds", configIds);
		return buffer.toString();
	}
		
	public void setFileNames(String fileNames) {
		this.fileNames = fileNames;
	}

	public String getFileNames() {
		return fileNames;
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
		desc.add("fileNames", "File names", "Comma-delimited list of file names to be processed (wildcards are allowed) in the same order as configuration ids");
		desc.add("configIds", "Filter configuration ids", "Comma-delimited list of configuration ids corresponding to the file names");
		return desc;
	}
	
	@Override
	public EditorDescription createEditorDescription(
			ParametersDescription parametersDescription) {
		EditorDescription desc = new EditorDescription("Archive Filter Parameters", true, false);
		
		desc.addTextInputPart(parametersDescription.get("mimeType"));
		desc.addTextInputPart(parametersDescription.get("fileNames"));
		desc.addTextInputPart(parametersDescription.get("configIds"));
		
		return desc;
	}
}