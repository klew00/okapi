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

package net.sf.okapi.steps.scopingreport;

import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.ParametersString;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.PathInputPart;
import net.sf.okapi.lib.extra.AbstractParameters;

@EditorFor(Parameters.class)
public class Parameters extends AbstractParameters implements IEditorDescriptionProvider {

	private static final String PROJECT_NAME = "projectName";
	private static final String CUSTOM_TEMPLATE_URI = "customTemplateURI";
	private static final String OUTPUT_PATH = "outputPath";
	private static final String EMPTY_URI = "";
	
	private static final String TRANSLATE_GMX_PROTECTED = "translateGMXProtected";
	private static final String TRANSLATE_GMX_EXACT_MATCHED = "translateGMXExactMatched";
	private static final String TRANSLATE_GMX_LEVERAGED_MATCHED = "translateGMXLeveragedMatched";
	private static final String TRANSLATE_GMX_REPETITION_MATCHED = "translateGMXRepetitionMatched";
	private static final String TRANSLATE_GMX_FUZZY_MATCHED = "translateGMXFuzzyMatch";
	private static final String TRANSLATE_GMX_ALPHANUMERIC_ONLY_TEXT_UNIT = "translateGMXAlphanumericOnlyTextUnit";
	private static final String TRANSLATE_GMX_NUMERIC_ONLY_TEXT_UNIT = "translateGMXNumericOnlyTextUnit";
	private static final String TRANSLATE_GMX_MEASUREMENT_ONLY_TEXT_UNIT = "translateGMXMeasurementOnlyTextUnit";
	
	private static final String TRANSLATE_EXACT_UNIQUE_ID = "translateExactUniqueIdMatch";
	private static final String TRANSLATE_EXACT_PREVIOUS_VERSION = "translateExactPreviousVersionMatch";
	private static final String TRANSLATE_EXACT_LOCAL_CONTEXT = "translateExactLocalContextMatch";
	private static final String TRANSLATE_EXACT_DOCUMENT_CONTEXT = "translateExactDocumentContextMatch";
	private static final String TRANSLATE_EXACT_STRUCTURAL = "translateExactStructuralMatch";
	private static final String TRANSLATE_EXACT = "translateExactMatch";
	private static final String TRANSLATE_EXACT_TEXT_ONLY_PREVIOUS_VERSION = "translateExactTextOnlyPreviousVersionMatch";
	private static final String TRANSLATE_EXACT_TEXT_ONLY_UNIQUE_ID = "translateExactTextOnlyUniqueIdMatch";
	private static final String TRANSLATE_EXACT_TEXT_ONLY = "translateExactTextOnly";
	private static final String TRANSLATE_EXACT_REPAIRED = "translateExactRepaired";
	private static final String TRANSLATE_FUZZY_PREVIOUS_VERSION = "translateFuzzyPreviousVersionMatch";
	private static final String TRANSLATE_FUZZY_UNIQUE_ID = "translateFuzzyUniqueIdMatch";
	private static final String TRANSLATE_FUZZY = "translateFuzzyMatch";
	private static final String TRANSLATE_FUZZY_REPAIRED = "translateFuzzyRepaired";
	private static final String TRANSLATE_PHRASE_ASSEMBLED = "translatePhraseAssembled";
	private static final String TRANSLATE_MT = "translateMT";
	private static final String TRANSLATE_CONCORDANCE = "translateConcordance";

	private String projectName;
	private String outputPath;
	private String customTemplateURI;
	
	private boolean translateGMXProtected;
	private boolean translateGMXExactMatched;
	private boolean translateGMXLeveragedMatched;
	private boolean translateGMXRepetitionMatched;
	private boolean translateGMXFuzzyMatch;
	private boolean translateGMXAlphanumericOnlyTextUnit;
	private boolean translateGMXNumericOnlyTextUnit;
	private boolean translateGMXMeasurementOnlyTextUnit;
	
