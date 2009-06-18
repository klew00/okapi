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

package net.sf.okapi.filters.plaintext.base;

import net.sf.okapi.common.filters.InlineCodeFinder;
import net.sf.okapi.filters.plaintext.common.AbstractParameters;
import net.sf.okapi.filters.plaintext.common.WrapMode;

/**
 * Base Plain Text Filter parameters
 * 
 * @version 0.1, 09.06.2009
 * @author Sergei Vasilyev
 */

public class Parameters extends AbstractParameters {
	
	public boolean unescapeSource = true;
	public boolean trimLeft = true;
	public boolean trimRight = false;
	public boolean preserveWS = true;
	public boolean useCodeFinder = false;
	public String codeFinderRules = "";	
	public WrapMode wrapMode = WrapMode.NONE;
	private InlineCodeFinder codeFinder;
	
//----------------------------------------------------------------------------------------------------------------------------	
	
	public Parameters() {
		super();
		
		codeFinder = new InlineCodeFinder();
		
		reset();
		toString(); // fill the list
	}

	public void reset() {
		
		// All parameters are set to defaults here
		unescapeSource = true;
		trimLeft = true;
		trimRight = false;
		preserveWS = true;
		useCodeFinder = false;
		
		// Default in-line codes: special escaped-chars and printf-style variable
		codeFinder.reset();
		
		// Default in-line codes: special escaped-chars and printf-style variable
		codeFinder.addRule("%(([-0+#]?)[-0+#]?)((\\d\\$)?)(([\\d\\*]*)(\\.[\\d\\*]*)?)[dioxXucsfeEgGpn]");
		codeFinder.addRule("(\\\\r\\\\n)|\\\\a|\\\\b|\\\\f|\\\\n|\\\\r|\\\\t|\\\\v");
		
		codeFinderRules = codeFinder.toString();
			
		wrapMode = WrapMode.NONE;
	}

	public void fromString(String data) {
		reset();
		
		buffer.fromString(data);
		
		// All parameters are retrieved here
		unescapeSource = buffer.getBoolean("unescapeSource", true);
		trimLeft = buffer.getBoolean("trimLeft", true);
		trimRight = buffer.getBoolean("trimRight", false);
		preserveWS = buffer.getBoolean("preserveWS", true);
		useCodeFinder = buffer.getBoolean("useCodeFinder", false);
		codeFinderRules = buffer.getString("codeFinderRules", codeFinder.toString());
//		wrapMode = WrapMode.class.getEnumConstants()[buffer.getInteger("wrapMode", WrapMode.NONE.ordinal())];
		wrapMode = WrapMode.values()[buffer.getInteger("wrapMode", WrapMode.NONE.ordinal())];
	}
	
	@Override
	public String toString () {
		buffer.reset();
		
		// All parameters are set here
		buffer.setBoolean("unescapeSource", unescapeSource);
		buffer.setBoolean("trimLeft", trimLeft);
		buffer.setBoolean("trimRight", trimRight);
		buffer.setBoolean("preserveWS", preserveWS);
		buffer.setBoolean("useCodeFinder", useCodeFinder);
		buffer.setString("codeFinderRules", codeFinderRules);
		buffer.setInteger("wrapMode", wrapMode.ordinal());
		
		return buffer.toString();
	}
}
