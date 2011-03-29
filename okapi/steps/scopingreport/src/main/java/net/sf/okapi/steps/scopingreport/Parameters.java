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
import net.sf.okapi.common.Util;
import net.sf.okapi.common.uidescription.CheckListPart;
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

	private static final String COUNT_AS_TRANSLATABLE_GMX_PROTECTED = "countAsTranslatable_GMXProtected";
	private static final String COUNT_AS_TRANSLATABLE_GMX_EXACT_MATCHED = "countAsTranslatable_GMXExactMatched";
	private static final String COUNT_AS_TRANSLATABLE_GMX_LEVERAGED_MATCHED = "countAsTranslatable_GMXLeveragedMatched";
	private static final String COUNT_AS_TRANSLATABLE_GMX_REPETITION_MATCHED = "countAsTranslatable_GMXRepetitionMatched";
	private static final String COUNT_AS_TRANSLATABLE_GMX_FUZZY_MATCHED = "countAsTranslatable_GMXFuzzyMatch";
	private static final String COUNT_AS_TRANSLATABLE_GMX_ALPHANUMERIC_ONLY_TEXT_UNIT = "countAsTranslatable_GMXAlphanumericOnlyTextUnit";
	private static final String COUNT_AS_TRANSLATABLE_GMX_NUMERIC_ONLY_TEXT_UNIT = "countAsTranslatable_GMXNumericOnlyTextUnit";
	private static final String COUNT_AS_TRANSLATABLE_GMX_MEASUREMENT_ONLY_TEXT_UNIT = "countAsTranslatable_GMXMeasurementOnlyTextUnit";
	
	private static final String COUNT_AS_TRANSLATABLE_EXACT_UNIQUE_ID = "countAsTranslatable_ExactUniqueIdMatch";
	private static final String COUNT_AS_TRANSLATABLE_EXACT_PREVIOUS_VERSION = "countAsTranslatable_ExactPreviousVersionMatch";
	private static final String COUNT_AS_TRANSLATABLE_EXACT_LOCAL_CONTEXT = "countAsTranslatable_ExactLocalContextMatch";
	private static final String COUNT_AS_TRANSLATABLE_EXACT_DOCUMENT_CONTEXT = "countAsTranslatable_ExactDocumentContextMatch";
	private static final String COUNT_AS_TRANSLATABLE_EXACT_STRUCTURAL = "countAsTranslatable_ExactStructuralMatch";
	private static final String COUNT_AS_TRANSLATABLE_EXACT = "countAsTranslatable_ExactMatch";
	private static final String COUNT_AS_TRANSLATABLE_EXACT_TEXT_ONLY_PREVIOUS_VERSION = "countAsTranslatable_ExactTextOnlyPreviousVersionMatch";
	private static final String COUNT_AS_TRANSLATABLE_EXACT_TEXT_ONLY_UNIQUE_ID = "countAsTranslatable_ExactTextOnlyUniqueIdMatch";
	private static final String COUNT_AS_TRANSLATABLE_EXACT_TEXT_ONLY = "countAsTranslatable_ExactTextOnly";
	private static final String COUNT_AS_TRANSLATABLE_EXACT_REPAIRED = "countAsTranslatable_ExactRepaired";
	private static final String COUNT_AS_TRANSLATABLE_FUZZY_PREVIOUS_VERSION = "countAsTranslatable_FuzzyPreviousVersionMatch";
	private static final String COUNT_AS_TRANSLATABLE_FUZZY_UNIQUE_ID = "countAsTranslatable_FuzzyUniqueIdMatch";
	private static final String COUNT_AS_TRANSLATABLE_FUZZY = "countAsTranslatable_FuzzyMatch";
	private static final String COUNT_AS_TRANSLATABLE_FUZZY_REPAIRED = "countAsTranslatable_FuzzyRepaired";
	private static final String COUNT_AS_TRANSLATABLE_PHRASE_ASSEMBLED = "countAsTranslatable_PhraseAssembled";
	private static final String COUNT_AS_TRANSLATABLE_MT = "countAsTranslatable_MT";
	private static final String COUNT_AS_TRANSLATABLE_CONCORDANCE = "countAsTranslatable_Concordance";

	private String projectName;
	private String outputPath;
	private String customTemplateURI;
	
	private boolean countAsTranslatable_GMXProtected;
	private boolean countAsTranslatable_GMXExactMatched;
	private boolean countAsTranslatable_GMXLeveragedMatched;
	private boolean countAsTranslatable_GMXRepetitionMatched;
	private boolean countAsTranslatable_GMXFuzzyMatch;
	private boolean countAsTranslatable_GMXAlphanumericOnlyTextUnit;
	private boolean countAsTranslatable_GMXNumericOnlyTextUnit;
	private boolean countAsTranslatable_GMXMeasurementOnlyTextUnit;
	
	private boolean countAsTranslatable_ExactUniqueIdMatch;
	private boolean countAsTranslatable_ExactPreviousVersionMatch;
	private boolean countAsTranslatable_ExactLocalContextMatch;
	private boolean countAsTranslatable_ExactDocumentContextMatch;
	private boolean countAsTranslatable_ExactStructuralMatch;
	private boolean countAsTranslatable_ExactMatch;
	private boolean countAsTranslatable_ExactTextOnlyPreviousVersionMatch;
	private boolean countAsTranslatable_ExactTextOnlyUniqueIdMatch;
	private boolean countAsTranslatable_ExactTextOnly;
	private boolean countAsTranslatable_ExactRepaired;
	private boolean countAsTranslatable_FuzzyPreviousVersionMatch;
	private boolean countAsTranslatable_FuzzyUniqueIdMatch;
	private boolean countAsTranslatable_FuzzyMatch;
	private boolean countAsTranslatable_FuzzyRepaired;
	private boolean countAsTranslatable_PhraseAssembled;
	private boolean countAsTranslatable_MT;
	private boolean countAsTranslatable_Concordance;
	
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
		
		countAsTranslatable_GMXProtected = buffer.getBoolean(COUNT_AS_TRANSLATABLE_GMX_PROTECTED, countAsTranslatable_GMXProtected);
		countAsTranslatable_GMXExactMatched = buffer.getBoolean(COUNT_AS_TRANSLATABLE_GMX_EXACT_MATCHED, countAsTranslatable_GMXExactMatched);
		countAsTranslatable_GMXLeveragedMatched = buffer.getBoolean(COUNT_AS_TRANSLATABLE_GMX_LEVERAGED_MATCHED, countAsTranslatable_GMXLeveragedMatched);
		countAsTranslatable_GMXRepetitionMatched = buffer.getBoolean(COUNT_AS_TRANSLATABLE_GMX_REPETITION_MATCHED, countAsTranslatable_GMXRepetitionMatched);
		countAsTranslatable_GMXFuzzyMatch = buffer.getBoolean(COUNT_AS_TRANSLATABLE_GMX_FUZZY_MATCHED, countAsTranslatable_GMXFuzzyMatch);
		countAsTranslatable_GMXAlphanumericOnlyTextUnit = buffer.getBoolean(COUNT_AS_TRANSLATABLE_GMX_ALPHANUMERIC_ONLY_TEXT_UNIT, countAsTranslatable_GMXAlphanumericOnlyTextUnit);
		countAsTranslatable_GMXNumericOnlyTextUnit = buffer.getBoolean(COUNT_AS_TRANSLATABLE_GMX_NUMERIC_ONLY_TEXT_UNIT, countAsTranslatable_GMXNumericOnlyTextUnit);
		countAsTranslatable_GMXMeasurementOnlyTextUnit = buffer.getBoolean(COUNT_AS_TRANSLATABLE_GMX_MEASUREMENT_ONLY_TEXT_UNIT, countAsTranslatable_GMXMeasurementOnlyTextUnit);
		
		countAsTranslatable_ExactUniqueIdMatch = buffer.getBoolean(COUNT_AS_TRANSLATABLE_EXACT_UNIQUE_ID, countAsTranslatable_ExactUniqueIdMatch);
		countAsTranslatable_ExactPreviousVersionMatch = buffer.getBoolean(COUNT_AS_TRANSLATABLE_EXACT_PREVIOUS_VERSION, countAsTranslatable_ExactPreviousVersionMatch);
		countAsTranslatable_ExactLocalContextMatch = buffer.getBoolean(COUNT_AS_TRANSLATABLE_EXACT_LOCAL_CONTEXT, countAsTranslatable_ExactLocalContextMatch);
		countAsTranslatable_ExactDocumentContextMatch = buffer.getBoolean(COUNT_AS_TRANSLATABLE_EXACT_DOCUMENT_CONTEXT, countAsTranslatable_ExactDocumentContextMatch);
		countAsTranslatable_ExactStructuralMatch = buffer.getBoolean(COUNT_AS_TRANSLATABLE_EXACT_STRUCTURAL, countAsTranslatable_ExactStructuralMatch);
		countAsTranslatable_ExactMatch = buffer.getBoolean(COUNT_AS_TRANSLATABLE_EXACT, countAsTranslatable_ExactMatch);
		countAsTranslatable_ExactTextOnlyPreviousVersionMatch = buffer.getBoolean(COUNT_AS_TRANSLATABLE_EXACT_TEXT_ONLY_PREVIOUS_VERSION, countAsTranslatable_ExactTextOnlyPreviousVersionMatch);
		countAsTranslatable_ExactTextOnlyUniqueIdMatch = buffer.getBoolean(COUNT_AS_TRANSLATABLE_EXACT_TEXT_ONLY_UNIQUE_ID, countAsTranslatable_ExactTextOnlyUniqueIdMatch);
		countAsTranslatable_ExactTextOnly = buffer.getBoolean(COUNT_AS_TRANSLATABLE_EXACT_TEXT_ONLY, countAsTranslatable_ExactTextOnly);
		countAsTranslatable_ExactRepaired = buffer.getBoolean(COUNT_AS_TRANSLATABLE_EXACT_REPAIRED, countAsTranslatable_ExactRepaired);
		countAsTranslatable_FuzzyPreviousVersionMatch = buffer.getBoolean(COUNT_AS_TRANSLATABLE_FUZZY_PREVIOUS_VERSION, countAsTranslatable_FuzzyPreviousVersionMatch);
		countAsTranslatable_FuzzyUniqueIdMatch = buffer.getBoolean(COUNT_AS_TRANSLATABLE_FUZZY_UNIQUE_ID, countAsTranslatable_FuzzyUniqueIdMatch);
		countAsTranslatable_FuzzyMatch = buffer.getBoolean(COUNT_AS_TRANSLATABLE_FUZZY, countAsTranslatable_FuzzyMatch);
		countAsTranslatable_FuzzyRepaired = buffer.getBoolean(COUNT_AS_TRANSLATABLE_FUZZY_REPAIRED, countAsTranslatable_FuzzyRepaired);
		countAsTranslatable_PhraseAssembled = buffer.getBoolean(COUNT_AS_TRANSLATABLE_PHRASE_ASSEMBLED, countAsTranslatable_PhraseAssembled);
		countAsTranslatable_MT = buffer.getBoolean(COUNT_AS_TRANSLATABLE_MT, countAsTranslatable_MT);
		countAsTranslatable_Concordance = buffer.getBoolean(COUNT_AS_TRANSLATABLE_CONCORDANCE, countAsTranslatable_Concordance);
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
		outputPath = Util.ROOT_DIRECTORY_VAR+"/scoping_report.html";
		
		countAsTranslatable_GMXProtected = false;
		countAsTranslatable_GMXExactMatched = false;
		countAsTranslatable_GMXLeveragedMatched = true;
		countAsTranslatable_GMXRepetitionMatched = true;
		countAsTranslatable_GMXFuzzyMatch = true;
		countAsTranslatable_GMXAlphanumericOnlyTextUnit = false;
		countAsTranslatable_GMXNumericOnlyTextUnit = false;
		countAsTranslatable_GMXMeasurementOnlyTextUnit = false;
		
		countAsTranslatable_ExactUniqueIdMatch = false;
		countAsTranslatable_ExactPreviousVersionMatch = false;
		countAsTranslatable_ExactLocalContextMatch = true;
		countAsTranslatable_ExactDocumentContextMatch = true;
		countAsTranslatable_ExactStructuralMatch = true;
		countAsTranslatable_ExactMatch = true;
		countAsTranslatable_ExactTextOnlyPreviousVersionMatch = true;
		countAsTranslatable_ExactTextOnlyUniqueIdMatch = true;
		countAsTranslatable_ExactTextOnly = true;
		countAsTranslatable_ExactRepaired = true;
		countAsTranslatable_FuzzyPreviousVersionMatch = true;
		countAsTranslatable_FuzzyUniqueIdMatch = true;
		countAsTranslatable_FuzzyMatch = true;
		countAsTranslatable_FuzzyRepaired = true;
		countAsTranslatable_PhraseAssembled = true;
		countAsTranslatable_MT = true;
		countAsTranslatable_Concordance = true;
	}

	@Override
	protected void parameters_save(ParametersString buffer) {
		buffer.setString(PROJECT_NAME, projectName);
		buffer.setString(CUSTOM_TEMPLATE_URI, customTemplateURI == null ? EMPTY_URI : customTemplateURI.toString());
		buffer.setString(OUTPUT_PATH, outputPath);
		
		buffer.setBoolean(COUNT_AS_TRANSLATABLE_GMX_PROTECTED, countAsTranslatable_GMXProtected);
		buffer.setBoolean(COUNT_AS_TRANSLATABLE_GMX_EXACT_MATCHED, countAsTranslatable_GMXExactMatched);
		buffer.setBoolean(COUNT_AS_TRANSLATABLE_GMX_LEVERAGED_MATCHED, countAsTranslatable_GMXLeveragedMatched);
		buffer.setBoolean(COUNT_AS_TRANSLATABLE_GMX_REPETITION_MATCHED, countAsTranslatable_GMXRepetitionMatched);
		buffer.setBoolean(COUNT_AS_TRANSLATABLE_GMX_FUZZY_MATCHED, countAsTranslatable_GMXFuzzyMatch);
		buffer.setBoolean(COUNT_AS_TRANSLATABLE_GMX_ALPHANUMERIC_ONLY_TEXT_UNIT, countAsTranslatable_GMXAlphanumericOnlyTextUnit);
		buffer.setBoolean(COUNT_AS_TRANSLATABLE_GMX_NUMERIC_ONLY_TEXT_UNIT, countAsTranslatable_GMXNumericOnlyTextUnit);
		buffer.setBoolean(COUNT_AS_TRANSLATABLE_GMX_MEASUREMENT_ONLY_TEXT_UNIT, countAsTranslatable_GMXMeasurementOnlyTextUnit);
		
		buffer.setBoolean(COUNT_AS_TRANSLATABLE_EXACT_UNIQUE_ID, countAsTranslatable_ExactUniqueIdMatch);
		buffer.setBoolean(COUNT_AS_TRANSLATABLE_EXACT_PREVIOUS_VERSION, countAsTranslatable_ExactPreviousVersionMatch);
		buffer.setBoolean(COUNT_AS_TRANSLATABLE_EXACT_LOCAL_CONTEXT, countAsTranslatable_ExactLocalContextMatch);
		buffer.setBoolean(COUNT_AS_TRANSLATABLE_EXACT_DOCUMENT_CONTEXT, countAsTranslatable_ExactDocumentContextMatch);
		buffer.setBoolean(COUNT_AS_TRANSLATABLE_EXACT_STRUCTURAL, countAsTranslatable_ExactStructuralMatch);
		buffer.setBoolean(COUNT_AS_TRANSLATABLE_EXACT, countAsTranslatable_ExactMatch);
		buffer.setBoolean(COUNT_AS_TRANSLATABLE_EXACT_TEXT_ONLY_PREVIOUS_VERSION, countAsTranslatable_ExactTextOnlyPreviousVersionMatch);
		buffer.setBoolean(COUNT_AS_TRANSLATABLE_EXACT_TEXT_ONLY_UNIQUE_ID, countAsTranslatable_ExactTextOnlyUniqueIdMatch);
		buffer.setBoolean(COUNT_AS_TRANSLATABLE_EXACT_TEXT_ONLY, countAsTranslatable_ExactTextOnly);
		buffer.setBoolean(COUNT_AS_TRANSLATABLE_EXACT_REPAIRED, countAsTranslatable_ExactRepaired);
		buffer.setBoolean(COUNT_AS_TRANSLATABLE_FUZZY_PREVIOUS_VERSION, countAsTranslatable_FuzzyPreviousVersionMatch);
		buffer.setBoolean(COUNT_AS_TRANSLATABLE_FUZZY_UNIQUE_ID, countAsTranslatable_FuzzyUniqueIdMatch);
		buffer.setBoolean(COUNT_AS_TRANSLATABLE_FUZZY, countAsTranslatable_FuzzyMatch);
		buffer.setBoolean(COUNT_AS_TRANSLATABLE_FUZZY_REPAIRED, countAsTranslatable_FuzzyRepaired);
		buffer.setBoolean(COUNT_AS_TRANSLATABLE_PHRASE_ASSEMBLED, countAsTranslatable_PhraseAssembled);
		buffer.setBoolean(COUNT_AS_TRANSLATABLE_MT, countAsTranslatable_MT);
		buffer.setBoolean(COUNT_AS_TRANSLATABLE_CONCORDANCE, countAsTranslatable_Concordance);
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
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(PROJECT_NAME,
			"Name of the project", "Name of the project to be displayed in the report");
		desc.add(CUSTOM_TEMPLATE_URI,
				"Custom template URI:", "URI of the report template");
		desc.add(OUTPUT_PATH,
			"Output path:", "Full path of the report to generate");
		
		desc.add(COUNT_AS_TRANSLATABLE_GMX_PROTECTED,
				"GMX Protected Word Count",
				"Count as translatable the words in PROJECT_GMX_PROTECTED_WORD_COUNT and ITEM_GMX_PROTECTED_WORD_COUNT categories");
		desc.add(COUNT_AS_TRANSLATABLE_GMX_EXACT_MATCHED,
				"GMX Exact Matched Word Count",
				"Count as translatable the words in PROJECT_GMX_EXACT_MATCHED_WORD_COUNT and ITEM_GMX_EXACT_MATCHED_WORD_COUNT categories");
		desc.add(COUNT_AS_TRANSLATABLE_GMX_LEVERAGED_MATCHED,
				"GMX Leveraged Matched Word Count",
				"Count as translatable the words in PROJECT_GMX_LEVERAGED_MATCHED_WORD_COUNT and ITEM_GMX_LEVERAGED_MATCHED_WORD_COUNT categories");
		desc.add(COUNT_AS_TRANSLATABLE_GMX_REPETITION_MATCHED,
				"GMX Repetition Matched Word Count",
				"Count as translatable the words in PROJECT_GMX_REPETITION_MATCHED_WORD_COUNT and ITEM_GMX_REPETITION_MATCHED_WORD_COUNT categories");
		desc.add(COUNT_AS_TRANSLATABLE_GMX_FUZZY_MATCHED,
				"GMX Fuzzy Matched Word Count",
				"Count as translatable the words in PROJECT_GMX_FUZZY_MATCHED_WORD_COUNT and ITEM_GMX_FUZZY_MATCHED_WORD_COUNT categories");
		desc.add(COUNT_AS_TRANSLATABLE_GMX_ALPHANUMERIC_ONLY_TEXT_UNIT,
				"GMX Alphanumeric Only Text Unit Word Count",
				"Count as translatable the words in PROJECT_GMX_ALPHANUMERIC_ONLY_TEXT_UNIT_WORD_COUNT and ITEM_GMX_ALPHANUMERIC_ONLY_TEXT_UNIT_WORD_COUNT categories");
		desc.add(COUNT_AS_TRANSLATABLE_GMX_NUMERIC_ONLY_TEXT_UNIT,
				"GMX Numeric Only Text Unit Word Count",
				"Count as translatable the words in PROJECT_GMX_NUMERIC_ONLY_TEXT_UNIT_WORD_COUNT and ITEM_GMX_NUMERIC_ONLY_TEXT_UNIT_WORD_COUNT categories");
		desc.add(COUNT_AS_TRANSLATABLE_GMX_MEASUREMENT_ONLY_TEXT_UNIT,
				"GMX Measurement Only Text Unit Word Count",
				"Count as translatable the words in PROJECT_GMX_MEASUREMENT_ONLY_TEXT_UNIT_WORD_COUNT and ITEM_GMX_MEASUREMENT_ONLY_TEXT_UNIT_WORD_COUNT categories");
		
		desc.add(COUNT_AS_TRANSLATABLE_EXACT_UNIQUE_ID,
				"Exact Unique Id Match",
				"Count as translatable the words in PROJECT_EXACT_UNIQUE_ID and ITEM_EXACT_UNIQUE_ID categories");
		desc.add(COUNT_AS_TRANSLATABLE_EXACT_PREVIOUS_VERSION,
				"Exact Previous Version Match",
				"Count as translatable the words in PROJECT_EXACT_PREVIOUS_VERSION and ITEM_EXACT_PREVIOUS_VERSION categories");
		desc.add(COUNT_AS_TRANSLATABLE_EXACT_LOCAL_CONTEXT,
				"Exact Local Context Match",
				"Count as translatable the words in PROJECT_EXACT_LOCAL_CONTEXT and ITEM_EXACT_LOCAL_CONTEXT categories");
		desc.add(COUNT_AS_TRANSLATABLE_EXACT_DOCUMENT_CONTEXT,
				"Exact Document Context Match",
				"Count as translatable the words in PROJECT_EXACT_DOCUMENT_CONTEXT and ITEM_EXACT_DOCUMENT_CONTEXT categories");
		desc.add(COUNT_AS_TRANSLATABLE_EXACT_STRUCTURAL,
				"Exact Structural Match",
				"Count as translatable the words in PROJECT_EXACT_STRUCTURAL and ITEM_EXACT_STRUCTURAL categories");
		desc.add(COUNT_AS_TRANSLATABLE_EXACT,
				"Exact Match",
				"Count as translatable the words in PROJECT_EXACT and ITEM_EXACT categories");
		desc.add(COUNT_AS_TRANSLATABLE_EXACT_TEXT_ONLY_PREVIOUS_VERSION,
				"Exact Text Only Previous Version Match",
				"Count as translatable the words in PROJECT_EXACT_TEXT_ONLY_PREVIOUS_VERSION and ITEM_EXACT_TEXT_ONLY_PREVIOUS_VERSION categories");
		desc.add(COUNT_AS_TRANSLATABLE_EXACT_TEXT_ONLY_UNIQUE_ID,
				"Exact Text Only Unique Id Match",
				"Count as translatable the words in PROJECT_EXACT_TEXT_ONLY_UNIQUE_ID and ITEM_EXACT_TEXT_ONLY_UNIQUE_ID categories");
		desc.add(COUNT_AS_TRANSLATABLE_EXACT_TEXT_ONLY,
				"Exact Text Only",
				"Count as translatable the words in PROJECT_EXACT_TEXT_ONLY and ITEM_EXACT_TEXT_ONLY categories");
		desc.add(COUNT_AS_TRANSLATABLE_EXACT_REPAIRED,
				"Exact Repaired",
				"Count as translatable the words in PROJECT_EXACT_REPAIRED and ITEM_EXACT_REPAIRED categories");
		desc.add(COUNT_AS_TRANSLATABLE_FUZZY_PREVIOUS_VERSION,
				"Fuzzy Previous Version Match",
				"Count as translatable the words in PROJECT_FUZZY_PREVIOUS_VERSION and ITEM_FUZZY_PREVIOUS_VERSION categories");
		desc.add(COUNT_AS_TRANSLATABLE_FUZZY_UNIQUE_ID,
				"Fuzzy Unique Id Match",
				"Count as translatable the words in PROJECT_FUZZY_UNIQUE_ID and ITEM_FUZZY_UNIQUE_ID categories");
		desc.add(COUNT_AS_TRANSLATABLE_FUZZY,
				"Fuzzy Match",
				"Count as translatable the words in PROJECT_FUZZY and ITEM_FUZZY categories");
		desc.add(COUNT_AS_TRANSLATABLE_FUZZY_REPAIRED,
				"Fuzzy Repaired",
				"Count as translatable the words in PROJECT_FUZZY_REPAIRED and ITEM_FUZZY_REPAIRED categories");
		desc.add(COUNT_AS_TRANSLATABLE_PHRASE_ASSEMBLED,
				"Phrase Assembled",
				"Count as translatable the words in PROJECT_PHRASE_ASSEMBLED and ITEM_PHRASE_ASSEMBLED categories");
		desc.add(COUNT_AS_TRANSLATABLE_MT,
				"MT",
				"Count as translatable the words in PROJECT_MT and ITEM_MT categories");
		desc.add(COUNT_AS_TRANSLATABLE_CONCORDANCE,
				"Concordance",
				"Count as translatable the words in PROJECT_CONCORDANCE and ITEM_CONCORDANCE categories");
		
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Scope Reporting", true, false);
		
		desc.addTextInputPart(paramsDesc.get(PROJECT_NAME));
		
		PathInputPart ctpip = desc.addPathInputPart(paramsDesc.get(CUSTOM_TEMPLATE_URI),
				"Custon Template", false);
		ctpip.setAllowEmpty(true);
		ctpip.setBrowseFilters("HTML Files (*.htm;*.html)\tAll Files (*.*)", "*.htm;*.html\t*.*");
		
		PathInputPart pip = desc.addPathInputPart(paramsDesc.get(OUTPUT_PATH),
			"Report to Generate", true);
		pip.setBrowseFilters("HTML Files (*.htm;*.html)\tAll Files (*.*)", "*.htm;*.html\t*.*");

//		desc.addSeparatorPart();
		CheckListPart clp = desc.addCheckListPart("GMX categories to count as translatable", 90);
		clp.addEntry(paramsDesc.get(COUNT_AS_TRANSLATABLE_GMX_PROTECTED));
		clp.addEntry(paramsDesc.get(COUNT_AS_TRANSLATABLE_GMX_EXACT_MATCHED));
		clp.addEntry(paramsDesc.get(COUNT_AS_TRANSLATABLE_GMX_LEVERAGED_MATCHED));
		clp.addEntry(paramsDesc.get(COUNT_AS_TRANSLATABLE_GMX_REPETITION_MATCHED));
		clp.addEntry(paramsDesc.get(COUNT_AS_TRANSLATABLE_GMX_FUZZY_MATCHED));
		clp.addEntry(paramsDesc.get(COUNT_AS_TRANSLATABLE_GMX_ALPHANUMERIC_ONLY_TEXT_UNIT));
		clp.addEntry(paramsDesc.get(COUNT_AS_TRANSLATABLE_GMX_NUMERIC_ONLY_TEXT_UNIT));
		clp.addEntry(paramsDesc.get(COUNT_AS_TRANSLATABLE_GMX_MEASUREMENT_ONLY_TEXT_UNIT));
		
//		desc.addCheckboxPart(paramsDesc.get(COUNT_AS_TRANSLATABLE_GMX_PROTECTED));
//		desc.addCheckboxPart(paramsDesc.get(COUNT_AS_TRANSLATABLE_GMX_EXACT_MATCHED));
//		desc.addCheckboxPart(paramsDesc.get(COUNT_AS_TRANSLATABLE_GMX_LEVERAGED_MATCHED));
//		desc.addCheckboxPart(paramsDesc.get(COUNT_AS_TRANSLATABLE_GMX_REPETITION_MATCHED));
//		desc.addCheckboxPart(paramsDesc.get(COUNT_AS_TRANSLATABLE_GMX_FUZZY_MATCHED));
//		desc.addCheckboxPart(paramsDesc.get(COUNT_AS_TRANSLATABLE_GMX_ALPHANUMERIC_ONLY_TEXT_UNIT));
//		desc.addCheckboxPart(paramsDesc.get(COUNT_AS_TRANSLATABLE_GMX_NUMERIC_ONLY_TEXT_UNIT));
//		desc.addCheckboxPart(paramsDesc.get(COUNT_AS_TRANSLATABLE_GMX_MEASUREMENT_ONLY_TEXT_UNIT));
		
//		desc.addSeparatorPart();
		clp = desc.addCheckListPart("Okapi categories to count as translatable", 120);
		clp.addEntry(paramsDesc.get(COUNT_AS_TRANSLATABLE_EXACT_UNIQUE_ID));
		clp.addEntry(paramsDesc.get(COUNT_AS_TRANSLATABLE_EXACT_PREVIOUS_VERSION));
		clp.addEntry(paramsDesc.get(COUNT_AS_TRANSLATABLE_EXACT_LOCAL_CONTEXT));
		clp.addEntry(paramsDesc.get(COUNT_AS_TRANSLATABLE_EXACT_DOCUMENT_CONTEXT));
		clp.addEntry(paramsDesc.get(COUNT_AS_TRANSLATABLE_EXACT_STRUCTURAL));
		clp.addEntry(paramsDesc.get(COUNT_AS_TRANSLATABLE_EXACT));
		clp.addEntry(paramsDesc.get(COUNT_AS_TRANSLATABLE_EXACT_TEXT_ONLY_PREVIOUS_VERSION));
		clp.addEntry(paramsDesc.get(COUNT_AS_TRANSLATABLE_EXACT_TEXT_ONLY_UNIQUE_ID));
		clp.addEntry(paramsDesc.get(COUNT_AS_TRANSLATABLE_EXACT_TEXT_ONLY));
		clp.addEntry(paramsDesc.get(COUNT_AS_TRANSLATABLE_EXACT_REPAIRED));
		clp.addEntry(paramsDesc.get(COUNT_AS_TRANSLATABLE_FUZZY_PREVIOUS_VERSION));
		clp.addEntry(paramsDesc.get(COUNT_AS_TRANSLATABLE_FUZZY_UNIQUE_ID));
		clp.addEntry(paramsDesc.get(COUNT_AS_TRANSLATABLE_FUZZY));
		clp.addEntry(paramsDesc.get(COUNT_AS_TRANSLATABLE_FUZZY_REPAIRED));
		clp.addEntry(paramsDesc.get(COUNT_AS_TRANSLATABLE_PHRASE_ASSEMBLED));
		clp.addEntry(paramsDesc.get(COUNT_AS_TRANSLATABLE_MT));
		clp.addEntry(paramsDesc.get(COUNT_AS_TRANSLATABLE_CONCORDANCE));
		
//		desc.addCheckboxPart(paramsDesc.get(COUNT_AS_TRANSLATABLE_EXACT_UNIQUE_ID));
//		desc.addCheckboxPart(paramsDesc.get(COUNT_AS_TRANSLATABLE_EXACT_PREVIOUS_VERSION));
//		desc.addCheckboxPart(paramsDesc.get(COUNT_AS_TRANSLATABLE_EXACT_LOCAL_CONTEXT));
//		desc.addCheckboxPart(paramsDesc.get(COUNT_AS_TRANSLATABLE_EXACT_DOCUMENT_CONTEXT));
//		desc.addCheckboxPart(paramsDesc.get(COUNT_AS_TRANSLATABLE_EXACT_STRUCTURAL));
//		desc.addCheckboxPart(paramsDesc.get(COUNT_AS_TRANSLATABLE_EXACT));
//		desc.addCheckboxPart(paramsDesc.get(COUNT_AS_TRANSLATABLE_EXACT_TEXT_ONLY_PREVIOUS_VERSION));
//		desc.addCheckboxPart(paramsDesc.get(COUNT_AS_TRANSLATABLE_EXACT_TEXT_ONLY_UNIQUE_ID));
//		desc.addCheckboxPart(paramsDesc.get(COUNT_AS_TRANSLATABLE_EXACT_TEXT_ONLY));
//		desc.addCheckboxPart(paramsDesc.get(COUNT_AS_TRANSLATABLE_EXACT_REPAIRED));
//		desc.addCheckboxPart(paramsDesc.get(COUNT_AS_TRANSLATABLE_FUZZY_PREVIOUS_VERSION));
//		desc.addCheckboxPart(paramsDesc.get(COUNT_AS_TRANSLATABLE_FUZZY_UNIQUE_ID));
//		desc.addCheckboxPart(paramsDesc.get(COUNT_AS_TRANSLATABLE_FUZZY));
//		desc.addCheckboxPart(paramsDesc.get(COUNT_AS_TRANSLATABLE_FUZZY_REPAIRED));
//		desc.addCheckboxPart(paramsDesc.get(COUNT_AS_TRANSLATABLE_PHRASE_ASSEMBLED));
//		desc.addCheckboxPart(paramsDesc.get(COUNT_AS_TRANSLATABLE_MT));
//		desc.addCheckboxPart(paramsDesc.get(COUNT_AS_TRANSLATABLE_CONCORDANCE));

		return desc;
	}
	
	public boolean useDefaultTemplate() {
		return EMPTY_URI.equalsIgnoreCase(customTemplateURI.toString()); 
	}
	
	public boolean isCountAsTranslatable_GMXExactMatched() {
		return countAsTranslatable_GMXExactMatched;
	}

	public void setCountAsTranslatable_GMXExactMatched(boolean countAsTranslatable_GMXExactMatched) {
		this.countAsTranslatable_GMXExactMatched = countAsTranslatable_GMXExactMatched;
	}

	public boolean isCountAsTranslatable_GMXLeveragedMatched() {
		return countAsTranslatable_GMXLeveragedMatched;
	}

	public void setCountAsTranslatable_GMXLeveragedMatched(boolean countAsTranslatable_GMXLeveragedMatched) {
		this.countAsTranslatable_GMXLeveragedMatched = countAsTranslatable_GMXLeveragedMatched;
	}

	public boolean isCountAsTranslatable_GMXRepetitionMatched() {
		return countAsTranslatable_GMXRepetitionMatched;
	}

	public void setCountAsTranslatable_GMXRepetitionMatched(
			boolean countAsTranslatable_GMXRepetitionMatched) {
		this.countAsTranslatable_GMXRepetitionMatched = countAsTranslatable_GMXRepetitionMatched;
	}

	public boolean isCountAsTranslatable_GMXFuzzyMatch() {
		return countAsTranslatable_GMXFuzzyMatch;
	}

	public void setCountAsTranslatable_GMXFuzzyMatch(boolean countAsTranslatable_GMXFuzzyMatch) {
		this.countAsTranslatable_GMXFuzzyMatch = countAsTranslatable_GMXFuzzyMatch;
	}

	public boolean isCountAsTranslatable_GMXAlphanumericOnlyTextUnit() {
		return countAsTranslatable_GMXAlphanumericOnlyTextUnit;
	}

	public void setCountAsTranslatable_GMXAlphanumericOnlyTextUnit(
			boolean countAsTranslatable_GMXAlphanumericOnlyTextUnit) {
		this.countAsTranslatable_GMXAlphanumericOnlyTextUnit = countAsTranslatable_GMXAlphanumericOnlyTextUnit;
	}

	public boolean isCountAsTranslatable_GMXNumericOnlyTextUnit() {
		return countAsTranslatable_GMXNumericOnlyTextUnit;
	}

	public void setCountAsTranslatable_GMXNumericOnlyTextUnit(
			boolean countAsTranslatable_GMXNumericOnlyTextUnit) {
		this.countAsTranslatable_GMXNumericOnlyTextUnit = countAsTranslatable_GMXNumericOnlyTextUnit;
	}

	public boolean isCountAsTranslatable_GMXMeasurementOnlyTextUnit() {
		return countAsTranslatable_GMXMeasurementOnlyTextUnit;
	}

	public void setCountAsTranslatable_GMXMeasurementOnlyTextUnit(
			boolean countAsTranslatable_GMXMeasurementOnlyTextUnit) {
		this.countAsTranslatable_GMXMeasurementOnlyTextUnit = countAsTranslatable_GMXMeasurementOnlyTextUnit;
	}

	public boolean isCountAsTranslatable_ExactUniqueIdMatch() {
		return countAsTranslatable_ExactUniqueIdMatch;
	}

	public void setCountAsTranslatable_ExactUniqueIdMatch(boolean countAsTranslatable_ExactUniqueIdMatch) {
		this.countAsTranslatable_ExactUniqueIdMatch = countAsTranslatable_ExactUniqueIdMatch;
	}

	public boolean isCountAsTranslatable_ExactPreviousVersionMatch() {
		return countAsTranslatable_ExactPreviousVersionMatch;
	}

	public void setCountAsTranslatable_ExactPreviousVersionMatch(
			boolean countAsTranslatable_ExactPreviousVersionMatch) {
		this.countAsTranslatable_ExactPreviousVersionMatch = countAsTranslatable_ExactPreviousVersionMatch;
	}

	public boolean isCountAsTranslatable_ExactLocalContextMatch() {
		return countAsTranslatable_ExactLocalContextMatch;
	}

	public void setCountAsTranslatable_ExactLocalContextMatch(
			boolean countAsTranslatable_ExactLocalContextMatch) {
		this.countAsTranslatable_ExactLocalContextMatch = countAsTranslatable_ExactLocalContextMatch;
	}

	public boolean isCountAsTranslatable_ExactDocumentContextMatch() {
		return countAsTranslatable_ExactDocumentContextMatch;
	}

	public void setCountAsTranslatable_ExactDocumentContextMatch(
			boolean countAsTranslatable_ExactDocumentContextMatch) {
		this.countAsTranslatable_ExactDocumentContextMatch = countAsTranslatable_ExactDocumentContextMatch;
	}

	public boolean isCountAsTranslatable_ExactStructuralMatch() {
		return countAsTranslatable_ExactStructuralMatch;
	}

	public void setCountAsTranslatable_ExactStructuralMatch(
			boolean countAsTranslatable_ExactStructuralMatch) {
		this.countAsTranslatable_ExactStructuralMatch = countAsTranslatable_ExactStructuralMatch;
	}

	public boolean isCountAsTranslatable_ExactMatch() {
		return countAsTranslatable_ExactMatch;
	}

	public void setCountAsTranslatable_ExactMatch(boolean countAsTranslatable_ExactMatch) {
		this.countAsTranslatable_ExactMatch = countAsTranslatable_ExactMatch;
	}

	public boolean isCountAsTranslatable_ExactTextOnlyPreviousVersionMatch() {
		return countAsTranslatable_ExactTextOnlyPreviousVersionMatch;
	}

	public void setCountAsTranslatable_ExactTextOnlyPreviousVersionMatch(
			boolean countAsTranslatable_ExactTextOnlyPreviousVersionMatch) {
		this.countAsTranslatable_ExactTextOnlyPreviousVersionMatch = countAsTranslatable_ExactTextOnlyPreviousVersionMatch;
	}

	public boolean isCountAsTranslatable_ExactTextOnlyUniqueIdMatch() {
		return countAsTranslatable_ExactTextOnlyUniqueIdMatch;
	}

	public void setCountAsTranslatable_ExactTextOnlyUniqueIdMatch(
			boolean countAsTranslatable_ExactTextOnlyUniqueIdMatch) {
		this.countAsTranslatable_ExactTextOnlyUniqueIdMatch = countAsTranslatable_ExactTextOnlyUniqueIdMatch;
	}

	public boolean isCountAsTranslatable_ExactTextOnly() {
		return countAsTranslatable_ExactTextOnly;
	}

	public void setCountAsTranslatable_ExactTextOnly(boolean countAsTranslatable_ExactTextOnly) {
		this.countAsTranslatable_ExactTextOnly = countAsTranslatable_ExactTextOnly;
	}

	public boolean isCountAsTranslatable_ExactRepaired() {
		return countAsTranslatable_ExactRepaired;
	}

	public void setCountAsTranslatable_ExactRepaired(boolean countAsTranslatable_ExactRepaired) {
		this.countAsTranslatable_ExactRepaired = countAsTranslatable_ExactRepaired;
	}

	public boolean isCountAsTranslatable_FuzzyPreviousVersionMatch() {
		return countAsTranslatable_FuzzyPreviousVersionMatch;
	}

	public void setCountAsTranslatable_FuzzyPreviousVersionMatch(
			boolean countAsTranslatable_FuzzyPreviousVersionMatch) {
		this.countAsTranslatable_FuzzyPreviousVersionMatch = countAsTranslatable_FuzzyPreviousVersionMatch;
	}

	public boolean isCountAsTranslatable_FuzzyUniqueIdMatch() {
		return countAsTranslatable_FuzzyUniqueIdMatch;
	}

	public void setCountAsTranslatable_FuzzyUniqueIdMatch(boolean countAsTranslatable_FuzzyUniqueIdMatch) {
		this.countAsTranslatable_FuzzyUniqueIdMatch = countAsTranslatable_FuzzyUniqueIdMatch;
	}

	public boolean isCountAsTranslatable_FuzzyMatch() {
		return countAsTranslatable_FuzzyMatch;
	}

	public void setCountAsTranslatable_FuzzyMatch(boolean countAsTranslatable_FuzzyMatch) {
		this.countAsTranslatable_FuzzyMatch = countAsTranslatable_FuzzyMatch;
	}

	public boolean isCountAsTranslatable_FuzzyRepaired() {
		return countAsTranslatable_FuzzyRepaired;
	}

	public void setCountAsTranslatable_FuzzyRepaired(boolean countAsTranslatable_FuzzyRepaired) {
		this.countAsTranslatable_FuzzyRepaired = countAsTranslatable_FuzzyRepaired;
	}

	public boolean isCountAsTranslatable_PhraseAssembled() {
		return countAsTranslatable_PhraseAssembled;
	}

	public void setCountAsTranslatable_PhraseAssembled(boolean countAsTranslatable_PhraseAssembled) {
		this.countAsTranslatable_PhraseAssembled = countAsTranslatable_PhraseAssembled;
	}

	public boolean isCountAsTranslatable_MT() {
		return countAsTranslatable_MT;
	}

	public void setCountAsTranslatable_MT(boolean countAsTranslatable_MT) {
		this.countAsTranslatable_MT = countAsTranslatable_MT;
	}

	public boolean isCountAsTranslatable_Concordance() {
		return countAsTranslatable_Concordance;
	}

	public void setCountAsTranslatable_Concordance(boolean countAsTranslatable_Concordance) {
		this.countAsTranslatable_Concordance = countAsTranslatable_Concordance;
	}

	public boolean isCountAsTranslatable_GMXProtected() {
		return countAsTranslatable_GMXProtected;
	}

	public void setCountAsTranslatable_GMXProtected(boolean countAsTranslatable_GMXProtected) {
		this.countAsTranslatable_GMXProtected = countAsTranslatable_GMXProtected;
	}
}

