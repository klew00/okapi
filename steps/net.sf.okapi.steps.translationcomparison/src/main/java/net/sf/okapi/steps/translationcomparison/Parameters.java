/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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
import net.sf.okapi.common.ParametersDescription;

public class Parameters extends BaseParameters {

	private boolean generateTMX;
	private String tmxPath;
	private boolean generateHTML;
	private boolean autoOpen;
	private boolean caseSensitive;
	private boolean whitespaceSensitive;
	private boolean punctuationSensitive;
	private String targetSuffix;

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

	public String getTargetSuffix () {
		return targetSuffix;
	}

	public void setTargetSuffix (String targetSuffix) {
		this.targetSuffix = targetSuffix;
	}

	public void reset() {
		generateTMX = false;
		tmxPath = "comparison.tmx"; // "${ProjDir}"+File.separator+"output.tmx";
		generateHTML = true;
		autoOpen = true;
		caseSensitive = true;
		whitespaceSensitive = true;
		punctuationSensitive = true;
		targetSuffix = "-mt";
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		generateTMX = buffer.getBoolean("generateTMX", generateTMX);
		tmxPath = buffer.getString("tmxPath", tmxPath);
		generateHTML = buffer.getBoolean("generateHTML", generateHTML);
		autoOpen = buffer.getBoolean("autoOpen", autoOpen);
		caseSensitive = buffer.getBoolean("caseSensitive", caseSensitive);
		whitespaceSensitive = buffer.getBoolean("whitespaceSensitive", whitespaceSensitive);
		punctuationSensitive = buffer.getBoolean("punctuationSensitive", punctuationSensitive);
		targetSuffix = buffer.getString("targetSuffix", targetSuffix);
	}

	public String toString() {
		buffer.reset();
		buffer.setParameter("generateTMX", generateTMX);
		buffer.setParameter("tmxPath", tmxPath);
		buffer.setParameter("generateHTML", generateHTML);
		buffer.setParameter("autoOpen", autoOpen);
		buffer.setParameter("caseSensitive", caseSensitive);
		buffer.setParameter("whitespaceSensitive", whitespaceSensitive);
		buffer.setParameter("punctuationSensitive", punctuationSensitive);
		buffer.setParameter("targetSuffix", targetSuffix);
		return buffer.toString();
	}

	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add("generateTMX",
			"Generate a TMX output document", "Generates an output document in TMX");
		desc.add("tmxPath",
			"TMX output path", "Full path of the output TMX file");
		desc.add("generateHTML",
			"Generate output tables in HTML", "Generates output tables in HTML");
		desc.add("autoOpen",
			"Opens the first HTML output after completion", null);
		desc.add("targetSuffix",
			"Suffix for the second target language code", null);
		desc.add("caseSensitive",
			"Take into account case differences", "Takes into account case differences");
		desc.add("whitespaceSensitive",
			"Take into account whitespace differences", "Takes into account whitespace differences");
		desc.add("punctuationSensitive",
			"Take into account punctuation differences", "Takes into account punctuation differences");
		return desc;
	}

}
