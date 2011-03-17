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

package net.sf.okapi.filters.mif;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.filters.InlineCodeFinder;

public class Parameters extends BaseParameters {

	private static final String EXTRACTBODYPAGES = "extractBodyPages";
	private static final String EXTRACTREFERENCEPAGES = "extractReferencePages";
	private static final String EXTRACTMASTERPAGES = "extractMasterPages";
	private static final String EXTRACTHIDDENPAGES = "extractHiddenPages";
	private static final String USECODEFINDER = "useCodeFinder";
	private static final String CODEFINDERRULES = "codeFinderRules";
	
	private boolean extractBodyPages;
	private boolean extractReferencePages;
	private boolean extractMasterPages;
	private boolean extractHiddenPages;
	private boolean useCodeFinder;
	private InlineCodeFinder codeFinder;

	public Parameters () {
		codeFinder = new InlineCodeFinder();
		reset();
		toString(); // fill the list
	}
	
	public boolean getUseCodeFinder () {
		return useCodeFinder;
	}

	public void setUseCodeFinder (boolean useCodeFinder) {
		this.useCodeFinder = useCodeFinder;
	}

	public InlineCodeFinder getCodeFinder () {
		return codeFinder;
	}

	public String getCodeFinderData () {
		return codeFinder.toString();
	}

	public void setCodeFinderData (String data) {
		codeFinder.fromString(data);
	}

	public boolean getExtractReferencePages () {
		return extractReferencePages;
	}
	
	public void setExtractReferencePages (boolean extractReferencePages) {
		this.extractReferencePages = extractReferencePages;
	}

	public boolean getExtractMasterPages () {
		return extractMasterPages;
	}
	
	public void setExtractMasterPages (boolean extractMasterPages) {
		this.extractMasterPages = extractMasterPages;
	}
	
	public boolean getExtractHiddenPages () {
		return extractHiddenPages;
	}

	public void setExtractHiddenPages (boolean extractHiddenPages) {
		this.extractHiddenPages = extractHiddenPages;
	}

	public boolean getExtractBodyPages () {
		return extractBodyPages;
	}

	public void setExtractBodyPages (boolean extractBodyPages) {
		this.extractBodyPages = extractBodyPages;
	}

	public void reset () {
		extractBodyPages = true;
		extractMasterPages = true;
		extractReferencePages = true;
		extractHiddenPages = true;
		useCodeFinder = true;
		codeFinder.reset();
		codeFinder.setSample("text <$varName> text");
		codeFinder.setUseAllRulesWhenTesting(true);
		codeFinder.addRule("<\\$.*?>");
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		extractBodyPages = buffer.getBoolean(EXTRACTBODYPAGES, extractBodyPages);
		extractHiddenPages = buffer.getBoolean(EXTRACTHIDDENPAGES, extractHiddenPages);
		extractMasterPages = buffer.getBoolean(EXTRACTMASTERPAGES, extractMasterPages);
		extractReferencePages = buffer.getBoolean(EXTRACTREFERENCEPAGES, extractReferencePages);
		useCodeFinder = buffer.getBoolean(USECODEFINDER, useCodeFinder);
		codeFinder.fromString(buffer.getGroup(CODEFINDERRULES, ""));
	}
	
	@Override
	public String toString () {
		buffer.reset();
		buffer.setBoolean(EXTRACTBODYPAGES, extractBodyPages);
		buffer.setBoolean(EXTRACTHIDDENPAGES, extractHiddenPages);
		buffer.setBoolean(EXTRACTMASTERPAGES, extractMasterPages);
		buffer.setBoolean(EXTRACTREFERENCEPAGES, extractReferencePages);
		buffer.setBoolean(USECODEFINDER, useCodeFinder);
		buffer.setGroup(CODEFINDERRULES, codeFinder.toString());
		return buffer.toString();
	}

}
