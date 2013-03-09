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

import net.sf.okapi.common.ParametersString;
import net.sf.okapi.common.filters.InlineCodeFinder;
import net.sf.okapi.lib.extra.AbstractParameters;
import net.sf.okapi.lib.extra.filters.WrapMode;

/**
 * Base Plain Text Filter parameters
 * 
 * @version 0.1, 09.06.2009
 */

public class Parameters extends AbstractParameters {
	
	public boolean unescapeSource;
	public boolean trimLeading;
	public boolean trimTrailing;
	public boolean preserveWS;
	public boolean useCodeFinder;
	public String codeFinderRules;	
	public WrapMode wrapMode;
	private InlineCodeFinder codeFinder;
	
//----------------------------------------------------------------------------------------------------------------------------
	
	@Override
	protected void parameters_init() {
		
		codeFinder = new InlineCodeFinder();
	}

	@Override
	protected void parameters_load(ParametersString buffer) {

		unescapeSource = buffer.getBoolean("unescapeSource", true);
		trimLeading = buffer.getBoolean("trimLeading", false);
		trimTrailing = buffer.getBoolean("trimTrailing", false);
		preserveWS = buffer.getBoolean("preserveWS", true);
		useCodeFinder = buffer.getBoolean("useCodeFinder", false);
		codeFinderRules = buffer.getString("codeFinderRules", codeFinder.toString());
//		wrapMode = WrapMode.class.getEnumConstants()[buffer.getInteger("wrapMode", WrapMode.NONE.ordinal())];
		wrapMode = WrapMode.values()[buffer.getInteger("wrapMode", WrapMode.NONE.ordinal())];		
	}

	@Override
	protected void parameters_reset() {
		
		unescapeSource = true;
		trimLeading = false;
		trimTrailing = false;
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

	@Override
	protected void parameters_save(ParametersString buffer) {
		
		buffer.setBoolean("unescapeSource", unescapeSource);
		buffer.setBoolean("trimLeading", trimLeading);
		buffer.setBoolean("trimTrailing", trimTrailing);
		buffer.setBoolean("preserveWS", preserveWS);
		buffer.setBoolean("useCodeFinder", useCodeFinder);
		buffer.setString("codeFinderRules", codeFinderRules);
		buffer.setInteger("wrapMode", wrapMode.ordinal());
	}

	

}