	private boolean translateExactUniqueIdMatch;
	private boolean translateExactPreviousVersionMatch;
	private boolean translateExactLocalContextMatch;
	private boolean translateExactDocumentContextMatch;
	private boolean translateExactStructuralMatch;
	private boolean translateExactMatch;
	private boolean translateExactTextOnlyPreviousVersionMatch;
	private boolean translateExactTextOnlyUniqueIdMatch;
	private boolean translateExactTextOnly;
	private boolean translateExactRepaired;
	private boolean translateFuzzyPreviousVersionMatch;
	private boolean translateFuzzyUniqueIdMatch;
	private boolean translateFuzzyMatch;
	private boolean translateFuzzyRepaired;
	private boolean translatePhraseAssembled;
	private boolean translateMT;
	private boolean translateConcordance;
	
	public String getCustomTemplateURI() {
		return customTemplateURI;
	}

	public void setCustomTemplateURI(String customTemplateURI) {
		this.customTemplateURI = customTemplateURI;
	}

	@Override
	protected void parameters_load(ParametersString buffer) {
		parameters_reset();
		projectName = buffer.getString(PROJECT_NAME, projectName);
		//customTemplateURI = Util.toURI(buffer.getString(CUSTOM_TEMPLATE_URI, EMPTY_URI));
		customTemplateURI = buffer.getString(CUSTOM_TEMPLATE_URI, customTemplateURI);
		outputPath = buffer.getString(OUTPUT_PATH, outputPath);
		
		translateGMXProtected = buffer.getBoolean(TRANSLATE_GMX_PROTECTED, translateGMXProtected);
		translateGMXExactMatched = buffer.getBoolean(TRANSLATE_GMX_EXACT_MATCHED, translateGMXExactMatched);
		translateGMXLeveragedMatched = buffer.getBoolean(TRANSLATE_GMX_LEVERAGED_MATCHED, translateGMXLeveragedMatched);
		translateGMXRepetitionMatched = buffer.getBoolean(TRANSLATE_GMX_REPETITION_MATCHED, translateGMXRepetitionMatched);
		translateGMXFuzzyMatch = buffer.getBoolean(TRANSLATE_GMX_FUZZY_MATCHED, translateGMXFuzzyMatch);
		translateGMXAlphanumericOnlyTextUnit = buffer.getBoolean(TRANSLATE_GMX_ALPHANUMERIC_ONLY_TEXT_UNIT, translateGMXAlphanumericOnlyTextUnit);
		translateGMXNumericOnlyTextUnit = buffer.getBoolean(TRANSLATE_GMX_NUMERIC_ONLY_TEXT_UNIT, translateGMXNumericOnlyTextUnit);
		translateGMXMeasurementOnlyTextUnit = buffer.getBoolean(TRANSLATE_GMX_MEASUREMENT_ONLY_TEXT_UNIT, translateGMXMeasurementOnlyTextUnit);
		
		translateExactUniqueIdMatch = buffer.getBoolean(TRANSLATE_EXACT_UNIQUE_ID, translateExactUniqueIdMatch);
		translateExactPreviousVersionMatch = buffer.getBoolean(TRANSLATE_EXACT_PREVIOUS_VERSION, translateExactPreviousVersionMatch);
		translateExactLocalContextMatch = buffer.getBoolean(TRANSLATE_EXACT_LOCAL_CONTEXT, translateExactLocalContextMatch);
		translateExactDocumentContextMatch = buffer.getBoolean(TRANSLATE_EXACT_DOCUMENT_CONTEXT, translateExactDocumentContextMatch);
		translateExactStructuralMatch = buffer.getBoolean(TRANSLATE_EXACT_STRUCTURAL, translateExactStructuralMatch);
		translateExactMatch = buffer.getBoolean(TRANSLATE_EXACT, translateExactMatch);
		translateExactTextOnlyPreviousVersionMatch = buffer.getBoolean(TRANSLATE_EXACT_TEXT_ONLY_PREVIOUS_VERSION, translateExactTextOnlyPreviousVersionMatch);
		translateExactTextOnlyUniqueIdMatch = buffer.getBoolean(TRANSLATE_EXACT_TEXT_ONLY_UNIQUE_ID, translateExactTextOnlyUniqueIdMatch);
		translateExactTextOnly = buffer.getBoolean(TRANSLATE_EXACT_TEXT_ONLY, translateExactTextOnly);
		translateExactRepaired = buffer.getBoolean(TRANSLATE_EXACT_REPAIRED, translateExactRepaired);
		translateFuzzyPreviousVersionMatch = buffer.getBoolean(TRANSLATE_FUZZY_PREVIOUS_VERSION, translateFuzzyPreviousVersionMatch);
		translateFuzzyUniqueIdMatch = buffer.getBoolean(TRANSLATE_FUZZY_UNIQUE_ID, translateFuzzyUniqueIdMatch);
		translateFuzzyMatch = buffer.getBoolean(TRANSLATE_FUZZY, translateFuzzyMatch);
		translateFuzzyRepaired = buffer.getBoolean(TRANSLATE_FUZZY_REPAIRED, translateFuzzyRepaired);
		translatePhraseAssembled = buffer.getBoolean(TRANSLATE_PHRASE_ASSEMBLED, translatePhraseAssembled);
		translateMT = buffer.getBoolean(TRANSLATE_MT, translateMT);
		translateConcordance = buffer.getBoolean(TRANSLATE_CONCORDANCE, translateConcordance);
	}

