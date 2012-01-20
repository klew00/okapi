/*===========================================================================
  Copyright (C) 2009-2011 by the Okapi Framework contributors
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

package net.sf.okapi.steps.formatconversion;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.uidescription.CheckboxPart;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.ListSelectionPart;
import net.sf.okapi.common.uidescription.PathInputPart;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	public static final String FORMAT_TMX = "tmx";
	public static final String FORMAT_PO = "po";
	public static final String FORMAT_TABLE = "table";
	public static final String FORMAT_PENSIEVE = "pensieve";
	public static final String FORMAT_CORPUS = "corpus";
	
	public static final int TRG_TARGETOREMPTY = 0; 
	public static final int TRG_FORCESOURCE = 1; 
	public static final int TRG_FORCEEMPTY = 2; 

	private static final String SINGLEOUTPUT = "singleOutput";
	private static final String AUTOEXTENSIONS = "autoExtensions";
	private static final String OUTPUTPATH = "outputPath";
	private static final String TARGETSTYLE = "targetStyle";
	private static final String OUTPUTFORMAT = "outputFormat";
	private static final String FORMATOPTIONS = "formatOptions";
	private static final String USEGENERICCODES = "useGenericCodes";
	private static final String SKIPENTRIESWITHOUTTEXT = "skipEntriesWithoutText";
	private static final String APPROVEDENTRIESONLY = "approvedEntriesOnly";
	private static final String OVERWRITESAMESOURCE = "overwriteSameSource";
	
	private boolean singleOutput;
	private boolean autoExtensions;
	private String outputPath;
	private int targetStyle;
	private String outputFormat;
	private boolean useGenericCodes;
	private String formatOptions;
	private boolean skipEntriesWithoutText;
	private boolean approvedEntriesOnly;
	private boolean overwriteSameSource;
	private IFilterWriter writer;
	
	public Parameters () {
		reset();
	}

	public int getTargetStyle () {
		return targetStyle;
	}

	public void setTargetStyle (int targetStyle) {
		this.targetStyle = targetStyle;
	}

	public boolean getSingleOutput () {
		return singleOutput;
	}

	public void setSingleOutput (boolean singleOutput) {
		this.singleOutput = singleOutput;
	}

	public boolean getAutoExtensions () {
		return autoExtensions;
	}

	public void setAutoExtensions (boolean autoExtensions) {
		this.autoExtensions = autoExtensions;
	}

	public String getOutputPath () {
		return outputPath;
	}

	public void setOutputPath (String outputPath) {
		this.outputPath = outputPath;
	}

	public String getOutputFormat () {
		return outputFormat;
	}

	public void setOutputFormat (String outputFormat) {
		this.outputFormat = outputFormat;
	}

	public boolean getUseGenericCodes () {
		return useGenericCodes;
	}

	public void setUseGenericCodes (boolean useGenericCodes) {
		this.useGenericCodes = useGenericCodes;
	}

	public String getFormatOptions () {
		return formatOptions;
	}

	public void setFormatOptions (String formatOptions) {
		this.formatOptions = formatOptions;
	}

	public boolean getSkipEntriesWithoutText () {
		return skipEntriesWithoutText;
	}

	public void setSkipEntriesWithoutText (boolean skipEntriesWithoutText) {
		this.skipEntriesWithoutText = skipEntriesWithoutText;
	}

	public boolean getApprovedEntriesOnly () {
		return approvedEntriesOnly;
	}

	public void setApprovedEntriesOnly (boolean approvedEntriesOnly) {
		this.approvedEntriesOnly = approvedEntriesOnly;
	}

	public boolean getOverwriteSameSource () {
		return overwriteSameSource;
	}
	
	public void setOverwriteSameSource (boolean overwriteSameSource) {
		this.overwriteSameSource = overwriteSameSource;
	}
	
	/**
	 * @return the writer
	 */
	public IFilterWriter getWriter() {
		return writer;
	}

	/**
	 * @param writer the writer to set
	 */
	public void setWriter(IFilterWriter writer) {
		this.writer = writer;
	}

	public void reset () {
		singleOutput = true;
		autoExtensions = false;
		targetStyle = TRG_TARGETOREMPTY;
		outputPath = "";
		outputFormat = FORMAT_TMX;
		formatOptions = null;
		useGenericCodes = false;
		skipEntriesWithoutText = true;
		approvedEntriesOnly = false;
		overwriteSameSource = false;
		writer = null;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		singleOutput = buffer.getBoolean(SINGLEOUTPUT, singleOutput);
		autoExtensions = buffer.getBoolean(AUTOEXTENSIONS, autoExtensions);
		targetStyle = buffer.getInteger(TARGETSTYLE, targetStyle);
		outputPath = buffer.getString(OUTPUTPATH, outputPath);
		outputFormat = buffer.getString(OUTPUTFORMAT, outputFormat);
		formatOptions = buffer.getGroup(FORMATOPTIONS, formatOptions);
		useGenericCodes = buffer.getBoolean(USEGENERICCODES, useGenericCodes);
		skipEntriesWithoutText = buffer.getBoolean(SKIPENTRIESWITHOUTTEXT, skipEntriesWithoutText);
		approvedEntriesOnly = buffer.getBoolean(APPROVEDENTRIESONLY, approvedEntriesOnly);
		overwriteSameSource = buffer.getBoolean(OVERWRITESAMESOURCE, overwriteSameSource);
	}

	public String toString() {
		buffer.reset();
		buffer.setBoolean(SINGLEOUTPUT, singleOutput);
		buffer.setBoolean(AUTOEXTENSIONS, autoExtensions);
		buffer.setInteger(TARGETSTYLE, targetStyle);
		buffer.setString(OUTPUTPATH, outputPath);
		buffer.setString(OUTPUTFORMAT, outputFormat);
		buffer.setGroup(FORMATOPTIONS, formatOptions);
		buffer.setBoolean(USEGENERICCODES, useGenericCodes);
		buffer.setBoolean(SKIPENTRIESWITHOUTTEXT, skipEntriesWithoutText);
		buffer.setBoolean(APPROVEDENTRIESONLY, approvedEntriesOnly);
		buffer.setBoolean(OVERWRITESAMESOURCE, overwriteSameSource);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(SINGLEOUTPUT, "Create a single output document", null);
		desc.add(AUTOEXTENSIONS, "Output paths are the input paths plus the new format extension", null);
		desc.add(OUTPUTPATH, "Output path", "Full path of the single output document to generate");
		desc.add(OUTPUTFORMAT, "Output format", "Format to generate in output");
		desc.add(USEGENERICCODES, "Output generic inline codes", null);
		desc.add(TARGETSTYLE, "Target content", "Type of content to put in the target");
		desc.add(SKIPENTRIESWITHOUTTEXT, "Do not output entries without text", null);
		desc.add(APPROVEDENTRIESONLY, "Output only approved entries", null);
		desc.add(OVERWRITESAMESOURCE, "Overwrite if source is the same (for Pensieve TM)", null);
		desc.add(FORMATOPTIONS, "Format options", null); // Not used for display 
		return desc;
	}

	public EditorDescription createEditorDescription(ParametersDescription paramDesc) {
		EditorDescription desc = new EditorDescription("Format Conversion", true, false);

		String[] choices = {FORMAT_PO, FORMAT_TMX, FORMAT_TABLE,
			FORMAT_PENSIEVE, FORMAT_CORPUS};
		String[] choicesLabels = {"PO File", "TMX Document", "Tab-Delimited Table",
			"Pensieve TM", "Parallel Corpus Files"};
		ListSelectionPart lsp = desc.addListSelectionPart(paramDesc.get(OUTPUTFORMAT), choices);
		lsp.setChoicesLabels(choicesLabels);
		
		desc.addCheckboxPart(paramDesc.get(APPROVEDENTRIESONLY));
		desc.addCheckboxPart(paramDesc.get(USEGENERICCODES));
		desc.addCheckboxPart(paramDesc.get(OVERWRITESAMESOURCE));

		CheckboxPart cbp1 = desc.addCheckboxPart(paramDesc.get(SINGLEOUTPUT));
		PathInputPart pip = desc.addPathInputPart(paramDesc.get(OUTPUTPATH), "Output File", true);
		pip.setMasterPart(cbp1, true);
		
		CheckboxPart cbp2 = desc.addCheckboxPart(paramDesc.get(AUTOEXTENSIONS));
		cbp2.setMasterPart(cbp1, false);

		return desc;
	}

}
