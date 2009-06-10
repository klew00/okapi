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

package net.sf.okapi.filters.plaintext.regex;

import net.sf.okapi.common.BaseParameters;

/**
 * Parameters of the Regex Plain Text Filter
 * 
 * @version 0.1, 09.06.2009
 * @author Sergei Vasilyev  
 */

public class Parameters extends BaseParameters {
			
	/**
	 * Java regex rule used to extract lines of text.<p>Default: "^(.*?)$". 
	 */
	public String rule;
	
	/**
	 * Java regex capturing group denoting text to be extracted.<p>Default: 1.
	 */
	public int sourceGroup;
	
	/**
	 * Java regex options.<p>Default: Pattern.MULTILINE.
	 */
	public int regexOptions;
							
//----------------------------------------------------------------------------------------------------------------------------	
	
	public Parameters() {
		super();		
		
		reset();
		toString(); // fill the list
	}

	public void reset() {		
		// All parameters are set to defaults here
		rule = RegexPlainTextFilter.DEF_RULE;
		sourceGroup = RegexPlainTextFilter.DEF_GROUP;		
		regexOptions = RegexPlainTextFilter.DEF_OPTIONS; 
	}

	public void fromString(String data) {
		reset();
		
		buffer.fromString(data);
		
		// All parameters are retrieved here
		rule =  buffer.getString("rule", RegexPlainTextFilter.DEF_RULE);
		sourceGroup = buffer.getInteger("sourceGroup", RegexPlainTextFilter.DEF_GROUP);		
		regexOptions = buffer.getInteger("regexOptions", RegexPlainTextFilter.DEF_OPTIONS);
	}
	
	@Override
	public String toString () {
		buffer.reset();
		
		// All parameters are set here
		buffer.setString("rule", rule);
		buffer.setInteger("sourceGroup", sourceGroup);		
		buffer.setInteger("regexOptions", regexOptions);
		
		return buffer.toString();
	}
}
