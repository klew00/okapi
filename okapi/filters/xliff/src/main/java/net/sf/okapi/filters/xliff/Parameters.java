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
============================================================================*/

package net.sf.okapi.filters.xliff;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.encoder.XMLEncoder;
import net.sf.okapi.common.uidescription.CheckboxPart;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.ListSelectionPart;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	public static final int SEGMENTATIONTYPE_ORIGINAL = 0;
	public static final int SEGMENTATIONTYPE_SEGMENTED = 1;
	public static final int SEGMENTATIONTYPE_NOTSEGMENTED = 2;
	public static final int SEGMENTATIONTYPE_ASNEEDED = 3;
	
	public static final int TARGETSTATEMODE_IGNORE = 0;
	public static final int TARGETSTATEMODE_EXTRACT = 1;
	public static final int TARGETSTATEMODE_DONOTEXTRACT = 2;

	public static final String ADDALTTRANS = "addAltTrans";
	public static final String ADDALTTRANSGMODE = "addAltTransGMode";
	
	private static final String USECUSTOMPARSER = "useCustomParser";
	private static final String FACTORYCLASS = "factoryClass";
	private static final String FALLBACKTOID = "fallbackToID";
	private static final String ADDTARGETLANGUAGE = "addTargetLanguage";
	private static final String OVERRIDETARGETLANGUAGE = "overrideTargetLanguage";
	private static final String OUTPUTSEGMENTATIONTYPE = "outputSegmentationType";
	private static final String IGNOREINPUTSEGMENTATION = "ignoreInputSegmentation";
	private static final String INCLUDEEXTENSIONS = "includeExtensions";
	private static final String TARGETSTATEMODE = "targetStateMode";
	private static final String TARGETSTATEVALUE = "targetStateValue";
	
	private boolean useCustomParser;
	private String factoryClass;
	private boolean fallbackToID;
	private boolean escapeGT;
	private boolean addTargetLanguage;
	private boolean overrideTargetLanguage;
	private int outputSegmentationType;
	private boolean ignoreInputSegmentation;
	private boolean addAltTrans;
	private boolean addAltTransGMode;
	private boolean includeExtensions;
	private int targetStateMode;
	private String targetStateValue;
	
	// Write-only parameters
	public boolean quoteModeDefined;
	public int quoteMode;
	
	public Parameters () {
		reset();
		toString(); // fill the list
	}

	public boolean getUseCustomParser() {
		return useCustomParser;
	}

	public void setUseCustomParser(boolean useCustomParser) {
		this.useCustomParser = useCustomParser;
	}

	public String getFactoryClass() {
		return factoryClass;
	}

	public void setFactoryClass(String factoryClass) {
		this.factoryClass = factoryClass;
	}
	
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
	
	public boolean getOverrideTargetLanguage () {
		return overrideTargetLanguage;
	}

	public void setOverrideTargetLanguage (boolean overrideTargetLanguage) {
		this.overrideTargetLanguage = overrideTargetLanguage;
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

	public boolean getAddAltTrans () {
		return this.addAltTrans;
	}
	
	public void setAddAltTrans (boolean addAltTrans) {
		this.addAltTrans = addAltTrans;
	}

	public boolean getAddAltTransGMode () {
		return this.addAltTransGMode;
	}
	
	public void setAddAltTransGMode (boolean addAltTransGMode) {
		this.addAltTransGMode = addAltTransGMode;
	}

	public boolean getIncludeExtensions () {
		return this.includeExtensions;
	}
	
	public void setIncludeExtensions (boolean includeExtensions) {
		this.includeExtensions = includeExtensions;
	}
	
	public int getTargetStateMode () {
		return this.targetStateMode;
	}
	
	public void setTargetStateMode (int targetStateMode) {
		this.targetStateMode = targetStateMode;
	}

	public String getTargetStateValue () {
		return this.targetStateValue;
	}
	
	public void setTargetStateValue (String targetStateValue) {
		this.targetStateValue = targetStateValue;
	}
	
	public boolean getQuoteModeDefined () {
		return quoteModeDefined;
	}

	public int getQuoteMode () {
		return quoteMode;
	}

	public void reset () {
		useCustomParser = false;
		factoryClass = "com.ctc.wstx.stax.WstxInputFactory"; // Woodstox XML parser
		fallbackToID = false;
		escapeGT = false;
		addTargetLanguage = true;
		overrideTargetLanguage = false;
		outputSegmentationType = SEGMENTATIONTYPE_ORIGINAL;
		ignoreInputSegmentation = false;
		addAltTrans = false;
		addAltTransGMode = true;
		includeExtensions = true;
		targetStateMode = TARGETSTATEMODE_IGNORE;
		targetStateValue = "needs-translation";
		
		// Forced write-only default options
		quoteModeDefined = true;
		quoteMode = 0; // no double or single quotes escaped
	}

	public void fromString (String data) {
		reset();		
		buffer.fromString(data);
		useCustomParser = buffer.getBoolean(USECUSTOMPARSER, useCustomParser);
		factoryClass = buffer.getString(FACTORYCLASS, factoryClass);
		fallbackToID = buffer.getBoolean(FALLBACKTOID, fallbackToID);
		escapeGT = buffer.getBoolean(XMLEncoder.ESCAPEGT, escapeGT);
		addTargetLanguage = buffer.getBoolean(ADDTARGETLANGUAGE, addTargetLanguage);
		overrideTargetLanguage = buffer.getBoolean(OVERRIDETARGETLANGUAGE, overrideTargetLanguage);
		outputSegmentationType = buffer.getInteger(OUTPUTSEGMENTATIONTYPE, outputSegmentationType);
		ignoreInputSegmentation = buffer.getBoolean(IGNOREINPUTSEGMENTATION, ignoreInputSegmentation);
		addAltTrans = buffer.getBoolean(ADDALTTRANS, addAltTrans);
		addAltTransGMode = buffer.getBoolean(ADDALTTRANSGMODE, addAltTransGMode);
		includeExtensions = buffer.getBoolean(INCLUDEEXTENSIONS, includeExtensions);
		targetStateMode = buffer.getInteger(TARGETSTATEMODE, targetStateMode);
		targetStateValue = buffer.getString(TARGETSTATEVALUE, targetStateValue);
		
		// Output, not user-defined
		quoteModeDefined = buffer.getBoolean(XMLEncoder.QUOTEMODEDEFINED, quoteModeDefined);
		quoteMode = buffer.getInteger(XMLEncoder.QUOTEMODE, quoteMode);
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setBoolean(USECUSTOMPARSER, useCustomParser);
		buffer.setString(FACTORYCLASS, factoryClass);
		buffer.setBoolean(FALLBACKTOID, fallbackToID);
		buffer.setBoolean(XMLEncoder.ESCAPEGT, escapeGT);
		buffer.setBoolean(ADDTARGETLANGUAGE, addTargetLanguage);
		buffer.setBoolean(OVERRIDETARGETLANGUAGE, overrideTargetLanguage);
		buffer.setInteger(OUTPUTSEGMENTATIONTYPE, outputSegmentationType);
		buffer.setBoolean(IGNOREINPUTSEGMENTATION, ignoreInputSegmentation);
		buffer.setBoolean(ADDALTTRANS, addAltTrans);
		buffer.setBoolean(ADDALTTRANSGMODE, addAltTransGMode);
		buffer.setBoolean(INCLUDEEXTENSIONS, includeExtensions);
		buffer.setInteger(TARGETSTATEMODE, targetStateMode);
		buffer.setString(TARGETSTATEVALUE, targetStateValue);
		buffer.setBoolean(XMLEncoder.QUOTEMODEDEFINED, quoteModeDefined);
		buffer.setInteger(XMLEncoder.QUOTEMODE, quoteMode);
		return buffer.toString();
	}
	
	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(USECUSTOMPARSER, "Use a custom XML stream parser", null);
		desc.add(FACTORYCLASS, "Factory class for the custom XML stream parser", null);
		desc.add(FALLBACKTOID, "Use the trans-unit id attribute for the text unit name if there is no resname", null);
		desc.add(IGNOREINPUTSEGMENTATION, "Ignore the segmentation information in the input", null);
		desc.add(XMLEncoder.ESCAPEGT, "Escape the greater-than characters", null);
		desc.add(ADDTARGETLANGUAGE, "Add the target-language attribute if not present", null);
		desc.add(OVERRIDETARGETLANGUAGE, "Override the target language of the XLIFF document", null);
		desc.add(OUTPUTSEGMENTATIONTYPE, "Type of output segmentation", "Indicates wether to segment or not the text content in output");
		desc.add(ADDALTTRANS, "Allow addition of new <alt-trans> elements", "Indicates wether or not to adding new <alt-trans> elements is allowed");
		desc.add(ADDALTTRANSGMODE, "Use the <g> notation in new <alt-trans> elements", "Indicates wether or not to use the <g> notation in new <alt-trans> elements");
		desc.add(INCLUDEEXTENSIONS, "Include extra information", "If set: non-standard information are included in the added <alt-trans>");
		desc.add(TARGETSTATEMODE, "Action to do when the value of the state attribute matches the specified pattern", null);
		desc.add(TARGETSTATEVALUE, "Pattern for the state attribute value", null);
		return desc;
	}

	public EditorDescription createEditorDescription (ParametersDescription paramDesc) {
		EditorDescription desc = new EditorDescription("XLIFF Filter Parameters", true, false);
		
		desc.addCheckboxPart(paramDesc.get(FALLBACKTOID));
		desc.addCheckboxPart(paramDesc.get(IGNOREINPUTSEGMENTATION));

//Not implemented yet		
//		desc.addTextInputPart(paramDesc.get(TARGETSTATEVALUE));
//		
//		String[] values = {
//			String.valueOf(TARGETSTATEMODE_IGNORE),
//			String.valueOf(TARGETSTATEMODE_EXTRACT),
//			String.valueOf(TARGETSTATEMODE_DONOTEXTRACT)};
//		String[] labels = {
//			"Ignore the state attribute",
//			"Extract the matching entries (and only those entries)",
//			"Do not extract the matching entries",
//		};
//		ListSelectionPart lsp = desc.addListSelectionPart(paramDesc.get(TARGETSTATEMODE), values);
//		lsp.setChoicesLabels(labels);
		
		desc.addSeparatorPart();
		
		desc.addCheckboxPart(paramDesc.get(XMLEncoder.ESCAPEGT));
		desc.addCheckboxPart(paramDesc.get(ADDTARGETLANGUAGE));
		desc.addCheckboxPart(paramDesc.get(OVERRIDETARGETLANGUAGE));

		String[] values2 = {
			String.valueOf(SEGMENTATIONTYPE_ORIGINAL),
			String.valueOf(SEGMENTATIONTYPE_SEGMENTED),
			String.valueOf(SEGMENTATIONTYPE_NOTSEGMENTED),
			String.valueOf(SEGMENTATIONTYPE_ASNEEDED)};
		String[] labels2 = {
			"Show segments only if the input text unit is segmented",
			"Always show segments (even if the input text unit is not segmented)",
			"Never show segments (even if the input text unit is segmented)",
			"Show segments only if the entry is segmented and regardless how the input was",
		};
		ListSelectionPart lsp = desc.addListSelectionPart(paramDesc.get(OUTPUTSEGMENTATIONTYPE), values2);
		lsp.setChoicesLabels(labels2);
		
		CheckboxPart cbp = desc.addCheckboxPart(paramDesc.get(ADDALTTRANS));
		desc.addCheckboxPart(paramDesc.get(INCLUDEEXTENSIONS)).setMasterPart(cbp, true);
		desc.addCheckboxPart(paramDesc.get(ADDALTTRANSGMODE)).setMasterPart(cbp, true);

		desc.addSeparatorPart();
		
		cbp = desc.addCheckboxPart(paramDesc.get(USECUSTOMPARSER));
		desc.addTextInputPart(paramDesc.get(FACTORYCLASS)).setMasterPart(cbp, true);
		
		return desc;
	}

}