	@Override
	protected void parameters_reset() {
		// Default values
		projectName = "My Project";
//		try {
//			customTemplateURI = new URI(EMPTY_URI); 
//		} catch (URISyntaxException e) {
//			new RuntimeException(e);
//		}
		customTemplateURI = EMPTY_URI;
		outputPath = "${rootDir}/scoping_report.html";
		
		translateGMXProtected = false;
		translateGMXExactMatched = false;
		translateGMXLeveragedMatched = true;
		translateGMXRepetitionMatched = true;
		translateGMXFuzzyMatch = true;
		translateGMXAlphanumericOnlyTextUnit = false;
		translateGMXNumericOnlyTextUnit = false;
		translateGMXMeasurementOnlyTextUnit = false;
		
		translateExactUniqueIdMatch = false;
		translateExactPreviousVersionMatch = false;
		translateExactLocalContextMatch = true;
		translateExactDocumentContextMatch = true;
		translateExactStructuralMatch = true;
		translateExactMatch = true;
		translateExactTextOnlyPreviousVersionMatch = true;
		translateExactTextOnlyUniqueIdMatch = true;
		translateExactTextOnly = true;
		translateExactRepaired = true;
		translateFuzzyPreviousVersionMatch = true;
		translateFuzzyUniqueIdMatch = true;
		translateFuzzyMatch = true;
		translateFuzzyRepaired = true;
		translatePhraseAssembled = true;
		translateMT = true;
		translateConcordance = true;
	}

