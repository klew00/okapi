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

package net.sf.okapi.filters.vignette;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ListUtil;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.CheckboxPart;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.TextInputPart;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {
	
	static final String PARTSCONFIGURATIONS = "partsConfigurations";
	static final String PARTSNAMES = "partsNames";
	static final String SOURCEID = "sourceId";
	static final String LOCALEID = "localeId";
	static final String QUOTEMODEDEFINED = "quoteModeDefined";
	static final String QUOTEMODE = "quoteMode";
	static final String MONOLINGUAL = "monolingual";
	static final String USECDATA = "useCDATA";
	
	private String partsConfigurations;
	private String partsNames;
	private String sourceId;
	private String localeId;
	private boolean monolingual;
	private boolean useCDATA;

	public String getPartsNames () {
		return partsNames;
	}

	public String[] getPartsNamesAsList () {
		return ListUtil.stringAsArray(partsNames);
	}

	public void setPartsNames (String partsNames) {
		this.partsNames = partsNames;
	}

	public String getPartsConfigurations () {
		return partsConfigurations;
	}

	public String[] getPartsConfigurationsAsList () {
		return ListUtil.stringAsArray(partsConfigurations);
	}

	public void setPartsConfigurations (String partsConfigurations) {
		this.partsConfigurations = partsConfigurations;
	}
	
	public String getSourceId () {
		return sourceId;
	}

	public void setSourceId (String sourceId) {
		this.sourceId = sourceId;
	}

	public String getLocaleId () {
		return localeId;
	}

	public void setLocaleId (String localeId) {
		this.localeId = localeId;
	}

	public boolean getMonolingual () {
		return monolingual;
	}

	public void setMonolingual (boolean monolingual) {
		this.monolingual = monolingual;
	}
	
	public boolean getUseCDATA() {
		return useCDATA;
	}

	public void setUseCDATA(boolean useCDATA) {
		this.useCDATA = useCDATA;
	}

	public boolean checkData () {
		String[] tmp1 = ListUtil.stringAsArray(partsNames);
		String[] tmp2 = ListUtil.stringAsArray(partsConfigurations);
		return (( tmp1.length > 0 ) && ( tmp1.length == tmp2.length ));
	}

	public Parameters () {
		reset();
		toString(); // fill the list
	}
	
	@Override
	public void reset () {
		partsNames = "SMCCONTENT-TITLE, SMCCONTENT-ABSTRACT, SMCCONTENT-BODY, SMCCONTENT-ALT, "
			+ "SMCCHANNELDESCRIPTOR-TITLE, SMCCHANNELDESCRIPTOR-ABSTRACT, SMCCHANNELDESCRIPTOR-ALT, "
			+ "SMCLINKCOLLECTIONS-LINKCOLLECTION-TITLE, SMCLINKCOLLECTIONS-LINKCOLLECTION-DESCRIPTION, "
			+ "SMCLINKS-TITLE, SMCLINKS-ABSTRACT, SMCLINKS-BODY, SMCLINKS-ALT";
		partsConfigurations = "default, okf_html, okf_html, default, "
			+ "default, okf_html, default, "
			+ "default, okf_html, "
			+ "default, okf_html, okf_html, default";
		sourceId = "SOURCE_ID";
		localeId = "LOCALE_ID";
		monolingual = false;
		useCDATA = true;
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setString(PARTSNAMES, partsNames);
		buffer.setString(PARTSCONFIGURATIONS, partsConfigurations);
		buffer.setString(SOURCEID, sourceId);
		buffer.setString(LOCALEID, localeId);
		buffer.setBoolean(MONOLINGUAL, monolingual);
		buffer.setBoolean(USECDATA, useCDATA);
		// Plus two *write-only* parameters: always set to true and 0
		// This is used by the encoder to know how it needs to escape the quotes
		// It must not be 0 if one of the data part to extract is an attribute
		// here we can use 0 because all extracted text comes from elements.
		buffer.setBoolean(QUOTEMODEDEFINED, true);
		buffer.setInteger(QUOTEMODE, 0);
		return buffer.toString();
	}
	
	@Override
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		partsNames = buffer.getString(PARTSNAMES, partsNames);
		partsConfigurations = buffer.getString(PARTSCONFIGURATIONS, partsConfigurations);
		sourceId = buffer.getString(SOURCEID, sourceId);
		localeId = buffer.getString(LOCALEID, localeId);
		monolingual = buffer.getBoolean(MONOLINGUAL, monolingual);
		useCDATA = buffer.getBoolean(USECDATA, useCDATA);
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(PARTSNAMES, "Names of the <attribute> elements to extract",
			"Comma-separated list of the names of the <attribute> elements to extract.");
		desc.add(PARTSCONFIGURATIONS, "Corresponding filter configurations (or 'default')",
			"Comma-separated list of the filter configurations to use, use 'default' for none");
		desc.add(MONOLINGUAL, "Monolingual mode", null);
		desc.add(USECDATA, "Use CDATA", 
				"Create CDATA sections in the output file");
		desc.add(SOURCEID, "Name for source ID element",
			"Name of the <attribute> element containing the source ID");
		desc.add(LOCALEID, "Name for locale ID element",
			"Name of the <attribute> element containing the locale ID");
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramDesc) {
		EditorDescription desc = new EditorDescription("Vignette Filter Parameters", true, false);
		
		TextInputPart tip = desc.addTextInputPart(paramDesc.get(Parameters.PARTSNAMES));
		tip.setHeight(60);
		
		tip = desc.addTextInputPart(paramDesc.get(Parameters.PARTSCONFIGURATIONS));
		tip.setHeight(60);

		desc.addCheckboxPart(paramDesc.get(USECDATA));
		
		CheckboxPart mono = desc.addCheckboxPart(paramDesc.get(MONOLINGUAL));
		
		tip = desc.addTextInputPart(paramDesc.get(Parameters.SOURCEID));
		tip.setVertical(false);
		tip.setMasterPart(mono, false);
		
		tip = desc.addTextInputPart(paramDesc.get(Parameters.LOCALEID));
		tip.setVertical(false);
		tip.setMasterPart(mono, false);
		
		return desc;
	}

	
	
}
