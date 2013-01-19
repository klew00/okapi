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
===========================================================================*/

package net.sf.okapi.steps.xliffsplitter;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.CheckboxPart;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.TextInputPart;

@EditorFor(XliffSplitterParameters.class)
public class XliffSplitterParameters extends BaseParameters implements IEditorDescriptionProvider {

	public static final String TRANSLATIONTYPE = "translation_type";
	public static final String TRANSLATIONSTATUS = "translation_status";

	private static final String BIGFILE = "bigFile";
	private static final String FILEMARKER = "fileMarker";

	private static final String UPDATESDLTRANSLATIONSTATUS = "updateSDLTranslationStatus";
	private static final String TRANSLATIONTYPEVALUE = "translationTypeValue";
	private static final String TRANSLATIONSTATUSVALUE = "translationStatusValue";

	private boolean bigFile;
	private String fileMarker;
	private boolean updateSDLTranslationStatus;
	private String translationTypeValue;
	private String translationStatusValue;

	public XliffSplitterParameters() {
		reset();
	}

	public void reset() {
		bigFile = false;
		fileMarker = "_PART";
		updateSDLTranslationStatus = false;
		translationTypeValue = "manual_translation";
		translationStatusValue = "finished";
	}

	public void fromString(String data) {
		reset();
		buffer.fromString(data);
		
		bigFile = buffer.getBoolean(BIGFILE, bigFile);
		fileMarker = buffer.getString(FILEMARKER, fileMarker);
		updateSDLTranslationStatus = buffer.getBoolean(UPDATESDLTRANSLATIONSTATUS,
			updateSDLTranslationStatus);
		translationTypeValue = buffer.getString(TRANSLATIONTYPEVALUE, translationTypeValue);
		translationStatusValue = buffer.getString(TRANSLATIONSTATUSVALUE, translationStatusValue);
	}

	public String toString() {
		buffer.reset();
		buffer.setBoolean(BIGFILE, bigFile);
		buffer.setString(FILEMARKER, fileMarker);
		buffer.setBoolean(UPDATESDLTRANSLATIONSTATUS, updateSDLTranslationStatus);
		buffer.setString(TRANSLATIONTYPEVALUE, translationTypeValue);
		buffer.setString(TRANSLATIONSTATUSVALUE, translationStatusValue);
		return buffer.toString();
	}

	public boolean isBigFile() {
		return bigFile;
	}

	public void setBigFile(boolean bigFile) {
		this.bigFile = bigFile;
	}

	public String getFileMarker() {
		return fileMarker;
	}
	
	public void setFileMarker (String fileMarker) {
		this.fileMarker = fileMarker;
	}
	
	public boolean isUpdateSDLTranslationStatus() {
		return updateSDLTranslationStatus;
	}

	public void setUpdateSDLTranslationStatus(boolean updateSDLTranslationStatus) {
		this.updateSDLTranslationStatus = updateSDLTranslationStatus;
	}
	
	public String getTranslationTypeValue () {
		return translationTypeValue;
	}
	
	public void setTranslationTypeValue (String translationTypeValue) {
		this.translationTypeValue = translationTypeValue;
	}

	public String getTranslationStatusValue () {
		return translationStatusValue;
	}
	
	public void setTranslationStatusValue (String translationStatusValue) {
		this.translationStatusValue = translationStatusValue;
	}

	@Override
	public ParametersDescription getParametersDescription() {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(BIGFILE, "Process big file", null);
		desc.add(FILEMARKER, "File marker", null);
		desc.add(UPDATESDLTRANSLATIONSTATUS, "Update the <iws:status> translation status (WorldServer-specific)", null);
		desc.add(TRANSLATIONTYPEVALUE, String.format("Value for '%s'", TRANSLATIONTYPE),
			String.format("Value to set for the %s attribute.", TRANSLATIONTYPE));
		desc.add(TRANSLATIONSTATUSVALUE, String.format("Value for '%s'", TRANSLATIONSTATUS),
			String.format("Value to set for the %s attribute.", TRANSLATIONSTATUS));
		return desc;
	}
	
	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("XLIFF Splitter", true, false);
		
		CheckboxPart cbp = desc.addCheckboxPart(paramsDesc.get(BIGFILE));
		TextInputPart tip = desc.addTextInputPart(paramsDesc.get(FILEMARKER));
		tip.setVertical(false);
		tip.setMasterPart(cbp, true);

		desc.addSeparatorPart();
		
		cbp = desc.addCheckboxPart(paramsDesc.get(UPDATESDLTRANSLATIONSTATUS));
		
		// translation_type
		tip = desc.addTextInputPart(paramsDesc.get(TRANSLATIONTYPEVALUE));
		tip.setVertical(false);
		tip.setMasterPart(cbp, true);
		
		// translation_status
		tip = desc.addTextInputPart(paramsDesc.get(TRANSLATIONSTATUSVALUE));
		tip.setVertical(false);
		tip.setMasterPart(cbp, true);
		
		return desc;
	}

}