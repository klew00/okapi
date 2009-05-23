/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.filters.plaintext;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.filters.InlineCodeFinder;
import net.sf.okapi.common.filters.LocalizationDirectives;

/**
 * Plain Text Filter parameters 
 */

public class Parameters extends BaseParameters {
		
	public boolean preserveWS;
	public boolean useCodeFinder;
	public String regularExpressionForEmbeddedMarkup;
	
	public LocalizationDirectives locDir;
	public InlineCodeFinder codeFinder;

//----------------------------------------------------------------------------------------------------------------------------	
	
	public Parameters() {
		super();
		locDir = new LocalizationDirectives();
		codeFinder = new InlineCodeFinder();
		
		reset();
		toString(); // fill the list
	}

	public void reset() {
		locDir.reset();
		codeFinder.reset();
		
		// Default in-line codes: special escaped-chars and printf-style variable
		codeFinder.addRule("%(([-0+#]?)[-0+#]?)((\\d\\$)?)(([\\d\\*]*)(\\.[\\d\\*]*)?)[dioxXucsfeEgGpn]");
		codeFinder.addRule("(\\\\r\\\\n)|\\\\a|\\\\b|\\\\f|\\\\n|\\\\r|\\\\t|\\\\v");
		
		// All parameters are set to defaults here
		preserveWS = true;
		useCodeFinder = false;
		regularExpressionForEmbeddedMarkup = "";
	}

	public void fromString(String data) {
		reset();
		
		buffer.fromString(data);
		
		// All parameters are retrieved here
		boolean tmpBool1 = buffer.getBoolean("useLD", locDir.useLD());
		boolean tmpBool2 = buffer.getBoolean("localizeOutside", locDir.localizeOutside());
		locDir.setOptions(tmpBool1, tmpBool2);
		
		preserveWS = buffer.getBoolean("preserveWS", true);
		useCodeFinder = buffer.getBoolean("useCodeFinder", false);
		regularExpressionForEmbeddedMarkup = buffer.getString("regularExpressionForEmbeddedMarkup", "");
	}
	
	@Override
	public String toString () {
		buffer.reset();
		
		// All parameters are set here
		buffer.setBoolean("useLD", locDir.useLD());
		buffer.setBoolean("localizeOutside", locDir.localizeOutside());
		
		buffer.setBoolean("preserveWS", preserveWS);
		buffer.setBoolean("useCodeFinder", useCodeFinder);
		buffer.setString("regularExpressionForEmbeddedMarkup", regularExpressionForEmbeddedMarkup);
		
		return buffer.toString();
	}
}