	@Override
	protected void parameters_save(ParametersString buffer) {
		buffer.setString(PROJECT_NAME, projectName);
		buffer.setString(CUSTOM_TEMPLATE_URI, customTemplateURI == null ? EMPTY_URI : customTemplateURI.toString());
		buffer.setString(OUTPUT_PATH, outputPath);
		
		buffer.setBoolean(TRANSLATE_GMX_PROTECTED, translateGMXProtected);
		buffer.setBoolean(TRANSLATE_GMX_EXACT_MATCHED, translateGMXExactMatched);
		buffer.setBoolean(TRANSLATE_GMX_LEVERAGED_MATCHED, translateGMXLeveragedMatched);
		buffer.setBoolean(TRANSLATE_GMX_REPETITION_MATCHED, translateGMXRepetitionMatched);
		buffer.setBoolean(TRANSLATE_GMX_FUZZY_MATCHED, translateGMXFuzzyMatch);
		buffer.setBoolean(TRANSLATE_GMX_ALPHANUMERIC_ONLY_TEXT_UNIT, translateGMXAlphanumericOnlyTextUnit);
		buffer.setBoolean(TRANSLATE_GMX_NUMERIC_ONLY_TEXT_UNIT, translateGMXNumericOnlyTextUnit);
		buffer.setBoolean(TRANSLATE_GMX_MEASUREMENT_ONLY_TEXT_UNIT, translateGMXMeasurementOnlyTextUnit);
		
		buffer.setBoolean(TRANSLATE_EXACT_UNIQUE_ID, translateExactUniqueIdMatch);
		buffer.setBoolean(TRANSLATE_EXACT_PREVIOUS_VERSION, translateExactPreviousVersionMatch);
		buffer.setBoolean(TRANSLATE_EXACT_LOCAL_CONTEXT, translateExactLocalContextMatch);
		buffer.setBoolean(TRANSLATE_EXACT_DOCUMENT_CONTEXT, translateExactDocumentContextMatch);
		buffer.setBoolean(TRANSLATE_EXACT_STRUCTURAL, translateExactStructuralMatch);
		buffer.setBoolean(TRANSLATE_EXACT, translateExactMatch);
		buffer.setBoolean(TRANSLATE_EXACT_TEXT_ONLY_PREVIOUS_VERSION, translateExactTextOnlyPreviousVersionMatch);
		buffer.setBoolean(TRANSLATE_EXACT_TEXT_ONLY_UNIQUE_ID, translateExactTextOnlyUniqueIdMatch);
		buffer.setBoolean(TRANSLATE_EXACT_TEXT_ONLY, translateExactTextOnly);
		buffer.setBoolean(TRANSLATE_EXACT_REPAIRED, translateExactRepaired);
		buffer.setBoolean(TRANSLATE_FUZZY_PREVIOUS_VERSION, translateFuzzyPreviousVersionMatch);
		buffer.setBoolean(TRANSLATE_FUZZY_UNIQUE_ID, translateFuzzyUniqueIdMatch);
		buffer.setBoolean(TRANSLATE_FUZZY, translateFuzzyMatch);
		buffer.setBoolean(TRANSLATE_FUZZY_REPAIRED, translateFuzzyRepaired);
		buffer.setBoolean(TRANSLATE_PHRASE_ASSEMBLED, translatePhraseAssembled);
		buffer.setBoolean(TRANSLATE_MT, translateMT);
		buffer.setBoolean(TRANSLATE_CONCORDANCE, translateConcordance);
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getOutputPath () {
		return outputPath;
	}

	public void setOutputPath (String outputPath) {
		this.outputPath = outputPath;
	}

	@Override
	public ParametersDescription getParametersDescription() {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(PROJECT_NAME,
			"Name of the project", "Name of the project to be displayed in the report");
		desc.add(CUSTOM_TEMPLATE_URI,
				"Custom template URI:", "URI of the report template");
		desc.add(OUTPUT_PATH,
			"Output path:", "Full path of the report to generate");
		
		desc.add(TRANSLATE_GMX_PROTECTED,
				"Translate GMX Protected",
				"Translate the words in PROJECT_GMX_PROTECTED_WORD_COUNT and ITEM_GMX_PROTECTED_WORD_COUNT categories");
		desc.add(TRANSLATE_GMX_EXACT_MATCHED,
				"Translate GMX Exact Matched",
				"Translate the words in PROJECT_GMX_EXACT_MATCHED_WORD_COUNT and ITEM_GMX_EXACT_MATCHED_WORD_COUNT categories");
		desc.add(TRANSLATE_GMX_LEVERAGED_MATCHED,
				"Translate GMX Leveraged Matched",
				"Translate the words in PROJECT_GMX_LEVERAGED_MATCHED_WORD_COUNT and ITEM_GMX_LEVERAGED_MATCHED_WORD_COUNT categories");
		desc.add(TRANSLATE_GMX_REPETITION_MATCHED,
				"Translate GMX Repetition Matched",
				"Translate the words in PROJECT_GMX_REPETITION_MATCHED_WORD_COUNT and ITEM_GMX_REPETITION_MATCHED_WORD_COUNT categories");
		desc.add(TRANSLATE_GMX_FUZZY_MATCHED,
				"Translate GMX Fuzzy Match",
				"Translate the words in PROJECT_GMX_FUZZY_MATCHED_WORD_COUNT and ITEM_GMX_FUZZY_MATCHED_WORD_COUNT categories");
		desc.add(TRANSLATE_GMX_ALPHANUMERIC_ONLY_TEXT_UNIT,
				"Translate GMX Alphanumeric Only Text Units",
				"Translate the words in PROJECT_GMX_ALPHANUMERIC_ONLY_TEXT_UNIT_WORD_COUNT and ITEM_GMX_ALPHANUMERIC_ONLY_TEXT_UNIT_WORD_COUNT categories");
		desc.add(TRANSLATE_GMX_NUMERIC_ONLY_TEXT_UNIT,
				"Translate GMX Numeric Only Text Units",
				"Translate the words in PROJECT_GMX_NUMERIC_ONLY_TEXT_UNIT_WORD_COUNT and ITEM_GMX_NUMERIC_ONLY_TEXT_UNIT_WORD_COUNT categories");
		desc.add(TRANSLATE_GMX_MEASUREMENT_ONLY_TEXT_UNIT,
				"Translate GMX Measurement Only Text Units",
				"Translate the words in PROJECT_GMX_MEASUREMENT_ONLY_TEXT_UNIT_WORD_COUNT and ITEM_GMX_MEASUREMENT_ONLY_TEXT_UNIT_WORD_COUNT categories");
		
		desc.add(TRANSLATE_EXACT_UNIQUE_ID,
				"Translate Exact Unique Id Match",
				"Translate the words in PROJECT_EXACT_UNIQUE_ID and ITEM_EXACT_UNIQUE_ID categories");
		desc.add(TRANSLATE_EXACT_PREVIOUS_VERSION,
				"Translate Exact Previous Version Match",
				"Translate the words in PROJECT_EXACT_PREVIOUS_VERSION and ITEM_EXACT_PREVIOUS_VERSION categories");
		desc.add(TRANSLATE_EXACT_LOCAL_CONTEXT,
				"Translate Exact Local Context Match",
				"Translate the words in PROJECT_EXACT_LOCAL_CONTEXT and ITEM_EXACT_LOCAL_CONTEXT categories");
		desc.add(TRANSLATE_EXACT_DOCUMENT_CONTEXT,
				"Translate Exact Document Context Match",
				"Translate the words in PROJECT_EXACT_DOCUMENT_CONTEXT and ITEM_EXACT_DOCUMENT_CONTEXT categories");
		desc.add(TRANSLATE_EXACT_STRUCTURAL,
				"Translate Exact Structural Match",
				"Translate the words in PROJECT_EXACT_STRUCTURAL and ITEM_EXACT_STRUCTURAL categories");
		desc.add(TRANSLATE_EXACT,
				"Translate Exact Match",
				"Translate the words in PROJECT_EXACT and ITEM_EXACT categories");
		desc.add(TRANSLATE_EXACT_TEXT_ONLY_PREVIOUS_VERSION,
				"Translate Exact Text Only Previous Version Match",
				"Translate the words in PROJECT_EXACT_TEXT_ONLY_PREVIOUS_VERSION and ITEM_EXACT_TEXT_ONLY_PREVIOUS_VERSION categories");
		desc.add(TRANSLATE_EXACT_TEXT_ONLY_UNIQUE_ID,
				"Translate Exact Text Only Unique Id Match",
				"Translate the words in PROJECT_EXACT_TEXT_ONLY_UNIQUE_ID and ITEM_EXACT_TEXT_ONLY_UNIQUE_ID categories");
		desc.add(TRANSLATE_EXACT_TEXT_ONLY,
				"Translate Exact Text Only",
				"Translate the words in PROJECT_EXACT_TEXT_ONLY and ITEM_EXACT_TEXT_ONLY categories");
		desc.add(TRANSLATE_EXACT_REPAIRED,
				"Translate Exact Repaired",
				"Translate the words in PROJECT_EXACT_REPAIRED and ITEM_EXACT_REPAIRED categories");
		desc.add(TRANSLATE_FUZZY_PREVIOUS_VERSION,
				"Translate Fuzzy Previous Version Match",
				"Translate the words in PROJECT_FUZZY_PREVIOUS_VERSION and ITEM_FUZZY_PREVIOUS_VERSION categories");
		desc.add(TRANSLATE_FUZZY_UNIQUE_ID,
				"Translate Fuzzy Unique Id Match",
				"Translate the words in PROJECT_FUZZY_UNIQUE_ID and ITEM_FUZZY_UNIQUE_ID categories");
		desc.add(TRANSLATE_FUZZY,
				"Translate Fuzzy Match",
				"Translate the words in PROJECT_FUZZY and ITEM_FUZZY categories");
		desc.add(TRANSLATE_FUZZY_REPAIRED,
				"Translate Fuzzy Repaired",
				"Translate the words in PROJECT_FUZZY_REPAIRED and ITEM_FUZZY_REPAIRED categories");
		desc.add(TRANSLATE_PHRASE_ASSEMBLED,
				"Translate Phrase Assembled",
				"Translate the words in PROJECT_PHRASE_ASSEMBLED and ITEM_PHRASE_ASSEMBLED categories");
		desc.add(TRANSLATE_MT,
				"Translate after MT",
				"Translate the words in PROJECT_MT and ITEM_MT categories");
		desc.add(TRANSLATE_CONCORDANCE,
				"Translate Concordance",
				"Translate the words in PROJECT_CONCORDANCE and ITEM_CONCORDANCE categories");
		
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Scope Reporting", true, false);
		
		desc.addTextInputPart(paramsDesc.get(PROJECT_NAME));
		
		PathInputPart ctpip = desc.addPathInputPart(paramsDesc.get(CUSTOM_TEMPLATE_URI),
				"Custon Template", false);
		ctpip.setAllowEmpty(true);
		ctpip.setBrowseFilters("HTML Files (*.htm;*.html)\tAll Files (*.*)", "*.htm;*.html\t*.*");
		
		PathInputPart pip = desc.addPathInputPart(paramsDesc.get(OUTPUT_PATH),
			"Report to Generate", true);
		pip.setBrowseFilters("HTML Files (*.htm;*.html)\tAll Files (*.*)", "*.htm;*.html\t*.*");
		
		desc.addSeparatorPart();
		desc.addCheckboxPart(paramsDesc.get(TRANSLATE_GMX_PROTECTED));
		desc.addCheckboxPart(paramsDesc.get(TRANSLATE_GMX_EXACT_MATCHED));
		desc.addCheckboxPart(paramsDesc.get(TRANSLATE_GMX_LEVERAGED_MATCHED));
		desc.addCheckboxPart(paramsDesc.get(TRANSLATE_GMX_REPETITION_MATCHED));
		desc.addCheckboxPart(paramsDesc.get(TRANSLATE_GMX_FUZZY_MATCHED));
		desc.addCheckboxPart(paramsDesc.get(TRANSLATE_GMX_ALPHANUMERIC_ONLY_TEXT_UNIT));
		desc.addCheckboxPart(paramsDesc.get(TRANSLATE_GMX_NUMERIC_ONLY_TEXT_UNIT));
		desc.addCheckboxPart(paramsDesc.get(TRANSLATE_GMX_MEASUREMENT_ONLY_TEXT_UNIT));
		
		desc.addSeparatorPart();
		desc.addCheckboxPart(paramsDesc.get(TRANSLATE_EXACT_UNIQUE_ID));
		desc.addCheckboxPart(paramsDesc.get(TRANSLATE_EXACT_PREVIOUS_VERSION));
		desc.addCheckboxPart(paramsDesc.get(TRANSLATE_EXACT_LOCAL_CONTEXT));
		desc.addCheckboxPart(paramsDesc.get(TRANSLATE_EXACT_DOCUMENT_CONTEXT));
		desc.addCheckboxPart(paramsDesc.get(TRANSLATE_EXACT_STRUCTURAL));
		desc.addCheckboxPart(paramsDesc.get(TRANSLATE_EXACT));
		desc.addCheckboxPart(paramsDesc.get(TRANSLATE_EXACT_TEXT_ONLY_PREVIOUS_VERSION));
		desc.addCheckboxPart(paramsDesc.get(TRANSLATE_EXACT_TEXT_ONLY_UNIQUE_ID));
		desc.addCheckboxPart(paramsDesc.get(TRANSLATE_EXACT_TEXT_ONLY));
		desc.addCheckboxPart(paramsDesc.get(TRANSLATE_EXACT_REPAIRED));
		desc.addCheckboxPart(paramsDesc.get(TRANSLATE_FUZZY_PREVIOUS_VERSION));
		desc.addCheckboxPart(paramsDesc.get(TRANSLATE_FUZZY_UNIQUE_ID));
		desc.addCheckboxPart(paramsDesc.get(TRANSLATE_FUZZY));
		desc.addCheckboxPart(paramsDesc.get(TRANSLATE_FUZZY_REPAIRED));
		desc.addCheckboxPart(paramsDesc.get(TRANSLATE_PHRASE_ASSEMBLED));
		desc.addCheckboxPart(paramsDesc.get(TRANSLATE_MT));
		desc.addCheckboxPart(paramsDesc.get(TRANSLATE_CONCORDANCE));
		
		return desc;
	}
	
	public boolean useDefaultTemplate() {
		return EMPTY_URI.equalsIgnoreCase(customTemplateURI.toString()); 
	}
	
	public boolean istranslateGMXExactMatched() {
		return translateGMXExactMatched;
	}

	public void settranslateGMXExactMatched(boolean translateGMXExactMatched) {
		this.translateGMXExactMatched = translateGMXExactMatched;
	}

	public boolean istranslateGMXLeveragedMatched() {
		return translateGMXLeveragedMatched;
	}

	public void settranslateGMXLeveragedMatched(boolean translateGMXLeveragedMatched) {
		this.translateGMXLeveragedMatched = translateGMXLeveragedMatched;
	}

	public boolean istranslateGMXRepetitionMatched() {
		return translateGMXRepetitionMatched;
	}

	public void settranslateGMXRepetitionMatched(
			boolean translateGMXRepetitionMatched) {
		this.translateGMXRepetitionMatched = translateGMXRepetitionMatched;
	}

	public boolean istranslateGMXFuzzyMatch() {
		return translateGMXFuzzyMatch;
	}

	public void settranslateGMXFuzzyMatch(boolean translateGMXFuzzyMatch) {
		this.translateGMXFuzzyMatch = translateGMXFuzzyMatch;
	}

	public boolean istranslateGMXAlphanumericOnlyTextUnit() {
		return translateGMXAlphanumericOnlyTextUnit;
	}

	public void settranslateGMXAlphanumericOnlyTextUnit(
			boolean translateGMXAlphanumericOnlyTextUnit) {
		this.translateGMXAlphanumericOnlyTextUnit = translateGMXAlphanumericOnlyTextUnit;
	}

	public boolean istranslateGMXNumericOnlyTextUnit() {
		return translateGMXNumericOnlyTextUnit;
	}

	public void settranslateGMXNumericOnlyTextUnit(
			boolean translateGMXNumericOnlyTextUnit) {
		this.translateGMXNumericOnlyTextUnit = translateGMXNumericOnlyTextUnit;
	}

	public boolean istranslateGMXMeasurementOnlyTextUnit() {
		return translateGMXMeasurementOnlyTextUnit;
	}

	public void settranslateGMXMeasurementOnlyTextUnit(
			boolean translateGMXMeasurementOnlyTextUnit) {
		this.translateGMXMeasurementOnlyTextUnit = translateGMXMeasurementOnlyTextUnit;
	}

	public boolean istranslateExactUniqueIdMatch() {
		return translateExactUniqueIdMatch;
	}

	public void settranslateExactUniqueIdMatch(boolean translateExactUniqueIdMatch) {
		this.translateExactUniqueIdMatch = translateExactUniqueIdMatch;
	}

	public boolean istranslateExactPreviousVersionMatch() {
		return translateExactPreviousVersionMatch;
	}

	public void settranslateExactPreviousVersionMatch(
			boolean translateExactPreviousVersionMatch) {
		this.translateExactPreviousVersionMatch = translateExactPreviousVersionMatch;
	}

	public boolean istranslateExactLocalContextMatch() {
		return translateExactLocalContextMatch;
	}

	public void settranslateExactLocalContextMatch(
			boolean translateExactLocalContextMatch) {
		this.translateExactLocalContextMatch = translateExactLocalContextMatch;
	}

	public boolean istranslateExactDocumentContextMatch() {
		return translateExactDocumentContextMatch;
	}

	public void settranslateExactDocumentContextMatch(
			boolean translateExactDocumentContextMatch) {
		this.translateExactDocumentContextMatch = translateExactDocumentContextMatch;
	}

	public boolean istranslateExactStructuralMatch() {
		return translateExactStructuralMatch;
	}

	public void settranslateExactStructuralMatch(
			boolean translateExactStructuralMatch) {
		this.translateExactStructuralMatch = translateExactStructuralMatch;
	}

	public boolean istranslateExactMatch() {
		return translateExactMatch;
	}

	public void settranslateExactMatch(boolean translateExactMatch) {
		this.translateExactMatch = translateExactMatch;
	}

	public boolean istranslateExactTextOnlyPreviousVersionMatch() {
		return translateExactTextOnlyPreviousVersionMatch;
	}

	public void settranslateExactTextOnlyPreviousVersionMatch(
			boolean translateExactTextOnlyPreviousVersionMatch) {
		this.translateExactTextOnlyPreviousVersionMatch = translateExactTextOnlyPreviousVersionMatch;
	}

	public boolean istranslateExactTextOnlyUniqueIdMatch() {
		return translateExactTextOnlyUniqueIdMatch;
	}

	public void settranslateExactTextOnlyUniqueIdMatch(
			boolean translateExactTextOnlyUniqueIdMatch) {
		this.translateExactTextOnlyUniqueIdMatch = translateExactTextOnlyUniqueIdMatch;
	}

	public boolean istranslateExactTextOnly() {
		return translateExactTextOnly;
	}

	public void settranslateExactTextOnly(boolean translateExactTextOnly) {
		this.translateExactTextOnly = translateExactTextOnly;
	}

	public boolean istranslateExactRepaired() {
		return translateExactRepaired;
	}

	public void settranslateExactRepaired(boolean translateExactRepaired) {
		this.translateExactRepaired = translateExactRepaired;
	}

	public boolean istranslateFuzzyPreviousVersionMatch() {
		return translateFuzzyPreviousVersionMatch;
	}

	public void settranslateFuzzyPreviousVersionMatch(
			boolean translateFuzzyPreviousVersionMatch) {
		this.translateFuzzyPreviousVersionMatch = translateFuzzyPreviousVersionMatch;
	}

	public boolean istranslateFuzzyUniqueIdMatch() {
		return translateFuzzyUniqueIdMatch;
	}

	public void settranslateFuzzyUniqueIdMatch(boolean translateFuzzyUniqueIdMatch) {
		this.translateFuzzyUniqueIdMatch = translateFuzzyUniqueIdMatch;
	}

	public boolean istranslateFuzzyMatch() {
		return translateFuzzyMatch;
	}

	public void settranslateFuzzyMatch(boolean translateFuzzyMatch) {
		this.translateFuzzyMatch = translateFuzzyMatch;
	}

	public boolean istranslateFuzzyRepaired() {
		return translateFuzzyRepaired;
	}

	public void settranslateFuzzyRepaired(boolean translateFuzzyRepaired) {
		this.translateFuzzyRepaired = translateFuzzyRepaired;
	}

	public boolean istranslatePhraseAssembled() {
		return translatePhraseAssembled;
	}

	public void settranslatePhraseAssembled(boolean translatePhraseAssembled) {
		this.translatePhraseAssembled = translatePhraseAssembled;
	}

	public boolean istranslateMT() {
		return translateMT;
	}

	public void settranslateMT(boolean translateMT) {
		this.translateMT = translateMT;
	}

	public boolean istranslateConcordance() {
		return translateConcordance;
	}

	public void settranslateConcordance(boolean translateConcordance) {
		this.translateConcordance = translateConcordance;
	}

	public boolean istranslateGMXProtected() {
		return translateGMXProtected;
	}

	public void settranslateGMXProtected(boolean translateGMXProtected) {
		this.translateGMXProtected = translateGMXProtected;
	}
}

