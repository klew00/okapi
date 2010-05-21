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
============================================================================*/

package net.sf.okapi.filters.xliff;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.encoder.XMLEncoder;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.ListSelectionPart;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	public static final int SEGMENTATIONTYPE_ORIGINAL = 0;
	public static final int SEGMENTATIONTYPE_SEGMENTED = 1;
	public static final int SEGMENTATIONTYPE_NOTSEGMENTED = 2;

	private static final String FALLBACKTOID = "fallbackToID";
	private static final String ADDTARGETLANGUAGE = "addTargetLanguage";
	private static final String OUTPUTSEGMENTATIONTYPE = "outputSegmentationType";
	private static final String IGNOREINPUTSEGMENTATION = "ignoreInputSegmentation";
	
	private boolean fallbackToID;
	private boolean escapeGT;
	private boolean addTargetLanguage;
	private int outputSegmentationType;
	private boolean ignoreInputSegmentation;
	
	// Not used for now
//	private boolean useStateValues;
//	private boolean extractOnlyMatchingValues;
//	private String stateValues;
//	private boolean extractNoState;

	public Parameters () {
		reset();
		toString(); // fill the list
	}
	
//	public boolean getUseStateValues () {
//		return useStateValues;
//	}
//
//	public void setUseStateValues (boolean useStateValues) {
//		this.useStateValues = useStateValues;
//	}
//
//	public boolean getExtractOnlyMatchingValues () {
//		return extractOnlyMatchingValues;
//	}
//
//	public void setExtractOnlyMatchingValues (boolean extractOnlyMatchingValues) {
//		this.extractOnlyMatchingValues = extractOnlyMatchingValues;
//	}
//
//	public String getStateValues () {
//		return stateValues;
//	}
//
//	public void setStateValues (String stateValues) {
//		this.stateValues = stateValues;
//	}
//
//	public boolean getExtractNoState () {
//		return extractNoState;
//	}
//
//	public void setExtractNoState (boolean extractNoState) {
//		this.extractNoState = extractNoState;
//	}

	public boolean getEscapeGT () {
		return escapeGT;
	}

	public void setEscapeGT (boolean escapeGT) {
		this.escapeGT = escapeGT;
	}

	public boolean getFallbackToID() {
		return fallbackToID;
	}

	public void setFallbackToID(boolean fallbackToID) {
		this.fallbackToID = fallbackToID;
	}

	public boolean getAddTargetLanguage () {
		return addTargetLanguage;
	}

	public void setAddTargetLanguage (boolean addTargetLanguage) {
		this.addTargetLanguage = addTargetLanguage;
	}
	
	public int getOutputSegmentationType () {
		return this.outputSegmentationType;
	}
	
	public void setOutputSegmentationType (int segmentationType) {
		this.outputSegmentationType = segmentationType;
	}

	public boolean getIgnoreInputSegmentation () {
		return this.ignoreInputSegmentation;
	}
	
	public void setIgnoreInputSegmentation (boolean ignoreInputSegmentation) {
		this.ignoreInputSegmentation = ignoreInputSegmentation;
	}

	public void reset () {
//		useStateValues = true;
//		stateValues = "new|needs-translation";
//		extractOnlyMatchingValues = true;
//		extractNoState = true;
		fallbackToID = false;
		escapeGT = false;
		addTargetLanguage = true;
		outputSegmentationType = SEGMENTATIONTYPE_ORIGINAL;
		ignoreInputSegmentation = false;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
//		useStateValues = buffer.getBoolean("useStateValues", useStateValues);
//		extractOnlyMatchingValues = buffer.getBoolean("extractOnlyMatchingValues", extractOnlyMatchingValues);
//		stateValues = buffer.getString("stateValues", stateValues);
//		extractNoState = buffer.getBoolean("extractNoState", extractNoState);
		fallbackToID = buffer.getBoolean(FALLBACKTOID, fallbackToID);
		escapeGT = buffer.getBoolean(XMLEncoder.ESCAPEGT, escapeGT);
		addTargetLanguage = buffer.getBoolean(ADDTARGETLANGUAGE, addTargetLanguage);
		outputSegmentationType = buffer.getInteger(OUTPUTSEGMENTATIONTYPE, outputSegmentationType);
		ignoreInputSegmentation = buffer.getBoolean(IGNOREINPUTSEGMENTATION, ignoreInputSegmentation);
	}

	@Override
	public String toString () {
		buffer.reset();
//		buffer.setBoolean("useStateValues", useStateValues);
//		buffer.setBoolean("extractOnlyMatchingValues", extractOnlyMatchingValues);
//		buffer.setString("stateValues", stateValues);
//		buffer.setBoolean("extractNoState", extractNoState);
		buffer.setBoolean(FALLBACKTOID, fallbackToID);
		buffer.setBoolean(XMLEncoder.ESCAPEGT, escapeGT);
		buffer.setBoolean(ADDTARGETLANGUAGE, addTargetLanguage);
		buffer.setInteger(OUTPUTSEGMENTATIONTYPE, outputSegmentationType);
		buffer.setBoolean(IGNOREINPUTSEGMENTATION, ignoreInputSegmentation);
		return buffer.toString();
	}
	
	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(FALLBACKTOID, "Use the trans-unit id attribute for the text unit name if there is no resname", null);
		desc.add(IGNOREINPUTSEGMENTATION, "Ignore the segmentation information in the input", null);
		desc.add(XMLEncoder.ESCAPEGT, "Escape the greater-than characters", null);
		desc.add(ADDTARGETLANGUAGE, "Add the target-language attribute if not present", null);
		desc.add(OUTPUTSEGMENTATIONTYPE, "Type of output segmentation", "Indicates wether to segment or not the text content in output");
		return desc;
	}

	public EditorDescription createEditorDescription (ParametersDescription paramDesc) {
		EditorDescription desc = new EditorDescription("XLIFF Filter Parameters", true, false);
		desc.addCheckboxPart(paramDesc.get(FALLBACKTOID));
		desc.addCheckboxPart(paramDesc.get(IGNOREINPUTSEGMENTATION));

		desc.addCheckboxPart(paramDesc.get(XMLEncoder.ESCAPEGT));
		desc.addCheckboxPart(paramDesc.get(ADDTARGETLANGUAGE));

		String[] values = {
			String.valueOf(SEGMENTATIONTYPE_ORIGINAL),
			String.valueOf(SEGMENTATIONTYPE_SEGMENTED),
			String.valueOf(SEGMENTATIONTYPE_NOTSEGMENTED)};
		String[] labels = {
			"Segment only if the input text unit is segmented",
			"Always segment (even if the input text unit is not segmented)",
			"Never segment (even if the input text unit is segmented)",
		};
		ListSelectionPart lsp = desc.addListSelectionPart(paramDesc.get(OUTPUTSEGMENTATIONTYPE), values);
		lsp.setChoicesLabels(labels);
		
		return desc;
	}

}
