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

package net.sf.okapi.steps.translationcomparison;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.CheckboxPart;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.PathInputPart;
import net.sf.okapi.common.uidescription.TextInputPart;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	public static final String GENERATETMX = "generateTMX";
	public static final String TMXPATH = "tmxPath"; 
	public static final String GENERATEHTML = "generateHTML";
	public static final String GENERICCODES = "genericCodes"; 
	public static final String AUTOOPEN = "autoOpen"; 
	public static final String TARGET2SUFFIX = "target2Suffix";
	public static final String TARGET3SUFFIX = "target3Suffix";
	public static final String DOCUMENT1LABEL = "document1Label";
	public static final String DOCUMENT2LABEL = "document2Label";
	public static final String DOCUMENT3LABEL = "document3Label";
	public static final String CASESENSITIVE = "caseSensitive";
	public static final String WHITESPACESENSITIVE = "whitespaceSensitive";
	public static final String PUNCTUATIONSENSITIVE = "punctuationSensitive";
	
	private boolean generateTMX;
	private String tmxPath;
	private boolean generateHTML;
	private boolean genericCodes;
	private boolean autoOpen;
	private boolean caseSensitive;
	private boolean whitespaceSensitive;
	private boolean punctuationSensitive;
	private String target2Suffix;
	private String target3Suffix;
	private String document1Label;
	private String document2Label;
	private String document3Label;

	public Parameters () {
		reset();
	}
	
	public boolean isGenerateTMX () {
		return generateTMX;
	}

	public void setGenerateTMX (boolean generateTMX) {
		this.generateTMX = generateTMX;
	}

	public String getTmxPath () {
		return tmxPath;
	}

	public void setTmxPath (String tmxPath) {
		this.tmxPath = tmxPath;
	}

	public boolean isGenerateHTML () {
		return generateHTML;
	}

	public void setGenerateHTML (boolean generateHTML) {
		this.generateHTML = generateHTML;
	}

	public boolean getGenericCodes () {
		return genericCodes;
	}
	
	public void setGenericCodes (boolean genericCodes) {
		this.genericCodes = genericCodes;
	}
	
	public boolean isAutoOpen () {
		return autoOpen;
	}

	public void setAutoOpen (boolean autoOpen) {
		this.autoOpen = autoOpen;
	}

	public boolean isCaseSensitive () {
		return caseSensitive;
	}

	public void setCaseSensitive (boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	public boolean isWhitespaceSensitive () {
		return whitespaceSensitive;
	}

	public void setWhitespaceSensitive (boolean whitespaceSensitive) {
		this.whitespaceSensitive = whitespaceSensitive;
	}

	public boolean isPunctuationSensitive () {
		return punctuationSensitive;
	}

	public void setPunctuationSensitive (boolean punctuationSensitive) {
		this.punctuationSensitive = punctuationSensitive;
	}

	public String getTarget2Suffix () {
		return target2Suffix;
	}

	public void setTarget2Suffix (String target2Suffix) {
		this.target2Suffix = target2Suffix;
	}

	public String getTarget3Suffix () {
		return target3Suffix;
	}

	public void setTarget3Suffix (String target3Suffix) {
		this.target3Suffix = target3Suffix;
	}

	public String getDocument1Label () {
		return document1Label;
	}

	public void setDocument1Label (String document1Label) {
		this.document1Label = document1Label;
	}

	public String getDocument2Label () {
		return document2Label;
	}

	public void setDocument2Label (String document2Label) {
		this.document2Label = document2Label;
	}

	public String getDocument3Label () {
		return document3Label;
	}

	public void setDocument3Label (String document3Label) {
		this.document3Label = document3Label;
	}

	@Override
	public void reset() {
		generateTMX = false;
		tmxPath = "comparison.tmx";
		generateHTML = true;
		autoOpen = true;
		genericCodes = true;
		caseSensitive = true;
		whitespaceSensitive = true;
		punctuationSensitive = true;
		target2Suffix = "-t2";
		target3Suffix = "-t3";
		document1Label = "Trans1";
		document2Label = "Trans2";
		document3Label = "Trans3";
	}

	@Override
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		generateTMX = buffer.getBoolean(GENERATETMX, generateTMX);
		tmxPath = buffer.getString(TMXPATH, tmxPath);
		generateHTML = buffer.getBoolean(GENERATEHTML, generateHTML);
		autoOpen = buffer.getBoolean(AUTOOPEN, autoOpen);
		genericCodes = buffer.getBoolean(GENERICCODES, genericCodes);
		caseSensitive = buffer.getBoolean(CASESENSITIVE, caseSensitive);
		whitespaceSensitive = buffer.getBoolean(WHITESPACESENSITIVE, whitespaceSensitive);
		punctuationSensitive = buffer.getBoolean(PUNCTUATIONSENSITIVE, punctuationSensitive);
		target2Suffix = buffer.getString(TARGET2SUFFIX, target2Suffix);
		target3Suffix = buffer.getString(TARGET3SUFFIX, target3Suffix);
		document1Label = buffer.getString(DOCUMENT1LABEL, document1Label);
		document2Label = buffer.getString(DOCUMENT2LABEL, document2Label);
		document3Label = buffer.getString(DOCUMENT3LABEL, document3Label);
	}

	@Override
	public String toString() {
		buffer.reset();
		buffer.setBoolean(GENERATETMX, generateTMX);
		buffer.setParameter(TMXPATH, tmxPath);
		buffer.setBoolean(GENERATEHTML, generateHTML);
		buffer.setBoolean(AUTOOPEN, autoOpen);
		buffer.setBoolean(GENERICCODES, genericCodes);
		buffer.setBoolean(CASESENSITIVE, caseSensitive);
		buffer.setBoolean(WHITESPACESENSITIVE, whitespaceSensitive);
		buffer.setBoolean(PUNCTUATIONSENSITIVE, punctuationSensitive);
		buffer.setParameter(TARGET2SUFFIX, target2Suffix);
		buffer.setParameter(TARGET3SUFFIX, target3Suffix);
		buffer.setParameter(DOCUMENT1LABEL, document1Label);
		buffer.setParameter(DOCUMENT2LABEL, document2Label);
		buffer.setParameter(DOCUMENT3LABEL, document3Label);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(GENERATETMX,
			"Generate a TMX output document", "Generates an output document in TMX");
		desc.add(TMXPATH,
			"TMX output path", "Full path of the output TMX file");
		desc.add(GENERATEHTML,
			"Generate output tables in HTML", "Generates output tables in HTML");
		desc.add(GENERICCODES,
			"Use generic representation (e.g. <1>...</1>) for the inline codes", null);
		desc.add(AUTOOPEN,
			"Opens the first HTML output after completion", null);
		
		desc.add(TARGET2SUFFIX,
			"Suffix for target language code of document 2", null);
		desc.add(TARGET3SUFFIX,
			"Suffix for target language code of document 3", null);
			
		desc.add(DOCUMENT1LABEL,
			"Label for the document 1", null);
		desc.add(DOCUMENT2LABEL,
			"Label for the document 2", null);
		desc.add(DOCUMENT3LABEL,
			"Label for the document 3", null);
			
		desc.add(CASESENSITIVE,
			"Take into account case differences", "Takes into account case differences");
		desc.add(WHITESPACESENSITIVE,
			"Take into account whitespace differences", "Takes into account whitespace differences");
		desc.add(PUNCTUATIONSENSITIVE,
			"Take into account punctuation differences", "Takes into account punctuation differences");
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Translation Comparison", true, false);
		
		//TODO: "HTML Output" group
		CheckboxPart cbpHTML = desc.addCheckboxPart(paramsDesc.get(GENERATEHTML));
		desc.addCheckboxPart(paramsDesc.get(GENERICCODES)).setMasterPart(cbpHTML, true);
		desc.addCheckboxPart(paramsDesc.get(AUTOOPEN)).setMasterPart(cbpHTML, true);
		
		//TODO: "TMX Output" group
		CheckboxPart cbpTMX = desc.addCheckboxPart(paramsDesc.get(GENERATETMX));
		PathInputPart pip = desc.addPathInputPart(paramsDesc.get(TMXPATH), "TMX Document", true);
		pip.setBrowseFilters("TMX Documents (*.tmx)\tAll Files (*.*)", "*.tmx\t*.*");
		pip.setMasterPart(cbpTMX, true);
		pip.setWithLabel(false);
		TextInputPart tip = desc.addTextInputPart(paramsDesc.get(TARGET2SUFFIX));
		tip.setMasterPart(cbpTMX, true);
		tip = desc.addTextInputPart(paramsDesc.get(TARGET3SUFFIX));
		tip.setMasterPart(cbpTMX, true);
		
		// HTML group
		tip = desc.addTextInputPart(paramsDesc.get(DOCUMENT1LABEL));
		tip.setMasterPart(cbpHTML, true);
		tip.setVertical(false);
		tip = desc.addTextInputPart(paramsDesc.get(DOCUMENT2LABEL));
		tip.setMasterPart(cbpHTML, true);
		tip.setVertical(false);
		tip = desc.addTextInputPart(paramsDesc.get(DOCUMENT3LABEL));
		tip.setMasterPart(cbpHTML, true);
		tip.setVertical(false);
		
		//TODO: "Comparison Options" group
		desc.addCheckboxPart(paramsDesc.get(CASESENSITIVE));
		desc.addCheckboxPart(paramsDesc.get(WHITESPACESENSITIVE));
		desc.addCheckboxPart(paramsDesc.get(PUNCTUATIONSENSITIVE));
		
		return desc;
	}

}
