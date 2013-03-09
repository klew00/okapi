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

package net.sf.okapi.filters.json;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.filters.InlineCodeFinder;

public class Parameters extends BaseParameters {

	private boolean extractStandalone;
	private boolean extractAllPairs;
	private String exceptions;
	private boolean useCodeFinder;
	private InlineCodeFinder codeFinder;

	public Parameters () {
		codeFinder = new InlineCodeFinder();
		reset();
		toString(); // fill the list
	}
	
	public boolean getExtractStandalone () {
		return extractStandalone;
	}

	public void setExtractStandalone (boolean extractStandalone) {
		this.extractStandalone = extractStandalone;
	}

	public boolean getExtractAllPairs () {
		return extractAllPairs;
	}

	public void setExtractAllPairs (boolean extractAllPairs) {
		this.extractAllPairs = extractAllPairs;
	}

	public String getExceptions () {
		return exceptions;
	}

	public void setExceptions (String exceptions) {
		this.exceptions = exceptions;
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

	public void reset () {
		extractStandalone = false;
		extractAllPairs = true;
		exceptions = "";
		
		useCodeFinder = false;
		codeFinder.reset();
		codeFinder.setSample("&name; <tag></at><tag/> <tag attr='val'> </tag=\"val\">");
		codeFinder.setUseAllRulesWhenTesting(true);
		codeFinder.addRule("</?([A-Z0-9a-z]*)\\b[^>]*>");
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		extractStandalone = buffer.getBoolean("extractIsolatedStrings", extractStandalone);
		extractAllPairs = buffer.getBoolean("extractAllPairs", extractAllPairs);
		exceptions = buffer.getString("exceptions", exceptions);
		useCodeFinder = buffer.getBoolean("useCodeFinder", useCodeFinder);
		codeFinder.fromString(buffer.getGroup("codeFinderRules", ""));
	}
	
	@Override
	public String toString () {
		buffer.reset();
		buffer.setBoolean("extractIsolatedStrings", extractStandalone);
		buffer.setBoolean("extractAllPairs", extractAllPairs);
		buffer.setString("exceptions", exceptions);
		buffer.setBoolean("useCodeFinder", useCodeFinder);
		buffer.setGroup("codeFinderRules", codeFinder.toString());
		return buffer.toString();
	}

}
